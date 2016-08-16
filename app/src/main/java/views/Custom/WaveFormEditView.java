package views.Custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.yossibarel.drummap.DrumMapJni;

import java.util.ArrayList;

import Utils.DLog;
import Utils.SizeCalc;
import Utils.TimeUtils;
import models.Midi;

/**
 * Created by yossibarel on 23/05/16.
 */
public class WaveFormEditView extends View {


    private final DrumMapJni mDrumMap;
    private boolean[] mOrderColor = {true, false, true, false, true, true, false, true, false, true, false, true, true, false, true, false, true, true, false, true, false, true, false, true};
    private Paint paint = new Paint();

    private double mWidth = 0;


    private double mHieght = 0;
    private double mZoomX;
    private double mZoomY;
    private float mStart = 0.0f;
    private float mEnd = 0.0f;
    private float mTop = 0.0f;
    private float mBottom = 0.0f;
    private float mDragDownX;
    private float mDragDownY;
    private float mMoveDownX1;
    private float mMoveDownX2;
    private float mMoveDownY1;
    private float mMoveDownY2;
    private double mUnitX;
    private double mUnitY;
    private double mDurationBeats;
    float mHeaderSizePx = 50;
    float mHeaderSizePxx = 0;
    private double mStartPlayPositonPercent;
    private double mEndPlayPositionPercent;
    private int mIndexChannel;

    ArrayList<Midi> pianoList = new ArrayList<>();
    private float mDistanceLineParamColumns;
    private float mSizeHeaderColumns;
    private float mBpm;
    private LinearGradient shaderWhite;
    private LinearGradient shaderBlack;
    private LinearGradient mShaderMidi;
    private LinearGradient shaderGray;

    short[] mBeats = new short[]{8, 8, 16, 16, 16, 16, 16, 32, 32, 32, 32, 32, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 512
            , 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 1024};
    private double mZoomXCenter;
    private double mZoomYCenter;
    private LinearGradient mShaderHeader;
    private float[] mBuffer;
    private LinearGradient mShaderLine;
    private long mDuration;
    private LinearGradient mShaderLoop;
    private LinearGradient mShaderWhite;


    public WaveFormEditView(Context context) {
        super(context);


        mDrumMap = DrumMapJni.getInstance();

    }

    public float getRemoveHeader() {
        return mHeaderSizePx;

    }

    public WaveFormEditView(Context context, AttributeSet attributes) {
        super(context, attributes);


        mDrumMap = DrumMapJni.getInstance();


    }

    public void setIndexChannel(int indexChannel) {
        mIndexChannel = indexChannel;
    }

    float getRealWidth() {
        return (float) (mWidth - mHeaderSizePx);
    }

    float getRealHieght() {
        return (float) (mHieght - mHeaderSizePx);
    }

    public void init() {


        mDurationBeats = 128;
        mZoomX = 1.0;
        mZoomY = 1.0;
        mBottom = (float) getRealHieght();
        mEnd = (float) (getRealWidth());
        mSizeHeaderColumns = (float) ((getRealHieght() / (mOrderColor.length + 2)) * 2);
        shaderWhite = new LinearGradient(0.0f, 0f, (float) getRealWidth(), (float) getRealHieght(), Color.WHITE, Color.WHITE, Shader.TileMode.CLAMP);
        shaderBlack = new LinearGradient(0.0f, 0f, (float) getRealWidth(), (float) getRealHieght(), Color.BLACK, Color.BLACK, Shader.TileMode.CLAMP);
        shaderGray = new LinearGradient(0.0f, 0f, (float) getRealWidth(), (float) getRealHieght(), 0xff777777, 0xff777777, Shader.TileMode.CLAMP);
        mShaderHeader = new LinearGradient(0.0f, 0f, (float) getRealWidth(), (float) getRealHieght(), 0xff113355, 0xff113355, Shader.TileMode.CLAMP);
        mShaderMidi = new LinearGradient(0.0f, 0f, (float) getRealWidth(), (float) getRealHieght(), 0xffff0000, 0xffff0000, Shader.TileMode.MIRROR);
        mIsInit = true;
        mShaderLine = new LinearGradient(0, (float) mHieght, 0, (float) mWidth, 0xff003368, 0xff003368, Shader.TileMode.CLAMP);
        mShaderLoop = new LinearGradient(0, (float) mHieght, 0, (float) mWidth, 0x77003368, 0x77003368, Shader.TileMode.CLAMP);
        mShaderWhite = new LinearGradient(0, (float) mHieght, 0, (float) mWidth, Color.argb(255, 255, 255, 255), Color.argb(255, 255, 255, 255), Shader.TileMode.CLAMP);

    }

    boolean mIsInit = false;


    @Override
    protected void onDraw(Canvas canvas) {

        mWidth = canvas.getWidth();
        mHieght = canvas.getHeight();
        if (!mIsInit)
            init();
        if (mBuffer == null)
            return;
        Shader shader = new LinearGradient(0, canvas.getHeight(), 0, canvas.getWidth(), 0xfff99999, 0xfff99999, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        mDistanceLineParamColumns = (float) ((canvas.getWidth() / (10.0)));// / mBeats[(int) unitW2]);


        int index = ((int) mZoomX) < mBeats.length ? (int) mZoomX : mBeats.length - 1;
        short numLines = mBeats[index];
        DLog.d("index", "index = " + index + " val = " + numLines);

        mUnitX = ((mZoomX * getRealWidth())) / numLines;
        mUnitY = ((mZoomY * getRealHieght())) / 8;

        paint.setStrokeWidth(5);


        //draw grid
        paint.setShader(mShaderLine);
        for (double i = mHeaderSizePx - mStart * mZoomX; i < mWidth + mStart; i += mUnitX)
            if (i >= 0 && i <= mWidth)
                canvas.drawLine((float) i, 0, (float) i, (float) mHieght, paint);

        canvas.drawLine(0, (float) getRealHieght() + mHeaderSizePx, (float) mWidth, (float) getRealHieght() + mHeaderSizePx, paint);
        for (double i = getRealHieght() / 2 + mHeaderSizePx, j = getRealHieght() / 2 + mHeaderSizePx; i < mHieght; i += mUnitY, j -= mUnitY) {
            canvas.drawLine(0, (float) i, (float) mWidth, (float) i, paint);
            canvas.drawLine(0, (float) j, (float) mWidth, (float) j, paint);

        }


        if (mZoomX < 8) {


            for (float i = mHeaderSizePx; i < mWidth; ++i) {
                if (i % 8 == 0) {
                    double percent = getRealPercentX(i - mHeaderSizePx);
                    double indexSample = percent * (double) mBuffer.length;
                    float sample = (float) (mBuffer[((int) (indexSample))] * mZoomY /** mAmpView */ * getRealHieght() / 2 + getRealHieght() / 2 + mHeaderSizePx);

                    shader = new LinearGradient(0, getRealHieght() / 2 + mHeaderSizePx, 0, sample, Color.argb(255, 255, 255, 255), Color.argb(50, 0, 0, 255), Shader.TileMode.MIRROR);
                    paint.setShader(shader);


                    canvas.drawLine(i, sample, i, (float) (-mBuffer[((int) indexSample)] * mZoomY * getRealHieght() / 2 + getRealHieght() / 2 + mHeaderSizePx), paint);
                }
            }
        } else {
            // paint.setStrokeWidth(1);
            paint.setShader(shader);
            for (float i = mHeaderSizePx; i < mWidth; i += 8) {

                double percent1 = getRealPercentX((i - mHeaderSizePx));
                double percent2 = getRealPercentX((i - mHeaderSizePx) + 8);
                double indexSample1 = percent1 * (double) mBuffer.length;
                double indexSample2 = percent2 * (double) mBuffer.length;
                if (indexSample2 >= mBuffer.length)
                    indexSample2 = mBuffer.length - 1;
                if (indexSample1 >= mBuffer.length)
                    indexSample1 = mBuffer.length - 1;
                float sample1 = (float) (mBuffer[((int) indexSample1)] * mZoomY * getRealHieght() / 2 + getRealHieght() / 2 + mHeaderSizePx);
                float sample2 = (float) (mBuffer[((int) indexSample2)] * mZoomY * getRealHieght() / 2 + getRealHieght() / 2 + mHeaderSizePx);



                canvas.drawLine(i, sample1, i + 8
                        , sample2, paint);
            }
        }

        paint.setStrokeWidth(5);
        //draw rect corner left top
        paint.setShader(mShaderHeader);
        canvas.drawRect(0, (float) 0, mHeaderSizePx, mHeaderSizePx, paint);
        super.onDraw(canvas);

        //draw loop
        double pos1 = getXCanvasPercent(mStartPlayPositonPercent);//(mStartPlayPositonPercent) * (getRealWidth()) * mZoomX - mStart * mZoomX;
        double pos2 = getXCanvasPercent(mEndPlayPositionPercent);//(mEndPlayPositionPercent) * (getRealWidth()) * mZoomX - mStart * mZoomX;
        paint.setShader(mShaderLoop);
        if (pos1 < mHeaderSizePx)
            pos1 = mHeaderSizePx;
        canvas.drawRect((float) (pos1), mHeaderSizePx, (float) (pos2), (float) mHieght, paint);


        paint.setShader(mShaderHeader);


        //draw header top
        canvas.drawRect(0, (float) 0, (float) mWidth, mHeaderSizePx, paint);

        //draw text top
        double ms = 0;
        double msCount = (double) ((double) mDuration / (double) numLines);
        paint.setTextSize(SizeCalc.pxFromDp(getContext(), 12));
        paint.setShader(shaderWhite);
        for (double i = mHeaderSizePx - mStart * mZoomX; i < mWidth + mStart; i += mUnitX) {
            if (i >= -200 && i <= mWidth)
                canvas.drawText(TimeUtils.getStringPosition(ms), (float) i, 40, paint);
            ms += msCount;
        }
        //draw header left
        paint.setShader(mShaderHeader);
        canvas.drawRect(0, (float) 0, (float) mHeaderSizePx, (float) mHieght, paint);

        //draw text left
        paint.setTextSize(SizeCalc.pxFromDp(getContext(), 8));
        double amp = 0;
        double countAmp = 1.0 / 4.0;
        paint.setShader(shaderWhite);
        canvas.drawText("0", (float) 4, getRealHieght() / 2 + mHeaderSizePx, paint);
        for (double i = mUnitY; i < mHieght / 2; i += mUnitY) {
            if (i >= 0 && i <= mHieght) {
                amp += countAmp;
                canvas.drawText(String.format("-%.1f", amp), (float) 4, (float) (getRealHieght() / 2 + mHeaderSizePx + i), paint);
                canvas.drawText(String.format(" %.1f", amp), (float) 4, (float) (getRealHieght() / 2 + mHeaderSizePx - i), paint);
            }
        }
    }

    public double getXCanvasPercent(double percentX) {
        return mHeaderSizePx + ((percentX) * (getRealWidth()) * mZoomX - mStart * mZoomX);
    }

    public void setStartEnd(double start, double end) {


        mStart = (float) (start * getRealWidth());
        mEnd = (float) (end * getRealWidth());

        mZoomXCenter = ((mEnd - mStart) / 2.0) + mStart;
        mZoomX = getRealWidth() / (mEnd - mStart);

    }

    public void setTopBottom(double top, double bottom) {


        mTop = (float) (top * getRealHieght());
        mBottom = (float) (bottom * getRealHieght());

        mZoomYCenter = ((mBottom - mTop) / 2.0) + mTop;
        mZoomY = getRealHieght() / (mBottom - mTop);

    }


    public void setDuration(double duration) {
        mDurationBeats = duration;
    }


    public float getColumnsHeaderSize() {
        return mSizeHeaderColumns;
    }

    public void setBpm(float bpm) {
        mBpm = bpm;
    }


    public void actionDragDown(float x, float y) {
        mDragDownX = x;
        mDragDownY = y;

    }

    public void actionDragMove(float x, float y) {

        //left right
        float distance = mEnd - mStart;
        float resultStart = (float) (mStart - ((mDragDownX - x) / (20.0 * mZoomX)));
        float resultEnd = (float) (mEnd - ((mDragDownX - x) / (20.0 * mZoomX)));
        if (resultStart >= 0 && resultEnd <= getRealWidth()) {
            mStart = resultStart;
            mEnd = resultEnd;
        } else if (resultStart < 0) {
            mStart = 0;
            mEnd = distance;
        } else if (resultEnd > getRealWidth()) {
            mEnd = (float) getRealWidth();
            mStart = mEnd - distance;
        }
        // up bootom
       /* distance = mBottom - mTop;
        float resultTop = (float) (mTop - ((mDragDownY - y) / (20.0 * mZoomY)));
        float resultBottom = (float) (mBottom - ((mDragDownY - y) / (20.0 * mZoomY)));
        if (resultTop >= 0 && resultBottom <= getRealHieght()) {
            mTop = resultTop;
            mBottom = resultBottom;
        } else if (resultTop < 0) {
            mTop = 0;
            mBottom = distance;
        } else if (resultBottom > getRealHieght()) {
            mBottom = (float) getRealHieght();
            mTop = mBottom - distance;
        }*/


    }

    public void actionZoomDown(float x1, float y1, float x2, float y2) {

        mMoveDownX1 = Math.min(x1, x2);
        mMoveDownX2 = Math.max(x1, x2);
        mMoveDownY1 = Math.min(y1, y2);
        mMoveDownY2 = Math.max(y1, y2);

    }

    public void actionZoomMove(float x1, float y1, float x2, float y2) {


        //zoom x
        float X1 = Math.min(x1, x2);
        float X2 = Math.max(x1, x2);
        float resultStart = (float) (mStart + ((mMoveDownX1 - X1) / (10.0 * mZoomX)));
        float resultEnd = (float) (mEnd + ((mMoveDownX2 - X2) / (10.0 * mZoomX)));


        if (resultStart >= 0) {
            mStart = resultStart;
        } else
            mStart = 0;

        if (resultEnd <= getRealWidth())
            mEnd = resultEnd;
        else
            mEnd = (float) getRealWidth();

        mZoomXCenter = ((mEnd - mStart) / 2.0) + mStart;
        mZoomX = getRealWidth() / (mEnd - mStart);

        //zoomY

        float Y1 = Math.min(y1, y2);
        float Y2 = Math.max(y1, y2);
        float resultTop = (float) (mTop + ((mMoveDownY1 - Y1) / (5 * mZoomY)));
        float resultBottom = (float) (mBottom + ((mMoveDownY2 - Y2) / (5 * mZoomY)));


        if (resultTop >= 0) {
            mTop = resultTop;
        } else
            mTop = 0;

        if (resultBottom <= getRealHieght())
            mBottom = resultBottom;
        else
            mBottom = (float) getRealHieght();

        mZoomYCenter = (((mBottom - mTop) / 2.0) + mTop);

        mZoomY = getRealHieght() / (mBottom - mTop);

    }

    public float getEndPercent() {
        return (float) (mEnd / getRealWidth());
    }

    public float getStartPercent() {
        return (float) (mStart / getRealWidth());
    }

    public float getTopPercent() {
        return (float) (mTop / getRealHieght());
    }

    public float getBottomPercent() {
        return (float) (mBottom / getRealHieght());
    }

    public double getPercentX(float x) {
        return mStart / mWidth + (x / mWidth * ((mEnd - mStart) / mWidth));
    }

    public double getRealPercentX(float x) {
        return mStart / getRealWidth() + (x / getRealWidth() * ((mEnd - mStart) / getRealWidth()));
    }
    public double getRealPercentY(float y) {
        return mStart / getRealHieght() + (y / getRealHieght() * ((mBottom - mTop) / getRealHieght()));
    }

   /* public double getPercentX(float x) {
        return mStart / getRealWidth() + (x / getRealWidth() * ((mEnd - mStart) / getRealWidth()));
    }*/

    public double getPercentY(float y) {
        return mTop / getRealHieght() + (y / getRealHieght() * ((mBottom - mTop) / getRealHieght()));
    }


    public void setBuffer(float[] mEditBuffer) {
        mBuffer = mEditBuffer;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public void setStartEndPlayPositionPercent(double startPlayPosition, double stopPlayPosition) {
        mStartPlayPositonPercent = startPlayPosition;
        mEndPlayPositionPercent = stopPlayPosition;
    }

    public void setPositionStartOrEnd(double touchPosition) {
        if (Math.abs(mStartPlayPositonPercent - touchPosition) < Math.abs(mEndPlayPositionPercent - touchPosition)) {
            mStartPlayPositonPercent = touchPosition;

        } else {
            mEndPlayPositionPercent = touchPosition;
        }
    }

    public double getStartPlayPositionPercent() {
        return mStartPlayPositonPercent;
    }

    public double getEndPlayPositionPercent() {
        return mEndPlayPositionPercent;
    }

    public void setPositionStartEnd(double start, double end) {
        mStartPlayPositonPercent = start;
        mEndPlayPositionPercent = end;
    }

    public void setZoomY(double zoomY) {
        mTop = (float) (mHieght / zoomY) / 2;
        mHieght = (float) mHieght - mTop;
        mZoomY = zoomY;
    }

    public double getZoomY() {
        return mZoomY;
    }
}