//
// Created by Yossi Barel on 31/03/16.
//

#ifndef DRUMMAP_WAVETRIANGLECREATOR_H
#define DRUMMAP_WAVETRIANGLECREATOR_H

#include "WaveCreator.h"

class WaveTriangleCreator : public WaveCreator
{

public:
	WaveTriangleCreator(int sampleRate, int bufferSize);



	virtual float proccess(double distanceMs);

private:

	float mValue;
	float mUnit;
	float mSample;
};


#endif //DRUMMAP_WAVETRIANGLECREATOR_H
