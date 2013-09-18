package de.viscreation;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class MySqlHandlerTest {

  @Test
  public void testMySqlHandler() throws Exception {
    MySqlHandler dbHandler = new MySqlHandler("jdbc:mysql://172.17.16.112:3306/kpi","root","root");
    
    ArrayList<HashMap<String, String>> list = null;
    for (int i = 0; i < 10; i++) {
      list = dbHandler.execute("SELECT * FROM Main LIMIT "+i+",10",1);
    }
    list.size();
    
  }

}
