package views.Custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import enums.WaveType;

/**
 * Created by yossibarel on 30/03/16.
 */


public class WaveFormView extends View {


    private Paint paint = new Paint();


    private double[] mWave = null;
    private double mWidth = 0;
    private double mHieght = 0;
    private double mRate = 1.0;
    private boolean mIsMain = false;
    private double mAmplitude = 1.0;
    private OnDrawListener mListener;

    private WaveType mWaveType = WaveType.SINUS;

    public WaveFormView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
        mRate = 1;

    }

    public WaveFormView(Context context, AttributeSet attributes) {
        super(context, attributes);
        paint.setColor(Color.BLACK);
        mRate = 1.0;

    }

    public void setRate(double rate) {
        mRate = rate;
    }

    public void setWave(double[] wave) {
        mWave = wave;
        mIsMain = true;

    }

    public void setWaveType(WaveType waveType) {
        mWaveType = waveType;
    }

    public void setAmplitude(double amp) {
        this.mAmplitude = amp;
    }

    public double[] getWave() {
        return mWave;
    }

    public void setOnDrawListener(OnDrawListener l) {
        mListener = l;
    }

    @Override
    public void onDraw(Canvas canvas) {

        mWidth = canvas.getWidth();
        mHieght = canvas.getHeight();
        paint.setColor(Color.BLACK);
        if (mWave == null)
            mWave = new double[(int) mWidth];
        if (!mIsMain) {

            switch (mWaveType) {
                case SINUS:
                    createSinus();
                    break;
                case SAW:
                    createSaw();
                    break;
                case RECTANGLE:
                    createRect();
                    break;
                case TRIANGLE:
                    createTriangle();
                    break;

            }

            if (mWave == null)
                return;
        }
        Shader shader = new LinearGradient(0, 0, canvas.getWidth(), 0, Color.argb(235, 74, 138, 255), Color.RED, Shader.TileMode.REPEAT);
        paint.setShader(shader);
        paint.setStrokeWidth(5);

        for (float i = 0; i < canvas.getWidth() - 1; ++i) {
            canvas.drawLine(i, (float) (mWave[(int) i] + mHieght / 2), i + 1, (float) (mWave[(int) (i + 1)] + mHieght / 2), paint);
        }
        if (mListener != null)
            mListener.onDraw();
    }

    void createSinus() {
        double sinusUnit = (360.0f / (float) mWidth) * mRate ;
        for (int i = 0; i < mWave.length; i++) {

            mWave[i] = Math.sin(Math.toRadians((double) i * sinusUnit)) * (mHieght / 2) * mAmplitude;
        }
    }

    void createSaw() {
        if (mRate == 0)
            return;

        int mod = (int) ((float) mWidth / mRate);
        for (int i = 0; i < mWave.length; i++) {
            mWave[i] = (((double) (i % mod) / (double) (mod - 1)) * 2 - 1) * (mHieght / 2) * mAmplitude;
        }
    }

    void createRect() {
        if (mRate == 0)
            return;

        int val = (int) ((float) mWidth  / mRate);
        for (int i = 0; i < mWave.length; i++) {
            mWave[i] = (double) ((i % val < val / 2 ? 1 : -1)) * (mHieght / 2) * mAmplitude;
        }
    }

    void createTriangle() {
        if (mRate == 0)
            return;

        int val = (int) ((double) mWidth  / mRate);
        double unit = 1.0 / ((double) val / 4);

        double temp = 0;
        for (int i = 0; i < mWave.length; i++) {

            if (Math.abs(temp + unit) > 1)
                unit = -unit;
            temp = temp + unit;
            mWave[i] = temp * (mHieght / 2) * mAmplitude;
        }
    }

    public interface OnDrawListener {
        void onDraw();
    }

}