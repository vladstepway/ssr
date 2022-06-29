package ru.croc.ugd.ssr.integration.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.reinform.cdp.utils.core.RIXmlUtils;

import java.time.LocalDate;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Сервис по интеграции с АСУР.
 */
@Service
@RequiredArgsConstructor
public class AsurIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(AsurIntegrationService.class);

    private final EnoCreator enoCreator;
    private final IntegrationPropertyConfig config;
    private final XmlUtils xmlUtils;

    /**
     * Создание xml документа getApartmentData для оправки в модуль ПСО.
     *
     * @param flat       квартира
     * @param unomString unom дома
     * @return xml документ
     */
    public String createGetApartmentDataRequestXml(FlatType flat, String unomString) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element root = document.createElement("getApartmentData");
            document.appendChild(root);

            String eno = enoCreator.generateAsurEnoNumber();
            Element serviceProperties = fillCommonParamXml(document, root, eno);

            Element room = document.createElement("address1_line3");
            room.appendChild(document.createTextNode(flat.getApartmentL4VALUE()));
            serviceProperties.appendChild(room);

            Element unom = document.createElement("unom");
            unom.appendChild(document.createTextNode(unomString));
            serviceProperties.appendChild(unom);

            flat.setIntegrationNumber(eno);
            flat.setUpdatedFromEZDdate(LocalDate.now());
            flat.setUpdatedFullDate(LocalDate.now());
            flat.setUpdatedFromEZDstatus("в процессе обогащения");
            flat.setUpdatedFullStatus("в процессе обогащения");

            return xmlUtils.xmlDocumentToString(document);
        } catch (ParserConfigurationException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    /**
     * Создание xml документа getSNILS для оправки в модуль ПСО.
     *
     * @param personData       данные жителя
     * @param gendercodeString пол
     * @return id отпавленных сообщений.
     */
    public String createGetSnilsRequestXml(PersonType personData, String gendercodeString) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element root = document.createElement("getSNILS");
            document.appendChild(root);

            String eno = enoCreator.generateAsurEnoNumber();
            if (gendercodeString.equalsIgnoreCase("1")) {
                personData.setIntegrationNumberMale(eno);
            } else {
                personData.setIntegrationNumberFemale(eno);
            }
            String updatedFromPfrStatus = personData.getUpdatedFromPFRstatus();
            if (!"в процессе обогащения".equals(updatedFromPfrStatus)
                && !"Превышен суточный лимит запросов на документ".equals(updatedFromPfrStatus)) {
                personData.setUpdatedFromPFRdate(LocalDate.now());
            }
            personData.setUpdatedFromPFRstatus("в процессе обогащения");

            Element serviceProperties = fillCommonParamXml(document, root, eno);

            String fio = personData.getFIO().trim();
            while (fio.contains("  ")) {
                fio = fio.replaceAll(" {2}", " ");
            }
            String[] names = fio.split(" ");

            Element birthdate = document.createElement("birthdate");
            birthdate.appendChild(document.createTextNode(
                RIXmlUtils.toXmlDateTime(personData.getBirthDate().atStartOfDay()).toString())
            );
            serviceProperties.appendChild(birthdate);

            Element firstname = document.createElement("firstname");
            firstname.appendChild(document.createTextNode(names[1]));
            serviceProperties.appendChild(firstname);

            Element gendercode = document.createElement("gendercode");
            gendercode.appendChild(document.createTextNode(gendercodeString));
            serviceProperties.appendChild(gendercode);

            Element lastname = document.createElement("lastname");
            lastname.appendChild(document.createTextNode(names[0]));
            serviceProperties.appendChild(lastname);

            if (names.length > 2) {
                StringBuilder middle = new StringBuilder();
                for (int i = 2; i < names.length; i++) {
                    if (!names[i].equals("-")) {
                        middle.append(names[i]).append(" ");
                    }
                }
                if (!middle.toString().trim().equals("")) {
                    Element middlename = document.createElement("middlename");
                    middlename.appendChild(document.createTextNode(middle.toString().trim()));
                    serviceProperties.appendChild(middlename);
                }
            }

            return xmlUtils.xmlDocumentToString(document);
        } catch (ParserConfigurationException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    /**
     * Создание общей части XML документа.
     *
     * @param document xml документ
     * @param root     верхнеуровневый элемент
     */
    private Element fillCommonParamXml(Document document, Element root, String eno) {
        Element includeBinary = document.createElement("includeBinaryView");
        includeBinary.appendChild(document.createTextNode("false"));
        root.appendChild(includeBinary);

        Element serviceNumber = document.createElement("serviceNumber");
        serviceNumber.appendChild(document.createTextNode(eno));
        root.appendChild(serviceNumber);

        Element orgProfile = document.createElement("orgProfile");
        orgProfile.appendChild(document.createTextNode("UGD"));
        root.appendChild(orgProfile);

        Element serviceTypeCode = document.createElement("serviceTypeCode");
        serviceTypeCode.appendChild(document.createTextNode(config.getAsurEnoService()));
        root.appendChild(serviceTypeCode);

        Element serviceProperties = document.createElement("ServiceProperties");
        root.appendChild(serviceProperties);

        return serviceProperties;
    }

}
