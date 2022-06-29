package ru.croc.ugd.ssr.mapper;

import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.disabledperson.DisabledPersonData;
import ru.croc.ugd.ssr.dto.disabledperson.DisabledPersonWithFlatIdDto;
import ru.croc.ugd.ssr.dto.disabledperson.RestDisabledPersonDto;
import ru.croc.ugd.ssr.model.disabledperson.DisabledPersonDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPDisabledPersonType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPDisabledPersons;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface DisabledPersonMapper {

    @Mapping(target = "document.disabledPersonData.firstName", source = "dto.disabledPerson.firstName")
    @Mapping(target = "document.disabledPersonData.lastName", source = "dto.disabledPerson.lastName")
    @Mapping(target = "document.disabledPersonData.middleName", source = "dto.disabledPerson.middleName")
    @Mapping(
        target = "document.disabledPersonData.fullName", source = "dto.disabledPerson", qualifiedByName = "toFullName"
    )
    @Mapping(target = "document.disabledPersonData.birthDate", source = "dto.disabledPerson.birthDate")
    @Mapping(target = "document.disabledPersonData.addressFrom", source = "dto.disabledPerson.addressFrom")
    @Mapping(target = "document.disabledPersonData.snils", source = "dto.disabledPerson.snils")
    @Mapping(target = "document.disabledPersonData.area", source = "dto.disabledPerson.area")
    @Mapping(target = "document.disabledPersonData.district", source = "dto.disabledPerson.district")
    @Mapping(target = "document.disabledPersonData.usingWheelchair", source = "dto.disabledPerson.usingWheelchair")
    @Mapping(target = "document.disabledPersonData.unom", source = "dto.disabledPerson.unom")
    @Mapping(target = "document.disabledPersonData.flatNumber", source = "dto.disabledPerson.flatNumber")
    @Mapping(target = "document.disabledPersonData.createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "document.disabledPersonData.personDocumentId", source = "personDocumentId")
    @Mapping(target = "document.disabledPersonData.deleted", source = "dto.disabledPerson.deleted")
    @Mapping(
        target = "document.disabledPersonData.disabledPersonImportDocumentId", source = "disabledPersonImportDocumentId"
    )
    @Mapping(
        target = "document.disabledPersonData.uniqueExcelRecordId", source = "dto.disabledPerson.uniqueExcelRecordId"
    )
    DisabledPersonDocument toDisabledPersonDocument(
        @MappingTarget final DisabledPersonDocument document,
        final DisabledPersonWithFlatIdDto dto,
        final String personDocumentId,
        final String disabledPersonImportDocumentId
    );

    @Mapping(target = "disabledPerson", source = "disabledPersonDto")
    @Mapping(target = "flatId", source = "flatId")
    DisabledPersonWithFlatIdDto toDisabledPersonWithFlatIdDto(
        final RestDisabledPersonDto disabledPersonDto,
        final String flatId
    );

    @Mapping(target = "firstName", source = "document.disabledPersonData.firstName")
    @Mapping(target = "lastName", source = "document.disabledPersonData.lastName")
    @Mapping(target = "middleName", source = "document.disabledPersonData.middleName")
    @Mapping(target = "birthDate", source = "document.disabledPersonData.birthDate")
    @Mapping(target = "snils", source = "document.disabledPersonData.snils")
    @Mapping(target = "address", source = "document.disabledPersonData.addressFrom")
    @Mapping(target = "admArea", source = "document.disabledPersonData.area")
    @Mapping(target = "district", source = "document.disabledPersonData.district")
    @Mapping(target = "unom", source = "document.disabledPersonData.unom")
    @Mapping(target = "flatNumber", source = "document.disabledPersonData.flatNumber")
    @Mapping(
        target = "wheelchair",
        source = "document.disabledPersonData",
        qualifiedByName = "toWheelchair")
    @Mapping(target = "deleted", source = "document.disabledPersonData.deleted")
    SuperServiceDGPDisabledPersonType toSuperServiceDgpDisabledPerson(final DisabledPersonDocument document);

    default SuperServiceDGPDisabledPersons toSuperServiceDgpDisabledPersons(
        final List<DisabledPersonDocument> disabledPersonDocuments
    ) {
        final SuperServiceDGPDisabledPersons superServiceDgpDisabledPersons = new SuperServiceDGPDisabledPersons();
        disabledPersonDocuments.forEach(
            document -> superServiceDgpDisabledPersons
                .getSuperServiceDGPDisabledPerson()
                .add(toSuperServiceDgpDisabledPerson(document))
        );
        return superServiceDgpDisabledPersons;
    }

    @Named("toFullName")
    default String toFullName(final RestDisabledPersonDto dto) {
        return PersonUtils.getFullName(dto.getLastName(), dto.getFirstName(), dto.getMiddleName());
    }

    @Named("toWheelchair")
    default String toWheelchair(final DisabledPersonData disabledPerson) {
        return disabledPerson.isUsingWheelchair() ? "Да" : "Нет";
    }
}
