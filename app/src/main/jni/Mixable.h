//
// Created by Yossi Barel on 07/08/16.
//

#ifndef DRUMMAP_NEWCPPCLASS_H
#define DRUMMAP_NEWCPPCLASS_H


class Mixable
{
public:
	void setMixFx(float value)
	{
		mMix = value;
	}

private:
	float mMix;
};


#endif //DRUMMAP_NEWCPPCLASS_H
