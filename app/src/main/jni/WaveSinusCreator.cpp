//
// Created by Yossi Barel on 31/03/16.
//

#include "WaveSinusCreator.h"

WaveSinusCreator::WaveSinusCreator(int sampleRate, int bufferSize) : WaveCreator(sampleRate, bufferSize)
{
	sinusCount = 0;
}

float WaveSinusCreator::proccess(double distanceMs)
{
	//__android_log_print(ANDROID_LOG_DEBUG, "ndk", "psinusCount %f", sinusCount);
	sinusCount += distanceMs / (mSampleRate / mRateHz* (60.0 / mBpm));
	if (sinusCount >= 1.0)
		sinusCount = 0.0;
	return (float) (sin(2.0f * float(M_PI) * sinusCount) * mAmplitude);
}
