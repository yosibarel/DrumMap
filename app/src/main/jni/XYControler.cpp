//
// Created by Yossi Barel on 07/04/16.
//

#include "XYControler.h"

XYControler::XYControler(XYControlerListener *listener)
{
	mListener = listener;
}

void XYControler::setControlValue(int contol, float val)
{
	for (map<string, XYFx *>::iterator iter = mapCtrlXY1[contol].begin(); iter != mapCtrlXY1[contol].end(); ++iter)
	{
		XYFx *fx = iter->second;
		mListener->setFx(fx->channelIndex, fx->keyFx, fx->keyFxParam, fx->start + val * (fx->end - fx->start));
	}
}

void XYControler::addFx(string indexControl, int control, int indexChannel, int keyfx, int keyFxParam, double start, double end, bool isUpdate)
{
	if (!isUpdate)
	{
		XYFx *fx = new XYFx();
		fx->channelIndex = indexChannel;
		fx->keyFxParam = keyFxParam;
		fx->keyFx = keyfx;
		fx->start = start;
		fx->end = end;
		mapCtrlXY1[control][indexControl] = fx;
	} else
	{
		mapCtrlXY1[control][indexControl]->start = start;
		mapCtrlXY1[control][indexControl]->end = end;
	}
}

void XYControler::removeFx(string indexControler, int control)
{
	XYFx *fx = mapCtrlXY1[control][indexControler];
	mapCtrlXY1[control].erase(indexControler);
	delete (fx);
}

ModIndex XYControler::getModState(string modIndex)
{

	for (int i = 0; i < 6; ++i)
	{
		if (mapCtrlXY1->find(modIndex) != mapCtrlXY1->end())
			return (ModIndex) (i + CONTROL_X1);
	}
	return NON_MOD;
}

void XYControler::clearAllSendMod(int channel)
{
	for (int i = 0; i < 6; ++i)
		for (map<string, XYFx *>::iterator iter = mapCtrlXY1[i].begin(); iter != mapCtrlXY1[i].end(); ++iter)
		{
			if (iter->second->channelIndex == channel)
			{
				delete (iter->second);
				mapCtrlXY1[i].erase(iter);
			}
		}




}
