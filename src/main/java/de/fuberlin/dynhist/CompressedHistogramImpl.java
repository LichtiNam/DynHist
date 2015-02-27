package de.fuberlin.dynhist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Martin Görick
 *
 * Implementation of a dynamic compressed histogram
 */
public class CompressedHistogramImpl implements CompressedHistogram {

  private int buckets;
  private List<Bucket> histogram;
  private boolean sorted = false;
  private double maxInput = 0;
  private double alpha = 0.0001;
  private double average = 0;     // maxInput / buckets

  private CompareBuckets cb;

  /**
   * Constructor of CompressedHistogram with # of buckets as input.
   */
  public CompressedHistogramImpl(int buckets) {
    this.buckets = buckets;
    cb = new CompareBuckets();
    histogram = new ArrayList<>();
  }

  /**
   * Constructor of CompressedHistogram with filePath to read a histogram from file.
   *
   */
  public CompressedHistogramImpl(String filePath) throws Exception {
    initHistogram(filePath);
  }


  private void initHistogram(String filePath) throws Exception {
    histogram = FileOperations.readHistogram(filePath);
    buckets = histogram.size();
    for (Bucket bucket : histogram) {
      maxInput += bucket.getCount();
    }
    average = maxInput / buckets;
    sorted = true;
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
  public void writeHistogram(String filePath) throws IOException {
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
   * @param value a double value
   */
  public void addValue(double value) {
    maxInput++;
    average = maxInput / buckets;
    int histSize = histogram.size();
    if (histSize < buckets) {
      for (Bucket bucket : histogram) {
        if (bucket.getLeftBorder() == value) {
          bucket.incCount();
          return;
        }
      }
      Bucket bucket = new Bucket(value);
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
        if (histogram.get(idx).getLeftBorder() > value) {
          if (idx > 0) {
            histogram.get(idx - 1).incCount();
            break;
          }
        }
        if (idx == buckets - 1 && histogram.get(idx).getLeftBorder() <= value) {
          histogram.get(idx).incCount();
        }
      }
      average = maxInput / buckets;

      if (MathUtil.chiSquareProb(2,chiSquare()) < alpha) {
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

  /**
   * Repartition method, can also be use manually
   */
  public void repartition() {
    double currentBorder = histogram.get(0).getLeftBorder();
    int intAverage = (int) average;
    double nextBorder;
    double range;
    double newRightBorder;
    double rest = 0;
    int set;
    int setAll = 0;
    for (int idx = 0; idx < buckets - 1; idx++) {
      rest += average - intAverage;
      if (rest - (int) rest > 0.5) {
        set = intAverage + 1;
      } else {
        set = intAverage;
      }
      setAll += set;
      nextBorder = histogram.get(idx + 1).getLeftBorder();
      range = nextBorder - currentBorder;
      newRightBorder = (range / histogram.get(idx).getCount()) * set;
      currentBorder = histogram.get(idx + 1).getLeftBorder();
      histogram.get(idx + 1).setLeftBorder(histogram.get(idx).getLeftBorder()
          + newRightBorder);
      histogram.get(idx).setCount(set);
    }

    histogram.get(buckets - 1).setCount((int) maxInput - setAll);
  }
}