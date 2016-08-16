//
// Created by Yossi Barel on 05/04/16.
//

#ifndef DRUMMAP_LFOCONTROLER_H
#define DRUMMAP_LFOCONTROLER_H

#include "LFO.h"
#include "MODFxListener.h"

typedef struct LfoFx
{
	int keyFx;
	int keyFxParam;
	double lfo1;
	double lfo2;
	double start;
	double end;
}LfoFx;

class LFOControler
{
public:
	LFOControler(int sampleRate, int bufferSize, MODFxListener *listener);

	~LFOControler();

	void proccess(double distanceMs);

	void setPresent(double present);

	void setLfoRate(int lfoType, double rate);

	void setLfoAmplitude(int lfoType, double rate);

	void setLfoWave(int lfoType, int wave);

	void addFx(string indexLfo, int keyFx, int keyFxParam, int lfo1, int lfo2, double d, double d1, bool b);

	void removeFx(int mod, string modIndex);

	double getLfoRate(int lfoType);

	double getLfoAmplitude(int lfoType);

	int getLFOTypeWave(int lfoType);

	double getLfoWavePresent();

	void setBpm(double bpm);

	ModIndex getModState(string modIndex);

	void removeAll();

private:
	LFO *mLfos[2];
	MODFxListener * mListener;
	map<string,LfoFx *> mLFOSend;
	int mSampleRate;
	int mBufferSize;
	double mPresent;

};


#endif //DRUMMAP_LFOCONTROLER_H
