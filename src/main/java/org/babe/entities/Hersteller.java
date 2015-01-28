package org.babe.entities;

import org.babe.App;

public class Hersteller {
  private Integer id;
  public String name;

  public Hersteller(String name) {
    this.name = name;
  }

  public Integer getId() {
    
    if(id != null) {
      return id;
    }
    
    id = App.db.getInt("SELECT id FROM `"+getClass().getSimpleName()+"` WHERE name LIKE '"+name+"'");
    if(id == null ) {
      id = App.save(this);
    }
    return id;
  }
  
}
