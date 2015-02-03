package de.fuberlin.dynhist;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Martin Görick
 */
public class CompressedHistogramTest {

  private double[] input;
  private int buckets;
  private Histogram histogram;

  @Before
  public void setUp() {
    input = new double[]{1.4,1.5,1.2,1.2,2.2,4.1,5.5,3.6,2.7,2.8,6,7.2};
    buckets = 6;
    histogram = new CompressedHistogram(buckets, 1);
  }

  @Test
  public void histogramTest() {
    for(double d : input) {
      histogram.addInput(d);
    }

    List<Bucket> hist = histogram.getHistogram();
    assertEquals(1.2, hist.get(0).getLeftBorder(), 0.1);
    assertEquals(1.4, hist.get(1).getLeftBorder(), 0.1);
    assertEquals(1.6, hist.get(2).getLeftBorder(), 0.1);
    assertEquals(3.0, hist.get(3).getLeftBorder(), 0.1);
    assertEquals(3.96, hist.get(4).getLeftBorder(), 0.1);
    assertEquals(5.36, hist.get(5).getLeftBorder(), 0.1);

    for (Bucket bucket : hist) {
      assertEquals(2 , bucket.getCount());
    }
  }
}
