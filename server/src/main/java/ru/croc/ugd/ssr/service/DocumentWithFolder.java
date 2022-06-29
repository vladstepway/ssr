package ru.croc.ugd.ssr.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import ru.croc.ugd.ssr.service.document.SsrAbstractDocumentService;
import ru.reinform.cdp.document.model.DocumentAbstract;
import ru.reinform.cdp.document.model.ICommonFilestoreSubfoldersModel;

/**
 * Расширяющий абстрактный класс для создания папки в альфреско при создании документа.
 *
 * @param <T> тип документа
 */
public abstract class DocumentWithFolder<T extends DocumentAbstract>
    extends SsrAbstractDocumentService<T>
    implements ICommonFilestoreSubfoldersModel<T> {

    @Getter
    @Value("${app.filestore.ssr.rootPath}")
    private String rootPath;

    @Getter
    @Value("${app.filestore.ssr.folderType}")
    private String folderType;

}
