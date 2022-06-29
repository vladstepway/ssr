package ru.croc.ugd.ssr.service.changelog.person;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.ChangelogDao;
import ru.croc.ugd.ssr.service.DictionaryService;
import ru.croc.ugd.ssr.service.changelog.AttributeConverter;
import ru.croc.ugd.ssr.service.changelog.ChangelogService;
import ru.reinform.cdp.ldap.service.LdapService;

/**
 * Сервис для работы с логикой журналирования жителей.
 */
@Service
public class PersonChangelogService extends ChangelogService {

    public PersonChangelogService(
        ChangelogDao dao,
        LdapService ldapService,
        DictionaryService dictionaryService,
        AttributeConverter attributeConverter
    ) {
        super(
            dao,
            ldapService,
            dictionaryService,
            attributeConverter);
    }

    protected String getDictName() {
        return "ugd_ssr_personChangelogAttributes";
    }

}
