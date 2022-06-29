package ru.croc.ugd.ssr.service.ipev;

import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.egrn.CreateEgrnFlatRequestDto;
import ru.croc.ugd.ssr.dto.ipev.IpevOrrResponseDto;
import ru.croc.ugd.ssr.enums.IpevStatus;
import ru.croc.ugd.ssr.ipev.IpevLogData;
import ru.croc.ugd.ssr.model.ipev.IpevLogDocument;
import ru.croc.ugd.ssr.service.document.IpevLogDocumentService;
import ru.croc.ugd.ssr.service.egrn.EgrnFlatRequestService;
import ru.croc.ugd.ssr.utils.RegularExpressionUtils;
import ru.reinform.cdp.search.model.search_input.QuerySearch;
import ru.reinform.cdp.search.model.search_output.SearchResponse;
import ru.reinform.cdp.search.service.SearchRemoteService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DefaultIpevEventService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultIpevEventService implements IpevEventService {

    private static final int SOLR_REQUEST_QUERY_SIZE = 1000;

    private final SearchRemoteService searchRemoteService;
    private final EgrnFlatRequestService egrnFlatRequestService;
    private final IpevLogDocumentService ipevLogDocumentService;

    @Override
    @Async
    public void processEvent(final IpevOrrResponseDto ipevOrrResponseDto, final IpevLogDocument ipevLogDocument) {
        final Map<String, CreateEgrnFlatRequestDto> createEgrnFlatRequestDtoMap = new HashMap<>();
        final List<String> cadastralNumbers = ipevOrrResponseDto.getCadastralNumbers();

        int from = 0;
        int to = SOLR_REQUEST_QUERY_SIZE;
        while (from < cadastralNumbers.size()) {
            if (to > cadastralNumbers.size()) {
                to = cadastralNumbers.size();
            }
            final List<String> cadastralNumberList = cadastralNumbers.subList(from, to);
            createEgrnFlatRequestDtoMap.putAll(solrSearchEgrnFlatRequests(cadastralNumberList));
            from += SOLR_REQUEST_QUERY_SIZE;
            to += SOLR_REQUEST_QUERY_SIZE;
        }

        egrnFlatRequestService.create(new ArrayList<>(createEgrnFlatRequestDtoMap.values()));

        final IpevLogData ipevLogData = ipevLogDocument
            .getDocument()
            .getIpevLogData();

        ipevLogData.setStatus(IpevStatus.PROCESSED.getStatus());
        ipevLogData.setEgrnRequestDateTime(LocalDateTime.now());
        ipevLogData.getFilteredCadastralNumbers().addAll(createEgrnFlatRequestDtoMap.keySet());

        ipevLogDocumentService.updateDocument(ipevLogDocument.getId(), ipevLogDocument, true, true, "endProcessEvent");
    }

    private Map<String, CreateEgrnFlatRequestDto> solrSearchEgrnFlatRequests(final List<String> cadastralNumbers) {
        final QuerySearch querySearch = new QuerySearch();
        querySearch.setPage(0);
        querySearch.setPageSize(10000);
        querySearch.setSort("ugd_ssr_egrn_flat_cadastral_number asc");
        querySearch.setTypes(Collections.singletonList("UGD_SSR_EGRN-FLAT-REQUEST"));
        querySearch.setQuery(solrQueryEgrnFlatRequests(cadastralNumbers));
        querySearch.setFieldList(Arrays.asList(
            "ugd_ssr_egrn_flat_cadastral_number",
            "ugd_ssr_egrn_flat_unom",
            "ugd_ssr_egrn_flat_number",
            "ugd_ssr_egrn_flat_real_estate_id",
            "ugd_ssr_egrn_flat_cco_id",
            "ugd_ssr_egrn_flat_resettled"
        ));
        final SearchResponse ccoFromSolr = searchRemoteService.searchByQuery(querySearch);

        final List<Map<String, Object>> docs = ccoFromSolr.getDocs();

        return ofNullable(docs)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(doc -> !("true".equals(toStringSafe(doc.get("ugd_ssr_egrn_flat_resettled")))))
            .map(doc -> CreateEgrnFlatRequestDto.builder()
                .cadastralNumber(toStringSafe(doc.get("ugd_ssr_egrn_flat_cadastral_number")))
                .unom(toStringSafe(doc.get("ugd_ssr_egrn_flat_unom")))
                .flatNumber(toStringSafe(doc.get("ugd_ssr_egrn_flat_number")))
                .realEstateDocumentId(toStringSafe(doc.get("ugd_ssr_egrn_flat_real_estate_id")))
                .ccoDocumentId(toStringSafe(doc.get("ugd_ssr_egrn_flat_cco_id")))
                .build())
            .collect(Collectors
                .toMap(CreateEgrnFlatRequestDto::getCadastralNumber, Function.identity(), (s1, s2) -> s1)
            );
    }

    private String solrQueryEgrnFlatRequests(final List<String> cadastralNumbers) {
        final List<String> solrCadastralNumbers = cadastralNumbers.stream()
            .map(RegularExpressionUtils::escapeSolrRegexCharacters)
            .collect(Collectors.toList());
        return "(ugd_ssr_egrn_flat_cadastral_number:("
            + String.join(" OR ", solrCadastralNumbers)
            + "))";
    }

    private String toStringSafe(final Object stringObject) {
        return stringObject == null ? null : String.valueOf(stringObject);
    }

}
