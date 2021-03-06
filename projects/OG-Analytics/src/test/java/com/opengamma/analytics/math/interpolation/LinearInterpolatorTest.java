/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.math.interpolation;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.opengamma.analytics.math.matrix.DoubleMatrix1D;
import com.opengamma.analytics.math.matrix.DoubleMatrix2D;
import com.opengamma.util.test.TestGroup;

/**
 * Test.
 */
@Test(groups = TestGroup.UNIT)
public class LinearInterpolatorTest {

  private static final double EPS = 1e-14;
  private static final double INF = 1. / 0.;

  /**
   * 
   */
  @Test
  public void recov2ptsTest() {
    final double[] xValues = new double[] {1., 2. };
    final double[] yValues = new double[] {6., 1. };

    final int nIntervalsExp = 1;
    final int orderExp = 2;
    final int dimExp = 1;
    final double[][] coefsMatExp = new double[][] {{-5., 6. } };

    LinearInterpolator interpMatrix = new LinearInterpolator();

    PiecewisePolynomialResult result = interpMatrix.interpolate(xValues, yValues);

    assertEquals(result.getDimensions(), dimExp);
    assertEquals(result.getNumberOfIntervals(), nIntervalsExp);
    assertEquals(result.getDimensions(), dimExp);

    for (int i = 0; i < nIntervalsExp; ++i) {
      for (int j = 0; j < orderExp; ++j) {
        final double ref = coefsMatExp[i][j] == 0. ? 1. : Math.abs(coefsMatExp[i][j]);
        assertEquals(result.getCoefMatrix().getData()[i][j], coefsMatExp[i][j], ref * EPS);
      }
    }

    for (int j = 0; j < nIntervalsExp + 1; ++j) {
      assertEquals(result.getKnots().getData()[j], xValues[j]);
    }
  }

  /**
   * 
   */
  @Test
  public void recov4ptsTest() {
    final double[] xValues = new double[] {1., 2., 4., 7. };
    final double[] yValues = new double[] {6., 1., 8., -2. };

    final int nIntervalsExp = 3;
    final int orderExp = 2;
    final int dimExp = 1;
    final double[][] coefsMatExp = new double[][] { {-5., 6. }, {7. / 2., 1. }, {-10. / 3., 8. } };

    LinearInterpolator interpMatrix = new LinearInterpolator();

    PiecewisePolynomialResult result = interpMatrix.interpolate(xValues, yValues);

    assertEquals(result.getDimensions(), dimExp);
    assertEquals(result.getNumberOfIntervals(), nIntervalsExp);
    assertEquals(result.getDimensions(), dimExp);

    for (int i = 0; i < nIntervalsExp; ++i) {
      for (int j = 0; j < orderExp; ++j) {
        final double ref = coefsMatExp[i][j] == 0. ? 1. : Math.abs(coefsMatExp[i][j]);
        assertEquals(result.getCoefMatrix().getData()[i][j], coefsMatExp[i][j], ref * EPS);
      }
    }

    for (int j = 0; j < nIntervalsExp + 1; ++j) {
      assertEquals(result.getKnots().getData()[j], xValues[j]);
    }
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void NullXvaluesTest() {
    double[] xValues = new double[4];
    double[] yValues = new double[] {1., 2., 3., 4. };

    xValues = null;

    NaturalSplineInterpolator interp = new NaturalSplineInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void NullYvaluesTest() {
    double[] xValues = new double[] {1., 2., 3., 4. };
    double[] yValues = new double[4];

    yValues = null;

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void wrongDatalengthTest() {
    double[] xValues = new double[] {1., 2., 3. };
    double[] yValues = new double[] {1., 2., 3., 4. };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void shortDataLengthTest() {
    double[] xValues = new double[] {1. };
    double[] yValues = new double[] {4. };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void NaNxValuesTest() {
    double[] xValues = new double[] {1., 2., Double.NaN, 4. };
    double[] yValues = new double[] {1., 2., 3., 4. };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void NaNyValuesTest() {
    double[] xValues = new double[] {1., 2., 3., 4. };
    double[] yValues = new double[] {1., 2., Double.NaN, 4. };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void InfxValuesTest() {
    double[] xValues = new double[] {1., 2., 3., INF };
    double[] yValues = new double[] {1., 2., 3., 4. };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void InfyValuesTest() {
    double[] xValues = new double[] {1., 2., 3., 4. };
    double[] yValues = new double[] {1., 2., 3., INF };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void coincideXvaluesTest() {
    double[] xValues = new double[] {1., 2., 3., 3. };
    double[] yValues = new double[] {1., 2., 3., 4. };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test
  public void recov2ptsMultiTest() {
    final double[] xValues = new double[] {1., 2. };
    final double[][] yValues = new double[][] { {6., 1. }, {2., 5. } };

    final int nIntervalsExp = 1;
    final int orderExp = 2;
    final int dimExp = 2;
    final double[][] coefsMatExp = new double[][] { {-5., 6. }, {3., 2. } };

    LinearInterpolator interpMatrix = new LinearInterpolator();

    PiecewisePolynomialResult result = interpMatrix.interpolate(xValues, yValues);

    assertEquals(result.getDimensions(), dimExp);
    assertEquals(result.getNumberOfIntervals(), nIntervalsExp);
    assertEquals(result.getDimensions(), dimExp);

    for (int i = 0; i < nIntervalsExp * dimExp; ++i) {
      for (int j = 0; j < orderExp; ++j) {
        final double ref = coefsMatExp[i][j] == 0. ? 1. : Math.abs(coefsMatExp[i][j]);
        assertEquals(result.getCoefMatrix().getData()[i][j], coefsMatExp[i][j], ref * EPS);
      }
    }

    for (int j = 0; j < nIntervalsExp + 1; ++j) {
      assertEquals(result.getKnots().getData()[j], xValues[j]);
    }
  }

  /**
   * 
   */
  @Test
  public void recov4ptsMultiTest() {
    final double[] xValues = new double[] {1., 2., 3., 4 };
    final double[][] yValues = new double[][] { {6., 1., 8., -2. }, {1., 1. / 3., 2. / 11., 1. / 7. } };

    final int nIntervalsExp = 3;
    final int orderExp = 2;
    final int dimExp = 2;
    final double[][] coefsMatExp = new double[][] { {-5., 6. }, {-2. / 3., 1. }, {7., 1. }, {-5. / 33., 1. / 3. }, {-10., 8. }, {-3. / 77., 2. / 11. } };

    LinearInterpolator interpMatrix = new LinearInterpolator();

    PiecewisePolynomialResult result = interpMatrix.interpolate(xValues, yValues);

    assertEquals(result.getDimensions(), dimExp);
    assertEquals(result.getNumberOfIntervals(), nIntervalsExp);
    assertEquals(result.getDimensions(), dimExp);

    for (int i = 0; i < nIntervalsExp * dimExp; ++i) {
      for (int j = 0; j < orderExp; ++j) {
        final double ref = coefsMatExp[i][j] == 0. ? 1. : Math.abs(coefsMatExp[i][j]);
        assertEquals(result.getCoefMatrix().getData()[i][j], coefsMatExp[i][j], ref * EPS);
      }
    }

    for (int j = 0; j < nIntervalsExp + 1; ++j) {
      assertEquals(result.getKnots().getData()[j], xValues[j]);
    }
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void NullXvaluesMultiTest() {
    double[] xValues = new double[4];
    double[][] yValues = new double[][] { {1., 2., 3., 4. }, {1., 5., 3., 4. } };

    xValues = null;

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void NullYvaluesMultiTest() {
    double[] xValues = new double[] {1., 2., 3., 4. };
    double[][] yValues = new double[2][4];

    yValues = null;

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void wrongDatalengthMultiTest() {
    double[] xValues = new double[] {1., 2., 3. };
    double[][] yValues = new double[][] { {1., 2., 3., 4. }, {2., 2., 3., 4. } };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void shortDataLengthMultiTest() {
    double[] xValues = new double[] {1. };
    double[][] yValues = new double[][] { {4. }, {1. } };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void NaNxValuesMultiTest() {
    double[] xValues = new double[] {1., 2., Double.NaN, 4. };
    double[][] yValues = new double[][] { {1., 2., 3., 4. }, {2., 2., 3., 4. } };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void NaNyValuesMultiTest() {
    double[] xValues = new double[] {1., 2., 3., 4. };
    double[][] yValues = new double[][] { {1., 2., 3., 4. }, {1., 2., Double.NaN, 4. } };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void InfxValuesMultiTest() {
    double[] xValues = new double[] {1., 2., 3., INF };
    double[][] yValues = new double[][] { {1., 2., 3., 4. }, {2., 2., 3., 4. } };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void InfyValuesMultiTest() {
    double[] xValues = new double[] {1., 2., 3., 4. };
    double[][] yValues = new double[][] { {1., 2., 3., 4. }, {1., 2., 3., INF } };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void coincideXvaluesMultiTest() {
    double[] xValues = new double[] {1., 2., 3., 3. };
    double[][] yValues = new double[][] { {1., 2., 3., 4. }, {2., 2., 3., 4. } };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * Derive value of the underlying cubic spline function at the value of xKey
   */
  @Test
  public void InterpolantsTest() {
    final double[] xValues = new double[] {1., 2., 3., 4. };
    final double[][] yValues = new double[][] { {6., 25. / 6., 10. / 3., 4. }, {6., 1., 0., 0. } };
    final double[][] xKey = new double[][] { {-1., 0.5, 1.5 }, {2.5, 3.5, 4.5 } };

    final double[][][] resultValuesExpected = new double[][][] { { {29. / 3., 15. / 4. }, {16., 1. / 2. } }, { {83. / 12., 11. / 3. }, {17. / 2., 0. } },
        { {61. / 12., 13. / 3. }, {7. / 2., 0. } } };

    final int yDim = yValues.length;
    final int keyLength = xKey[0].length;
    final int keyDim = xKey.length;

    LinearInterpolator interp = new LinearInterpolator();

    double value = interp.interpolate(xValues, yValues[0], xKey[0][0]);
    {
      final double ref = resultValuesExpected[0][0][0] == 0. ? 1. : Math.abs(resultValuesExpected[0][0][0]);
      assertEquals(value, resultValuesExpected[0][0][0], ref * EPS);
    }

    DoubleMatrix1D valuesVec1 = interp.interpolate(xValues, yValues, xKey[0][0]);
    for (int i = 0; i < yDim; ++i) {
      final double ref = resultValuesExpected[0][i][0] == 0. ? 1. : Math.abs(resultValuesExpected[0][i][0]);
      assertEquals(valuesVec1.getData()[i], resultValuesExpected[0][i][0], ref * EPS);
    }

    DoubleMatrix1D valuesVec2 = interp.interpolate(xValues, yValues[0], xKey[0]);
    for (int k = 0; k < keyLength; ++k) {
      final double ref = resultValuesExpected[k][0][0] == 0. ? 1. : Math.abs(resultValuesExpected[k][0][0]);
      assertEquals(valuesVec2.getData()[k], resultValuesExpected[k][0][0], ref * EPS);
    }

    DoubleMatrix2D valuesMat1 = interp.interpolate(xValues, yValues[0], xKey);
    for (int j = 0; j < keyDim; ++j) {
      for (int k = 0; k < keyLength; ++k) {
        final double ref = resultValuesExpected[k][0][j] == 0. ? 1. : Math.abs(resultValuesExpected[k][0][j]);
        assertEquals(valuesMat1.getData()[j][k], resultValuesExpected[k][0][j], ref * EPS);
      }
    }

    DoubleMatrix2D valuesMat2 = interp.interpolate(xValues, yValues, xKey[0]);
    for (int i = 0; i < yDim; ++i) {
      for (int k = 0; k < keyLength; ++k) {
        final double ref = resultValuesExpected[k][i][0] == 0. ? 1. : Math.abs(resultValuesExpected[k][i][0]);
        assertEquals(valuesMat2.getData()[i][k], resultValuesExpected[k][i][0], ref * EPS);
      }
    }

    DoubleMatrix2D[] valuesMat3 = interp.interpolate(xValues, yValues, xKey);
    for (int i = 0; i < yDim; ++i) {
      for (int j = 0; j < keyDim; ++j) {
        for (int k = 0; k < keyLength; ++k) {
          final double ref = resultValuesExpected[k][i][j] == 0. ? 1. : Math.abs(resultValuesExpected[k][i][j]);
          assertEquals(valuesMat3[k].getData()[i][j], resultValuesExpected[k][i][j], ref * EPS);
        }
      }
    }
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void LargeOutputTest() {
    double[] xValues = new double[] {1., 2.e-308, 3.e-308, 4. };
    double[] yValues = new double[] {1., 2., 1.e308, 3. };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void LargeOutputMultiTest() {
    double[] xValues = new double[] {1., 2.e-308, 3.e-308, 4. };
    double[][] yValues = new double[][] { {1., 2.e307, 3., 4. }, {2., 2., 3., 4. } };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void LargeInterpolantsTest() {
    final double[] xValues = new double[] {1., 2., 3., 4. };
    final double[][] yValues = new double[][] { {2., 10., 2., 5. }, {1., 2., 10., 11. } };

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues[0], 1.e308);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void NullKeyTest() {
    double[] xValues = new double[] {1., 2., 3. };
    double[] yValues = new double[] {1., 3., 4. };
    double[] xKey = new double[3];

    xKey = null;

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues, xKey);

  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void NullKeyMultiTest() {
    double[] xValues = new double[] {1., 2., 3. };
    double[][] yValues = new double[][] { {1., 3., 4. }, {2., 3., 1. } };
    double[] xKey = new double[3];

    xKey = null;

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues, xKey);

  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void NullKeyMatrixTest() {
    double[] xValues = new double[] {1., 2., 3. };
    double[] yValues = new double[] {1., 3., 4. };
    double[][] xKey = new double[3][3];

    xKey = null;

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues, xKey);

  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void NullKeyMatrixMultiTest() {
    double[] xValues = new double[] {1., 2., 3. };
    double[][] yValues = new double[][] { {1., 3., 4. }, {2., 3., 1. } };
    double[][] xKey = new double[3][4];

    xKey = null;

    LinearInterpolator interp = new LinearInterpolator();

    interp.interpolate(xValues, yValues, xKey);

  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void notReconnectedTest() {
    double[] xValues = new double[] {1., 2.000000000001, 2.000000000002, 4. };
    double[] yValues = new double[] {2., 4.e10, 3.e-5, 5.e11 };

    PiecewisePolynomialInterpolator interpPos = new LinearInterpolator();
    interpPos.interpolate(xValues, yValues);
    //    System.out.println(interpPos.interpolate(xValues, yValues, xValues[1] * (1. - EPS)));
    //    System.out.println(interpPos.interpolate(xValues, yValues, xValues[1] * (1.)));
    //    System.out.println(interpPos.interpolate(xValues, yValues, xValues[1] * (1. + .00000000001)));
    //    System.out.println(interpPos.interpolate(xValues, yValues, xValues[2]));
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void notReconnectedMultiTest() {
    double[] xValues = new double[] {1., 2.000000000001, 2.000000000002, 4. };
    double[][] yValues = new double[][] {{2., 4.e10, 3.e-5, 5.e11 } };

    PiecewisePolynomialInterpolator interpPos = new LinearInterpolator();
    interpPos.interpolate(xValues, yValues);
  }

}
