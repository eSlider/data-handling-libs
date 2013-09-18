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
import java.util.Hashtable;

public class MySqlHandler {

  private static final String INSERT_VALUES = ") VALUES (";
  private static final String INSERT_INTO = "INSERT INTO";
  private static final char BACK_BRACE2 = ')';
  private static final char                  COMMA        = ',';
  private static final char                  BACK_BRACE   = '`';
  private static final String                INSERT_VALUE = "? ,";
  private Connection                         connection   = null;
  private HashMap<String, String>            data;
  private int                                columnCount;
  private ArrayList<HashMap<String, String>> list;
  private String[]                           columns;

  /**
   * @param connectionId 
   * @throws ClassNotFoundException 
   * @throws SQLException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public MySqlHandler(String url, String username, String password) throws Exception {
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    connection = DriverManager.getConnection(url, username, password);
  }

  public ResultSet createTable(String name) throws SQLException {
    return execute("CREATE TABLE IF NOT EXISTS `"+name+"` ( `id` INT(10) NULL AUTO_INCREMENT, `creationDate` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,  PRIMARY KEY (`id`) ) COLLATE='utf8_general_ci' ENGINE=MyISAM");
  }
  
  public long insert(String tableName,  Hashtable<String,Object> data ) throws SQLException {
    long id;
    String sql;
    StringBuilder keys = new StringBuilder();
    StringBuilder values = new StringBuilder();
    String value;
    int i = 0;
    PreparedStatement statement;
    ResultSet result;
    
    // prepare sql 
    for ( String key : data.keySet()) {
      keys.append(BACK_BRACE+key+BACK_BRACE+COMMA);
      values.append(INSERT_VALUE);
    }
    
    values.setLength(values.length()-1);
    keys.setLength(keys.length()-1);
    
    sql = INSERT_INTO+BACK_BRACE +tableName+BACK_BRACE +" (" +keys+ INSERT_VALUES+values + BACK_BRACE2;
    
    statement = connection.prepareStatement( sql , Statement.RETURN_GENERATED_KEYS );
    
    for ( String key : data.keySet()) {
      value = data.get(key).toString();
      statement.setString(++i, value);
    }
    
    statement.execute();
    id = statement.executeUpdate();
    result = statement.getGeneratedKeys();
    
    if (result.next()){
        id=result.getInt(1);
    }
    
    close(statement);
    close(result);
    
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
  public ResultSet execute(String sql) throws SQLException{
    return connection.createStatement().executeQuery(sql);
  }
  
  public ArrayList<HashMap<String, String>> execute(String sql, int mode) throws SQLException{
    ResultSet resultSet = execute(sql);
    return convertResultSetToList(resultSet, getResultColumns(resultSet.getMetaData()));
  }

  public ArrayList<HashMap<String, String>> convertResultSetToList(ResultSet resultSet, String[] columns) throws SQLException {
    list = new  ArrayList<HashMap<String, String>>();
    while (resultSet.next()) {
      list.add(convertResultToMap(resultSet, columns));
    }
    return list;
  }

  public HashMap<String, String> convertResultToMap(ResultSet resultSet, String[] columns) throws SQLException{
    columnCount = columns.length;
    data = new HashMap<String, String>();
    for (int i = 0; i < columnCount; i++) {
      data.put(columns[i], resultSet.getString(i+1));
    }
    return data;
  }

  public String[] getResultColumns(ResultSetMetaData metaData) throws SQLException {
    columnCount = metaData.getColumnCount();
    columns = new String[columnCount];
    for (int i = 0; i <columnCount; i++) {
      columns[i] = metaData.getColumnLabel(i+1);
    }
    return columns;
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
