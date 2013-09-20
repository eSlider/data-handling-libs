package de.viscreation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

class SQLiteHandler {

  private static final SQLiteHandler dbcontroller = new SQLiteHandler();
  private static Connection          connection;

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
      Statement stmt = getStatement();
      String tableName = "books";
      
      HashMap<String, Object> data = new HashMap<String, Object>() {{
        put("author","Paulchen Paule");
        put("title","Paul der Penner");
        put("publication","2001-05-06");
        put("pages",1234);
        put("price",5.67);
      }};
      
      Map<String,String> data1 = ImmutableMap.of(
          "author","Paulchen Paule",
          "title","Paul der Penner",
          "publication","2001-05-06",
          "pages", "1234",
          "price", "5.67"
      );
      
      
      Preconditions.checkNotNull(data1);
      
      
      dropTable(tableName);
      createTable(tableName);
      
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
      e.printStackTrace();
    }
  }

  private void createTable(String name) throws SQLException {
    getStatement().executeUpdate("CREATE TABLE "+name+" (id, title)");
  }

  
  public int dropTable(String name) throws SQLException {
    return getStatement().executeUpdate("DROP TABLE IF EXISTS"+name);
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