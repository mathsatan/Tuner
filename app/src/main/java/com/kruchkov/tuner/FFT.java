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
        int i, j, n, m, Mmax, Istp;
        double Tmpr, Tmpi, Wtmp, Theta;
        double Wpr, Wpi, Wr, Wi;
        double[] Tmvl;

        double[] FTvl = new double[analysisArray.length];
        n = analysisArray.length * 2; Tmvl = new double[n];

        for (i = 0; i < n; i+=2) {
            Tmvl[i] = 0;
            Tmvl[i+1] = analysisArray[i/2];
        }

        i = 1; j = 1;
        while (i < n) {
            if (j > i) {
                Tmpr = Tmvl[i]; Tmvl[i] = Tmvl[j]; Tmvl[j] = Tmpr;
                Tmpr = Tmvl[i+1]; Tmvl[i+1] = Tmvl[j+1]; Tmvl[j+1] = Tmpr;
            }
            i = i + 2; m = analysisArray.length;
            while ((m >= 2) && (j > m)) {
                j = j - m; m = m >> 1;
            }
            j = j + m;
        }

        Mmax = 2;
        while (n > Mmax) {
            Theta = -TWO_PI / Mmax; Wpi = Math.sin(Theta);
            Wtmp = Math.sin(Theta / 2); Wpr = Wtmp * Wtmp * 2;
            Istp = Mmax * 2; Wr = 1; Wi = 0; m = 1;

            while (m < Mmax) {
                i = m; m = m + 2; Tmpr = Wr; Tmpi = Wi;
                Wr = Wr - Tmpr * Wpr - Tmpi * Wpi;
                Wi = Wi + Tmpr * Wpi - Tmpi * Wpr;

                while (i < n) {
                    j = i + Mmax;
                    Tmpr = Wr * Tmvl[j] - Wi * Tmvl[j-1];
                    Tmpi = Wi * Tmvl[j] + Wr * Tmvl[j-1];

                    Tmvl[j] = Tmvl[i] - Tmpr; Tmvl[j-1] = Tmvl[i-1] - Tmpi;
                    Tmvl[i] = Tmvl[i] + Tmpr; Tmvl[i-1] = Tmvl[i-1] + Tmpi;
                    i = i + Istp;
                }
            }

            Mmax = Istp;
        }
        for (i = 0; i < FTvl.length; i++) {
            j = i * 2;
            FTvl[i] = Math.sqrt(Math.pow(Tmvl[j],2) + Math.pow(Tmvl[j+1],2))/analysisArray.length;
        }
        return FTvl;
    }
}
