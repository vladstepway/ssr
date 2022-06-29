package ru.croc.ugd.ssr.integration.command;

import lombok.Builder;
import lombok.Data;
import ru.croc.ugd.ssr.dto.shipping.BookingInformation;
import ru.croc.ugd.ssr.dto.shipping.EdpResponseStatusDto;

import java.time.ZonedDateTime;

/**
 * Команда для отправки статусов по переезду в очередь.
 */
@Data
@Builder(toBuilder = true)
public class PublishReasonCommand {

    private BookingInformation bookingInformation;

    private EdpResponseStatusDto edpResponseStatusData;

    private ZonedDateTime responseReasonDate;
}
