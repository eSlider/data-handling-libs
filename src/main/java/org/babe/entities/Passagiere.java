package org.babe.entities;

public class Passagiere {
  public Integer id;
  public String externId;
  public String anrede;
  public String name;
  public String plz;
  public String ort;
  public String strasse;
  public String land;


  public Passagiere(String externId, String anrede, String name, String plz, String ort, String strasse) {
    this.externId = externId;
    this.anrede = anrede;
    this.name = name;
    this.plz = plz;
    this.ort = ort;
    this.strasse = strasse;

  }

 

}
