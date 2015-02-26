package de.fuberlin.dynhist;

import java.io.IOException;
import java.util.List;

/**
 * @author Martin Görick
 *
 * Interface to operate with histograms
 */
public interface CompressedHistogram {

  /**
   * Add a double value to the histogram.
   */
  public void addValue(double value);

  /**
   * Returns the list of buckets as histogram.
   */
  public List<Bucket> getHistogram();

  /**
   * Write histogram to the filePath.
   */
  public void writeHistogram(String filePath) throws IOException;

  /**
   * Returns the number of buckets.
   */
  public int getBuckets();

  /**
   * manually use of repartition
   */
  public void repartition();
}
