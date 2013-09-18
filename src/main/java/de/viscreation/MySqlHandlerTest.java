package de.viscreation;

import org.junit.Test;

public class MySqlHandlerTest {
  
  private static final int COUNT = 2000;
  public MySqlHandler dbHandle = new MySqlHandler("jdbc:mysql://172.17.16.112:3306/kpi","root","root");
  
  @Test
  public void testOnlyExecution() throws Exception {
    for (int i = 0; i < COUNT; i++) {
      dbHandle.execute("SELECT * FROM Main LIMIT "+i+",10");
    }
  }

  @Test
  public void testExecuteAsList() throws Exception {
    for (int i = 0; i < COUNT; i++) {
      dbHandle.execute("SELECT * FROM Main LIMIT "+i+",10",1);
    }
  }

}
