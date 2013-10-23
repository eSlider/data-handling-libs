package de.viscreation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class IOUtils {

  private static final String NEW_LINE_REGEXP      = "\\r?\\n";
  private static final String EMPTY                = "";
  private static final String CLEAN_CHARACTERS     = "^\"|\"$";
  private static final String SPLIT_ROW_CHARACTERS = "\";\"";
  private static final char   NL                   = '\n';
  
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
  
  /**
   * Parse CSV string to ArrayList<HashMap<String, String>>
   * 
   * @param fileName
   * @return
   * @throws IOException
   */
  public static ArrayList<HashMap<String, String>> parseCsv(String fileName) throws IOException {
    String importCsv = IOUtils.getFileContent(fileName);
    
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
  
  /**
   * Get InputStream to String
   * 
   * @param inputStream
   * @return String input value as string
   * @throws IOException
   */
  public static String getInputStreamContent( InputStream inputStream) throws IOException{
    BufferedReader bufferReader = new BufferedReader( new InputStreamReader(inputStream));
    StringBuffer result = new StringBuffer();
    String line;
    while ((line  = bufferReader.readLine()) != null) {
      result.append(line).append(NL);
    }
    bufferReader.close();
    return result.toString();
  }

}
