//
// Created by Yossi Barel on 30/03/16.
//

#ifndef DRUMMAP_WAVECREATOR_H
#define DRUMMAP_WAVECREATOR_H

#include <math.h>

class WaveCreator
{
public:
	WaveCreator(int sampleRate, int bufferSize) : mSampleRate(sampleRate), mBufferSize(bufferSize)
	{
		mRateHz = 1.0;
		mAmplitude = 1.0;
	}

	void setRate(double rateHzSec)
	{
		mRateHz = rateHzSec;
	}

	void setAmplitude(double amp)
	{
		mAmplitude = amp;
	}

	void setBpm(double bpm)
	{
		mBpm=bpm;
	}


	virtual float proccess(double distanceMs) = 0;

protected:
	int mSampleRate;
	int mBufferSize;
	double mAmplitude;
	double mRateHz;
	double mBpm;
};


#endif //DRUMMAP_WAVECREATOR_H
