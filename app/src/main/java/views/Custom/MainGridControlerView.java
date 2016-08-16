package views.Custom;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yossibarel.drummap.DrumMapActivity;
import com.yossibarel.drummap.DrumMapJni;
import com.yossibarel.drummap.R;

import Utils.DLog;
import Utils.TimeUtils;
import models.ChannelItem;

/**
 * Created by yossibarel on 05/06/16.
 */
public class MainGridControlerView extends LinearLayout implements RangeBar.OnRangeChangeListener, MainGridView.ChannelItemChangeListener {

    private MainGridView mMainGrid;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private OnClosePianoListener mOnClosePianoListener;
    float mDuration = 128;

    private RangeBar rbPrecent;
    private int BPM;
    private DrumMapJni mDrumMap;

    private Float mSaveX;
    private Float mSaveY;
    private OnClickListener mTransportClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnPlayPiano:
                    mDrumMap.playMainGrid();
                    break;
                case R.id.btnPausePiano:
                    mDrumMap.pauseMainGrid();
                    break;
                case R.id.btnRecPiano:
                    break;
            }
        }
    };
    private View vCursorPosition;
    private boolean mIsViewOpen;
    private RangeBarVertical rbVertical;
    private boolean mIsQuntize = true;
    private TextView mEtBeatDuration;
    private View mTransportView;
    private TextView tvPosition;
    private boolean mIsGridCursorFollowPosition;

    private void startThreadPosition() {
        mIsViewOpen = true;
        new Thread() {
            @Override
            public void run() {
                do {

                    vCursorPosition.post(new Runnable() {
                        @Override
                        public void run() {
                            float positionBeat = (float) mDrumMap.getMainPositionBeat();
                            float pos = positionBeat / mDuration;
                            mMainGrid.setPositionBeat(positionBeat);
                            rbPrecent.setCursorPresent(pos);

                            vCursorPosition.setTranslationX((float) mMainGrid.getXCanvasPercent(pos));

                            if (mIsGridCursorFollowPosition)
                                if (mDrumMap.getIsMainGridPlay() && mMainGrid.setCanvasPosition(pos)) {
                                    mMainGrid.invalidate();
                                    rbPrecent.setProgress1(mMainGrid.getStartPercent() * 1000.0);
                                    rbPrecent.setProgress2(mMainGrid.getEndPercent() * 1000.0);

                                }
                            tvPosition.setText(TimeUtils.getStringPosition(mDrumMap.getMainPosition()));
                        }
                    });

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } while (mIsViewOpen);
            }
        }.start();
    }

    public void startLoopThread() {
        startThreadPosition();
    }

    public void stopLoopThread() {
        mIsViewOpen = false;
    }

    @Override
    public void onChannelItemAdd(ChannelItem channelItem) {
        if (channelItem.mEnd >= mDuration) {
            updateDurationMultiple2();
        }
    }

    @Override
    public void onChannelItemDelete(ChannelItem channelItem) {

    }

    @Override
    public void onChannelItemLenChange(ChannelItem channelItem) {
        if (channelItem.mEnd >= mDuration) {
            updateDurationMultiple2();
        }
    }

    private void updateDurationMultiple2() {
        mDuration *= 2;
        mMainGrid.notifyStartAnimationCanvasDuration();
        mEtBeatDuration.setText((int) mDuration + "");
        animateCanvasDuration();
        // mPiano.setDuration(mDuration);

    }

    Handler mHandlerAnimationCnavasDuration = new Handler();
    Runnable mRunnableAnimationCnavasDuration = new Runnable() {
        @Override
        public void run() {
            if (mMainGrid.getDurationBeat() < mDuration) {
                mMainGrid.incrementAnimationDuration();
                mHandlerAnimationCnavasDuration.postDelayed(mRunnableAnimationCnavasDuration, 50);
            } else
                mMainGrid.setDuration(mDuration);
            mMainGrid.invalidate();

        }


    };

    private void animateCanvasDuration() {

        mHandlerAnimationCnavasDuration.postDelayed(mRunnableAnimationCnavasDuration, 50);
        mDrumMap.setMainGridBeatsDuration(mDuration);
        mMainGrid.invalidate();
    }

    enum EditState {
        MOVE, DRAW, PLAY, LOOP;
    }

    public interface OnClosePianoListener {
        void onClosePiano();

        void onOpenPiano();
    }

    public void setOnCloseListener(OnClosePianoListener l) {
        mOnClosePianoListener = l;
    }

    public boolean isZoom;
    EditState mEditState;

    public void setBpm(int bpm) {
        BPM = bpm;
        mMainGrid.setBpm(bpm);
        DrumMapActivity.setBpm(bpm);
    }

    public MainGridControlerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {

        mDrumMap = DrumMapJni.getInstance();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.main_grid_view_controler, this, true);
        vCursorPosition = findViewById(R.id.vCursorPosition);
        mMainGrid = (MainGridView) view.findViewById(R.id.vMainGridView);
        mMainGrid.setStartEndPlayPositionPercent(mDrumMap.getMainGridStartPlayPosition() / mDuration, mDrumMap.getMainGridStopPlayPosition() / mDuration);
        mMainGrid.setChannelItemChangeListener(this);
        BPM = (int) DrumMapActivity.getBpm();
        mMainGrid.setBpm(BPM);
        mMainGrid.setOnTouchListener(mPianoTouchListener);
        mEditState = EditState.MOVE;
        mSaveX = mSaveY = null;


        RadioGroup rgState = (RadioGroup) findViewById(R.id.rgState);
        rgState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbDrag:
                        mEditState = EditState.MOVE;
                        break;
                    case R.id.rbDraw:
                        mEditState = EditState.DRAW;
                        break;
                    case R.id.rbPlay:
                        mEditState = EditState.PLAY;
                        break;
                    case R.id.rbLoop:
                        mEditState = EditState.LOOP;
                        break;
                }
            }
        });
        final TextView etBpm = (TextView) findViewById(R.id.etBpm);

        Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/digital-7.ttf");
        etBpm.setTypeface(typeFace);
        BPM = mDrumMap.getBpm();
        etBpm.setText(String.valueOf(BPM));
        etBpm.setOnTouchListener(new View.OnTouchListener() {
            int base = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    base = BPM;

                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    int res = base + (int) (event.getX() / 8);
                    if (res > 240)
                        res = 240;
                    else if (res < 60)
                        res = 60;
                    BPM = res;
                    etBpm.setText(BPM + "");
                    mDrumMap.setBpm(BPM);
                }
                return true;
            }
        });
        mEtBeatDuration = (TextView) findViewById(R.id.etBeatDuration);

        mEtBeatDuration.setTypeface(typeFace);

        mEtBeatDuration.setOnTouchListener(new View.OnTouchListener() {
            int base = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    base = (int) mDuration;

                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    int res = base + (int) (event.getX() / 8);

                    if (res < 4)
                        res = 4;
                    mDuration = res;
                    mEtBeatDuration.setText((int) mDuration + "");
                    mMainGrid.setDuration(mDuration);
                    mDrumMap.setMainGridBeatsDuration(mDuration);
                    mMainGrid.invalidate();
                }
                return true;
            }
        });
        createRangBar();
        createRangBarVertical();

        createPianoTransport(view);
        mDuration = (float) mDrumMap.getMainDurationBeat();
        mMainGrid.setDuration(mDuration);
        mEtBeatDuration.setText(String.valueOf(mDuration));
        createChannelItemsFromJni();

        mIsGridCursorFollowPosition = mDrumMap.getIsGridCursorFollowPosition();
        CheckBox cbFollow = (CheckBox) findViewById(R.id.cbFollow);
        cbFollow.setChecked(mIsGridCursorFollowPosition);
        cbFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsGridCursorFollowPosition = isChecked;
                mDrumMap.setIsGridCursorFollowPosition(isChecked);
            }
        });
        tvPosition = (TextView) findViewById(R.id.tvPosition);
        Typeface typeFace1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/digital_counter_7.ttf");
        tvPosition.setTypeface(typeFace1);

    }

    private void createChannelItemsFromJni() {
        float[] channelItemFloat = mDrumMap.getChannelItemsFloat();
        mMainGrid.setChannelItem(channelItemFloat);
    }

    private void createPianoTransport(View view) {
        Button btnPlayPiano = (Button) findViewById(R.id.btnPlayPiano);
        Button btnPausePiano = (Button) findViewById(R.id.btnPausePiano);
        Button btnRecPiano = (Button) findViewById(R.id.btnRecPiano);
        btnPlayPiano.setOnClickListener(mTransportClickListener);
        btnPausePiano.setOnClickListener(mTransportClickListener);
        CheckBox cbTransport = (CheckBox) view.findViewById(R.id.cbTransport);
        cbTransport.setChecked(true);
        cbTransport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mSaveX != null) {
                        mTransportView.setX(mSaveX);
                        mTransportView.setY(mSaveY);
                    }
                    mTransportView.setVisibility(VISIBLE);
                    mTransportView.animate().alpha(1.0f).setDuration(250);
                } else {
                    mTransportView.animate().alpha(0.0f).setDuration(250).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mTransportView.setVisibility(GONE);
                        }
                    });
                }
            }
        });
        mTransportView = findViewById(R.id.vTransport);
        findViewById(R.id.vDrag).setOnTouchListener(new OnTouchListener() {
            float dX, dY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        dX = mTransportView.getX() - event.getRawX();
                        dY = mTransportView.getY() - event.getRawY();
                        if (mSaveX == null) {
                            mSaveX = mTransportView.getX();
                            mSaveY = mTransportView.getY();
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:

                        mTransportView.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

    }

    private void createRangBarVertical() {
        rbVertical = (RangeBarVertical) findViewById(R.id.rbVertical);
        rbVertical.setMax(1000);


        rbVertical.setFormater(new RangeBarVertical.ValueTopBottomFormatter() {
            @Override
            public String getTop(double value) {
                return String.valueOf((int) (value / 10));
            }

            @Override
            public String getBottom(double value) {
                return String.valueOf((int) (value / 10));
            }
        });
        rbVertical.setProgress1(0);
        rbVertical.setProgress2(1000);
        rbVertical.setOnRangeChangeListener(new RangeBarVertical.OnRangeChangeListener() {
            @Override
            public void onRangeChange(double top, double bottom) {
                mMainGrid.setTopBottom(top / 1000.0, bottom / 1000.0);
                // vWaveForm.setEnd(right / 100.0);
                mMainGrid.invalidate();

            }
        });
    }

    private void createRangBar() {

        rbPrecent = (RangeBar) findViewById(R.id.rbPrecent);
        rbPrecent.setMax(1000);


        rbPrecent.setFormater(new RangeBar.ValueLeftRightFormatter() {
            @Override
            public String getLeft(double value) {
                return TimeUtils.getStringPosition((value / 1000.0) * mDuration);
            }

            @Override
            public String getRight(double value) {
                return TimeUtils.getStringPosition((value / 1000.0) * mDuration);
            }
        });
        rbPrecent.setProgress1(0);
        rbPrecent.setProgress2(1000);
        rbPrecent.setOnRangeChangeListener(this);

    }

    public void openPiano() {
        setClickable(true);
        mIsViewOpen = true;
        animate().scaleY(1).scaleX(1).setDuration(250).withEndAction(new Runnable() {
            @Override
            public void run() {
                rbVertical.updateSize();
                rbPrecent.updateSize();
                startThreadPosition();

            }
        });
        if (mOnClosePianoListener != null)
            mOnClosePianoListener.onOpenPiano();
    }

    OnTouchListener mPianoTouchListener = new OnTouchListener() {


        @Override
        public boolean onTouch(View v, MotionEvent event) {

            boolean retVal = false;
            Log.d("event", "x = " + event.getX() + " w = " + v.getWidth());
            switch (mEditState) {
                case MOVE:
                    retVal = onMoveTouch(v, event);
                    break;
                case DRAW:
                    retVal = onDrawToch(v, event);
                    break;
                case PLAY:
                    retVal = onPlayToch(v, event);
                    break;
                case LOOP:
                    retVal = onLoopToch(v, event);
                    break;
            }
            return retVal;
        }
    };

    private boolean onPlayToch(View v, MotionEvent event) {

        double positionPercent;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                if (x < mMainGrid.getRemoveHeader()) {
                    x = mMainGrid.getRemoveHeader();
                } else if (x > v.getWidth())
                    x = v.getWidth();
                positionPercent = mMainGrid.getRealPercentX(x - mMainGrid.getRemoveHeader());
                Log.d("play", positionPercent * mDuration + "");
                vCursorPosition.setTranslationX(x);

                mDrumMap.setMainGridPositionBeat(positionPercent * mDuration);
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                if (x < mMainGrid.getRemoveHeader()) {
                    x = mMainGrid.getRemoveHeader();
                } else if (x > v.getWidth())
                    x = v.getWidth();
                positionPercent = mMainGrid.getRealPercentX(x - mMainGrid.getRemoveHeader());
                Log.d("play", positionPercent * mDuration + "");
                vCursorPosition.setTranslationX(x);
                mDrumMap.setMainGridPositionBeat(positionPercent * mDuration);
                break;
        }
        return true;
    }

    private boolean onDrawToch(View v, MotionEvent event) {


        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mMainGrid.actionDrawDown(event.getX(), event.getY());
                mMainGrid.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                if (mMainGrid.updateEndChannelItemAdded(event.getX()))
                    mMainGrid.invalidate();
                break;
            case MotionEvent.ACTION_UP:

                mMainGrid.resetChannelItemAdded();
                break;


        }
        return true;
    }

    boolean onMoveTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                mMainGrid.actionDragDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    mMainGrid.actionDragMove(event.getX(), event.getY());
                } else if (mode == ZOOM) {
                    mMainGrid.actionZoomMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                mMainGrid.actionZoomDown(event.getX(0), event.getY(0), event.getX(1), event.getY(1));

                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_UP:
                mode = NONE;
                break;


        }
        rbPrecent.setProgress1(mMainGrid.getStartPercent() * rbPrecent.getMax());
        rbPrecent.setProgress2(mMainGrid.getEndPercent() * rbPrecent.getMax());
        rbVertical.setProgress1(mMainGrid.getTopPercent() * rbVertical.getMax());
        rbVertical.setProgress2(mMainGrid.getBottomPercent() * rbVertical.getMax());
        mMainGrid.invalidate();
        return true;
    }

    private boolean onLoopToch(View v, MotionEvent event) {

        double touchPosition = 0;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchPosition = validateValuePercent(mMainGrid.getRealPercentX(event.getX() - mMainGrid.getRemoveHeader()));
                mMainGrid.setPositionStartOrEnd(touchPosition, mIsQuntize);
                double s = mMainGrid.getStartPlayPositionPercent() * mDuration;
                double e = mMainGrid.getEndPlayPositionPercent() * mDuration;
                DLog.d("loop", "start =  " + s + " e = " + e);
                mDrumMap.setStartEndLoopPositionPrecentMainGrid(mMainGrid.getStartPlayPositionPercent(), mMainGrid.getEndPlayPositionPercent());

                Log.d("touch", "touch " + touchPosition);
                mMainGrid.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() < 2) {
                    touchPosition = validateValuePercent(mMainGrid.getRealPercentX(event.getX() - mMainGrid.getRemoveHeader()));
                    mMainGrid.setPositionStartOrEnd(touchPosition, mIsQuntize);
                    Log.d("touch", "touch " + touchPosition);
                } else {
                    double touchPosition1 = mMainGrid.getRealPercentX(event.getX(0) - mMainGrid.getRemoveHeader());
                    double touchPosition2 = mMainGrid.getRealPercentX(event.getX(1) - mMainGrid.getRemoveHeader());

                    touchPosition1 = validateValuePercent(touchPosition1);
                    touchPosition2 = validateValuePercent(touchPosition2);

                    if (touchPosition1 < touchPosition2) {

                        mMainGrid.setPositionStartEnd(touchPosition1, touchPosition2, mIsQuntize);

                    } else {
                        mMainGrid.setPositionStartEnd(touchPosition2, touchPosition1, mIsQuntize);
                    }


                }
                mDrumMap.setStartEndLoopPositionPrecentMainGrid(mMainGrid.getStartPlayPositionPercent(), mMainGrid.getEndPlayPositionPercent());


                mMainGrid.invalidate();
                break;


        }
        return true;
    }

    private double validateValuePercent(double value) {
        if (value < 0.0)
            return 0;
        if (value > 1.0)
            return 1.0;
        return value;
    }

    @Override
    public void onRangeChange(double left, double right) {

        mMainGrid.setStartEnd(left / 1000.0, right / 1000.0);
        // vWaveForm.setEnd(right / 100.0);
        mMainGrid.invalidate();

    }

}
