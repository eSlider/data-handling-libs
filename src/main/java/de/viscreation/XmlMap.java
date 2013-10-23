package de.viscreation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * XML data manager
 * 
 * @author ano, Keynote SIGOS GmbH, 20.06.2013
 */
public class XmlMap {
  
  /** tags container */
  public final HashMap<String, ArrayList<XmlMap>> tags             = new HashMap<String, ArrayList<XmlMap>>();

  /** attributes container */
  public final Dict<String>                       attributes       = new Dict<String>();

  /** tag name */
  public String                                   name;

  /** tag name */
  private String                                  _text            = EMPTY;

  /** parent XmlMap tag */
  public XmlMap                                   parent;

  /* constants to generate XML */

  /** Request XML head */
  public static final String                      xmlHead          = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; // xml head
  public static final char                        QUOT_CHAR        = '\"';                                        // escape char
  public static final char                        NL               = '\n';                                        // new line char
  public static final char                        TB               = '\t';                                        // tab char
  public static final char                        LT_CHAR          = '<';                                         // open brace
  public static final char                        GT_CHAR          = '>';                                         // close brace char 2
  public static final char                        CB1              = '/';                                         // close brace char 1
  public static final char                        EQ               = '=';                                         // equals char
  public static final char                        EM               = ' ';                                         // empty char
  private static final char                       AMP_CHAR         = '&';                                         // amp char
  private static final char                       APOS_CHAR        = '\'';                                        // apostroph char

  private static final String                     AMP              = "&amp;";
  private static final String                     APOS             = "&apos;";
  private static final String                     GT               = "&gt;";
  private static final String                     QUOT             = "&quot;";
  private static final String                     LT               = "&lt;";

  /** Empty string */
  public static final String                      EMPTY            = "";

  /** String to split tags by */
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
   * encode char
   * 
   * @param value string or char
   * @return object value
   */
  public static Object encodeChar(char value) {
    switch (value) {
      case LT_CHAR:     return LT;
      case GT_CHAR:     return GT;
      case QUOT_CHAR:   return QUOT;
      case APOS_CHAR:   return APOS;
      case AMP_CHAR:    return AMP;
    }
    return value;
  }
  
  /**
   * encode XML attribute
   * 
   * @param value
   * @return encoded string
   */
  public static String encodeAttribute(String value) {
    if (value == null) {
      return EMPTY;
    }

    int len = value.length();
    
    if (len == 0) {
      return EMPTY;
    }

    StringBuffer encoded = new StringBuffer();
    for (int i = 0; i < len; i++) {
      encoded.append(encodeChar(value.charAt(i)));
    }
    
    return encoded.toString();
  }
  
  public String decodeAttribute(String value) {
    return value;
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
      r.append(EM).append(k).append(EQ).append(QUOT_CHAR).append(
          encodeAttribute(attributes.get(k))
        ).append(QUOT_CHAR);
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
        .append(LT_CHAR).append(name).append(r).append(GT_CHAR)
          // text here
          .append(_text)
          .append(t)
        // </tagName>
        .append(LT_CHAR).append(CB1).append(name).append(GT_CHAR);
    }else if(lenght < 1 ) {
      r = new StringBuilder().append(d).append(LT_CHAR).append(name).append(r).append(CB1).append(GT_CHAR);
    }else{
      r = new StringBuilder().append(d).append(LT_CHAR).append(name).append(r).append(GT_CHAR).append(NL).append(_text).append(t).append(d).append(LT_CHAR).append(CB1).append(name).append(GT_CHAR);
    }
    
    return r.toString();
  }
  
  public XmlMap text(Object value) {
    _text = value == null?EMPTY:value.toString();
    return this;
  }
  
  /**
   * Get tag text content
   * 
   * @return text content
   */
  public String text() {
    return _text;
  }
  
  /**
   * Replace tag info
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
   * Get or create tag by name
   * 
   * @param expr
   * @return XmlMap
   */
  public XmlMap tag(String expr){
    return tags(expr).get(0);
  }
  
  /**
   * Get or create tags by name.
   * 
   * @param searchTagName tag name to search
   * @return list of XmlMap's
   */
  public ArrayList<XmlMap> tags(String expr) {
    ArrayList<XmlMap> arrayList = null;
    XmlMap xmlMap;
    
    HashMap<String, ArrayList<XmlMap>> tags = this.tags;
    
    for (String tagName : expr.split(TAGS_SPLIT_REGEX)) {
      
      // get tag if doesn't exists
      if (tags.containsKey(tagName) ){
        arrayList = tags.get(tagName);
      } else {
        arrayList = new ArrayList<XmlMap>();
        tags.put(tagName, arrayList);
      }
      
      // create one tag if doesn't exists
      if (arrayList.size() <  1) {
        xmlMap = new XmlMap(tagName);
        xmlMap.parent = this;
        arrayList.add(xmlMap);
      } 
      
      tags = arrayList.get(0).tags;
    }

    return arrayList;
  }
  
  /**
   * Add new tag
   * 
   * @param tag xml tag
   * @return  same tag as argument given
   */
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
   * Get tag attribute 
   * 
   * @param key - attribute key 
   * @return attribute value
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
   * @return XML string
   */
  public String toXml(){
    return toXml(EMPTY);
  }
  
  /**
   * convert object to string
   * 
   * @see java.lang.Object#toString()
   * @return XML string with head
   */
  public String toString() {
    return new StringBuilder(xmlHead).append(NL).append(toXml()).append(NL).toString();
  }
}