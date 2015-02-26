package de.fuberlin.dynhist;

import java.util.Comparator;

/**
 * @author Martin Görick
 *
 * Comparator class to sort the buckets by leftBorder.
 */
public class CompareBuckets implements Comparator<Bucket> {

  /**
   * Return the test of the borders of two buckets.
   */
  public int compare(Bucket bucket1, Bucket bucket2) {
    if (bucket1.getLeftBorder() > bucket2.getLeftBorder()) {
      return 1;
    }
    return -1;
  }
}
