package ru.croc.ugd.ssr.mq.listener.notarycompensation;

import static org.mapstruct.ReportingPolicy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.model.integration.etpmv.ArrayOfBaseDeclarant;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestContact;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign;
import ru.croc.ugd.ssr.model.notaryCompensation.NotaryCompensationDocument;
import ru.croc.ugd.ssr.notarycompensation.BankDetails;
import ru.croc.ugd.ssr.notarycompensation.NotaryCompensationApplicant;
import ru.mos.gu.service._091201.ServiceProperties;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface EtpNotaryCompensationMapper {

    @Mapping(
        target = "document.notaryCompensationData.eno",
        source = "coordinateMessage.coordinateDataMessage.service.serviceNumber"
    )
    @Mapping(
        target = "document.notaryCompensationData.applicationDateTime",
        source = "coordinateMessage.coordinateDataMessage.service.regDate",
        dateFormat = "yyyy-MM-dd HH:mm:ss"
    )
    @Mapping(
        target = "document.notaryCompensationData.statusId",
        expression = "java(ru.croc.ugd.ssr.dto.notarycompensation.NotaryCompensationFlowStatus.ACCEPTED.getId())"
    )
    @Mapping(
        target = "document.notaryCompensationData.applicant",
        source = "applicant"
    )
    @Mapping(
        target = "document.notaryCompensationData.bankDetails",
        source = "bankDetails"
    )
    NotaryCompensationDocument toNotaryCompensationDocument(
        final CoordinateMessage coordinateMessage,
        final NotaryCompensationApplicant applicant,
        final BankDetails bankDetails
    );

    default NotaryCompensationDocument toNotaryCompensationDocument(final CoordinateMessage coordinateMessage) {
        final ServiceProperties serviceProperties = toServiceProperties(coordinateMessage);

        return toNotaryCompensationDocument(
            coordinateMessage,
            toNotaryCompensationApplicant(
                serviceProperties,
                toRequestContact(coordinateMessage)
            ),
            toBankDetails(serviceProperties)
        );
    }

    default ServiceProperties toServiceProperties(final CoordinateMessage coordinateMessage) {
        return Optional.ofNullable(coordinateMessage)
            .map(CoordinateMessage::getCoordinateDataMessage)
            .map(CoordinateData::getSignService)
            .map(RequestServiceForSign::getCustomAttributes)
            .map(RequestServiceForSign.CustomAttributes::getAny)
            .filter(value -> value instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .orElse(null);
    }

    @Mapping(target = "lastName", source = "requestContact.lastName")
    @Mapping(target = "firstName", source = "requestContact.firstName")
    @Mapping(target = "middleName", source = "requestContact.middleName")
    @Mapping(target = "email", source = "requestContact.EMail")
    @Mapping(target = "phone", source = "requestContact.mobilePhone")
    @Mapping(target = "personDocumentId", source = "serviceProperties.personDocumentId")
    @Mapping(target = "additionalPhone", source = "serviceProperties.additionalPhone")
    NotaryCompensationApplicant toNotaryCompensationApplicant(
        final ServiceProperties serviceProperties,
        final RequestContact requestContact
    );

    default RequestContact toRequestContact(final CoordinateMessage coordinateMessage) {
        return Optional.ofNullable(coordinateMessage)
            .map(CoordinateMessage::getCoordinateDataMessage)
            .map(CoordinateData::getSignService)
            .map(RequestServiceForSign::getContacts)
            .map(ArrayOfBaseDeclarant::getBaseDeclarant)
            .filter(baseDeclarants -> !baseDeclarants.isEmpty())
            .map(baseDeclarants -> baseDeclarants.get(0))
            .map(RequestContact.class::cast)
            .orElse(null);
    }

    @Mapping(target = "recipientFio", source = "serviceProperties.recipientFio")
    @Mapping(target = "account", source = "serviceProperties.paymentAccount")
    @Mapping(target = "bankName", source = "serviceProperties.bankName")
    @Mapping(target = "inn", source = "serviceProperties.bankInn")
    @Mapping(target = "bankBik", source = "serviceProperties.bankBik")
    @Mapping(target = "kpp", source = "serviceProperties.bankKpp")
    @Mapping(target = "correspondentAccount", source = "serviceProperties.correspondentAccount")
    /*
    TODO: for Konstantin: @Mapping(target = "bankStatementFileLink")
    field is not defined by EtpCreateNotaryCompensation
     */
    BankDetails toBankDetails(final ServiceProperties serviceProperties);

    //TODO: Konstantin: Store rightHolders information
    default List<ServiceProperties.RightHolderList.RightHolder> toRightHoldersList(
        final CoordinateMessage coordinateMessage
    ) {
        return Optional.ofNullable(toServiceProperties(coordinateMessage))
            .map(ServiceProperties::getRightHolderList)
            .filter(list -> list.getRightHolder() != null && !list.getRightHolder().isEmpty())
            .map(ServiceProperties.RightHolderList::getRightHolder)
            .orElse(Collections.emptyList());
    }
}
