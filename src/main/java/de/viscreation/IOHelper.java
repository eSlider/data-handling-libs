package de.viscreation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class IOHelper {

  private static final String NEW_LINE_REGEXP      = "\\r?\\n";
  private static final String EMPTY                = "";
  private static final String CLEAN_CHARACTERS     = "^\"|\"$";
  private static final String SPLIT_ROW_CHARACTERS = "\";\"";

  /**
   * get file content as string
   * 
   * @param fileName
   * @throws IOException
   */
  public static String getFileContent(String fileName) throws IOException {
    File file = new File(fileName);
    return getFileContent(file);
  }

  public static String getFileContent(File file) throws FileNotFoundException, IOException {
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

  public static ArrayList<HashMap<String, String>> parseCsv(String fileName) throws IOException {
    String importCsv = IOHelper.getFileContent(fileName);
    
    String[] split = importCsv.split(NEW_LINE_REGEXP);
    String[] columns = getEntriesFromCsvLine(split[0]);
    ArrayList<HashMap<String, String>> mapList = new ArrayList< HashMap<String, String>>();
    String[] data;
    
    HashMap<String, String> dataMap;
    
    for (int i = 1; i < split.length; i++) {
      data = getEntriesFromCsvLine(split[i]);
      dataMap = new HashMap<String, String>();
      for (int j = 0; j < data.length; j++) {
        dataMap.put(columns[j], data[j]);
      }
      mapList.add(dataMap);
    }
    
    return mapList;
  }
  
  private static String[] getEntriesFromCsvLine(String line) {
    return line.replaceAll(CLEAN_CHARACTERS,EMPTY).split(SPLIT_ROW_CHARACTERS);
  }

}
