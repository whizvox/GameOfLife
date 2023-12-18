package me.whizvox.gameoflife;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtils {

  public static InputStream getResourceStream(String path) {
    return IOUtils.class.getClassLoader().getResourceAsStream(path);
  }

  public static String readResourceAsString(String path) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(getResourceStream(path)))) {
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append('\n');
      }
      return sb.toString();
    }
  }

}
