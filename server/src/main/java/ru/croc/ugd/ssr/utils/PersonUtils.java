package ru.croc.ugd.ssr.utils;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.PersonType.Agreements;
import static ru.croc.ugd.ssr.PersonType.Contracts;
import static ru.croc.ugd.ssr.PersonType.Contracts.Contract;
import static ru.croc.ugd.ssr.PersonType.NewFlatInfo;
import static ru.croc.ugd.ssr.PersonType.OfferLetters;
import static ru.croc.ugd.ssr.integration.service.flows.AdministrativeDocumentFlowService.ADMINISTRATIVE_DOC_FILE_TYPE;

import org.apache.commons.lang.StringUtils;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.PersonType.OfferLetters.OfferLetter;
import ru.croc.ugd.ssr.PersonType.OfferLetters.OfferLetter.Files.File;
import ru.croc.ugd.ssr.db.projection.TenantProjection;
import ru.croc.ugd.ssr.enums.StatusLiving;
import ru.croc.ugd.ssr.model.PersonDocument;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс для обработки данных по жителям.
 */
public class PersonUtils {

    /**
     * тип файла с предложением.
     */
    public static final String OFFER_FILE_TYPE = "1";
    public static final String SIGNED_CONTRACT_FILE_TYPE = "4";

    public static final String PDF_CONTRACT_FILE_TYPE = "1";
    public static final String RTF_CONTRACT_TO_SIGN_FILE_TYPE = "2";

    public static final String EVENT_RESETTLEMENT_ACCEPTED = "1";
    public static final String EVENT_RESETTLEMENT_DECLINED = "2";
    public static final String EVENT_AREA_FIXED = "4";

    /**
     * Получение статуса обремененности по коду.
     *
     * @param encumbrancesCode код
     * @return статус обремененности
     */
    public static String getEncumbrances(final String encumbrancesCode) {
        switch (encumbrancesCode) {
            case "1":
                return "Да";
            case "2":
                return "Нет";
            default:
                return "Нет данных";
        }
    }

    /**
     * Получение гендера по коду.
     *
     * @param genderCode код
     * @return гендер
     */
    public static String getGender(final String genderCode) {
        switch (genderCode) {
            case "1":
                return "Мужской";
            case "2":
                return "Женский";
            default:
                return "Нет данных";
        }
    }

    /**
     * Получение кода события соглашения.
     *
     * @param event событие
     * @return код
     */
    public static String getAgreementEvent(final String event) {
        switch (event.trim().toLowerCase()) {
            case "согласие":
                return "1";
            case "отказ":
                return "2";
            default:
                return null;
        }
    }

    public static List<PersonType.Agreements.Agreement> retrieveAgreementList(final PersonDocument personDocument) {
        return of(personDocument.getDocument())
            .map(Person::getPersonData)
            .map(PersonUtils::retrieveAgreementList)
            .orElse(Collections.emptyList());
    }

    public static List<PersonType.Agreements.Agreement> retrieveAgreementList(final PersonType personType) {
        return ofNullable(personType)
            .map(PersonType::getAgreements)
            .map(PersonType.Agreements::getAgreement)
            .orElse(Collections.emptyList());
    }

    /**
     * Получение статуса проживания по коду.
     *
     * @param statusLivingCode код
     * @return статус проживания
     */
    public static String getStatusLiving(final String statusLivingCode) {
        if (Objects.isNull(statusLivingCode)) {
            return "Отсутствует";
        }
        switch (statusLivingCode) {
            case "1":
                return "Собственник (частная собственность)";
            case "2":
                return "Пользователь (частная собственность)";
            case "3":
                return "Наниматель (найм/пользование)";
            case "4":
                return "Проживающий (найм/пользователь)";
            case "5":
                return "Свободная";
            default:
                return "Отсутствует";
        }
    }

    /**
     * Получение значения по коду.
     *
     * @param code код
     * @return значение
     */
    public static String getValueFromCode(final String code) {
        if (Objects.isNull(code)) {
            return "Нет данных";
        }
        return code.equals("1") ? "Да" : "Нет";
    }

    /**
     * Получение последнего письма.
     *
     * @param offerLetters список писем
     * @return письмо
     */
    public static OfferLetter getLastOfferLetter(
        final List<OfferLetter> offerLetters
    ) {
        return ofNullable(offerLetters)
            .map(List::stream)
            .orElse(Stream.empty())
            .max(Comparator.comparing(OfferLetter::getDate, Comparator.nullsFirst(LocalDate::compareTo)))
            .orElse(null);
    }

    /**
     * Последнее письмо.
     * @param person person
     * @return письмо
     */
    public static Optional<OfferLetter> getLastOfferLetter(final PersonType person) {
        return ofNullable(person)
            .map(PersonType::getOfferLetters)
            .map(OfferLetters::getOfferLetter)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .reduce((first, second) -> second);
    }

    /**
     * Последнее письмо без отказа.
     * @param person person
     * @return письмо
     */
    public static Optional<OfferLetter> getLastOfferLetterIfNotDeclined(final PersonType person) {
        return ofNullable(person)
            .map(PersonType::getOfferLetters)
            .map(OfferLetters::getOfferLetter)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .reduce((first, second) -> second)
            .filter(letter -> !hasLastAgreementByType(person, letter, EVENT_RESETTLEMENT_DECLINED));
    }

    /**
     * Получение contracts.
     *
     * @param contracts contracts
     * @return last contract
     */
    public static Contract getLastContract(
        final List<Contract> contracts
    ) {
        return contracts.stream()
            .max(Comparator.comparing(Contract::getIssueDate,
                Comparator.nullsFirst(LocalDate::compareTo)))
            .orElse(null);
    }

    public static Optional<Contract> getContractByOrderId(
        final PersonDocument personDocument, final String orderId
    ) {
        return getContractsStream(personDocument)
            .filter(contract -> contract.getOrderId() != null)
            .filter(contract -> contract.getOrderId().equals(orderId))
            .findFirst();
    }

    public static Optional<Contract> getContractByOrderId(final PersonType personType, final String orderId) {
        return getContractsStream(personType)
            .filter(contract -> contract.getOrderId() != null)
            .filter(contract -> contract.getOrderId().equals(orderId))
            .findFirst();
    }

    /**
     * Получение файла из письма по типу файла.
     * @param letter letter
     * @param fileType fileType
     * @return файл
     */
    public static Optional<File> extractOfferLetterFileByType(final OfferLetter letter, final String fileType) {
        return Optional.ofNullable(letter)
            .map(OfferLetter::getFiles)
            .map(OfferLetter.Files::getFile)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(f -> StringUtils.isNotBlank(f.getFileType()) && f.getFileType().equals(fileType))
            .findFirst();
    }

    /**
     * Получение файла из договора по типу файла.
     * @param contract contract
     * @param fileType fileType
     * @return файл
     */
    public static Optional<Contract.Files.File> extractContractFileByType(
        final Contract contract, final String fileType
    ) {
        return Optional.ofNullable(contract)
            .map(Contract::getFiles)
            .map(Contract.Files::getFile)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(f -> StringUtils.isNotBlank(f.getFileType()) && f.getFileType().equals(fileType))
            .findFirst();
    }

    /**
     * Получение файла из письма по типу файла.
     * @param contract contract
     * @return файл
     */
    public static Optional<Contract.Files.File> extractContactFileWithChed(
        final Contract contract
    ) {
        return Optional.ofNullable(contract)
            .map(Contract::getFiles)
            .map(Contract.Files::getFile)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(f -> !StringUtils.isEmpty(f.getChedFileId()))
            .findFirst();
    }

    /**
     * Получить PersonType по документу персоны.
     * @param personDocument документ персоны
     * @return PersonType
     */
    public static Optional<PersonType> getPersonType(final PersonDocument personDocument) {
        return Optional.ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData);
    }

    /**
     * Найти письмо по ИД.
     * @param personDocument документ персоны
     * @param letterId ИД письма
     * @return данные письма
     */
    public static Optional<OfferLetter> getOfferLetter(
        final PersonDocument personDocument, final String letterId
    ) {
        return getPersonType(personDocument)
            .flatMap(person -> getOfferLetter(person, letterId));
    }

    /**
     * Найти письмо по ИД.
     * @param person person
     * @param letterId letterId
     * @return данные письма
     */
    public static Optional<OfferLetter> getOfferLetter(
        final PersonType person, final String letterId
    ) {
        return ofNullable(person)
            .map(PersonType::getOfferLetters)
            .map(OfferLetters::getOfferLetter)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(offerLetter -> offerLetter.getLetterId().equals(letterId))
            .findFirst();
    }

    /**
     * Последнее по времени согласие.
     * @param personDocument данные
     * @return Последнее по времени согласие
     */
    public static Optional<Agreements.Agreement> getLastAcceptedAgreement(
        final PersonDocument personDocument
    ) {
        return of(personDocument.getDocument())
            .map(Person::getPersonData)
            .flatMap(PersonUtils::getLastAcceptedAgreement);
    }

    /**
     * Последнее по времени согласие.
     * @param personType данные
     * @return Последнее по времени согласие
     */
    public static Optional<Agreements.Agreement> getLastAcceptedAgreement(final PersonType personType) {
        return getAcceptedOrDeclinedAgreementsStream(personType)
            .sorted(Comparator.comparing(Agreements.Agreement::getDate, Comparator.nullsFirst(LocalDate::compareTo)))
            .reduce(((first, second) -> second))
            .filter(agreement -> EVENT_RESETTLEMENT_ACCEPTED.equals(agreement.getEvent()));
    }

    /**
     * Последнее по времени согласие для письма.
     * @param personType данные
     * @param letter письмо
     * @return Последнее по времени согласие
     */
    public static Optional<Agreements.Agreement> getAcceptedAgreement(
        final PersonType personType, final OfferLetter letter
    ) {
        return getAcceptedOrDeclinedAgreementsStream(personType)
            .filter(agreement -> nonNull(agreement.getLetterId()))
            .filter(agreement -> nonNull(letter) && Objects.equals(agreement.getLetterId(), letter.getLetterId()))
            .sorted(Comparator.comparing(Agreements.Agreement::getDate, Comparator.nullsFirst(LocalDate::compareTo)))
            .reduce((first, second) -> second)
            .filter(agreement -> EVENT_RESETTLEMENT_ACCEPTED.equals(agreement.getEvent()));
    }

    public static boolean hasAreaFixedAgreement(final PersonType personType, final OfferLetter letter) {
        return hasAgreementByType(personType, letter, EVENT_AREA_FIXED);
    }

    public static boolean hasAgreementByType(
        final PersonType personType, final OfferLetter letter, final String agreementType
    ) {
        return retrieveAgreementList(personType).stream()
            .filter(agreement -> nonNull(agreement.getLetterId()))
            .filter(agreement -> nonNull(letter) && Objects.equals(agreement.getLetterId(), letter.getLetterId()))
            .filter(agreement -> nonNull(agreement.getEvent()))
            .anyMatch(agreement -> Objects.equals(agreementType, agreement.getEvent()));
    }

    public static boolean hasLastAgreementByType(
        final PersonType personType, final OfferLetter letter, final String agreementType
    ) {
        return retrieveAgreementList(personType).stream()
            .filter(agreement -> nonNull(agreement.getLetterId()))
            .filter(agreement -> nonNull(letter) && Objects.equals(agreement.getLetterId(), letter.getLetterId()))
            .sorted(Comparator.comparing(Agreements.Agreement::getDate, Comparator.nullsFirst(LocalDate::compareTo)))
            .reduce((first, second) -> second)
            .filter(agreement -> nonNull(agreement.getEvent()))
            .filter(agreement -> Objects.equals(agreementType, agreement.getEvent()))
            .isPresent();
    }

    /**
     * Проверка на наличие согласия для письма.
     * @param personType данные
     * @param offerLetter письмо
     * @return Наличие согласия - true/false
     * */

    public static boolean hasAcceptedAgreement(final PersonType personType, final OfferLetter offerLetter) {
        return getAcceptedAgreement(personType, offerLetter)
            .isPresent();
    }

    private static Stream<Agreements.Agreement> getAcceptedOrDeclinedAgreementsStream(final PersonType personType) {
        return ofNullable(personType.getAgreements())
            .map(Agreements::getAgreement)
            .orElse(new ArrayList<>())
            .stream()
            .filter(agreement -> EVENT_RESETTLEMENT_ACCEPTED.equals(agreement.getEvent())
                || EVENT_RESETTLEMENT_DECLINED.equals(agreement.getEvent()));
    }

    /**
     * Письмо из последнего согласия.
     * @param personDocument personDocument
     * @return письмо из последнего согласия
     */
    public static Optional<OfferLetter> getLastAcceptedAgreementOfferLetter(final PersonDocument personDocument) {
        return of(personDocument.getDocument())
            .map(Person::getPersonData)
            .flatMap(PersonUtils::getLastAcceptedAgreementOfferLetter);
    }

    /**
     * Письмо из последнего согласия.
     * @param person person
     * @return письмо из последнего согласия
     */
    public static Optional<OfferLetter> getLastAcceptedAgreementOfferLetter(final PersonType person) {
        return ofNullable(person)
            .flatMap(PersonUtils::getLastAcceptedAgreement)
            .map(Agreements.Agreement::getLetterId)
            .flatMap(letterId -> getOfferLetter(person, letterId));
    }

    /**
     * Получить ИД ЦИП.
     * @param personDocument документ персоны
     * @return ИД ЦИП
     */
    public static Optional<String> getCipId(final PersonDocument personDocument) {
        return getPersonType(personDocument)
            .map(PersonUtils::getCipId)
            .orElse(null);
    }

    /**
     * Получить ИД ЦИП.
     * @param personType документ персоны
     * @return ИД ЦИП
     */
    public static Optional<String> getCipId(final PersonType personType) {
        return getOfferLetterWithAgreementStream(personType)
            .filter(offerLetter -> StringUtils.isNotEmpty(offerLetter.getIdCIP()))
            .map(OfferLetter::getIdCIP)
            .reduce((first, second) -> second);
    }

    /**
     * Получить ИД ЦИП.
     * @param personDocument документ персоны
     * @return ИД ЦИП
     */
    public static Optional<String> getOfferLetterChedId(final PersonDocument personDocument) {
        final OfferLetter lastOfferLetter = getLastOfferLetter(getOfferLetterStream(personDocument)
            .collect(Collectors.toList()));
        return getOfferLetterChedId(lastOfferLetter);
    }

    public static Optional<String> getOfferLetterChedId(final OfferLetter offerLetter) {
        return getOfferLetterFile(offerLetter)
            .map(OfferLetters.OfferLetter.Files.File::getChedFileId);
    }

    public static Optional<String> getOfferLetterFileLink(final OfferLetter offerLetter) {
        return getOfferLetterFile(offerLetter)
            .map(OfferLetters.OfferLetter.Files.File::getFileLink);
    }

    public static Optional<File> getOfferLetterFile(final OfferLetter offerLetter) {
        return extractOfferLetterFileByType(offerLetter, OFFER_FILE_TYPE);
    }

    public static Optional<File> getAdministrativeDocument(final OfferLetter offerLetter) {
        return extractOfferLetterFileByType(offerLetter, ADMINISTRATIVE_DOC_FILE_TYPE);
    }

    public static Optional<Contract.Files.File> getRtfContractToSignFile(final Contract contract) {
        return extractContractFileByType(contract, RTF_CONTRACT_TO_SIGN_FILE_TYPE);
    }

    public static Optional<Contract.Files.File> getPdfContractFile(final Contract contract) {
        return extractContractFileByType(contract, PDF_CONTRACT_FILE_TYPE);
    }

    /**
     * Получить ИД ЦИП.
     * @param personDocument документ персоны
     * @return ИД ЦИП
     */
    public static Optional<String> getLastContractChedId(final PersonDocument personDocument) {
        final Contract lastContract = getLastContract(getContractsStream(personDocument)
            .collect(Collectors.toList()));
        return extractContactFileWithChed(lastContract)
            .map(Contract.Files.File::getChedFileId);
    }

    public static String getUnom(final PersonDocument personDocument) {
        return ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getUNOM)
            .map(BigInteger::toString)
            .orElse(null);
    }

    public static String getFlatNumber(final PersonDocument personDocument) {
        return ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getFlatNum)
            .orElse(null);
    }

    public static List<String> getRooms(final PersonDocument personDocument) {
        return ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getRoomNum)
            .orElse(Collections.emptyList());
    }

    /**
     * Получить полное имя.
     * @param personDocument документ персоны
     * @return ФИО
     */
    public static String getFullName(final PersonDocument personDocument) {
        return ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonUtils::getFullName)
            .orElse(null);
    }

    public static String getFullName(final PersonType personType) {
        final String fio = ofNullable(personType.getFIO())
            .orElseGet(() ->
                getFullName(personType.getLastName(), personType.getFirstName(), personType.getMiddleName())
            );
        return !StringUtils.isEmpty(fio) ? fio : null;
    }

    public static String getFullName(final TenantProjection tenantProjection) {
        return ofNullable(tenantProjection)
            .map(TenantProjection::getFullName)
            .orElseGet(() -> ofNullable(tenantProjection)
                .map(tenant -> getFullName(
                        tenant.getLastName(),
                        tenant.getFirstName(),
                        tenant.getMiddleName()
                    )
                )
                .orElse(null)
            );
    }

    public static String getFullName(final String lastName, final String firstName, final String middleName) {
        final String fio = Stream.of(lastName, firstName, middleName)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(" "));
        return StringUtils.isNotEmpty(fio) ? fio : null;
    }

    /**
     * Получить заселяемую квартиру по order id.
     *
     * @param personDocument документ
     * @param contractOrderId contractOrderId
     * @return заселяемую квартиру
     */
    public static NewFlatInfo.NewFlat getNewFlatByOrderId(
        final PersonDocument personDocument,
        final String contractOrderId
    ) {
        return getPersonType(personDocument)
            .map(personType -> getNewFlatByOrderId(personType, contractOrderId))
            .orElse(null);
    }

    public static NewFlatInfo.NewFlat getNewFlatByOrderId(
        final PersonType personType,
        final String contractOrderId
    ) {
        return getNewFlatStream(personType)
            .filter(flat -> Objects.equals(flat.getOrderId(), contractOrderId))
            .findFirst()
            .orElse(null);
    }

    public static Stream<NewFlatInfo.NewFlat> getNewFlatStream(final PersonType personType) {
        return ofNullable(personType)
            .map(PersonType::getNewFlatInfo)
            .map(NewFlatInfo::getNewFlat)
            .map(Collection::stream)
            .orElse(Stream.empty());
    }

    public static Stream<OfferLetter> getOfferLetterWithAgreementStream(final PersonType personType) {
        return getOfferLetterStream(personType)
            .filter(offerLetter -> hasAcceptedAgreement(personType, offerLetter));
    }

    public static Stream<OfferLetter> getOfferLetterStream(final PersonType personType) {
        return Optional.ofNullable(personType)
            .map(PersonType::getOfferLetters)
            .map(OfferLetters::getOfferLetter)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(Objects::nonNull);
    }

    public static Stream<OfferLetter> getOfferLetterStream(final PersonDocument personDocument) {
        return getPersonType(personDocument)
            .map(PersonType::getOfferLetters)
            .map(OfferLetters::getOfferLetter)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(Objects::nonNull);
    }

    public static Optional<Contract> getContractByNewFlat(
        final PersonType personType, final NewFlatInfo.NewFlat newFlat
    ) {
        if (isNull(newFlat)) {
            return Optional.empty();
        }
        return ofNullable(personType.getContracts())
            .map(Contracts::getContract)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(contract ->
                (nonNull(contract.getLetterId()) && contract.getLetterId().equals(newFlat.getLetterId()))
                    || (nonNull(contract.getOrderId()) && contract.getOrderId().equals(newFlat.getOrderId()))
            )
            .findFirst();
    }

    public static Optional<String> getOrderIdByNewFlat(
        final PersonType personType, final NewFlatInfo.NewFlat newFlat
    ) {
        return getContractByNewFlat(personType, newFlat)
            .map(Contract::getOrderId);
    }

    public static Stream<Contract> getContractsStream(final PersonDocument personDocument) {
        return getPersonType(personDocument)
            .map(PersonUtils::getContractsStream)
            .orElse(Stream.empty());
    }

    public static Stream<Contract> getContractsStream(final PersonType personType) {
        return ofNullable(personType)
            .map(PersonType::getContracts)
            .map(Contracts::getContract)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(Objects::nonNull);
    }

    public static boolean isContractSigned(final PersonType personType, final NewFlatInfo.NewFlat newFlat) {
        final boolean agreementDateExists = Optional.ofNullable(newFlat)
            .map(PersonType.NewFlatInfo.NewFlat::getAgrDate)
            .isPresent();

        final Optional<Contract> optionalContract =
            getContractByNewFlat(personType, newFlat);

        final boolean contractSignDateExists = optionalContract
            .map(Contract::getContractSignDate)
            .isPresent();

        //TODO Aleksandr, Konstantin: confirm if this check should be shared for all services
        final boolean signedContractFileExists = optionalContract
            .map(Contract::getFiles)
            .map(Contract.Files::getFile)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(Contract.Files.File::getFileType)
            .anyMatch(SIGNED_CONTRACT_FILE_TYPE::equals);

        return agreementDateExists || contractSignDateExists || signedContractFileExists;
    }

    public static boolean isOwnersOrTenant(final String statusLiving) {
        return StatusLiving.OWNER.value().toString().equals(statusLiving)
            || StatusLiving.TENANT.value().toString().equals(statusLiving)
            || StatusLiving.RESIDENT.value().toString().equals(statusLiving);
    }

    public static String getBasePhoneNumber(final PersonType.Phones phones) {
        if (nonNull(phones) && nonNull(phones.getBaseNumber())) {
            switch (phones.getBaseNumber()) {
                case "1":
                    return phones.getPhoneNumber1();
                case "2":
                    return phones.getPhoneNumber2();
                case "3":
                    return phones.getPhoneNumber3();
                default:
                    break;
            }
        }
        return null;
    }

    public static String getNewFlatAddress(final PersonType.NewFlatInfo.NewFlat newFlat) {
        return ofNullable(newFlat.getCcoAddress())
            .map(address -> address.concat(
                ofNullable(newFlat.getCcoFlatNum())
                    .map(flatNum -> ", квартира " + flatNum)
                    .orElse("")
            ))
            .orElse(null);
    }

    public static Optional<NewFlatInfo.NewFlat> getNewFlatByUnomAndFlatNumber(
        final PersonDocument personDocument, final String unom, final String flatNumber
    ) {
        return ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .flatMap(person -> getNewFlatByUnomAndFlatNumber(person, unom, flatNumber));
    }

    public static Optional<NewFlatInfo.NewFlat> getNewFlatByUnomAndFlatNumber(
        final PersonType person, final String unom, final String flatNumber
    ) {
        return ofNullable(person)
            .map(PersonType::getNewFlatInfo)
            .map(NewFlatInfo::getNewFlat)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(flat -> nonNull(flat.getCcoUnom()) && Objects.equals(flat.getCcoUnom().toString(), unom))
            .filter(flat -> Objects.equals(flat.getCcoFlatNum(), flatNumber))
            .findFirst();
    }

    public static List<PersonType.Contracts.Contract> retrieveContractList(final PersonType personType) {
        return ofNullable(personType)
            .map(PersonType::getContracts)
            .map(PersonType.Contracts::getContract)
            .orElse(Collections.emptyList());
    }

    public static Optional<Contract> getContractByLetterId(
        final PersonType personType, final String letterId
    ) {
        return retrieveContractList(personType)
            .stream()
            .filter(contract -> Objects.equals(letterId, contract.getLetterId()))
            .findFirst();
    }

    public static Optional<Agreements.Agreement> getLastAgreementByLetterId(
        final PersonType personType, final String letterId
    ) {
        return retrieveAgreementList(personType)
            .stream()
            .filter(agreement -> Objects.equals(letterId, agreement.getLetterId()))
            .reduce((first, second) -> second);
    }
}
