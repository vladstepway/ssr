package ru.croc.ugd.ssr.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.diff.JsonDiff;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.reinform.cdp.exception.RIPermissionException;
import ru.reinform.cdp.userprofile.api.UserprofileServerRestApi;
import ru.reinform.cdp.userprofile.model.UserprofileDocument;
import ru.reinform.cdp.utils.core.RIExceptionUtils;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Сервис по работе с ЦИПами для пользователя ЕЦП.
 */
@Service
@RequiredArgsConstructor
public class UserCipsCrudService {

    private final JsonMapper jsonMapper;

    private final UserprofileServerRestApi userprofileServerRestApi;

    /**
     * Добавление ЦИПов пользователю.
     *
     * @param login логин пользователя
     * @param cips  коды ЦИПов через запятую
     */
    public void addUserCipsByLogin(String login, String cips) {
        String[] splitCips = cips.split(",");
        UserprofileDocument userprofileDocument = userprofileServerRestApi.getByLogin(login);

        if (nonNull(userprofileDocument)) {
            boolean hasChanges = false;
            List<String> userCips = userprofileDocument.getDocument().getCips();
            for (String cip : splitCips) {
                if (!userCips.contains(cip)) {
                    userCips.add(cip);
                    hasChanges = true;
                }
            }

            if (hasChanges) {
                updateUserProfile(userprofileDocument);
            }
        }
    }

    /**
     * Удаление всех ЦИПов пользователя.
     *
     * @param login логин пользователя
     */
    public void deleteAllUserCipsByLogin(String login) {
        UserprofileDocument userprofileDocument = userprofileServerRestApi.getByLogin(login);

        if (nonNull(userprofileDocument)) {
            List<String> userCips = userprofileDocument.getDocument().getCips();
            userCips.clear();

            updateUserProfile(userprofileDocument);
        }
    }

    /**
     * Удаление ЦИПов пользователя.
     *
     * @param login логин пользователя
     * @param cips  коды ЦИПов через запятую
     */
    public void deleteUserCipsByLogin(String login, String cips) {
        String[] splitCips = cips.split(",");
        UserprofileDocument userprofileDocument = userprofileServerRestApi.getByLogin(login);

        if (nonNull(userprofileDocument)) {
            List<String> userCips = userprofileDocument.getDocument().getCips();
            for (String cip : splitCips) {
                userCips.remove(cip);
            }

            updateUserProfile(userprofileDocument);
        }
    }

    /**
     * Получение всех ЦИПов пользователя.
     *
     * @param login логин пользователя
     * @return пользователи ЦИПа
     */
    public List<String> getAllUserCipsByLogin(String login) {
        UserprofileDocument userprofileDocument = userprofileServerRestApi.getByLogin(login);

        if (isNull(userprofileDocument)) {
            return Collections.emptyList();
        }

        return userprofileDocument.getDocument().getCips();
    }

    private JsonPatch createJsonPatch(UserprofileDocument newDoc) throws IOException {
        String jsonOld = jsonMapper.writeObject(userprofileServerRestApi.getByLogin(newDoc.getDocument().getLogin()));
        String jsonNew = jsonMapper.writeObject(newDoc);

        return JsonPatch.fromJson(JsonDiff.asJson(jsonMapper.readNode(jsonOld), jsonMapper.readNode(jsonNew)));
    }

    private void updateUserProfile(UserprofileDocument userprofileDocument) {
        try {
            userprofileServerRestApi
                .update(userprofileDocument.getId(), createJsonPatch(userprofileDocument), "update from ugd_ssr");
        } catch (RIPermissionException e) {
            throw RIExceptionUtils.wrap(e, "Не выдана системная ГБ SYS_USERPROFILE_EDIT");
        } catch (Exception e) {
            throw RIExceptionUtils.wrap(e, "Проблема при привязке пользователя к ЦИП");
        }
    }

}
