package ru.croc.ugd.ssr.solr.converter;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.RoomType;
import ru.croc.ugd.ssr.enums.DistrictCode;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.service.DocumentConverterService;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.solr.UgdSsrPerson;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.reinform.cdp.document.model.DocumentType;

import javax.annotation.Nonnull;

/**
 * Реализация конвертера для Жильца дома.
 */
@Slf4j
@Component
public class PersonDocumentConverter extends SsrDocumentConverter<PersonDocument, UgdSsrPerson> {

    private final DocumentConverterService service;
    private final PersonDocumentService personDocumentService;
    private final FlatService flatService;

    public PersonDocumentConverter(
        final DocumentConverterService service,
        @Lazy final PersonDocumentService personDocumentService,
        @Lazy final FlatService flatService
    ) {
        this.service = service;
        this.personDocumentService = personDocumentService;
        this.flatService = flatService;
    }

    @Override
    @Nonnull
    public DocumentType<PersonDocument> getDocumentType() {
        return SsrDocumentTypes.PERSON;
    }

    @Override
    @Nonnull
    public UgdSsrPerson convertDocument(@NotNull PersonDocument document) {
        UgdSsrPerson position = createDocument(getAnyAccessType(), document.getId());
        try {
            // UgdSsrPerson.class - класс сгенерированный из DocumentTypes

            // Получим удобный источник данных
            PersonType source = document.getDocument().getPersonData();
            if (isNull(source)) {
                return position;
            }

            // Переносим данные из документа в то, что отправим на индексацию в Solr
            // Разработчик, помни! Исходный документ - это всего лишь JSON - поэтому null-ом может
            // оказаться всё что угодно. Предохраняйтесь от NPE!
            final String fullName = PersonUtils.getFullName(source);
            if (isNotBlank(fullName)) {
                position.setUgdSsrPersonFio(fullName);
            } else {
                log.warn("Skip converting person with empty full name, personDocumentId = {}", document.getId());
                return null;
            }
            if (nonNull(source.getUNOM())) {
                position.setUgdSsrPersonUnom(source.getUNOM().longValue());

                final RealEstateDocument realEstateDocument =
                    getRealEstateDocument(source.getUNOM().toString());

                if (nonNull(realEstateDocument) && nonNull(realEstateDocument.getDocument().getRealEstateData())) {
                    RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();

                    position.setUgdSsrPersonRealEstateAddress(RealEstateUtils.getRealEstateAddress(realEstateData));

                    if (nonNull(realEstateData.getDISTRICT())) {
                        position.setUgdSsrPersonPrefect(realEstateData.getDISTRICT().getName());
                    }
                    if (nonNull(realEstateData.getMunOkrugP5())) {
                        position.setUgdSsrPersonDistrict(
                            DistrictCode.ofName(realEstateData.getMunOkrugP5().getName())
                                .map(DistrictCode::getCode)
                                .orElse(null)
                        );
                    }
                    if (nonNull(source.getFlatID()) && nonNull(realEstateData.getFlats())) {
                        final FlatType flat = realEstateData.getFlats().getFlat()
                            .stream()
                            .filter(flatType -> flatType.getFlatID().equals(source.getFlatID()))
                            .findFirst()
                            .orElseGet(() -> flatService.fetchFlat(source.getFlatID()));
                        if (nonNull(flat)) {
                            position.setUgdSsrPersonFlatNumber(flat.getApartmentL4VALUE());
                            position.setUgdSsrPersonRealEstateFlatAddress(
                                RealEstateUtils.getFlatAddress(realEstateData, flat)
                            );
                        }
                    }
                }
            }
            if (nonNull(source.getStatusLiving())) {
                position.setUgdSsrPersonResidenceReason(source.getStatusLiving());
            }
            if (nonNull(source.getBirthDate())) {
                position.setUgdSsrPersonBirthdate(source.getBirthDate());
            }
            if (nonNull(source.getPassport())) {
                position.setUgdSsrPersonPassport(source.getPassport());
            }
            if (nonNull(source.getSNILS())) {
                position.setUgdSsrPersonSnils(source.getSNILS());
            }
            if (nonNull(source.getPersonID())) {
                position.setUgdSsrPersonPersonId(source.getPersonID());
            }
            if (nonNull(source.getRoomID())) {
                final RoomType roomType = fetchRoomType(source.getRoomID());
                if (nonNull(roomType)) {
                    position.setUgdSsrPersonRoomNumber(roomType.getRoomL5VALUE());
                }
            }
            if (nonNull(source.getUpdatedFromELKdate())) {
                position.setUgdSsrPersonUpdatedFromElkDate(source.getUpdatedFromELKdate());
            }
            if (nonNull(source.getUpdatedFromELKstatus())) {
                String sourceUpdatedFromElkStatus = source.getUpdatedFromELKstatus();
                String updatedFromElkStatus = "Превышен суточный лимит запросов на документ".equals(
                    sourceUpdatedFromElkStatus
                ) ? "в процессе обогащения" : sourceUpdatedFromElkStatus;
                position.setUgdSsrPersonUpdatedFromElkStatus(updatedFromElkStatus);
            }
            if (isNull(source.getUpdatedFromDgiStatus())) {
                position.setUgdSsrPersonNotupdatedfromdgi(true);
            }
            if (nonNull(source.getUpdatedFromPFRdate())) {
                position.setUgdSsrPersonUpdatedFromPfrDate(source.getUpdatedFromPFRdate());
            }
            if (nonNull(source.getUpdatedFromPFRstatus())) {
                String sourceUpdatedFromPfrStatus = source.getUpdatedFromPFRstatus();
                String updatedFromPfrStatus;
                if (sourceUpdatedFromPfrStatus.equals("Превышен суточный лимит запросов на документ")
                    || sourceUpdatedFromPfrStatus.equals("в процессе обогащения")) {
                    updatedFromPfrStatus = "в процессе обогащения";
                } else if (sourceUpdatedFromPfrStatus.equals("Запрашиваемые сведения не найдены")
                    || sourceUpdatedFromPfrStatus.equals("Невалидная дата рождения (ранее 1801г.), "
                    + "запрос в ПФР не может быть отправлен")
                    || sourceUpdatedFromPfrStatus.equals("обогащено")) {
                    updatedFromPfrStatus = "обогащено";
                } else {
                    updatedFromPfrStatus = "ошибка обогащения";
                }
                position.setUgdSsrPersonUpdatedFromPfrStatus(updatedFromPfrStatus);
            }
            if (nonNull(source.getSsoID())) {
                position.setUgdSsrPersonSsoId(source.getSsoID());
            }
            if (!source.isIsArchive()) {
                position.setUgdSsrPersonIsnotarchived(true);
            }
            if (nonNull(source.getRelocationStatus())) {
                position.setUgdSsrPersonRelocationStatus(source.getRelocationStatus());
            }
            // Определим к какому ЦИП привязан житель по последнему письму с предложением
            if (
                nonNull(source.getOfferLetters())
                    && nonNull(source.getOfferLetters().getOfferLetter())
                    && !source.getOfferLetters().getOfferLetter().isEmpty()
            ) {
                position.setUgdSsrPersonInformationCenterCode(
                    source.getOfferLetters().getOfferLetter().get(
                        source.getOfferLetters().getOfferLetter().size() - 1
                    ).getIdCIP()
                );
            }
            if (nonNull(position.getUgdSsrPersonInformationCenterCode())) {
                position.setUgdSsrPersonInformationCenterAddress(
                    getCipAddressById(position.getUgdSsrPersonInformationCenterCode())
                );
            }
            // Определим адрес новой квартиры
            if (
                nonNull(source.getNewFlatInfo())
                    && nonNull(source.getNewFlatInfo().getNewFlat())
                    && !source.getNewFlatInfo().getNewFlat().isEmpty()
            ) {
                final String newFlatAddress = getPersonNewFlatAddress(source);
                if (StringUtils.isNotBlank(newFlatAddress)) {
                    position.setUgdSsrPersonSettleFlatAddress(newFlatAddress);
                }
            }
            position.setUgdSsrPersonIsnothavedoublessnils(!nonNull(source.getSNILSDoubles())
                || !nonNull(source.getSNILSDoubles().getSNILSDouble())
                || source.getSNILSDoubles().getSNILSDouble().isEmpty());

            return position;
        } catch (Exception e) {
            log.warn("Unable to convertDocument: {}", e.getMessage(), e);
            return position;
        }
    }

    private String getCipAddressById(final String informationCenterCode) {
        try {
            return service.getCipAddressById(informationCenterCode);
        } catch (Exception e) {
            log.warn("Unable to getCipAddressById: {}", e.getMessage(), e);
            return null;
        }
    }

    private String getPersonNewFlatAddress(final PersonType personType) {
        try {
            return personDocumentService.getPersonNewFlatAddress(personType);
        } catch (Exception e) {
            log.warn("Unable to getPersonNewFlatAddress: {}", e.getMessage(), e);
            return null;
        }
    }

    private RealEstateDocument getRealEstateDocument(final String unom) {
        try {
            return service.getRealEstateByUnom(unom);
        } catch (Exception e) {
            log.warn("Unable to getRealEstateDocument: {}", e.getMessage(), e);
            return null;
        }
    }

    private RoomType fetchRoomType(final String roomId) {
        try {
            return service.fetchRoom(roomId);
        } catch (Exception e) {
            log.warn("Unable to fetchRoomType: {}", e.getMessage(), e);
            return null;
        }
    }

}
