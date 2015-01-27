### Short tutorial to use DynHist

#### Simple usage

```
int buckets = 40;

Histogram histogram = new Histogram(buckets);
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

Histogram histogram = new Histigram(filePath);

System.out.println("Buckets of readed histogram: "+ histogram.getBuckets);

// write histogram
histogram.writeHistogram(filePath);
```
