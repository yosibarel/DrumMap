//
// Created by Yossi Barel on 17/04/16.
//

#ifndef DRUMMAP_ADSRCONTROLER_H
#define DRUMMAP_ADSRCONTROLER_H

#include <jni.h>

#define A 0
#define D 1
#define S 2
#define R 3

class ADSRControler
{
public:
	ADSRControler(int bufferSize, int sampleRate);

	double proccess();

	void keyDown();

	void keyRelese();

	void setADSRValue(int param, double val);

	double getParamValue(int param);

private :
	double mADSR[4];
	double mCurrentTime;
	int mBufferSize;
	int mSampleRate;
	int mCurrentIndex;
	double mProccessTimeUnit;
};


#endif //DRUMMAP_ADSRCONTROLER_H
