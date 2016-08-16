//
// Created by Yossi Barel on 07/04/16.
//

#ifndef DRUMMAP_XYCONTROLER_H
#define DRUMMAP_XYCONTROLER_H

#include "XYControlListener.h"
#include "Channel.h"

#define C1X 0
#define C1Y 1
#define C2X 2
#define C2Y 3
typedef struct XYFx
{
	int channelIndex;
	int keyFx;
	int keyFxParam;


	double start;
	double end;
} XYFx;

class XYControler
{
public:

	XYControler(XYControlerListener *listener);

	void addFx(string indexControl, int control, int indexChannel, int keyfx, int keyFxParam, double d, double d1, bool b);

	void removeFx(string indexControler, int control);

	void setControlValue(int contol, float val);

	ModIndex getModState(string modIndex);

	void clearAllSendMod(int channel);

private:
	XYControlerListener *mListener;
	map<string, XYFx *> mapCtrlXY1[6];



};


#endif //DRUMMAP_XYCONTROLER_H
