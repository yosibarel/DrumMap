//
// Created by Yossi Barel on 12/04/16.
//

#ifndef DRUMMAP_SEQCONTROLER_H
#define DRUMMAP_SEQCONTROLER_H


#include "ADSRControler.h"
#include "FxControler.h"

using namespace std;

class Channel;
enum SeqValueIndex
{
	SEQ_ENABLE=-1,SEQ_VOLUME,SEQ_PITCH,SEQ_LENGTH,SEQ_STEP_DUR,SEQ_CURVE_1,SEQ_CURVE_2
};
typedef struct SeqParrern
{


	double volume;
	double pitch;
	double length;
	double stepDur;
	double curve[2];
	bool isEnable;

} SeqParrern;
typedef struct CurveFx
{
	int keyFx;
	int keyFxParam;
	double start;
	double end;
} CurveFx;
enum SeqStepState
{
	SEQ_NORMAL, SEQ_REVERSE, SEQ_GO_AND_BACK, SEQ_RANDOM
};

class SeqControler
{
public:
	SeqControler(int sampleRate, int bufferSize, MODFxListener *listener);

	static double RATES[6];

	float *proccess(Channel *channel, double currentPosition, double pitch);

	void setBpm(double bpm)
	{
		mBpm = bpm;
	}

	void setRate(double rate)
	{
		mIndexRate = rate;

		mRate = RATES[mIndexRate];
	}

	void enableSequencerPattern(int indexPattern, bool enable);

	void setValueSequencerPattern(int indexPattern, int indexValue, double val);

	double getPatternValue(int pattern, int indexValue);

	int getCurrentIndex();

	void resetValue(int indexValue);

	void sendFxToCurve(string indexString, int curve, int fxType, int keyEffectParam, double start, double end, bool b);

	ModIndex getModState(string modIndex);

	void removeFx(int mod, string indexMod);

	void startSeq();

	int getRateProgress();

	void setStepState(int state)
	{
		mLastIndex = -1;
		mReversePluse = 1;
		mStepState = (SeqStepState) state;
	}

	int getStepState()
	{
		return mStepState;
	}

	bool isGoToNext();

	void setIndexPlay(int indexPattern);

	int getSeqIndexEnable();

	void setSeqIndexEnable(int indexSeq);

	void removeAll();

	void setRetriger();

	void randSeq(int indexVal);

private:

	static double stepsArr[6];

	double getPrecentFromCurrentIndex(double len);

	int getTheNextIndex();

	int getIndexOfSeq();

	double bpmToMs();

	double mLastIndex;
	map<string, CurveFx *> curveSend[2];
	double mCurrentTime;
	double mBpm;
	double mSampleRate;
	double mBufferSize;
	SeqParrern *mPatterns[16];
	int mCurrentIndex;
	int mIndexRate;
	double mRate;
	MODFxListener *mListener;
	SeqStepState mStepState;
	int mMaxSteps;
	int mReversePluse;
	bool mIsRetriger;
};


#endif //DRUMMAP_SEQCONTROLER_H
