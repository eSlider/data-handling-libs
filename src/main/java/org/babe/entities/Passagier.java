package org.babe.entities;

import org.babe.App;

public class Passagier {
  private Integer id;
  
  public String nummer;
  public String anrede;
  public String name;
  public String plz;
  public String ort;
  public String strasse;
  public String land;
  
  public Passagier(String nummer, String anrede, String name, String plz,
      String ort, String strasse, String land) {
    this.nummer = nummer;
    this.anrede = anrede;
    this.name = name;
    this.plz = plz;
    this.ort = ort;
    this.strasse = strasse;
    this.land = land;
  }
  
  public Integer getId() {
    
    if (id != null) {
      return id;
    }
    
    id = App.db.getInt("SELECT id FROM `" + getClass().getSimpleName() + "` "
        + "WHERE nummer LIKE '" + nummer + "'");
    
    if (id == null) {
      id = App.save(this);
    }
    return id;
  }
  
}
