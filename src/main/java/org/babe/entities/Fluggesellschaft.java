package org.babe.entities;

import org.babe.App;

public class Fluggesellschaft {
  private Integer id;
  public String name;
  public String kurzel;

  public Fluggesellschaft(  String kurzel, String name) {
    this.name = name;
    this.kurzel = kurzel;
  }
  
  public Integer getId() {
    
    if(id != null) {
      return id;
    }
    
    id = App.db.getInt(
          "SELECT id FROM `"+getClass().getSimpleName()+"` "
        + "WHERE name LIKE '"+name+"'"
        + "AND kurzel LIKE '"+kurzel+"'"
    );
    
    if(id == null ) {
      id = App.save(this);
    }
    return id;
  }
}
