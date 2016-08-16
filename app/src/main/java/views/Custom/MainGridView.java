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

import java.util.ArrayList;

import Utils.SizeCalc;
import models.ChannelItem;

/**
 * Created by yossibarel on 05/06/16.
 */
public class MainGridView extends View {


    private double mIncrementCanvasAnimationStart;
    private double mIncrementCanvasAnimationEnd;

    public void incrementAnimationDuration() {
        mDurationBeats += mIncrementCanvasAnimationDuration;
        mStartPlayPositonPercent -= mIncrementCanvasAnimationStart;
        mEndPlayPositionPercent -= mIncrementCanvasAnimationEnd;
    }

    public double getDurationBeat() {
        return mDurationBeats;
    }

    private double mIncrementCanvasAnimationDuration;

    public void notifyStartAnimationCanvasDuration() {
        mIncrementCanvasAnimationDuration = mDurationBeats * 0.05;
        mIncrementCanvasAnimationStart = mStartPlayPositonPercent / 2 * 0.05;
        mIncrementCanvasAnimationEnd = mEndPlayPositionPercent / 2 * 0.05;


    }

    public interface ChannelItemChangeListener {
        void onChannelItemAdd(ChannelItem channelItem);

        void onChannelItemDelete(ChannelItem channelItem);

        void onChannelItemLenChange(ChannelItem channelItem);
    }

    public void setChannelItemChangeListener(ChannelItemChangeListener channelItemChangeListener) {
        this.mChannelItemChangeListener = channelItemChangeListener;
    }

    private ChannelItemChangeListener mChannelItemChangeListener;
    private static final double NUM_CHANNELS = 16;
    private final DrumMapJni mDrumMap;
    private Paint[] mChannelPaint = new Paint[(int) NUM_CHANNELS];

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
    private double mStartPlayPositon;
    private double mEndPlayPosition;


    ArrayList<ChannelItem> channelItemList = new ArrayList<>();
    private float mDistanceLineParamColumns;
    private float mSizeHeaderColumns;
    private float mBpm;
    private LinearGradient mShaderWhite;
    private LinearGradient mShaderBlack;
    private LinearGradient mShaderMidi;
    private LinearGradient shaderGray;

    short[] mBeats = new short[]{8, 8, 16, 16, 16, 16, 16, 32, 32, 32, 32, 32, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 256, 512
            , 512, 512, 512, 512, 512, 512, 512, 512, 512, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 1024, 2048, 2048, 2048, 2048, 2048, 2048, 2048, 2048, 2048, 2048, 2048};


    int[] mChannelsColor = new int[(int) NUM_CHANNELS];
    private double mZoomXCenter;
    private double mZoomYCenter;
    private LinearGradient mShaderHeader;
    private LinearGradient mShaderKeyWhite;
    private Paint mMidiPaintBorder;
    private double mStartPlayPositonPercent;
    private double mEndPlayPositionPercent;
    private LinearGradient mShaderLoop;
    private float mPositionBeat;
    private Paint mMidiPaintBorderPlay;
    private Paint mMidiPaintPlay;
    private ChannelItem mChannelItemAdded = null;


    public MainGridView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);

        mDrumMap = DrumMapJni.getInstance();

    }

    public MainGridView(Context context, AttributeSet attributes) {
        super(context, attributes);
        paint.setColor(Color.BLACK);


        mDrumMap = DrumMapJni.getInstance();
        for (int i = 0; i < NUM_CHANNELS; ++i) {
            mChannelsColor[i] = mDrumMap.getChannelColor(i);
        }

    }

    float getRealWidth() {
        return (float) (mWidth - mHeaderSizePx);
    }

    float getRealHieght() {
        return (float) (mHieght - mHeaderSizePx);
    }

    public void init() {


        mDurationBeats = mDrumMap.getMainDurationBeat();
        mZoomX = 1.0;
        mZoomY = 1.0;
        mBottom = (float) getRealHieght();
        mEnd = (float) (getRealWidth());
        mSizeHeaderColumns = (float) ((getRealHieght() / (NUM_CHANNELS + 2)) * 2);
        mShaderKeyWhite = new LinearGradient(0, (float) mHieght, 0, (float) mWidth, 0x77003368, 0x77003368, Shader.TileMode.CLAMP);
        mShaderWhite = new LinearGradient(0.0f, 0f, (float) getRealWidth(), (float) getRealHieght(), Color.WHITE, Color.WHITE, Shader.TileMode.CLAMP);
        // new LinearGradient(0.0f, 0f, (float) getRealWidth(), (float) getRealHieght(), Color.WHITE, Color.WHITE, Shader.TileMode.CLAMP);
        mShaderBlack = new LinearGradient(0.0f, 0f, (float) getRealWidth(), (float) getRealHieght(), Color.BLACK, Color.BLACK, Shader.TileMode.CLAMP);
        shaderGray = new LinearGradient(0, (float) mHieght, 0, (float) mWidth, 0xff003368, 0xff003368, Shader.TileMode.CLAMP);
        // new LinearGradient(0.0f, 0f, (float) getRealWidth(), (float) getRealHieght(), 0xff777777, 0xff777777, Shader.TileMode.CLAMP);
        mShaderHeader = new LinearGradient(0.0f, 0f, (float) getRealWidth(), (float) getRealHieght(), 0xff113355, 0xff113355, Shader.TileMode.CLAMP);
        mShaderMidi = new LinearGradient(0.0f, 0f, (float) getRealWidth(), (float) getRealHieght(), 0xffff0000, 0xffff0000, Shader.TileMode.MIRROR);
        mShaderLoop = new LinearGradient(0, (float) mHieght, 0, (float) mWidth, 0x77003368, 0x77003368, Shader.TileMode.CLAMP);


        mMidiPaintBorder = new Paint();
        mMidiPaintBorder.setStyle(Paint.Style.STROKE);
        mMidiPaintBorder.setStrokeWidth(3);
        mMidiPaintBorder.setARGB(255, 255, 255, 255);

        mMidiPaintPlay = new Paint(paint);
        mMidiPaintPlay.setStyle(Paint.Style.FILL);
        mMidiPaintPlay.setARGB(255, 0, 255, 0);
        mMidiPaintBorderPlay = new Paint();
        mMidiPaintBorderPlay.setStyle(Paint.Style.STROKE);
        mMidiPaintBorderPlay.setStrokeWidth(3);
        mMidiPaintBorderPlay.setARGB(255, 255, 255, 255);
        for (int i = 0; i < NUM_CHANNELS; ++i) {
            mChannelPaint[i] = new Paint(paint);
            mChannelPaint[i].setStyle(Paint.Style.FILL);
            mChannelPaint[i].setColor(mChannelsColor[i]);
        }
        mIsInit = true;


    }

    boolean mIsInit = false;


    @Override
    protected void onDraw(Canvas canvas) {

        mWidth = canvas.getWidth();
        mHieght = canvas.getHeight();
        if (!mIsInit)
            init();
        Shader shader = new LinearGradient(0, canvas.getHeight(), 0, canvas.getWidth(), 0xfff99999, 0xfff99999, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        mDistanceLineParamColumns = (float) ((canvas.getWidth() / (10.0)));// / mBeats[(int) unitW2]);


        short numLines = mBeats[((int) mZoomX) < mBeats.length ? (int) mZoomX : mBeats.length - 1];
        mUnitX = ((mZoomX * getRealWidth())) / numLines;
        mUnitY = ((mZoomY * getRealHieght())) / NUM_CHANNELS;


        //draw piano
        int index = 0;
        for (double i = mHeaderSizePx - mTop * mZoomY; i < mHieght + mTop; i += mUnitY, index++) {
            if (i >= 0 - mUnitY && i <= mWidth) {

                if (index < NUM_CHANNELS) {
                    /*if (mOrderColor[index])
                        paint.setShader(mShaderKeyWhite);
                    else
                        paint.setShader(mShaderBlack);
                    canvas.drawRect(0, (float) i, (float) mWidth, (float) (i + mUnitY), paint);
                    */
                    paint.setShader(shaderGray);
                    canvas.drawLine(0, (float) i, canvas.getWidth(), (float) i, paint);
                }
            }
        }
        //draw beat
        for (double i = mHeaderSizePx - mStart * mZoomX; i < mWidth + mStart; i += mUnitX)
            if (i >= 0 && i <= mWidth)
                canvas.drawLine((float) i, 0, (float) i, (float) mHieght, paint);


        double pos1 = getXCanvasPercent(mStartPlayPositonPercent);//(mStartPlayPositonPercent) * (getRealWidth()) * mZoomX - mStart * mZoomX;
        double pos2 = getXCanvasPercent(mEndPlayPositionPercent);//(mEndPlayPositionPercent) * (getRealWidth()) * mZoomX - mStart * mZoomX;
        paint.setShader(mShaderLoop);
        if (pos1 < mHeaderSizePx)
            pos1 = mHeaderSizePx;
        canvas.drawRect((float) (pos1), mHeaderSizePx, (float) (pos2), (float) mHieght, paint);

        //draw midi


        for (ChannelItem channelItem : channelItemList) {
            ChannelItem.setPlay(channelItem, mPositionBeat);
            float left = mHeaderSizePx + (float) (((channelItem.mStart / mDurationBeats) * (getRealWidth()) * mZoomX) - mStart * mZoomX);
            float top = mHeaderSizePx + (float) ((((double) channelItem.mChannel / NUM_CHANNELS) * (getRealHieght()) * mZoomY) - mTop * mZoomY);
            float right = mHeaderSizePx + (float) (((channelItem.mEnd / mDurationBeats) * (getRealWidth()) * mZoomX) - mStart * mZoomX);
            float bottom = mHeaderSizePx + (float) (((((double) channelItem.mChannel + 1.0) / NUM_CHANNELS) * (getRealHieght()) * mZoomY) - mTop * mZoomY);
            canvas.drawRect(left, top, right, bottom, mChannelPaint[channelItem.mChannel]);
            canvas.drawRect(left, top, right, bottom, mMidiPaintBorder);


           /* double   midiStartPercent=((((midi.mStart/mDurationBeats)*(getRealWidth()-mHeaderSizePxx)))+mHeaderSizePxx);
            double   midiEndtPercent=((((midi.mEnd/mDurationBeats)*(getRealWidth()-mHeaderSizePxx)))+mHeaderSizePxx);
            canvas.drawRect((float) midiStartPercent, 0, (float) midiEndtPercent, (float) getRealHieght(), paint);*/

        }

        //draw header
        paint.setShader(mShaderHeader);
        canvas.drawRect(0, (float) 0, (float) mWidth, mHeaderSizePx, paint);

        //draw beat text
        double beat = 1;
        int iBeat = 0;
        double beatCount = (double) (mDurationBeats / numLines);
        paint.setTextSize(SizeCalc.pxFromDp(getContext(), 12));
        paint.setShader(mShaderWhite);
        int countSuBeat = 0;
        for (double i = mHeaderSizePx - mStart * mZoomX; i < mWidth + mStart; i += mUnitX) {
            if (i >= 0 && i <= mWidth) {

                iBeat = (int) beat;
                float beatPart = (float) (beat - (double) iBeat);
                if (beatPart == 0.0f) {
                    paint.setTextSize(SizeCalc.pxFromDp(getContext(), 12));
                    canvas.drawText(iBeat + "", (float) i, 40, paint);
                } else {
                    paint.setTextSize(SizeCalc.pxFromDp(getContext(), 8));
                    double text = (beatPart * 4.0);
                    if (((double) (int) text) - beatPart * 4.0 == 0.0)
                        canvas.drawText((int) text + "", (float) i, 40, paint);
                    else {
                        double text2 = (text - (int) text) * 4.0;

                        paint.setTextSize(SizeCalc.pxFromDp(getContext(), 6));
                        canvas.drawText((int) text2 + "", (float) i, 40, paint);
                    }


                }

            }
            beat += beatCount;
        }
        //draw header row
        paint.setShader(mShaderHeader);
        canvas.drawRect(0, (float) 0, (float) mHeaderSizePx, (float) mHieght, paint);
        //draw note text
        int ch = 1;

        paint.setTextSize(SizeCalc.pxFromDp(getContext(), 8));
        paint.setShader(mShaderWhite);
        for (double i = mHeaderSizePx - mTop * mZoomY; i < mHieght + mTop; i += mUnitY, index++) {
            if (i >= 0 - mUnitY && i <= mHieght) {
                canvas.drawText(String.valueOf(ch), 8, (float) i + 20, paint);
            }
            ++ch;
        }

        //draw rect corner left top
        paint.setShader(mShaderHeader);
        canvas.drawRect(0, (float) 0, mHeaderSizePx, mHeaderSizePx, paint);
        super.onDraw(canvas);
    }


    public void setStartEnd(double start, double end) {


        mStart = (float) (start * getRealWidth());
        mEnd = (float) (end * getRealWidth());

        mZoomXCenter = ((mEnd - mStart) / 2.0) + mStart;
        mZoomX = getRealWidth() / (mEnd - mStart);
        Log.e("zoom", "mZoomXCenter = " + mZoomXCenter + " mZoomX = " + mZoomX + " mStart = " + mStart + " mEnd = " + mEnd);
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


    public void setChannelItem(float[] channelItemsFloat) {

        for (int i = 0; i < channelItemsFloat.length; i += 4) {
            int channel = (int) channelItemsFloat[i + 2];

            ChannelItem channelItem = new ChannelItem(channelItemsFloat[i], (float) (channelItemsFloat[i + 1]), channel);
            channelItem.mIndex = (int) channelItemsFloat[i + 3];
            channelItemList.add(channelItem);

        }

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
        distance = mBottom - mTop;
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
        }


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

        mZoomYCenter = ((mBottom - mTop) / 2.0) + mTop;
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

   /* public double getPercentX(float x) {
        return mStart / getRealWidth() + (x / getRealWidth() * ((mEnd - mStart) / getRealWidth()));
    }*/

    public double getPercentY(float y) {
        return mTop / getRealHieght() + (y / getRealHieght() * ((mBottom - mTop) / getRealHieght()));
    }

    public void actionDrawDown(float x, float y) {

        double xPoint = getRealPercentX(x - mHeaderSizePx);
        double yPoint = getRealPercentY(y - mHeaderSizePx);

        setChannelItem(xPoint, yPoint);
    }


    public void setChannelItem(double xPoint, double yPoint) {


        //  double posBeat = (xPoint - Math.IEEEremainder(xPoint, mUnitX / (getRealWidth() * mZoomX))) * mDurationBeats;
        double posBeat = (xPoint - xPoint % (mUnitX / (getRealWidth() * mZoomX))) * mDurationBeats;

        // double key = (yPoint - Math.IEEEremainder(yPoint, mUnitY / (getRealHieght() * mZoomY))) * 24;
        double channel = (yPoint - yPoint % (mUnitY / (getRealHieght() * mZoomY))) * 16;

        //  Log.d("midi", "start = " + (posBeat) + " len = " + (float) (mUnitX / (getRealWidth() * mZoomX) * mDurationBeats));
        ChannelItem channelItem = new ChannelItem(posBeat, (float) (mUnitX / (getRealWidth() * mZoomX) * mDurationBeats), (int) (channel));
        Log.d("midi", "start = " + channelItem.mStart + " len = " + channelItem.mEnd + " key = " + ((-channelItem.mChannel + 12) - 1) + "xpoint = " + xPoint + "yPoint = " + yPoint);
        if (addOrRemoveChannelItem(channelItem)) {
            //  mDrumMap.setPitch(mIndexChannel, (-midi.mKey + 12) - 1);


        } else {

        }

    }

    public double getRealPercentX(float x) {
        return mStart / getRealWidth() + (x / getRealWidth() * ((mEnd - mStart) / getRealWidth()));
    }

    public double getRealPercentY(float y) {
        return mTop / getRealHieght() + (y / getRealHieght() * ((mBottom - mTop) / getRealHieght()));
    }

    private boolean addOrRemoveChannelItem(ChannelItem channelItem) {

        ChannelItem channelItemToRemove = null;
        boolean canAdd = true;
        for (ChannelItem ci : channelItemList) {
            if (ci.mChannel == channelItem.mChannel) {
                if (ci.mStart == channelItem.mStart) {
                    channelItemToRemove = ci;
                    canAdd = false;
                }
                if (channelItem.mStart < ci.mEnd && channelItem.mStart > ci.mStart) {
                    channelItemToRemove = ci;
                    canAdd = true;
                }
                if (channelItem.mEnd > ci.mStart && channelItem.mEnd <= ci.mEnd && ci.mStart != channelItem.mStart) {
                    channelItemToRemove = ci;
                    canAdd = true;
                }
            }

        }
        if (channelItemToRemove != null) {
            if (mChannelItemChangeListener != null)
                mChannelItemChangeListener.onChannelItemDelete(channelItemToRemove);
            channelItemList.remove(channelItemToRemove);
            mDrumMap.removeChannelItem(channelItemToRemove.mIndex);
        }
        if (canAdd) {

            channelItemList.add(channelItem);
            if (mChannelItemChangeListener != null)
                mChannelItemChangeListener.onChannelItemAdd(channelItem);
            channelItem.mIndex = mDrumMap.addChannelItem(channelItem.mChannel, channelItem.mStart, channelItem.mEnd);
            mChannelItemAdded = channelItem;
        }
        return canAdd;

    }

    public boolean updateEndChannelItemAdded(float x) {
        double xPoint = getRealPercentX(x - mHeaderSizePx);
        double posBeat = (xPoint - xPoint % (mUnitX / (getRealWidth() * mZoomX))) * mDurationBeats;
        if (mChannelItemAdded != null) {
            if (mChannelItemAdded.updateEnd(posBeat)) {
                Log.d("midi", "start = " + mChannelItemAdded.mStart + " len = " + mChannelItemAdded.mEnd + " key = " + mChannelItemAdded.mChannel + "xpoint = " + xPoint);
                if (mChannelItemChangeListener != null)
                    mChannelItemChangeListener.onChannelItemLenChange(mChannelItemAdded);
                mDrumMap.updateChannelAddedEnd(mChannelItemAdded.mEnd);
                return true;
            }
        }
        return false;
    }

    public void resetChannelItemAdded() {
        mChannelItemAdded = null;
        mDrumMap.resetChannelItemAdded();
    }

    public float getRemoveHeader() {
        return mHeaderSizePx;
    }

    public double getXCanvasPercent(double percentX) {
        return mHeaderSizePx + ((percentX) * (getRealWidth()) * mZoomX - mStart * mZoomX);
    }

    public void setStartEndPlayPositionPercent(double startPlayPosition, double stopPlayPosition) {
        mStartPlayPositonPercent = startPlayPosition;
        mEndPlayPositionPercent = stopPlayPosition;
    }

    public void setPositionStartOrEnd(double touchPosition, boolean isQuntize) {

        if (isQuntize)
            touchPosition = (touchPosition - touchPosition % (mUnitX / (getRealWidth() * mZoomX)));
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

    public void setPositionStartEnd(double start, double end, boolean isQuntize) {
        if (isQuntize) {
            start = (start - start % (mUnitX / (getRealWidth() * mZoomX)));
            end = (end - end % (mUnitX / (getRealWidth() * mZoomX)));
        }
        mStartPlayPositonPercent = start;
        mEndPlayPositionPercent = end;
    }

    public void setPositionBeat(float positionBeat) {
        mPositionBeat = positionBeat;
    }

    public boolean setCanvasPosition(float pos) {


        float add = ((float) (getRealWidth() / mZoomX)) / 2;

        float middle = pos * getRealWidth();
        if (middle - add >= 0 && middle + add <= getRealWidth()) {
            mStart = middle - add;

            mEnd = middle + add;
            return true;
        } else if (middle - add < 0) {

            mStart = 0;
            mEnd = add * 2;
            return true;
        } else if (middle + add > getRealWidth()) {
            mEnd = getRealWidth();
            mStart = mEnd - add * 2;
            return true;
        }

        return false;

    }

    public float getZoomX() {
        return (float) mZoomX;
    }
}
