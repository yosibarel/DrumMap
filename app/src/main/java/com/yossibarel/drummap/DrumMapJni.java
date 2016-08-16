package com.yossibarel.drummap;

import java.util.ArrayList;

/**
 * Created by yossibarel on 29/03/16.
 */
public class DrumMapJni {


    public static final int EOF = 0;
    public static final int LOAD_FILE = 1;
    public static final int ERROR_LOAD = 2;
    public static final int BUFFER_EDIT_LOADED = 3;

    private static DrumMapJni mInstance;

    private DrumMapJni() {


    }

    public static DrumMapJni getInstance() {
        if (mInstance == null)
            mInstance = new DrumMapJni();
        return mInstance;
    }

    public native void scretch(double value);

    public native void resetRecord();

    public native void startRecord(String s);

    public native void stopRecord();

    public native boolean startPlayRecord();

    public native void stopPlayRecord();

    public native double getRecordPosition();

    public native void setScratchEnable(int channel, boolean scratchEnable);

    public native void setStopPlayOnRelease(int mIndexChannel, boolean checked);

    public native boolean getStopPlayOnRelease(int mIndexChannel);

    public native double getPositionPrecent(int mIndexChannel);

    public native void setPitchBand(double pitch);

    public native void enableSequencerPattern(int indexChannel, int indexPattern, boolean enable);

    public native void setValueSequencerPattern(int mIndexChannel, int indexPattern, int mIndexValue, double val);

    public native void enableSequencer(int mIndexChannel, boolean checked);

    public native void setSeqRate(int mIndexChannel, int progress);

    public native double getSeqPattern(int mIndexChannel, int indexPattern, int mIndexValue);

    public native int getSeqIndexPlaying(int indexChannel);

    public native void resetSeq(int mIndexChannel, int mIndexValue);

    public native void setPitch(int mIndexChannel, double pitch);

    public native int[] getActiveEffect(int mChannelIndex);

    public native void setLoop(int indexChannel, boolean isLoop);

    public native void setPlayFromStartLoop(int indexChannel, boolean isPlayFromStartLoop);

    public native boolean getPlayFromStartLoop(int indexChannel);

    public native void setBpm(double bpm);

    public native void sendFxToSensorXY(String mIndexLfo, int mIndex, int control, int mFxType, int keyEffectParam);

    public native void startGlobalRecord(String file);

    public native void enableFilter(int mIndexChannel, boolean checked);

    public native void sendFxToADSR(String mIndexControl, int indexChannel, int mFxType, int keyEffectParam, double start, double end, boolean isUpdate);

    public native void sendFxToCurve(String indexString, int mIndex, int curve, int mFxType, int keyEffectParam, double start, double end, boolean isUpdate);

    public native void sendFxToLFO(String mIndexLfo, int channelIndex, int fxType, int keyEffectParam, int lfo1, int lfo2, double start, double end, boolean isUpdate);

    public native void sendFxToControlXY(String mIndexLfo, int mIndex, int control, int mFxType, int keyEffectParam, double start, double end, boolean isUpdate);

    public native void setVolume(int indexChannel, double vol);

    public native double getFxValue(int indexChannel, int fxType, int fxKeyParam);

    public native double getEnvADSR(int adsrType, int indexChannel, int adsrParam);

    public native int getLFOTypeWave(int channelIndex, int lfoType);

    public native double getLFOAmplitudeWave(int mChannelIndex, int lfoType);

    public native double getLFORateWave(int indexChannel, int lfo);

    public native double getLfoWavePresent(int mChannelIndex);

    public native double getVolume(int mIndexChannel);

    public native int getModState(int indexChannel, String indexMod);

    public native void removeModFx(int indexChannel, String indexMod, int keyEffectParam, int mod);

    public native int getCurrentFilter(int mIndexChannel);

    public native double getMainPosition();

    public native void setMainQuantize(int quntizeIndex);

    public native void setQuantizeEnable(boolean enable);

    public native boolean getIsPlay(int index);

    public native void playChannel(int channelIndex);

    public native void stopPlayChannel(int channelIndex);

    public native void setStereo(int mIndexChannel, double left, double right, int progress);

    public native int getStereoProgress(int mIndexChannel);

    public native void setMainChannelPitch(int mIndexChannel, double mPitch);

    public native double getMainChannelPitch(int mIndexChannel);

    public native float getViewMeterChannelLeft(int mIndexChannel);

    public native float getViewMeterChannelRight(int mIndexChannel);

    public native int getSeqRateProgress(int mIndexChannel);

    public native void drumMap(int sampleRate, int bufferSize, String tempFileName);

    public native void setEnvADSR(int envType, int index, int type, double progress);

    public native void keyDown(int index);

    public native void keyRelese(int index);

    public native void setFx(int mIndexChannel, int fxKey, int fxKeyParam, float damp);

    public native boolean addEffect(int mChannelIndex, int indexFx);

    public native boolean openChannelFile(int channelIndex, String path, long length);

    public native long getDurationMs(int mIndexChannel);

    public native void loopBetween(int channelIndex, double start, double end, boolean startPlay);

    public native void setReverse(int mIndexChannel, boolean checked);


    public native void setLFORateWave(int mChannelIndex, int lfo1, double rate);

    public native void setLFOPresentWave(int mChannelIndex, double present);

    public native void setLFOAmplitudeWave(int mChannelIndex, int lfo, double amp);

    public native void setLFOTypeWave(int mChannelIndex, int lfo, int waveType);


    public native void setControlXYValue(int control, double val);

    public native float[] getAllViewMeters();

    public native void setSeqStepState(int state, int indexChannel);

    public native int getSeqStepState(int indexChannel);

    public native float[] getWaveform(int mIndexChannel, int bufferSize);

    public native void openChannelFileForEdit(int mChannelIndex, String mSourcePath, long length);

    public void jnicallback(int channel, int typeMessage) {


        switch (typeMessage) {
            case EOF:
                notifyEOF(channel);
                break;
            case LOAD_FILE:
                notifyFileLoadSuccess(channel);
                break;
            case ERROR_LOAD:
                notifyFileErrorLoad(channel);
                break;
            case BUFFER_EDIT_LOADED:
                notifyBufferEditLoaded(channel);
                break;

        }
    }

    synchronized private void notifyBufferEditLoaded(int channel) {
        for (int i = 0; i < listeners.size(); i++)
            listeners.get(i).onBufferEditLoaded(channel);
    }

    synchronized private void notifyFileLoadSuccess(int channel) {
        for (int i = 0; i < listeners.size(); i++)
            listeners.get(i).onFileLoadSuccess(channel);
    }

    synchronized private void notifyFileErrorLoad(int channel) {
        for (int i = 0; i < listeners.size(); i++)
            listeners.get(i).onLoadFileError(channel);
    }

    synchronized private void notifyEOF(int channel) {
        for (int i = 0; i < listeners.size(); i++)
            listeners.get(i).onEOF(channel);
    }

    public native float[] getEditBuffer(int sizeByte);

    public native byte[] getFilePath(int mIndexChannel);

    public native double getStartPlayPosition(int mChannelIndex);

    public native double getStopPlayPosition(int mChannelIndex);

    public native boolean getIsLoaded(int mIndexChannel);

    public native void setPosition(int mChannelIndex, double positionMs);

    public native boolean getIsLoop(int mChannelIndex);

    public native int getBpm();

    public native void playPiano(int mIndexChannel);

    public native void pausePiano(int mIndexChannel);

    public native void removeMidiPiano(int mIndexChannel, int mIndex);

    public native int addMidiPiano(int mIndexChannel, int key, float mStart, float mEnd);

    public native double getPianoPosition(int mIndexChannel);

    public native double getPianoPositionBeat(int mIndexChannel);

    public native float[] getMidiFloat(int mIndexChannel);

    public native void setPianoPositionBeat(int mIndexChannel, double mianoPosition);

    public native void setPianoBeatsDuration(int mIndexChannel, float mDuration);

    public native float getPianoDurationBeat(int mIndexChannel);

    public native float getPianoStartPlayPosition(int mIndexChannel);

    public native float getPianoStopPlayPosition(int mIndexChannel);

    public native void setStartEndLoopPositionPrecentPiano(int indexChannel, double start, double end);

    public native boolean getIsPianoPlay(int mIndexChannel);

    public native void setSeqState(int state, int mIndexChannel);

    public native int getSeqState(int mIndexChannel);

    public native int getSeqIndexEnable(int mIndexChannel);

    public native void setSeqIndexEnable(int mIndexChannel, int indexSeq);

    public native int addChannelItem(int mChannel, double mStart, double mEnd);

    public native double getMainPositionBeat();

    public native double getMainDurationBeat();

    public native float[] getChannelItemsFloat();

    public native void removeChannelItem(int mIndex);

    public native boolean getIsMainGridPlay();

    public native void playMainGrid();

    public native void pauseMainGrid();

    public native float getMainGridStartPlayPosition();

    public native float getMainGridStopPlayPosition();

    public native void setMainGridBeatsDuration(float mDuration);

    public native void setMainGridPositionBeat(double positionBeat);

    public native void setStartEndLoopPositionPrecentMainGrid(double startPlayPositionPercent, double endPlayPositionPercent);

    public native int getChannelColor(int mIndexChannel);

    public native void setChannelColor(int mIndexChannel, int color);

    public native void resetChannelItemAdded();

    public native void updateChannelAddedEnd(double mEnd);

    public native void updateMidiAddedEnd(int mIndexChannel, float mEnd);

    public native void resetMidiAdded(int indexChannel);

    public native float[] getRecordWaveform(int bufferSize);

    public native void setIsPianoCursorFollowPosition(int mIndexChannel, boolean isFollow);

    public native boolean getIsPianoCursorFollowPosition(int mIndexChannel);

    public native boolean getIsGridCursorFollowPosition();

    public native void setIsGridCursorFollowPosition(boolean isFollow);

    public native void updateMidiPiano(int mIndexChannel, int mIndexKey, int note, float mStart, float mEnd);

    public native boolean getIsMuted(int mChannel);

    public native void setMuteed(int mChannel, boolean checked);

    public native boolean getIsSolo(int mChannel);

    public native void setSolo(int mChannel, boolean checked);

    public native void randSeq(int mIndexChannel, int mIndexValue);

    public native void replaceFxChannelPosition(int mChannel, int fromPosition, int toPosition);

    public native void deleteEffectChannel(int mIndexChannel, int index);

    public native boolean isEnbableSendFx1(int mChannel);

    public native boolean isEnbableSendFx2(int mChannel);

    public native double getSendFx1(int mChannel);

    public native double getSendFx2(int mChannel);

    public native void enableSendFx1(int mChannel, boolean checked);

    public native void enableSendFx2(int mChannel, boolean checked);

    public native void setSendFx1(int mChannel, double v);

    public native void setSendFx2(int mChannel, double v);

    public interface JniListener {
        void onEOF(int channel);

        void onFileLoadSuccess(int channel);

        void onLoadFileError(int channel);

        void onBufferEditLoaded(int channel);
    }

    public static ArrayList<JniListener> listeners = new ArrayList<>();

    synchronized public void addJniListener(JniListener l) {
        listeners.add(l);
    }

    synchronized public void removeJniListener(JniListener l) {
        listeners.remove(l);
    }

    static {
        System.loadLibrary("drummapjni");
    }
}
