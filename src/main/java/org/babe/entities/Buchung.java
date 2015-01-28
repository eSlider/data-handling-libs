package org.babe.entities;

import org.babe.App;

public class Buchung {
  
  public Integer id;
  public String datum;
  public String preis;
  public Integer passagierId;
  public Integer fluegId;
  
  
  public Buchung(Integer id, String datum, String preis, Flueg flueg, Passagier passagier) {
    this.id = id;
    this.datum = datum;
    
    // bereite Preis vor
    this.preis = preis;
    this.fluegId = flueg.getId();
    this.passagierId = passagier.getId();
  }
  
  public void save(){
    if (!istBuchungBereitsVorhanden()) {
      id = App.save(this);
    }
  }

  private boolean istBuchungBereitsVorhanden() {
    return App.db.getInt("SELECT id FROM `Buchung` WHERE id=" + id) != null;
  }
  
}
