package ru.croc.ugd.ssr.solr.converter.contractappointment;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.enums.ContractAppointmentSignType;
import ru.croc.ugd.ssr.solr.UgdSsrContractAppointment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrContractAppointmentMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(
        target = "ugdSsrContractAppointmentApplicationNumber",
        source = "contractAppointmentData.eno"
    )
    @Mapping(
        target = "ugdSsrContractAppointmentCreationDateTime",
        source = "contractAppointmentData.applicationDateTime"
    )
    @Mapping(
        target = "ugdSsrContractAppointmentFullName",
        source = "applicantFullName"
    )
    @Mapping(
        target = "ugdSsrContractAppointmentCipAddress",
        source = "cipAddress"
    )
    @Mapping(
        target = "ugdSsrContractAppointmentAddressTo",
        source = "contractAppointmentData.addressTo"
    )
    @Mapping(
        target = "ugdSsrContractAppointmentDateTime",
        source = "contractAppointmentData.appointmentDateTime"
    )
    @Mapping(
        target = "ugdSsrContractAppointmentSignType",
        source = "contractAppointmentData.signType",
        qualifiedByName = "toSignTypeName"
    )
    @Mapping(
        target = "ugdSsrContractAppointmentStatus",
        source = "contractAppointmentData.statusId",
        qualifiedByName = "toApplicationStatus"
    )
    @Mapping(
        target = "ugdSsrContractAppointmentArea",
        source = "area"
    )
    @Mapping(
        target = "ugdSsrContractAppointmentDistrictCode",
        source = "districtCode"
    )
    UgdSsrContractAppointment toUgdSsrContractAppointment(
        @MappingTarget final UgdSsrContractAppointment ugdSsrContractAppointment,
        final ContractAppointmentData contractAppointmentData,
        final String applicantFullName,
        final String cipAddress,
        final String area,
        final String districtCode
    );

    @Named("toApplicationStatus")
    default String toApplicationStatus(final String statusId) {
        return ofNullable(statusId)
            .map(ContractAppointmentFlowStatus::of)
            .map(ContractAppointmentFlowStatus::getSsrStatus)
            .orElse(null);
    }

    @Named("toSignTypeName")
    default String toSignTypeName(final int signType) {
        return ContractAppointmentSignType.ofTypeCode(signType)
            .getTypeName();
    }
}
