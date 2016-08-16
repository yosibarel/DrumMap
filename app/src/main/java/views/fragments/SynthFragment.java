package views.fragments;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yossibarel.drummap.DrumMapActivity;
import com.yossibarel.drummap.DrumMapJni;
import com.yossibarel.drummap.R;

import java.io.File;
import java.nio.charset.Charset;

import Utils.Const;
import Utils.Extras;
import Utils.ResizeAnimation;
import Utils.SizeCalc;
import Utils.TimeUtils;
import effects.Effect;
import effects.FxFactory;
import effects.FxPitch;
import effects.FxStereo;
import interfaces.CloseAnimationListener;
import interfaces.DeleteFileListener;
import interfaces.AnimtionListener;
import views.Custom.LinearLayoutTouched;
import views.Custom.PianoRollControlerView;
import views.Custom.RangeBar;
import views.Custom.ViewEffectParam;
import views.Custom.ViewMeter;
import views.Custom.WaveFormRTView;
import views.Custom.pianoview.Piano;
import views.dialogs.FileDialog;
import views.dialogs.ModDialog;
import views.dialogs.RecordDialog;
import yuku.ambilwarna.AmbilWarnaDialog;


/**
 * Created by yossibarel on 03/04/16.
 */
public class SynthFragment extends Fragment implements FileDialog.IOnFileSelected, DrumMapJni.JniListener, AnimtionListener {


    private int mIndexChannel;
    private DrumMapJni drumMap;
    private RangeBar mRbPosition;
    private long mDuration;

    private File mFile;
    private LinearLayout mFilterContainer;
    private OnChannelSelectedListener mOnChannelSelected;
    private View.OnClickListener onChannelSelected = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


           /* Intent activity = new Intent(getContext(), EditChannelActivity.class);
            activity.putExtra(Extras.EXTRA_CHANNEL_INDEX, (Integer) v.getTag());
            startActivity(activity);
            getActivity().finish();
            getActivity().getIntent().putExtra(Extras.EXTRA_CHANNEL_INDEX, (Integer) v.getTag());
            getChildFragmentManager().beginTransaction().*/
            mOnChannelSelected.onChannelSelected((Integer) v.getTag());

        }
    };
    private FxStereo mStereoFx;
    private Button btnSndPan;
    private Button btnSendRight;
    private TextView tvPitch;
    private FxPitch mPitchFx;
    private double mPitch;
    private Button btnSendPitch;

    private ViewMeter vmLeft;
    private ViewMeter vmRight;
    private CloseAnimationListener mCurrentOpenLayout = null;

    private int mWFCanvasH;
    private int mWFCanvasW;
    private WaveFormRTView vWaveForm;
    private float[] mSilencebuffer;
    private Button mBtnEditWave;
    private OnEditWaveSelectedListener mOnEditWaveSelectedListener;
    private RelativeLayout mPianoRollLo;
    private PianoRollControlerView vPianoControler;

    public void setOnEditWaveSelectedListener(OnEditWaveSelectedListener listener) {
        mOnEditWaveSelectedListener = listener;
    }

    public boolean onBackPress() {
        if (vPianoControler.isOpen()) {

            vPianoControler.closeAnimation();
            return true;
        }
        return false;
    }


    public interface OnEditWaveSelectedListener {
        void onEditWaveSelectedListener(String filePath, int indexChannel);
    }

    public interface OnChannelSelectedListener {
        void onChannelSelected(int channel);
    }

    public void setOnChannelSelectedListener(OnChannelSelectedListener l) {
        mOnChannelSelected = l;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_synth_file, container, false);
        mIndexChannel = getArguments().getInt(Extras.EXTRA_CHANNEL_INDEX, -1);
        Button btnSelectFile = (Button) view.findViewById(R.id.btnSelectFile);
        Button btnRec = (Button) view.findViewById(R.id.btnRec);

        mFilterContainer = (LinearLayout) view.findViewById(R.id.loFilterValue);


        mRbPosition = (RangeBar) view.findViewById(R.id.rbPosition);
        mRbPosition.setMax(1000f);
      /*  mRbPosition.setProgress1(0);
        mRbPosition.setProgress2(1000);*/
        mRbPosition.setFormater(new RangeBar.ValueLeftRightFormatter() {
            @Override
            public String getLeft(double value) {

                return TimeUtils.getStringPosition((value / 1000.0) * mDuration);
            }

            @Override
            public String getRight(double value) {
                return TimeUtils.getStringPosition((value / 1000.0) * mDuration);
            }
        });
        drumMap = DrumMapJni.getInstance();
        SeekBar sbVol = (SeekBar) view.findViewById(R.id.sbVol);
        sbVol.setProgress((int) (drumMap.getVolume(mIndexChannel) * 100.0));
        final TextView tvVol = (TextView) view.findViewById(R.id.tvVol);
        sbVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvVol.setText(progress + "");
                drumMap.setVolume(mIndexChannel, progress / 100.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tvVol.setText(sbVol.getProgress() + "");
        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileDialog fileDialog = new FileDialog(getActivity());
                fileDialog.SetOnFileSelected(SynthFragment.this);
                fileDialog.Show(Const.getAppFolder(getContext()), 0);
            }
        });
        final CheckBox cbStopPlayOnRelese = (CheckBox) view.findViewById(R.id.cbStopPlayOnRelese);
        cbStopPlayOnRelese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drumMap.setStopPlayOnRelease(mIndexChannel, cbStopPlayOnRelese.isChecked());
            }
        });

        Piano vPiano = (Piano) view.findViewById(R.id.vPiano);

        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordDialog.showDialog(drumMap, mIndexChannel, getActivity(), SynthFragment.this);

            }
        });


        if (drumMap.getIsLoaded(mIndexChannel)) {
            mDuration = drumMap.getDurationMs(mIndexChannel);
            mRbPosition.setProgress1((float) (drumMap.getStartPlayPosition(mIndexChannel) / mDuration) * 1000.0);
            mRbPosition.setProgress2((float) (drumMap.getStopPlayPosition(mIndexChannel) / mDuration) * 1000.0);

        }
        mRbPosition.setOnRangeChangeListener(new RangeBar.OnRangeChangeListener() {
            @Override
            public void onRangeChange(double left, double right) {
                drumMap.loopBetween(mIndexChannel, (left / 1000.0) * mDuration, (right / 1000.0) * mDuration, true);
            }
        });


        createControlFilter(view);
        final CheckBox cbReverse = (CheckBox) view.findViewById(R.id.cbReverse);
        cbReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drumMap.setReverse(mIndexChannel, cbReverse.isChecked());
            }
        });
        vPiano.setPianoKeyListener(new Piano.PianoKeyListener() {
            @Override
            public void keyPressed(int id, int action) {
                if (action == 0) {
                    drumMap.setPitch(mIndexChannel, id - 12);
                    drumMap.keyDown(mIndexChannel);
                    lastPitchId = id;

                } else if (lastPitchId == id) {
                    drumMap.keyRelese(mIndexChannel);
                    //  drumMap.setPitch(mIndexChannel, 0);
                }

            }
        });
        final CheckBox cbLoop = (CheckBox) view.findViewById(R.id.cbLoop);
        cbLoop.setChecked(drumMap.getIsLoop(mIndexChannel));
        final CheckBox cbPlayFromStartLoop = (CheckBox) view.findViewById(R.id.cbPlayFromStartLoop);
        cbLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                drumMap.setLoop(mIndexChannel, isChecked);
            }
        });
        cbPlayFromStartLoop.setChecked(drumMap.getPlayFromStartLoop(mIndexChannel));
        cbPlayFromStartLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                drumMap.setPlayFromStartLoop(mIndexChannel, isChecked);
            }
        });
        final CheckBox cbFilterEnable = (CheckBox) view.findViewById(R.id.cbFilterEnable);

        cbFilterEnable.setChecked((int) drumMap.getFxValue(mIndexChannel, drumMap.getCurrentFilter(mIndexChannel), Effect.FILTER_ENABLE) == 1);
        cbFilterEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                drumMap.enableFilter(mIndexChannel, cbFilterEnable.isChecked());
            }
        });


        SeekBar sbEnvA = (SeekBar) view.findViewById(R.id.sbEnvA);
        SeekBar sbEnvD = (SeekBar) view.findViewById(R.id.sbEnvD);
        SeekBar sbEnvS = (SeekBar) view.findViewById(R.id.sbEnvS);
        SeekBar sbEnvR = (SeekBar) view.findViewById(R.id.sbEnvR);
        sbEnvA.setProgress((int) (drumMap.getEnvADSR(0, mIndexChannel, 0) * 100));
        sbEnvD.setProgress((int) (drumMap.getEnvADSR(0, mIndexChannel, 1) * 100));
        sbEnvS.setProgress((int) (drumMap.getEnvADSR(0, mIndexChannel, 2) * 100));
        sbEnvR.setProgress((int) (drumMap.getEnvADSR(0, mIndexChannel, 3) * 100));


        sbEnvA.setOnSeekBarChangeListener(sbADSRListener);
        sbEnvD.setOnSeekBarChangeListener(sbADSRListener);
        sbEnvS.setOnSeekBarChangeListener(sbADSRListener);
        sbEnvR.setOnSeekBarChangeListener(sbADSRListener);

        LinearLayout[] llBtn = new LinearLayout[4];
        llBtn[0] = (LinearLayout) view.findViewById(R.id.llBtn1);
        llBtn[1] = (LinearLayout) view.findViewById(R.id.llBtn2);
        llBtn[2] = (LinearLayout) view.findViewById(R.id.llBtn3);
        llBtn[3] = (LinearLayout) view.findViewById(R.id.llBtn4);
        for (int i = 0; i < 16; i++) {
            Button btn = new Button(getContext());
            //btn.setDuplicateParentStateEnabled(true);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.weight = 1;
            int margin = (int) -SizeCalc.pxFromDp(getContext(), 2);
            lp.setMargins(margin, margin, margin, margin);
            btn.setLayoutParams(lp);
            btn.setText((i + 1) + "");
            btn.setGravity(Gravity.CENTER);
            btn.setTag(i);
            btn.setTextSize(8);
            btn.setMinimumWidth(0);
            btn.setMinimumHeight(0);
            //


            if (mIndexChannel == i) {
                btn.setBackgroundResource(R.drawable.drw_mod_button);
                btn.setPadding(3, 3, 3, 3);

            } else
                btn.setOnClickListener(onChannelSelected);
            llBtn[i / 4].addView(btn);

        }

        final SeekBar sbStereo = (SeekBar) view.findViewById(R.id.sbStereo);
        sbStereo.setProgress(drumMap.getStereoProgress(mIndexChannel));
        sbStereo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    double left;
                    double right;
                    if (progress > 100) {

                        left = (double) (100 - (progress - 100)) / 100;
                        right = 1.0;
                    } else {
                        left = 1.0;
                        right = (double) progress / 100;
                    }
                    // mStereoFx.setFx(0, (float) progress / 200);
                    drumMap.setStereo(mIndexChannel, left, right, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (progress < 120 && progress > 80) {

                    drumMap.setStereo(mIndexChannel, 1.0, 1.0, 100);
                    seekBar.setProgress(100);
                }

            }
        });
        btnSndPan = (Button) view.findViewById(R.id.btnSndPan);

        mStereoFx = new FxStereo(drumMap, mIndexChannel);
        final int modIndex = mStereoFx.getIsInModeState(0);
        if (modIndex != -1)
            btnSndPan.setBackgroundResource(R.drawable.drw_mod_button);


        btnSndPan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ModDialog dialog = new ModDialog();
                dialog.show(getActivity(), mStereoFx, 0, new ModDialog.ModDialogListener() {
                    @Override
                    public void onSelectMod() {
                        int modIndex = mStereoFx.getIsInModeState(0);
                        if (modIndex != -1) {
                            sbStereo.setEnabled(false);
                            btnSndPan.setBackgroundResource(R.drawable.drw_mod_button);
                        } else {
                            sbStereo.setEnabled(true);
                            btnSndPan.setBackgroundResource(R.drawable.drw_mod_button_normal);

                        }
                    }
                }, mStereoFx.getIsInModeState(0));

            }
        });

        btnSendPitch = (Button) view.findViewById(R.id.btnSendPitch);

        mPitchFx = new FxPitch(drumMap, mIndexChannel);
        final int modIndexPitch = mPitchFx.getIsInModeState(0);
        if (modIndexPitch != -1) {

            btnSendPitch.setBackgroundResource(R.drawable.drw_mod_button);
        }
        btnSendPitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModDialog dialog = new ModDialog();
                dialog.show(getActivity(), mPitchFx, 0, new ModDialog.ModDialogListener() {
                    @Override
                    public void onSelectMod() {
                        int modIndex = mPitchFx.getIsInModeState(0);
                        if (modIndex != -1)
                            btnSendPitch.setBackgroundResource(R.drawable.drw_mod_button);
                        else
                            btnSendPitch.setBackgroundResource(R.drawable.drw_mod_button_normal);
                    }
                }, mPitchFx.getIsInModeState(0));
            }
        });

        mPitch = drumMap.getMainChannelPitch(mIndexChannel);
        tvPitch = (TextView) view.findViewById(R.id.tvPitch);
        Typeface typeFace2 = Typeface.createFromAsset(getContext().getAssets(), "fonts/digital-7.ttf");
        tvPitch.setTypeface(typeFace2);
        Button btnPitchUp = (Button) view.findViewById(R.id.btnPitchUp);
        Button btnPitchDown = (Button) view.findViewById(R.id.btnPitchDown);
        tvPitch.setText(mPitch + "");
        btnPitchDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPitch--;
                drumMap.setMainChannelPitch(mIndexChannel, mPitch);
                tvPitch.setText(mPitch + "");
            }
        });
        btnPitchUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPitch++;
                drumMap.setMainChannelPitch(mIndexChannel, mPitch);
                tvPitch.setText(mPitch + "");
            }
        });
        vmLeft = (ViewMeter) view.findViewById(R.id.vmLeft);
        vmRight = (ViewMeter) view.findViewById(R.id.vmRight);
        createResizeUiManager(view);
        createWaveform(view);

        mBtnEditWave = (Button) view.findViewById(R.id.btnEditWave);
        mBtnEditWave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] fileData = drumMap.getFilePath(mIndexChannel);
                String filePath = null;
                if (fileData != null) {

                    filePath = new String(fileData, Charset.forName("UTF-8"));
                }
                if (mOnEditWaveSelectedListener != null)
                    mOnEditWaveSelectedListener.onEditWaveSelectedListener(filePath, mIndexChannel);


              /*  } else {
                    Toast.makeText(getActivity(), "No selected audio file.", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
        view.findViewById(R.id.btnColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                AlertDialog dialog = new AmbilWarnaDialog(getActivity(), drumMap.getChannelColor(mIndexChannel), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {

                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        drumMap.setChannelColor(mIndexChannel, color);
                    }
                }).getDialog();
                dialog.show();
                Button btnCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button btnOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnCancel.setText("Cancel");
                btnOk.setText("Ok");

            }
        });


        return view;
    }


    private void createWaveform(View view) {

        mSilencebuffer = new float[DrumMapActivity.BUFFER_SIZE];
        vWaveForm = (WaveFormRTView) view.findViewById(R.id.vWaveForm);

    }


    SeekBar.OnSeekBarChangeListener sbADSRListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int type = 0;
            switch (seekBar.getId()) {

                case R.id.sbEnvA:
                    type = 0;
                    break;
                case R.id.sbEnvD:
                    type = 1;
                    break;
                case R.id.sbEnvS:
                    type = 2;
                    break;
                case R.id.sbEnvR:
                    type = 3;
                    break;
            }
            drumMap.setEnvADSR(0, mIndexChannel, type, (double) (progress + 1) / 101.0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private int lastPitchId = -1;
    Thread threadPosition;

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    private void createControlFilter(View view) {
        RadioGroup gbFilters = (RadioGroup) view.findViewById(R.id.gbFilters);
        int currentFilter = drumMap.getCurrentFilter(mIndexChannel);
        for (int i = 0; i < gbFilters.getChildCount(); i++) {
            RadioButton rb = (RadioButton) gbFilters.getChildAt(i);
            rb.setText(Effect.FILTER_NAME[i]);
            rb.setTag(i);
            if (i + 10 == currentFilter) {
                rb.setChecked(true);
                createFilterView(currentFilter);
            }

        }
        gbFilters.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int indexFilter = (int) group.findViewById(group.getCheckedRadioButtonId()).getTag() + 10;
                changeFilterType(indexFilter);
            }
        });

    }

    void changeFilterType(int indexFilter) {

        if (drumMap.addEffect(mIndexChannel, indexFilter)) {

            createFilterView(indexFilter);
        }
    }

    void createFilterView(int indexFilter) {
        mFilterContainer.removeAllViews();
        Effect filter = FxFactory.createEffect(mIndexChannel, drumMap, indexFilter);
        for (int i = 0; i < filter.getNumParams(); i++) {
            ViewEffectParam fxParam = new ViewEffectParam(getActivity(), filter, i);
            mFilterContainer.addView(fxParam);
        }
        float dps = mFilterContainer.getChildAt(0).getLayoutParams().width * filter.getNumParams();
        float pxs = dps * getActivity().getResources().getDisplayMetrics().density;
        mFilterContainer.getLayoutParams().width = (int) pxs;
    }

    @Override
    public void OnSelected(File file, int channelIndex) {

        mFile = file;
        drumMap.openChannelFile(mIndexChannel, file.getPath(), file.length());
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

    @Override
    public void onStart() {
        threadPosition = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isAdded()) {

                    final float[] buffer = drumMap.getWaveform(mIndexChannel, DrumMapActivity.BUFFER_SIZE);
                    try {
                        mRbPosition.post(new Runnable() {
                            @Override
                            public void run() {
                                mRbPosition.setCursorPresent(drumMap.getPositionPrecent(mIndexChannel));

                                vmLeft.setValue(drumMap.getViewMeterChannelLeft(mIndexChannel));
                                vmRight.setValue(drumMap.getViewMeterChannelRight(mIndexChannel));
                                if (buffer != null)
                                    vWaveForm.setBuffer(buffer);
                                else
                                    vWaveForm.setBuffer(mSilencebuffer);
                                vWaveForm.invalidate();
                                vmLeft.invalidate();
                                vmRight.invalidate();
                            }
                        });


                        Thread.sleep(50);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        threadPosition.start();
        super.onStart();
        drumMap.addJniListener(SynthFragment.this);
    }

    @Override
    public void onStop() {
        if (!threadPosition.isInterrupted())
            threadPosition.interrupt();
        super.onStop();
        drumMap.removeJniListener(SynthFragment.this);
    }

    @Override
    public void onEOF(int channel) {

    }

    @Override
    public void onFileLoadSuccess(int channel) {
        if (channel == mIndexChannel) {
            mDuration = drumMap.getDurationMs(mIndexChannel);
            mRbPosition.post(new Runnable() {
                @Override
                public void run() {
                    mRbPosition.setProgress1((float) (drumMap.getStartPlayPosition(mIndexChannel) / mDuration) * 1000.0);
                    mRbPosition.setProgress2((float) (drumMap.getStopPlayPosition(mIndexChannel) / mDuration) * 1000.0);
                }
            });

        }
    }

    @Override
    public void onLoadFileError(int channel) {

    }

    @Override
    public void onBufferEditLoaded(int channel) {

    }

    ResizeAnimation resizeAnimationOpenAdsr;
    ResizeAnimation resizeAnimationCloseAdsr;

    private void createResizeUiManager(View view) {


        LinearLayoutTouched loChannels = (LinearLayoutTouched) view.findViewById(R.id.gridViewEdit);
        loChannels.setAnimationParam(0, 0, 2, 2, 1000, this);
        LinearLayoutTouched loBaseControl = (LinearLayoutTouched) view.findViewById(R.id.loBaseControl);
        loBaseControl.setAnimationParam(0, 0, 1.5f, 1.5f, 1000, this);
        LinearLayoutTouched loTransport = (LinearLayoutTouched) view.findViewById(R.id.loTransport);
        loTransport.setAnimationParam(0, 100, 2, 2, 1000, this);
        LinearLayoutTouched loPitch = (LinearLayoutTouched) view.findViewById(R.id.loPitch);
        loPitch.setAnimationParam(100, 100, 2, 2, 1000, this);
        LinearLayoutTouched loPan = (LinearLayoutTouched) view.findViewById(R.id.loPan);
        loPan.setAnimationParam(600, 100, 2, 2, 1000, this);
        final LinearLayoutTouched loADSR = (LinearLayoutTouched) view.findViewById(R.id.loADSR);
        loADSR.setAnimationParam(340, 310, 2, 2, 1000, new AnimtionListener() {
            @Override
            public void onOpenAnimation(CloseAnimationListener view) {
                if (mCurrentOpenLayout != null && mCurrentOpenLayout != view)
                    mCurrentOpenLayout.closeAnimation();
                mCurrentOpenLayout = view;
                loADSR.startAnimation(resizeAnimationOpenAdsr);
            }

            @Override
            public void onCloseAnimation(CloseAnimationListener view) {
                loADSR.startAnimation(resizeAnimationCloseAdsr);
            }
        });
        resizeAnimationOpenAdsr = new ResizeAnimation(loADSR, (int) SizeCalc.pxFromDp(getContext(), 148), (int) SizeCalc.pxFromDp(getContext(), 104));
        resizeAnimationCloseAdsr = new ResizeAnimation(loADSR, (int) SizeCalc.pxFromDp(getContext(), 104), (int) SizeCalc.pxFromDp(getContext(), 148));
        resizeAnimationCloseAdsr.setDuration(300);
        resizeAnimationOpenAdsr.setDuration(300);

        LinearLayoutTouched loVolume = (LinearLayoutTouched) view.findViewById(R.id.loVolume);
        loVolume.setAnimationParam(200, 0, 2, 2, 1000, this);
        LinearLayoutTouched loFilterType = (LinearLayoutTouched) view.findViewById(R.id.loFilterType);
        loFilterType.setAnimationParam(200, 0, 2, 2, 1000, this);
        LinearLayoutTouched loFilterValue = (LinearLayoutTouched) view.findViewById(R.id.loFilterValue);
        loFilterValue.setAnimationParam(200, 0, 2, 2, 1000, this);
        LinearLayoutTouched loPiano = (LinearLayoutTouched) view.findViewById(R.id.loPiano);
        loPiano.setAnimationParam(0, 300, 1.24f, 1.24f, 1000, this);
        final LinearLayoutTouched loPosition = (LinearLayoutTouched) view.findViewById(R.id.loPosition);
        loPosition.setAnimationParam(100, 100, 1, 1.5f, 1000, this);
        mPianoRollLo = (RelativeLayout) view.findViewById(R.id.loPianoRoll);
        vPianoControler = (PianoRollControlerView) view.findViewById(R.id.vPianoControler);
        final Button btnPianoRoll = (Button) view.findViewById(R.id.btnPianoRoll);
        vPianoControler.setChannel(mIndexChannel);
        vPianoControler.setOnCloseListener(new PianoRollControlerView.OnClosePianoListener() {
            @Override
            public void onClosePiano() {
                mPianoRollLo.setVisibility(View.GONE);
                // btnPianoRoll.setVisibility(View.VISIBLE);
            }

            @Override
            public void onOpenPiano() {


            }
        });

        btnPianoRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPianoRollLo.setVisibility(View.VISIBLE);

                vPianoControler.openPiano();

                mPianoRollLo.bringToFront();

            }
        });
    }

    @Override
    public void onOpenAnimation(CloseAnimationListener view) {
        if (mCurrentOpenLayout != null && mCurrentOpenLayout != view)
            mCurrentOpenLayout.closeAnimation();
        mCurrentOpenLayout = view;

    }

    @Override
    public void onCloseAnimation(CloseAnimationListener view) {

    }


}