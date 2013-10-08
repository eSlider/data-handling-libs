package de.viscreation;

import org.junit.Test;

import de.viscreation.MySqlHandler.DataList;

public class MySqlHandlerTest {
  
  private static final int COUNT = 10000;
  public MySqlHandler dbHandle = new MySqlHandler("jdbc:mysql://192.168.178.54/wims","root","root");
  
  @Test
  public void testOnlyExecution() throws Exception {
    for (int i = 0; i < COUNT; i++) {
      dbHandle.execute("SELECT * FROM project LIMIT "+i+",10");
    }
  }
  @Test
  public void testOnlyGetValue() throws Exception {
    String value;
    for (int i = 0; i < COUNT; i++) {
      value = dbHandle.getValue("SELECT * FROM project LIMIT 0,1");
      System.out.println(value);
    }
  }

  @Test
  public void testExecuteAsList() throws Exception {
    for (int i = 0; i < COUNT; i++) {
      DataList list = dbHandle.execute("SELECT * FROM project LIMIT "+i+",10");
      list.size();
    }
  }

}
