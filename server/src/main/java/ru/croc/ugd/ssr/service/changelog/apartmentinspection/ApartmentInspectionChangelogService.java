package ru.croc.ugd.ssr.service.changelog.apartmentinspection;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.ChangelogDao;
import ru.croc.ugd.ssr.service.DictionaryService;
import ru.croc.ugd.ssr.service.changelog.AttributeConverter;
import ru.croc.ugd.ssr.service.changelog.ChangelogService;
import ru.reinform.cdp.ldap.service.LdapService;

/**
 * Сервис для работы с логикой журналирования актов по дефектам.
 */
@Service
public class ApartmentInspectionChangelogService extends ChangelogService {

    public ApartmentInspectionChangelogService(
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
        return "ugd_ssr_apartmentInspectionChangelogAttributes";
    }

}
