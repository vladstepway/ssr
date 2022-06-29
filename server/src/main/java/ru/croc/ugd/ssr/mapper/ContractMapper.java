package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPSignActStatusType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPSignAgreementStatusType;

import java.time.LocalDate;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface ContractMapper {

    @Mapping(target = "affairId", source = "affairId")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "pdfSignedAgr", source = "signContractFileId")
    @Mapping(target = "signDate", source = "signContractDate")
    @Mapping(target = "techInfo", source = "techInfo")
    SuperServiceDGPSignAgreementStatusType toSuperServiceDgpSignAgreementStatusType(
        final String affairId,
        final String orderId,
        final String signContractFileId,
        final LocalDate signContractDate,
        final SuperServiceDGPSignAgreementStatusType.TechInfo techInfo
    );

    @Mapping(target = "userID", source = "userLogin")
    @Mapping(target = "userFIO", source = "userFullName")
    @Mapping(target = "actionDateTime", expression = "java(java.time.LocalDateTime.now())")
    SuperServiceDGPSignAgreementStatusType.TechInfo toSuperServiceDgpSignAgreementStatusTypeTechInfo(
        final String userLogin,
        final String userFullName
    );

    @Mapping(target = "affairId", source = "affairId")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "pdfSignedAct", source = "signActFileId")
    @Mapping(target = "signActDate", source = "signActDate")
    @Mapping(target = "techInfo", source = "techInfo")
    SuperServiceDGPSignActStatusType toSuperServiceDgpSignActStatus(
        final String affairId,
        final String orderId,
        final String signActFileId,
        final LocalDate signActDate,
        final SuperServiceDGPSignActStatusType.TechInfo techInfo
    );

    @Mapping(target = "userID", source = "userLogin")
    @Mapping(target = "userFIO", source = "userFullName")
    @Mapping(target = "actionDateTime", expression = "java(java.time.LocalDateTime.now())")
    SuperServiceDGPSignActStatusType.TechInfo toSuperServiceDgpSignActStatusTypeTechInfo(
        final String userLogin,
        final String userFullName
    );

    @Mapping(target = "fileType")
    @Mapping(target = "fileLink")
    @Mapping(target = "fileName")
    @Mapping(target = "chedFileId")
    PersonType.Contracts.Contract.Files.File toContractFile(
        final String fileType,
        final String fileLink,
        final String fileName,
        final String chedFileId
    );

    @Mapping(target = "userID", source = "userLogin")
    @Mapping(target = "userFIO", source = "userFullName")
    @Mapping(target = "actionDateTime", expression = "java(java.time.LocalDateTime.now())")
    PersonType.Contracts.Contract.TechInfo toContractTechInfo(
        final String userLogin,
        final String userFullName
    );

}
