package ru.croc.ugd.ssr.service.personaldocument.type;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.DictionaryService;

import java.util.Set;
import javax.annotation.PostConstruct;

@Service
public class SsrPersonalDocumentTypeService {

    private final DictionaryService dictionaryService;
    private final SsrPersonalDocumentTypeConverter ssrPersonalDocumentTypeConverter;

    /**
     * Типы документов.
     */
    private Set<SsrPersonalDocumentType> ssrPersonalDocumentTypes;

    public SsrPersonalDocumentTypeService(
        DictionaryService dictionaryService,
        SsrPersonalDocumentTypeConverter ssrPersonalDocumentTypeConverter
    ) {
        this.dictionaryService = dictionaryService;
        this.ssrPersonalDocumentTypeConverter = ssrPersonalDocumentTypeConverter;
    }

    /**
     * Заполняет типов документов данными из справочника.
     */
    @PostConstruct
    public void afterInit() {
        ssrPersonalDocumentTypes = ssrPersonalDocumentTypeConverter.convertElements(
            dictionaryService.getDictionaryElementsAsServiceUser("ugd_ssr_personalDocumentType")
        );
    }

    public SsrPersonalDocumentType getTypeByCode(final String typeCode) {
        return ssrPersonalDocumentTypes.stream()
            .filter(type -> type.getCode().equals(typeCode))
            .findFirst()
            .orElse(null);
    }

    public String getTypeCodeByKindCode(final String kindCode) {
        return ofNullable(getTypeByKindCode(kindCode))
            .map(SsrPersonalDocumentType::getCode)
            .orElse(null);
    }

    public String getNameByCode(final String typeCode) {
        return ofNullable(getTypeByCode(typeCode))
            .map(SsrPersonalDocumentType::getName)
            .orElse(null);
    }

    public Integer getSortOrderByCode(final String typeCode) {
        return ofNullable(getTypeByCode(typeCode))
            .map(SsrPersonalDocumentType::getSortNumber)
            .orElse(null);
    }

    private SsrPersonalDocumentType getTypeByKindCode(final String kindCode) {
        return ssrPersonalDocumentTypes.stream()
            .filter(type -> nonNull(type.getKindCode()) && type.getKindCode().equals(kindCode))
            .findFirst()
            .orElse(null);
    }
}
