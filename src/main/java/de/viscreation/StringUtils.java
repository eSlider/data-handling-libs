package de.viscreation;

public class StringUtils {
  public static String combine(Object[] objects, String glue) {
    int l = objects.length;
    int m = l-1;
    int x;

    if (l == 0){
      return null;
    }
    
    StringBuilder r = new StringBuilder();
    
    for (x = 0; x < m; x++){
      r.append(objects[x]).append(glue);
    }
    r.append(objects[x]);
    return r.toString();
  }
}
