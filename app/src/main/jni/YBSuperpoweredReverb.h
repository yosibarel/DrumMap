//
// Created by Yossi Barel on 07/08/16.
//

#ifndef DRUMMAP_YBSUPERPOWEREDREVERB_H
#define DRUMMAP_YBSUPERPOWEREDREVERB_H

#include "SuperpoweredReverb.h"
#include "Mixable.h"

class YBSuperpoweredReverb : public Mixable
{
public:
	YBSuperpoweredReverb(int sampleRate)
	{

	}

	void enable(bool flag);

	void setDamp(float damp);

	void setWidth(float w);

	void setMix(float mix);

	void setDry(float dry);

	void setWet(float wet);

	void setRoomSize(float rs);

	bool getEnabled();

	double getDamp();

	double getWidth();

	double getMix();

	double getDry();

	double getRoomSize();

	double getWet();

private:
	SuperpoweredReverb* mSuperpoweredReverb;
};


#endif //DRUMMAP_YBSUPERPOWEREDREVERB_H
