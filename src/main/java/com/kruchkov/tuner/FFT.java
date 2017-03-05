package com.kruchkov.tuner;

public class FFT implements FourierTransform {
    private static final double TWO_PI = 2 * Math.PI;

    private boolean isPowerOfTwo(int n) {
        return (n > 0 && (n & (n - 1)) == 0);
    }

    /**
     * Gets specter of input data
     * @param       analysisArray   array for analysis
     * @return      array with complex data
     * @throws      IllegalArgumentException    in case argument is wrong
     */
    public double[] FFTAnalysis(short[] analysisArray) {
        if (!isPowerOfTwo(analysisArray.length)) {
            throw new IllegalArgumentException("Array length is not a 2 power");
        }
        int n = analysisArray.length * 2;
        double[] resultArray = new double[n];
        int i = 0;
        int j = 0;
        while (i < analysisArray.length) {
            j = i * 2;
            resultArray[j] = 0.0;
            resultArray[j + 1] = analysisArray[i];
            ++i;
        }

        i = 1;
        j = 1;
        double d1;
        int k;
        while (j < n) {
            if (i > j) {
                d1 = resultArray[j];
                resultArray[j] = resultArray[i];
                resultArray[i] = d1;
                d1 = resultArray[(j + 1)];
                resultArray[(j + 1)] = resultArray[(i + 1)];
                resultArray[(i + 1)] = d1;
            }
            k = j + 2;
            j = analysisArray.length;
            while ((j >= 2) && (i > j)) {
                i -= j;
                j >>= 1;
            }
            i += j;
            j = k;
        }

        int paramInt1 = 1;
        int paramInt2 = analysisArray.length;
        for (i = 2; n > i; i = k) {
            d1 = -TWO_PI / i;
            double d5 = Math.sin(d1);
            d1 = Math.sin(d1 / 2.0D);
            double d6 = d1 * d1 * 2.0D;
            k = i * 2;
            d1 = 1.0;
            double d2 = 0.0;
            if (paramInt1 < i) {
                j = paramInt1;
                int m = paramInt1 + 2;
                double d3 = d1 - d1 * d6 - d2 * d5;
                double d4 = d1 * d5 + d2 - d2 * d6;
                for (;;) {
                    paramInt1 = m;
                    if (j >= n) {
                        break;
                    }
                    paramInt1 = j + i;
                    d1 = resultArray[paramInt1] * d3 - resultArray[(paramInt1 - 1)] * d4;
                    d2 = resultArray[paramInt1] * d4 + resultArray[(paramInt1 - 1)] * d3;
                    resultArray[j] -= d1;
                    resultArray[(j - 1)] -= d2;
                    resultArray[j] += d1;
                    resultArray[(j - 1)] += d2;
                    j += k;
                }
            }
        }
        paramInt1 = 0;
        while (paramInt1 < paramInt2) {
            i = paramInt1 * 2;
            resultArray[(paramInt2 - paramInt1 - 1)] =
                    (Math.sqrt(resultArray[i] * resultArray[i] +
                            resultArray[(i + 1)] * resultArray[(i + 1)]) / Math.sqrt(paramInt2));
            paramInt1 += 1;
        }
        return resultArray;
    }
}
