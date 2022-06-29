package ru.croc.ugd.ssr.service.flatappointment;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus;
import ru.croc.ugd.ssr.dto.flatappointment.RestCancelFlatAppointmentDto;
import ru.croc.ugd.ssr.dto.flatappointment.RestFlatAppointmentApplicantDto;
import ru.croc.ugd.ssr.dto.flatappointment.RestFlatAppointmentDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.flatappointment.Applicant;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.integration.service.notification.FlatAppointmentElkNotificationService;
import ru.croc.ugd.ssr.mapper.FlatAppointmentMapper;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.FlatAppointmentDocumentService;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DefaultRestFlatAppointmentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultRestFlatAppointmentService implements RestFlatAppointmentService {

    private final CipService cipService;
    private final FlatAppointmentDocumentService flatAppointmentDocumentService;
    private final FlatAppointmentMapper flatAppointmentMapper;
    private final PersonDocumentService personDocumentService;
    private final FlatService flatService;
    private final FlatAppointmentService flatAppointmentService;
    private final FlatAppointmentElkNotificationService flatAppointmentElkNotificationService;
    private final SsrFilestoreService ssrFilestoreService;

    @Override
    public RestFlatAppointmentDto fetchById(final String id) {
        final FlatAppointmentDocument flatAppointmentDocument = flatAppointmentDocumentService.fetchDocument(id);

        final CipDocument cipDocument = retrieveCipDocument(flatAppointmentDocument);
        final RestFlatAppointmentApplicantDto applicantDto = toApplicantDto(flatAppointmentDocument);

        return flatAppointmentMapper.toRestFlatAppointmentDto(flatAppointmentDocument, cipDocument, applicantDto);
    }

    @Override
    public List<RestFlatAppointmentDto> fetchAll(final String personDocumentId, final boolean includeInactive) {
        final List<FlatAppointmentDocument> flatAppointmentDocuments = flatAppointmentDocumentService
            .findAll(personDocumentId, includeInactive);

        final PersonDocument personDocument = personDocumentService.fetchById(personDocumentId)
            .orElse(null);

        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto = ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getFlatID)
            .map(flatService::fetchRealEstateAndFlat)
            .orElse(null);

        return flatAppointmentDocuments.stream()
            .map(flatAppointmentDocument ->
                toRestFlatAppointmentDto(flatAppointmentDocument, personDocument, realEstateDataAndFlatInfoDto)
            )
            .collect(Collectors.toList());
    }

    private RestFlatAppointmentDto toRestFlatAppointmentDto(
        final FlatAppointmentDocument flatAppointmentDocument,
        final PersonDocument personDocument,
        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto
    ) {
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        final CipDocument cipDocument = retrieveCipDocument(flatAppointmentDocument);
        final RestFlatAppointmentApplicantDto applicantDto = flatAppointmentMapper.toRestFlatAppointmentApplicantDto(
            flatAppointment, personDocument, realEstateDataAndFlatInfoDto
        );

        return flatAppointmentMapper
            .toRestFlatAppointmentDto(flatAppointmentDocument, cipDocument, applicantDto);
    }

    @Override
    public void cancelAppointmentByOperator(final String id, final RestCancelFlatAppointmentDto dto) {
        final FlatAppointmentDocument flatAppointmentDocument = flatAppointmentDocumentService.fetchDocument(id);

        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        if (!flatAppointmentService.canCancelAppointment(flatAppointment, false)) {
            throw new SsrException("Нельзя перенести запись менее чем за сутки до приема");
        }

        flatAppointment.setStatusId(FlatAppointmentFlowStatus.CANCELED_BY_OPERATOR.getId());
        flatAppointment.setCancelDate(dto.getCancelDate());
        flatAppointment.setCancelReason(dto.getCancelReason());

        flatAppointmentElkNotificationService
            .sendStatus(FlatAppointmentFlowStatus.CANCELED_BY_OPERATOR, flatAppointmentDocument);

        flatAppointmentService.cancelBookingIfRequired(flatAppointmentDocument);

        ofNullable(flatAppointment.getProcessInstanceId())
            .ifPresent(flatAppointmentService::finishBpmProcess);

        flatAppointmentDocumentService
            .updateDocument(flatAppointmentDocument.getId(), flatAppointmentDocument, true, true, null);
    }

    @Override
    public void closeCancellation(final String id) {
        final FlatAppointmentDocument flatAppointmentDocument = flatAppointmentDocumentService.fetchDocument(id);

        flatAppointmentElkNotificationService.sendStatus(
            FlatAppointmentFlowStatus.UNABLE_TO_CANCEL, flatAppointmentDocument
        );
    }

    @Override
    public byte[] getIcsFileContent(final String id) {
        final FlatAppointmentDocument flatAppointmentDocument = flatAppointmentDocumentService.fetchDocument(id);
        return retrieveIcsFileStoreId(flatAppointmentDocument);
    }

    private byte[] retrieveIcsFileStoreId(final FlatAppointmentDocument flatAppointmentDocument) {
        return of(flatAppointmentDocument.getDocument().getFlatAppointmentData())
            .map(FlatAppointmentData::getIcsFileStoreId)
            .map(ssrFilestoreService::getFile)
            .orElse(null);
    }

    private CipDocument retrieveCipDocument(final FlatAppointmentDocument flatAppointmentDocument) {
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();
        return ofNullable(flatAppointment.getCipId())
            .flatMap(cipService::fetchById)
            .orElse(null);
    }

    private RestFlatAppointmentApplicantDto toApplicantDto(
        final FlatAppointmentDocument flatAppointmentDocument
    ) {
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        final PersonDocument personDocument = ofNullable(flatAppointment.getApplicant())
            .map(Applicant::getPersonDocumentId)
            .flatMap(personDocumentService::fetchById)
            .orElse(null);

        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto = ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getFlatID)
            .map(flatService::fetchRealEstateAndFlat)
            .orElse(null);

        return flatAppointmentMapper.toRestFlatAppointmentApplicantDto(
            flatAppointment,
            personDocument,
            realEstateDataAndFlatInfoDto
        );
    }

    @Async
    @Override
    public void actualizeOfferLetterFileLinks() {
        log.info("Start actualize offer letter file links.");
        flatAppointmentDocumentService.findAll()
            .forEach(flatAppointmentDocument -> {
                try {
                    actualizeOfferLetterFileLink(flatAppointmentDocument);
                } catch (Exception e) {
                    log.error(
                        "Unable to actualize offer letter file link for flatAppointmentId = {}: {}",
                        flatAppointmentDocument.getId(),
                        e.getMessage(),
                        e
                    );
                }
            });
        log.info("Finish actualize offer letter file links.");
    }

    private void actualizeOfferLetterFileLink(final FlatAppointmentDocument flatAppointmentDocument) {
        final FlatAppointmentData flatAppointmentData = flatAppointmentDocument.getDocument().getFlatAppointmentData();
        final String letterId = flatAppointmentData.getOfferLetterId();
        final String personDocumentId = of(flatAppointmentData)
            .map(FlatAppointmentData::getApplicant)
            .map(Applicant::getPersonDocumentId)
            .orElse(null);

        if (isNull(flatAppointmentData.getOfferLetterFileLink())
            && nonNull(letterId)
            && nonNull(personDocumentId)) {
            final String fileLink = personDocumentService.fetchById(personDocumentId)
                .flatMap(personDocument -> PersonUtils.getOfferLetter(personDocument, letterId))
                .flatMap(PersonUtils::getOfferLetterFileLink)
                .orElse(null);

            if (nonNull(fileLink)) {
                flatAppointmentData.setOfferLetterFileLink(fileLink);
                flatAppointmentDocumentService.updateDocument(flatAppointmentDocument, "actualizeOfferLetterFileLink");
                log.info(
                    "Finish actualize offer letter file link for flatAppointmentId = {}",
                    flatAppointmentDocument.getId()
                );
            }
        }
    }
}
