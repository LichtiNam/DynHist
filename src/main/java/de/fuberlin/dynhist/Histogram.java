package de.fuberlin.dynhist;

import java.util.List;

/**
 * @author Martin Görick
 *
 * Interface to operate with histograms
 */
public interface Histogram {

  /**
   * add a double to the histogram.
   * @param input
   */
  public void addInput(double input);

  /**
   * Returns the list of buckets as histogram.
   */
  public List<Bucket> getHistogram();

  /**
   * Write histogram if to filePath.
   * @param filePath
   */
  public void writeHistogram(String filePath);

  /**
   * Read histogram from filePath.
   * @param filePath
   */
  public void readHistogram(String filePath);

  /**
   * Returns the number of buckets.
   */
  public int getBuckets();
}
