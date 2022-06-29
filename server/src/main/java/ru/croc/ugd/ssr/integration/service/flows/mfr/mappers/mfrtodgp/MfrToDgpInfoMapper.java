package ru.croc.ugd.ssr.integration.service.flows.mfr.mappers.mfrtodgp;

import org.mapstruct.Mapping;
import ru.croc.ugd.ssr.dto.tradeaddition.EstateInfoDto;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.PersonInfoType;

public interface MfrToDgpInfoMapper {

    @Mapping(target = "personId", source = "personId")
    @Mapping(target = "personFio", ignore = true)
    @Mapping(target = "personDocumentId", ignore = true)
    @Mapping(target = "snils", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "middleName", ignore = true)
    PersonInfoType toPersonInfoType(final String personId);

    @Mapping(target = "unom", source = "unom")
    @Mapping(target = "cadNumber", source = "cadNumber")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "flatNumber", source = "flatNumber")
    @Mapping(target = "rooms", source = "rooms")
    @Mapping(target = "flatCadNumber", ignore = true)
    EstateInfoType toEstateInfoType(final EstateInfoDto estateInfoDto);
}
