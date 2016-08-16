//
// Created by Yossi Barel on 02/08/16.
//

#ifndef DRUMMAP_FX_H
#define DRUMMAP_FX_H

#include "Filter.h"
using namespace std;

class Fx
{
public:
	Fx(SuperpoweredFX **fx, string name,int id);

	~Fx();
	SuperpoweredFX *getFx();

	void setName(string name);

	string getName();

	void setId(int id);

	int getId();

private :
	SuperpoweredFX **mFx;
	string mName;
	int mId;
};


#endif //DRUMMAP_FX_H
