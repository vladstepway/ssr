package ru.croc.ugd.ssr.filter;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.reinform.cdp.security.utils.RiAuthenticationUtils;
import ru.reinform.cdp.utils.rest.utils.AuthHeadersUtils;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    private final RiAuthenticationUtils riAuthenticationUtils;
    private final AuthHeadersUtils authHeadersUtils;
    @Value("${app.security.permitAll:false}")
    private boolean isPermitAllRequests;

    @Override
    protected void doFilterInternal(
        final @NotNull HttpServletRequest request,
        final @NotNull HttpServletResponse response,
        final @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (!isAsyncDispatch(request)
            && !this.authHeadersUtils.hasAuthorizationHeader()
            && isPermitAllRequests
        ) {
            riAuthenticationUtils.setSecurityContextByServiceuser();
        }
        filterChain.doFilter(request, response);
    }
}
