package org.babe.entities;

import org.babe.App;

public class Buchungen extends App {
  public String externId;
  public String buchungsDatum;
  public String preis;
  public String belegteplaetze;
  public String passagierId;
 //public Integer linienId;
  
  
  public Buchungen(String externId, String buchDatum, String preis, String belegteplaetze, String passagierId ) {
    this.externId = externId;
    this.buchungsDatum = buchungsDatum;
    this.preis = preis;
    this.belegteplaetze = belegteplaetze;
    this.passagierId = passagierId;

  }

}
