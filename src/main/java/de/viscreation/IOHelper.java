package de.viscreation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * ano, Keynote SIGOS GmbH, 19.06.2013
 */
public class IOHelper {

  /**
   * get file content as string
   * 
   * @param fileName
   * @throws IOException
   */
  public static String getFileContent(String fileName) throws IOException {

    File file = new File(fileName);
    if (!file.exists() || !file.isFile() || !file.canRead()) {
      return null;
    }
    StringBuffer fileData = new StringBuffer();
    BufferedReader reader = new BufferedReader(new FileReader(fileName));
    char[] buf = new char[1024];
    int numRead = 0;
    while ((numRead = reader.read(buf)) != -1) {
      String readData = String.valueOf(buf, 0, numRead);
      fileData.append(readData);
    }
    reader.close();

    return fileData.toString();
  }

}
