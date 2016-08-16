//
// Created by Yossi Barel on 31/03/16.
//

#include "WaveSawCreator.h"

WaveSawCreator::WaveSawCreator(int sampleRate, int bufferSize) : WaveCreator(sampleRate, bufferSize)
{
	mCount = 0;
	mMod = 0;
}


float WaveSawCreator::proccess(double distanceMs)
{
	mMod = (int) (float) (mSampleRate / (mRateHz )* (60.0 / mBpm));
	mCount += distanceMs;
	return (((double) ((mCount % mMod) / (double) (mMod))) * 2.0 - 1.0) * mAmplitude;

}
