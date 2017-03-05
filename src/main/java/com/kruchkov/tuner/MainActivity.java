package com.kruchkov.tuner;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Button btnStart;
    private Button btnStop;
    private Handler h;
    private TextView tvInfo;
    private RunnableTask task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.id.myText);
        tvInfo = ((TextView)findViewById(R.id.tvInfo));
        btnStart = ((Button)findViewById(R.id.btnStart));
        btnStop = ((Button)findViewById(R.id.btnStop));
        tvInfo.setText("Frequency: ? Hz");
        h = new Handler() {
            public void handleMessage(Message msg) {
                tvInfo.setText("Frequency: " + msg.what + " Hz");
            }
        };
    }

    public void onclick(View paramView) {
        switch (paramView.getId()) {
            case 1:
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                task = new RunnableTask();
                task.setHandler(h);
                Thread t = new Thread(task);
                t.setUncaughtExceptionHandler(exceptionHandler);
                t.start();
                break;
            case 2:
                task.stop();
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
            default:
                break;
        }
    }

    private Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread th, Throwable ex) {
            tvInfo.setText("Error has occurred: " + ex.getMessage());
        }
    };
}
