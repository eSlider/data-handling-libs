package org.babe.entities;

public class Linien {
  public Integer id;
  public String kurzel;
  public String flugDatum;
  public String flugdauer;

  public Linien(String kurzel, String flugDatum, String flugdauer) {
    this.kurzel = kurzel;
    this.flugDatum = flugDatum;
    this.flugdauer = flugdauer;
  }

}
