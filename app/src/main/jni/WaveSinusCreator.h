//
// Created by Yossi Barel on 31/03/16.
//

#ifndef DRUMMAP_WAVESINUSCREATOR_H
#define DRUMMAP_WAVESINUSCREATOR_H


#include <math.h>
#include "WaveCreator.h"
#include <android/log.h>

class WaveSinusCreator : public WaveCreator
{

public:
	WaveSinusCreator(int sampleRate, int bufferSize);

	float proccess(double distanceMs);

private:

	float mRamp;
	float sinusCount;
	double mul;
};


#endif //DRUMMAP_WAVESINUSCREATOR_H
