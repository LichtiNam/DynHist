# DynHist

This is an  implementation of dynamic histogram.
It use Compressed Histogram as dynamic histogram.

### generate the jar file

Run `mvn package` to genreate the jar file.

### Using

import the `dynhist-<version>.jar` file in your program.

To work with the histogram:

```
CompressedHistogram histogram = new CompressedHistogramImpl(buckets);

histogram.addInput(Doublevalue);
```

The Histogram is a `List<Bucket>` if you handle with it.


