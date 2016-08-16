//
// Created by Yossi Barel on 05/04/16.
//

#include "LFOControler.h"

LFOControler::LFOControler(int sampleRate, int bufferSize, MODFxListener *listener)
{
	mSampleRate = sampleRate;
	mBufferSize = bufferSize;
	mLfos[0] = new LFO(sampleRate, bufferSize);
	mLfos[1] = new LFO(sampleRate, bufferSize);
	mListener = listener;
}

LFOControler::~LFOControler()
{
	delete(mLfos[0]);
	delete(mLfos[1]);


}

int sample = 0;

void LFOControler::proccess(double distanceMs)
{
	double lfo1val = (mLfos[0]->proccess(distanceMs) + 1.0) / 2.0;
	double lfo2val = (mLfos[1]->proccess(distanceMs) + 1.0) / 2.0;
	double val, l1, l2;

	for (map<string, LfoFx *>::iterator iter = mLFOSend.begin(); iter != mLFOSend.end(); ++iter)
	{
		LfoFx *lfofx = iter->second;
		l1 = lfofx->lfo1;
		l2 = lfofx->lfo2;


		if (l1 > 0 && l2 > 0)
		{
			val = (lfo1val * mPresent + lfo2val * (1.0 - mPresent));
		} else
		{
			val = lfo1val * l1 + lfo2val * l2;
		}

		__android_log_print(ANDROID_LOG_DEBUG, "ndk", "lfo1 %f sample %d", val, sample);
		mListener->setFx(lfofx->keyFx, lfofx->keyFxParam, lfofx->start + val * (lfofx->end - lfofx->start));
	}
	sample += distanceMs;
	sample %= mSampleRate;


}


void LFOControler::setPresent(double present)
{
	mPresent = present;
}

void LFOControler::setLfoRate(int lfoType, double rate)
{
	mLfos[lfoType]->setRate(rate);
}

void LFOControler::setLfoAmplitude(int lfoType, double amp)
{
	mLfos[lfoType]->setAmplitude(amp);
}

void LFOControler::setLfoWave(int lfoType, int wave)
{
	mLfos[lfoType]->setLfoWave(wave);
}

void LFOControler::addFx(string indexLfo, int keyFx, int keyFxParam, int lfo1, int lfo2, double start, double end, bool isUpdate)
{
	if (!isUpdate)
	{
		LfoFx *fx = new LfoFx();
		fx->keyFx = keyFx;
		fx->keyFxParam = keyFxParam;
		fx->lfo1 = lfo1;
		fx->lfo2 = lfo2;
		fx->start = start;
		fx->end = end;
		mLFOSend[indexLfo] = fx;
	} else
	{
		mLFOSend[indexLfo]->start = start;
		mLFOSend[indexLfo]->end = end;
	}
}

void LFOControler::removeFx(int mod, string modIndex)
{


	LfoFx *fx = mLFOSend[modIndex];
	mLFOSend.erase(modIndex);
	delete (fx);
}

double LFOControler::getLfoRate(int lfoType)
{
	return mLfos[lfoType]->getRate();
}

double LFOControler::getLfoAmplitude(int lfoType)
{
	return mLfos[lfoType]->getAmplitude();
}


int LFOControler::getLFOTypeWave(int lfoType)
{
	return mLfos[lfoType]->getLfoWave();;
}

double LFOControler::getLfoWavePresent()
{
	return mPresent;
}

void LFOControler::setBpm(double bpm)
{
	mLfos[0]->setBpm(bpm);
	mLfos[1]->setBpm(bpm);
}

ModIndex LFOControler::getModState(string modIndex)
{
	ModIndex mod = NON_MOD;
	if (mLFOSend.find(modIndex) != mLFOSend.end())
	{
		LfoFx *fx = mLFOSend[modIndex];
		if (fx->lfo1 > 0.5 && fx->lfo2 > 0.5)
			mod = LFO_1_2;
		else if (fx->lfo1 > 0.5)
			mod = LFO_1;
		else if (fx->lfo2 > 0.5)
			mod = LFO_2;
	}
	return mod;
}

void LFOControler::removeAll()
{
	for (map<string, LfoFx *>::iterator iter = mLFOSend.begin(); iter != mLFOSend.end(); ++iter)
	{
		LfoFx *lfofx = iter->second;
		delete(lfofx);
	}
	mLFOSend.clear();
}