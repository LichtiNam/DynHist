package de.fuberlin.dynhist;

import java.io.Serializable;

/**
* @author Martin Görick
*
* Class to use for a bucket of the histogram.
* It's serializable to save the buckets to a file.
*/
public class Bucket implements Serializable {

  private double leftBorder;
  private int count = 0;

  /**
   * Constructor for a bucket.
   * @param leftBorder of the bucket
   */
  public Bucket(double leftBorder) {
    this.leftBorder = leftBorder;
    incCount();
  }

  /**
   * Increment count of the bucket.
   */
  public void incCount() {
    count++;
  }

  /**
   * Returns the count of the bucket.
   */
  public int getCount() {
    return count;
  }

  /**
   * Set count, it use for partition.
   */
  public void setCount(int count) {
    this.count = count;
  }

  /**
   * Returns the left border.
   */
  public double getLeftBorder() {
    return leftBorder;
  }

  /**
  * Set left border, round to better readable.
  */
  public void setLeftBorder(double leftBorder) {
    this.leftBorder = Math.round(leftBorder * 100) / 100.0;
  }
}
