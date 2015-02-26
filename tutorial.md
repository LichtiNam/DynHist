### Short tutorial to use DynHist

#### Simple usage

```
int buckets = 40;

CompressedHistogram histogram = new CompressedHistogramImpl(buckets);
histogram.addInput(value);
List<Bucket> hist = histogram.getHistogram();

for (Bucket bucket : hist) {
  System.out.println("Bucket leftBorder: " + bucket.getLeftBorder() + ", count:
  " + bucket.getCount());
}
```

#### Read / Write

```
String filePath "histogram.dat";

CompressedHistogram histogram = new CompressedHistigramImpl(filePath);

System.out.println("Buckets of readed histogram: "+ histogram.getBuckets);

// write histogram
histogram.writeHistogram(filePath);
```
