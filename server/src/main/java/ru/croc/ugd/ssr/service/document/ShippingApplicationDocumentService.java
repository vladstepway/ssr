package ru.croc.ugd.ssr.service.document;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.RECALL_AVAILABLE;
import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.RECORD_ADDED;
import static ru.croc.ugd.ssr.enums.ShippingApplicationSource.SSR;
import static ru.croc.ugd.ssr.utils.ShippingApplicationUtils.toShippingDateTimeInfo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.db.dao.ShippingApplicationDao;
import ru.croc.ugd.ssr.db.dao.ShippingDayScheduleDao;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.service.notification.ShippingElkNotificationService;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.ShippingApplicationDocument;
import ru.croc.ugd.ssr.service.BookingSlotService;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.shipping.Apartment;
import ru.croc.ugd.ssr.shipping.ShippingApplicant;
import ru.croc.ugd.ssr.shipping.ShippingApplication;
import ru.croc.ugd.ssr.shipping.ShippingApplicationType;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.PersonDocumentUtils;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * ShippingApplicationDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ShippingApplicationDocumentService extends AbstractDocumentService<ShippingApplicationDocument> {

    private final ShippingApplicationDao shippingApplicationDao;
    private final EnoCreator enoCreator;
    private final ShippingElkNotificationService shippingElkNotificationService;
    private final PersonDocumentService personDocumentService;
    private final PersonDocumentUtils personDocumentUtils;
    private final FlatService flatService;
    private final ShippingDayScheduleDao shippingDayScheduleDao;
    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final CipService cipService;
    private final BookingSlotService bookingSlotService;

    /**
     * Before create.
     *
     * @param document result
     */
    @Override
    public void beforeCreate(@NotNull ShippingApplicationDocument document) {
        ofNullable(document.getDocument())
            .map(ShippingApplication::getShippingApplicationData)
            .ifPresent(shippingApplication -> {
                if (StringUtils.isEmpty(shippingApplication.getBookingId())
                    || !bookingSlotService.checkExistsBooking(shippingApplication.getBookingId())) {
                    throw new BookingNotExists();
                }
                final String eno = ofNullable(shippingApplication.getEno())
                    .orElseGet(() -> enoCreator.generateEtpMvNotificationEnoNumber("067801"));
                shippingApplication.setEno(eno);
                if (shippingApplication.getSource() == null) {
                    shippingApplication.setSource(SSR.value());
                }
                shippingApplication.setCreationDate(LocalDateTime.now());
                shippingApplication.setStatus(RECORD_ADDED.getDescription());
                shippingApplication.setShippingDateTimeInfo(toShippingDateTimeInfo(shippingApplication));
                shippingApplication.setBrigade(retrieveBrigade(shippingApplication.getBookingId()));
                shippingApplication.setDistrict(retrieveDistrict(shippingApplication.getBookingId()));

                populatePersonUidIfEmpty(shippingApplication);

                final String cipId = retrieveCipId(shippingApplication.getApplicant())
                    .orElseGet(() -> retrieveCipId(shippingApplication.getApartmentTo())
                        .orElse(null)
                    );
                if (nonNull(cipId)) {
                    shippingApplication.setCipId(cipId);
                }
            });

    }

    private Optional<String> retrieveCipId(final ShippingApplicant applicant) {
        return ofNullable(applicant)
            .map(ShippingApplicant::getPersonUid)
            .flatMap(personDocumentService::getCipId);
    }

    private Optional<String> retrieveCipId(final Apartment apartment) {
        return ofNullable(apartment)
            .map(Apartment::getUnom)
            .map(capitalConstructionObjectService::getCcoIdByUnom)
            .map(cipService::fetchByCcoId)
            .map(Collection::stream)
            .orElseGet(Stream::empty)
            .map(CipDocument::getDocument)
            .map(Cip::getDocumentID)
            .findFirst();
    }

    private String retrieveDistrict(final String bookingId) {
        return shippingDayScheduleDao
            .findDistrictByPreBookingId(bookingId)
            .stream()
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    private String retrieveBrigade(final String bookingId) {
        return shippingDayScheduleDao
            .findBrigadeByPreBookingId(bookingId)
            .stream()
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    private void populatePersonUidIfEmpty(final ShippingApplicationType shippingApplicationType) {
        if (shippingApplicationType.getApplicant() == null
            || shippingApplicationType.getApplicant().getPersonUid() != null) {
            return;
        }
        final String snils = shippingApplicationType.getApplicant().getSnils();
        final String ssoId = shippingApplicationType.getApplicant().getSsoId();
        final Apartment apartmentFrom = shippingApplicationType.getApartmentFrom();
        final List<PersonDocument> personDocumentDataList = personDocumentService
            .findPersonDocumentsWithUniqueId(snils, ssoId);
        if (personDocumentDataList.isEmpty()) {
            return;
        }
        if (personDocumentDataList.size() == 1 && personDocumentDataList.get(0) != null) {
            shippingApplicationType.getApplicant().setPersonUid(personDocumentDataList.get(0).getId());
        }

        if (apartmentFrom == null) {
            return;
        }

        final String addressFrom = apartmentFrom.getStringAddress();
        final BigInteger flatFrom = apartmentFrom.getRoomNumber();
        personDocumentDataList
            .forEach(personDocument -> {
                final PersonType personType = personDocumentUtils.getPersonDataFromDocument(personDocument);

                final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto
                    = flatService.fetchRealEstateAndFlat(personType.getFlatID());

                ofNullable(realEstateDataAndFlatInfoDto)
                    .map(RealEstateDataAndFlatInfoDto::getFlat)
                    .ifPresent(flatOld -> {
                        final String realEstateAddress = RealEstateUtils
                            .getRealEstateAddress(realEstateDataAndFlatInfoDto);
                        final String apartmentFromAddress = cleanFlatFromAddress(addressFrom);

                        if (!StringUtils.isEmpty(flatOld.getAddress())
                            && StringUtils.equals(flatOld.getAddress(), apartmentFromAddress)
                            || !StringUtils.isEmpty(realEstateAddress)
                            && StringUtils.equals(realEstateAddress, apartmentFromAddress)) {

                            if (flatFrom == null) {
                                shippingApplicationType.getApplicant().setPersonUid(personDocument.getId());
                            }
                            if (flatFrom != null && (StringUtils.equals(flatOld.getApartmentL4VALUE(),
                                flatFrom.toString())
                                || StringUtils.equals(flatOld.getFlatNumber(), flatFrom.toString()))) {
                                shippingApplicationType.getApplicant().setPersonUid(personDocument.getId());
                            }
                        }
                    });
            });
    }

    private String cleanFlatFromAddress(final String addressLine) {
        if (StringUtils.isEmpty(addressLine)) {
            return addressLine;
        }
        return addressLine.replaceAll(", кв.+", "");
    }

    @Override
    public void afterCreate(@NotNull ShippingApplicationDocument document) {
        shippingElkNotificationService.sendStatus(RECORD_ADDED, document);
        shippingElkNotificationService.sendStatus(RECALL_AVAILABLE, document);
    }

    /**
     * Найти шипинг по personUid.
     *
     * @param personUid personUid
     * @return список документов
     */
    public List<ShippingApplicationType> findShippingApplicationByPersonUid(
        final String personUid) {
        return shippingApplicationDao.findApplicationsByPersonUid(personUid)
            .stream()
            .map(this::parseDocumentData)
            .map(ShippingApplicationDocument::getDocument)
            .map(ShippingApplication::getShippingApplicationData)
            .collect(Collectors.toList());
    }

    /**
     * Найти шипинг по personUid и UNOM.
     *
     * @param personUid personUid
     * @param unomTo    UNOM заселяемого дома
     * @param unomFrom  UNOM отселяемого дома
     * @return список документов
     */
    public ShippingApplicationDocument findShippingApplicationByPersonUidAndUnoms(
        final String personUid,
        final String unomTo,
        final String unomFrom
    ) {
        return shippingApplicationDao.findShippingApplicationByPersonUidAndUnom(personUid, unomTo, unomFrom)
            .map(this::parseDocumentData)
            .orElse(null);
    }

    private ShippingApplicationDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private ShippingApplicationDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, ShippingApplicationDocument.class);
    }

    @Nonnull
    @Override
    public DocumentType<ShippingApplicationDocument> getDocumentType() {
        return SsrDocumentTypes.SHIPPING_APPLICATION;
    }
}
