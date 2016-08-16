package views.fragments;

import android.content.DialogInterface;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yossibarel.drummap.DrumMapJni;
import com.yossibarel.drummap.R;

import java.io.File;

import Utils.Const;
import Utils.DLog;
import Utils.Extras;
import Utils.TimeUtils;
import interfaces.DeleteFileListener;
import views.Custom.RangeBar;
import views.Custom.WaveFormEditView;
import views.dialogs.FileDialog;

/**
 * Created by yossibarel on 01/05/16.
 */
public class EditWaveFragment extends Fragment implements DrumMapJni.JniListener, View.OnTouchListener, RangeBar.OnRangeChangeListener, FileDialog.IOnFileSelected {

    private static final double MAX_ZOOM_Y = 8;
    private int mChannelIndex;
    private String mSourcePath;
    private DrumMapJni drumMapJni;
    private float[] mEditBuffer;
    private WaveFormEditView vWaveForm;
    private FrameLayout loEditWave;


    // we can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;
    private float dx = 0.0f;
    private RangeBar rbPrecent;
    private EditState mEditState;
    private long mDuration;
    private View vCursorPosition;
    private CheckBox cbLoop;
    private Button btnPlay;
    private File mFile;
    private TextView tvProcessMessage;
    private SeekBar mZoomYSb;

    @Override
    public void OnSelected(File file, int channelIndex) {
        mFile = file;
        drumMapJni.openChannelFile(mChannelIndex, file.getPath(), file.length());
        tvProcessMessage.setVisibility(View.VISIBLE);

    }

    @Override
    public void OnLongPressItem(final File file, final int channelIndex, View viewSelected, final DeleteFileListener listener) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getActivity(), viewSelected);

        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.menu_select_file, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.file_select:
                        OnSelected(file, channelIndex);
                        break;
                    case R.id.file_delete:
                        file.delete();
                        listener.onDeleteFile(file);
                        break;

                }
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    enum EditState {
        MOVE, SELECT, PLAY;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_wave, container, false);

        vWaveForm = (WaveFormEditView) view.findViewById(R.id.vWaveForm);
        loEditWave = (FrameLayout) view.findViewById(R.id.loEditWave);
        tvProcessMessage = (TextView) view.findViewById(R.id.tvProcessMessage);
        cbLoop = (CheckBox) view.findViewById(R.id.cbLoop);
        btnPlay = (Button) view.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drumMapJni.getIsPlay(mChannelIndex)) {
                    btnPlay.setText("Play");
                    drumMapJni.keyRelese(mChannelIndex);
                    drumMapJni.stopPlayChannel(mChannelIndex);
                } else {
                    btnPlay.setText("Pause");
                    drumMapJni.keyDown(mChannelIndex);
                    drumMapJni.playChannel(mChannelIndex);

                }

            }
        });
        vCursorPosition = view.findViewById(R.id.vCursorPosition);
        loEditWave.setOnTouchListener(this);
        drumMapJni = DrumMapJni.getInstance();
        drumMapJni.addJniListener(this);
        cbLoop.setChecked(drumMapJni.getIsLoop(mChannelIndex));
        cbLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drumMapJni.setLoop(mChannelIndex, cbLoop.isChecked());
            }
        });
        mChannelIndex = getArguments().getInt(Extras.EXTRA_CHANNEL_INDEX, -1);
        vWaveForm.setIndexChannel(mChannelIndex);
        mSourcePath = getArguments().getString(Extras.EXTRA_FILE_PATH);
        if (drumMapJni.getIsLoaded(mChannelIndex)) {
            tvProcessMessage.setVisibility(View.VISIBLE);
            File file = new File(mSourcePath);

            drumMapJni.openChannelFileForEdit(mChannelIndex, mSourcePath, file.length());
        } else {
            tvProcessMessage.setVisibility(View.GONE);
        }
        createRangBar(view);
        loEditWave.setOnTouchListener(this);
        mEditState = EditState.MOVE;
        RadioGroup rgState = (RadioGroup) view.findViewById(R.id.rgState);
        rgState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.rbMove:
                        mEditState = EditState.MOVE;
                        break;
                    case R.id.rbEdit:
                        mEditState = EditState.SELECT;
                        break;
                    case R.id.rbPlay:
                        mEditState = EditState.PLAY;
                        break;
                }
            }
        });
        Button btnOpenFile = (Button) view.findViewById(R.id.btnOpenFile);
        btnOpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileDialog fileDialog = new FileDialog(getActivity());
                fileDialog.SetOnFileSelected(EditWaveFragment.this);
                fileDialog.Show(Const.getAppFolder(getContext()), 0);
            }
        });
        mZoomYSb = (SeekBar) view.findViewById(R.id.sbZoomY);
        mZoomYSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    double zoomY = (mZoomYSb.getProgress() / 1000.0) * MAX_ZOOM_Y + 1.0;
                    vWaveForm.setZoomY(zoomY);
                    vWaveForm.invalidate();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        startThreadPosition();
        return view;
    }

    private void startThreadPosition() {
        new Thread() {
            @Override
            public void run() {
                do {
                    if (drumMapJni.getIsPlay(mChannelIndex)) {
                        vCursorPosition.post(new Runnable() {
                            @Override
                            public void run() {
                                float pos = (float) drumMapJni.getPositionPrecent(mChannelIndex);
                                rbPrecent.setCursorPresent(pos);

                                vCursorPosition.setTranslationX((float) vWaveForm.getXCanvasPercent(pos));
                            }
                        });
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } while (isAdded());
            }
        }.start();
    }

    private void createRangBar(View view) {

        rbPrecent = (RangeBar) view.findViewById(R.id.rbPrecent);
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

        rbPrecent.setOnRangeChangeListener(this);

    }

    @Override
    public void onEOF(int channel) {
        if (channel == mChannelIndex)
            if (!cbLoop.isChecked()) {
                btnPlay.post(new Runnable() {
                    @Override
                    public void run() {
                        btnPlay.setText("Play");
                    }
                });
            }
    }

    @Override
    public void onFileLoadSuccess(int channel) {
        mSourcePath = mFile.getPath();
        drumMapJni.openChannelFileForEdit(mChannelIndex, mSourcePath, mFile.length());

    }

    @Override
    public void onLoadFileError(int channel) {

    }

    @Override
    public void onBufferEditLoaded(final int sizeByte) {


        mEditBuffer = drumMapJni.getEditBuffer(sizeByte);
        vWaveForm.setBuffer(mEditBuffer);
        mDuration = drumMapJni.getDurationMs(mChannelIndex);

        float start = (float) (drumMapJni.getStartPlayPosition(mChannelIndex) / mDuration);
        float end = (float) (drumMapJni.getStopPlayPosition(mChannelIndex) / mDuration);
        vWaveForm.setDuration(mDuration);
        vWaveForm.setStartEnd(start, end);
        vWaveForm.setStartEndPlayPositionPercent(drumMapJni.getStartPlayPosition(mChannelIndex) / mDuration, drumMapJni.getStopPlayPosition(mChannelIndex) / mDuration);
        vWaveForm.post(new Runnable() {
            @Override
            public void run() {
                tvProcessMessage.setVisibility(View.GONE);
                rbPrecent.setProgress1(0);
                rbPrecent.setProgress2(1000);
                vWaveForm.invalidate();
            }
        });


    }

    @Override
    public void onDestroyView() {
        drumMapJni.removeJniListener(this);
        super.onDestroyView();
    }

    boolean isZoom = false;

    public boolean onTouch(View v, MotionEvent event) {

        if (mEditState == EditState.MOVE)
            return onMoveTouch(v, event);
        else if (mEditState == EditState.SELECT)
            return onEditToch(v, event);
        else if (mEditState == EditState.PLAY)
            return onPlayTouch(v, event);

        return false;
    }

    private boolean onPlayTouch(View v, MotionEvent event) {
        double positionPercent;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                if (x < vWaveForm.getRemoveHeader()) {
                    x = vWaveForm.getRemoveHeader();
                } else if (x > v.getWidth())
                    x = v.getWidth();
                positionPercent = vWaveForm.getRealPercentX(x - vWaveForm.getRemoveHeader());
                Log.d("play", positionPercent * mDuration + "");
                vCursorPosition.setTranslationX(x);
                drumMapJni.setPosition(mChannelIndex, positionPercent * mDuration);
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                if (x < vWaveForm.getRemoveHeader()) {
                    x = vWaveForm.getRemoveHeader();
                } else if (x > v.getWidth())
                    x = v.getWidth();
                positionPercent = vWaveForm.getRealPercentX(x - vWaveForm.getRemoveHeader());
                Log.d("play", positionPercent * mDuration + "");
                vCursorPosition.setTranslationX(x);
                drumMapJni.setPosition(mChannelIndex, positionPercent * mDuration);
                break;
        }
        return true;
    }


    private boolean onEditToch(View v, MotionEvent event) {

        double touchPosition = 0;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchPosition = validateValuePercent(vWaveForm.getRealPercentX(event.getX() - vWaveForm.getRemoveHeader()));
                vWaveForm.setPositionStartOrEnd(touchPosition);
                double s = vWaveForm.getStartPlayPositionPercent() * mDuration;
                double e = vWaveForm.getEndPlayPositionPercent() * mDuration;
                DLog.d("loop", "start =  " + s + " e = " + e);
                drumMapJni.loopBetween(mChannelIndex, vWaveForm.getStartPlayPositionPercent() * mDuration, vWaveForm.getEndPlayPositionPercent() * mDuration, drumMapJni.getIsPlay(mChannelIndex));

                Log.d("touch", "touch " + touchPosition);
                vWaveForm.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() < 2) {
                    touchPosition = validateValuePercent(vWaveForm.getRealPercentX(event.getX() - vWaveForm.getRemoveHeader()));
                    vWaveForm.setPositionStartOrEnd(touchPosition);
                    Log.d("touch", "touch " + touchPosition);
                } else {
                    double touchPosition1 = vWaveForm.getRealPercentX(event.getX(0) - vWaveForm.getRemoveHeader());
                    double touchPosition2 = vWaveForm.getRealPercentX(event.getX(1) - vWaveForm.getRemoveHeader());

                    touchPosition1 = validateValuePercent(touchPosition1);
                    touchPosition2 = validateValuePercent(touchPosition2);

                    if (touchPosition1 < touchPosition2) {

                        vWaveForm.setPositionStartEnd(touchPosition1, touchPosition2);

                    } else {
                        vWaveForm.setPositionStartEnd(touchPosition2, touchPosition1);
                    }


                }
                drumMapJni.loopBetween(mChannelIndex, vWaveForm.getStartPlayPositionPercent() * mDuration, vWaveForm.getEndPlayPositionPercent() * mDuration, drumMapJni.getIsPlay(mChannelIndex));


                vWaveForm.invalidate();
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

    boolean onMoveTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                vWaveForm.actionDragDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    vWaveForm.actionDragMove(event.getX(), event.getY());
                } else if (mode == ZOOM) {
                    vWaveForm.actionZoomMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                vWaveForm.actionZoomDown(event.getX(0), event.getY(0), event.getX(1), event.getY(1));

                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_UP:
                mode = NONE;
                break;


        }
        rbPrecent.setProgress1(vWaveForm.getStartPercent() * rbPrecent.getMax());
        rbPrecent.setProgress2(vWaveForm.getEndPercent() * rbPrecent.getMax());
        mZoomYSb.setProgress((int) ((vWaveForm.getZoomY() - 1.0) * 1000 / MAX_ZOOM_Y));
      /*  rbVertical.setProgress1(vWaveForm.getTopPercent() * rbVertical.getMax());
        rbVertical.setProgress2(vWaveForm.getBottomPercent() * rbVertical.getMax());*/
        vWaveForm.invalidate();
        return true;
    }

    @Override
    public void onRangeChange(double left, double right) {

        vWaveForm.setStartEnd(left / 1000.0, right / 1000.0);
        // vWaveForm.setEnd(right / 100.0);
        vWaveForm.invalidate();

    }

}
