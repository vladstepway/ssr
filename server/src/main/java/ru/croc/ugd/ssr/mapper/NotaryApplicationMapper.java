package ru.croc.ugd.ssr.mapper;

import static java.util.Objects.isNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.PersonType.Agreements.Agreement;
import ru.croc.ugd.ssr.PersonType.OfferLetters.OfferLetter;
import ru.croc.ugd.ssr.dto.PersonDto;
import ru.croc.ugd.ssr.dto.ResettlementInfoDto;
import ru.croc.ugd.ssr.dto.commissioninspection.RestApplicantDto;
import ru.croc.ugd.ssr.dto.notary.NotaryApplicationFlowStatus;
import ru.croc.ugd.ssr.dto.notary.RestNotaryApplicationApartmentDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryApplicationCreateUpdateRequestDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryApplicationFileDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryApplicationHistoryEventDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryApplicationResponseDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryInfoDto;
import ru.croc.ugd.ssr.enums.NotaryApplicationSource;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryApplicationDto;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.notary.NotaryApplicationDocument;
import ru.croc.ugd.ssr.model.notary.NotaryDocument;
import ru.croc.ugd.ssr.notary.Apartment;
import ru.croc.ugd.ssr.notary.NotaryApplicantFile;
import ru.croc.ugd.ssr.notary.NotaryApplication;
import ru.croc.ugd.ssr.notary.NotaryApplicationApplicant;
import ru.croc.ugd.ssr.notary.NotaryApplicationHistoryEvent;
import ru.croc.ugd.ssr.notary.NotaryApplicationType;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface NotaryApplicationMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "document.notaryApplicationData.eno", source = "eno")
    @Mapping(target = "document.notaryApplicationData.applicant", source = "personDocument")
    @Mapping(target = "document.notaryApplicationData.applicant.email", source = "dto.applicant.email")
    @Mapping(target = "document.notaryApplicationData.applicant.phone", source = "dto.applicant.phone")
    @Mapping(
        target = "document.notaryApplicationData.applicant.additionalPhone",
        source = "dto.applicant.additionalPhone"
    )
    @Mapping(target = "document.notaryApplicationData.affairId", source = "dto.affairId")
    @Mapping(target = "document.notaryApplicationData.apartmentFrom", source = "dto.apartmentFrom")
    @Mapping(target = "document.notaryApplicationData.apartmentTo.apartment", source = "dto.apartmentTo")
    @Mapping(target = "document.notaryApplicationData.notaryId", source = "dto.notaryId")
    @Mapping(target = "document.notaryApplicationData.applicantComment", source = "dto.applicantComment")
    @Mapping(target = "document.notaryApplicationData.source",
        expression = "java( ru.croc.ugd.ssr.enums.NotaryApplicationSource.SSR.value() )")
    NotaryApplicationDocument toNotaryApplicationDocument(
        final PersonDocument personDocument,
        final RestNotaryApplicationCreateUpdateRequestDto dto,
        final String eno
    );

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "document.notaryApplicationData.affairId", source = "dto.affairId")
    @Mapping(target = "document.notaryApplicationData.apartmentFrom", source = "dto.apartmentFrom")
    @Mapping(target = "document.notaryApplicationData.apartmentTo.apartment", source = "dto.apartmentTo")
    @Mapping(target = "document.notaryApplicationData.statusId",
        expression = "java( ru.croc.ugd.ssr.dto.notary.NotaryApplicationFlowStatus.ACCEPTED.getId() )")
    @Mapping(target = "document.notaryApplicationData.notaryId", source = "dto.notaryId")
    @Mapping(target = "document.notaryApplicationData.applicantComment", source = "dto.applicantComment")
    @Mapping(target = "document.notaryApplicationData.source",
        expression = "java( ru.croc.ugd.ssr.enums.NotaryApplicationSource.SSR.value() )")
    NotaryApplicationDocument updateNotaryApplicationDocument(
        final @MappingTarget NotaryApplicationDocument notaryApplicationDocument,
        final RestNotaryApplicationCreateUpdateRequestDto dto
    );

    @AfterMapping
    default NotaryApplicationDocument doAfterMapping(
        final @MappingTarget NotaryApplicationDocument notaryApplicationDocument
    ) {
        populateApplicationDateTime(notaryApplicationDocument);
        fillApplicantName(notaryApplicationDocument);

        return notaryApplicationDocument;
    }

    default NotaryApplicationDocument populateApplicationDateTime(
        final @MappingTarget NotaryApplicationDocument notaryApplicationDocument
    ) {
        Optional.of(notaryApplicationDocument.getDocument())
            .map(NotaryApplication::getNotaryApplicationData)
            .ifPresent(notaryApplication -> {
                if (isNull(notaryApplication.getApplicationDateTime())) {
                    notaryApplication.setApplicationDateTime(LocalDateTime.now());
                }
            });

        return notaryApplicationDocument;
    }

    default NotaryApplicationDocument fillApplicantName(
        final @MappingTarget NotaryApplicationDocument notaryApplicationDocument
    ) {
        Optional.of(notaryApplicationDocument.getDocument())
            .map(NotaryApplication::getNotaryApplicationData)
            .map(NotaryApplicationType::getApplicant)
            .ifPresent(applicant -> {
                if (Objects.nonNull(applicant.getFirstName())
                    && Objects.nonNull(applicant.getLastName())
                    && Objects.nonNull(applicant.getMiddleName())
                    && Objects.nonNull(applicant.getFullName())
                ) {
                    return;
                }
                if (Objects.nonNull(applicant.getFullName())) {
                    final String[] fullNameArray = applicant.getFullName().trim().split(" ");
                    if (fullNameArray.length >= 3) {
                        of(fullNameArray[0]).ifPresent(applicant::setLastName);
                        of(fullNameArray[1]).ifPresent(applicant::setFirstName);
                        of(fullNameArray[2]).ifPresent(applicant::setMiddleName);
                    }
                }
                if (Objects.nonNull(applicant.getFirstName())
                    || Objects.nonNull(applicant.getLastName())
                    || Objects.nonNull(applicant.getMiddleName())
                ) {
                    final String fullName = Stream
                        .of(applicant.getLastName(), applicant.getFirstName(), applicant.getMiddleName())
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(" "));
                    applicant.setFullName(fullName);
                }
            });

        return notaryApplicationDocument;
    }

    @Mapping(target = "id", source = "applicationDocument.id")
    @Mapping(target = "eno", source = "applicationDocument.document.notaryApplicationData.eno")
    @Mapping(target = "affairId", source = "applicationDocument.document.notaryApplicationData.affairId")
    @Mapping(
        target = "applicationDate",
        source = "applicationDocument.document.notaryApplicationData.applicationDateTime"
    )
    @Mapping(target = "applicant", source = "applicationDocument.document.notaryApplicationData.applicant")
    @Mapping(
        target = "apartmentFrom",
        source = "applicationDocument.document.notaryApplicationData.apartmentFrom"
    )
    @Mapping(
        target = "apartmentTo",
        source = "applicationDocument.document.notaryApplicationData.apartmentTo.apartment"
    )
    @Mapping(target = "notary", source = "notary")
    @Mapping(
        target = "applicantComment",
        source = "applicationDocument.document.notaryApplicationData.applicantComment"
    )
    @Mapping(
        target = "appointmentDateTime",
        source = "applicationDocument.document.notaryApplicationData.appointmentDateTime"
    )
    @Mapping(target = "otherLivers", source = "otherLivers")
    @Mapping(target = "statusId", source = "applicationDocument.document.notaryApplicationData.statusId")
    @Mapping(
        target = "status",
        source = "applicationDocument.document.notaryApplicationData.statusId",
        qualifiedByName = "toNotaryApplicationStatusValue"
    )
    @Mapping(
        target = "source",
        source = "applicationDocument.document.notaryApplicationData.source",
        qualifiedByName = "toNotarySourceName"
    )
    @Mapping(
        target = "processInstanceId",
        source = "applicationDocument.document.notaryApplicationData.processInstanceId"
    )
    @Mapping(target = "files", source = "applicationDocument.document.notaryApplicationData.applicantFiles.files")
    @Mapping(target = "resettlementInfo", source = "personDocument", qualifiedByName = "toResettlementInfo")
    @Mapping(target = "history", source = "applicationDocument.document.notaryApplicationData.history.history")
    RestNotaryApplicationResponseDto toRestNotaryApplicationResponseDto(
        final NotaryApplicationDocument applicationDocument,
        final List<PersonDocument> otherLivers,
        final RestNotaryInfoDto notary,
        final PersonDocument personDocument
    );

    @Named("toNotarySourceName")
    default String toNotarySourceName(final String sourceValue) {
        return NotaryApplicationSource.fromValue(sourceValue).name();
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "document.personData.FIO")
    @Mapping(target = "phone", source = "document.personData.phones", qualifiedByName = "toPhone")
    @Mapping(target = "birthDate", source = "document.personData.birthDate")
    @Mapping(
        target = "statusLiving",
        source = "document.personData.statusLiving",
        qualifiedByName = "toStatusLiving"
    )
    @Mapping(target = "birthLocation", source = "document.personData.birthLocation")
    @Mapping(target = "snils", source = "document.personData.SNILS")
    @Mapping(target = "passport", source = "document.personData.passport")
    @Mapping(
        target = "isDead",
        source = "document.personData.addInfo.isDead",
        qualifiedByName = "toValueFromCode"
    )
    @Mapping(
        target = "isDisable",
        source = "document.personData.addInfo.isDisable",
        qualifiedByName = "toValueFromCode"
    )
    @Mapping(
        target = "isNonCapable",
        source = "document.personData.addInfo.isNonCapable",
        qualifiedByName = "toValueFromCode"
    )
    @Mapping(
        target = "isPreferential",
        source = "document.personData.addInfo.isPreferential",
        qualifiedByName = "toValueFromCode"
    )
    PersonDto toPersonDto(final PersonDocument person);

    @Named("toStatusLiving")
    default String toStatusLiving(final String statusLiving) {
        return PersonUtils.getStatusLiving(statusLiving);
    }

    @Named("toValueFromCode")
    default String toValueFromCode(final String code) {
        return PersonUtils.getValueFromCode(code);
    }

    @Mapping(target = "eventDate", source = "event.eventDate")
    @Mapping(target = "comment", source = "event.comment")
    @Mapping(target = "eventId", source = "event.eventId")
    RestNotaryApplicationHistoryEventDto toRestNotaryApplicationHistoryEventDto(
        final NotaryApplicationHistoryEvent event
    );

    @Mapping(target = "name", source = "name")
    @Mapping(target = "fileStoreId", source = "fileStoreId")
    @Mapping(target = "creationDate", source = "creationDate")
    RestNotaryApplicationFileDto toRestNotaryApplicationFileDto(final NotaryApplicantFile file);

    @Mapping(target = "fullName", source = "applicant.fullName")
    @Mapping(target = "id", source = "applicant.personId")
    @Mapping(target = "email", source = "applicant.email")
    @Mapping(target = "phone", source = "applicant.phone")
    @Mapping(target = "additionalPhone", source = "applicant.additionalPhone")
    RestApplicantDto toRestApplicantDto(final NotaryApplicationApplicant applicant);

    @Mapping(target = "personId", source = "personDocument.id")
    @Mapping(target = "snils", source = "personDocument.document.personData.SNILS")
    @Mapping(target = "ssoId", source = "personDocument.document.personData.ssoID")
    @Mapping(target = "fullName", source = "personDocument.document.personData.FIO")
    @Mapping(target = "lastName", source = "personDocument.document.personData.lastName")
    @Mapping(target = "firstName", source = "personDocument.document.personData.firstName")
    @Mapping(target = "middleName", source = "personDocument.document.personData.middleName")
    @Mapping(target = "phone", source = "personDocument.document.personData.phones", qualifiedByName = "toPhone")
    @Mapping(target = "additionalPhone", ignore = true)
    @Mapping(target = "email", ignore = true)
    NotaryApplicationApplicant toNotaryApplicationApplicant(final PersonDocument personDocument);

    @Mapping(target = "address", source = "address")
    @Mapping(target = "unom", source = "unom")
    @Mapping(target = "flatNumber", source = "flatNumber")
    @Mapping(target = "roomNum", source = "roomNum")
    RestNotaryApplicationApartmentDto toRestNotaryApplicationApartmentDto(
        final Apartment apartment);

    @Mapping(target = "address", source = "address")
    @Mapping(target = "unom", source = "unom")
    @Mapping(target = "flatNumber", source = "flatNumber")
    @Mapping(target = "roomNum", source = "roomNum")
    Apartment toApartment(
        final RestNotaryApplicationApartmentDto apartment);

    @Named("toNotaryApplicationStatusValue")
    default String toNotaryApplicationStatusValue(final String notaryStatusId) {
        return ofNullable(notaryStatusId)
            .map(NotaryApplicationFlowStatus::of)
            .map(NotaryApplicationFlowStatus::getSsrStatus)
            .orElse(null);
    }

    @Mapping(target = "notary.id", source = "notary.document.documentID")
    @Mapping(target = "notary.fio", source = "notary.document.notaryData.fullName")
    @Mapping(target = "notary.address", source = "notary.document.notaryData.address.address")
    @Mapping(target = "notary.phone", source = "notary.document.notaryData.phones.phone", qualifiedByName = "toString")
    @Mapping(target = "notary.additionalData", source = "notary.document.notaryData.address.additionalInformation")
    @Mapping(target = "bookingId", source = "notaryApplicationDocument.document.notaryApplicationData.bookingId")
    @Mapping(
        target = "appointmentDateTime",
        source = "notaryApplicationDocument.document.notaryApplicationData.appointmentDateTime"
    )
    RestNotaryApplicationDto toExternalRestNotaryApplicationResponseDto(
        final NotaryApplicationDocument notaryApplicationDocument, final NotaryDocument notary
    );

    @Named("toString")
    default String toString(final List<String> list) {
        return String.join(", ", list);
    }

    @Named("toResettlementInfo")
    default ResettlementInfoDto toResettlementInfo(final PersonDocument personDocument) {
        final Agreement lastAcceptedAgreement = PersonUtils
            .getLastAcceptedAgreement(personDocument)
            .orElse(null);
        final OfferLetter lastAcceptedAgreementOfferLetter = PersonUtils
            .getLastAcceptedAgreementOfferLetter(personDocument)
            .orElse(null);

        return toResettlementInfoDto(lastAcceptedAgreement, lastAcceptedAgreementOfferLetter);
    }

    @Mapping(target = "creationDate", source = "letter.date")
    @Mapping(target = "letterId", source = "letter.letterId")
    @Mapping(target = "letterFileId", source = "letter", qualifiedByName = "toLetterFileId")
    @Mapping(target = "acceptedDate", source = "agreement.date")
    @Mapping(target = "acceptedFileId", source = "agreement.fileLink")
    @Mapping(
        target = "administrativeDocumentFileId",
        source = "letter",
        qualifiedByName = "toAdministrativeDocumentFileId"
    )
    @Mapping(
        target = "administrativeDocumentFileDate",
        source = "letter",
        qualifiedByName = "toAdministrativeDocumentFileDate"
    )
    ResettlementInfoDto toResettlementInfoDto(final Agreement agreement, final OfferLetter letter);

    @Named("toLetterFileId")
    default String toLetterFileId(final OfferLetter letter) {
        return PersonUtils.extractOfferLetterFileByType(letter, "1")
            .map(OfferLetter.Files.File::getFileLink)
            .orElse(null);
    }

    @Named("toAdministrativeDocumentFileId")
    default String toAdministrativeDocumentFileId(final OfferLetter letter) {
        return PersonUtils.extractOfferLetterFileByType(letter, "3")
            .map(OfferLetter.Files.File::getFileLink)
            .orElse(null);
    }

    @Named("toAdministrativeDocumentFileDate")
    default LocalDate toAdministrativeDocumentFileDate(final OfferLetter letter) {
        return PersonUtils.extractOfferLetterFileByType(letter, "3")
            .map(OfferLetter.Files.File::getCreationDateTime)
            .map(LocalDateTime::toLocalDate)
            .orElse(null);
    }

    @Named("toPhone")
    default String toPhone(final PersonType.Phones phones) {
        return PersonUtils.getBasePhoneNumber(phones);
    }

}
