package de.viscreation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
    BufferedReader reader = new BufferedReader(new FileReader(file));
    char[] buf = new char[1024];
    int numRead = 0;
    while ((numRead = reader.read(buf)) != -1) {
      fileData.append(String.valueOf(buf, 0, numRead));
    }
    reader.close();
    return fileData.toString();
  }

}
