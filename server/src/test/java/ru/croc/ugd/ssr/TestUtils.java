package ru.croc.ugd.ssr;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

import static org.apache.commons.codec.Charsets.UTF_8;

@UtilityClass
public class TestUtils {
  @SneakyThrows
  public static String readFromPath(String path) {
    InputStream expectedResultPath = TestUtils.class.getClassLoader().getResourceAsStream(path);
    return IOUtils.toString(expectedResultPath, UTF_8);
  }
}
