package ru.croc.ugd.ssr.parser;

import static java.util.Optional.ofNullable;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.exception.ArchivePasswordNotExistException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class ZipFileExtractor {
    /**
     * Парсер multipart file.
     *
     * @param file файл
     * @param filepath путь файла
     * @param zipPassword пароль
     * @return ZipFile zipFile
     */
    public ZipFile parseFile(
        final byte[] file,
        final Path filepath,
        final String zipPassword
    ) throws IOException {
        try (final OutputStream os = Files.newOutputStream(filepath)) {
            os.write(file);
            final ZipFile zipFile = new ZipFile(filepath.toString());

            if (zipFile.isEncrypted()) {
                ofNullable(zipPassword)
                    .filter(password -> !password.isEmpty())
                    .orElseThrow(ArchivePasswordNotExistException::new);
                zipFile.setPassword(zipPassword.toCharArray());
            }

            zipFile.extractAll(getFileName(filepath.toString()));
            return zipFile;
        }
    }

    private String getFileName(final String filePath) {
        return filePath.substring(0, filePath.lastIndexOf("."));
    }

}
