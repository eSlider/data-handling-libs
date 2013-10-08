package de.viscreation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class MySqlHandler {

  private static final String START_BRACE   = " (";
  private static final String INSERT_VALUES = ") VALUES (";
  private static final String INSERT_INTO   = "INSERT INTO ";
  private static final char   BACK_BRACE2   = ')';
  private static final char   COMMA         = ',';
  private static final char   BACK_BRACE    = '`';
  private static final String INSERT_VALUE  = "? ,";
  private Connection          connection    = null;

  /**
   * @param connectionId 
   * @throws ClassNotFoundException 
   * @throws SQLException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public MySqlHandler(String url, String username, String password) {
    try {
      connection = DriverManager.getConnection(url, username, password);
    } catch (Exception e) {
    }
  }

  public Connection getConnection(){
    return connection;
  }

  public boolean truncateTable(String name) throws SQLException {
    return connection.prepareStatement("TRUNCATE " + name).execute();
  }
  
  public ResultSet createTable(String name) throws SQLException {
    return _execute("CREATE TABLE IF NOT EXISTS `"
        + name
        + "` ( `id` INT(10) NULL AUTO_INCREMENT, `creationDate` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,  PRIMARY KEY (`id`) ) COLLATE='utf8_general_ci' ENGINE=MyISAM");
  }

  public long insert(String tableName,  HashMap<String,Object> data ) throws SQLException {
    long id = 0;
    StringBuilder sql;
    StringBuilder keys = new StringBuilder();
    StringBuilder values = new StringBuilder();
    String value;
    int i = 0;
    ResultSet result = null;
    
    // prepare sql 
    for ( String key : data.keySet()) {
      keys.append(BACK_BRACE).append(key).append(BACK_BRACE).append(COMMA);
      values.append(INSERT_VALUE);
    }
    
    values.setLength(values.length()-1);
    keys.setLength(keys.length()-1);
    
    sql = new StringBuilder(INSERT_INTO)
     .append(tableName)
     .append(BACK_BRACE)
     .append(START_BRACE)
     .append(keys)
     .append(INSERT_VALUES)
     .append(values)
     .append(BACK_BRACE2);
    
    PreparedStatement statement = connection.prepareStatement( sql.toString() , Statement.RETURN_GENERATED_KEYS );
    
    for ( String key : data.keySet()) {
      value = data.get(key).toString();
      statement.setString(++i, value);
    }
    
    try {
      //statement.execute();
      id = statement.executeUpdate();
      result = statement.getGeneratedKeys();
      if (result.next()){
        id=result.getInt(1);
      }
      close(statement);
      close(result);
      
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
    return id;
  }

  /**
   * execute SQL and return ResutlSet
   * it can be null if error catch
   * 
   * A ResultSet object is automatically closed by the Statement object that generated 
   * it when that Statement object is closed, re-executed, 
   * or is used to retrieve the next result from a sequence of multiple results. 
   * A ResultSet object is also automatically closed when it is garbage collected. 
   * 
   * http://docs.oracle.com/javase/1.4.2/docs/api/java/sql/ResultSet.html#close%28%29
   * 
   * @param sql
   * @return result set
   * @throws SQLException
   */
  public ResultSet _execute(String sql){
    ResultSet result = null;
    try {
      result = connection.createStatement().executeQuery(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return result;
  }
  
  
  /**
   * get int by sql query
   * 
   * @param sql
   * @return int
   * @throws SQLException
   */
  public Integer getInt(String sql){
    String value = null;
    Integer intResult = null;
    
    try {
      value = getValue(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
    if(value != null){
      intResult = Integer.parseInt(value);
    }
    
    return intResult;
  }
  
  /**
   * get string value by sql query
   * 
   * @param sql
   * @return String
   * @throws SQLException
   */
  public String getValue(String sql) throws SQLException{
    ResultSet result = _execute(sql);
    String value = null;
    if( result != null && result.next() ){
      value = result.getString(1);
//      close(result);
    }
    
    return value;
  }
  
  /**
   * get string value by sql query
   * 
   * @param sql
   * @return int if 
   * @throws SQLException
   */
  public Data getRow(String sql) throws SQLException{
    DataList _list = execute(sql);
    Data data = null;
    if(_list != null && _list.size() > 0) {
      data = _list.get(0);
    }
    return data;
  }

  public DataList execute(String sql) throws SQLException{
    DataList _list = null;
    ResultSet result = _execute(sql);
    if(result !=null){
      _list = new DataList(result);
    }
    return _list;
  }

  
  /**
   * Data list manager
   */
  class DataList extends ArrayList<Data> {

    private static final long serialVersionUID = 1L;

    public DataList(ResultSet resultSet) throws SQLException {
      String[] columns = getResultColumns(resultSet.getMetaData());
      while (resultSet.next()) {
        add(new Data(resultSet, columns));
      }
    }

    public String[] getResultColumns(ResultSetMetaData metaData)
        throws SQLException {
      int length = metaData.getColumnCount();
      String[] columns = new String[length];
      for (int i = 0; i < length; i++) {
        columns[i] = metaData.getColumnLabel(i + 1);
      }
      return columns;
    }
  }
  
  /**
   * Data holder
   */
  class Data extends HashMap<String, String> {

    private static final long serialVersionUID = 1L;

    public Data(ResultSet resultSet, String[] columns) throws SQLException {
      int length = columns.length;
      for (int i = 0; i < length; i++) {
        put(columns[i], resultSet.getString(i + 1));
      }
    }
  }
  
  /**
   * close all 
   */
  private void close() {
    close(connection);
  }

  public void close(ResultSet result) {
    try {
      result.close();
    } catch (Exception e) {
    } finally {
      result = null;
    }
  }

  private void close(Connection connection) {
    try {
      connection.close();
    } catch (Exception e) {
    } finally {
      connection = null;
    }
  }

  private void close(Statement statement) {
    try {
      statement.close();
    } catch (Exception e) {
    } finally {
      statement = null;
    }
  }
  
  @Override
  protected void finalize() throws Throwable {
    close();
    super.finalize();
  }
}
