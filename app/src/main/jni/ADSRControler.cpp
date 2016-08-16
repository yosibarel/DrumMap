//
// Created by Yossi Barel on 17/04/16.
//

#include "ADSRControler.h"

ADSRControler::ADSRControler(int bufferSize, int sampleRate)
{
	mBufferSize = bufferSize;
	mSampleRate = sampleRate;
	mADSR[A] =0.0;
	mADSR[D] = 1.0 * 5000.0;
	mADSR[S] = 1.0;
	mADSR[R] = 0.2 * 5000;
	mCurrentTime = 0.0;
	mCurrentIndex = 0.0;
	mProccessTimeUnit = ((double) bufferSize / sampleRate) * 1000.0;
}

double ADSRControler::proccess()
{

	double val;
	if (mCurrentIndex == A)
	{
		mCurrentTime += mProccessTimeUnit;
		val = mCurrentTime / mADSR[A];
		if (val >= 1.0)
		{
			++mCurrentIndex;
			mCurrentTime = mADSR[D];
		}
	} else if (mCurrentIndex == D)
	{
		mCurrentTime -= mProccessTimeUnit;
		if (mCurrentTime >= 0.0)
			val = ((mCurrentTime / mADSR[D]) * (1.0 - mADSR[S])) + mADSR[S];
		else
			val = mADSR[S];

	} else if (mCurrentIndex == R)
	{
		mCurrentTime -= mProccessTimeUnit;
		if (mCurrentTime >= 0.0)
			val = ((mCurrentTime / mADSR[R]) * (mADSR[S]));
		else
			val = 0.0;
	}

	return val;
}

void ADSRControler::keyDown()
{
	mCurrentTime = 0.0;
	mCurrentIndex = A;
}

void ADSRControler::keyRelese()
{
	mCurrentTime = mADSR[R];
	mCurrentIndex = R;
}

void ADSRControler::setADSRValue(int param, double val)
{
	if (param == S)
	{
		mADSR[S] = val;
	} else
		mADSR[param] = (val) * 5000.0;
}

double ADSRControler::getParamValue(int param)
{
	if (param == S)
	{
		return mADSR[S];
	}
	return mADSR[param] / 5000.0;
}
