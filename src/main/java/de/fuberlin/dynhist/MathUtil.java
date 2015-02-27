package de.fuberlin.dynhist;

/**
 * @author Martin Goerick
 * Class for Math operations, especily chi square probability
 */
public class MathUtil {

  /**
   * Returns chi-square probability.
   */
  public static double chiSquareProb(int number, double value) {
    if (number % 2 == 0) {
      return chiSquareProbEven(number,value);
    } else {
      return chiSquareProbOdd(number,value);
    }
  }

  /**
   * Returns chi-square probability for even numbers number.
   */
  public static double chiSquareProbEven(int number, double value) {
    double result = 0;
    for (int idx = 0; idx <= (number / 2 - 1); idx++) {
      result += (1 / gammaFunction(idx + 1)) * Math.pow(value / 2, idx);
    }
    return Math.exp(-value / 2) * result;
  }

  /**
   * Returns chi-square probability for odd numbers number.
   */
  public static double chiSquareProbOdd(int number, double value) {
    double result = 0;
    for (int idx = 0; idx <= (number / 2 - 1); idx++) {
      result += (1 / gammaFunction(idx + 1.5)) * Math.pow(value / 2, idx + 0.5);
    }
    return errFunction(Math.sqrt(value / 2)) - Math.exp(-value / 2) * result;
  }

  /**
   * Returns error function for given double in.
   */
  public static double errFunction(double in) {
    return ( 2 / Math.sqrt(Math.PI) )  * Math.exp(-in * in) - 1;
  }

  /**
   * Returns gamma value for a double in.
   */
  public static double gammaFunction(double in) {
    if (in == 1.0) {
      return 1.0;
    }
    if (in == 0.5) {
      return Math.sqrt(Math.PI);
    }
    return (in - 1) * gammaFunction(in - 1);
  }
}
