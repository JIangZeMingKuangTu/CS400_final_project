package application;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * The instance of this Numeric class represents a number, which can be a Integer, or a Fraction, or
 * a Double. It supports calculations like addition and subtraction, in which the number will be
 * cast into proper format automatically to do the calculation.
 * 
 * To do calculations, Integer can be cast to Fraction, and both Integer and Fraction can be cast to
 * Double. (Integer -> Fraction -> Double)
 * 
 * @author Houming Chen
 *
 */
public class Numeric extends Number implements Comparable<Numeric> {
  

  /**
   * Since there are float error. Two doubles are considered the same number if their first 12
   * significant digits are same and have same exponent number.
   */
  final static int SIGNIFICANT_FIGURE_FOR_COMPARISON = 10;
  
  final static int SIGNIFICANT_FIGURE_FOR_INPUT_PARSE_FRACTION = 10;

  /**
   * Number of significant figures for output.
   */
  static int outputSignificantFigures = 9;

  /**
   * A private Object representing the number, which can only be a Integer, or a Fraction, or a
   * Double.
   */
  private Number number;

  /**
   * The constructor to construct this Numeric instance with an given number
   * 
   * @param integerNumber the given number
   */
  public Numeric(Number number) {
    if (number instanceof Numeric) {
      this.number = ((Numeric) number).number;
    } else if (number instanceof Fraction) {
      try { // Try to turn fraction back to integer
        this.number = number.intValue();
      } catch (ClassCastException e) {
        this.number = number;
      }
    } else if (number instanceof Long) {
      if (Integer.MIN_VALUE <= number.longValue() && number.longValue() <= Integer.MAX_VALUE) { 
        this.number = number.intValue();
      } else {
        this.number = number.doubleValue();
      }
    } else if (number instanceof Integer || number instanceof Short) {
      this.number = number.intValue();
    } else {
      this.number = number.doubleValue();
    }
  }
  
  public boolean isInteger() {
    return number instanceof Integer;
  }
  
  public boolean isFraction() {
    return number instanceof Fraction;
  }
  
  public boolean isDouble() {
    return number instanceof Double;
  }

  private static boolean theStringIsDouble(String string) {
    Pattern pattern = Pattern.compile("^-?[0-9]+\\.[0-9]+$");
    return pattern.matcher(string).matches();
  }

  private static boolean theStringIsFraction(String string) {
    Pattern pattern = Pattern.compile("^-?[0-9]+\\/[0-9]+$");
    return pattern.matcher(string).matches();
  }

  private static boolean theStringIsInteger(String string) {
    Pattern pattern = Pattern.compile("^-?[0-9]+$");
    return pattern.matcher(string).matches();
  }

  private int tenPow(int n) {
    int ans = 1;
    for(int i = 0; i < n; i++)
      ans *= 10;
    return ans;
  }

  public Numeric(String string) {
    if (theStringIsInteger(string))
      number = Numeric.of((Long) Long.parseLong(string)).number;
    else if (theStringIsFraction(string)) {
      String part[] = string.split("\\/");
      int numerator = Integer.parseInt(part[0]);
      int denominator = Integer.parseInt(part[1]);
      number = Fraction.of(numerator, denominator);
    } else if (theStringIsDouble(string)) {
      int index = string.indexOf(".");
      int decimalPlaces = string.length() - index - 1;
      if (string.length() - 1 <= SIGNIFICANT_FIGURE_FOR_INPUT_PARSE_FRACTION) {
        boolean negative = (string.charAt(0) == '-');
        String part[] = string.split("\\.");
        int integerPart = Integer.parseInt(part[0]);
        if(negative)
          part[1] = "-" + part[1];
        int decimalPart = Integer.parseInt(part[1]);
        if(decimalPart == 0)
          number = Integer.valueOf(integerPart);
        number = new Fraction(decimalPart, tenPow(decimalPlaces)).add(Fraction.of(integerPart));
      } else
        number = Double.parseDouble(string);
    } else {
      throw new NumberFormatException(string + ": Cannot convert the String to Numeric");
    }
  }

  /**
   * Generate a Numeric object with a given number
   * 
   * @param number a given number
   * @return a constructed Numeric object
   */
  public static Numeric of(Number number) {
    return new Numeric(number);
  }

  public static Numeric of(String string) {
    return new Numeric(string);
  }

  /**
   * Get the sum of this Numeric Instance and another given Number Instance.
   * 
   * @param other other the other given Numeric Instance
   * @return the sum
   */
  public Numeric add(Number other) {
    if (other instanceof Numeric) {
      Numeric otherNum = new Numeric(other);
      Numeric thisNum = new Numeric(this);
      if (thisNum.number instanceof Double || otherNum.number instanceof Double) {
        return new Numeric(thisNum.number.doubleValue() + otherNum.number.doubleValue());
      }
      if (thisNum.number instanceof Fraction || otherNum.number instanceof Fraction) {
        try {
          return new Numeric(Fraction.of(thisNum.number).add(Fraction.of(otherNum.number)));
        } catch (ArithmeticException arithmeticException) {
          if (arithmeticException.getMessage().equals("integer overflow")) {
            return new Numeric(thisNum.number.doubleValue() + otherNum.number.doubleValue());
          } else
            throw arithmeticException;
        }
      }
      if (thisNum.number instanceof Integer || otherNum.number instanceof Integer) {
        return new Numeric(thisNum.number.longValue() + otherNum.number.longValue());
      }

      throw new ClassCastException(thisNum.number.getClass() + ":" + otherNum.number.getClass()
          + ": Cannot cast to Integer or Double or Fraction");
    }
    return this.add(new Numeric(other));
  }

  public Numeric add(String other) {
    return add(new Numeric(other));
  }

  /**
   * Get the difference of this Numeric Instance and another given Number Instance.
   * 
   * @param other other the other given Numeric Instance
   * @return the difference
   */
  public Numeric subtract(Number other) {
    if (other instanceof Numeric) {
      Numeric otherNum = new Numeric(other);
      Numeric thisNum = new Numeric(this);
      if (thisNum.number instanceof Double || otherNum.number instanceof Double) {
        return new Numeric(thisNum.number.doubleValue() - otherNum.number.doubleValue());
      }
      if (thisNum.number instanceof Fraction || otherNum.number instanceof Fraction) {
        try {
          return new Numeric(Fraction.of(thisNum.number).subtract(Fraction.of(otherNum.number)));
        } catch (ArithmeticException arithmeticException) {
          if (arithmeticException.getMessage().equals("integer overflow")) {
            return new Numeric(thisNum.number.doubleValue() - otherNum.number.doubleValue());
          } else
            throw arithmeticException;
        }
      }
      if (thisNum.number instanceof Integer || otherNum.number instanceof Integer) {
        return new Numeric(thisNum.number.longValue() - otherNum.number.longValue());
      }
      throw new ClassCastException("Cannot cast to Integer or Double or Fraction");
    }
    return this.subtract(new Numeric(other));
  }

  public Numeric subtract(String other) {
    return subtract(new Numeric(other));
  }

  /**
   * Get the product of this Numeric Instance and another given Number Instance.
   * 
   * @param other other the other given Numeric Instance
   * @return the product
   */
  public Numeric multiply(Number other) {
    if (other instanceof Numeric) {
      Numeric otherNum = new Numeric(other);
      Numeric thisNum = new Numeric(this);
      if ((thisNum.number instanceof Fraction || thisNum.number instanceof Integer) && thisNum.equals(Numeric.of(0))) {
        return new Numeric(0);
      }
      if ((otherNum.number instanceof Fraction || otherNum.number instanceof Integer) && otherNum.equals(Numeric.of(0))) {
        return new Numeric(0);
      }
      if (thisNum.number instanceof Double || otherNum.number instanceof Double) {
        return new Numeric(thisNum.number.doubleValue() * otherNum.number.doubleValue());
      }
      if (thisNum.number instanceof Fraction || otherNum.number instanceof Fraction) {
        try {
          return new Numeric(Fraction.of(thisNum.number).multiply(Fraction.of(otherNum.number)));
        } catch (ArithmeticException arithmeticException) {
          if (arithmeticException.getMessage().equals("integer overflow")) {
            return new Numeric(thisNum.number.doubleValue() * otherNum.number.doubleValue());
          } else
            throw arithmeticException;
        }
      }
      if (thisNum.number instanceof Integer || otherNum.number instanceof Integer) {
        return new Numeric(thisNum.number.longValue() * otherNum.number.longValue());
      }
      throw new ClassCastException("Cannot cast to Integer or Double or Fraction");
    }
    return this.multiply(new Numeric(other));
  }

  public Numeric multiply(String other) {
    return multiply(new Numeric(other));
  }

  /**
   * Get the quotient of this Numeric Instance and another given Number Instance.
   * 
   * @param other other the other given Numeric Instance
   * @return the quotient
   */
  public Numeric dividedBy(Number other) {
    if (other instanceof Numeric) {
      Numeric otherNum = new Numeric(other);
      Numeric thisNum = new Numeric(this);
      if ((thisNum.number instanceof Fraction || thisNum.number instanceof Integer) && thisNum.equals(Numeric.of(0))) {
        return new Numeric(0);
      }
      if (thisNum.number instanceof Double || otherNum.number instanceof Double) {
        return new Numeric(thisNum.number.doubleValue() / otherNum.number.doubleValue());
      }
      if (thisNum.number instanceof Fraction || otherNum.number instanceof Fraction) {
        try {
          return new Numeric(Fraction.of(thisNum.number).dividedBy(Fraction.of(otherNum.number)));
        } catch (ArithmeticException arithmeticException) {
          if (arithmeticException.getMessage().equals("integer overflow")) {
            return new Numeric(thisNum.number.doubleValue() / otherNum.number.doubleValue());
          } else
            throw arithmeticException;
        }
      }
      if (thisNum.number instanceof Integer || otherNum.number instanceof Integer) {
        return new Numeric(new Fraction(thisNum.number.intValue(), otherNum.number.intValue()));
      }
      throw new ClassCastException("Cannot cast to Integer or Double or Fraction");
    }
    return this.dividedBy(new Numeric(other));
  }

  public Numeric dividedBy(String other) {
    return dividedBy(new Numeric(other));
  }

  /**
   * 
   * Check whether the given integer x is a perfect square number
   * 
   * @param x a given integer
   * @return true if x is a perfect square number, false otherwise.
   */
  private static boolean isPerfectSquare(int x) {
    double sqrt = Math.sqrt(x);
    int sqrtInt = (int) Math.floor(sqrt);
    return sqrtInt * sqrtInt == x;
  }

  /**
   * 
   * If x is a perfect square number, get its square root.
   * 
   * @param x a given integer
   * @return a integer which is the square root of x.
   */
  private static Integer perfectSquareRoot(int x) {
    double sqrt = Math.sqrt(x);
    int sqrtInt = (int) Math.floor(sqrt);
    return sqrtInt;
  }

  /**
   * 
   * Get the square root of this Numeric instance.
   * 
   * @return the square root of this Numeric instance.
   */
  public Numeric sqrt() {
    Numeric thisNum = new Numeric(this);
    if (thisNum.number instanceof Integer && isPerfectSquare((Integer) thisNum.number)) {
      return new Numeric(perfectSquareRoot((Integer) thisNum.number));
    }
    if (thisNum.number instanceof Fraction
        && isPerfectSquare(((Fraction) thisNum.number).getNumerator())
        && isPerfectSquare(((Fraction) thisNum.number).getDenominator())) {
      return new Numeric(new Fraction(perfectSquareRoot(((Fraction) thisNum.number).getNumerator()),
          perfectSquareRoot(((Fraction) thisNum.number).getDenominator())));
    } else {
      return new Numeric(Math.sqrt(thisNum.number.doubleValue()));
    }
  }

  /**
   * Round a given double to the given significant figures.
   * 
   * @param value            a given double
   * @param significatFigure a given int representing the significant figures
   * @return the rounded double
   */
  private static double roundWithSignificantFigure(double value, int significantFigure) {
    if(Double.toString(value).contains("Inf"))
      throw new ArithmeticException("The result is too big!");
    BigDecimal bigDecimal = new BigDecimal(Double.toString(value));
    bigDecimal = bigDecimal.round(new MathContext(significantFigure));
    return bigDecimal.doubleValue();
  }

  /**
   * Round a given double to having at most MAXIMUM_SIGNIFICANT_FIGURE significant figures.
   * 
   * @param value a given double
   * @return the rounded double
   */
  private static double round(double value) {
    return roundWithSignificantFigure(value, SIGNIFICANT_FIGURE_FOR_COMPARISON);
  }

  @Override
  public int compareTo(Numeric other) {
    other = new Numeric(other);
    Numeric thisNum = new Numeric(this);
    if (thisNum.number instanceof Double || other.number instanceof Double) {
      return Double.valueOf(round(thisNum.number.doubleValue()))
          .compareTo(Double.valueOf(round(other.number.doubleValue())));
    }
    if (thisNum.number instanceof Fraction || other.number instanceof Fraction) {
      return (Fraction.of(thisNum.number).compareTo(Fraction.of(other.number)));
    }
    if (thisNum.number instanceof Integer || other.number instanceof Integer) {
      return Integer.valueOf(thisNum.number.intValue())
          .compareTo(Integer.valueOf(other.number.intValue()));
    }
    throw new ClassCastException("Cannot cast to Integer or Double or Fraction");
  }

  public int compareTo(String other) {
    return compareTo(new Numeric(other));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof String)
      return compareTo((String) obj) == 0;
    if (obj instanceof Numeric)
      return compareTo((Numeric) obj) == 0;
    else if (obj instanceof Number)
      return compareTo(new Numeric((Number) obj)) == 0;
    return false;
  }
  
  public boolean mathematicallyEquals(Object obj) {
    if (this.number instanceof Double)
      return false;
    if (obj instanceof String)
      return mathematicallyEquals(new Numeric((String) obj));
    if (obj instanceof Numeric) {
      if(((Numeric) obj).number instanceof Double)
        return false;
      return compareTo((Numeric) obj) == 0;
    }
    else if (obj instanceof Number)
      return mathematicallyEquals(new Numeric((Number) obj));
    return false;
  }

  @Override
  public int intValue() {
    if (number instanceof Integer) {
      return ((Integer) number);
    }
    throw new ClassCastException("Cannot cast to an Integer");
  }

  @Override
  public long longValue() {
    if (number instanceof Integer) {
      return ((Integer) number).longValue();
    }
    throw new ClassCastException("Cannot cast to an Long");
  }

  @Override
  public float floatValue() {
    return (float) number.doubleValue();
  }

  @Override
  public double doubleValue() {
    return number.doubleValue();
  }
  
  public Numeric castToDouble() {
    return new Numeric(doubleValue());
  }
  
  public Numeric opposite() {
    return Numeric.of(0).subtract(this);
  }
  
  public Numeric abs() {
    if (compareTo(new Numeric(0)) < 0)
      return opposite();
    return new Numeric(this);
  }
  
  public Numeric castToNearestFraction() {
    if (number instanceof Double) {
      Numeric absThis = this.abs();
      int numerator = 1;
      int denominator = 1;
      while(numerator < 1000 && denominator < 1000) {
        Numeric fractionVersion = Numeric.of(new Fraction(numerator, denominator));
        if(fractionVersion.compareTo(absThis) < 0) {
          numerator++;
        } else if (fractionVersion.compareTo(absThis) > 0){
          denominator++;
        } else {
          if (compareTo(new Numeric(0)) < 0)
            return fractionVersion.opposite();
          return fractionVersion;
        }
      }
      throw new ClassCastException("Cannot cast to nearest Fraction");
    }
    else
      return new Numeric(number);
  }

  @Override
  public String toString() {
    if (number instanceof Double)
      return "" + roundWithSignificantFigure((Double) number, outputSignificantFigures);
    /*if (number instanceof Fraction) {
        int numerator = ((Fraction) number).getNumerator();
        int denominator = ((Fraction) number).getDenominator();
        if(("" + numerator + denominator).length() >= 8) {
          return "" + roundWithSignificantFigure(((Fraction) number).doubleValue(), OUTPUT_SIGNIFICANT_FIGURE);
        }
    }*/
    return number.toString();
  }
  
  public static void main(String[] args) throws MatrixDimensionsMismatchException {
    
    System.out.println(Numeric.of("-1.333"));
    //System.out.println(Numeric.of("1/3").castToDouble().castToNearestFraction().mathematicallyEquals("1/3"));
    //Matrix matrix = new Matrix(new String[][] {{"0.5", "1"}, {"1", "0.5"}});
    //System.out.println(Arrays.toString(matrix.eigenValues()));
    //System.out.println(Numeric.of("0.3333333332").castToNearestFraction());
  }
}