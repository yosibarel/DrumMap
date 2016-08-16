package views.Custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yossibarel.drummap.DrumMapJni;

import Utils.SizeCalc;
import Utils.TimeUtils;

/**
 * Created by yossibarel on 30/04/16.
 */
public class WaveFormViewEdit extends View {


    private DrumMapJni drumMap;
    private Paint paint = new Paint();


    private double mWidth = 0;
    private double mHieght = 0;


    private float[] mBuffer;

    private double mZoom;

    private double mSaveZoom;
    private float mStart = 0.0f;
    private float mEnd = 0.0f;

    private float unitW;
    private boolean mCanSave;
    private float p1D;
    private float p2D;
    private float p2M;
    private float p1M;
    private float mStartSave;
    private float mEndSave;
    private float mDragXDown;
    private float p11D;
    private float p22D;
    private float mUpSave;
    private float mBottomSave;
    private float mUp;
    private float mBottom;
    private float p11M;
    private float p22M;
    private double mAmpView;
    private float unitW2;
    private float unitH2;
    private double mDuration;


    private double mStartPlayPositon;
    private double mEndPlayPosition;
    private int mIndexChannel;

    public WaveFormViewEdit(Context context) {
        super(context);
        paint.setColor(Color.BLACK);

        drumMap = DrumMapJni.getInstance();
    }


    public WaveFormViewEdit(Context context, AttributeSet attributes) {
        super(context, attributes);
        paint.setColor(Color.BLACK);
        drumMap = DrumMapJni.getInstance();


    }


    public void saveStart(double start) {
        if (mCanSave)
            mStartSave = (float) (start + mStartSave);

    }

    public void saveZoom(double startZoom, double endZoom) {


        mSaveZoom = (startZoom / endZoom) * mSaveZoom;

    }

    public void setZoom(double startZoom, double endZoom) {


        double mZoom = (startZoom) / (endZoom) * mSaveZoom;


        Log.d("draw", "mZoom =" + mZoom + " W =" + mWidth);
    }

    public void setIndexChannel(int indexChannel) {
        mIndexChannel = indexChannel;
    }

    public void setBuffer(float[] buffer) {

        mAmpView = 1;
        mUp = 0;
        mBottom = (float) (mHieght);
        mBottomSave = (float) (mHieght);


        mStartSave = 0.0f;
        mEndSave = (float) mWidth;

        mSaveZoom = 1.0;
        mZoom = 1.0;

        mEnd = (float) (mWidth);

        mBuffer = buffer;
    }


    @Override
    public void onDraw(Canvas canvas) {
        mWidth = canvas.getWidth();
        mHieght = canvas.getHeight();
        if (mBuffer != null) {

            paint.clearShadowLayer();
            paint.setColor(Color.BLACK);
            unitW = (mBuffer.length / (mEnd + mStart));
            unitW2 = (mEnd + mStart) / (canvas.getWidth());
            unitH2 = (mUp + mBottom) / (canvas.getHeight());

            float distanceLineParamColumns = (canvas.getWidth() / 10.0f) / (int) unitW2;
            float distanceLineParamRow = (canvas.getHeight() / 5.0f) / (int) (mAmpView + 1);


            //draw line horizontal
            paint.setStrokeWidth(5);
            Shader shader = new LinearGradient(0, canvas.getHeight(), 0, canvas.getWidth(), 0xff003368, 0xff003368, Shader.TileMode.CLAMP);
            Shader shaderText = new LinearGradient(0, canvas.getHeight(), 0, canvas.getWidth(), 0xffffffff, 0xffffffff, Shader.TileMode.CLAMP);
            for (float i = canvas.getHeight() / 2; i < canvas.getHeight(); i += distanceLineParamRow * mAmpView) {
                paint.setShader(shader);
                paint.setColor(0xff003368);
                canvas.drawLine(0, i, canvas.getWidth(), i, paint);
                float line = canvas.getHeight() - (i);
                canvas.drawLine(0, line, canvas.getWidth(), line, paint);

                paint.setTextSize(SizeCalc.pxFromDp(getContext(), 12));
                paint.setShader(shaderText);
                paint.setColor(Color.WHITE);
                float amp = (float) (((i - mHieght / 2) / (mHieght / 2)) / mAmpView);

                //draw text header row
                canvas.drawText(String.format("%.02f", -amp), 0, i, paint);

                canvas.drawText(String.format("%.02f", amp), 0, line, paint);
            }

            // line vertical
            paint.setShader(shader);
            for (float i = unitW2 * distanceLineParamColumns, count = 0; i < canvas.getWidth() + mStart; i += unitW2 * distanceLineParamColumns, count++) {
                paint.setShader(shader);
                paint.setColor(0xff003368);
                canvas.drawLine(i - mStart, 0, i - mStart, canvas.getHeight(), paint);


            }
            //draw text header col
            for (float i = unitW2 * distanceLineParamColumns, count = 0; i < canvas.getWidth() + mStart; i += unitW2 * distanceLineParamColumns, count++) {


                paint.setTextSize(SizeCalc.pxFromDp(getContext(), 12));
                paint.setShader(shaderText);
                paint.setColor(Color.WHITE);
                canvas.drawText(TimeUtils.getStringPosition(mDuration * (i / (mStart + mEnd))), i - mStart, 50, paint);


            }


            //draw wave
            for (float i = 0; i < canvas.getWidth(); ++i) {


                if (mEnd + mStart < mBuffer.length / 4) {
                    if (i % 8 == 0) {

                        float sample = (float) (mBuffer[(int) (mStart * unitW + i * unitW)] * mAmpView * mHieght / 2 + mHieght / 2);

                        shader = new LinearGradient(0, canvas.getHeight() / 2, 0, sample, Color.argb(255, 255, 255, 255), Color.argb(50, 0, 0, 255), Shader.TileMode.MIRROR);
                        paint.setShader(shader);


                        canvas.drawLine(i, sample, i, (float) (-mBuffer[(int) (mStart * unitW + i * unitW)] * mAmpView * mHieght / 2 + mHieght / 2), paint);
                    }
                } else {

                    float sample = (float) (mBuffer[(int) (mStart * unitW + i * unitW)] * mAmpView * mHieght / 2 + mHieght / 2);
                    shader = new LinearGradient(0, canvas.getHeight() / 2, 0, sample, Color.argb(255, 255, 255, 255), Color.argb(255, 255, 255, 255), Shader.TileMode.CLAMP);
                    paint.setShader(shader);

                    if (i + 1 < canvas.getWidth())
                        canvas.drawLine(i, sample, i + 1, (float) (mBuffer[(int) (mStart * unitW + (i + 1) * unitW)] * mAmpView * mHieght / 2 + mHieght / 2), paint);

                }
            }

            shader = new LinearGradient(0, canvas.getHeight(), 0, canvas.getWidth(), 0x77003368, 0x77003368, Shader.TileMode.CLAMP);
            //   mStartPlayPositon = 0.25 * mDuration;
            //   mEndPlayPosition = 0.75 * mDuration;
          /*  double pos1 = (mStartPlayPositon / mDuration) *(mStart+mEnd)-mStart;
            double pos2 = (mEndPlayPosition / mDuration) * (mStart+mEnd)-mStart;*/

            double pos1 = (mStartPlayPositon) * (mStart + mEnd) - mStart;
            double pos2 = (mEndPlayPosition) * (mStart + mEnd) - mStart;
            paint.setShader(shader);
            canvas.drawRect((float) pos1, 0, (float) pos2, (float) mHieght, paint);


        }

    }


    public void setStartEndDown(float x, float x1) {
        p1D = x;
        p2D = x1;
    }

    public void setStartEndMove(float x, float x1) {

        if (x < x1) {
            p1M = x;
            p2M = x1;
            mStart = mStartSave + (p1D - p1M);
            mEnd = mEndSave + (p2M - p2D);
        } else {
            p1M = x;
            p2M = x1;
            mStart = mStartSave + (p2D - p2M);
            mEnd = mEndSave + (p1M - p1D);
        }
        if (mEnd < mWidth)
            mEnd = (float) mWidth;
        if (mStart < 0)
            mStart = 0;
        Log.e("up_bottom", "p1D=" + p1D + " p1M= " + p1M + " p2D=" + p2D + " p2M=" + p2M);


    }

    public void setStartEndUp() {
        mStartSave = mStart;
        mEndSave = mEnd;


    }

    public void setUpBottomDown(float x, float x1) {
        p11D = x;
        p22D = x1;
    }

    public void setUpBottomMove(float x, float x1) {

        if (x < x1) {
            p11M = x;
            p22M = x1;
            mUp = mUpSave + (p11D - p11M);
            mBottom = mBottomSave + (p22M - p22D);
        } else {
            p11M = x;
            p22M = x1;
            mUp = mUpSave + (p22D - p22M);
            mBottom = mBottomSave + (p11M - p11D);
        }
        mAmpView = mHieght / (mBottom - mUp);
        Log.e("up_bottom", "up=" + mUp + " bottom= " + mBottom + " res=" + (mBottom - mUp) / mHieght);


    }

    public void setUpBottomUp() {
        mUpSave = mUp;
        mBottomSave = mBottom;


    }


    public void setStartDragDown(float x) {
        mDragXDown = x;
    }

    public void setStartDragMove(float x) {
        if (mStart - (mDragXDown - x) >= 0 && mEnd + (mDragXDown - x) > mWidth) {
            mStart -= (mDragXDown - x);
            mEnd += (mDragXDown - x);

            mStartSave = mStart;
            mEndSave = mEnd;
        } else if (mStart - (mDragXDown - x) < 0)
            mStart = 0;
        else if (mEnd + (mDragXDown - x) < mWidth)
            mEnd = (float) mWidth;
    }

    public double getEndPrecent() {
        return mStart / (mEnd + mStart) + mWidth / (mEnd + mStart);
    }

    public double getStartPrecent() {
        return mStart / (mEnd + mStart);
    }

    public double getStartWidth() {
        return mStart;
    }

    public double getEndWidth() {
        return mEnd;
    }

    public void setEnd(double end) {
        mEnd = (float) (mWidth / end - mStart);
    }


    public void setStartEnd(double start, double end) {

        //mStart = (float) ((mWidth / (end- start)) - mWidth);


        mStart = (float) (mWidth / (1 - start) - mWidth);
        mEnd = (float) ((mWidth / (end)));

        mStartSave = mStart;
        mEndSave = mEnd;
        Log.e("wave", "start=" + mStart + " end =" + mEnd);
    }

    public void setStartEndPosition(float pos1, float pos2) {

        //   drumMap.loopBetween(mIndexChannel, pos1, pos2, false);
        pos1 =pos1* (float) mWidth / (mEnd + mStart) + mStart / (mEnd + mStart);
        pos2 = pos2 *(float) mWidth / (mEnd + mStart) + mStart / (mEnd + mStart);

        if (Math.abs(pos1) < Math.abs(pos2)) {
            mStartPlayPositon = pos1;
            mEndPlayPosition = pos2;

        } else {
            mStartPlayPositon = pos2;
            mEndPlayPosition = pos1;

        }
        drumMap.loopBetween(mIndexChannel, mStartPlayPositon * mDuration, mEndPlayPosition * mDuration, false);

    }

    public void setPlayPosition(double pos) {

        pos = pos*(float) mWidth / (mEnd + mStart) + mStart / (mEnd + mStart);

        Log.e("position", (pos * 100) + "");
        if (Math.abs(pos - mStartPlayPositon) < Math.abs(pos - mEndPlayPosition)) {
            mStartPlayPositon = pos;

        } else
            mEndPlayPosition = pos;
        drumMap.loopBetween(mIndexChannel, mStartPlayPositon * mDuration, mEndPlayPosition * mDuration, false);

    }

    public double getCanvasWidth() {
        return mWidth;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }
}