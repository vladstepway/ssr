package ru.croc.ugd.ssr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import ru.reinform.cdp.security.config.BaseSecurityConfig;

/**
 * Конфигурация аутентификации.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends BaseSecurityConfig {
    @Value("${app.security.enabled:true}")
    private boolean securityEnabled;
    @Value("${app.security.permitAll:false}")
    private boolean isPermitAllRequests;

    @Override
    protected HttpSecurity authorizeRequests(HttpSecurity httpSecurity) throws Exception {
        httpSecurity = httpSecurity.cors().and();
        httpSecurity = configureShippingControllerSecurity(httpSecurity);
        if (securityEnabled) {
            return super.authorizeRequests(httpSecurity);
        }

        return httpSecurity.authorizeRequests().antMatchers("/**").permitAll().and();
    }

    private HttpSecurity configureShippingControllerSecurity(HttpSecurity httpSecurity)
            throws Exception {
        if (isPermitAllRequests) {
            return httpSecurity.authorizeRequests()
                    .antMatchers("/**")
                    .permitAll()
                    .and();
        }
        return httpSecurity;
    }
}
