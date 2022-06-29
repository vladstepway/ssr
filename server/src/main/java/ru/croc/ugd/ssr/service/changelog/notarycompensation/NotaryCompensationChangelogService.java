package ru.croc.ugd.ssr.service.changelog.notarycompensation;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.ChangelogDao;
import ru.croc.ugd.ssr.service.DictionaryService;
import ru.croc.ugd.ssr.service.changelog.AttributeConverter;
import ru.croc.ugd.ssr.service.changelog.ChangelogService;
import ru.reinform.cdp.ldap.service.LdapService;

/**
 * Сервис для работы с логикой журналирования заявлений на возмещение оплаты услуг нотариуса.
 */
@Service
public class NotaryCompensationChangelogService extends ChangelogService {

    public NotaryCompensationChangelogService(
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
        return "ugd_ssr_notaryCompensationChangelogAttributes";
    }
}
