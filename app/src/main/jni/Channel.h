//
// Created by Yossi Barel on 28/03/16.
//
#include "LFO.h"


#ifndef DRUMMAP_CHANNEL_H
#define DRUMMAP_CHANNEL_H


#include "PianoRoll.h"

enum EnumPlayerType
{
	PLAYER_OSCILLATOR, PLAYER_FILE
};
enum CallBackJavaTypeMessage
{
	EOF_, FILE_LOADED_, ERROR_LOAD_, BUFFER_EDIT_LOADED
};
enum SequencerState
{
	SEQ_OFF, SEQ_ON, SEQ_RETRIGER
};

class Channel : public MODFxListener, PianoListener
{
public:

	Channel(int bufferSize, int sampleRate, int index, ChannelListener *listener);

	~Channel();

	float *proccess(double currentPosition);

	float *internalProccess(double currentPostion);

	float *getBuffer();

	void setEnvADSR(int type, int envType, double value);

	void keyDown();

	void keyRelese();

	void setFx(int keyFx, int fxKeyParam, float value);

	bool addFx(int fxType);

	void openChannelFile(const char *filePath, long len);

	void playerEventCallback(SuperpoweredAdvancedAudioPlayerEvent event, void *value);


	long getDurationMs();

	void loopBetween(jdouble start, jdouble end, jboolean i1);

	void setReverse(bool flag);


	void setLFORateWave(int lfo, double waveType);

	void setLFOTypeWave(int lfoType, int waveType);

	void setLFOAmplitudeWave(int lfo, double amp);

	void setLFOPresentWave(double present);

	void scretch(double val);

	void setStopPlayOnRelease(bool isStop);

	bool getStopPlayOnRelease();

	double getPositionPrecent();

	void setPitchBand(double pitch);

	void setBpm(double bpm);

	void setVolume(double vol)
	{
		mVolume = vol;
	}

	void enableSequencerPattern(int indexPattern, bool enable);

	void setPitchShift(double pitch);

	void setValueSequencerPattern(int indexPattern, int iindexValue, double val);

	void enableSequencer(bool enable);

	void setSeqRate(int indexValue);

	double getSeqPattern(int indexPattern, int indexValue);

	int getSeqIndexPlaying()
	{
		return mSeqControler->getCurrentIndex();
	}

	void resetSeq(int indexValue);

	void setPitch(double pitch);

	vector<int> getActiveEffect();

	void setPlayFromStartLoop(bool isPlayFromStartLoop);

	void setLoop(bool isLoop);

	void enableFilter(bool enable);


	void sendFxToLFO(string indexLfo, int fxType, int keyEffectParam, int lfo1, int lfo2, double start, double end, bool b2);

	void sendFxToADSR(string mIndexControl, int fxType, int keyEffectParam, double start, double end, bool b1);

	void sendFxToCurve(string indexString, int curve, int fxType, int keyEffectParam, double start, double end, bool b);

	void setMainVolume(double vol);

	double getFxValue(int fxType, int fxKeyParam);

	double getEnvADSR(int type, int param);

	int getLFOTypeWave(int lfoType);

	double getLFOAmplitudeWave(int lfoType);

	double getLFORateWave(int lfo);

	double getLfoWavePresent();

	double getVolume();

	ModIndex getModState(string basic_string);

	void removeModFx(int mod, string indexMod, int i);

	int getCurrentFilter();

	void seqTrigger();

	void resetSeq();

	float getRight()
	{
		return mRight;
	}

	float getLeft()
	{
		return mLeft;
	}

	void setRight(float right)
	{
		mRight = right;
	}

	void setLeft(float left)
	{
		mLeft = left;
	}

	void setStereo(float left, float right, int progress);

	int getStereoProgress();

	void setMainChannelPitch(double pitch);

	double getMainChannelPitch();

	float mViewMeterLeft;
	float mViewMeterRight;

	int getSeqRateProgress();

	void setSeqStepState(int state);

	int getSeqStepState();

	void initWaveform(float **pBuffer);

	const char *getFilePath();

	double getStopPlayPosition();

	double getStartPlayPosition();

	bool getIsLoaded();

	void setPosition(double positionMs);

	bool getIsLoop();

	void playPiano();

	void pausePiano();

	int addMidiPiano(int key, float start, float mEnd);

	void removeMidiPiano(int keyIndex);


	void setStartEndLoopPositionPrecentPiano(double start, double end);

	double getStartLoopMsPiano();

	double getEndLoopMsPiano();

	void setPositionMsPiano(double position);

	double getPositionMsPiano();

	void setDurationMsPiano(double duration);

	double getDurationMsPiano();

	void keyPianoDown(double key);

	bool isPianoPlay();

	void proccessPiano();

	double getPianoPositionBeat();

	Midi *getMidis();

	void setPianoPositionBeat(double pianoPosition);

	void setPianoBeatsDuration(float durationBeat);

	float getPianoDurationBeat();

	float getPianoStartPlayPosition();

	float getPianoStopPlayPosition();

	void setSeqState(int state);

	int getSeqState();

	int getSeqIndexEnable();

	void setSeqIndexEnable(int indexSeq);

	int getChannelColor();

	void setChannelColor(int color);

	void clearAllSendMod();

	void updateMidiAddedEnd(float endMidiBeat);

	void resetMidiAdded();

	bool getPlayFromStartLoop();

	void setIsPianoCursorFollowPosition(bool isFollow);

	bool getIsPianoCursorFollowPosition();

	void updateMidiPiano(int indexMidi, int note, float start, float end);

	void setMuted(bool muted);

	bool isMuted();

	void randSeq(int indexValue);

	void replaceFxPosition(int from, int to);

	void deleteEffectChannel(int index);

	double getSendFx1()
	{
		return mSendFx1;
	}

	double getSendFx2()
	{
		return mSendFx2;
	}

	void setSendFx1(double snd)
	{
		mSendFx1 = snd;
	}

	void setSendFx2(double snd)
	{
		mSendFx2 = snd;
	}

	void setIsSendFx2(bool snd)
	{
		mIsSendFx2 = snd;
	}

	void setIsSendFx1(bool snd)
	{
		mIsSendFx1 = snd;
	}

	bool isSendFx1()
	{
		return mIsSendFx1;
	}

	bool isSendFx2()
	{
		return mIsSendFx2;
	}

private:

	bool mIsFileOpen;
	PianoRoll *mPianoRoll;
	FxControler *mFxControler;
	LFOControler *mLFOControler;
	SeqControler *mSeqControler;
	ADSRControler *mADSRControler;
	ADSRFxControler *mADSRFxControler;
	SuperpoweredAdvancedAudioPlayer *mPlayer;

	float mPlayEnergy;


	char *mFilePath;
	float *mBufferProccess;
	float *mBufferSilence;


	int mSampleRate;
	int mBufferSize;
	int mIndex;


	ChannelListener *mListener;

	double mStartPosition;
	double mEndPosition;

	bool mKeyDown;
	bool mIsReverse;
	double mVolume;
	double mMainVolume;
	bool mIsStopOnRelease;
	bool mIsSequencer;

	bool mIsLoop;
	bool mIsPlaynigFromStartOnKeyDown;
	bool mIsStopOnRelese;

	double getPatternValue(int pattern, int value);


	float mRight;
	float mLeft;
	double mPitch;
	double mBpm;
	bool mIsEmpty;
	int mStereoProgress;
	double mMainPitch;
	double mFxPitch;

	int mColor;
	double mUnitLoopProccessMs;

	SequencerState mSeqState;
	int mNumKeyPlaying;

	void keyPianoRelese();

	bool mMuted;

	double mSendFx1;
	double mSendFx2;
	bool mIsSendFx1;
	bool mIsSendFx2;
};


#endif //DRUMMAP_CHANNEL_H
