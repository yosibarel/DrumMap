package com.yossibarel.drummap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;

import Utils.Extras;
import Utils.FileManager;
import Utils.TimeUtils;
import adapters.ChannelsAdapter;
import adapters.ScratchAdapter;
import effects.Effect;
import interfaces.DeleteFileListener;
import models.Channel;
import saves.MainSaver;
import views.Custom.CircleLayout;
import views.Custom.ViewChannel;
import views.Custom.ViewControlXY;
import views.Custom.ViewPitchBand;
import views.dialogs.FileDialog;
import views.dialogs.GlobalRecordDialog;
import views.dialogs.SaveFileDialog;

public class DrumMapActivity extends AppCompatActivity implements ChannelsAdapter.OnTapListener, ViewChannel.OnEditListener, MainSaver.SaveListener, FileDialog.IOnFileSelected {

    private static final int OVERLAY_PERMISSION_REQ_CODE = 132;
    public static int BUFFER_SIZE;
    public static int SCRATCH_WIDTH;
    public static int SCRATCH_HEIGHT;
    public static int WIDTH;
    public static int HEIGHT;
    private boolean isControlXYOpen = false;
    private ScratchAdapter mScratchAdapter;
    private DrumMapJni drunMap;
    private LinearLayout mllControlXYContainer;
    private LinearLayout mllControlXY;
    private LinearLayout mllControlScretchContainer;
    private LinearLayout mllControlScretch;
    private boolean isControlSrechOpen;
    private GridView mGwScrachEnable;
    private ViewPitchBand vPitchBand;
    ArrayList<Channel> mChannels;
    ArrayList<ViewChannel> mViewChannels;
    SensorControler mSensorControler;
    private static int BPM = 140;
    private Thread mThreadUpdatePosition;
    private boolean mIsRunning;
    private boolean mIsActive;
    private Thread threadViewMeter;
    private View mTransportView;
    private Float mSaveX;
    private float mSaveY;
    private TextView mBpmEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_drum_map);
        final Button btnFileOption = (Button) findViewById(R.id.btnFileOption);
        mBpmEt = (TextView) findViewById(R.id.etBpm);
        Typeface typeFace1 = Typeface.createFromAsset(getAssets(), "fonts/digital_counter_7.ttf");
        Typeface typeFace2 = Typeface.createFromAsset(getAssets(), "fonts/digital-7.ttf");
        mBpmEt.setTypeface(typeFace2);
        mBpmEt.setOnTouchListener(new View.OnTouchListener() {
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
                    mBpmEt.setText(BPM + "");
                    drunMap.setBpm(BPM);
                }
                return true;
            }
        });
        drunMap = DrumMapJni.getInstance();
        BUFFER_SIZE = 240;
        drunMap.drumMap(48000, BUFFER_SIZE, getFilesDir().getPath() + "/temp.mp3");
        drunMap.setBpm(BPM);

        mSensorControler = new SensorControler();
        mGwScrachEnable = (GridView) findViewById(R.id.gwScrachEnable);
        vPitchBand = (ViewPitchBand) findViewById(R.id.vPitch);
        mChannels = new ArrayList<>();
        mViewChannels = new ArrayList<>();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        for (int i = 0; i < 16; i++) {
            Channel channel = new Channel(i, drunMap);
            ViewChannel viewChannel = new ViewChannel(this, channel);
            viewChannel.setLayoutParams(lp);
            viewChannel.setOnEditChannelListener(this);
            mViewChannels.add(viewChannel);
            mChannels.add(channel);
        }

        // mChannelAdapter = new ChannelsAdapter(this, mChannels, drunMap);

        mScratchAdapter = new ScratchAdapter(this, mChannels);
        mGwScrachEnable.setAdapter(mScratchAdapter);

        createGrid();
        // gridViewEdit.setAdapter(mEditChannelAdapter);

        //mChannelAdapter.setOnTapListener(this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        WIDTH = metrics.widthPixels;
        HEIGHT = metrics.heightPixels - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56.0f, getResources().getDisplayMetrics());
        ViewTreeObserver vto = mGwScrachEnable.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mGwScrachEnable.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                SCRATCH_WIDTH = mGwScrachEnable.getMeasuredWidth();
                SCRATCH_HEIGHT = mGwScrachEnable.getMeasuredHeight();
                mScratchAdapter.notifyDataSetChanged();

            }
        });

        initControlXY();
        initSrechControl();
        final CheckBox cbSensor = (CheckBox) findViewById(R.id.cbSensor);
        cbSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cbSensor.isChecked()) {
                    mSensorControler.create(DrumMapActivity.this, mSensorConrolerListener);
                } else {
                    mSensorControler.destroy();
                }
            }
        });
        vPitchBand.setOnPichChangeListener(new ViewPitchBand.OnPichChangeListener() {
            @Override
            public void OnPichChange(double pich) {
                drunMap.setPitchBand(pich);
                Log.e("pitch", pich + "");
            }
        });

        final ToggleButton btnGlobalRec = (ToggleButton) findViewById(R.id.btnRecPiano);
        btnGlobalRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnGlobalRec.isChecked())
                    drunMap.startGlobalRecord(App.getAppDir() + "/" + TimeUtils.getFileTimeString() + ".wav");
                else {
                    drunMap.stopRecord();
                    GlobalRecordDialog.showDialog(drunMap, 0, DrumMapActivity.this, new FileDialog.IOnFileSelected() {
                        @Override
                        public void OnSelected(File file, int channelIndex) {

                        }

                        @Override
                        public void OnLongPressItem(File file, int channelIndex, View viewSelected, DeleteFileListener listener) {

                        }
                    });

                }

            }
        });
        mIsRunning = true;
        final TextView tvPosition = (TextView) findViewById(R.id.tvPosition);
        tvPosition.setTypeface(typeFace1);
        mThreadUpdatePosition = new Thread(new Runnable() {


            @Override
            public void run() {
                do {
                    tvPosition.post(new Runnable() {
                        @Override
                        public void run() {
                            tvPosition.setText(TimeUtils.getStringPosition(drunMap.getMainPosition()));
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (mIsRunning);
            }
        });
        final TextView tvQuantize = (TextView) findViewById(R.id.tvQuantize);
        tvQuantize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(DrumMapActivity.this, tvQuantize);

                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_quantize, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {


                        tvQuantize.setText(item.getTitle());
                        switch (item.getItemId()) {
                            case R.id.one:
                                drunMap.setMainQuantize(0);
                                break;
                            case R.id.two:
                                drunMap.setMainQuantize(1);
                                break;
                            case R.id.three:
                                drunMap.setMainQuantize(2);
                                break;
                            case R.id.four:
                                drunMap.setMainQuantize(3);
                                break;
                            case R.id.five:
                                drunMap.setMainQuantize(4);
                                break;
                            case R.id.six:
                                drunMap.setMainQuantize(5);
                                break;
                            case R.id.seven:
                                drunMap.setMainQuantize(6);
                                break;
                            case R.id.hight:
                                drunMap.setMainQuantize(7);
                                break;

                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
        btnFileOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(DrumMapActivity.this, btnFileOption);

                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_file, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {


                        switch (item.getItemId()) {
                            case R.id.file_new:

                                break;
                            case R.id.file_open:
                                FileDialog fileDialog = new FileDialog(DrumMapActivity.this);
                                fileDialog.SetOnFileSelected(DrumMapActivity.this);
                                fileDialog.Show(FileManager.getChannelSaveDir().toString(), 0);
                                break;
                            case R.id.file_save:
                                SaveFileDialog.show(DrumMapActivity.this, FileManager.TypeFile.CHANNEL, DrumMapActivity.this);
                                break;
                            case R.id.file_restore:

                                break;


                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
        final CheckBox cbQuantize = (CheckBox) findViewById(R.id.cbQuantize);
        cbQuantize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drunMap.setQuantizeEnable(cbQuantize.isChecked());
            }
        });
        findViewById(R.id.btnMixer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mixerActivity = new Intent(DrumMapActivity.this, MixerActivity.class);
                startActivity(mixerActivity);
            }
        });
        findViewById(R.id.btnFx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendFxActivity = new Intent(DrumMapActivity.this, SendFxActivity.class);
                startActivity(sendFxActivity);
            }
        });
        findViewById(R.id.btnMainGrid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mixerActivity = new Intent(DrumMapActivity.this, GridChannelActivity.class);
                startActivity(mixerActivity);
            }
        });

        createPianoTransport();
        mThreadUpdatePosition.start();
        checkViewOverView();
    }

    public void checkViewOverView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }

        }

    }

    private void createGrid() {
        LinearLayout lChannel1 = (LinearLayout) findViewById(R.id.lChannel1);
        LinearLayout lChannel2 = (LinearLayout) findViewById(R.id.lChannel2);
        LinearLayout lChannel3 = (LinearLayout) findViewById(R.id.lChannel3);
        LinearLayout lChannel4 = (LinearLayout) findViewById(R.id.lChannel4);
        int i = 0;
        for (; i < 4; i++)
            lChannel1.addView(mViewChannels.get(i));
        for (; i < 8; i++)
            lChannel2.addView(mViewChannels.get(i));
        for (; i < 12; i++)
            lChannel3.addView(mViewChannels.get(i));
        for (; i < 16; i++)
            lChannel4.addView(mViewChannels.get(i));
    }

    SensorControler.SensorConrolerListener mSensorConrolerListener = new SensorControler.SensorConrolerListener() {
        @Override
        public void onSensorChange(double x, double y, double z) {
            drunMap.setControlXYValue(Effect.SNSRCX, x);
            drunMap.setControlXYValue(Effect.SNSRCY, y);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        update();

        mIsActive = true;
        threadViewMeter = new Thread(new Runnable() {
            @Override
            public void run() {
                do {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isFinishing() || !mIsActive)
                                return;
                            float[] vmParam = drunMap.getAllViewMeters();
                            for (int i = 0; i < 16; i++) {
                                mViewChannels.get(i).updateViewMeter(vmParam[i * 2]);
                                Log.d("viewMeter", "L =" + vmParam[i * 2] + " R = " + vmParam[i * 2 + 1]);
                            }
                        }
                    });


                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (mIsActive);
            }
        });

        threadViewMeter.start();

    }

    private void update() {
        BPM = drunMap.getBpm();

        mBpmEt.setText(String.valueOf(BPM));
        boolean isSolo = false;
        for (int i = 0; i < 16; i++) {
            mViewChannels.get(i).update();
            isSolo = isSolo || mViewChannels.get(i).getIsSolo();
        }
        if (isSolo)
            onSoloChannel(null, true);
    }

    @Override
    protected void onStop() {
        mIsActive = false;
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        onWantToClose();
    }

    void onWantToClose() {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Exit").setMessage("Yow want to close app").setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create();
        dialog.show();

    }


    private void initSrechControl() {

        Button btnControlScretch = (Button) findViewById(R.id.btnControlScretch);
        mllControlScretchContainer = (LinearLayout) findViewById(R.id.llControlScretchContainer);
        mllControlScretch = (LinearLayout) findViewById(R.id.llControlScretch);

        btnControlScretch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isControlSrechOpen)
                    closeContolStrech();
                else
                    openControlStrech();
                isControlSrechOpen = !isControlSrechOpen;
            }
        });
    }

    private void openControlStrech() {
        mllControlScretch.setVisibility(View.VISIBLE);
        mllControlScretch.animate().alpha(1.0f).setDuration(300);
        mllControlScretchContainer.animate().translationX(0).setDuration(300);
    }

    private void closeContolStrech() {
        mllControlScretch.animate().alpha(0).setDuration(300);
        mllControlScretchContainer.animate().translationX((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -200.0f, getResources().getDisplayMetrics())).setDuration(300).withEndAction(runEndCloseControlScrech);

    }

    private void initControlXY() {

        ViewControlXY vControlXY1 = (ViewControlXY) findViewById(R.id.vControlXY1);
        ViewControlXY vControlXY2 = (ViewControlXY) findViewById(R.id.vControlXY2);
        vControlXY1.setOnValueChangeLitener(new ViewControlXY.OnValueChangeListener() {
            @Override
            public void onValueChange(double x, double y) {
                drunMap.setControlXYValue(Effect.C1X, x);
                drunMap.setControlXYValue(Effect.C1Y, y);
            }
        });
        vControlXY2.setOnValueChangeLitener(new ViewControlXY.OnValueChangeListener() {
            @Override
            public void onValueChange(double x, double y) {
                drunMap.setControlXYValue(Effect.C2X, x);
                drunMap.setControlXYValue(Effect.C2Y, y);

            }
        });
        Button btnControlXY = (Button) findViewById(R.id.btnControlXY);
        mllControlXYContainer = (LinearLayout) findViewById(R.id.llControlXYContainer);
        mllControlXY = (LinearLayout) findViewById(R.id.llControlXY);

        btnControlXY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isControlXYOpen)
                    closeContolXY();
                else
                    openControlXY();
                isControlXYOpen = !isControlXYOpen;
            }
        });
        CircleLayout vScretch = (CircleLayout) findViewById(R.id.vScretch);
        vScretch.setOnValueChangeListener(new CircleLayout.OnValueChangeListener() {
            @Override
            public void onValueChange(double value) {
                Log.d("scratch", value + "");
                drunMap.scretch(value);
            }
        });
    }

    private void openControlXY() {
        mllControlXY.setVisibility(View.VISIBLE);
        mllControlXY.animate().alpha(1.0f).setDuration(300);
        mllControlXYContainer.animate().translationX(0).setDuration(300);
    }

    private void closeContolXY() {
        mllControlXY.animate().alpha(0).setDuration(300);
        mllControlXYContainer.animate().translationX((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160.0f, getResources().getDisplayMetrics())).setDuration(300).withEndAction(runEndCloseControlXY);
    }

    Runnable runEndCloseControlXY = new Runnable() {
        @Override
        public void run() {
            mllControlXY.setVisibility(View.GONE);
        }
    };
    Runnable runEndCloseControlScrech = new Runnable() {
        @Override
        public void run() {
            mllControlScretch.setVisibility(View.GONE);
        }
    };

    @Override
    public void onTap(Channel channel) {
        drunMap.playChannel(channel.getIndex());
    }

    @Override
    public void onStopTap(Channel channel) {
        drunMap.stopPlayChannel(channel.getIndex());
    }

    @Override
    public void onEdit(Channel channel) {
        Intent activity = new Intent(DrumMapActivity.this, EditChannelActivity.class);
        activity.putExtra(Extras.EXTRA_CHANNEL_INDEX, channel.getIndex());
        startActivity(activity);

    }

    @Override
    public void onMuteChannel(Channel mChannel, boolean mIsMuted) {
        for (ViewChannel mv : mViewChannels) {

            mv.updateMute();
        }
    }

    @Override
    public void onSoloChannel(Channel mChannel, boolean isSolo) {
        for (ViewChannel mv : mViewChannels) {
            mv.updateSolo(isSolo);
        }
    }

    public static void setBpm(float bpm) {

        BPM = (int) bpm;
        DrumMapJni.getInstance().setBpm(bpm);
    }

    public static float getBpm() {

        return BPM;
    }

    @Override
    public void onSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DrumMapActivity.this, "Save success", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onFailed(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DrumMapActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void OnSelected(File file, int channelIndex) {
        FileManager.loadFile(file, new MainSaver.LoadListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        update();
                        Toast.makeText(DrumMapActivity.this, "Load success", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailed(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DrumMapActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public void OnLongPressItem(final File file, final int channelIndex, View viewSelected, final DeleteFileListener listener) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(DrumMapActivity.this, viewSelected);

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
                    mTransportView.setVisibility(View.VISIBLE);
                    mTransportView.animate().alpha(1.0f).setDuration(250);
                } else {
                    mTransportView.animate().alpha(0.0f).setDuration(250).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mTransportView.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
        mTransportView = findViewById(R.id.vTransport);
        findViewById(R.id.vDrag).setOnTouchListener(new View.OnTouchListener() {
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

    private View.OnClickListener mTransportClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnPlayPiano:
                    drunMap.playMainGrid();
                    break;
                case R.id.btnPausePiano:
                    drunMap.pauseMainGrid();
                    break;
                case R.id.btnRecPiano:
                    break;
            }
        }
    };
}
