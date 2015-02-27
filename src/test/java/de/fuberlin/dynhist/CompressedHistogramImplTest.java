package de.fuberlin.dynhist;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Martin Görick
 */
public class CompressedHistogramImplTest {

  private double[] input;
  private CompressedHistogram histogram;

  @Before
  public void setUp() {
    input = new double[]{1.4,1.5,1.2,1.2,2.2,4.1,5.5,3.6,2.7,2.8,6,7.2};
    int buckets = 6;
    histogram = new CompressedHistogramImpl(buckets);
  }

  @Test
  public void histogramTest() {
    for(double d : input) {
      histogram.addValue(d);
    }

    List<Bucket> hist = histogram.getHistogram();
    assertEquals(1.2, hist.get(0).getLeftBorder(), 0.1);
    assertEquals(1.4, hist.get(1).getLeftBorder(), 0.1);
    assertEquals(1.43, hist.get(2).getLeftBorder(), 0.1);
    assertEquals(2.2, hist.get(3).getLeftBorder(), 0.1);
    assertEquals(4.1, hist.get(4).getLeftBorder(), 0.1);
    assertEquals(5.5, hist.get(5).getLeftBorder(), 0.1);

    assertEquals(2 , hist.get(0).getCount());
    assertEquals(1 , hist.get(1).getCount());
    assertEquals(1 , hist.get(2).getCount());
    assertEquals(4 , hist.get(3).getCount());
    assertEquals(1 , hist.get(4).getCount());
    assertEquals(3 , hist.get(5).getCount());
  }
}
