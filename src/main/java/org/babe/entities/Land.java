package org.babe.entities;

import org.babe.App;

public class Land {
  private Integer id;
  public String name;

  public Land(String name) {
    this.name = name;
  }

  public Integer getId() {
    
    // wenn die ID vorhanden, gib zurück
    if(id != null) {
      return id;
    }
    
    // versuche die ID anhand der Name in der DB zu finden
    id = App.db.getInt("SELECT id FROM `Land` WHERE name LIKE '"+name+"'");
    
    if(id == null ) {
      id = App.save(this);
    }
    return id;
  }
  
}
