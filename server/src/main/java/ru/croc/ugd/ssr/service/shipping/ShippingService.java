package ru.croc.ugd.ssr.service.shipping;

import ru.croc.ugd.ssr.dto.shipping.ApartmentFromDto;
import ru.croc.ugd.ssr.dto.shipping.ApartmentToDto;
import ru.croc.ugd.ssr.dto.shipping.BookingInformation;
import ru.croc.ugd.ssr.dto.shipping.CheckResponseApplicantDto;
import ru.croc.ugd.ssr.dto.shipping.MoveShippingDateRequestDto;
import ru.croc.ugd.ssr.generated.dto.RestCheckResponseApplicantDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

/**
 * Сервис помощи в переселении.
 */
public interface ShippingService {

    /**
     * Выполнение проверок и получение сведений о жильцах и помещениях для переезда.
     *
     * @param snils snils
     * @param ssoId ssoId
     * @return Результат
     */
    BookingInformation fetchBookingInformationIfValidForPerson(final String snils, final String ssoId);

    /**
     * Выполнение проверок и получение сведений о жильцах и помещениях для переезда на проде.
     *
     * @param snils snils
     * @param ssoId ssoId
     * @return Результат
     */
    default BookingInformation checkProd(final String snils, final String ssoId) {
        return StubsService.getBookingInformationStub(snils, ssoId);
    }

    /**
     * Выполнение проверок и получение сведений о жильцах и помещениях для переезда.
     *
     * @param personDocumentId personDocumentId
     * @return Результат
     */
    BookingInformation internalCheck(final String personDocumentId);

    /**
     * Отказ от записи на оказание помощи в переезде.
     * @param applicationId applicationId
     * @param declineReason declineReason
     * @param declineDateTime declineDateTime
     */
    void declineApplicationById(
        final String applicationId, final String declineReason, final LocalDateTime declineDateTime
    );

    /**
     * Перенос даты и времени записи на оказание помощи в переезде.
     * @param applicationId applicationId
     * @param moveShippingDateRequestDto moveShippingDateRequestDto
     */
    void moveShippingDate(final String applicationId, final MoveShippingDateRequestDto moveShippingDateRequestDto);

    /**
     * Отзыв заявления на оказание помощи в переезде.
     * @param eno eno
     * @param declineReason declineReason
     */
    void declineApplicationByApplicant(final String eno, final String declineReason);

    byte[] generateShippingApplicationPdfReport(final String documentId);

    /**
     * Заглушки...
     */
    static class StubsService {

        /**
         * getBookingInformationStub.
         * @param snils snils
         * @param ssoId ssoId
         * @return result
         */
        static BookingInformation getBookingInformationStub(String snils, String ssoId) {
            return BookingInformation.builder()
                .isPossible(true)
                .toApartment(Collections.singletonList(getApartmentToDtoStub()))
                .fromApartment(Collections.singletonList(getApartmentFromDtoStub()))
                .applicant(getCheckResponseApplicantDtoStub(snils, ssoId))
                .build();
        }

        /**
         * getApartmentToDtoStub.
         * @return result
         */
        static ApartmentToDto getApartmentToDtoStub() {
            return ApartmentToDto.builder()
                .address("г. Москва, пос. Филимонковское, ул. Генерала Максимчука, д. 3")
                .cadNumberNew(UUID.randomUUID().toString())
                .flatNumberNew("2")
                .uid(UUID.randomUUID().toString())
                .unomNew("5271685")
                .build();
        }

        /**
         * getApartmentFromDtoStub.
         * @return result
         */
        static ApartmentFromDto getApartmentFromDtoStub() {
            return ApartmentFromDto.builder()
                .address("город Москва, Карельский бульвар, дом 14/16")
                .cadNumberOld(UUID.randomUUID().toString())
                .flatNumberOld("11")
                .floorOld("2")
                .roomNumberOld(1)
                .uid(UUID.randomUUID().toString())
                .unomOld("9462")
                .moveDate(LocalDate.now().plusDays(30))
                .build();
        }

        /**
         * getCheckResponseApplicantDtoStub.
         * @param snils snils
         * @param ssoId ssoId
         * @return result
         */
        static CheckResponseApplicantDto getCheckResponseApplicantDtoStub(String snils, String ssoId) {
            return CheckResponseApplicantDto.builder()
                .dob(LocalDateTime.now().minusYears(30))
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .isDisable(false)
                .rightType(RestCheckResponseApplicantDto.RIGHTTYPEEnum.EMPLOYER)
                .snils(snils)
                .ssoId(ssoId)
                .build();
        }
    }
}
