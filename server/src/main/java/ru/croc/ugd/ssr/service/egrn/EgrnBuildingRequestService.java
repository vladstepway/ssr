package ru.croc.ugd.ssr.service.egrn;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.service.bus.request.payload.EgrnBuildingRequestPayloadFactory.CADASTRAL_NUMBER_KEY;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.HouseToResettle;
import ru.croc.ugd.ssr.HouseToSettle;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.ResettlementRequest;
import ru.croc.ugd.ssr.ResettlementRequestType;
import ru.croc.ugd.ssr.dto.bus.CreateBusRequestDto;
import ru.croc.ugd.ssr.dto.egrn.CreateEgrnBuildingRequestDto;
import ru.croc.ugd.ssr.dto.egrn.CreateEgrnFlatRequestDto;
import ru.croc.ugd.ssr.egrn.BuildingEgrnResponse;
import ru.croc.ugd.ssr.egrn.BuildingRequestCriteria;
import ru.croc.ugd.ssr.egrn.CadNumber;
import ru.croc.ugd.ssr.egrn.EgrnBuildingRequest;
import ru.croc.ugd.ssr.egrn.EgrnBuildingRequestData;
import ru.croc.ugd.ssr.egrn.ExtractAboutPropertyBuild;
import ru.croc.ugd.ssr.egrn.ObjectCadNumber;
import ru.croc.ugd.ssr.egrn.ObjectFactory;
import ru.croc.ugd.ssr.egrn.ParamsRoomObject;
import ru.croc.ugd.ssr.egrn.ParamsRoomObject.RoomRecord;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.mapper.EgrnBuildingRequestMapper;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.ResettlementRequestDocument;
import ru.croc.ugd.ssr.model.bus.BusRequestDocument;
import ru.croc.ugd.ssr.model.egrn.EgrnBuildingRequestDocument;
import ru.croc.ugd.ssr.model.integration.busresponse.SmevResponse;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.bus.request.BusRequestService;
import ru.croc.ugd.ssr.service.bus.request.BusRequestType;
import ru.croc.ugd.ssr.service.document.EgrnBuildingRequestDocumentService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.bind.JAXBElement;

@Slf4j
@Service
@RequiredArgsConstructor
//TODO Konstantin: minimize code duplication
public class EgrnBuildingRequestService {

    private static final String SSR_ASUR_CODE = "0874-9000154";
    private static final String GU_CODE = "086601";

    private static final String BUS_REQUEST_ERROR_STATUS = "-1";
    private static final String BUS_REQUEST_SENT_STATUS = "0";

    private final EgrnBuildingRequestDocumentService egrnBuildingRequestDocumentService;
    private final EnoCreator enoCreator;
    private final EgrnBuildingRequestMapper egrnBuildingRequestMapper;
    private final BusRequestService busRequestService;
    private final SsrFilestoreService ssrFilestoreService;
    private final XmlUtils xmlUtils;
    private final EgrnFlatRequestService egrnFlatRequestService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final CapitalConstructionObjectService ccoService;

    @Value("${ugd.ssr.egrn-building-request.enabled:false}")
    private boolean isEgrnBuildingRequestEnabled;

    public void create(final List<CreateEgrnBuildingRequestDto> createEgrnBuildingRequestDtoList) {
        createEgrnBuildingRequestDtoList.forEach(this::processRequestCreation);
    }

    private void processRequestCreation(final CreateEgrnBuildingRequestDto createEgrnBuildingRequestDto) {
        final String serviceNumber = enoCreator.generateAsurEnoNumber(SSR_ASUR_CODE, GU_CODE);
        try {
            String cadastralNumber = createEgrnBuildingRequestDto.getCadastralNumber();
            if (StringUtils.isBlank(cadastralNumber)
                && StringUtils.isNotBlank(createEgrnBuildingRequestDto.getRealEstateDocumentId())) {
                final String cadastralNumberFromRealEstate = realEstateDocumentService
                    .fetchById(createEgrnBuildingRequestDto.getRealEstateDocumentId())
                    .map(RealEstateDocument::getDocument)
                    .map(RealEstate::getRealEstateData)
                    .map(this::retrieveCadastralNum)
                    .orElse(null);

                if (nonNull(cadastralNumberFromRealEstate)) {
                    cadastralNumber = cadastralNumberFromRealEstate;
                } else {
                    throw new SsrException(
                        "Cadastral number not found by realEstateDocumentId "
                            + createEgrnBuildingRequestDto.getRealEstateDocumentId()
                    );
                }
            }
            if (StringUtils.isBlank(cadastralNumber)) {
                throw new SsrException("Cadastral number is empty: unom " + createEgrnBuildingRequestDto.getUnom()
                    + ", address" + createEgrnBuildingRequestDto.getAddress()
                    + ", ccoDocumentId" + createEgrnBuildingRequestDto.getCcoDocumentId()
                    + ", realEstateDocumentId" + createEgrnBuildingRequestDto.getRealEstateDocumentId());
            }

            log.info(
                "Send egrn building request; cadastralNumber {}, serviceNumber {}",
                cadastralNumber,
                serviceNumber
            );

            final EgrnBuildingRequestDocument egrnBuildingRequestDocument = egrnBuildingRequestMapper
                .toEgrnBuildingRequestDocument(
                    createEgrnBuildingRequestDto.toBuilder().cadastralNumber(cadastralNumber).build(),
                    serviceNumber
                );

            egrnBuildingRequestDocumentService.createDocument(egrnBuildingRequestDocument, false, null);

            final EgrnBuildingRequestData egrnBuildingRequestData = egrnBuildingRequestDocument
                .getDocument()
                .getEgrnBuildingRequestData();

            try {
                final BusRequestDocument busRequestDocument = sendBusRequest(
                    serviceNumber, cadastralNumber, egrnBuildingRequestDocument
                );
                egrnBuildingRequestData.setBusRequestDocumentId(busRequestDocument.getId());
                egrnBuildingRequestData.setStatusCode(BUS_REQUEST_SENT_STATUS);
                egrnBuildingRequestDocumentService.updateDocument(egrnBuildingRequestDocument);
            } catch (Exception e) {
                egrnBuildingRequestData.setStatusCode(BUS_REQUEST_ERROR_STATUS);
                egrnBuildingRequestDocumentService.updateDocument(egrnBuildingRequestDocument);
                throw e;
            }
        } catch (Exception e) {
            log.error("Unable to process request: serviceNumber {}, {}", serviceNumber, e.getMessage(), e);
        }
    }

    private BusRequestDocument sendBusRequest(
        final String serviceNumber,
        final String cadastralNumber,
        final EgrnBuildingRequestDocument egrnBuildingRequestDocument
    ) {
        final CreateBusRequestDto createBusRequestDto = CreateBusRequestDto
            .builder()
            .busRequestType(BusRequestType.EGRN_BUILDING)
            .serviceNumber(serviceNumber)
            .serviceTypeCode(GU_CODE)
            .ochdFolderGuid(egrnBuildingRequestDocument.getFolderId())
            .customVariables(
                Collections.singletonMap(CADASTRAL_NUMBER_KEY, cadastralNumber)
            )
            .build();

        return busRequestService.sendBusRequest(createBusRequestDto);
    }

    public EgrnBuildingRequestDocument fillResponseData(
        final EgrnBuildingRequestDocument egrnBuildingRequestDocument, final SmevResponse response
    ) {
        final String folderId = egrnBuildingRequestDocument.getFolderId();
        final String zipFileStoreId = ssrFilestoreService.retrieveFileStoreIdsFromSubfoldersByType(folderId, "zip")
            .stream()
            .findFirst()
            .orElse(null);

        final byte[] zipFileContents = ssrFilestoreService.getFile(zipFileStoreId);
        final List<Pair<String, String>> filesFromZip = ssrFilestoreService
            .retrieveFilesFromZip(zipFileContents, folderId);

        final String xmlFileStoreId = retrieveFileStoreIdByType(filesFromZip, "xml");
        final String pdfFileStoreId = retrieveFileStoreIdByType(filesFromZip, "pdf");

        final ExtractAboutPropertyBuild extractAboutPropertyBuild = parseEgrnBuildingXmlMessage(xmlFileStoreId);

        final EgrnBuildingRequestData egrnBuildingRequestData = egrnBuildingRequestDocument
            .getDocument()
            .getEgrnBuildingRequestData();

        egrnBuildingRequestData.setStatusCode(response.getStatusCode());
        egrnBuildingRequestData.setErrorDescription(null);

        final BuildingEgrnResponse egrnResponse = ofNullable(egrnBuildingRequestData.getEgrnResponse())
            .orElseGet(BuildingEgrnResponse::new);
        egrnResponse.setPdfFileStoreId(pdfFileStoreId);
        egrnResponse.setXmlFileStoreId(xmlFileStoreId);
        egrnResponse.setZipFileStoreId(zipFileStoreId);
        egrnResponse.setResponseDateTime(LocalDateTime.now());
        egrnResponse.setExtractAboutPropertyBuild(extractAboutPropertyBuild);

        egrnBuildingRequestData.setEgrnResponse(egrnResponse);
        return egrnBuildingRequestDocument;
    }

    private ExtractAboutPropertyBuild parseEgrnBuildingXmlMessage(final String xmlFileStoreId) {
        return ofNullable(xmlFileStoreId)
            .map(ssrFilestoreService::getFile)
            .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
            .map(contents -> contents.replace(
                "<extract_about_property_build>",
                "<extract_about_property_build xmlns=\"http://www.ugd.croc.ru/ssr/egrn\">"))
            .flatMap(xmlMessage -> xmlUtils
                .<JAXBElement<ExtractAboutPropertyBuild>>parseXml(xmlMessage, new Class[]{ObjectFactory.class}))
            .map(JAXBElement::getValue)
            .orElse(null);
    }

    private String retrieveFileStoreIdByType(final List<Pair<String, String>> filesFromZip, final String type) {
        return filesFromZip.stream()
            .filter(pair -> Objects.equals(pair.getRight(), type))
            .findFirst()
            .map(Pair::getLeft)
            .orElse(null);
    }

    public void sendFlatRequests(final EgrnBuildingRequestDocument egrnBuildingRequestDocument) {
        final EgrnBuildingRequestData egrnBuildingRequestData = egrnBuildingRequestDocument
            .getDocument()
            .getEgrnBuildingRequestData();

        final List<RoomRecord> roomRecords = retrieveRoomRecords(egrnBuildingRequestData);

        final List<CreateEgrnFlatRequestDto> egrnFlatRequests = roomRecords.stream()
            .map(RoomRecord::getObject)
            .map(ObjectCadNumber::getCommonData)
            .map(CadNumber::getCadNumber)
            .map(flatCadastralNumber -> toCreateEgrnFlatRequestDto(flatCadastralNumber, egrnBuildingRequestData))
            .collect(Collectors.toList());

        log.info(
            "Send {} egrn flat requests for egrn building request {}",
            egrnFlatRequests.size(),
            egrnBuildingRequestDocument.getId()
        );

        egrnFlatRequestService.create(egrnFlatRequests);
    }

    private List<RoomRecord> retrieveRoomRecords(final EgrnBuildingRequestData egrnBuildingRequestData) {
        return ofNullable(egrnBuildingRequestData)
            .map(EgrnBuildingRequestData::getEgrnResponse)
            .map(BuildingEgrnResponse::getExtractAboutPropertyBuild)
            .map(ExtractAboutPropertyBuild::getRoomRecords)
            .map(ParamsRoomObject::getRoomRecord)
            .orElse(Collections.emptyList());
    }

    private CreateEgrnFlatRequestDto toCreateEgrnFlatRequestDto(
        final String flatCadastralNumber, final EgrnBuildingRequestData egrnBuildingRequestData
    ) {
        final BuildingRequestCriteria requestCriteria = egrnBuildingRequestData.getRequestCriteria();

        return CreateEgrnFlatRequestDto.builder()
            .cadastralNumber(flatCadastralNumber)
            .realEstateDocumentId(requestCriteria.getRealEstateDocumentId())
            .ccoDocumentId(requestCriteria.getCcoDocumentId())
            .unom(requestCriteria.getUnom())
            .build();
    }

    public void requestByRealEstateUnom(final String unom) {
        requestByRealEstate(realEstateDocumentService.fetchDocumentByUnom(unom));
    }

    public void requestByRealEstate(final RealEstateDocument realEstateDocument) {
        if (!isEgrnBuildingRequestEnabled) {
            log.info("Egrn building request is disabled");
            return;
        }
        ofNullable(realEstateDocument)
            .map(this::toCreateEgrnBuildingRequestDto)
            .filter(dto -> nonNull(dto.getCadastralNumber()))
            .ifPresent(this::requestIfNeeded);
    }

    public void requestByResettlementRequest(final ResettlementRequestDocument resettlementRequestDocument) {
        if (!isEgrnBuildingRequestEnabled) {
            log.info("Egrn building request is disabled");
            return;
        }

        requestRealEstate(resettlementRequestDocument);

        requestCapitalConstructionObject(resettlementRequestDocument);
    }

    private void requestRealEstate(final ResettlementRequestDocument resettlementRequestDocument) {
        ofNullable(resettlementRequestDocument)
            .map(ResettlementRequestDocument::getDocument)
            .map(ResettlementRequest::getMain)
            .map(ResettlementRequestType::getHousesToSettle)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(HouseToSettle::getHousesToResettle)
            .flatMap(List::stream)
            .map(HouseToResettle::getRealEstateId)
            .filter(Objects::nonNull)
            .map(realEstateDocumentService::fetchById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(this::toCreateEgrnBuildingRequestDto)
            .filter(dto -> nonNull(dto.getCadastralNumber()))
            .forEach(this::requestIfNeeded);
    }

    private String retrieveCadastralNum(final RealEstateDataType realEstateDataType) {
        return ofNullable(realEstateDataType)
            .map(RealEstateDataType::getCadastralNums)
            .map(RealEstateDataType.CadastralNums::getCadastralNum)
            .map(List::stream)
            .orElse(Stream.empty())
            .findFirst()
            .map(RealEstateDataType.CadastralNums.CadastralNum::getValue)
            .orElse(null);
    }

    private void requestCapitalConstructionObject(final ResettlementRequestDocument resettlementRequestDocument) {
        ofNullable(resettlementRequestDocument)
            .map(ResettlementRequestDocument::getDocument)
            .map(ResettlementRequest::getMain)
            .map(ResettlementRequestType::getHousesToSettle)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(Objects::nonNull)
            .filter(house -> nonNull(house.getCapitalConstructionObjectUnom()))
            .map(this::toCreateEgrnBuildingRequestDto)
            .filter(dto -> nonNull(dto.getCadastralNumber()))
            .forEach(this::requestIfNeeded);
    }

    private CreateEgrnBuildingRequestDto toCreateEgrnBuildingRequestDto(final RealEstateDocument realEstateDocument) {
        final RealEstateDataType realEstateDataType = realEstateDocument.getDocument().getRealEstateData();
        return CreateEgrnBuildingRequestDto
            .builder()
            .realEstateDocumentId(realEstateDocument.getId())
            .unom(realEstateDataType.getUNOM().toString())
            .address(realEstateDataType.getAddress())
            .cadastralNumber(retrieveCadastralNum(realEstateDataType))
            .build();
    }

    private CreateEgrnBuildingRequestDto toCreateEgrnBuildingRequestDto(final HouseToSettle houseToSettle) {
        return CreateEgrnBuildingRequestDto
            .builder()
            .ccoDocumentId(houseToSettle.getCapitalConstructionObjectId())
            .unom(houseToSettle.getCapitalConstructionObjectUnom())
            .cadastralNumber(ccoService.getCadNumberByUnom(houseToSettle.getCapitalConstructionObjectUnom()))
            .build();
    }

    private void requestIfNeeded(final CreateEgrnBuildingRequestDto createEgrnBuildingRequestDto) {
        final boolean isAlreadyRequested = egrnBuildingRequestDocumentService
            .fetchLastRequestByCadastralNumber(createEgrnBuildingRequestDto.getCadastralNumber())
            .map(EgrnBuildingRequestDocument::getDocument)
            .map(EgrnBuildingRequest::getEgrnBuildingRequestData)
            .map(EgrnBuildingRequestData::getCreationDateTime)
            .filter(creationDateTime -> creationDateTime.isAfter(LocalDateTime.now().minusDays(5)))
            .isPresent();
        if (!isAlreadyRequested) {
            processRequestCreation(createEgrnBuildingRequestDto);
        }
    }
}
