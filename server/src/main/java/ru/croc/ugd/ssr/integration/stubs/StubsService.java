package ru.croc.ugd.ssr.integration.stubs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.MethodsInformingProperties;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.model.integration.etpmv.Department;
import ru.croc.ugd.ssr.model.integration.etpmv.ObjectFactory;
import ru.croc.ugd.ssr.model.integration.etpmv.Person;
import ru.croc.ugd.ssr.model.integration.etpmv.ServiceProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBElement;

/**
 * Сервис для заглушек.
 */
@Service
@RequiredArgsConstructor
public class StubsService {

    private final XmlUtils xmlUtils;
    private final MethodsInformingProperties methodsInformingProperties;

    /**
     * Создание департамента.
     *
     * @return департамент
     */
    public Department createStubDepartment() {
        final Department department = new Department();
        department.setName("Департамент градостроительной политики г. Москвы");
        department.setCode("2591");
        department.setInn("7703742961");
        department.setOgrn("1117746321219");
        department.setRegDate(xmlUtils.getXmlDateNow());
        ObjectFactory fact = new ObjectFactory();
        JAXBElement<String> systemCode = fact.createDepartmentSystemCode("9000154");
        department.setSystemCode(systemCode);
        return department;
    }

    /**
     * Создание Person.
     *
     * @return person
     */
    public Person createPerson() {
        final Person person = new Person();
        person.setLastName("Гнездилов");
        person.setFirstName("Роман");
        person.setJobTitle("Советник отдела информационной безопасности");
        return person;
    }

    /**
     * Create ServiceProperties.MethodInforming.
     *
     * @return ServiceProperties.MethodInforming
     */
    public ServiceProperties.MethodInforming createServicePropertiesMethodInforming() {
        return createServicePropertiesMethodInforming(null);
    }

    /**
     * Create ServiceProperties.MethodInforming.
     *
     * @param notificationCode notificationCode
     * @return ServiceProperties.MethodInforming
     */
    public ServiceProperties.MethodInforming createServicePropertiesMethodInforming(final String notificationCode) {
        final ServiceProperties.MethodInforming methodInforming = new ServiceProperties.MethodInforming();
        final List<Integer> values = methodInforming.getValue();

        final List<Integer> methodInformingCodes = getMethodsInformingCodes(notificationCode);
        values.addAll(methodInformingCodes);

        return methodInforming;
    }

    private List<Integer> getMethodsInformingCodes(final String notificationCode) {
        final Map<String, String> codes = methodsInformingProperties.getNotificationCodes();
        final String methodsInforming = codes.containsKey(notificationCode)
            ? codes.get(notificationCode)
            : methodsInformingProperties.getDefaultMethods();

        return Arrays.stream(methodsInforming.split(","))
            .map(Integer::valueOf)
            .collect(Collectors.toList());
    }

}
