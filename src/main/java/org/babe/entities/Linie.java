package org.babe.entities;

import org.babe.App;

public class Linie {
  private Integer id;
  public String nummer;
  public Integer vonFlughafenId;
  public Integer nachFlughafenId;

  public Linie(String nummer, Flughafen startFlughafen, Flughafen zielFlughafen) {
    this.nummer = nummer;
    vonFlughafenId = startFlughafen.getId();
    nachFlughafenId = zielFlughafen.getId();
  }
  
  public Integer getId() {
    
    if(id != null) {
      return id;
    }
    
    id = App.db.getInt(
          "SELECT id FROM `"+getClass().getSimpleName()+"` "
        + "WHERE nummer LIKE '"+nummer+"'"
        + "AND vonFlughafenId = '"+vonFlughafenId+"'"
        + "AND nachFlughafenId ='"+nachFlughafenId+"'"
    );
    
    if(id == null ) {
      id = App.save(this);
    }
    return id;
  }

}
