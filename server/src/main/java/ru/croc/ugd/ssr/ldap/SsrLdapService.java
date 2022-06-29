package ru.croc.ugd.ssr.ldap;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.reinform.cdp.ldap.model.UserAttribute;
import ru.reinform.cdp.ldap.model.UserBean;
import ru.reinform.cdp.ldap.service.LdapService;
import ru.reinform.cdp.ldap.utils.UsersUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class SsrLdapService extends LdapService {
    private SsrOpenLdapDao ldapDao;

    public SsrLdapService(SsrOpenLdapDao ldapDao) {
        super(ldapDao);
        this.ldapDao = ldapDao;
    }

    public List<UserBean> getUsersByLoginLike(String login, UserAttribute... userAttributes) {
        return this.ldapDao.getBaseList()
            .stream()
            .map(base -> ldapDao.getUserByBaseAndLoginLike(base, login, userAttributes))
            .map(UsersUtils::removeDuplicates)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }
}
