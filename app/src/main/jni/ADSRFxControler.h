//
// Created by Yossi Barel on 17/04/16.
//

#ifndef DRUMMAP_ADSRFXCONTROLER_H
#define DRUMMAP_ADSRFXCONTROLER_H


#include "SeqControler.h"

using namespace std;
typedef struct ADSRFx
{
	int keyFx;
	int keyFxParam;
	double start;
	double end;
} ADSRFx;

class ADSRFxControler
{
public:

	ADSRFxControler(int bufferSize, int sampleRate,MODFxListener* listener);

	void proccess();

	void addFx(string index, int keyFx, int keyFxParam, double d, double d1, bool b);

	void removeFx(int keyFx, string modIndex);

	void keyDown();

	void keyRelese();


	void setADSRValue(int param, double val);

	double getParamValue(int param);

	ModIndex getModState(string modIndex);

	void removeAll();

private:
	ADSRControler *mADSR;
	MODFxListener *mListener;
	map<string, ADSRFx *> mADSRSend;
	int mSampleRate;
	int mBufferSize;
};


#endif //DRUMMAP_ADSRFXCONTROLER_H
