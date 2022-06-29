package ru.croc.ugd.ssr.ldap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;
import ru.reinform.cdp.ldap.config.LdapProperties;
import ru.reinform.cdp.ldap.core.AttributesMapperWithFilter;
import ru.reinform.cdp.ldap.core.LdapContextUserMapper;
import ru.reinform.cdp.ldap.core.LdapResultOrganizationMapper;
import ru.reinform.cdp.ldap.core.LdapResultUserMapper;
import ru.reinform.cdp.ldap.core.LdapResultUserPhotoMapper;
import ru.reinform.cdp.ldap.dao.impl.OpenLdapDaoImpl;
import ru.reinform.cdp.ldap.model.UserAttribute;
import ru.reinform.cdp.ldap.model.UserBean;
import ru.reinform.cdp.ldap.service.LdapMonitoringService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Primary
@Slf4j
public class SsrOpenLdapDao extends OpenLdapDaoImpl {
    private final LdapProperties ldapProperties;
    private final LdapTemplate ldapTemplate;

    public SsrOpenLdapDao(LdapTemplate ldapTemplate,
                          LdapProperties ldapProperties,
                          LdapResultOrganizationMapper orgMapper,
                          LdapResultUserPhotoMapper photoMapper,
                          LdapMonitoringService monitoringService) {
        super(ldapTemplate, ldapProperties, orgMapper, photoMapper, monitoringService);
        this.ldapProperties = ldapProperties;
        this.ldapTemplate = ldapTemplate;
    }

    public List<UserBean> getUserByBaseAndLoginLike(String base, String login, UserAttribute... userAttributes) {
        try {
            List<UserAttribute> attributes =
                userAttributes != null ? Arrays.asList(userAttributes) : Collections.emptyList();
            AttributesMapperWithFilter<UserBean> userMapper =
                new LdapResultUserMapper(this.ldapProperties, attributes);
            String loginForSearch = "*" + login + "*";
            LdapQuery query = LdapQueryBuilder.query().base(base)
                .attributes(userMapper.attributesFilter())
                .where("objectclass").is("person")
                .and("uid").like(loginForSearch);
            LdapContextUserMapper contextUserMapper
                = this.getContextUserMapper(userMapper);
            return this.ldapTemplate.search(query, contextUserMapper);
        } catch (NameNotFoundException var9) {
            log.warn("Wrong DN: " + base);
            return Collections.emptyList();
        }
    }

    private LdapContextUserMapper getContextUserMapper(AttributesMapper<UserBean> userMapper) {
        return new LdapContextUserMapper(userMapper);
    }
}
