package ru.croc.ugd.ssr.service.notary;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.dto.notary.RestCreateUpdateNotaryRequestDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryInfoDto;
import ru.croc.ugd.ssr.dto.notary.RestNotaryResponseDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryPersonCheckDto;
import ru.croc.ugd.ssr.mapper.NotaryMapper;
import ru.croc.ugd.ssr.model.notary.NotaryDocument;
import ru.croc.ugd.ssr.notary.Notary;
import ru.croc.ugd.ssr.notary.NotaryStatus;
import ru.croc.ugd.ssr.notary.NotaryType;
import ru.croc.ugd.ssr.service.UserService;
import ru.croc.ugd.ssr.service.document.NotaryDocumentService;
import ru.reinform.cdp.ldap.model.UserBean;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DefaultRestNotaryService.
 */
@Service
@AllArgsConstructor
@Slf4j
public class DefaultRestNotaryService implements RestNotaryService {

    private final NotaryDocumentService notaryDocumentService;
    private final UserService userService;
    private final NotaryMapper notaryMapper;
    private final NotaryApplicationCheckService notaryApplicationCheckService;

    /**
     * Получить карточку.
     *
     * @param notaryId Ид карточки
     * @return карточка
     */
    @Override
    public RestNotaryResponseDto fetchById(String notaryId) {
        NotaryDocument notary = notaryDocumentService.fetchDocument(notaryId);
        return notaryMapper.toRestNotaryResponseDto(notary.getDocument(), getEmployee(notary));
    }

    @Override
    public RestNotaryInfoDto fetchNotaryInfo(final String notaryId) {
        NotaryDocument notary = notaryDocumentService.fetchDocument(notaryId);
        return notaryMapper.toRestNotaryInfoDto(notary.getDocument());
    }

    /**
     * Создание карточки нотариуса.
     *
     * @param body Данные нотариуса (required)
     * @return карточка
     */
    @Override
    public RestNotaryResponseDto create(final RestCreateUpdateNotaryRequestDto body) {
        final NotaryDocument notary = notaryMapper.toNotaryDocument(new NotaryDocument(), body);
        final NotaryDocument savedNotary = notaryDocumentService.createDocument(notary, true, "");
        return notaryMapper.toRestNotaryResponseDto(notary.getDocument(), getEmployee(savedNotary));
    }

    /**
     * Редакторивание карточки нотариуса.
     *
     * @param notaryId Ид карточки
     * @param body Данные нотариуса
     * @return карточка
     */
    @Override
    public RestNotaryResponseDto update(String notaryId, RestCreateUpdateNotaryRequestDto body) {
        final NotaryDocument notary = notaryDocumentService.fetchDocument(notaryId);
        final NotaryDocument notaryToUpdate = notaryMapper.toNotaryDocument(notary, body);
        final UserBean employee = getEmployee(notaryToUpdate);
        final NotaryDocument updatedNotary = notaryDocumentService
            .updateDocument(notaryId, notaryToUpdate, true, true, null);
        return notaryMapper.toRestNotaryResponseDto(updatedNotary.getDocument(), employee);
    }

    @Override
    public List<RestNotaryInfoDto> findAll(
        final String fullName, final String login, final boolean includeUnassignedEmployee
    ) {
        return notaryDocumentService.findAll(fullName, login, includeUnassignedEmployee)
            .stream()
            .map(notary -> notaryMapper.toRestNotaryInfoDto(notary.getDocument()))
            .collect(Collectors.toList());
    }

    /**
     * Отправить в архив.
     *
     * @param notaryId Ид карточки
     */
    @Override
    public void archive(String notaryId) {
        final NotaryDocument notary = notaryDocumentService.fetchDocument(notaryId);
        notary.getDocument().getNotaryData().setStatus(NotaryStatus.ARCHIVE);
        notaryDocumentService.updateDocument(notaryId, notary, true, true, null);
    }

    @Override
    public RestNotaryPersonCheckDto check(final String personId) {
        return notaryApplicationCheckService.getInternalCheckResult(personId);
    }

    private UserBean getEmployee(NotaryDocument notary) {
        return Optional.ofNullable(notary.getDocument())
            .map(Notary::getNotaryData)
            .map(NotaryType::getEmployeeLogin)
            .map(userService::getUserBeanByLogin)
            .orElse(null);
    }

}
