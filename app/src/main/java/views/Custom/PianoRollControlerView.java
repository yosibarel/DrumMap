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
import Utils.SizeCalc;
import Utils.TimeUtils;
import models.Midi;

/**
 * Created by yossibarel on 08/05/16.
 */
public class PianoRollControlerView extends LinearLayout implements RangeBar.OnRangeChangeListener, PianoRollView.MidiChangeListener {

    private OnClickListener mBtnMidiOptionListener;

    public enum SelectOptionMidiState {
        NONE, SELECT, COPY, CUT, DELETE, SHIFT_LEFT, SHIFT_RIGHT
    }

    enum EditState {
        MOVE, DRAW, PLAY, LOOP, SELECT;
    }

    public interface OnClosePianoListener {
        void onClosePiano();

        void onOpenPiano();
    }

    private SelectOptionMidiState mSelectOptionMidiState;
    private PianoRollView mPiano;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private OnClosePianoListener mOnClosePianoListener;
    float mDuration = 4;
    private int mIndexChannel;
    private RangeBar rbPrecent;
    private int BPM;
    private DrumMapJni mDrumMap;
    private OnClickListener mTransportClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnPlayPiano:
                    mDrumMap.playPiano(mIndexChannel);
                    break;
                case R.id.btnPausePiano:
                    mDrumMap.pausePiano(mIndexChannel);
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
    private boolean mIsPianoCursorFollowPosition;
    private View mTransportView;
    private Float mSaveX;
    private Float mSaveY;
    private View mViewMidiOption;
    private boolean mIsMidiOptionOpen;

    private void startThreadPosition() {
        new Thread() {
            @Override
            public void run() {
                do {

                    vCursorPosition.post(new Runnable() {
                        @Override
                        public void run() {
                            float positionBeat = (float) mDrumMap.getPianoPositionBeat(mIndexChannel);
                            float pos = positionBeat / mDuration;
                            mPiano.setPositionBeat(positionBeat);
                            rbPrecent.setCursorPresent(pos);

                            vCursorPosition.setTranslationX((float) mPiano.getXCanvasPercent(pos));
                            boolean isPlayPiano = mDrumMap.getIsPianoPlay(mIndexChannel);
                            if (mIsPianoCursorFollowPosition) {
                                if (isPlayPiano && mPiano.setCanvasPosition(pos)) {

                                    rbPrecent.setProgress1(mPiano.getStartPercent() * 1000.0);
                                    rbPrecent.setProgress2(mPiano.getEndPercent() * 1000.0);

                                }
                            }
                            mPiano.setIsPlay(isPlayPiano);
                            if (isPlayPiano)
                                mPiano.invalidate();

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

    public void setChannel(int channel) {
        mIndexChannel = channel;

        mPiano.setIndexChannel(mIndexChannel);
        mDuration = mDrumMap.getPianoDurationBeat(mIndexChannel);
        mPiano.setStartEndPlayPositionPercent(mDrumMap.getPianoStartPlayPosition(mIndexChannel) / mDuration, mDrumMap.getPianoStopPlayPosition(mIndexChannel) / mDuration);

        mPiano.setDuration(mDuration);
        mPiano.setIsPlay(mDrumMap.getIsPianoPlay(mIndexChannel));
        mEtBeatDuration.setText(String.valueOf(mDuration));
        createMidiFromJni();
    }


    private void updateDurationMultiple2() {
        mDuration *= 2;
        mPiano.notifyStartAnimationCanvasDuration();
        mEtBeatDuration.setText((int) mDuration + "");
        animateCanvasDuration();
        // mPiano.setDuration(mDuration);

    }

    @Override
    public void onMidiAdd(Midi midi) {
        if (midi.mEnd >= mDuration) {
            updateDurationMultiple2();
        }
    }

    @Override
    public void onMidiDelete(Midi midi) {

    }

    @Override
    public void onMidiLenChange(Midi midi) {

        if (midi.mEnd >= mDuration) {
            updateDurationMultiple2();
        }
    }

    Handler mHandlerAnimationCnavasDuration = new Handler();

    Runnable mRunnableAnimationCnavasDuration = new Runnable() {
        @Override
        public void run() {
            if (mPiano.getDurationBeat() < mDuration) {
                mPiano.incrementAnimationDuration();
                mHandlerAnimationCnavasDuration.postDelayed(mRunnableAnimationCnavasDuration, 50);
            } else
                mPiano.setDuration(mDuration);
            mPiano.invalidate();

        }


    };

    private void animateCanvasDuration() {

        mHandlerAnimationCnavasDuration.postDelayed(mRunnableAnimationCnavasDuration, 50);
        mDrumMap.setPianoBeatsDuration(mIndexChannel, mDuration);
        mPiano.invalidate();
    }


    public void setOnCloseListener(OnClosePianoListener l) {
        mOnClosePianoListener = l;
    }

    public boolean isZoom;
    EditState mEditState;

    public void setBpm(int bpm) {
        BPM = bpm;
        mPiano.setBpm(bpm);
        DrumMapActivity.setBpm(bpm);
    }

    public PianoRollControlerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {

        mDrumMap = DrumMapJni.getInstance();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.piano_roll_view_controler, this, true);
        vCursorPosition = findViewById(R.id.vCursorPosition);
        mPiano = (PianoRollView) view.findViewById(R.id.pianoRollView);
        mPiano.setMidiChangeListener(this);
        BPM = (int) DrumMapActivity.getBpm();
        mPiano.setBpm(BPM);
        mPiano.setOnTouchListener(mPianoTouchListener);
        mEditState = EditState.MOVE;
        Button btnClose = (Button) findViewById(R.id.btnClosePiano);
        setScaleY(0);
        setScaleX(0);

        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAnimation();

            }
        });
        RadioGroup rgState = (RadioGroup) findViewById(R.id.rgState);
        rgState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbDrag:
                        mSelectOptionMidiState = SelectOptionMidiState.NONE;
                        mEditState = EditState.MOVE;
                        closeMidiOption();
                        mPiano.setMidiOptionSelectState(SelectOptionMidiState.NONE);
                        break;
                    case R.id.rbDraw:
                        mSelectOptionMidiState = SelectOptionMidiState.NONE;
                        mEditState = EditState.DRAW;
                        closeMidiOption();
                        mPiano.setMidiOptionSelectState(SelectOptionMidiState.NONE);
                        break;
                    case R.id.rbPlay:
                        mSelectOptionMidiState = SelectOptionMidiState.NONE;
                        mEditState = EditState.PLAY;
                        closeMidiOption();
                        mPiano.setMidiOptionSelectState(SelectOptionMidiState.NONE);
                        break;
                    case R.id.rbLoop:
                        mSelectOptionMidiState = SelectOptionMidiState.NONE;
                        mEditState = EditState.LOOP;
                        closeMidiOption();
                        mPiano.setMidiOptionSelectState(SelectOptionMidiState.NONE);
                        break;
                    case R.id.rbSelect:
                        mSelectOptionMidiState = SelectOptionMidiState.SELECT;
                        mPiano.setMidiOptionSelectState(SelectOptionMidiState.SELECT);
                        openMidiOption();
                        mEditState = EditState.SELECT;
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
                    mPiano.setBpm(BPM);
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
                  /*  int res = base + (int) (event.getX() / 8);

                    if (res < 4)
                        res = 4;
                    mDuration = res;
                    mEtBeatDuration.setText((int) mDuration + "");
                    mPiano.setDuration(mDuration);
                    mDrumMap.setPianoBeatsDuration(mIndexChannel, mDuration);
                    mPiano.invalidate();*/
                }
                return true;
            }
        });

        mIsPianoCursorFollowPosition = mDrumMap.getIsPianoCursorFollowPosition(mIndexChannel);
        CheckBox cbFollow = (CheckBox) findViewById(R.id.cbFollow);
        cbFollow.setChecked(mIsPianoCursorFollowPosition);
        cbFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsPianoCursorFollowPosition = isChecked;
                mDrumMap.setIsPianoCursorFollowPosition(mIndexChannel, isChecked);
            }
        });
        createRangBar();
        createRangBarVertical();

        createPianoTransport();
        createMidiOption();
    }

    private void openMidiOption() {
        if (!mIsMidiOptionOpen) {
            mIsMidiOptionOpen = true;
            mViewMidiOption.animate().translationX(0).setDuration(200);
        }
    }

    private void closeMidiOption() {
        if (mIsMidiOptionOpen) {
            mIsMidiOptionOpen = false;
            mViewMidiOption.animate().translationX(SizeCalc.pxFromDp(getContext(), 40)).setDuration(200);
        }
    }

    private void createMidiOption() {
        mBtnMidiOptionListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.btnMidiCopy:
                        mSelectOptionMidiState = SelectOptionMidiState.COPY;
                        mPiano.copySelectedMidi();
                        break;
                    case R.id.btnMidiCut:
                        mSelectOptionMidiState = SelectOptionMidiState.CUT;
                        break;
                    case R.id.btnMidiDelete:
                        mSelectOptionMidiState = SelectOptionMidiState.DELETE;
                        mPiano.deleteSelectedMidi();
                        mPiano.invalidate();
                        break;
                    case R.id.btnMidiShiftLeft:
                        mPiano.shiftLeftMidi();
                        mPiano.invalidate();
                        mSelectOptionMidiState = SelectOptionMidiState.SHIFT_LEFT;
                        break;
                    case R.id.btnMidiShiftRight:
                        mPiano.shiftRightMidi();

                        mSelectOptionMidiState = SelectOptionMidiState.SHIFT_RIGHT;

                        break;
                    case R.id.btnMidiSelect:
                        mPiano.resetSelected();

                        mSelectOptionMidiState = SelectOptionMidiState.SELECT;

                        break;
                }
                mPiano.invalidate();
                mPiano.setMidiOptionSelectState(mSelectOptionMidiState);
            }
        };
        mIsMidiOptionOpen = false;
        mViewMidiOption = findViewById(R.id.vMidiOption);
        mViewMidiOption.setTranslationX(SizeCalc.pxFromDp(getContext(), 40));
        Button btnCopyMidiOption = (Button) findViewById(R.id.btnMidiCopy);
        Button btnCutMidiOption = (Button) findViewById(R.id.btnMidiCut);
        Button btnDeleteMidiOption = (Button) findViewById(R.id.btnMidiDelete);
        Button btnShiftLeftMidiOption = (Button) findViewById(R.id.btnMidiShiftLeft);
        Button btnShiftRightMidiOption = (Button) findViewById(R.id.btnMidiShiftRight);
        Button btnSelectMidiOption = (Button) findViewById(R.id.btnMidiSelect);
        btnCopyMidiOption.setOnClickListener(mBtnMidiOptionListener);
        btnCutMidiOption.setOnClickListener(mBtnMidiOptionListener);
        btnDeleteMidiOption.setOnClickListener(mBtnMidiOptionListener);
        btnShiftLeftMidiOption.setOnClickListener(mBtnMidiOptionListener);
        btnShiftRightMidiOption.setOnClickListener(mBtnMidiOptionListener);
        btnSelectMidiOption.setOnClickListener(mBtnMidiOptionListener);

    }

    public boolean isOpen() {
        return mIsViewOpen;
    }

    public void closeAnimation() {
        setClickable(false);
        mIsViewOpen = false;
        animate().scaleY(0).scaleX(0).setDuration(250).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (mOnClosePianoListener != null)
                    mOnClosePianoListener.onClosePiano();
            }
        });
    }


    private void createMidiFromJni() {
        float[] midiFloat = mDrumMap.getMidiFloat(mIndexChannel);
        mPiano.setMidi(midiFloat);
    }

    private void createPianoTransport() {
        Button btnPlayPiano = (Button) findViewById(R.id.btnPlayPiano);
        Button btnPausePiano = (Button) findViewById(R.id.btnPausePiano);
        Button btnRecPiano = (Button) findViewById(R.id.btnRecPiano);
        btnPlayPiano.setOnClickListener(mTransportClickListener);
        btnPausePiano.setOnClickListener(mTransportClickListener);
        CheckBox cbTransport = (CheckBox) findViewById(R.id.cbTransport);
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
        findViewById(R.id.tvPosition).setVisibility(GONE);


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
                mPiano.setTopBottom(top / 1000.0, bottom / 1000.0);
                // vWaveForm.setEnd(right / 100.0);
                mPiano.invalidate();

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
                case SELECT:
                    retVal = onSelectToch(v, event);
                    break;
            }
            return retVal;
        }
    };

    private boolean onSelectToch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mSelectOptionMidiState == SelectOptionMidiState.SELECT) {
                    float touchPositionX = (float) validateValuePercent(mPiano.getRealPercentX(event.getX() - mPiano.getRemoveHeader()));
                    float touchPositionY = (float) validateValuePercent(mPiano.getRealPercentY(event.getY() - mPiano.getRemoveHeader()));
                    mPiano.setStartPointSelectedPercent(touchPositionX, touchPositionY);

                } else {
                    mPiano.setCutCopyDown(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mSelectOptionMidiState == SelectOptionMidiState.SELECT) {
                    float touchPositionX = (float) validateValuePercent(mPiano.getRealPercentX(event.getX() - mPiano.getRemoveHeader()));
                    float touchPositionY = (float) validateValuePercent(mPiano.getRealPercentY(event.getY() - mPiano.getRemoveHeader()));
                    mPiano.setEndPointSelectedPercent(touchPositionX, touchPositionY);
                } else {
                    mPiano.setCutCopyMove(event.getX(), event.getY());
                }
                mPiano.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mSelectOptionMidiState != SelectOptionMidiState.SELECT) {
                    mPiano.setCutCopyUp(event.getX(), event.getY());
                    mPiano.setMidiOptionSelectState(SelectOptionMidiState.CUT);
                }
                mPiano.invalidate();
                break;

        }


        return true;
    }

    private boolean onPlayToch(View v, MotionEvent event) {

        double positionPercent;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                if (x < mPiano.getRemoveHeader()) {
                    x = mPiano.getRemoveHeader();
                } else if (x > v.getWidth())
                    x = v.getWidth();
                positionPercent = mPiano.getRealPercentX(x - mPiano.getRemoveHeader());
                Log.d("play", positionPercent * mDuration + "");
                vCursorPosition.setTranslationX(x);

                mDrumMap.setPianoPositionBeat(mIndexChannel, positionPercent * mDuration);
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                if (x < mPiano.getRemoveHeader()) {
                    x = mPiano.getRemoveHeader();
                } else if (x > v.getWidth())
                    x = v.getWidth();
                positionPercent = mPiano.getRealPercentX(x - mPiano.getRemoveHeader());
                Log.d("play", positionPercent * mDuration + "");
                vCursorPosition.setTranslationX(x);
                mDrumMap.setPianoPositionBeat(mIndexChannel, positionPercent * mDuration);
                break;
        }
        return true;
    }

    private boolean onDrawToch(View v, MotionEvent event) {


        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mPiano.actionDrawDown(event.getX(), event.getY());
                mPiano.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                if (mPiano.updateEndMidiAdded(event.getX()))
                    mPiano.invalidate();
                break;
            case MotionEvent.ACTION_UP:

                mPiano.resetMidiAdded();
                break;


        }
        return true;
    }

    boolean onMoveTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                mPiano.actionDragDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    mPiano.actionDragMove(event.getX(), event.getY());
                } else if (mode == ZOOM) {
                    mPiano.actionZoomMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                mPiano.actionZoomDown(event.getX(0), event.getY(0), event.getX(1), event.getY(1));

                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_UP:
                mode = NONE;
                break;


        }
        rbPrecent.setProgress1(mPiano.getStartPercent() * rbPrecent.getMax());
        rbPrecent.setProgress2(mPiano.getEndPercent() * rbPrecent.getMax());
        rbVertical.setProgress1(mPiano.getTopPercent() * rbVertical.getMax());
        rbVertical.setProgress2(mPiano.getBottomPercent() * rbVertical.getMax());
        mPiano.invalidate();
        return true;
    }

    private boolean onLoopToch(View v, MotionEvent event) {

        double touchPosition = 0;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchPosition = validateValuePercent(mPiano.getRealPercentX(event.getX() - mPiano.getRemoveHeader()));
                mPiano.setPositionStartOrEnd(touchPosition, mIsQuntize);
                double s = mPiano.getStartPlayPositionPercent() * mDuration;
                double e = mPiano.getEndPlayPositionPercent() * mDuration;
                DLog.d("loop", "start =  " + s + " e = " + e);
                mDrumMap.setStartEndLoopPositionPrecentPiano(mIndexChannel, mPiano.getStartPlayPositionPercent(), mPiano.getEndPlayPositionPercent());

                Log.d("touch", "touch " + touchPosition);
                mPiano.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() < 2) {
                    touchPosition = validateValuePercent(mPiano.getRealPercentX(event.getX() - mPiano.getRemoveHeader()));
                    mPiano.setPositionStartOrEnd(touchPosition, mIsQuntize);
                    Log.d("touch", "touch " + touchPosition);
                } else {
                    double touchPosition1 = mPiano.getRealPercentX(event.getX(0) - mPiano.getRemoveHeader());
                    double touchPosition2 = mPiano.getRealPercentX(event.getX(1) - mPiano.getRemoveHeader());

                    touchPosition1 = validateValuePercent(touchPosition1);
                    touchPosition2 = validateValuePercent(touchPosition2);

                    if (touchPosition1 < touchPosition2) {

                        mPiano.setPositionStartEnd(touchPosition1, touchPosition2, mIsQuntize);

                    } else {
                        mPiano.setPositionStartEnd(touchPosition2, touchPosition1, mIsQuntize);
                    }


                }
                mDrumMap.setStartEndLoopPositionPrecentPiano(mIndexChannel, mPiano.getStartPlayPositionPercent(), mPiano.getEndPlayPositionPercent());


                mPiano.invalidate();
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

        mPiano.setStartEnd(left / 1000.0, right / 1000.0);
        // vWaveForm.setEnd(right / 100.0);
        mPiano.invalidate();

    }
}
