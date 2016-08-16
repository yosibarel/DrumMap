//
// Created by Yossi Barel on 06/04/16.
//

#include "LFO.h"

LFO::LFO(int sampleRate, int bufferSize)
{
	mWaveSinus = new WaveSinusCreator(sampleRate, bufferSize);
	mWaveSaw = new WaveSawCreator(sampleRate, bufferSize);
	mWaveRectangle = new WaveRectangleCreator(sampleRate, bufferSize);
	mWaveTriangle = new WaveTriangleCreator(sampleRate, bufferSize);
	mWaveCurrent = mWaveSinus;

	setAmplitude(1.0);
	setRate(0.5);


}

LFO::~LFO()
{
	delete(mWaveSinus);
	delete(mWaveSaw);
	delete(mWaveRectangle);
	delete(mWaveTriangle);
}

void LFO::setLfoWave(int type)
{
	mWaveFrom = (EnumWaveForm) type;
	switch ((EnumWaveForm) type)
	{
		case SINUS:
			mWaveCurrent = mWaveSinus;
			break;
		case SAW:
			mWaveCurrent = mWaveSaw;
			break;
		case RECTANGLE:
			mWaveCurrent = mWaveRectangle;
			break;
		case TRIANGLE:
			mWaveCurrent = mWaveTriangle;
			break;
	}
	mWaveCurrent->setBpm(mBpm);
	mWaveCurrent->setAmplitude(mAmplitude);
	mWaveCurrent->setRate(mRate);
}

double LFO::proccess(double currentMS)
{
	return mWaveCurrent->proccess(currentMS);
}

void LFO::setRate(double rate)
{
	mRateValue = rate;
	mRate = 16.0 / pow(2.0, (double) (int) (rate * 8.0));

	mWaveCurrent->setRate(mRate);
}

void LFO::setAmplitude(double amp)
{
	mAmplitude = amp;
	mWaveCurrent->setAmplitude(mAmplitude);
}

int LFO::getLfoWave()
{
	return (int) mWaveFrom;
}

double LFO::getAmplitude()
{
	return mAmplitude;
}

double LFO::getRate()
{
	return mRateValue;
}

void LFO::setBpm(double bpm)
{
	mBpm = bpm;
	if (mWaveCurrent != NULL)
		mWaveCurrent->setBpm(bpm);
}
