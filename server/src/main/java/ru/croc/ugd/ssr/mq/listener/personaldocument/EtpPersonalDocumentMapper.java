package ru.croc.ugd.ssr.mq.listener.personaldocument;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.db.projection.TenantProjection;
import ru.croc.ugd.ssr.model.integration.etpmv.BaseDeclarant;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateFile;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateFileReference;
import ru.croc.ugd.ssr.model.integration.etpmv.Note;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestContact;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign;
import ru.croc.ugd.ssr.model.integration.etpmv.ServiceDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentDocument;
import ru.croc.ugd.ssr.personaldocument.DocumentType;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentFile;
import ru.croc.ugd.ssr.personaldocument.TenantType;
import ru.mos.gu.service._088201.EtpTenant;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.xml.datatype.XMLGregorianCalendar;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface EtpPersonalDocumentMapper {

    @Mapping(target = "document.personalDocumentData.affairId", source = "affairId")
    @Mapping(
        target = "document.personalDocumentData.documents.document",
        source = "signService.documents.serviceDocument",
        qualifiedByName = "serviceDocumentsToDocuments"
    )
    @Mapping(
        target = "document.personalDocumentData.tenants.tenant",
        source = "signService",
        qualifiedByName = "signServiceToTenants"
    )
    PersonalDocumentDocument toPersonalDocumentDocument(
        final String affairId,
        final RequestServiceForSign signService,
        @Context final List<CoordinateFile> coordinateFiles,
        @Context final BiFunction<RequestServiceForSign, RequestContact, EtpTenant> retrieveEtpTenant,
        @Context final Function<String, TenantProjection> retrieveTenant,
        @Context final Function<String, String> retrieveTypeCode,
        @Context final String documentKindCodeForLink
    );

    @Mapping(target = "lastName", source = "requestContact.lastName")
    @Mapping(target = "firstName", source = "requestContact.firstName")
    @Mapping(target = "middleName", source = "requestContact.middleName")
    @Mapping(target = "birthDate", expression = "java( toBirthDate(requestContact, etpTenant, retrieveTenant) )")
    @Mapping(target = "personDocumentId", source = "etpTenant.personDocumentId")
    @Mapping(target = "status", source = "etpTenant.status")
    @Mapping(target = "isSuspicious", source = "etpTenant.isSuspicious")
    @Mapping(target = "suspicionReason", source = "etpTenant.suspicionReason")
    @Mapping(
        target = "documents.document",
        source = "requestContact.documents.serviceDocument",
        qualifiedByName = "serviceDocumentsToDocuments"
    )
    TenantType toTenant(
        final RequestContact requestContact,
        final EtpTenant etpTenant,
        @Context final List<CoordinateFile> coordinateFiles,
        @Context final Function<String, TenantProjection> retrieveTenant,
        @Context final Function<String, String> retrieveTypeCode,
        @Context final String documentKindCodeForLink
    );

    @Mapping(target = "typeCode", source = "serviceDocument.docKind.code", qualifiedByName = "kindCodeToTypeCode")
    @Mapping(target = "comment", source = "serviceDocument.docNotes.note", qualifiedByName = "toComment")
    @Mapping(
        target = "files.file", source = "serviceDocument.docFiles.coordinateFileReference", qualifiedByName = "toFiles"
    )
    DocumentType toDocument(
        final ServiceDocument serviceDocument,
        @Context final List<CoordinateFile> coordinateFiles,
        @Context final Function<String, String> retrieveTypeCode
    );

    @Mapping(target = "fileStoreId", source = "fileStoreId")
    @Mapping(target = "fileType", source = "fileType")
    @Mapping(target = "chedFileId", ignore = true)
    PersonalDocumentFile toFile(final String fileStoreId, final String fileType);

    @Mapping(target = "chedFileId", source = "id", qualifiedByName = "toChedFileId")
    @Mapping(target = "fileType", source = "id", qualifiedByName = "toFileType")
    @Mapping(target = "fileStoreId", ignore = true)
    PersonalDocumentFile toFile(
        final CoordinateFileReference coordinateFileReference, @Context final List<CoordinateFile> coordinateFiles
    );

    @Named("toFiles")
    List<PersonalDocumentFile> toFiles(
        final List<CoordinateFileReference> coordinateFileReferences,
        @Context final List<CoordinateFile> coordinateFiles
    );

    @Named("serviceDocumentsToDocuments")
    default List<DocumentType> serviceDocumentsToDocuments(
        final List<ServiceDocument> serviceDocuments,
        @Context final List<CoordinateFile> coordinateFiles,
        @Context final Function<String, String> retrieveTypeCode,
        @Context final String documentKindCodeForLink
    ) {
        return serviceDocuments.stream()
            .filter(serviceDocument -> nonNull(serviceDocument.getDocKind())
                && !documentKindCodeForLink.equals(serviceDocument.getDocKind().getCode()))
            .map(serviceDocument -> toDocument(serviceDocument, coordinateFiles, retrieveTypeCode))
            .collect(Collectors.toList());
    }

    @Named("kindCodeToTypeCode")
    default String kindCodeToTypeCode(final String code, @Context final Function<String, String> retrieveTypeCode) {
        return retrieveTypeCode.apply(code);
    }

    @Named("toComment")
    default String toComment(final List<Note> notes) {
        if (nonNull(notes)) {
            return notes.stream()
                .filter(note -> "Описание документа".equals(note.getSubject()))
                .findFirst()
                .map(Note::getText)
                .orElse(null);
        } else {
            return null;
        }
    }

    @Named("toChedFileId")
    default String toChedFileId(final String id, @Context final List<CoordinateFile> coordinateFiles) {
        return coordinateFiles.stream()
            .filter(coordinateFile -> id.equals(coordinateFile.getId()))
            .findFirst()
            .map(CoordinateFile::getFileIdInStore)
            .orElse(null);
    }

    @Named("toFileType")
    default String toFileType(final String id, @Context final List<CoordinateFile> coordinateFiles) {
        return coordinateFiles.stream()
            .filter(coordinateFile -> id.equals(coordinateFile.getId()))
            .findFirst()
            .map(CoordinateFile::getFileName)
            .filter(fileName -> fileName.toLowerCase().matches(".+\\.((pdf)|(gif)|(jpeg)|(jpg)|(png)|(zip))"))
            .map(fileName -> fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase())
            .orElse(null);
    }

    @Named("signServiceToTenants")
    default List<TenantType> signServiceToTenants(
        final RequestServiceForSign signService,
        @Context final List<CoordinateFile> coordinateFiles,
        @Context final BiFunction<RequestServiceForSign, RequestContact, EtpTenant> retrieveEtpTenant,
        @Context final Function<String, TenantProjection> retrieveTenant,
        @Context final Function<String, String> retrieveTypeCode,
        @Context final String documentKindCodeForLink
    ) {
        final List<RequestContact> requestContacts = retrieveRequestContracts(signService);
        return requestContacts.stream()
            .map(requestContact -> {
                final EtpTenant etpTenant = retrieveEtpTenant.apply(signService, requestContact);
                return toTenant(
                    requestContact,
                    etpTenant,
                    coordinateFiles,
                    retrieveTenant,
                    retrieveTypeCode,
                    documentKindCodeForLink
                );
            })
            .collect(Collectors.toList());
    }

    default List<RequestContact> retrieveRequestContracts(final RequestServiceForSign signService) {
        final List<BaseDeclarant> baseDeclarants = signService.getContacts().getBaseDeclarant();
        return baseDeclarants.stream()
            .map(RequestContact.class::cast)
            .collect(Collectors.toList());
    }

    @Named("toBirthDate")
    default LocalDate toBirthDate(
        final RequestContact requestContact,
        final EtpTenant etpTenant,
        final Function<String, TenantProjection> retrieveTenant
    ) {
        return ofNullable(requestContact)
            .map(RequestContact::getBirthDate)
            .map(this::xmlGregorianCalendarToLocalDate)
            .orElseGet(() -> ofNullable(etpTenant)
                .map(EtpTenant::getPersonDocumentId)
                .map(retrieveTenant)
                .map(TenantProjection::getBirthDate)
                .map(LocalDate::parse)
                .orElse(null)
            );
    }

    default LocalDate xmlGregorianCalendarToLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
        return isNull(xmlGregorianCalendar) ? null : LocalDate.of(
            xmlGregorianCalendar.getYear(), xmlGregorianCalendar.getMonth(), xmlGregorianCalendar.getDay()
        );
    }
}
