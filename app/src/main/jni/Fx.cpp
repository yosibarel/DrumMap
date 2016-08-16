//
// Created by Yossi Barel on 02/08/16.
//

#include "Fx.h"

Fx::Fx(SuperpoweredFX **fx, string name, int id)
{
	mFx = fx;
	mName = name;
	mId = id;
}

Fx::~Fx()
{
	if (*mFx != NULL)
		delete(*mFx);
	*mFx = NULL;
}

SuperpoweredFX *Fx::getFx()
{
	return *mFx;
}

int Fx::getId()
{
	return mId;
}

string Fx::getName()
{
	return mName;
}

void Fx::setId(int id)
{
	mId = id;
}

void Fx::setName(string name)
{
	mName = name;
}
