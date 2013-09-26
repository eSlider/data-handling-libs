package de.viscreation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * XML data manager
 */
public class XmlMap {
  

  /* tags container */
  public final HashMap<String, ArrayList<XmlMap>> tags       = new HashMap<String, ArrayList<XmlMap>>();
  
  /* attributes container */
  public final HashMap<String, String>            attributes = new HashMap<String, String>();
  
  /* tag name */
  public String                                   name;

  /* tag name */
  public String                                   _text      = EMPTY;
  
  /* parent XmlMap tag */
  public XmlMap                                   parent;                               
  

  /* constants to generate XML */
  public static final String                      xmlHead          = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; // xml head
  public static final char                        ES               = '"';                                         // escape char
  public static final char                        NL               = '\n';                                        // new line char
  public static final char                        TB               = '\t';                                        // tab char
  public static final char                        OB               = '<';                                         // open brace
  public static final char                        CB1              = '/';                                         // close brace char 1
  public static final char                        CB2              = '>';                                         // close brace char 2
  public static final char                        EQ               = '=';                                         // equals char
  public static final char                        EM               = ' ';                                         // empty char
  public static final String                      EMPTY            = "";                                          // empty string

  private static final String                     TAGS_SPLIT_REGEX = "/";

  /**
   * tag constructor 
   * 
   * @param name
   */
  public XmlMap(String name) {
    this.name = name;
  }
  
  /**
   * convert object to xml 
   * 
   * @param tab delimeter
   * @return String <
   */
  public String toXml( String d ) {
    StringBuilder t = new StringBuilder(); // attributes and result string 
    StringBuilder r = new StringBuilder();
    
    // get attributes
    for( String k : attributes.keySet() ){
      r.append(EM).append(k).append(EQ).append(ES).append(attributes.get(k)).append(ES);
    }
    
    int lenght = 0;
    for( String k : tags.keySet() ){
      for( XmlMap tag : tags.get(k) ){
        t.append(tag.toXml( new StringBuilder(d).append(TB).toString() )).append(NL); 
      }
      lenght++;
    }
    
    if (_text != EMPTY) {
      r = new StringBuilder().append(d)
          // <tagName>
          .append(OB).append(name).append(r).append(CB2)
            // text here
            .append(_text)
            .append(t)
          // </tagName>
          .append(OB).append(CB1).append(name).append(CB2);
    }else if(lenght < 1 ) {
      r = new StringBuilder().append(d).append(OB).append(name).append(r).append(CB1).append(CB2);
    }else{
      r = new StringBuilder().append(d).append(OB).append(name).append(r).append(CB2).append(NL).append(_text).append(t).append(d).append(OB).append(CB1).append(name).append(CB2);
    }
    
    return r.toString();
  }
  
  public XmlMap text(Object value) {
    _text = value == null?EMPTY:value.toString();
    return this;
  }
  
  public String text() {
    return _text;
  }
  
  /**
   * replace tag info
   * 
   * @param expr
   * @param value
   * @return
   */
  public XmlMap tag(String expr, Object value){
    XmlMap xmlMap = tags(expr).get(0);
    xmlMap.text(value);
    return xmlMap;
  }
  
  /**
   * get tag by name
   * 
   * @param expr
   * @return XmlMap
   */
  public XmlMap tag(String expr){
    XmlMap subTag = this;
    for (String tagName : expr.split(TAGS_SPLIT_REGEX)) {
        subTag = subTag.tags(tagName).get(0);
    }
    return subTag;
  }
  
  /**
   * get tags by name 
   * if nothing found, create and add the tag!
   * 
   * @param expr
   * @return
   */
  public ArrayList<XmlMap> tags(String searchTagName) {
    ArrayList<XmlMap> arrayList;
    XmlMap xmlMap;
    
    // get tag if doesn't exists
    if (tags.containsKey(searchTagName) ){
      arrayList = tags.get(searchTagName);
    } else {
      arrayList = new ArrayList<XmlMap>();
      tags.put(searchTagName, arrayList);
    }
    
    // create one tag if doesn't exists
    if (arrayList.size() <  1) {
      xmlMap = new XmlMap(searchTagName);
      xmlMap.parent = this;
      arrayList.add(xmlMap);
    } 

    return arrayList;
  }
  
  public XmlMap addTag(XmlMap tag){
    ArrayList<XmlMap> tagsList;
    String tagName = tag.name;
    tag.parent = this;
    
    // get tag if doesn't exists
    if (tags.containsKey(tagName) ){
      tagsList = tags.get(tagName);
    } else {
      tagsList = new ArrayList<XmlMap>();
      tags.put(tagName, tagsList);
    }
    tagsList.add(tag);
    
    return tag;
  }
  
  public XmlMap delAttr(String name) {
    attributes.remove(name);
    return this;
  }
  
  public XmlMap delTags(String name) {
    tags.remove(name);
    return this;
  }
  
  /**
   * get attribute 
   * 
   * @param key
   * @return String
   */
  public String attr(String key){
    return attributes.containsKey(key)? attributes.get(key):EMPTY;
  }
  
  /**
   * set attribute
   * 
   * @param key
   * @param value
   * @return String
   */
  public XmlMap attr(String key, Object value){
    attributes.put(key, value.toString());
    return this;
  }
  
  /**
   * convet object to XML
   * 
   * @return
   */
  public String toXml(){
    return toXml(EMPTY);
  }
  
  /**
   * convert object to string
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return new StringBuilder(xmlHead).append(NL).append(toXml()).toString();
  }
}