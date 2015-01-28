package org.babe.entities;

import org.babe.App;

public class Flughafen {
  
  private Integer id;
  
  public String kurzel;
  public String stadt;
  public Integer landId;
  
  public Flughafen ( String kurzel ,String stadt, Land land ){
    this.kurzel  = kurzel ;
    this.stadt = stadt;
    this.landId = land.getId();
  }
  
  public Integer getId() {
    
    if(id != null) {
      return id;
    }
    
    id = App.db.getInt(
      "SELECT id FROM `"+getClass().getSimpleName()
      +"` WHERE kurzel LIKE '"+kurzel+"'"
    );
    
    if(id == null ) {
      id = App.save(this);
    }
    return id;
  }
}
