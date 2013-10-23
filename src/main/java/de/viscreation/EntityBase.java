package de.viscreation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.keynote.sigos.guixml.managers.entities.annotations.GuiXml;

/**
 * Base class to generate GuiXML speicific XML
 */
public class EntityBase {

  // constants
  protected static final String ROOT_TAG        = "row";
  protected static final String DATA_ATTRIBUTE  = "data";
  protected static final String NAME_ATTRIBUTE  = "name";
  protected static final String VALUE_ATTRIBUTE = "value";
  protected static final String TYPE_ATTRIBUTE  = "type";

  /**
   * Package field value to XML only for GuiXml
   * 
   * @param annotation
   * @param field
   * @return Gui XML 
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   */
  protected XmlMap packFieldToGuiXml(GuiXml annotation, Field field) throws IllegalArgumentException, IllegalAccessException {
    return new XmlMap(VALUE_ATTRIBUTE)
      .attr(NAME_ATTRIBUTE, annotation.name())
      .attr(DATA_ATTRIBUTE, field.get(this).toString())
      .attr(TYPE_ATTRIBUTE, field.getType().getSimpleName());
  }

  /**
   * Package fields to XML
   * 
   * @return
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   */
  public XmlMap toGuiXml() {
    XmlMap xmlMap = new XmlMap(ROOT_TAG);
    try {
      for (Field field : getClass().getFields()) {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
          if (annotation instanceof GuiXml) {
            xmlMap.addTag(packFieldToGuiXml((GuiXml) annotation,field));
            break;
          }
        }
      }
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return xmlMap;
  }

  @Override
  public String toString() {
    return toGuiXml().toString();
  }
}
