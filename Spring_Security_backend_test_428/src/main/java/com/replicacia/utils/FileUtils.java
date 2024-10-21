package com.replicacia.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FileUtils {

  @Value("${temp.directory}")
  private String DEFAULT_PATH;

  File copyFile(final InputStream inputStream, final String fileName) {
    final File targetFile = new File(this.DEFAULT_PATH + File.separator + fileName);

    try {
      Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }

    return targetFile;
  }

  File getFile(final InputStream inputStream, final String fileName) {
    return this.copyFile(inputStream, fileName);
  }

  public void clearTempFiles() {
    final File folder = new File(this.DEFAULT_PATH);

    if (folder.isDirectory()) {
      final File[] files = folder.listFiles();

      if (files != null) {
        for (final File file : files) {
          if (file.isFile()) {
            if (file.delete()) {
              log.info("Deleted file: " + file.getName());
            } else {
              log.error("Failed to delete file: " + file.getName());
            }
          }
        }
      } else {
        log.info("No files found in the directory.");
      }
    } else {
      log.info("Specified path is not a directory.");
    }
  }
}
