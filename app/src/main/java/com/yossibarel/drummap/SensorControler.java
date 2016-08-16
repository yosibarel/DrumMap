package com.yossibarel.drummap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by yossibarel on 17/04/16.
 */
public class SensorControler implements SensorEventListener {


    private SensorManager mSensorMgr;
    private SensorConrolerListener mListener;

    public interface SensorConrolerListener {
        void onSensorChange(double x, double y, double z);
    }

    public void create(Context context, SensorConrolerListener listener) {
        mListener = listener;
        mSensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        HandlerThread mHandlerThread = new HandlerThread("sensorThread");

        mHandlerThread.start();

        Handler handler = new Handler(mHandlerThread.getLooper());

        mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST, handler);

    }

    public void destroy() {
        mSensorMgr.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mListener.onSensorChange(event.values[0]/20.0+0.5, event.values[1]/20.0+0.5, event.values[2]/20.0+0.5);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
