//
// Created by Yossi Barel on 31/03/16.
//

#include "WaveRectangleCreator.h"

WaveRectangleCreator::WaveRectangleCreator(int sampleRate, int bufferSize) : WaveCreator(sampleRate, bufferSize)
{
	mCount = 0;
	mValue = 0;
}


float WaveRectangleCreator::proccess(double distanceMs)
{


	mValue = (int) (mCount / (mSampleRate / (mRateHz * 2.0) * (60.0 / mBpm))) % 2 == 0 ? -1.0 : 1.0;

	mCount += distanceMs;
	return mValue * mAmplitude;

}