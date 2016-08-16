//
// Created by Yossi Barel on 31/03/16.
//

#include <stdlib.h>
#include "WaveTriangleCreator.h"

WaveTriangleCreator::WaveTriangleCreator(int sampleRate, int bufferSize) : WaveCreator(sampleRate, bufferSize)
{
	mValue = 0;
	mUnit = 1.0;
	mSample = 0;
}


float WaveTriangleCreator::proccess(double distanceMs)
{
	if (mRateHz == 0.0)
		return 1.0;
	mValue = distanceMs / float(mSampleRate / mRateHz* (60.0 / mBpm));
	mUnit = (mValue*4.0) * fabsf(mUnit) / mUnit;

	if (fabsf(mSample + mUnit) >= 1.0)
	{
		mUnit = -mUnit;
		//	mSample = mSample + mUnit;
	}
	mSample = mSample + mUnit;

	return mSample * mAmplitude;

}