//
// Created by Yossi Barel on 31/03/16.
//

#ifndef DRUMMAP_WAVSAWCREATOR_H
#define DRUMMAP_WAVSAWCREATOR_H

#include "WaveCreator.h"
class WaveSawCreator : public WaveCreator
{

public:
	WaveSawCreator(int sampleRate, int bufferSize);




	virtual float proccess(double distanceMs);

private:

	int mMod;
	int mCount;
};

#endif //DRUMMAP_WAVSAWCREATOR_H
