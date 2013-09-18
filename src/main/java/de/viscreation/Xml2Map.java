package de.viscreation;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Xml2Map {

  private static Xml2Map instance = new Xml2Map();
  private NamedNodeMap   attributes;
  private Attr           attribute;

  public static XmlMap parseFile(String fileName) throws Exception {
    return parseXmlString(IOHelper.getFileContent(fileName));
  }
  
  public static XmlMap parseXmlString( String xmlContent ) {
    XmlMap xmlMap = null; 
    try {
      xmlMap = instance.parseDocument(instance.xml2doc(xmlContent).getFirstChild());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return xmlMap; 
  }

  public Document xml2doc(String xml) throws Exception {
    return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
  }

  /**
   * parse tag and add value to map as ArrayList of XmlMap
   * 
   * @param node
   * @param map
   * @return boolean
   */
  private boolean parseTag(Node node, XmlMap tag) {
    
    short noteType = node.getNodeType();
    
    if ( noteType == Node.TEXT_NODE) {
      String textContent = node.getTextContent();
      if(!textContent.trim().isEmpty()){
        tag.text(textContent);
      }
      return false;
    }
    
    if (node.getNodeType() != Node.ELEMENT_NODE) {
      return false;
    }
    
    String name = node.getNodeName();
    ArrayList<XmlMap> arrayList;
    HashMap<String, ArrayList<XmlMap>> tags = tag.tags;

    if (tags.containsKey(name)) {
      arrayList = tags.get(name);
    } else {
      arrayList = new ArrayList<XmlMap>();
      tag.tags.put(name, arrayList);
    }
    XmlMap childTag = parseDocument(node);
    childTag.parent = tag;
    arrayList.add(childTag);

    return true;
  }

  /**
   * parse tag and add attribute to map as ArrayList of XmlMap
   * 
   * @param node
   * @param map
   * @return boolean
   */
  private boolean parseAttribute(Node node, XmlMap tag) {
    if (node.getNodeType() != Node.ATTRIBUTE_NODE) {
      return false;
    }
    attribute = (Attr) node;
    tag.attributes.put(attribute.getName(), attribute.getValue());
    return true;
  }

  /**
   * parse node tags and attributes
   * 
   * @param rootNode
   * @return HashMap<String, Object>
   */
  public XmlMap parseDocument(Node rootNode) {

    XmlMap tag = new XmlMap(rootNode.getNodeName());

    NodeList nodes;
    int length;
    int i;

    // get tags
    nodes = rootNode.getChildNodes();
    length = nodes.getLength();
    for (i = 0; i < length; i++) {
      parseTag(nodes.item(i), tag);
    }

    // get attributes
    attributes = rootNode.getAttributes();
    length = attributes.getLength();
    for (i = 0; i < length; i++) {
      parseAttribute(attributes.item(i), tag);
    }
    
    //tag.text(rootNode.getNodeValue());

    return tag;
  }
}
