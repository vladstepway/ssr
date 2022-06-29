package ru.croc.ugd.ssr.solr.converter.flatappointment;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.solr.UgdSsrFlatAppointment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface SolrFlatAppointmentMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(
        target = "ugdSsrFlatAppointmentEno",
        source = "flatAppointmentData.eno"
    )
    @Mapping(
        target = "ugdSsrFlatAppointmentApplicationDateTime",
        source = "flatAppointmentData.applicationDateTime"
    )
    @Mapping(
        target = "ugdSsrFlatAppointmentFullName",
        source = "applicantFullName"
    )
    @Mapping(
        target = "ugdSsrFlatAppointmentCipAddress",
        source = "cipAddress"
    )
    @Mapping(
        target = "ugdSsrFlatAppointmentLetterId",
        source = "flatAppointmentData.offerLetterId"
    )
    @Mapping(
        target = "ugdSsrFlatAppointmentLetterFileLink",
        source = "flatAppointmentData.offerLetterFileLink"
    )
    @Mapping(
        target = "ugdSsrFlatAppointmentDateTime",
        source = "flatAppointmentData.appointmentDateTime"
    )
    @Mapping(
        target = "ugdSsrFlatAppointmentStatus",
        source = "flatAppointmentData.statusId",
        qualifiedByName = "toApplicationStatus"
    )
    @Mapping(
        target = "ugdSsrFlatAppointmentArea",
        source = "area"
    )
    @Mapping(
        target = "ugdSsrFlatAppointmentDistrictCode",
        source = "districtCode"
    )
    UgdSsrFlatAppointment toUgdSsrFlatAppointment(
        @MappingTarget final UgdSsrFlatAppointment ugdSsrFlatAppointment,
        final FlatAppointmentData flatAppointmentData,
        final String applicantFullName,
        final String cipAddress,
        final String area,
        final String districtCode
    );

    @Named("toApplicationStatus")
    default String toApplicationStatus(final String statusId) {
        return ofNullable(statusId)
            .map(FlatAppointmentFlowStatus::of)
            .map(FlatAppointmentFlowStatus::getSsrStatus)
            .orElse(null);
    }
}
