package ru.croc.ugd.ssr.service;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.OrganizationInformation;
import ru.croc.ugd.ssr.config.CacheConfig;
import ru.croc.ugd.ssr.dto.CcoDto;
import ru.croc.ugd.ssr.dto.CcoFlat;
import ru.croc.ugd.ssr.dto.CcoInfo;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.service.wsdl.flat.model.FlatDetails;
import ru.croc.ugd.ssr.model.api.chess.CcoFlatInfoResponse;
import ru.croc.ugd.ssr.model.api.chess.CcoSolrResponse;
import ru.croc.ugd.ssr.model.api.chess.EntrancesResponse;
import ru.croc.ugd.ssr.model.api.chess.FlatsResponse;
import ru.croc.ugd.ssr.model.api.chess.FullOksHousesResponse;
import ru.croc.ugd.ssr.model.api.chess.StatsHouseResponse;
import ru.croc.ugd.ssr.model.api.oks.OksParticipant;
import ru.croc.ugd.ssr.model.api.oks.Organization;
import ru.reinform.cdp.exception.RIException;
import ru.reinform.cdp.search.model.search_input.QuerySearch;
import ru.reinform.cdp.search.model.search_output.SearchResponse;
import ru.reinform.cdp.search.service.SearchRemoteService;
import ru.reinform.cdp.utils.rest.utils.SendRestUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис по работы с ОКС.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CapitalConstructionObjectService {

    private static final String GENERAL_CONTRACTORS_ROLE_NAME = "Генеральный подрядчик";
    private static final String DEVELOPER_ROLE_NAME = "Застройщик";

    private final SearchRemoteService searchRemoteService;

    private final SendRestUtils restUtils;

    private final IntegrationProperties integrationProperties;

    private final DictionaryService dictionaryService;

    @Value("${common.playground.url}")
    private String playgroundUrl;

    /**
     * Получить список застройщиков из солр по уному.
     *
     * @param unom уном
     * @return список застройщиков
     */
    public List<OrganizationInformation> getDeveloperOrganisationsByUnom(final String unom) {
        return getOrganisationsByUnom(unom, DEVELOPER_ROLE_NAME);
    }

    /**
     * Получить список генподрядчиков из солр по уному.
     *
     * @param unom уном
     * @return список генподрядчиков
     */
    public List<OrganizationInformation> getContractOrganisationsByUnom(final String unom) {
        return getOrganisationsByUnom(unom, GENERAL_CONTRACTORS_ROLE_NAME);
    }

    private List<OrganizationInformation> getOrganisationsByUnom(final String unom, final String roleName) {
        if (StringUtils.isEmpty(unom)) {
            return Collections.emptyList();
        }
        return ofNullable(getCcoIdByUnom(unom))
            .map(Collections::singletonList)
            .map(this::psGetOrganizationsFromCco)
            .orElse(Collections.emptyList())
            .stream()
            .filter(organization -> roleName.equalsIgnoreCase(organization.getRoleName()))
            .map(this::mapToOrganizationInformation)
            .collect(Collectors.toList());
    }

    private OrganizationInformation mapToOrganizationInformation(final Organization organization) {
        final OrganizationInformation organizationInformation = new OrganizationInformation();
        organizationInformation.setExternalId(organization.getInn());
        organizationInformation.setOrgFullName(organization.getFullName());
        return organizationInformation;
    }

    /**
     * Получить данные квартиры из ОКС по кадастровому номеру квартиры.
     *
     * @param cadNumber кадастровый номер квартиры.
     * @return данные по квартире
     */
    public CcoFlat getCcoFlatByFlatCadNumber(final String cadNumber) {
        try {
            String query = "{\"capitalConstructionObject\": {\"capitalConstructionObjectData\":"
                + " {\"flats\": [{\"cadastralNumber\": \"" + cadNumber + "\"}]}}}";
            final String result = restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getPsCcoFindCco(),
                HttpMethod.POST,
                query,
                String.class
            );
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray.length() == 1) {
                final JSONObject capitalConstructionObjectData = jsonArray
                    .getJSONObject(0)
                    .getJSONObject("capitalConstructionObject")
                    .getJSONObject("capitalConstructionObjectData");
                final BigInteger unom = new BigInteger(capitalConstructionObjectData.getString("unom"));
                final String address = ofNullable(capitalConstructionObjectData.getString("assignedAddressString"))
                    .orElse(capitalConstructionObjectData.getString("buildingAddressString"));
                final JSONArray flats = capitalConstructionObjectData.getJSONArray("flats");
                String flatNumber = null;
                for (int i = 0; i < flats.length(); i++) {
                    final JSONObject flat = flats.getJSONObject(i);
                    if (cadNumber.equals(flat.getString("cadastralNumber"))) {
                        flatNumber = flat.getString("number");
                        break;
                    }
                }
                return CcoFlat.builder()
                    .unom(unom)
                    .address(address)
                    .flatNumber(flatNumber)
                    .build();
            } else {
                log.info("getCcoUnomByFlatCadNumber: result size = {}", jsonArray.length());
            }
        } catch (Exception exception) {
            log.error("Unable to getCcoUnomByFlatCadNumber by cadNumber {}", cadNumber, exception);
        }
        return null;
    }

    /**
     * Получить данные из ОКС по documentId.
     *
     * @param documentId Идентификатор документа.
     * @return данные ОКС
     */
    public Optional<CcoInfo> getCcoInfoByDocumentId(final String documentId) {
        try {
            final String result = restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getPsCcoFetchCco() + documentId,
                HttpMethod.GET,
                null,
                String.class
            );
            JSONObject capitalConstructionObject = new JSONObject(result);
            final JSONObject capitalConstructionObjectData = capitalConstructionObject
                .getJSONObject("capitalConstructionObject")
                .getJSONObject("capitalConstructionObjectData");

            int flatCount = capitalConstructionObjectData.getJSONArray("flats").length();
            if (flatCount <= 0) {
                try {
                    final JSONArray indexValues = capitalConstructionObjectData.getJSONArray("indexValues");
                    for (int i = 0; i < indexValues.length(); i++) {
                        final JSONObject flat = indexValues.getJSONObject(i);
                        if ("FLAT_NUMBER".equals(flat.getString("techEconomIndex"))) {
                            flatCount = Integer.parseInt(flat.getString("value"));
                            break;
                        }
                    }
                } catch (Exception e) {
                    log.warn("Unable to find flat number from cco (documentId = {}): {}", documentId, e.getMessage());
                    flatCount = 0;
                }
            }
            return Optional.of(CcoInfo.builder()
                .unom(capitalConstructionObjectData.getString("unom"))
                .flatCount(flatCount)
                .build());
        } catch (Exception exception) {
            log.error("Unable to getCcoByDocumentId {}", documentId, exception);
        }
        return Optional.empty();
    }

    /**
     * Получить адрес ОКС из солр по уному.
     *
     * @param unom уном
     * @return адрес
     */
    @Cacheable(value = CacheConfig.CCO_ADDRESS_BY_UNOM)
    public String getCcoAddressByUnom(String unom) {
        final SearchResponse ccoFromSolr = getCcoFromSolrByUnom(unom);
        final List<Map<String, Object>> docs = ccoFromSolr.getDocs();

        if (nonNull(docs) && nonNull(docs.get(0))) {
            if (nonNull(docs.get(0).get("ugd_ps_oks_assignedAddress"))) {
                return (String) docs.get(0).get("ugd_ps_oks_assignedAddress");
            } else if (nonNull(docs.get(0).get("ugd_ps_oks_buildingAddress"))) {
                return (String) docs.get(0).get("ugd_ps_oks_buildingAddress");
            }
        }
        return null;
    }

    public String getCadNumberByUnom(String unom) {
        final SearchResponse ccoFromSolr = getCcoFromSolrByUnom(unom);
        final List<Map<String, Object>> docs = ccoFromSolr.getDocs();

        if (nonNull(docs) && nonNull(docs.get(0))) {
            if (nonNull(docs.get(0).get("ugd_ps_oks_cadastralNum"))) {
                return (String) docs.get(0).get("ugd_ps_oks_cadastralNum");
            }
        }
        return null;
    }

    /**
     * Получить адреса ОКС из солр.
     *
     * @return адрес
     */
    public Map<String, CcoInfo> getCcoInfoByUnoms() {
        final SearchResponse ccoFromSolr = getAllAddressesFromSolr();
        final List<Map<String, Object>> docs = ccoFromSolr.getDocs();

        return ListUtils.emptyIfNull(docs)
            .stream()
            .filter(map -> nonNull(map.get("ugd_ps_oks_assignedAddress"))
                || nonNull(map.get("ugd_ps_oks_buildingAddress")))
            .collect(Collectors.toMap(
                map -> Objects.toString(map.get("ugd_ps_oks_unom")),
                map -> CcoInfo.builder()
                    .address(toStringSafe(getCcoAddress(map)))
                    .cadNumber(toStringSafe(getCcoCadNumber(map)))
                    .build(),
                (s1, s2) -> s1)
            );
    }

    public List<CcoInfo> getAllCcoInfoAfterLastUpdate(final LocalDateTime lastUpdateDateTime) {
        final SearchResponse ccoFromSolr = getAllCcoAfterLastUpdate(lastUpdateDateTime);
        final List<Map<String, Object>> docs = ccoFromSolr.getDocs();

        return ListUtils.emptyIfNull(docs)
            .stream()
            .map(map -> CcoInfo.builder()
                .address(toStringSafe(getCcoAddress(map)))
                .psDocumentId(toStringSafe(getCcoPsDocId(map)))
                .cadNumber(toStringSafe(getCcoCadNumber(map)))
                .unom(toStringSafe(getCcoUnom(map)))
                .districts(toListSafe(getCcoDistricts(map)))
                .areas(toListSafe(getCcoAreas(map)))
                .build())
            .collect(Collectors.toList());
    }

    public CcoInfo getCcoInfoByUnom(final String unom) {
        final SearchResponse ccoFromSolr = getCcoFromSolrByUnom(unom);
        final List<Map<String, Object>> docs = ccoFromSolr.getDocs();

        return ListUtils.emptyIfNull(docs)
            .stream()
            .findFirst()
            .map(doc -> CcoInfo.builder()
                .address(toStringSafe(getCcoAddress(doc)))
                .cadNumber(toStringSafe(getCcoCadNumber(doc)))
                .build())
            .orElse(null);
    }

    private SearchResponse getAllCcoAfterLastUpdate(final LocalDateTime lastUpdateDateTime) {
        final QuerySearch querySearch = new QuerySearch();
        querySearch.setPage(0);
        querySearch.setPageSize(50000);
        querySearch.setSort("ugd_ps_oks_addressName asc");
        querySearch.setTypes(Collections.singletonList("UGD_PS_CAPITAL-CONSTRUCTION-OBJECT"));
        querySearch.setQuery("ugd_ps_oks_dateLastUpdate:["
            + lastUpdateDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z"
            + " TO NOW] AND ugd_ps_oks_unom: [* TO *]");
        querySearch.setFieldList(Arrays.asList(
            "sys_documentId",
            "ugd_ps_oks_documentId",
            "ugd_ps_oks_assignedAddress",
            "ugd_ps_oks_buildingAddress",
            "ugd_ps_oks_cadastralNum",
            "ugd_ps_oks_unom",
            "ugd_ps_oks_areas",
            "ugd_ps_oks_districts"
        ));
        return searchRemoteService.searchByQuery(querySearch);
    }

    /**
     * Получить адрес ОКС из солр по уному.
     *
     * @param unom уном
     * @return адрес
     */
    public String getAddressByCcoUnom(final BigInteger unom) {
        try {
            final String newFlatAddress = restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getPsCcoAddressByUnom(),
                HttpMethod.GET,
                null,
                String.class,
                unom);
            if (newFlatAddress.equals("null")) {
                return "";
            }
            return newFlatAddress;
        } catch (RIException riException) {
            log.error("Unable to getAddressByCcoUnom by unom {}", unom, riException);
        }
        return null;
    }

    /**
     * Получить id ОКС из солр по уному.
     *
     * @param unom уном
     * @return id
     */
    public String getCcoIdByUnom(String unom) {
        SearchResponse ccoFromSolr = getCcoFromSolrByUnom(unom);

        List<Map<String, Object>> docs = ccoFromSolr.getDocs();

        if (nonNull(docs) && nonNull(docs.get(0))) {
            if (nonNull(docs.get(0).get("sys_documentId"))) {
                return (String) docs.get(0).get("sys_documentId");
            }
        }
        return null;
    }

    /**
     * Получить адрес ОКС и количество квартир из солр по уному.
     *
     * @param unom уном
     * @return мапа: address - адрес ОКС, flatCount - количество квартир ОКС
     */
    public Map<String, String> getCcoAddressAndFlatCountByUnom(String unom) {
        SearchResponse ccoFromSolr = getCcoFromSolrByUnom(unom);

        List<Map<String, Object>> docs = ccoFromSolr.getDocs();
        Map<String, String> res = new HashMap<>();

        if (nonNull(docs) && nonNull(docs.get(0))) {
            if (nonNull(docs.get(0).get("ugd_ps_oks_assignedAddress"))) {
                res.put("address", (String) docs.get(0).get("ugd_ps_oks_assignedAddress"));
            } else if (nonNull(docs.get(0).get("ugd_ps_oks_buildingAddress"))) {
                res.put("address", (String) docs.get(0).get("ugd_ps_oks_buildingAddress"));
            }
            if (nonNull(docs.get(0).get("ugd_ps_oks_flatCount"))) {
                res.put("flatCount", String.valueOf((int) docs.get(0).get("ugd_ps_oks_flatCount")));
            }
        }
        return res;
    }

    /**
     * Получить ОКС по уному.
     *
     * @param unom уном
     * @return SearchResponse
     */
    public SearchResponse getCcoFromSolrByUnom(final String unom) {
        QuerySearch querySearch = new QuerySearch();

        querySearch.setPage(0);
        querySearch.setPageSize(1);
        querySearch.setSort("ugd_ps_oks_addressName asc");
        querySearch.setTypes(Collections.singletonList("UGD_PS_CAPITAL-CONSTRUCTION-OBJECT"));
        querySearch.setQuery("ugd_ps_oks_unom:(" + unom + ")");

        return searchRemoteService.searchByQuery(querySearch);
    }

    /**
     * Получить ОКС адреса.
     *
     * @return SearchResponse
     */
    public SearchResponse getAllAddressesFromSolr() {
        QuerySearch querySearch = new QuerySearch();

        querySearch.setPage(0);
        querySearch.setPageSize(10000);
        querySearch.setSort("ugd_ps_oks_addressName asc");
        querySearch.setTypes(Collections.singletonList("UGD_PS_CAPITAL-CONSTRUCTION-OBJECT"));
        querySearch.setQuery("ugd_ps_oks_unom:(*)");
        querySearch.setFieldList(Arrays.asList("ugd_ps_oks_buildingAddress", "ugd_ps_oks_assignedAddress",
            "ugd_ps_oks_unom", "ugd_ps_oks_cadastralNum"));
        return searchRemoteService.searchByQuery(querySearch);
    }

    /**
     * Получить районы ОКС из солр по уному.
     *
     * @param unom уном
     * @return районы
     */
    public ArrayList<String> getCcoDistrictsByUnom(String unom) {
        SearchResponse ccoFromSolr = getCcoFromSolrByUnom(unom);

        List<Map<String, Object>> docs = ccoFromSolr.getDocs();

        if (nonNull(docs) && nonNull(docs.get(0))) {
            if (nonNull(docs.get(0).get("ugd_ps_oks_districts"))) {
                return (ArrayList<String>) docs.get(0).get("ugd_ps_oks_districts");
            }
        }
        return null;
    }

    /**
     * Получение списка всех ОКСов учавствующих в программе реновации.
     *
     * @return список ОКСов
     */
    public List<CcoDto> fetchCcoList() {
        final Map<String, CcoInfo> ccoAddressMap = retrieveCcoAddressMap();

        return ccoAddressMap.entrySet().stream()
            .map(entry -> CcoDto.builder()
                .id(entry.getKey())
                .address(entry.getValue().getAddress())
                .unom(entry.getValue().getUnom())
                .build()
            )
            .collect(Collectors.toList());
    }

    /**
     * Получить ОКС по адресу (подстроке).
     *
     * @param addrSubstr адрес (подстрока)
     * @param top        кол-во элементов на стринице
     * @return SearchResponse
     */
    public SearchResponse getCcoFromSolrByAddrSubstr(String addrSubstr, Integer top) {
        QuerySearch querySearch = new QuerySearch();

        querySearch.setPage(0);
        querySearch.setPageSize(top);
        querySearch.setSort("ugd_ps_oks_addressName asc");
        querySearch.setTypes(Collections.singletonList("UGD_PS_CAPITAL-CONSTRUCTION-OBJECT"));
        if (nonNull(addrSubstr)) {
            StringBuilder filter = new StringBuilder();
            for (String str : addrSubstr.split(" ")) {
                filter.append("*").append(str).append("*").append(" AND ");
            }
            String filterQuery = filter.substring(0, filter.length() - 5);
            querySearch.setQuery(
                "ugd_ps_oks_isActual:(true) AND ("
                    + "ugd_ps_oks_addressName:(" + filterQuery + ")"
                    + "OR ugd_ps_oks_unom:(" + filterQuery + ")"
                    + ")"
            );
        }

        return searchRemoteService.searchByQuery(querySearch);
    }

    /**
     * Получить ОКС по Уномам.
     *
     * @param unoms список уномов
     * @return SearchResponse
     */
    public SearchResponse getCcoFromSolrByUnoms(Set<String> unoms) {
        QuerySearch querySearch = new QuerySearch();

        querySearch.setPage(0);
        querySearch.setPageSize(1000);
        querySearch.setSort("ugd_ps_oks_addressName asc");
        querySearch.setTypes(Collections.singletonList("UGD_PS_CAPITAL-CONSTRUCTION-OBJECT"));
        StringBuilder query = new StringBuilder();
        for (String unom : unoms) {
            query.append("ugd_ps_oks_unom:(").append(unom).append(") OR ");
        }
        querySearch.setQuery(query.substring(0, query.length() - 4));

        return searchRemoteService.searchByQuery(querySearch);
    }

    /**
     * Получение квартир ОКС для шахматки по unom..
     *
     * @param unom уном
     * @return flats
     */
    public EntrancesResponse getCcoChessFlatsByUnom(String unom) {
        try {
            return restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getPsCcoChessFlatsByUnom(),
                HttpMethod.GET,
                null,
                EntrancesResponse.class,
                unom
            );
        } catch (RIException riException) {
            log.error("Unable to getCcoChessFlatsByUnom by unom {}", unom, riException);
        }
        return null;
    }

    public List<Organization> psGetOrganizationsFromCco(final List<String> ccoIds) {
        final List<OksParticipant> oksParticipants = psGetParticipantsFromCco(ccoIds);
        final List<Organization> organizations = getCcoOrgsFromSolrByParticipant(new HashSet(oksParticipants));
        return organizations;
    }

    public List<OksParticipant> psGetParticipantsFromCco(final List<String> ccoIds) {
        try {
            return Arrays.asList(restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getPsGetParticipantsFromCco(),
                HttpMethod.POST,
                ccoIds,
                OksParticipant[].class
            ));
        } catch (RIException riException) {
            log.error("Unable to psGetParticipantsFromCco by ccoIds {}", ccoIds, riException);
        }
        return Collections.emptyList();
    }

    public List<Organization> getCcoOrgsFromSolrByParticipant(final Set<OksParticipant> participants) {
        final List<String> organizationIds = participants
            .stream()
            .filter(OksParticipant::isActual)
            .map(OksParticipant::getOrganizationId)
            .collect(Collectors.toList());
        if (organizationIds.isEmpty()) {
            return Collections.emptyList();
        }

        final QuerySearch querySearch = getCcoOrgsFromSolrByParticipantQuery(organizationIds);
        final SearchResponse searchResponse = searchRemoteService.searchByQuery(querySearch);
        if (searchResponse.getDocs() == null) {
            return Collections.emptyList();
        }

        final List<Map<String, Object>> solrDocuments = searchResponse.getDocs();
        return participants
            .stream()
            .filter(OksParticipant::isActual)
            .map(participant -> mapToOrganization(participant, solrDocuments))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private QuerySearch getCcoOrgsFromSolrByParticipantQuery(final List<String> organizationIds) {
        final QuerySearch querySearch = new QuerySearch();
        querySearch.setPage(0);
        querySearch.setPageSize(organizationIds.size());
        querySearch.setTypes(Collections.singletonList("UGD_NSI_ORGANIZATION-CATALOG-POSITION"));
        final StringBuilder query = new StringBuilder();
        final String orgEnumeration = String.join(" OR ", organizationIds);
        query
            .append("sys_documentId: (")
            .append(orgEnumeration)
            .append(")")
            .append(" OR ")
            .append("ugd_nsi_organization_ugdId: (")
            .append(orgEnumeration)
            .append(")");
        querySearch.setFilterQuery(Collections.singletonList(query.toString()));
        return querySearch;
    }

    private Organization mapToOrganization(
        final OksParticipant participant,
        final List<Map<String, Object>> solrDocuments
    ) {
        final List<Map<String, Object>> filteredSolrDocuments = solrDocuments.stream()
            .filter(
                document -> {
                    final String documentId = document.get("sys_documentId") == null
                        ? null
                        : (String) document.get("sys_documentId");
                    final String orgId = document.get("ugd_nsi_organization_ugdId") == null
                        ? null
                        : (String) document.get("ugd_nsi_organization_ugdId");
                    return StringUtils.equals(documentId, participant.getOrganizationId())
                        || StringUtils.equals(orgId, participant.getOrganizationId());
                }
            ).collect(Collectors.toList());

        if (filteredSolrDocuments.size() != 1) {
            log.warn(
                "Unable to find organization for participant: participantId {}, participantOrganizationId {}",
                participant.getId(),
                participant.getOrganizationId()
            );
            return null;
        }

        final Map<String, Object> solrDocument = filteredSolrDocuments.get(0);

        final String role = participant.getRole();
        final Organization organization = new Organization();
        organization.setRoleNumber(role);
        final String roleName = dictionaryService.getNameByCode(
            "ugd_memberRole", organization.getRoleNumber()
        );
        organization.setRoleName(roleName);

        if (nonNull(solrDocument.get("ugd_nsi_organization_inn"))) {
            organization.setInn((String) solrDocument.get("ugd_nsi_organization_inn"));
        }
        if (nonNull(solrDocument.get("ugd_nsi_organization_full_name"))) {
            organization.setFullName((String) solrDocument.get("ugd_nsi_organization_full_name"));
        }
        return organization;
    }

    /**
     * updateCcoFlatWithFlatDetails.
     *
     * @param unom        unom
     * @param flatDetails FlatDetails
     */
    public void updateCcoFlatWithFlatDetails(final String unom, final List<FlatDetails> flatDetails) {
        try {
            restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getUpdateCcoFlats(),
                HttpMethod.POST,
                flatDetails,
                String.class,
                unom
            );
            log.info("updateCcoFlatWithFlatDetails: UNOM: {} successfully sent", unom);
        } catch (RIException riException) {
            log.error("updateCcoFlatWithFlatDetails: UNOM: {} failed sent", unom, riException);
        }
    }

    /**
     * updateCcoFlatWithFlatDetail.
     *
     * @param unom       unom
     * @param flatDetail FlatDetail
     */
    public void updateCcoFlatWithFlatDetail(final String unom, final FlatDetails flatDetail) {
        try {
            restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getUpdateCcoFlat(),
                HttpMethod.POST,
                flatDetail,
                String.class,
                unom
            );
            log.info("updateCcoFlatWithFlatDetail: UNOM: {} successfully sent", unom);
        } catch (RIException riException) {
            log.error("updateCcoFlatWithFlatDetail: UNOM: {} failed sent", unom, riException);
        }
    }

    /**
     * Получение квартир ОКС для шахматки по unom..
     *
     * @param unom     уном ОКС
     * @param entrance номер подъезда
     * @return flats
     */
    public FlatsResponse getCcoChessEntranceFlatsByUnom(String unom, String entrance) {
        try {
            return restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getPsCcoChessEntranceFlatsByUnom(),
                HttpMethod.GET,
                null,
                FlatsResponse.class,
                unom, entrance
            );
        } catch (RIException riException) {
            log.error(
                "Unable to getCcoChessEntranceFlatsByUnom by unom {} and entrance {}",
                unom,
                entrance,
                riException
            );
        }
        return null;
    }

    /**
     * Получение данных квартиры ОКС для шахматки по unom и номеру.
     *
     * @param unom       уном ОКС
     * @param flatNumber номер квартиры
     * @return информация по квартире
     */
    public CcoFlatInfoResponse getCcoChessFlatInfoByUnom(String unom, String flatNumber) {
        try {
            return restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getPsCcoChessFlatInfoByUnom(),
                HttpMethod.GET,
                null,
                CcoFlatInfoResponse.class,
                unom, flatNumber
            );
        } catch (RIException riException) {
            log.error(
                "Unable to getCcoChessFlatInfoByUnom by unom {} and flatNumber {}",
                unom,
                flatNumber,
                riException
            );
        }
        return null;
    }

    /**
     * Получение данных квартиры ОКС для шахматки по unom, номеру, подъезду и этажу.
     *
     * @param unom       уном ОКС
     * @param flatNumber номер квартиры
     * @param entrance номер подъезда
     * @param floor этаж
     * @return информация по квартире
     */
    public CcoFlatInfoResponse getCcoChessFlatInfoByUnom(
        String unom, String flatNumber, String entrance, String floor
    ) {
        try {
            return restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getPsCcoChessFlatInfoByUnomFlatnumberEntranceAndFloor(),
                HttpMethod.GET,
                null,
                CcoFlatInfoResponse.class,
                unom, flatNumber, entrance, floor
            );
        } catch (RIException riException) {
            log.error(
                "Unable to getCcoChessFlatInfoByUnom by unom {}, flatNumber {}, entrance {} and floor {}",
                unom,
                flatNumber,
                entrance,
                floor,
                riException
            );
        }
        return null;
    }

    /**
     * Получение статистики по заселяемому дому по УНОМ.
     *
     * @param unom УНОМ дома
     * @return статистика по заселяемому дому
     */
    public StatsHouseResponse getStatOksHouses(String unom) {
        try {
            return restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getPsCcoChessStatsByUnom(),
                HttpMethod.GET,
                null,
                StatsHouseResponse.class,
                unom
            );
        } catch (RIException riException) {
            log.error("Unable to getStatOksHouses by unom {}", unom, riException);
        }
        return null;
    }

    /**
     * Получение полной информации по ОКС для шахматки по unom.
     *
     * @param unom уном
     * @return полная информация ОКС
     */
    public FullOksHousesResponse getFullCcoChessInfo(String unom) {
        try {
            return restUtils.sendJsonRequest(
                playgroundUrl + integrationProperties.getPsFullCcoChessInfoByUnom(),
                HttpMethod.GET,
                null,
                FullOksHousesResponse.class,
                unom
            );
        } catch (RIException riException) {
            log.error("Unable to getFullCcoChessInfo by unom {}", unom, riException);
        }
        return null;
    }

    /**
     * Получить id и адрес ОКС из солр по уному.
     *
     * @param unom уном
     * @return CcoSolrResponse
     */
    public CcoSolrResponse getCcoSolrResponseByUnom(String unom) {
        SearchResponse ccoFromSolr = getCcoFromSolrByUnom(unom);

        List<Map<String, Object>> docs = ccoFromSolr.getDocs();

        CcoSolrResponse ccoSolrResponse = new CcoSolrResponse();
        if (nonNull(docs) && nonNull(docs.get(0))) {
            final Map<String, Object> cco = docs.get(0);
            ccoSolrResponse.setId((String) cco.get("sys_documentId"));

            if (nonNull(cco.get("ugd_ps_oks_assignedAddress"))) {
                ccoSolrResponse.setAddress((String) cco.get("ugd_ps_oks_assignedAddress"));
            } else if (nonNull(cco.get("ugd_ps_oks_buildingAddress"))) {
                ccoSolrResponse.setAddress((String) cco.get("ugd_ps_oks_buildingAddress"));
            }

            if (nonNull(cco.get("ugd_ps_oks_areas"))) {
                final List<String> areas = (List<String>) cco.get("ugd_ps_oks_areas");
                if (!areas.isEmpty()) {
                    ccoSolrResponse.setArea(areas.get(0));
                }
            }

            if (nonNull(cco.get("ugd_ps_oks_districts"))) {
                final List<String> districts = (List<String>) cco.get("ugd_ps_oks_districts");
                if (!districts.isEmpty()) {
                    ccoSolrResponse.setDistrict(districts.get(0));
                }
            }

            final String completionDate = (String) ofNullable(cco.get("ugd_ps_oks_factInputDate"))
                .orElse(cco.get("ugd_ps_oks_planDateInput"));

            ofNullable(completionDate)
                .map(LocalDate::parse)
                .ifPresent(ccoSolrResponse::setCompletionDate);
        }
        return ccoSolrResponse;
    }

    private Map<String, CcoInfo> retrieveCcoAddressMap() {
        return ofNullable(getCcoListFromSolr())
            .map(SearchResponse::getDocs)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(solrDocument -> nonNull(solrDocument.get("sys_documentId"))
                && nonNull(getCcoUnom(solrDocument))
                && nonNull(getCcoAddress(solrDocument)))
            .collect(Collectors.toMap(
                solrDocument -> solrDocument.get("sys_documentId").toString(),
                solrDocument -> CcoInfo.builder()
                    .address(getCcoAddress(solrDocument).toString())
                    .unom(getCcoUnom(solrDocument).toString())
                    .build(),
                (s1, s2) -> s1
            ));
    }

    private Object getCcoAddress(final Map<String, Object> solrDocument) {
        return ofNullable(solrDocument.get("ugd_ps_oks_assignedAddress"))
            .orElseGet(() -> solrDocument.get("ugd_ps_oks_buildingAddress"));
    }

    private Object getCcoOrgExternalIds(final Map<String, Object> solrDocument) {
        return solrDocument.get("ugd_ps_oks_orgInn");
    }

    private Object getCcoOrgFullNames(final Map<String, Object> solrDocument) {
        return solrDocument.get("ugd_ps_oks_orgNameFull");
    }

    private Object getCcoOrgRoles(final Map<String, Object> solrDocument) {
        return solrDocument.get("ugd_ps_oks_orgRole");
    }

    private Object getCcoAreas(final Map<String, Object> solrDocument) {
        return solrDocument.get("ugd_ps_oks_areas");
    }

    private Object getCcoDistricts(final Map<String, Object> solrDocument) {
        return solrDocument.get("ugd_ps_oks_districts");
    }

    private Object getCcoUnom(final Map<String, Object> solrDocument) {
        return solrDocument.get("ugd_ps_oks_unom");
    }

    private Object getCcoPsDocId(final Map<String, Object> solrDocument) {
        return solrDocument.get("ugd_ps_oks_documentId");
    }

    private Object getCcoCadNumber(final Map<String, Object> solrDocument) {
        return solrDocument.get("ugd_ps_oks_cadastralNum");
    }

    private SearchResponse getCcoListFromSolr() {
        final QuerySearch querySearch = new QuerySearch();
        querySearch.setPage(0);
        querySearch.setPageSize(10000);
        querySearch.setQuery("ugd_ps_oks_booleanRenovation: true");
        querySearch.setTypes(Collections.singletonList("UGD_PS_CAPITAL-CONSTRUCTION-OBJECT"));

        return searchRemoteService.searchByQuery(querySearch);
    }

    private String toStringSafe(final Object stringObject) {
        return stringObject == null ? null : String.valueOf(stringObject);
    }

    private List<String> toListSafe(final Object listObject) {
        return listObject instanceof List ? (List<String>) listObject : Collections.emptyList();
    }
}
