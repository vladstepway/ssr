package ru.croc.ugd.ssr.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.config.CacheConfig;
import ru.croc.ugd.ssr.ldap.SsrLdapService;
import ru.reinform.cdp.ldap.model.LdapTableRequestBean;
import ru.reinform.cdp.ldap.model.LogicOperation;
import ru.reinform.cdp.ldap.model.OrganizationBean;
import ru.reinform.cdp.ldap.model.UserAttribute;
import ru.reinform.cdp.ldap.model.UserBean;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Сервис по работе с пользователями платформы.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final SsrLdapService ldapService;

    /**
     * Получает полные ФИО пользователя по логину.
     *
     * @param login логин пользователя (пример - ivanov_ii)
     * @return полные ФИО пользователя
     */
    public String getUserFioByLogin(String login) {
        try {
            UserBean userByLogin = ldapService.getUserByLogin(login);
            if (userByLogin == null) {
                return "";
            }

            return userByLogin.getDisplayName();
        } catch (Exception e) {
            log.info("User full name not found by login {}", login);
            return null;
        }
    }

    /**
     * Поиск пользователей.
     *
     * @param departmentCode код подразделения.
     * @param fio            фио пользователя.
     * @param position       должность.
     * @return список пользователей.
     */
    public List<UserBean> getAllLdapUsers(
        @Nullable String departmentCode, @Nullable String fio, @Nullable String position
    ) {
        LdapTableRequestBean requestBean = new LdapTableRequestBean();
        requestBean.setOnlyActiveUsers(Boolean.TRUE);
        if (StringUtils.isNotEmpty(departmentCode)) {
            requestBean.setDepartment(Collections.singletonList(departmentCode));
        }
        if (StringUtils.isNotEmpty(fio)) {
            requestBean.setFio(fio);
        }
        if (StringUtils.isNotEmpty(position)) {
            requestBean.setPost(Collections.singletonList(position));
        }

        return ldapService.findUsers(requestBean, LogicOperation.AND);
    }

    /**
     * Получение всех пользователей по логинам.
     *
     * @param logins логины.
     * @return список пользователей.
     */
    public List<UserBean> getAllLdapUsers(List<String> logins) {
        return ldapService.getUsersByLogin(
            logins,
            // перечисление нужных атрибутов (сделано для быстродейсвтвия - можно расширить)
            UserAttribute.fio,
            UserAttribute.department,
            UserAttribute.post,
            UserAttribute.mail
        );
    }

    /**
     * Получение всех пользователей по логинам.
     *
     * @param login логины.
     * @return список пользователей.
     */
    public List<UserBean> getAllLdapUsersLoginLike(String login) {
        return ldapService.getUsersByLoginLike(
            login,
            // перечисление нужных атрибутов (сделано для быстродейсвтвия - можно расширить)
            UserAttribute.fio,
            UserAttribute.department,
            UserAttribute.post,
            UserAttribute.mail
        );
    }

    /**
     * Получение всех организаций.
     *
     * @return список организаций
     */
    public List<OrganizationBean> getAllOrganization() {
        return ldapService.getUserOrganizations();
    }

    /**
     * Получение организации пользователя.
     *
     * @param login логин пользователя
     * @return организация
     */
    public OrganizationBean getOrganization(final String login) {
        return nonNull(login)
            ? ldapService.getUserDepartmentBean(login)
            : ldapService.getUserDepartmentBean(getCurrentUserLogin());
    }

    /**
     * Получить ФИО текущего пользователя.
     *
     * @return имя пользователя
     */
    public String getCurrentUserName() {
        return getUserFioByLogin(getCurrentUserLogin());
    }

    /**
     * Получить логин текущего пользователя.
     * @return логин пользователя
     */
    public String getCurrentUserLogin() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof UserDetails
            ? ((UserDetails) principal).getUsername()
            : principal.toString();
    }

    /**
     * Получить пользователей по роли.
     *
     * @param role роль
     * @return пользователи
     */
    public List<UserBean> getUsersByRole(String role) {
        return ldapService.findUsersArray(
            Collections.singletonList(role), null, LogicOperation.OR
        );
    }

    /**
     * Возвращает UserBean по логину.
     *
     * @param login логин
     * @return UserBean
     */
    @Cacheable(value = CacheConfig.ASSIGNEES)
    public UserBean getUserBeanByLogin(String login) {
        return ldapService.getUserByLogin(login);
    }

    /**
     * findUsersByLdapGroup.
     *
     * @param ldapGroup ldapGroup
     * @return List UserBean
     */
    public List<UserBean> findUsersByLdapGroup(final String ldapGroup) {
        return ldapService.findUsers(ldapGroup, null, null);
    }

    /**
     * findUsersByLdapGroupAndDepartment.
     *
     * @param ldapGroup ldapGroup
     * @param department department
     * @return List UserBean
     */
    public List<UserBean> findUsersByLdapGroupAndDepartment(final String ldapGroup, final String department) {
        return ldapService.findUsers(ldapGroup, department, null);
    }

    /**
     * Возвращает фио текущего пользователя или его логин.
     * @return фио текущего пользователя или его логин
     */
    public String getUserFullNameOrLogin() {
        final String currentUserLogin = getCurrentUserLogin();
        return ofNullable(getUserFioByLogin(currentUserLogin))
            .orElse(currentUserLogin);
    }

    /**
     * Проверка группы пользователя.
     * @param userGroup наименование группы
     * @return состоит ли пользователь в группе
     */
    public boolean checkUserGroup(final String userGroup) {
        final String currentUserLogin = getCurrentUserLogin();
        return checkUserGroup(userGroup, currentUserLogin);
    }

    /**
     * Проверка группы пользователя.
     * @param userGroup наименование группы
     * @param login     логин пользователя
     * @return состоит ли пользователь в группе
     */
    public boolean checkUserGroup(final String userGroup, final String login) {
        final List<String> userGroups = ldapService.getUserGroups(login);
        return userGroups.contains(userGroup);
    }
}
