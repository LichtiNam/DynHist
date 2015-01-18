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
  public CompressedHistogram (int buckets, boolean readFile, String ... parameter) {
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
    for (Bucket b : this.histogram) {
      maxInput += b.getCount();
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
      for (Bucket b : this.histogram) {
        if (b.getLeftBorder() == input) {
          b.incCount();
          return;
        }
      }
      Bucket b = new Bucket(input);
      this.histogram.add(b);
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
      for (int i = 0; i < this.buckets; i++) {
        if (this.histogram.get(i).getLeftBorder() > input) {
          if (i == 0) {
            this.histogram.get(0).incCount();
            this.histogram.get(0).setLeftBorder(input);
            break;
          } else {
            this.histogram.get(i - 1).incCount();
            break;
          }
        }
        if (i == this.buckets -1 && this.histogram.get(i).getLeftBorder() <= input) {
            this.histogram.get(i).incCount();
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
    for (Bucket b : this.histogram) {
      chi += ((b.getCount() - this.average) * (b.getCount() - this.average)) / this.average;
    }
    return chi;
  }

  private void repartition() {
    double currentBorder = this.histogram.get(0).getLeftBorder();
    int iAverage = (int) this.average;
    double nextBorder;
    double range;
    double newRightBorder;
    for (int i = 0; i < buckets-1; i++) {
      nextBorder = this.histogram.get(i+1).getLeftBorder();
      range = nextBorder - currentBorder;
      newRightBorder = (range / this.histogram.get(i).getCount()) * iAverage;
      currentBorder = this.histogram.get(i + 1).getLeftBorder();
      this.histogram.get(i+1).setLeftBorder(this.histogram.get(i).getLeftBorder() + newRightBorder);
      this.histogram.get(i).setCount(iAverage);
    }
    this.histogram.get(this.buckets-1).setCount((int) this.maxInput - (iAverage * (this.buckets - 1)));
  }
}
