package de.fuberlin.dynhist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Martin Görick
 *
 * Implementation of a dynamic compressed histogram
 */
public class CompressedHistogram implements Histogram {

  private int buckets;
  private List<Bucket> histogram;
  private boolean sorted = false;
  private double maxInput = 0;
  private double lowerBound;      // lower bound for chiSquare check, 1.0 / # buckets
  private double average = 0;     // maxInput / buckets

  private CompareBuckets cb;

  /**
   * Constructor of CompressedHistogram with # of buckets as input.
   */
  public CompressedHistogram(int buckets) {
    this.buckets = buckets;
    cb = new CompareBuckets();
    histogram = new ArrayList<>();
    lowerBound = 1.0 / this.buckets;
  }

  /**
   * Constructor of CompressedHistogram with filePath to read a histogram from file.
   *
   */
  public CompressedHistogram(String filePath) {
    initHistogram(filePath);
  }


  private void initHistogram(String filePath) {
    histogram = FileOperations.readHistogram(filePath);
    buckets = histogram.size();
    for (Bucket bucket : histogram) {
      maxInput += bucket.getCount();
    }
    average = maxInput / buckets;
    sorted = true;
    lowerBound = 1.0 / buckets;
  }

  /**
   * Returns number of buckets.
   */
  public int getBuckets() {
    return buckets;
  }

  /**
   * Write histogram to file with function of ComHistUtil class.
   */
  public void writeHistogram(String filePath) {
    FileOperations.writeHistogram(histogram, filePath);
  }

  /**
   * Returns histogram as a list of buckets.
   */
  public List<Bucket> getHistogram() {
    return histogram;
  }

  /**
   * Add an double value to histogram. Create extra bucket till no bucket are empty.
   * check the chiSquare if all buckets filled and repartition the histogram.
   * @param input a double value
   */
  public void addInput(double input) {
    maxInput++;
    average = maxInput / buckets;
    int histSize = histogram.size();
    if (histSize < buckets) {
      for (Bucket bucket : histogram) {
        if (bucket.getLeftBorder() == input) {
          bucket.incCount();
          return;
        }
      }
      Bucket bucket = new Bucket(input);
      histogram.add(bucket);
      return;
    }
    if (!sorted) {
      Collections.sort(histogram, cb);
    }
    if ( histogram.size() == buckets) {
      if (!sorted) {
        Collections.sort(histogram, cb);
        sorted = true;
      }
      for (int idx = 0; idx < buckets; idx++) {
        if (histogram.get(idx).getLeftBorder() > input) {
          if (idx == 0) {
            histogram.get(0).incCount();
            histogram.get(0).setLeftBorder(input);
            break;
          } else {
            histogram.get(idx - 1).incCount();
            break;
          }
        }
        if (idx == buckets - 1 && histogram.get(idx).getLeftBorder() <= input) {
          histogram.get(idx).incCount();
        }
      }
      average = maxInput / buckets;
      
      // check chiSquare if true then repartition histogram
      if (chiSquare() > lowerBound) {
        repartition();
      }
    }
  }

  private double chiSquare() {
    double chi = 0;
    for (Bucket bucket : histogram) {
      chi += ((bucket.getCount() - average) * (bucket.getCount() - average))
          / average;
    }
    return chi;
  }

  private void repartition() {
    double currentBorder = histogram.get(0).getLeftBorder();
    int intAverage = (int) average;
    double nextBorder;
    double range;
    double newRightBorder;
    for (int idx = 0; idx < buckets - 1; idx++) {
      nextBorder = histogram.get(idx + 1).getLeftBorder();
      range = nextBorder - currentBorder;
      newRightBorder = (range / histogram.get(idx).getCount()) * intAverage;
      currentBorder = histogram.get(idx + 1).getLeftBorder();
      histogram.get(idx + 1).setLeftBorder(histogram.get(idx).getLeftBorder()
          + newRightBorder);
      histogram.get(idx).setCount(intAverage);
    }
    histogram.get(buckets - 1).setCount((int) maxInput
        - (intAverage * (buckets - 1)));
  }
}
