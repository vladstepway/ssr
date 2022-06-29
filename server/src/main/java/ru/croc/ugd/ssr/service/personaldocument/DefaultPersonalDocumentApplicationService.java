package ru.croc.ugd.ssr.service.personaldocument;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.db.projection.TenantProjection;
import ru.croc.ugd.ssr.dto.personaldocument.PersonalDocumentApplicationFlowStatus;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.service.notification.PersonalDocumentApplicationElkNotificationService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.ArrayOfBaseDeclarant;
import ru.croc.ugd.ssr.model.integration.etpmv.ArrayOfCoordinateFile;
import ru.croc.ugd.ssr.model.integration.etpmv.ArrayOfServiceDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.BaseDeclarant;
import ru.croc.ugd.ssr.model.integration.etpmv.ContactType;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateFile;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestContact;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign;
import ru.croc.ugd.ssr.model.integration.etpmv.ServiceDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentApplicationDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentDocument;
import ru.croc.ugd.ssr.mq.listener.personaldocument.EtpPersonalDocumentApplicationMapper;
import ru.croc.ugd.ssr.mq.listener.personaldocument.EtpPersonalDocumentMapper;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplication;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplicationType;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentApplicationDocumentService;
import ru.croc.ugd.ssr.service.personaldocument.type.SsrPersonalDocumentTypeService;
import ru.mos.gu.service._088201.EtpTenant;
import ru.mos.gu.service._088201.ServiceProperties;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DefaultPersonalDocumentApplicationService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultPersonalDocumentApplicationService implements PersonalDocumentApplicationService {

    private static final String DOCUMENT_KIND_CODE_FOR_LINK = "12685";

    private final PersonalDocumentApplicationRegistrationService personalDocumentApplicationRegistrationService;
    private final PersonalDocumentApplicationDocumentService personalDocumentApplicationDocumentService;
    private final PersonDocumentService personDocumentService;
    private final PersonalDocumentApplicationElkNotificationService personalDocumentApplicationElkNotificationService;
    private final EtpPersonalDocumentApplicationMapper etpPersonalDocumentApplicationMapper;
    private final EtpPersonalDocumentMapper etpPersonalDocumentMapper;
    private final MessageUtils messageUtils;
    private final SsrPersonalDocumentTypeService ssrPersonalDocumentTypeService;

    @Override
    public void processRegistration(final CoordinateMessage coordinateMessage) {
        try {
            final RequestServiceForSign signService = retrieveSignService(coordinateMessage);
            final RequestContact applicantRequestContract = retrieveApplicantRequestContract(signService);
            final EtpTenant applicantEtpTenant = retrieveEtpTenant(signService, applicantRequestContract);
            final PersonalDocumentApplicationDocument personalDocumentApplicationDocument =
                etpPersonalDocumentApplicationMapper.toPersonalDocumentApplicationDocument(
                    coordinateMessage, applicantRequestContract, applicantEtpTenant
                );

            final String affairId = retrieveAffairId(personalDocumentApplicationDocument);
            final List<CoordinateFile> coordinateFiles = retrieveFiles(coordinateMessage);
            final PersonalDocumentDocument personalDocumentDocument =
                etpPersonalDocumentMapper.toPersonalDocumentDocument(
                    affairId,
                    signService,
                    coordinateFiles,
                    this::retrieveEtpTenant,
                    this::retrieveTenant,
                    ssrPersonalDocumentTypeService::getTypeCodeByKindCode,
                    DOCUMENT_KIND_CODE_FOR_LINK
                );

            if (isNull(personalDocumentApplicationDocument) || isNull(personalDocumentDocument)) {
                throw new SsrException("Unable to parse personal document application request: " + coordinateMessage);
            }
            if (!isDuplicate(personalDocumentApplicationDocument)) {
                personalDocumentApplicationRegistrationService.processRegistration(
                    personalDocumentApplicationDocument, personalDocumentDocument
                );
            } else {
                log.warn("Personal document application has been already registered: {}", coordinateMessage);
            }
        } catch (Exception e) {
            log.warn("Unable to save personal document application due to: {}", e.getMessage(), e);
            personalDocumentApplicationElkNotificationService.sendStatus(
                PersonalDocumentApplicationFlowStatus.TECHNICAL_CRASH_REGISTRATION,
                messageUtils.retrieveEno(coordinateMessage).orElse(null)
            );
        }
    }

    private String retrieveAffairId(final PersonalDocumentApplicationDocument personalDocumentApplicationDocument) {
        return ofNullable(personalDocumentApplicationDocument)
            .map(PersonalDocumentApplicationDocument::getDocument)
            .map(PersonalDocumentApplication::getPersonalDocumentApplicationData)
            .map(PersonalDocumentApplicationType::getPersonDocumentId)
            .flatMap(personDocumentService::fetchById)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getAffairId)
            .orElseThrow(() -> new SsrException("Unable to find affair id by person document id"));
    }

    private boolean isDuplicate(final PersonalDocumentApplicationDocument personalDocumentApplicationDocument) {
        return of(personalDocumentApplicationDocument.getDocument())
            .map(PersonalDocumentApplication::getPersonalDocumentApplicationData)
            .map(PersonalDocumentApplicationType::getEno)
            .filter(personalDocumentApplicationDocumentService::existsByEno)
            .isPresent();
    }

    private RequestServiceForSign retrieveSignService(final CoordinateMessage coordinateMessage) {
        return ofNullable(coordinateMessage)
            .map(CoordinateMessage::getCoordinateDataMessage)
            .map(CoordinateData::getSignService)
            .orElse(null);
    }

    private RequestContact retrieveApplicantRequestContract(final RequestServiceForSign signService) {
        final List<BaseDeclarant> baseDeclarants = ofNullable(signService)
            .map(RequestServiceForSign::getContacts)
            .map(ArrayOfBaseDeclarant::getBaseDeclarant)
            .orElse(Collections.emptyList())
            .stream()
            .filter(baseDeclarant -> ContactType.DECLARANT.equals(baseDeclarant.getType()))
            .collect(Collectors.toList());

        if (baseDeclarants.isEmpty()) {
            return null;
        }
        return (RequestContact) baseDeclarants.get(0);
    }

    private EtpTenant retrieveEtpTenant(final RequestServiceForSign signService, final RequestContact requestContact) {
        final String objectId = ofNullable(requestContact)
            .map(BaseDeclarant::getDocuments)
            .map(ArrayOfServiceDocument::getServiceDocument)
            .orElse(Collections.emptyList())
            .stream()
            .filter(serviceDocument -> nonNull(serviceDocument.getDocKind())
                && DOCUMENT_KIND_CODE_FOR_LINK.equals(serviceDocument.getDocKind().getCode()))
            .map(ServiceDocument::getObjectId)
            .findFirst()
            .orElse(null);

        final ServiceProperties.Tenants tenants = ofNullable(signService)
            .map(RequestServiceForSign::getCustomAttributes)
            .map(RequestServiceForSign.CustomAttributes::getAny)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(ServiceProperties::getTenants)
            .orElse(null);

        return ofNullable(tenants)
            .map(ServiceProperties.Tenants::getTenant)
            .orElse(Collections.emptyList())
            .stream()
            .filter(tenant -> nonNull(objectId) && objectId.equals(tenant.getId()))
            .findFirst()
            .orElse(null);
    }

    private List<CoordinateFile> retrieveFiles(final CoordinateMessage coordinateMessage) {
        return ofNullable(coordinateMessage)
            .map(CoordinateMessage::getFiles)
            .map(ArrayOfCoordinateFile::getCoordinateFile)
            .orElse(Collections.emptyList());
    }

    private TenantProjection retrieveTenant(final String personDocumentId) {
        return personDocumentService.fetchTenantById(personDocumentId)
            .orElse(null);
    }
}
