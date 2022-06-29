package ru.croc.ugd.ssr.service.egrn;

import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.service.bus.request.payload.EgrnFlatRequestPayloadFactory.CADASTRAL_NUMBER_KEY;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.MessageType;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.bus.CreateBusRequestDto;
import ru.croc.ugd.ssr.dto.egrn.CreateEgrnFlatRequestDto;
import ru.croc.ugd.ssr.egrn.Address;
import ru.croc.ugd.ssr.egrn.AddressLocationRoom;
import ru.croc.ugd.ssr.egrn.AddressMain;
import ru.croc.ugd.ssr.egrn.AddressOksLocation;
import ru.croc.ugd.ssr.egrn.Apartment;
import ru.croc.ugd.ssr.egrn.DetailedLevel;
import ru.croc.ugd.ssr.egrn.Dict;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequestData;
import ru.croc.ugd.ssr.egrn.ExtractAboutPropertyRoom;
import ru.croc.ugd.ssr.egrn.FlatEgrnResponse;
import ru.croc.ugd.ssr.egrn.FlatRequestCriteria;
import ru.croc.ugd.ssr.egrn.IndividualOut;
import ru.croc.ugd.ssr.egrn.ObjectFactory;
import ru.croc.ugd.ssr.egrn.ParamsRoomBase;
import ru.croc.ugd.ssr.egrn.RightHolderOut;
import ru.croc.ugd.ssr.egrn.RightHoldersOut;
import ru.croc.ugd.ssr.egrn.RightRecordsAboutProperty;
import ru.croc.ugd.ssr.egrn.RightRecordsAboutProperty.RightRecord;
import ru.croc.ugd.ssr.egrn.RoomRecordAboutProperty;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;
import ru.croc.ugd.ssr.integration.service.notification.ResidencePlaceRegistrationNotificationsDescriptor;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.mapper.EgrnFlatRequestMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.bus.BusRequestDocument;
import ru.croc.ugd.ssr.model.egrn.EgrnFlatRequestDocument;
import ru.croc.ugd.ssr.model.integration.busresponse.SmevResponse;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.bus.request.BusRequestService;
import ru.croc.ugd.ssr.service.bus.request.BusRequestType;
import ru.croc.ugd.ssr.service.document.EgrnFlatRequestDocumentService;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.xml.bind.JAXBElement;

@Slf4j
@Service
@RequiredArgsConstructor
public class EgrnFlatRequestService {

    public static final String BUS_REQUEST_SENT_STATUS = "0";
    private static final String BUS_REQUEST_ERROR_STATUS = "-1";

    private static final String SSR_ASUR_CODE = "0874-9000154";
    private static final String GU_CODE = "086601";
    private static final String LIVING_PURPOSE_CODE = "206002000000";

    @Value("${ugd.ssr.residence-place-registration.enabled:false}")
    private boolean residencePlaceRegistrationEnabled;
    @Value("${ugd.ssr.residence-place-registration.days-to-disable-notification:30}")
    private int daysToDisableResidencePlaceRegistrationNotification;

    private final EgrnFlatRequestDocumentService egrnFlatRequestDocumentService;
    private final EgrnFlatRequestMapper egrnFlatRequestMapper;
    private final SsrFilestoreService ssrFilestoreService;
    private final BusRequestService busRequestService;
    private final EnoCreator enoCreator;
    private final XmlUtils xmlUtils;
    private final PersonDocumentService personDocumentService;
    private final ElkUserNotificationService elkUserNotificationService;

    public EgrnFlatRequestDocument fetchLast(
        final String unom, final String flatNumber, final String statusCode
    ) {
        return egrnFlatRequestDocumentService.fetchLast(unom, flatNumber, statusCode);
    }

    public void create(final List<CreateEgrnFlatRequestDto> createEgrnFlatRequestDtoList) {
        createEgrnFlatRequestDtoList.forEach(this::create);
    }

    public void create(final CreateEgrnFlatRequestDto createEgrnFlatRequestDto) {
        final String serviceNumber = enoCreator.generateAsurEnoNumber(SSR_ASUR_CODE, GU_CODE);

        log.info(
            "Send egrn flat request; cadastralNumber {}, serviceNumber {}",
            createEgrnFlatRequestDto.getCadastralNumber(),
            serviceNumber
        );

        final EgrnFlatRequestDocument egrnFlatRequestDocument = egrnFlatRequestMapper
            .toEgrnFlatRequestDocument(createEgrnFlatRequestDto, serviceNumber);

        egrnFlatRequestDocumentService.createDocument(egrnFlatRequestDocument, false, null);

        final EgrnFlatRequestData egrnFlatRequestData = egrnFlatRequestDocument.getDocument().getEgrnFlatRequestData();

        try {
            final BusRequestDocument busRequestDocument = sendBusRequest(
                serviceNumber, createEgrnFlatRequestDto, egrnFlatRequestDocument
            );
            egrnFlatRequestData.setBusRequestDocumentId(busRequestDocument.getId());
            egrnFlatRequestData.setStatusCode(BUS_REQUEST_SENT_STATUS);
            egrnFlatRequestDocumentService.updateDocument(egrnFlatRequestDocument);
        } catch (Exception e) {
            egrnFlatRequestData.setStatusCode(BUS_REQUEST_ERROR_STATUS);
            egrnFlatRequestDocumentService.updateDocument(egrnFlatRequestDocument);
            throw e;
        }
    }

    private BusRequestDocument sendBusRequest(
        final String serviceNumber,
        final CreateEgrnFlatRequestDto createEgrnFlatRequestDto,
        final EgrnFlatRequestDocument egrnFlatRequestDocument
    ) {
        final CreateBusRequestDto createBusRequestDto = CreateBusRequestDto
            .builder()
            .busRequestType(BusRequestType.EGRN_FLAT)
            .serviceNumber(serviceNumber)
            .serviceTypeCode(GU_CODE)
            .ochdFolderGuid(egrnFlatRequestDocument.getFolderId())
            .customVariables(
                Collections.singletonMap(CADASTRAL_NUMBER_KEY, createEgrnFlatRequestDto.getCadastralNumber())
            )
            .build();

        return busRequestService.sendBusRequest(createBusRequestDto);
    }

    public EgrnFlatRequestDocument fillResponseData(
        final EgrnFlatRequestDocument egrnFlatRequestDocument, final SmevResponse response
    ) {
        final String folderId = egrnFlatRequestDocument.getFolderId();
        final String zipFileStoreId = ssrFilestoreService.retrieveFileStoreIdsFromSubfoldersByType(folderId, "zip")
            .stream()
            .findFirst()
            .orElse(null);

        final byte[] zipFileContents = ssrFilestoreService.getFile(zipFileStoreId);
        final List<Pair<String, String>> filesFromZip = ssrFilestoreService
            .retrieveFilesFromZip(zipFileContents, folderId);

        final String xmlFileStoreId = retrieveFileStoreIdByType(filesFromZip, "xml");
        final String pdfFileStoreId = retrieveFileStoreIdByType(filesFromZip, "pdf");

        final ExtractAboutPropertyRoom extractAboutPropertyRoom = parseEgrnFlatXmlMessage(xmlFileStoreId);

        final EgrnFlatRequestData egrnFlatRequestData = egrnFlatRequestDocument.getDocument().getEgrnFlatRequestData();

        egrnFlatRequestData.setStatusCode(response.getStatusCode());
        egrnFlatRequestData.setErrorDescription(null);

        addFlatNumberToLivingApartment(extractAboutPropertyRoom, egrnFlatRequestData.getRequestCriteria());

        final FlatEgrnResponse egrnResponse = ofNullable(egrnFlatRequestData.getEgrnResponse())
            .orElseGet(FlatEgrnResponse::new);
        egrnResponse.setPdfFileStoreId(pdfFileStoreId);
        egrnResponse.setXmlFileStoreId(xmlFileStoreId);
        egrnResponse.setZipFileStoreId(zipFileStoreId);
        egrnResponse.setResponseDateTime(LocalDateTime.now());
        egrnResponse.setExtractAboutPropertyRoom(extractAboutPropertyRoom);

        egrnFlatRequestData.setEgrnResponse(egrnResponse);
        return egrnFlatRequestDocument;
    }

    private void addFlatNumberToLivingApartment(
        final ExtractAboutPropertyRoom extractAboutPropertyRoom,
        final FlatRequestCriteria requestCriteria
    ) {
        final RoomRecordAboutProperty roomRecordAboutProperty = ofNullable(extractAboutPropertyRoom)
            .map(ExtractAboutPropertyRoom::getRoomRecord)
            .orElse(null);
        if (hasLivingPurpose(roomRecordAboutProperty)) {
            final String flatNumber = retrieveFlatNumber(roomRecordAboutProperty);
            requestCriteria.setFlatNumber(flatNumber);
        }
    }

    public boolean hasLivingPurpose(final RoomRecordAboutProperty roomRecordAboutProperty) {
        return ofNullable(roomRecordAboutProperty)
            .map(RoomRecordAboutProperty::getParams)
            .map(ParamsRoomBase::getPurpose)
            .map(Dict::getCode)
            .filter(LIVING_PURPOSE_CODE::equals)
            .isPresent();
    }

    private String retrieveFlatNumber(final RoomRecordAboutProperty roomRecordAboutProperty) {
        return ofNullable(roomRecordAboutProperty)
            .map(RoomRecordAboutProperty::getAddressRoom)
            .map(AddressLocationRoom::getAddress)
            .map(AddressOksLocation::getAddress)
            .map(AddressMain::getAddressFias)
            .map(Address::getDetailedLevel)
            .map(DetailedLevel::getApartment)
            .map(Apartment::getNameApartment)
            .orElse(null);
    }

    private ExtractAboutPropertyRoom parseEgrnFlatXmlMessage(final String xmlFileStoreId) {
        return ofNullable(xmlFileStoreId)
            .map(ssrFilestoreService::getFile)
            .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
            .map(contents -> contents.replace(
                "<extract_about_property_room>",
                "<extract_about_property_room xmlns=\"http://www.ugd.croc.ru/ssr/egrn\">"))
            .flatMap(xmlMessage -> xmlUtils
                .<JAXBElement<ExtractAboutPropertyRoom>>parseXml(xmlMessage, new Class[]{ObjectFactory.class}))
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

    public void sendResidencePlaceRegistrationNotificationIfNeeded(
        final EgrnFlatRequestDocument egrnFlatRequestDocument
    ) {
        if (residencePlaceRegistrationEnabled) {
            final EgrnFlatRequestData egrnFlatRequestData = egrnFlatRequestDocument.getDocument()
                .getEgrnFlatRequestData();
            final String unom = ofNullable(egrnFlatRequestData)
                .map(EgrnFlatRequestData::getRequestCriteria)
                .map(FlatRequestCriteria::getUnom)
                .orElse(null);
            final String flatNumber = ofNullable(egrnFlatRequestData)
                .map(EgrnFlatRequestData::getRequestCriteria)
                .map(FlatRequestCriteria::getFlatNumber)
                .orElse(null);
            if (StringUtils.isNotBlank(unom) && StringUtils.isNotBlank(flatNumber)) {
                of(egrnFlatRequestData)
                    .map(EgrnFlatRequestData::getEgrnResponse)
                    .map(FlatEgrnResponse::getExtractAboutPropertyRoom)
                    .map(ExtractAboutPropertyRoom::getRightRecords)
                    .map(RightRecordsAboutProperty::getRightRecord)
                    .map(Collection::stream)
                    .orElse(Stream.empty())
                    .map(RightRecord::getRightHolders)
                    .map(RightHoldersOut::getRightHolder)
                    .flatMap(List::stream)
                    .map(RightHolderOut::getIndividual)
                    .filter(individual -> nonNull(individual) && StringUtils.isNotBlank(individual.getSnils()))
                    .forEach(individual -> sendResidencePlaceRegistrationNotificationIfNeeded(
                        individual, unom, flatNumber
                    ));
            }
        }
    }

    private void sendResidencePlaceRegistrationNotificationIfNeeded(
        final IndividualOut individual, final String unom, final String flatNumber
    ) {
        personDocumentService.findPersonDocumentsWithUniqueId(individual.getSnils(), null)
            .stream()
            .filter(this::notExistResidencePlaceRegistrationNotification)
            .filter(personDocument -> existsNewFlatWithSignedContract(personDocument, unom, flatNumber))
            .forEach(elkUserNotificationService::sendResidencePlaceRegistrationNotification);
    }

    private boolean notExistResidencePlaceRegistrationNotification(final PersonDocument personDocument) {
        return of(personDocument.getDocument())
            .map(Person::getPersonData)
            .map(PersonType::getSendedMessages)
            .map(PersonType.SendedMessages::getMessage)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(MessageType::getBusinessType)
            .noneMatch(businessType -> Objects.equals(
                ResidencePlaceRegistrationNotificationsDescriptor.OPTION_AVAILABLE.getNotificationTitle(),
                businessType
            ));
    }

    private boolean existsNewFlatWithSignedContract(
        final PersonDocument personDocument, final String unom, final String flatNumber
    ) {
        return PersonUtils.getNewFlatByUnomAndFlatNumber(personDocument, unom, flatNumber)
            .flatMap(newFlat -> personDocumentService
                .getContractSignDate(personDocument.getDocument().getPersonData(), newFlat))
            .filter(contractSignDate -> contractSignDate
                .isAfter(LocalDate.now().minusDays(daysToDisableResidencePlaceRegistrationNotification)))
            .isPresent();
    }

}
