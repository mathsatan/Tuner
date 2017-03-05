package com.kruchkov.tuner;

import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import java.util.concurrent.TimeUnit;
import static android.os.Process.THREAD_PRIORITY_DEFAULT;

public class RunnableTask implements Runnable {

    private Handler h;
    private boolean isStop = false;

    public void setHandler(Handler handler) {
        h = handler;
    }

    public void stop() {
        isStop = true;
    }

    public void run() {
        Process.setThreadPriority(THREAD_PRIORITY_DEFAULT);
        int i = AudioRecord.getMinBufferSize(48000, 1, 2);
        AudioRecord localAudioRecord = new AudioRecord(1, 48000, 1, 2, i);
        localAudioRecord.startRecording();
        while (!isStop) {
            try {
                TimeUnit.MILLISECONDS.sleep(150);
            }
            catch (InterruptedException localInterruptedException) {
                localInterruptedException.printStackTrace();
            }
            short[] arrayOfShort = new short[i];
            localAudioRecord.read(arrayOfShort, 0, i);
            FourierTransform fft = new FFT();
            double[] arrayOfDouble = fft.FFTAnalysis(arrayOfShort);
            h.sendEmptyMessage(this.getNumbOfMax(arrayOfDouble, i) * 48000 / i);
        }
        localAudioRecord.stop();
        localAudioRecord.release();
    }

    private int getNumbOfMax(double[] paramArrayOfDouble, int paramInt) {
        double d1 = 0.0;
        int j = 0;
        int i = 0;
        while (i < paramInt) {
            double d2 = d1;
            if (d1 < (int) Math.abs(paramArrayOfDouble[i])) {
                d2 = (int) Math.abs(paramArrayOfDouble[i]);
                j = i;
            }
            i += 1;
            d1 = d2;
        }
        return j;
    }
}
