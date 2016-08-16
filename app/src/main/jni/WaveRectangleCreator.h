//
// Created by Yossi Barel on 31/03/16.
//

#ifndef DRUMMAP_WAVERECTANGLECREATOR_H
#define DRUMMAP_WAVERECTANGLECREATOR_H

#include "WaveCreator.h"
class WaveRectangleCreator : public WaveCreator
{

public:
	WaveRectangleCreator(int sampleRate, int bufferSize);



	float proccess(double distanceMs);

private:

	int mValue;
	int mCount;
};

#endif //DRUMMAP_WAVERECTANGLECREATOR_H
