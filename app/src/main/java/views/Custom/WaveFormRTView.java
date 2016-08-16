package views.Custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yossibarel on 30/04/16.
 */
public class WaveFormRTView extends View {


    private Paint paint = new Paint();


    private double[] mWave = null;
    private double mWidth = 0;
    private double mHieght = 0;


    private float[] mBuffer;

    private double mZoom;

    private double mSaveZoom;
    private double mStart = 0.0;
    private double mSaveStart = 0.0;
    private float unitW;
    private boolean mCanSave;


    public WaveFormRTView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);


    }

    public WaveFormRTView(Context context, AttributeSet attributes) {
        super(context, attributes);
        paint.setColor(Color.BLACK);


    }

    public void setStart(double start) {

        double value = start + mSaveStart;
        if ((int) ((value * mZoom) + ((0 * unitW) * mZoom)) >= 0 && (int) ((value * mZoom) + (((mWidth - 1) * unitW) * mZoom)) < mBuffer.length - 1) {
            mStart = value;
            mCanSave = true;
        } else
            mCanSave = false;

    }

    public void saveStart(double start) {
        if (mCanSave)
            mSaveStart = start + mSaveStart;

    }

    public void saveZoom(double startZoom, double endZoom) {

        if (mCanSave)
            mSaveZoom = (startZoom / endZoom) * mSaveZoom;

    }

    public void setZoom(double startZoom, double endZoom) {


        double value = (startZoom) / (endZoom) * mSaveZoom;


        if ((int) ((mStart * value) + ((0 * unitW) * value)) >= 0 && (int) ((mStart * value) + (((mWidth - 1) * unitW) * value)) < mBuffer.length - 1) {
            mZoom = value;
            mCanSave = true;
        } else
            mCanSave = false;

    }

    public void setBuffer(float[] buffer) {

        mSaveZoom = 1.0;
        mZoom = 1.0;
        mBuffer = buffer;
    }


    @Override
    public void onDraw(Canvas canvas) {

        if (mBuffer != null) {
            mWidth = canvas.getWidth();
            mHieght = canvas.getHeight();
            paint.setColor(Color.BLACK);
            unitW = ((float) (mBuffer.length) / (float) mWidth);


            paint.setStrokeWidth(5);

            for (float i = 0; i < canvas.getWidth(); ++i) {

                if (i % 8 == 0) {

                    float sample = (float) (mBuffer[(int) ((mStart * mZoom) + ((i * unitW) * mZoom))] * mHieght / 2 + mHieght / 2);
                    Shader shader = new LinearGradient(0, canvas.getHeight() / 2, 0, sample, Color.argb(255, 255, 255, 255), Color.argb(20, 0, 0, 255), Shader.TileMode.MIRROR);
                    paint.setShader(shader);


                    canvas.drawLine(i, sample, i, (float) (-mBuffer[(int) ((mStart * mZoom) + ((i * unitW) * mZoom))] * mHieght / 2 + mHieght / 2), paint);
                }
            }
        }

    }


}