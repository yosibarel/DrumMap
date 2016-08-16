//
// Created by Yossi Barel on 02/04/16.
//

#ifndef DRUMMAP_FXCONTROLER_H
#define DRUMMAP_FXCONTROLER_H
#define FX_PITCH -3
#define FX_STEREO -2
#define FX_REVERB 0
#define FX_DELAY 1
#define FX_FLANGER 2
#define FX_ROLL 3
#define FX_LIMITER 4
#define FX_GATE 5
#define FX_WHOOSH 6

#define FILTER_LOW_PASS 10
#define FILTER_HIGH_PASS 11
#define FILTER_BAND_PASS 12
#define FILTER_NOTCH 13
#define FILTER_LOW_SHELF 14
#define FILTER_HIGH_SHELF 15
#define FILTER_PARAMETRIC 16

#include "Fx.h"
enum KeyStereo
{
	ST_LEFT, ST_RIGHT
};
enum KeyReverb
{
	REVERB_DISABLE = -1, MIX, DUMP, WIDTH, DRY, WET, ROOMSIZE
};


enum KeyDelay
{
	DELAY_DISABLE = -1, DELAY_MIX, BEATS, DECAY
};
enum KeyFlanger
{
	FLANGER_DISABLE = -1, FLNG_WET, FLNG_DEPTH, FLNG_LFO_BEATS, FLNG_CLIPPER_DB, FLNG_CLIPPER_MAX_DB,
};
enum KeyRoll
{
	ROLL_DISABLE = -1, ROLL_WET, ROLL_BEATS
};
enum KeyLimiter
{
	LIMITER_DISABLE = -1, LIMITER_CEILLING_DB, LIMITER_THRESHHOLD_DB, LIMITER_RELEASE_SEC

};
enum KeyFilter
{
	FILTER_DISABLE = -1, FILTER_FREQ, FILTER_RES, FILTER_DECIBEL, FILTER_OCTAVE, FILTER_SLOPE

};
enum KeyGate
{
	GATE_DISABLE = -1, GATE_WET, GATE_BEATS
};
enum KeyWhoosh
{
	WHOOSH_DISABLE = -1, WHOOSH_WET, WHOOSH_FREQ
};


class DMSuperpoweredReverb;

class FxControler
{
public :

	FxControler(int sampleRate, int bufferSize);

	void process(float *audio,float mix);

	void setFx(int keyFx, int fxKeyParam, float value);

	bool addFx(int fxType);

	vector<int> getActiveEffect();

	void setBpm(double bpm);

	void enableFilter(bool enable);

	double getFxValue(int fxType, int fxKeyParam);

	int getCurrentFilter();

	void reset();

	void replaceFxPosition(int from, int to);

	void deleteEffectChannel(int index);

private:

	std::vector<Fx *> vecFx;
	std::vector<int> vecFxOrder;
	void setFxReverb(int fxKeyParam, float value);

	void setfxDeley(int fxKeyParam, float value);

	void setFxFlanger(int fxKeyParam, float value);

	void setFxLimiter(int fxKeyParam, float value);

	void setFxRoll(int fxKeyParam, float value);

	void setFxFilter(int fxKeyParam, float value);

	YBSuperpoweredReverb *fxReverb;
	YBSuperpoweredEcho *fxDelay;
	YBSuperpoweredFlanger *fxFlanger;
	YBSuperpoweredLimiter *fxLimiter;
	YBSuperpoweredRoll *fxRoll;
	YBSuperpoweredGate *fxGate;
	YBSuperpoweredWhoosh *fxWhoosh;
	Filter *mFilter;
	int mSampleRate;
	int mBufferSize;

	void setFxGate(int fxKeyParam, float value);

	double mBpm;
	bool mIsFilterEnable;
	void setFxWhoosh(int fxKeyParam, float value);

	double getfxDeley(int fxKeyParam);

	double getFxFlanger(int fxKeyParam);

	double getFxLimiter(int fxKeyParam);

	double getFxReverb(int fxKeyParam);

	double getFxRoll(int fxKeyParam);

	double getFxGate(int fxKeyParam);

	double getFxWhoosh(int fxKeyParam);

	double getFxFilter(int fxKeyParam);

	double mDeleayMix;
	double fxRollBeats;
	double fxGateBeats;
	double fxWhooshFreq;
	float fxFlangerLfoBeats;
	float fxDelayBeats;
	int mCurrentFilter;

};


#endif //DRUMMAP_FXCONTROLER_H



