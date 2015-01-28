package org.babe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;

import org.babe.entities.Buchung;
import org.babe.entities.Flueg;
import org.babe.entities.Fluggesellschaft;
import org.babe.entities.Flughafen;
import org.babe.entities.Flugzeugtyp;
import org.babe.entities.Hersteller;
import org.babe.entities.Land;
import org.babe.entities.Linie;
import org.babe.entities.Passagier;
import org.babe.libs.IOHelper;
import org.babe.libs.MySqlHandler;

/**
 * Reiseveranstaltungssoftware
 * 
 * @author Michael Billiy
 * @version 1.02
 */
public class App {
  
  public static MySqlHandler db;
  
  /**
   * Reiseveranstaltungssoftware
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    
    // initiere Datenbank Verbindung Aufbau
    db = new MySqlHandler("jdbc:mysql://localhost/fluge", "root", "blanco");
    
    // lösche daten aus der Datenbanktabellen (nur für's testen)
    //bereinigeDatenbank();
    
    // importiere csv Dateien
    importCsvFiles("data/csv");
  }

  /**
   * importiert Buchungen als CSV Dateien aus "data/csv" Verzeichniss 
   * @param pfad 
   * 
   * 
   * @throws FileNotFoundException
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws SQLException
   */
  private static void importCsvFiles(String pfad) throws FileNotFoundException, IOException, IllegalArgumentException, IllegalAccessException, SQLException {
    
    // holle Dateinamen von CSV Verzeichniss
    File csvFolder = new File(pfad);
    
    for (File csvFile : csvFolder.listFiles()) {

      System.out.println("Lese CSV Datei: " + csvFile);
      
      // hollt die Datei Inhalt
      String csv = IOHelper.getFileContent(csvFile);
      
      // trenne die Zeilen der Dateinhalt
      String[] csvLines = csv.split("\\r?\\n");
      
      // gehe durch alle Linien in CSV
      // Ausnahmen:
      //  * die ersten zwei Linien sind die Bezeichnungen
      //  * die letzen zwei Linien beinhalten Statistik und lehres String
      for (int i = 2; i < csvLines.length-2; i++) {
        
        // trenne die Zeile in Zellen
        String[] cells = csvLines[i].split(";");

        // holle Länder
        Land vonLand        = new Land(cells[5]);
        Land nachLand       = new Land(cells[8]);
        
        // holle Flughäfen
        Flughafen startFlughafen = new Flughafen(cells[4], cells[6], vonLand);
        Flughafen zielFlughafen = new Flughafen(cells[7], cells[9], nachLand);
        
        // holle die Daten für Fluggesellschaft
        Fluggesellschaft geselschaft = new Fluggesellschaft(cells[0], cells[1]);
        
        // holle Hersteller
        Hersteller hersteller = new Hersteller(cells[14]);
        
        // holle die Daten für Flugzeugtyp
        Flugzeugtyp flugzeugtyp = new Flugzeugtyp(cells[13], cells[16], hersteller);
        
        // holle die Daten für Fluglinien
        Linie linie = new Linie(cells[3], startFlughafen, zielFlughafen);
        
        // holle die Daten für Flüge
        Flueg flueg = new Flueg(cells[10],cells[15], konvertiereDatum(cells[11]), linie, geselschaft, flugzeugtyp);
        
        // holle die Daten für Passagier
        Passagier passagier = new Passagier (cells[19], cells[20], cells[21], cells[22], cells[23], cells[24], cells[25]);
        
        // holle die Daten für Buchungen
        Buchung Buchungen = new Buchung( Integer.parseInt(cells[17]), konvertiereDatum(cells[18]), konvertierePreis(cells[12]), flueg, passagier);
        
        // Buchung abspeichern
        Buchungen.save();
      }
    }
  }
  
  /**
   * konvertiere Import-Datum in MySql Datum
   * 
   * @param datum 
   * @return MySql datum
   */
  public static String konvertiereDatum(String datum) {
    String[] teile = datum.split("\\.");
    return teile[2]+"-"+teile[1]+"-"+teile[0];
  }
 
  /**
   * konvertiere Import Preis in MySql double
   * 
   * @param preis
   * @return MySql double
   */
  public static String konvertierePreis(String preis) {
    return preis.replace(".","").replace(',','.');
  }
  
  /**
   * Speichern von Objekten
   * 
   * @param value object
   * @return 
   */
  public static Integer save(Object valueObject) {
    
    Field[] fields = valueObject.getClass().getFields();
    String name = valueObject.getClass().getSimpleName();
    HashMap<String, Object> data = new HashMap<String, Object>();
    Integer id = null;
    
    
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      String key = field.getName();
      Object value = null;
      
      try {
        value = field.get(valueObject);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
      
      if(value == null) {
        continue;
      }
      
      data.put(key, value);
    }
    try {
      id = (int) db.insert(name, data);
      System.out.println("Import von " + name + " war erfolgreich: " + data.toString());
    } catch (SQLException e) {
      System.out.println("Import von " + name + " fehlgeschlagen " + data.toString());
      e.printStackTrace();
    }
    
    return id;
  }
  
  /**
   * Datenbank tabellen komplett bereinigen
   * 
   * @throws SQLException
   */
  private static void bereinigeDatenbank() throws SQLException {
    String[] tables = new String[] {"buchung","flueg","Fluggesellschaft","flughafen","flugzeugtyp","hersteller","land","linie","passagier"};
    for (int i = 0; i < tables.length; i++) {
      db.truncateTable(tables[i]);
    }
  }
  
}
