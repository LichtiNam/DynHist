package de.fuberlin.dynhist;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Martin Görick
 *
 * Implementation of a dynamic compressed histogram
 */
public class CompressedHistogram implements Histogram {

  private int buckets = 50;                   // default #buckets
  private List<Bucket> histogram;
  private String histFile = "histogram.dat";
  private boolean sorted = false;
  private double maxInput = 0;
  private double lbound = 0.1;                // lower bound for chiSquare check
  private double average = 0;                 // maxInput / buckets

  /**
   * Contractor of DynComHist use buckets of readFile is false
   * histFile use the first String parameter, parameter can be empty then default is used.
   */
  public CompressedHistogram(int buckets, boolean readFile, String ... parameter) {
    if (parameter.length > 0) {
      File file = new File(parameter[0]);
      if (file.exists()) {
        histFile = parameter[0];
      }
    }
    if (readFile) {
      initHistogram();
    } else {
      this.buckets = buckets;
      histogram = new ArrayList<Bucket>();
    }
    lbound = 1.0 / this.buckets;
  }

  private void initHistogram() {
    histogram = FileOperations.readHistogram(histFile);
    buckets = histogram.size();
    for (Bucket bucket : histogram) {
      maxInput += bucket.getCount();
    }
    average = maxInput / buckets;
    sorted = true;
  }

  /**
   * Write histogram to file with function of ComHistUtil class.
   */
  public void writeHist() {
    FileOperations.writeHistogram(histogram, histFile);
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
      CompareBuckets cb = new CompareBuckets();
      Collections.sort(histogram, cb);
    }
    if ( histogram.size() == buckets) {
      // sort list of buckets
      if (!sorted) {
        CompareBuckets cb = new CompareBuckets();
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
      if (chiSquare() > lbound) {
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
