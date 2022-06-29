package ru.croc.ugd.ssr.service.flowerrorreport;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.flowreportederror.PersonInfoType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.utils.RealEstateUtils;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface FlowErrorReportMapper {
    @Mapping(
        target = "affairId",
        source = "personDocument.document.personData.affairId"
    )
    @Mapping(
        target = "personId",
        source = "personDocument.document.personData.personID"
    )
    @Mapping(
        target = "fio",
        source = "personDocument.document.personData.FIO"
    )
    @Mapping(
        target = "addressFrom",
        source = "realEstateDataAndFlatInfo",
        qualifiedByName = "toAddressFrom"
    )
    @Mapping(
        target = "roomsNumber",
        source = "personDocument.document.personData.roomNum",
        qualifiedByName = "toRoomsNumber"
    )
    @Mapping(
        target = "link",
        source = "linkProperties",
        qualifiedByName = "toLink"
    )
    @Mapping(target = "isCommunal", ignore = true)
    PersonInfoType toPersonInfoType(
        final PersonDocument personDocument,
        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfo,
        final LinkProperties linkProperties
    );

    default String toRoomsNumber(final List<String> roomNum) {
        if (CollectionUtils.isEmpty(roomNum)) {
            return null;
        }
        return String.join(", ", roomNum);
    }

    default String toAddressFrom(RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto) {
        return RealEstateUtils.getFlatAddress(realEstateDataAndFlatInfoDto);
    }

    default String toLink(LinkProperties linkProperties) {
        return "https://" + linkProperties.getDomain()
            + "/ugd/ssr/#/app/person/" + linkProperties.getPersonId();
    }
}
