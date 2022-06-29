package ru.croc.ugd.ssr.integration.service.wsdl.flat;

import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import ru.croc.ugd.ssr.config.SoapMosruProperties;
import ru.croc.ugd.ssr.integration.service.wsdl.CredentialsCallback;
import ru.croc.ugd.ssr.integration.service.wsdl.flat.model.FlatDetails;
import ru.croc.ugd.ssr.wsdl.EhdCatalogItem;
import ru.croc.ugd.ssr.wsdl.EhdCatalogRow;
import ru.croc.ugd.ssr.wsdl.GetCatalogItems;
import ru.croc.ugd.ssr.wsdl.GetCatalogItemsResponse;
import ru.croc.ugd.ssr.wsdl.ObjectFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBElement;

@AllArgsConstructor
public class FetchFlatDetailsSoapService extends WebServiceGatewaySupport {

    private final int flatCatalogId = 28609;
    private final String soapEndpointUrl = "https://op.mos.ru:443/EHDWS/soap";
    private final ObjectFactory objectFactory = new ObjectFactory();

    private SoapMosruProperties soapMosruProperties;

    public List<FlatDetails> getFlatDetailsByUnom(String unom) {
        GetCatalogItems request = createGetCatalogItemsRequestForFlats(unom);
        return executeRequest(request);
    }

    public List<FlatDetails> getFlatDetailsByGlobalId(String globalId) {
        GetCatalogItems request = getCatalogRequest();
        request.setIdGlobalObject(Long.valueOf(globalId));
        return executeRequest(request);
    }

    private List<FlatDetails> executeRequest(GetCatalogItems request) {
        JAXBElement<GetCatalogItemsResponse> response = (JAXBElement<GetCatalogItemsResponse>) getWebServiceTemplate()
                .marshalSendAndReceive(soapEndpointUrl,
                        objectFactory.createGetCatalogItems(request),
                        new CredentialsCallback(soapMosruProperties.getUsername(), soapMosruProperties.getPassword()));

        return parseServiceResponse(response.getValue());
    }

    private GetCatalogItems createGetCatalogItemsRequestForFlats(String unom) {
        GetCatalogItems request = getCatalogRequest();
        request.setFilters(getUnomRequestFilter(unom));
        return request;
    }

    private GetCatalogItems getCatalogRequest() {
        GetCatalogItems request = new GetCatalogItems();
        request.setIdCatalog(flatCatalogId);
        request.setStart(1);
        request.setEnd(1000);
        return request;
    }

    private List<FlatDetails> parseServiceResponse(GetCatalogItemsResponse response) {
        if (response == null
                || response.getEhdCatalogItemsset() == null
                || CollectionUtils.isEmpty(response.getEhdCatalogItemsset().getEhdCatalogItem())) {
            return Collections.emptyList();
        }
        return response
                .getEhdCatalogItemsset()
                .getEhdCatalogItem()
                .stream()
                .filter(Objects::nonNull)
                .map(this::mapFromEhdCatalogRow)
                .collect(Collectors.toList());
    }

    private FlatDetails mapFromEhdCatalogRow(EhdCatalogRow ehdCatalogRow) {
        final FlatDetails flatDetails = new FlatDetails();
        if (CollectionUtils.isEmpty(ehdCatalogRow.getEhdCatalogAttr())) {
            return flatDetails;
        }
        ehdCatalogRow
                .getEhdCatalogAttr()
                .forEach(ehdCatalogItem -> populateCatalogItemValueToFlatDetails(ehdCatalogItem, flatDetails));
        return flatDetails;
    }

    /**
     * Идентификатор.
     * Уном.
     * Кол-во жилых комнат в квартире.
     * Номер квартиры.
     * Номер этажа квартиры.
     * Общая площадь.
     * Приведенная площадь(с учетом лоджий и балконов).
     * Тип помещения.
     * Номер квартиры.
     * Жилая площадь.
     */
    private void populateCatalogItemValueToFlatDetails(final EhdCatalogItem ehdCatalogItem,
                                                       final FlatDetails flatDetails) {
        final String tehName = extractStringValue(ehdCatalogItem.getTehName());
        final String value = extractStringValue(ehdCatalogItem.getValue());
        switch (tehName) {
            case "BAC_ID":
                flatDetails.setAddressIdentification(value);
                return;
            case "BAC_UNOM":
                flatDetails.setUnom(value);
                return;
            case "BAC_KMQ":
                flatDetails.setAmountOfLivingRooms(value);
                return;
            case "BAC_UNKV":
                flatDetails.setUniqueFlatNumber(value);
                return;
            case "BAC_ET":
                flatDetails.setFloor(value);
                return;
            case "BAC_OPL":
                flatDetails.setFullSquare(value);
                return;
            case "BAC_PPL":
                flatDetails.setCalculatedSquare(value);
                return;
            case "BAC_TP":
                flatDetails.setFlatTypeExternalRefId(value);
                return;
            case "BAC_KVNOM":
                flatDetails.setFlatNumber(value);
                return;
            case "BAC_GPL":
                flatDetails.setLivingSquare(value);
                return;
            case "BAC_NSEK":
                flatDetails.setSectionNumber(value);
                return;
            default:
                return;
        }
    }

    private String extractStringValue(JAXBElement jaxbElement) {
        if (jaxbElement == null) {
            return null;
        }
        final Object objectValue = jaxbElement.getValue();
        return String.valueOf(objectValue);
    }

    private String getUnomRequestFilter(String unom) {
        return "<filters><filter id_attr=\"1139006\" filt_type=\"4\">" + unom + "</filter></filters>";
    }
}
