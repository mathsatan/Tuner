package com.kruchkov.tuner;

import android.media.AudioFormat;
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

    private final int SAMPLE_RATE = 8000;

    public void setHandler(Handler handler) {
        h = handler;
    }

    public void stop() {
        isStop = true;
    }

    private Bundle b = new Bundle();

    public void run() {
        Process.setThreadPriority(THREAD_PRIORITY_DEFAULT);
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, 1, AudioFormat.ENCODING_PCM_16BIT);
        if (bufferSize < 1024) {
            bufferSize = 1024;
        }
        AudioRecord localAudioRecord = new AudioRecord(1, SAMPLE_RATE, 1, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        localAudioRecord.startRecording();
        while (!isStop) {
            short[] arrayOfShort = new short[bufferSize];
            localAudioRecord.read(arrayOfShort, 0, bufferSize);

            FourierTransform fft = new FFT();
            double[] arrayOfDouble = fft.FFTAnalysis(arrayOfShort);
            float f = this.getNumbOfMax(arrayOfDouble);

            b.putFloat("freq", f);
            Message message = new Message();
            message.setData(b);
            h.sendMessage(message);

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException localInterruptedException) {
                localInterruptedException.printStackTrace();
            }
        }
        localAudioRecord.stop();
        localAudioRecord.release();
    }

    private float getNumbOfMax(double[] arr) {
        double max = 0.0;
        int j = 0;
        for (int i = 0; i < arr.length; ++i) {
            if (max < arr[i]) {
                max = arr[i];
                j = i;
            }
        }

        return ((float) j * SAMPLE_RATE) / ((float) arr.length);
    }
}
