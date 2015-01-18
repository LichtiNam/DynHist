package de.fuberlin.dynhist;

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
   * Returns the histogram. Have to be cast.
   */
  public Object getHistogram();

  /**
   * write histogram if to file if buckets serializable
   */
  public void writeHist();
}
