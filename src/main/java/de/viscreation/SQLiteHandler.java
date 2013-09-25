package de.viscreation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.sqlite.SQLite;

class SQLiteHandler {

  private static final SQLiteHandler dbcontroller = new SQLiteHandler();
  private static Connection          connection;

  public class Data extends HashMap<String, Object>{
    private static final long serialVersionUID = 1L;
    
    public Data(Object... args) {
      for (int i = 0; (i+1) < args.length; i+=2) {
         put( args[i].toString(), args[i+1] );
      }
    }
    
    @Override
    public Data put(String key, Object value) {
      super.put(key, value);
      return this;
    }
  }
  
  // load JDBC driver once
  static {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      System.err.println("Fehler beim Laden des JDBC-Treibers");
      e.printStackTrace();
    }
  }

  private SQLiteHandler() {
  }

  public static SQLiteHandler getInstance() {
    return dbcontroller;
  }

  private void init(String dbPath) {
    try {
      if (connection != null)
        return;
      System.out.println("Creating Connection to Database...");
      connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
      if (!connection.isClosed())
        System.out.println("...Connection established");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        try {
          if (!connection.isClosed() && connection != null) {
            connection.close();
            if (connection.isClosed())
              System.out.println("Connection to Database closed");
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void testSqlLite() {
    try {

      String tableName = "books";
      
      Data data = new Data(
         "author","Paulchen Paule"
        ,"title","Paul der Penner"
        ,"publication","2001-05-06")
         .put("pages",1234)
         .put("price",5.67);
      
      
      PreparedStatement stmt = connection.prepareStatement("PRAGMA ?");
      stmt.setString(1, "max_page_count");
      boolean execute = stmt.execute();
      
      if(false){
        return;
      }
      
//      int dropTable = dropTable(tableName);
//      ResultSet createTable = createTable(tableName);
      int dropTable = 0;
      
      stmt.execute("INSERT INTO books (author, title, publication, pages, price) VALUES ('Paulchen Paule', 'Paul der Penner', '2001-05-06', '1234', '5.67')");

      PreparedStatement ps = connection.prepareStatement("INSERT INTO books VALUES (?, ?, ?, ?, ?);");

      ps.setString(1, "Willi Winzig");
      ps.setString(2, "Willi's Wille");
      ps.setDate(3, Date.valueOf("2011-05-16"));
      ps.setInt(4, 432);
      ps.setDouble(5, 32.95);
      ps.addBatch();
      
      ps.setString(1, "Anton Antonius");
      ps.setString(2, "Anton's Alarm");
      ps.setDate(3, Date.valueOf("2009-10-01"));
      ps.setInt(4, 123);
      ps.setDouble(5, 98.76);
      ps.addBatch();

      connection.setAutoCommit(false);
      ps.executeBatch();
      connection.setAutoCommit(true);

      ResultSet rs = stmt.executeQuery("SELECT * FROM books;");
      while (rs.next()) {
        System.out.println("Autor = " + rs.getString("author"));
        System.out.println("Titel = " + rs.getString("title"));
        System.out.println("Erscheinungsdatum = " + rs.getString("publication"));
        System.out.println("Seiten = " + rs.getInt("pages"));
        System.out.println("Preis = " + rs.getDouble("price"));
      }
      rs.close();
      connection.close();
    } catch (SQLException e) {
      System.err.println("Couldn't handle DB-Query");
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
  
 

//  private ResultSet exec(PreparedStatement statement)  {
//    ResultSet result = null;
//    int executeUpdate = statement.executeUpdate();
//    try {
//      if(statement.executeBatch()){
//        result = statement.getResultSet();
//        result.close();
//      }
//    } catch (SQLException e) {
//      System.out.println("[Bad SQL]: " + sql);
//      e.printStackTrace();
//    }
//    return result;
//  }
  
  private ResultSet createTable(String name)  {
    ResultSet result = null;
    String sql = "CREATE TABLE IF NOT EXISTS "+name+" (id, title)";
    try {
      Statement statement = getStatement();
      if(statement.execute(sql)){
        result = statement.getResultSet();
        result.close();
      }
    } catch (SQLException e) {
      System.out.println("[Bad SQL]: " + sql);
      e.printStackTrace();
    }
    return result;
  }

  
  public int dropTable(String name) throws SQLException {
    return getStatement().executeUpdate("DROP TABLE IF EXISTS "+name);
  }

  public Statement getStatement() throws SQLException {
    return connection.createStatement();
  }

  public static void main(String[] args) {
    SQLiteHandler dbc = SQLiteHandler.getInstance();
    dbc.init(System.getProperty("user.home") + "/testdb.db");
    dbc.testSqlLite();
  }
}