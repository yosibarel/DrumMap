//
// Created by Yossi Barel on 28/03/16.
//

#ifndef DRUMMAP_DRUMMAP_H
#define DRUMMAP_DRUMMAP_H


#include "MainGridControler.h"

#define  NUM_CHANNELS 16

enum State
{
	NORML, STATE_RECORD
};

enum QuantizeState
{
	QSTOP, QASK_STOP, QASK_START, QSTART
};
enum StateEditBuffer
{
	EDIT_NOT_PROCCESS, EDIT_PROCCESS, EDIT_FINISH
};

class DrumMap : public ChannelListener, XYControlerListener, MainGridControllerListener
{
public:
	DrumMap(int sampleRate, int bufferSize, void (*fun)(CallBackJavaTypeMessage, int), const char *tempRecordFile);

	~DrumMap();

	void setBpm(double bpm);

	double getBpm()
	{
		return mBpm;
	}

	double getPositionPrecent(int index);

	virtual void onNeedStopProccess(int index);

	bool process(short int *audioIO, unsigned int numberOfSamples, int sampleRate);


	void setEnvADSR(int index, int envType, int type, double value);

	void keyDown(int index);

	void keyRelese(int index);

	void setFx(int indexChannel, int fxFey, int fxKeyParam, float value);

	bool addFx(int channelIndex, int fxType);

	void openChannelFile(int channelIndex, const char *filePath, long len);

	void onLoadFileEvent(SuperpoweredAdvancedAudioPlayerEvent event, int indexChannel);


	long getDurationMs(int channelIndex);

	void loopBetween(jint channelIndex, jdouble start, jdouble end, jboolean i8);

	void setReverse(int indexChannel, bool flag);

	bool getIsLfoActive()
	{
		return mIsLfoActive;
	}

	void sendFxToLFO(string indexLfo, int indexChannel, int fxType, int keyEffectParam, int lfo1, int lfo2, double start, double end, bool i4);

	void setLFORateWave(int indexChannel, int lfoType, double rate);


	void setLFOTypeWave(int indexChannel, int lfoType, int waveType);


	void setLFOAmplitudeWave(int indexChannel, int lfoType, double amplitude);


	void setLFOPresentWave(int indexChannel, double present);

	void sendFxToControlXY(string basic_string, int i, int i1, int i2, int i3, double start, double end, bool i5);

	void setXYControlValue(int i, double d);

	void scretch(double val);

	void resetRecord();

	void startRecord(const char *fileName, bool needMutex);

	void stopRecord();

	bool startPlayRecord();

	void stopPlayRecord();

	double getRecordPosition();

	void setScratchEnable(int channelIndex, bool enable);

	void setStopPlayOnRelease(int index, bool isStop);

	bool getStopPlayOnRelease(int indexChannel);

	void setPitchBend(double pi);

	void enableSequencerPattern(int indexChannel, int indexPattern, bool enable);

	void setValueSequencerPattern(int indexChannel, int indexPattern, int indexValue, double val);

	void enableSequencer(int indexChannel, bool enable);

	void setSeqRate(int indexChannel, int indexValue);

	double getSeqPattern(int indexChannel, int indexPattern, int indexValue);

	int getSeqIndexPlaying(int indexChannel);

	void resetSeq(int indexChannel, int indexValue);

	void setPitchChannel(int indexChannel, double pitch);

	vector<int> getActiveEffect(int indexChannel);

	void setPlayFromStartLoop(int indexChannel, bool isPlayFromStartLoop);

	void setLoop(int indexChannel, bool isLoop);

	void startGlobalRecord(const char *filePath);

	void enableFilter(int indexChannel, bool ebable);

	void sendFxToADSR(int indexChannel, string mIndexControl, int fxType, int keyEffectParam, double start, double end, bool i6);

	void sendFxToCurve(string indexString, int indexChannel, int curve, int fxType, int keyEffectParam, double start, double end, bool i7);

	void setVolume(int indexChannel, double vol);

	double getFxValue(int indexChannel, int fxType, int fxKeyParam);

	double getEnvADSR(int type, int indexChannel, int param);

	int getLFOTypeWave(int indexChannel, int lfoType);

	double getLFOAmplitudeWave(int indexChannel, int lfoType);

	double getLFORateWave(int indexChannel, int lfo);

	double getLfoWavePresent(int indexChannel);

	double getVolume(int indexChannel);

	int getModState(int indexChannel, string indexMod);

	void removeModFx(int indexChannel, string indexMod, int keyEffectParam, int mod);

	int getCurrentFilter(int indexChannel);


	double getMainPositionMs();

	void setMainQuantize(int quntizeIndex);

	void setQuantizeEnable(bool enable);

	bool getIsPlay(int indexChannel);

	void playChannel(int indexChannel);

	void stopPlayChannel(int indexChannel);

	void setStereo(int indexChannel, double left, double right, int progressStereo);

	int getStereoProgress(int indexChannel);

	void setMainChannelPitch(int indexChannel, double value);

	double getMainChannelPitch(int indexChannel);

	void initViewMeter(float **pDouble);

	float getViewMeterChannelLeft(int indexChannel);

	float getViewMeterChannelRight(int indexChannel);

	int getSeqRateProgress(int indexChannel);

	void setSeqStepState(int state, int indexChannel);

	int getSeqStepState(int indexChannel);


	void initWaveform(int indexChannel, float **buffer);

	void openChannelFileForEdit(int i, const char *path, long len);

	void playerEventCallback(SuperpoweredAdvancedAudioPlayerEvent event, void *value);

	void proccessEditWave();

	float **getEditBuffer();

	const char *getFilePath(int indexChannel);

	double getStopPlayPosition(int indexChannel);

	double getStartPlayPosition(int indexChannel);

	bool getIsLoaded(int indexChannel);

	void setPositionMs(int indexChannel, double positionMs);

	bool getIsLoop(int indexChannel);

	void playPiano(int indexChannel);

	void pausePiano(int indexChannel);

	int addMidiPiano(int indexChannel, int key, float start, float mEnd);

	void removeMidiPiano(int indexChannel, int keyIndex);


	void setStartEndLoopPositionPrecentPiano(int indexChannel, double start, double end);

	double getStartLoopMsPiano(int indexChannel);

	double getEndLoopMsPiano(int indexChannel);

	void setPositionMsPiano(int indexChannel, double position);

	double getPositionMsPiano(int indexChannel);

	void setDurationMsPiano(int indexChannel, double duration);

	double getDurationMsPiano(int indexChannel);

	virtual void onPianoKeyDown(int index);

	virtual void onPianoKeyRelsese(int index);

	double getPianoPositionBeat(int indexChannel);

	Midi *getMidis(int indexChannel);

	void setPianoPositionBeat(int indexChannel, double pianoPosition);

	void setPianoBeatsDuration(int indexChannel, float durationBeat);

	float getPianoDurationBeat(int indexChannel);

	float getPianoStartPlayPosition(int indexChannel);

	float getPianoStopPlayPosition(int indexChannel);

	bool getIsPianoPlay(int indexChannel);

	void setSeqState(int indexChannel, int state);

	int getSeqState(int indexChannel);

	int getSeqIndexEnable(int indexChannel);

	void setSeqIndexEnable(int indexChannel, int indexSeq);

	ChannelItem *getChannelItems();

	virtual void stopPlayChannelCallBack(int indexChannel);

	virtual void startPlayChannelCallBack(int indexChannel);

	int addChannelItem(int channel, double start, double end);

	double getMainPositionBeat();

	double getMainDurationBeat();

	void removeChannelItem(int index);

	bool getIsMainGridPlay();

	void playMainGrid();

	void pauseMainGrid();

	float getMainGridStopPlayPosition();

	float getMainGridStartPlayPosition();

	void setMainGridBeatsDuration(float durationBeat);

	void setMainGridPositionBeat(double positionBeat);

	void setStartEndLoopPositionPrecentMainGrid(double startPercent, double endPercent);

	int getChannelColor(int indexChannel);

	void setChannelColor(int indexChannel, int color);

	void resetChannelItemAdded();

	void updateChannelAddedEnd(double end);

	void updateMidiAddedEnd(int indexChannel, float endMidiBeat);

	void resetMidiAdded(int indexChannel);

	bool getPlayFromStartLoop(int indexChannel);

	void initRecordWaveform(float **pBuffer);

	void setIsPianoCursorFollowPosition(int indexChannel, bool isFollow);

	bool getIsPianoCursorFollowPosition(int indexChannel);

	bool getIsGridCursorFollowPosition();

	void setIsGridCursorFollowPosition(bool isFollow);

	void updateMidiPiano(int indexChannel, int indexMidi, int note, float start, float end);

	bool getIsMuted(int indexChannel);

	bool getIsSolo(int indexChannel);

	void setMuted(int indexChannel, bool muted);

	void setSolo(int indexChannel, bool solo);

	void randSeq(int indexChannel, int indexValue);

	void replaceFxChannelPosition(int indexChannel, int from, int to);

	void deleteEffectChannel(int indexChannel, int index);

	bool isEnbableSendFx1(int indexChannel);

	bool isEnbableSendFx2(int indexChannel);

	double getSendFx1(int indexChannel);

	double getSendFx2(int indexChannel);

	void enableSendFx1(int indexChannel, bool enable);

	void enableSendFx2(int indexChannel, bool enable);

	void setSendFx1(int indexChannel, double val);

	void setSendFx2(int indexChannel, double val);

private:
	StateEditBuffer stateEdit;
	static double QUANTIZE[8];
	float *mTempEditBuffer;
	float *mEditBuffer;
	int mProccessIndexOrder[16];
	SuperpoweredAndroidAudioIO *mAudioSystem;
	SuperpoweredStereoMixer *mMainMixer;
	SuperpoweredStereoMixer *mMixer;
	SuperpoweredAdvancedAudioPlayer *mEditPlayer;
	Recorder *mRecorder;
	Channel **mChannels;
	int mSampleRate;
	int mBufferSize;
	float mMainInputLevel[8];
	float mInputLevel[4][8];
	float mOutputLevel[2];
	float mInputMeters[4][8];
	float *mMainStereoBufferOutput[2];
	float *mSubStereoBuffersOutput[4][2];

	float *mbufferProccessing[4][4];
	QuantizeState currentChannelProccessing[NUM_CHANNELS];
	bool scratchChannels[NUM_CHANNELS];
	pthread_mutex_t mutex;

	void (*mFuncCallBack)(CallBackJavaTypeMessage, int);

	bool mIsLfoActive;
	pthread_t mLfoThread;
	XYControler *xyControler;
	FxControler *mFxControler1;
	FxControler *mFxControler2;
	MainGridControler *mMainGridControler;
	State mState;
	double mBpm;
	double mMainPosition;

	int getIndexQuantize();

	double bpmToMs();

	int mQuntizeIndex;
	bool mIsQuantize;
	int mLastQuantize;
	static const int mColors[NUM_CHANNELS];
	int mSoloChannel;

};


#endif //DRUMMAP_DRUMMAP_H
