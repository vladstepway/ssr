package ru.croc.ugd.ssr.integration.util;

import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.stubs.StubsService;
import ru.croc.ugd.ssr.model.integration.etpmv.ArrayOfBaseDeclarant;
import ru.croc.ugd.ssr.model.integration.etpmv.CitizenshipType;
import ru.croc.ugd.ssr.model.integration.etpmv.ContactType;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateStatusMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.Department;
import ru.croc.ugd.ssr.model.integration.etpmv.DictionaryItem;
import ru.croc.ugd.ssr.model.integration.etpmv.ObjectFactory;
import ru.croc.ugd.ssr.model.integration.etpmv.OutputKindType;
import ru.croc.ugd.ssr.model.integration.etpmv.Person;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestContact;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestService;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestStatus;
import ru.croc.ugd.ssr.model.integration.etpmv.ServiceProperties;
import ru.croc.ugd.ssr.model.integration.etpmv.StatusType;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskDataType;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskType;

import java.util.Optional;
import java.util.UUID;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * Утильный класс по формированию сообщений в очередь ЕТП.
 */
@Component
@RequiredArgsConstructor
public class MessageUtils {

    private static final String FUNCTION_TYPE_CODE = "067801";

    private final StubsService stubsService;
    private final EnoCreator enoCreator;
    private final XmlUtils xmlUtils;

    /**
     * Формирует конверт для сообщения и добавляет в параметры информацию о переданном объекте.
     *
     * @param any         параметр.
     * @param enoSequence полседовательность для генерации номера сообщения.
     * @param guNumber    номер гу.
     * @return сообщение для очереди.
     */
    public String createCoordinateTaskMessage(String enoSequence, String guNumber, Object any) {
        final String eno =
            enoCreator.generateEtpMvEnoNumber(guNumber, enoSequence);
        return createCoordinateTaskMessage(eno, any);
    }

    /**
     * Формирует конверт для сообщения и добавляет в параметры информацию о переданном объекте.
     *
     * @param eno         eno.
     * @param any         параметр.
     * @return сообщение для очереди.
     */
    public String createCoordinateTaskMessage(final String eno, final Object any) {
        return createCoordinateTaskMessage(eno, any, FUNCTION_TYPE_CODE);
    }

    /**
     * Формирует конверт для сообщения и добавляет в параметры информацию о переданном объекте.
     *
     * @param eno              eno.
     * @param any              параметр.
     * @param functionTypeCode код гос. услуги.
     * @return сообщение для очереди.
     */
    public String createCoordinateTaskMessage(String eno, Object any, String functionTypeCode) {
        final TaskDataType taskDataType = new TaskDataType();

        taskDataType.setDocumentTypeCode("1");
        taskDataType.setIncludeBinaryView(false);
        taskDataType.setIncludeXmlView(false);

        final TaskDataType.Parameter parameter = new TaskDataType.Parameter();
        parameter.setAny(any);
        taskDataType.setParameter(parameter);

        final Department department = stubsService.createStubDepartment();

        final Person person = stubsService.createPerson();

        final TaskType taskType = new TaskType();
        taskType.setTaskNumber(eno);
        taskType.setDepartment(department);
        taskType.setResponsible(person);
        taskType.setMessageId(UUID.randomUUID().toString());
        taskType.setTaskId(UUID.randomUUID().toString());
        taskType.setTaskDate(xmlUtils.getXmlDateNow());
        taskType.setFunctionTypeCode(functionTypeCode);

        final CoordinateTaskData coordinateTaskData = new CoordinateTaskData();
        coordinateTaskData.setData(taskDataType);
        coordinateTaskData.setTask(taskType);
        coordinateTaskData.setSignature(null);

        final CoordinateTaskMessage taskMessage = new CoordinateTaskMessage();
        taskMessage.setCoordinateTaskDataMessage(coordinateTaskData);

        return xmlUtils.transformObjectToXmlString(taskMessage,
            CoordinateTaskMessage.class,
            ObjectFactory.class,
            any.getClass());
    }

    /**
     * Сформировать пустой CoordinateMessage для уведомлений.
     *
     * @return CoordinateMessage
     */
    public CoordinateMessage createEmptyCoordinateMessageForNotification() {
        return createEmptyCoordinateMessageForNotification("Помощь при переезде в рамках Программы реновации");
    }

    /**
     * Сформировать пустой CoordinateMessage для уведомлений.
     * @param notificationTitle notificationTitle
     * @return CoordinateMessage
     */
    public CoordinateMessage createEmptyCoordinateMessageForNotification(final String notificationTitle) {
        return createEmptyCoordinateMessageForNotification(notificationTitle, null);
    }

    /**
     * Сформировать пустой CoordinateMessage для уведомлений.
     * @param notificationTitle notificationTitle
     * @param notificationCode notificationCode
     * @return CoordinateMessage
     */
    public CoordinateMessage createEmptyCoordinateMessageForNotification(
        final String notificationTitle, final String notificationCode
    ) {
        RequestStatus status = new RequestStatus();

        StatusType statusType = new StatusType();
        statusType.setStatusDate(xmlUtils.getXmlDateNow());
        statusType.setStatusTitle("");
        status.setStatus(statusType);

        RequestServiceForSign requestServiceForSign = new RequestServiceForSign();
        DictionaryItem serviceType = new DictionaryItem();
        serviceType.setName(notificationTitle);
        requestServiceForSign.setServiceType(serviceType);
        requestServiceForSign.setId("");
        RequestContact contact = new RequestContact();
        contact.setType(ContactType.DECLARANT);
        contact.setBirthDate(null);
        contact.setCitizenshipType(CitizenshipType.RF);

        ArrayOfBaseDeclarant contracts = new ArrayOfBaseDeclarant();
        contracts.getBaseDeclarant().add(contact);
        requestServiceForSign.setContacts(contracts);

        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setUnsubscribeLink(
            "https://robot.mos.ru/l/unsubscribe/?muid=164b0b6a-d6f4-4d05-8250-ee2932437e85&category=0429a93a-4609-467d-aca5-6b953a6a021d"
        );
        serviceProperties.setEventLink("setEventLink");
        serviceProperties.setMethodInforming(stubsService.createServicePropertiesMethodInforming(notificationCode));

        RequestServiceForSign.CustomAttributes customAttributes = new RequestServiceForSign.CustomAttributes();
        customAttributes.setAny(serviceProperties);
        requestServiceForSign.setCustomAttributes(customAttributes);

        RequestService service = new RequestService();
        service.setRegDate(xmlUtils.getXmlDateNow());
        service.setServicePrice(null);
        service.setDepartment(stubsService.createStubDepartment());
        service.setCreatedByDepartment(stubsService.createStubDepartment());
        service.setOutputKind(OutputKindType.PORTAL);

        CoordinateData coordinateData = new CoordinateData();
        coordinateData.setService(service);
        coordinateData.setSignService(requestServiceForSign);
        coordinateData.setStatus(status);

        CoordinateMessage coordinateMessage = new CoordinateMessage();
        coordinateMessage.setCoordinateDataMessage(coordinateData);
        return coordinateMessage;
    }

    /**
     * Retrieve statusCode.
     * @param coordinateStatusMessage coordinateStatusMessage
     * @return statusCode
     */
    public Optional<Integer> retrieveStatusCode(final CoordinateStatusMessage coordinateStatusMessage) {
        return ofNullable(coordinateStatusMessage)
            .map(CoordinateStatusMessage::getCoordinateStatusDataMessage)
            .map(CoordinateStatusData::getStatus)
            .map(StatusType::getStatusCode);
    }

    /**
     * Retrieve statusReasonCode.
     * @param coordinateStatusMessage coordinateStatusMessage
     * @return statusReasonCode
     */
    public Optional<Integer> retrieveStatusReasonCode(final CoordinateStatusMessage coordinateStatusMessage) {
        return ofNullable(coordinateStatusMessage)
            .map(CoordinateStatusMessage::getCoordinateStatusDataMessage)
            .map(CoordinateStatusData::getReason)
            .map(DictionaryItem::getCode)
            .map(Integer::valueOf);
    }

    /**
     * Retrieve eno.
     * @param coordinateMessage coordinateMessage
     * @return eno
     */
    public Optional<String> retrieveEno(final CoordinateMessage coordinateMessage) {
        return ofNullable(coordinateMessage)
            .map(CoordinateMessage::getCoordinateDataMessage)
            .map(CoordinateData::getService)
            .map(RequestService::getServiceNumber);
    }

    /**
     * Retrieve eno.
     * @param coordinateStatusMessage coordinateStatusMessage
     * @return eno
     */
    public Optional<String> retrieveEno(final CoordinateStatusMessage coordinateStatusMessage) {
        return ofNullable(coordinateStatusMessage)
            .map(CoordinateStatusMessage::getCoordinateStatusDataMessage)
            .map(CoordinateStatusData::getServiceNumber);
    }

}
