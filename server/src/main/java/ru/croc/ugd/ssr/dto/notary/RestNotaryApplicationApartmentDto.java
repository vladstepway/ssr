package ru.croc.ugd.ssr.dto.notary;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.dto.commissioninspection.RestApplicantDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 * Информация о квартире.
 */
@Builder
@Value
public class RestNotaryApplicationApartmentDto {

    /**
     * Адрес.
     */
    private final String address;
    /**
     * УНОМ дома.
     */
    private final String unom;
    /**
     * Номер квартиры.
     */
    private final String flatNumber;
    /**
     * Номера комнат расселяемого дома.
     */
    private final List<String> roomNum;
}
