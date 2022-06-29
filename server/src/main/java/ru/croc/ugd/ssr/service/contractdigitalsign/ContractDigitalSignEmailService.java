package ru.croc.ugd.ssr.service.contractdigitalsign;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.config.contractappointment.ContractAppointmentProperties;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSign;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignData;
import ru.croc.ugd.ssr.contractdigitalsign.Employee;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.service.UserService;
import ru.croc.ugd.ssr.service.document.ContractDigitalSignDocumentService;
import ru.reinform.cdp.ldap.model.UserBean;
import ru.reinform.cdp.mail.api.MailRestApi;
import ru.reinform.cdp.mail.model.data.PersonalAddress;
import ru.reinform.cdp.mail.model.rest.MailRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class ContractDigitalSignEmailService {

    private final MailRestApi mailRestApi;
    private final UserService userService;
    private final ContractAppointmentProperties contractAppointmentProperties;
    private final ContractDigitalSignDocumentService contractDigitalSignDocumentService;

    public void sendContractDigitalSignRemainderEmail() {
        log.debug("sendContractDigitalSignRemainderEmail started");

        final String signingContractRole = ofNullable(contractAppointmentProperties.getElectronicSign())
            .map(ContractAppointmentProperties.ElectronicSign::getSigningContractRole)
            .orElse(null);
        final String signingContractTaskLink = ofNullable(contractAppointmentProperties.getElectronicSign())
            .map(ContractAppointmentProperties.ElectronicSign::getSigningContractTaskLink)
            .orElse(null);

        final String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        final List<ContractDigitalSignDocument> documents = contractDigitalSignDocumentService.fetchAllForToday();

        if (hasTodayDigitalSignsWithEmptyEmployeeLogin(documents)) {
            log.debug("sendContractDigitalSignRemainderEmail sendEmailByRole");
            sendEmailByRole(signingContractRole, signingContractTaskLink, currentDate);
        } else {
            log.debug("sendContractDigitalSignRemainderEmail sendEmailByDocuments");
            sendEmailByDocuments(documents, signingContractTaskLink, currentDate);
        }

        log.debug("sendContractDigitalSignRemainderEmail finished");
    }

    private void sendEmailByRole(final String role, final String link, final String date) {
        final List<UserBean> users = userService.getUsersByRole(role);

        users.stream()
            .filter(user -> nonNull(user.getMail()))
            .forEach(user -> sendMail(user, link, date));
    }

    private void sendEmailByDocuments(
        final List<ContractDigitalSignDocument> documents, final String link, final String date
    ) {
        documents.stream()
            .map(ContractDigitalSignDocument::getDocument)
            .map(ContractDigitalSign::getContractDigitalSignData)
            .map(ContractDigitalSignData::getEmployee)
            .map(Employee::getLogin)
            .map(userService::getUserBeanByLogin)
            .filter(user -> nonNull(user.getMail()))
            .forEach(user -> sendMail(user, link, date));
    }

    private boolean hasTodayDigitalSignsWithEmptyEmployeeLogin(final List<ContractDigitalSignDocument> documents) {
        final boolean hasEmptyEmployee = documents
            .stream()
            .map(ContractDigitalSignDocument::getDocument)
            .map(ContractDigitalSign::getContractDigitalSignData)
            .map(ContractDigitalSignData::getEmployee)
            .anyMatch(Objects::isNull);
        final boolean hasEmptyLogin = documents
            .stream()
            .map(ContractDigitalSignDocument::getDocument)
            .map(ContractDigitalSign::getContractDigitalSignData)
            .map(ContractDigitalSignData::getEmployee)
            .filter(Objects::nonNull)
            .map(Employee::getLogin)
            .anyMatch(Objects::isNull);
        return hasEmptyEmployee || hasEmptyLogin;
    }

    private void sendMail(final UserBean user, final String link, final String date) {
        final MailRequest mailRequest = new MailRequest();
        mailRequest.setSubject("Подписать договоры");
        mailRequest.setBody(String.format("Добрый день, %s!<br><br>"
                + "Сегодня, %s, вам необходимо подписать договоры "
                + "на предоставляемые равнозначные жилые квартиры. "
                + "Перейдите по ссылке: %s",
            user.getDisplayName(),
            date,
            link
        ));
        sendMail(mailRequest, user.getMail(), user.getFioShort());
    }

    private void sendMail(final MailRequest mailRequest, final String emailTo, final String fio) {
        final PersonalAddress to = new PersonalAddress(emailTo, fio);
        mailRequest.setFrom(new PersonalAddress("noreply@smart.mos.ru", "UGD SSR"));
        mailRequest.setTo(Collections.singletonList(to));
        try {
            mailRestApi.sendMail(mailRequest);
            log.info("sendContractDigitalSignRemainderEmail: was send to: {}", emailTo);
        } catch (Exception e) {
            log.error("sendContractDigitalSignRemainderEmail: unable to send to: {}", emailTo, e);
        }
    }
}
