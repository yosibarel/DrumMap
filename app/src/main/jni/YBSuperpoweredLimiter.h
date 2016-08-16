//
// Created by Yossi Barel on 07/08/16.
//

#ifndef DRUMMAP_YBSUPERPOWEREDLIMITER_H
#define DRUMMAP_YBSUPERPOWEREDLIMITER_H

#include "SuperpoweredLimiter.h"
#include "Mixable.h"
class YBSuperpoweredLimiter :public Mixable
{
public:
	YBSuperpoweredLimiter(int sampleRate)
	{
		mSuperpoweredLimiter=new SuperpoweredLimiter(sampleRate);
	}
~YBSuperpoweredLimiter()
{
	delete mSuperpoweredLimiter;
}
	void setCeilingDb(double db);

	void setThresholdDb(double db);

	void setReleaseSec(double rel);

	void enable(bool flag);

	bool getEnabled();

	double getcCeilingDb();

	double getThresholdDb();

	double getReleaseSec();

private:
	SuperpoweredLimiter* mSuperpoweredLimiter;
};


#endif //DRUMMAP_YBSUPERPOWEREDLIMITER_H
