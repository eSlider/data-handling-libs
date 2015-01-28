package org.babe.entities;

import org.babe.App;

public class Flugzeugtyp {
  private Integer id;
  public String typ;
  public String plaetzeGesamt;
  public Integer herstellerId;
  
  public Flugzeugtyp(String typ, String plaetzeGesamt, Hersteller hersteller) {
    this.typ = typ ;
    this.plaetzeGesamt = plaetzeGesamt;
    herstellerId = hersteller.getId();
  }
  
  public Integer getId() {
    
    if(id != null) {
      return id;
    }
    
    id = App.db.getInt("SELECT id FROM `"+getClass().getSimpleName()+"` "
        + "WHERE typ LIKE '"+typ+"' "
        + "AND herstellerId="+herstellerId);
    
    if(id == null ) {
      id = App.save(this);
    }
    return id;
  }
}
