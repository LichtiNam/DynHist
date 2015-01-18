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
  private double lbound = 0.2;                // lower bound for chiSquare check
  private double average = 0;                 // maxInput / buckets

  /**
   * Contractor of DynComHist use buckets of readFile is false
   * histFile use the first String parameter, parameter can be empty then default is used.
   */
  public CompressedHistogram(int buckets, boolean readFile, String ... parameter) {
    if (parameter.length > 0) {
      File file = new File(parameter[0]);
      if (file.exists()) {
        this.histFile = parameter[0];
      }
    }
    if (readFile) {
      initHistogram();
    } else {
      this.buckets = buckets;
      this.histogram = new ArrayList<Bucket>();
    }
  }

  private void initHistogram() {
    this.histogram = FileOperations.readHistogram(this.histFile);
    this.buckets = this.histogram.size();
    for (Bucket bucket : this.histogram) {
      maxInput += bucket.getCount();
    }
    this.average = maxInput / buckets;
    this.sorted = true;
  }

  /**
   * Write histogram to file with function of ComHistUtil class.
   */
  public void writeHist() {
    FileOperations.writeHistogram(this.histogram, this.histFile);
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
    this.maxInput++;
    this.average = this.maxInput / this.buckets;
    int histSize = this.histogram.size();
    if (histSize < this.buckets) {
      for (Bucket bucket : this.histogram) {
        if (bucket.getLeftBorder() == input) {
          bucket.incCount();
          return;
        }
      }
      Bucket bucket = new Bucket(input);
      this.histogram.add(bucket);
      return;
    }
    if (!sorted) {
      CompareBuckets cb = new CompareBuckets();
      Collections.sort(this.histogram, cb);
    }
    if ( this.histogram.size() == buckets) {
      // sort list of buckets
      if (!this.sorted) {
        CompareBuckets cb = new CompareBuckets();
        Collections.sort(this.histogram, cb);
        this.sorted = true;
      }
      for (int idx = 0; idx < this.buckets; idx++) {
        if (this.histogram.get(idx).getLeftBorder() > input) {
          if (idx == 0) {
            this.histogram.get(0).incCount();
            this.histogram.get(0).setLeftBorder(input);
            break;
          } else {
            this.histogram.get(idx - 1).incCount();
            break;
          }
        }
        if (idx == this.buckets - 1 && this.histogram.get(idx).getLeftBorder() <= input) {
          this.histogram.get(idx).incCount();
        }
      }
      this.average = this.maxInput / this.buckets;

      // check chiSquare if true then repartition histogram
      if (chiSquare() > this.lbound) {
        repartition();
      }
    }
  }

  private double chiSquare() {
    double chi = 0;
    for (Bucket bucket : this.histogram) {
      chi += ((bucket.getCount() - this.average) * (bucket.getCount() - this.average))
          / this.average;
    }
    return chi;
  }

  private void repartition() {
    double currentBorder = this.histogram.get(0).getLeftBorder();
    int intAverage = (int) this.average;
    double nextBorder;
    double range;
    double newRightBorder;
    for (int idx = 0; idx < buckets - 1; idx++) {
      nextBorder = this.histogram.get(idx + 1).getLeftBorder();
      range = nextBorder - currentBorder;
      newRightBorder = (range / this.histogram.get(idx).getCount()) * intAverage;
      currentBorder = this.histogram.get(idx + 1).getLeftBorder();
      this.histogram.get(idx + 1).setLeftBorder(this.histogram.get(idx).getLeftBorder()
          + newRightBorder);
      this.histogram.get(idx).setCount(intAverage);
    }
    this.histogram.get(this.buckets - 1).setCount((int) this.maxInput
        - (intAverage * (this.buckets - 1)));
  }
}
