package com.kruchkov.tuner;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    protected Button btnStart;
    protected Button btnStop;
    protected TextView tvInfo;
    protected TextView stringNoteLabel;
    private static Handler tunerHandler;
    private RunnableTask task = null;
    private static DecimalFormat decFormatter = new DecimalFormat(".##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stringNoteLabel = findViewById(R.id.open_string_note);
        tvInfo = findViewById(R.id.tvInfo);
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                task = new RunnableTask();
                task.setHandler(tunerHandler);
                Thread t = new Thread(task);
                t.setUncaughtExceptionHandler(exceptionHandler);
                t.start();
            }
        });
        btnStop = findViewById(R.id.btnStop);
        btnStop.setEnabled(false);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.stop();
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                tvInfo.setText(getString(R.string.frequency_init_label));
            }
        });
        tvInfo.setText(getString(R.string.frequency_init_label));
        tunerHandler = new TunerHandler(new TunerHandler.TunerCallback() {
            public void func(Message msg) {
                Bundle b = msg.getData();
                if (b != null) {
                    String s = decFormatter.format(b.getFloat("freq"));
                    tvInfo.setText(String.format(getString(R.string.frequency_label), s));
                    final String noteFound = isSomeNote(b.getFloat("freq"));
                    if (!noteFound.equals("")) {
                        Animation animation1 = new AlphaAnimation(0.0f, 1.0f);
                        animation1.setDuration(1000);
                        animation1.setStartOffset(500);
                        animation1.setAnimationListener(new Animation.AnimationListener(){
                            @Override
                            public void onAnimationEnd(Animation arg0) {
                                stringNoteLabel.setText("");
                            }
                            @Override
                            public void onAnimationRepeat(Animation arg0) {
                                // TODO Auto-generated method stub
                            }
                            @Override
                            public void onAnimationStart(Animation arg0) {
                                stringNoteLabel.setText(noteFound);
                            }
                        });
                        stringNoteLabel.startAnimation(animation1);
                    }
                }
            }
        });
    }

    private static String isSomeNote(float noteFreq) {
        double df = 1.0;    // Hz
        for (Map.Entry<String, Double> entry : stringNote.entrySet()) {
            String symbol = entry.getKey();
            Double val = entry.getValue();
            if (Math.abs(noteFreq - val) <= df) {
                return symbol;
            }
        }
        return "";
    }

    private static final Map<String, Double> stringNote = new HashMap<>();
    static {
        stringNote.put("e (mi)", 329.63);
        stringNote.put("b (si)", 246.94);
        stringNote.put("g (sol)", 196.00);
        stringNote.put("d (re)", 146.83);
        stringNote.put("A (la)", 110.00);
        stringNote.put("E (mi)", 82.41);
    }

    private static class TunerHandler extends Handler {
        interface TunerCallback {
            void func(Message msg);
        }
        private static TunerCallback callback;
        TunerHandler(TunerCallback tunerCallback) {
            TunerHandler.callback = tunerCallback;
        }

        public void handleMessage(Message msg) {
            TunerHandler.callback.func(msg);
        }
    }

    private Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread th, Throwable ex) {
            tvInfo.setText(String.format(getString(R.string.concurrent_error), ex.getMessage()));
        }
    };
}
