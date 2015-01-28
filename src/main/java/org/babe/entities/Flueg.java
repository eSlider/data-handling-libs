package org.babe.entities;

import org.babe.App;

public class Flueg {
  private Integer id;
  
  public String dauer;
  public String plaetzeBelegt;
  
  public String datum;
  public Integer linieId;
  public Integer geselschaftId;
  public Integer flugzeugtypId;
  

  public Flueg(String dauer, String plaetzeBelegt, String datum, Linie linie, Fluggesellschaft geselschaft, Flugzeugtyp flugzeugtyp) {
    
    this.dauer = dauer;
    this.plaetzeBelegt = plaetzeBelegt;
    this.datum = datum;
    
    linieId = linie.getId();
    geselschaftId = geselschaft.getId();
    flugzeugtypId = flugzeugtyp.getId();
  }

  public Integer getId() {
    
    if(id != null) {
      return id;
    }
    
    id = App.db.getInt(
          "SELECT id FROM `"+getClass().getSimpleName()+"` "
        + "WHERE datum LIKE '"+datum+"'"
        + "AND linieId = '"+linieId+"'"
        + "AND geselschaftId ='"+geselschaftId+"'"
        + "AND flugzeugtypId ='"+flugzeugtypId+"'"
    );
    
    if(id == null ) {
      id = App.save(this);
    }
    return id;
  }
  
}
