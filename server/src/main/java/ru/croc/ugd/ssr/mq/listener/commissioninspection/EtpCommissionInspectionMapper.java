package ru.croc.ugd.ssr.mq.listener.commissioninspection;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.IGNORE;
import static ru.croc.ugd.ssr.enums.SsrTradeType.EQUAL_TRADE;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.PersonType.OfferLetters.OfferLetter;
import ru.croc.ugd.ssr.application.Applicant;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.commission.DefectType;
import ru.croc.ugd.ssr.commission.LetterType;
import ru.croc.ugd.ssr.enums.SsrTradeType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.BaseDeclarant;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestContact;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.mos.gu.service._0834.EtpDefectList;
import ru.mos.gu.service._0834.EtpDefectType;
import ru.mos.gu.service._0834.ServiceProperties;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface EtpCommissionInspectionMapper {

    @Mapping(
        target = "document.commissionInspectionData.applicant",
        source = "coordinateMessage.coordinateDataMessage.signService",
        qualifiedByName = "signServiceToApplicant"
    )
    @Mapping(
        target = "document.commissionInspectionData.eno",
        source = "coordinateMessage.coordinateDataMessage.service.serviceNumber"
    )
    @Mapping(
        target = "document.commissionInspectionData.applicationDateTime",
        source = "coordinateMessage.coordinateDataMessage.service.regDate",
        dateFormat = "yyyy-MM-dd HH:mm:ss"
    )
    @Mapping(
        target = "document.commissionInspectionData.address",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToAddress"
    )
    @Mapping(
        target = "document.commissionInspectionData.letter",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any"
    )
    @Mapping(
        target = "document.commissionInspectionData.ccoUnom",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToCcoUnom"
    )
    @Mapping(
        target = "document.commissionInspectionData.flatNum",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToFlatNum"
    )
    @Mapping(
        target = "document.commissionInspectionData.tradeType",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToTradeType"
    )
    @Mapping(
        target = "document.commissionInspectionData.flatStatus",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToFlatStatus"
    )
    @Mapping(
        target = "document.commissionInspectionData.useElectronicFormat",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToUseElectronicFormat"
    )
    @Mapping(
        target = "document.commissionInspectionData.defects.defect",
        source = "coordinateMessage.coordinateDataMessage.signService.customAttributes.any",
        qualifiedByName = "customToDefects"
    )
    @Mapping(
        target = "document.commissionInspectionData.applicationStatusId",
        expression = "java(ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus.REGISTERED.getId())"
    )
    CommissionInspectionDocument toCommissionInspectionDocument(
        final CoordinateMessage coordinateMessage,
        @Context final PersonDocument person,
        @Context final Function<String, String> ccoAddressResolver
    );

    @AfterMapping
    default CommissionInspectionDocument populatePersonId(
        @MappingTarget final CommissionInspectionDocument commissionInspectionDocument,
        @Context final PersonDocument person
    ) {
        final CommissionInspectionData commissionInspection = commissionInspectionDocument
            .getDocument()
            .getCommissionInspectionData();

        ofNullable(commissionInspection.getApplicant())
            .ifPresent(applicant -> applicant.setPersonId(person.getId()));

        commissionInspection.setKpUgs(isKpUgs(commissionInspection));

        return commissionInspectionDocument;
    }

    default boolean isKpUgs(final CommissionInspectionData commissionInspection) {
        return ofNullable(commissionInspection.getTradeType())
            .map(SsrTradeType::ofCommissionInspectionTypeName)
            .filter(EQUAL_TRADE::equals)
            .isPresent();
    }

    default LetterType toLetterType(final Object customAttributes, @Context final PersonDocument person) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(ServiceProperties::getLetterId)
            .flatMap(letterId -> PersonUtils.getOfferLetter(person, letterId))
            .map(this::toLetterType)
            .orElse(null);
    }

    @Mapping(target = "id", source = "letterId")
    @Mapping(target = "fileLink", source = "offerLetter")
    LetterType toLetterType(final OfferLetter offerLetter);

    default String toLetterFileLink(final OfferLetter letter) {
        return PersonUtils.extractOfferLetterFileByType(letter, PersonUtils.OFFER_FILE_TYPE)
            .map(OfferLetter.Files.File::getFileLink)
            .orElse(null);
    }

    @Named("signServiceToApplicant")
    default Applicant signServiceToApplicant(final RequestServiceForSign signService) {
        final List<BaseDeclarant> baseDeclarants = signService.getContacts().getBaseDeclarant();
        if (baseDeclarants == null || baseDeclarants.size() == 0) {
            return null;
        }
        final RequestContact requestContact = (RequestContact) baseDeclarants.get(0);

        final Object customAttribute = signService.getCustomAttributes().getAny();
        final String additionalPhone = customAttribute instanceof ServiceProperties
            ? ((ServiceProperties) customAttribute).getAdditionalPhone()
            : null;

        return requestContractToApplicant(requestContact, additionalPhone);
    }

    @Named("customToAddress")
    default String customToAddress(
        final Object customAttributes,
        @Context final Function<String, String> ccoAddressResolver
    ) {
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(etpCreateCommissionInspection -> ofNullable(etpCreateCommissionInspection.getAddress())
                .orElseGet(() -> ofNullable(etpCreateCommissionInspection.getCcoUnom())
                    .map(ccoAddressResolver)
                    .orElse(null)
                )
            )
            .orElse(null);
    }

    @Named("customToCcoUnom")
    default String customToCcoUnom(final Object customAttributes) {
        return customAttributes instanceof ServiceProperties
            ? ((ServiceProperties) customAttributes).getCcoUnom()
            : null;
    }

    @Named("customToFlatNum")
    default String customToFlatNum(final Object customAttributes) {
        return customAttributes instanceof ServiceProperties
            ? ((ServiceProperties) customAttributes).getFlatNum()
            : null;
    }

    @Named("customToTradeType")
    default String customToTradeType(final Object customAttributes) {
        return customAttributes instanceof ServiceProperties
            ? ((ServiceProperties) customAttributes).getTradeType()
            : null;
    }

    @Named("customToFlatStatus")
    default String customToFlatStatus(final Object customAttributes) {
        return customAttributes instanceof ServiceProperties
            ? ((ServiceProperties) customAttributes).getFlatStatus()
            : null;
    }

    @Named("customToUseElectronicFormat")
    default Boolean customToUseElectronicFormat(final Object customAttributes) {
        return customAttributes instanceof ServiceProperties
            ? ((ServiceProperties) customAttributes).isUseElectronicFormat()
            : null;
    }

    @Named("customToDefects")
    default List<DefectType> customToDefects(final Object customAttributes) {
        final LocalDateTime now = LocalDateTime.now();
        return ofNullable(customAttributes)
            .filter(attributes -> attributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .map(ServiceProperties::getDefects)
            .map(EtpDefectList::getDefect)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(etpDefectType -> toDefectType(etpDefectType, now))
            .collect(Collectors.toList());
    }

    default List<DefectType> toDefectTypeList(final List<EtpDefectType> etpDefectTypes) {
        final LocalDateTime now = LocalDateTime.now();
        return etpDefectTypes.stream()
            .map(etpDefectType -> toDefectType(etpDefectType, now))
            .collect(Collectors.toList());
    }

    @Mapping(target = "flatElement", source = "etpDefectType.flatElement")
    @Mapping(target = "description", source = "etpDefectType.description")
    @Mapping(target = "createdAt", source = "createdAt")
    DefectType toDefectType(final EtpDefectType etpDefectType, final LocalDateTime createdAt);

    @Mapping(target = "phone", source = "requestContact.mobilePhone")
    @Mapping(target = "email", source = "requestContact.EMail")
    Applicant requestContractToApplicant(
        final RequestContact requestContact, final String additionalPhone
    );
}
