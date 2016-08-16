//
// Created by Yossi Barel on 07/08/16.
//

#ifndef DRUMMAP_YBSUPERPOWEREDWHOOSH_H
#define DRUMMAP_YBSUPERPOWEREDWHOOSH_H

#include "SuperpoweredWhoosh.h"
#include "Mixable.h"

class YBSuperpoweredWhoosh : public Mixable
{

public:
	YBSuperpoweredWhoosh(int sampleRate)
	{

	}

	void enable(bool flag);

	void setWet(float wet);

	void setFrequency(float freq);

	bool getEnabled();

	double getWet();

private:
	SuperpoweredWhoosh* mSuperpoweredWhoosh;
};


#endif //DRUMMAP_YBSUPERPOWEREDWHOOSH_H
