package de.fuberlin.dynhist;

import java.io.*;
import java.util.List;

/**
 * @author Martin Görick
 *
 * Class to operate with histogram files
 */
public class FileOperations {

  /**
   * Read histogram from file, need the same information like the write method
   * @return histogram as list of buckets
   */
  public static List<Bucket> readHistogram(String filename) {
    List<Bucket> histogram = null;
    try {
      File f = new File(filename);
      if (f.exists()) {
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        histogram = (List<Bucket>) ois.readObject();
        ois.close();
        fis.close();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return histogram;
  }

  /**
   * Write histogram to a file.
   * Buckets in list have to serializable.
   * @param histogram
   */
  public static void writeHistogram(List<Bucket> histogram, String filename) {
    try {
      FileOutputStream fos = new FileOutputStream(filename);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(histogram);
      oos.close();
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
