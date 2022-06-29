package ru.croc.ugd.ssr.filter;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ru.croc.ugd.ssr.config.RequestResponseLoggingProperties;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@AllArgsConstructor
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private final RequestResponseLoggingProperties requestResponseLoggingProperties;

    @Override
    protected void doFilterInternal(
        final @NotNull HttpServletRequest request,
        final @NotNull HttpServletResponse response,
        final @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
        }
    }

    private void doFilterWrapped(
        final ContentCachingRequestWrapper request,
        final ContentCachingResponseWrapper response,
        final FilterChain filterChain
    ) throws ServletException, IOException {
        final StringBuilder msg = new StringBuilder();
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (logger.isDebugEnabled()) {
                logRequest(request, msg);
                logResponse(response, msg);
                logger.debug(msg.toString());
            } else if (checkUri(request.getRequestURI())) {
                logRequest(request, msg);
                logResponse(response, msg);
                logger.info(msg.toString());
            }
            response.copyBodyToResponse();
        }
    }

    private static void logRequest(final ContentCachingRequestWrapper request, final StringBuilder msg) {
        msg.append("\nRequest url ")
            .append(request.getRequestURL())
            .append("\n");
        final Map<String, String[]> parameters = request.getParameterMap();
        if (!parameters.isEmpty()) {
            logParameters(parameters, msg);
        }
        final String userSsoId = request.getHeader("iv-user");
        if (StringUtils.isNotBlank(userSsoId)) {
            msg.append("Request iv-user header: ")
                .append(userSsoId)
                .append("\n");
        }
        final byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            msg.append("Request body:\n");
            logContent(content, request.getCharacterEncoding(), msg);
        }
    }

    private static void logResponse(final ContentCachingResponseWrapper response, final StringBuilder msg) {
        final int status = response.getStatus();
        msg.append(
            String.format("Response status: %s %s",
                status,
                HttpStatus.valueOf(status).getReasonPhrase()))
            .append("\n");
        msg.append("Response body:\n");
        response.getHeaderNames()
            .forEach(headerName ->
                response.getHeaders(headerName)
                    .forEach(headerValue ->
                        msg.append(String.format("%s: %s", headerName, headerValue))
                            .append("\n")));
        final byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, response.getCharacterEncoding(), msg);
        }
    }

    private static void logParameters(final Map<String, String[]> parameters, final StringBuilder msg) {
        msg.append("Request params:");
        parameters.forEach((key, values) ->
            msg.append(key)
                .append(": ")
                .append(String.join(", ", values))
                .append("; "));
    }

    private static void logContent(final byte[] content, final String contentEncoding, final StringBuilder msg) {
        try {
            final String contentString = new String(content, contentEncoding);
            msg.append(contentString.replace("\r\n", " "))
                .append("\n");
        } catch (final UnsupportedEncodingException e) {
            msg.append(
                String.format("[%d bytes content]", content.length))
                .append("\n");
        }
    }

    private static ContentCachingRequestWrapper wrapRequest(final HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private static ContentCachingResponseWrapper wrapResponse(final HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }

    private boolean checkUri(final String requestUri) {
        return requestResponseLoggingProperties.isEnabled()
            && (!requestResponseLoggingProperties.getUriFilter().isEnabled()
            || ofNullable(requestResponseLoggingProperties.getUriFilter().getValues())
            .orElse(Collections.emptyList())
            .stream()
            .anyMatch(requestUri::startsWith));
    }

}
