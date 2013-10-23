package de.viscreation;

import java.util.HashMap;

/**
 *  Key value container 
 */
public class Dict<V> extends HashMap<String, V> {
  private static final char NEW_LINE         = '\n';
  private static final char OPEN_BRACE1      = '[';
  private static final char CLOSE_BRACE1     = ']';
  private static final char EQUAL            = '=';
  private static final long serialVersionUID = 1L;
  private StringBuilder r;

  @Override
  public String toString() {
    r = new StringBuilder();
    for (String key : keySet()) {
       r.append(OPEN_BRACE1)
        .append(key)
        .append(CLOSE_BRACE1)
        .append(EQUAL)
        .append(get(key))
        .append(NEW_LINE);
    }
    return r.toString();
  }
}