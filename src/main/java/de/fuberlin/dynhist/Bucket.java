package de.fuberlin.dynhist;

import java.io.Serializable;

/**
* @author Martin Görick
*
* Class to use for a bucket of the histogram.
* It's serializable to save the buckets to a file
*/
public class Bucket implements Serializable {

  private double leftBorder;
  private int count = 0;

  public Bucket(double leftBorder) {
    this.leftBorder = leftBorder;
    incCount();
  }

  public void incCount() {
    count++;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public double getLeftBorder() {
    return leftBorder;
  }

  /**
  * leftBorder is round to better readable
  * @param leftBorder
  */
  public void setLeftBorder(double leftBorder) {
    this.leftBorder = Math.round(leftBorder * 100) / 100.0;
  }
}
