package ru.croc.ugd.ssr.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import ru.reinform.cdp.security.holder.AuthUserData;
import ru.reinform.cdp.security.holder.UserRepository;
import ru.reinform.cdp.utils.rest.utils.ISendRestUtils;
import ru.reinform.cdp.utils.springboot.cache.CacheKeyIgnore;

import javax.annotation.Nonnull;

//TODO Should be removed as soon as dev environment is migrated to CDP 5.0
@Slf4j
@Primary
@Repository
@Profile("dev")
public class SsrUserRepository extends UserRepository {

    @Value("${auth.url}")
    private String authServerUrl;

    @Override
    @Cacheable(
        cacheNames = {"sys_UserRepository_find_v1"},
        key = "#login"
    )
    public AuthUserData find(@CacheKeyIgnore @Nonnull ISendRestUtils sendRestUtils, @Nonnull String login) {
        log.trace("****** User not found in cache: {}. Calling auth.", login);
        return sendRestUtils
            .sendJsonRequest(this.authServerUrl + "/stateless", HttpMethod.POST, null, AuthUserData.class);
    }
}
