package ru.croc.ugd.ssr.service.rsm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.mqetpmv.flow.ExportFlowMessageDto;
import ru.croc.ugd.ssr.dto.rsm.RsmRequestDto;
import ru.croc.ugd.ssr.dto.rsm.RsmResponseDto;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.croc.ugd.ssr.integration.eno.EnoCreator;
import ru.croc.ugd.ssr.integration.eno.EnoSequenceCode;
import ru.croc.ugd.ssr.integration.mqetpmv.MqetpmvProperties;
import ru.croc.ugd.ssr.integration.service.mqetpmv.flow.SsrMqetpmvFlowService;
import ru.croc.ugd.ssr.integration.util.MessageUtils;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.rsm.Filter;
import ru.croc.ugd.ssr.rsm.ObjectResponseType;
import ru.croc.ugd.ssr.rsm.RsmObjectRequest;
import ru.croc.ugd.ssr.rsm.RsmObjectResponse;
import ru.croc.ugd.ssr.service.document.RsmObjectRequestLogDocumentService;
import ru.croc.ugd.ssr.utils.JsonMapperUtil;
import ru.reinform.cdp.utils.rest.utils.SendRestUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * DefaultRsmObjectService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRsmObjectService implements RsmObjectService {

    private static final String EMPTY_RESPONSE =
        "По данному фильтру не найдено ни одного объекта капитального строительства!";
    private static final int PAGE_SIZE = 50;

    private final RsmObjectRequestLogDocumentService rsmObjectRequestLogDocumentService;
    private final MessageUtils messageUtils;
    private final IntegrationProperties integrationProperties;
    private final SendRestUtils restUtils;
    private final EnoCreator enoCreator;
    private final IntegrationPropertyConfig propertyConfig;
    private final SsrMqetpmvFlowService ssrMqetpmvFlowService;
    private final MqetpmvProperties mqetpmvProperties;

    @Value("${common.playground.url}")
    private String playgroundUrl;

    @Override
    public void processRequest(final RsmRequestDto request) {
        log.info(
            "RsmObjectService: start request processing: eno = {}, etpmvMessageId = {}",
            request.getRequestEno(),
            request.getEtpmvMessageId()
        );

        final String rsmObjectRequestLogDocumentId = rsmObjectRequestLogDocumentService
            .startProcessRequest(request)
            .getId();

        try {
            final RsmObjectResponse rsmObjectResponse = sendRsmObjectRequestToPs(request.getRsmObjectRequest());
            log.info(
                "RsmObjectService: request sent to PS: responseObjectSize = {}",
                rsmObjectResponse.getObjectResponse().size()
            );
            final RsmResponseDto response = sendResponseToEtpmv(rsmObjectResponse);
            rsmObjectRequestLogDocumentService.endProcessRequest(rsmObjectRequestLogDocumentId, response);
        } catch (Exception e) {
            log.error(
                "RsmObjectService: unable to process request: eno = {}, etpmvMessageId = {}",
                request.getRequestEno(),
                request.getEtpmvMessageId(),
                e
            );
            rsmObjectRequestLogDocumentService.failProcessRequest(rsmObjectRequestLogDocumentId);
        }

        log.info(
            "RsmObjectService: finish request processing: eno = {}, etpmvMessageId = {}",
            request.getRequestEno(),
            request.getEtpmvMessageId()
        );
    }

    private RsmObjectResponse sendRsmObjectRequestToPs(final RsmObjectRequest rsmObjectRequest) throws IOException {
        //TODO: Konstantin, Alexander: возможно стоит сделать асинхронную обработку заявок

        final RsmObjectResponse rsmObjectResponse = new RsmObjectResponse();

        final String filters = buildRequestFilters(rsmObjectRequest.getFilters());

        log.info("RsmObjectService: Filters: {}", filters);

        int pageNum = 0;
        List<ObjectResponseType> objectResponseList = sendRsmObjectRequestToPs(filters, pageNum);

        while (objectResponseList.size() > 0) {
            rsmObjectResponse.getObjectResponse().addAll(objectResponseList);
            pageNum++;
            objectResponseList = sendRsmObjectRequestToPs(filters, pageNum);
        }

        return rsmObjectResponse;
    }

    private List<ObjectResponseType> sendRsmObjectRequestToPs(
        final String filter, final int pageNum
    ) throws IOException {
        final String result = restUtils.sendJsonRequest(
            playgroundUrl + integrationProperties.getPsFindObjectsUsingXmlFilter(),
            HttpMethod.POST,
            filter,
            String.class,
            pageNum,
            PAGE_SIZE
        );
        log.info("RsmObjectService: sendRsmObjectRequestToPS result: {}", result);

        if (EMPTY_RESPONSE.equals(result)) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(JsonMapperUtil.getMapper().readValue(result, ObjectResponseType[].class));
        }
    }

    private String buildRequestFilters(final List<Filter> filters) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<filters>").append('\n');
        for (Filter filter : filters) {
            sb.append("<filter attr_name = \"")
                .append(filter.getAttrName())
                .append("\" filt_type = \"")
                .append(filter.getFiltType())
                .append("\">")
                .append(filter.getValue())
                .append("</filter>")
                .append('\n');
        }
        sb.append("</filters>");
        return sb.toString();
    }

    private RsmResponseDto sendResponseToEtpmv(final RsmObjectResponse rsmObjectResponse) {
        final String eno = enoCreator.generateEtpMvEnoNumber(
            propertyConfig.getRsmObjectResponse(), EnoSequenceCode.UGD_SSR_ENO_RSM_OBJECT_RESPONSE_SEQ
        );
        final String message = messageUtils.createCoordinateTaskMessage(eno, rsmObjectResponse);
        final ExportFlowMessageDto exportFlowMessageDto = ssrMqetpmvFlowService.sendFlowMessage(
            mqetpmvProperties.getRsmObjectResponseStatusOutProfile(),
            message,
            eno,
            propertyConfig.getRsmObjectResponse(),
            IntegrationFlowType.DGP_TO_DGI_RSM_OBJECT_RESPONSE,
            integrationProperties.getXmlExportRsmObject()
        );
        return RsmResponseDto.builder()
            .etpmvMessageId(exportFlowMessageDto.getEtpMessageId())
            .responseEno(eno)
            .rsmObjectResponse(rsmObjectResponse)
            .build();
    }
}
