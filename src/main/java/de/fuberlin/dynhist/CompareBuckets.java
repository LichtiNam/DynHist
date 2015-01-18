package de.fuberlin.dynhist;

import java.util.Comparator;

/**
 * @author Martin Görick
 *
 * Comparator class to sort the buckets by leftBorder.
 */
public class CompareBuckets implements Comparator<Bucket> {

  public int compare(Bucket o, Bucket t1) {
    if (o.getLeftBorder() > t1.getLeftBorder()) {
      return 1;
    }
    return -1;
  }
}
