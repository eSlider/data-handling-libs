package de.viscreation;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class MySqlHandlerTest {
  
  private static final int COUNT = 1;
  public MySqlHandler dbHandle = new MySqlHandler("jdbc:mysql://192.168.178.42/flights","root","root");
  
  @Test
  public void testOnlyExecution() throws Exception {
    for (int i = 0; i < COUNT; i++) {
      dbHandle.execute("SELECT * FROM Personen LIMIT "+i+",10");
    }
  }

  @Test
  public void testExecuteAsList() throws Exception {
    for (int i = 0; i < COUNT; i++) {
      ArrayList<HashMap<String,String>> list = dbHandle.execute("SELECT * FROM Fluge LIMIT "+i+",10",1);
      list.size();
    }
  }

}
