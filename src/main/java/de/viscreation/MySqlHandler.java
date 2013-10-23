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

  private static final String SQL_ERROR = "SQL Error:";
  private static final String DOUBLE_QUOTE = "\"";
  private static final String REPLACEMENT = "\\\"";
  private static final String REGEX2 = "'$";
  private static final String REGEX = "'";
  private static final String EMPTY = "";
  private static final String ESCAPE_REGEXP = "^.+?\\:\\s";
  private static final String ESCAPE_CHART_STRING = "?";
  private static final String START_BRACE   = " (";
  private static final String INSERT_VALUES = ") VALUES (";
  private static final String INSERT_INTO   = "INSERT INTO `";
  private static final char   END_BRACE   = ')';
  private static final char   COMMA         = ',';
  private static final char   BACK_BRACE    = '`';
  private static final String INSERT_VALUE  = "? ,";
  
  private static final String LIMIT_0_1 = " LIMIT 1";
  private static final String LIKE = " LIKE ";
  private static final String WHERE = " WHERE ";
  private static final String LIMIT = " LIMIT ";

  private Connection          connection    = null;

  private static final char OB = '{';
  private static final char CB = '}';
  private static final char NL = '\n';
  private static final char WS = ' ';
  
  private DatabaseManager databaseManager;
  private TableManager tableManager;
  private String url;
  private String username;
  private String password;

  /**
   * @param connectionId 
   * @throws ClassNotFoundException 
   * @throws SQLException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public MySqlHandler(String url, String username, String password) {
    this.url = url;
    this.username = username;
    this.password = password;
    getConnection();
  }

  public Connection getConnection() {
    try {
      if(connection == null){
        connection = DriverManager.getConnection(url, username, password);
      } 
    } catch (Exception e) {
      e.printStackTrace();
    }
    return connection;
  }
  
  public String escape(String value) throws SQLException{
    PreparedStatement statement = connection.prepareStatement(ESCAPE_CHART_STRING);
    statement.setString(1, value); 
    String r = statement.toString()
        .replaceAll(ESCAPE_REGEXP, EMPTY)
        .replaceFirst(REGEX, EMPTY)
        .replaceAll(REGEX2, EMPTY)
        .replace(DOUBLE_QUOTE,REPLACEMENT);
    statement.close();
    return r;
  }
  
  public StringBuffer escapeName(String value) throws SQLException{
    return new StringBuffer().append(BACK_BRACE).append(escape(value)).append(BACK_BRACE);
  }
  
  /**
   * 
   * Table manager
   *
   */
  public class TableManager {
    private static final String DESCRIBE                   = "DESCRIBE ";
    private static final String SHOW_TABLES                = "SHOW TABLES";
    private static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";
    private static final String TABLE_PRIMARY_KEY          = "`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`) ";
    private static final String DEFAULT_COLLATE            = " COLLATE='utf8_general_ci' ";
    private static final String DEFAULT_ENGINE             = " ENGINE=MyISAM";
    private static final String DROP_TABLE_IF_EXISTS       = "DROP TABLE IF EXISTS ";
    private static final String TRUNCATE                   = "TRUNCATE ";
    private static final String SELECT_ID_FROM             = "SELECT `id` FROM ";
    private static final String SELECT_COUNT_FROM          = "SELECT count(*) FROM ";
    private static final String SELECT_ALL_FROM            = "SELECT * FROM ";
    private static final String SELECT_DISTINCT            = "SELECT DISTINCT ";
    private static final String ALTER_TABLE                = "ALTER TABLE ";
    private static final String ADD_CONSTRAINT             = "ADD CONSTRAINT ";
    private static final String FROM                       = " FROM ";
    private static final String ADD                        = " ADD ";
    private static final String UNIQUE                     = " UNIQUE ";

    public boolean rename(String newName,String oldName) throws SQLException {
       return executeStatement("RENAME TABLE "+ escapeName(oldName) +WS+escapeName(newName));
    }
    
    public Integer getIdByValue(String tableName, String fieldName, String fieldValue) throws SQLException  {
      StringBuffer sql = new StringBuffer(SELECT_ID_FROM)
        .append(escapeName(tableName))
        .append(WHERE).append(escapeName(fieldName))
        .append(LIKE).append(DOUBLE_QUOTE).append(escape(fieldValue)).append(DOUBLE_QUOTE)
        .append(LIMIT_0_1);
      
      Integer r = null;
      String value = getValue(sql.toString());
      if (value != null){
        r = Integer.parseInt(value);
      }
      return r;
    }
    
    public int count(String name) throws SQLException{ 
      return Integer.parseInt(getValue(SELECT_COUNT_FROM+escapeName(name)));
    }
    
    public Data gatDataRow(String name, int rowNr ) throws SQLException{ 
      return getRow(
          new StringBuilder(SELECT_ALL_FROM)
            .append(escapeName(name))
            .append(LIMIT)
              .append(rowNr).append(COMMA)
              .append(1).toString()) ;
    }
    
    public DataList distinct(String name, Field field) throws SQLException{
      return execute(SELECT_DISTINCT+escapeName(field.name)+FROM+escapeName(name));
    }
    
    public boolean addField(String name, String fieldName, String extra) throws SQLException{
      return connection.prepareStatement(
          ALTER_TABLE+escapeName(name)+
          ADD+escapeName(fieldName)+
          WS+extra).execute();
    }
    
    public boolean addUniqueKey(String name, String fieldName) throws SQLException {
      return executeStatement(
          ALTER_TABLE+escapeName(name) + 
          ADD_CONSTRAINT+escapeName(fieldName) +
          UNIQUE + 
            START_BRACE +escapeName(fieldName)+END_BRACE
          );
    }
    
    public boolean truncate(String name) throws SQLException {
      return connection.prepareStatement(
          TRUNCATE + 
          escapeName(name)).execute();
    }

    public boolean remove(String name) throws SQLException {
      return executeStatement(
          DROP_TABLE_IF_EXISTS+
          escapeName(name));
    }

    public boolean create(String name) throws SQLException {
      return executeStatement(
          CREATE_TABLE_IF_NOT_EXISTS + 
            escapeName(name) + 
            START_BRACE + 
              TABLE_PRIMARY_KEY +
              // + "`creationDate` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP "
            END_BRACE + 
           DEFAULT_COLLATE + 
           DEFAULT_ENGINE);
    }
    
    public ArrayList<String> list() throws SQLException{
      ArrayList<String> list = new ArrayList<String>();
      for (Data data : execute(SHOW_TABLES)) {
        list.add(data.values().toArray()[0].toString());
      }
      return list;
    }

    public Fields describe(String name) throws SQLException{
      return new Fields(execute(DESCRIBE+escapeName(name)));
    }
    
    public class Fields extends ArrayList<Field> {
      
      private static final long serialVersionUID = 1L;

      public Fields(DataList list) {
        for (Data data : list) {
          add(new Field(data));
        }
      }
    }
    
    public class Field  {
      private static final char   CH     = '(';
      private static final String CB     = "\\)";
      private static final String OB     = "\\(";
      private static final String TYPE   = "Type";
      private static final String KEY2   = "Key";
      private static final String FIELD  = "Field";
      private static final String EXTRA2 = "Extra";
      private static final String NILL   = "Null";
      private static final String YES    = "YES";

      public String defaultValue;
      public String key;
      public String extra;
      public String name;
      public String typeName;
      public boolean isNull;
      public int typeSize;
      public String originalType;

      public Field(Data data){
        name = data.get(FIELD);
        key = data.get(KEY2);
        originalType = data.get(TYPE);
        if(originalType.indexOf(CH)>0){
          String[] typeInfo = originalType.split(OB);
          typeName = typeInfo[0];
          typeSize = Integer.parseInt(typeInfo[1].split(CB)[0]);
        } else {
          typeName = originalType;
        }
        isNull = data.get(NILL).equals(YES);
        extra = data.get(EXTRA2);
      }
      
      @Override
      public String toString()  {
        StringBuffer r = new StringBuffer();
        try {
          for (java.lang.reflect.Field field : getClass().getFields() ) {
              r.append(field.getName()).append("=").append(field.get(this)).append(WS);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        return r.toString();
      }

    }

  }
  
  public TableManager getTableManager(){
    return tableManager == null ? tableManager = new TableManager() : tableManager;
  }
  
  /**
   * Database manager
   */
  public class DatabaseManager {
    private static final String CREATE_DATABASE_IF_NOT_EXISTS = "CREATE DATABASE IF NOT EXISTS ";
    private static final String DROP_DATABASE_IF_EXISTS       = "DROP DATABASE IF EXISTS ";
    private static final String SHOW_DATABASES                = "SHOW DATABASES";
    private static final String USE                           = "USE ";

    public void create(String name) throws SQLException{
      executeStatement(CREATE_DATABASE_IF_NOT_EXISTS+escapeName(name));
    }
    
    public void drop(String name) throws SQLException{
      executeStatement(DROP_DATABASE_IF_EXISTS+escapeName(name));
    }
    
    public void use(String name) throws SQLException{
      executeStatement(USE+escapeName(name));
    }
    
    public ArrayList<String> list() throws SQLException{
      ArrayList<String> list = new ArrayList<String>();
      for (Data data : execute(SHOW_DATABASES)) {
        list.add(data.values().toArray()[0].toString());
      }
      return list;
    }

  }
  
  public DatabaseManager getDatabaseManager(){
    return databaseManager == null ? databaseManager = new DatabaseManager() : databaseManager;
  }
  
  public long insert(String tableName,  Data data ) throws SQLException {
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
     .append(escape(tableName))
     .append(BACK_BRACE)
     .append(START_BRACE)
     .append(keys)
     .append(INSERT_VALUES)
     .append(values)
     .append(END_BRACE);
    
    PreparedStatement statement = connection.prepareStatement( sql.toString() , Statement.RETURN_GENERATED_KEYS );
    
    for ( String key : data.keySet()) {
      value = data.get(key);
      statement.setString(++i, value);
    }
    
    try {
      //statement.execute();
      id = statement.executeUpdate();
      result = statement.getGeneratedKeys();
      if (result.next()){
        id=result.getInt(1);
        
      }
      result.close();
      statement.close();
      
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
      result.close();
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
    result.getStatement().closeOnCompletion();
    result.getStatement().close();
    result.close();
    result.close();
    return _list;
  }
  
  
  /**
   * Data list manager
   */
  public class DataList extends ArrayList<Data> {

    private static final long serialVersionUID = 1L;

    public DataList(){
    }
    
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
    
    public DataList search(String keyRegExp, String searchValue) {
      DataList list = new DataList();
      for (Data item : this) {
        for (String key : item.keySet()) {
          if(key.matches(keyRegExp)){
            String value = item.get(key);
            if(value != null && value.equals(searchValue)){
              list.add(item);
            }
            break;
          }
        }
      }
      
      return list;
    }
    
    @Override
    public String toString() {
      StringBuffer r = new StringBuffer().append(OB).append(NL);
      for (Data item : this) {
        r.append(WS).append(item).append(NL);
      }
      return r.append(CB).toString();
    }
  }
  
  /**
   * Recreate view 
   * 
   * @param name view name
   * @param sql
   * @throws SQLException
   */
  public void recreateView(String name, String sql) throws SQLException{
    executeStatement("DROP VIEW IF EXISTS "+escapeName(name));
    executeStatement("CREATE VIEW "+escapeName(name)+" AS " +sql);
  }
  
  public boolean executeStatement(String sql){
    boolean r = false;
    try {
       getConnection().createStatement().execute(sql);
       r = true;
    } catch (SQLException e) {
      System.err.println(SQL_ERROR+sql);
    }
    return r;
  }
  
  /**
   * Data holder
   */
  public class Data extends HashMap<String, String> {

    private static final long serialVersionUID = 1L;
    
    public Data() {
    }
    
    public Data(ResultSet resultSet, String[] columns) throws SQLException {
      int length = columns.length;
      for (int i = 0; i < length; i++) {
        put(columns[i], resultSet.getString(i + 1));
      }
    }
    
    public String toTclList() {
      StringBuffer r = new StringBuffer().append(OB);
      for (String key : keySet()) {
        r.append(key).append(WS).append(get(key)).append(WS);
      }
      return r.append(CB).toString();
    }
    
    @Override
    public String toString() {
      return toTclList();
    }
  }


}
