package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentLogData;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

/**
 * DAO по работе с Changelog.
 */
public interface ChangelogDao extends PagingAndSortingRepository<DocumentLogData, String>,
    JpaSpecificationExecutor<DocumentLogData> {

    /**
     * Получает JSON старого объекта по id лога.
     *
     * @param changelogId id лога
     * @return JSON старого объекта
     */
    @Query(
        value = "SELECT CAST(json_old AS TEXT) FROM documents_log WHERE id = :changelogId",
        nativeQuery = true
    )
    Optional<String> getOldJsonObject(@NotNull @Param("changelogId") String changelogId);

    /**
     * Получает JSON нового объекта по id лога.
     *
     * @param changelogId id лога
     * @return JSON нового объекта
     */
    @Query(
        value = "SELECT CAST(json_new AS TEXT) FROM documents_log WHERE id = :changelogId",
        nativeQuery = true
    )
    Optional<String> getNewJsonObject(@NotNull @Param("changelogId") String changelogId);

    /**
     * Получает логины всех пользователей, вносивших изменения в документ.
     *
     * @param idDoc id документа
     * @return список логинов, вносивших правки
     */
    @Query(
        value = "SELECT user_name FROM documents_log WHERE id_doc = :idDoc group by user_name",
        nativeQuery = true
    )
    List<String> findLoginsWhoChangedDocument(@NotNull @Param("idDoc") String idDoc);

    /**
     * Получает логин пользователя, кто создал документ.
     *
     * @param idDoc id документа
     * @return логин
     */
    @Query(
        value = "select user_name from documents_log where id_doc = :idDoc"
            + " and json_new is not null and json_old is null and json_patch is null limit 1",
        nativeQuery = true
    )
    Optional<String> findCreatedBy(@NotNull @Param("idDoc") String idDoc);
}
