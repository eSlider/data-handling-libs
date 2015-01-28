package de.viscreation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public class EntityBase {

  // constants
  protected static final String ROOT_TAG        = "row";
  protected static final String DATA_ATTRIBUTE  = "data";
  protected static final String NAME_ATTRIBUTE  = "name";
  protected static final String VALUE_ATTRIBUTE = "value";
  protected static final String TYPE_ATTRIBUTE  = "type";

  /**
   * Package field value to XML only for CustomAnnotation
   * 
   * @param annotation
   * @param field
   * @return Gui XML 
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   */
  protected XmlMap packFieldToCustomAnnotation(CustomAnnotation annotation, Field field) throws IllegalArgumentException, IllegalAccessException {
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
  public XmlMap toCustomAnnotation() {
    XmlMap xmlMap = new XmlMap(ROOT_TAG);
    try {
      for (Field field : getClass().getFields()) {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
          if (annotation instanceof CustomAnnotation) {
            xmlMap.addTag(packFieldToCustomAnnotation((CustomAnnotation) annotation,field));
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
    return toCustomAnnotation().toString();
  }
}
