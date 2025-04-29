package com.deloitte.elrr.aggregator.test.util;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;

public class TestFileUtils {

  public static File getJsonTestFile(String filename) throws IOException {

    File file;

    try {
      file = new ClassPathResource(filename).getFile();
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    }

    return file;
  }
}
