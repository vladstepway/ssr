package ru.croc.ugd.ssr.integration.util;

import static org.apache.commons.io.FileUtils.writeStringToFile;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class FileUtils {
    /**
     * Запись файла на диск.
     *
     * @param absolutePath абсолютный путь до папки
     * @param fileName     наименование файла
     * @param body    тело файла
     */
    public static void writeStringFile(String absolutePath, String fileName, String body) {
        createDir(absolutePath);
        final File file = new File(absolutePath + fileName);
        try {
            writeStringToFile(file, body, StandardCharsets.UTF_8);
            log.info("Файл {} сформирован в директории {}", fileName, absolutePath);
        } catch (IOException ex) {
            log.error("Файл не был сформирован. Ошибка: {}", ex.getMessage(), ex);
        }
    }

    public static void processAllFilesInDir(String absolutePath, Consumer<? super FileStringData> handler) {
        createDir(absolutePath);
        try (Stream<Path> paths = Files.walk(Paths.get(absolutePath))) {
            paths
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .map(FileUtils::extractFileStringData)
                .forEach(handler);
        } catch (IOException e) {
            log.error("Папка {} не была прочитана. Ошибка: {}", absolutePath, e.getMessage(), e);
        }
    }

    public static void moveFile(String sourceFilePath, String destinationFilePath) {
        try {
            final Path sourcePath = Paths.get(sourceFilePath);
            final Path destinationPath = Paths.get(destinationFilePath);
            Files.createDirectories(destinationPath.getParent());
            Files.move(sourcePath, destinationPath);
        } catch (IOException e) {
            log.error("Не удалось переместить файл {}. Ошибка: {}", sourceFilePath, e.getMessage(), e);

        }
    }

    public static void removeFile(String sourceFilePath) {
        try {
            final Path sourcePath = Paths.get(sourceFilePath);
            Files.delete(sourcePath);
        } catch (IOException e) {
            log.error("Не удалось удалить файл {}. Ошибка: {}", sourceFilePath, e.getMessage(), e);

        }
    }

    private static FileStringData extractFileStringData(final File file) {
        try {
            return FileStringData
                .builder()
                .fileAbsolutePath(file.getAbsolutePath())
                .fileContent(org.apache.commons.io.FileUtils
                    .readFileToString(file, StandardCharsets.UTF_8))
                .build();
        } catch (IOException e) {
            return FileStringData
                .builder()
                .fileAbsolutePath(file.getAbsolutePath())
                .build();
        }
    }

    public static void createDir(final String absolutePath) {
        if (!(new File(absolutePath).exists())) {
            // Создадим путь до директории
            if (new File(absolutePath).mkdirs()) {
                log.info("Директория под файлы создана: {}", absolutePath);
            } else {
                log.error("Директория под файлы не создана: {}", absolutePath);
            }
        }
    }
}
