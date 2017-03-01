package com.kantele.fysiikkaprojekti;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private SensorManager mSensorManager;
    private Sensor gyroSensor;
    private Sensor acceloSensor;

    private TextView xDataHolder;
    private TextView yDataHolder;
    private TextView zDataHolder;

    private TextView accelZDataHolder;
    private TextView accelZPeakHolder;

    private TextView timeHolder;

    private Button resetButton;
    private Button playButton;

    private String MPS = " m/s²";
    private String DPS = " deg/s";
    private String SEC = " s";

    long timerStart;
    long timerEnd;
    long timerDelta;
    double elapsedSeconds;

    float zPeak = 0;


    final ArrayList<Long> timeList = new ArrayList<>();
    final ArrayList<String> xList = new ArrayList<String>();
    final ArrayList<String> yList = new ArrayList<String>();
    final ArrayList<String> zList = new ArrayList<String>();

    private static final String TAGX = "X: ";
    private static final String TAGY = "Y: ";
    private static final String TAGZ = "Z: ";
    private static final String TAGMILLIS = "Millis: ";

    private boolean autoIncrement = false;
    private final long REPEAT_DELAY = 200;
    private Handler repeatUpdateHandler = new Handler();

    int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        acceloSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
//        mSensorManager.registerListener(acceloListener, acceloSensor , SensorManager.SENSOR_DELAY_NORMAL);

        xDataHolder = (TextView) findViewById(R.id.xDataHolder);
        yDataHolder = (TextView) findViewById(R.id.yDataHolder);
        zDataHolder = (TextView) findViewById(R.id.zDataHolder);
//        accelZDataHolder = (TextView) findViewById(R.id.accelZDataHolder);
//        accelZPeakHolder = (TextView) findViewById(R.id.accelZPeakHolder);
        timeHolder = (TextView) findViewById(R.id.timeHolder);

        resetButton = (Button) findViewById(R.id.resetButton);
        playButton = (Button) findViewById(R.id.playButton);

        // REPETITIVE COUNTER

        class RepetitiveUpdater implements Runnable {

            @Override
            public void run() {
                if (autoIncrement) {
                    increment();
                    repeatUpdateHandler.postDelayed(new RepetitiveUpdater(), REPEAT_DELAY);
                }
            }
        }

        // RESET BUTTON

        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
 //               zPeak = 0;
 //               accelZPeakHolder.setText(zPeak + MPS);
                elapsedSeconds = 0;
                timeHolder.setText(elapsedSeconds + SEC);

                timeList.clear();
                xList.clear();
                zList.clear();
                yList.clear();
                i = 0;
            }
        });

        // PLAY BUTTON

        playButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch ( arg1.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        //start timer
                        timerStart = System.currentTimeMillis();

                        // start logging data
                        increment();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        autoIncrement = true;
                        repeatUpdateHandler.post(new RepetitiveUpdater());

                        break;
                    case MotionEvent.ACTION_UP:
                        timerCounter();

                        if (arg1.getAction() == MotionEvent.ACTION_UP && autoIncrement) {
                            autoIncrement = false;
                        }

                        for (i = 0; i < timeList.size()-1; i++) {
                            Log.v(TAGMILLIS, String.valueOf(timeList.get(i)));
                            Log.v(TAGX, xList.get(i));
                            Log.v(TAGY, yList.get(i));
                            Log.v(TAGZ, zList.get(i));
                        }

                        break;
                }
                return false;
            }
        });
    }

    // TIMER

    double timerCounter() {

        timerEnd = System.currentTimeMillis();
        timerDelta = timerEnd - timerStart;
        elapsedSeconds = timerDelta / 1000.0;
        timeHolder.setText(elapsedSeconds + SEC);

        return timerDelta;
    }


    public void increment() {
        if (i < 10000) {

            timeList.add(i, (long) timerCounter());


            xList.add(i, xDataHolder.getText().toString());
            yList.add(i, yDataHolder.getText().toString());
            zList.add(i, zDataHolder.getText().toString());

            i++;
        }
    }

    // ANDROID LIFECURVE //

    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(gyroListener, gyroSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
/*
        mSensorManager.registerListener(acceloListener, acceloSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
*/
    }

    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(gyroListener);

        // mSensorManager.unregisterListener(acceloListener);
    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) { }

        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xDataHolder.setText(Math.toDegrees(x) + DPS);
            yDataHolder.setText(Math.toDegrees(y) + DPS);
            zDataHolder.setText(Math.toDegrees(z) + DPS);
        }
    };
/*
    public SensorEventListener acceloListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) { }

        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            if(zPeak > z) {
                zPeak = z;
                accelZPeakHolder.setText(z + MPS);
            }

            accelZDataHolder.setText(z + MPS);
        }
    };
 */
}
