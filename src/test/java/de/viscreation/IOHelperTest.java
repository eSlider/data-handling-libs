package de.viscreation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class IOHelperTest {

  private int size;

  @Test
  public void testGetFileContent() throws IOException{
    ArrayList<HashMap<String, String>> list = IOHelper.parseCsv("pom.xml");
  }
}
