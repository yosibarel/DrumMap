//
// Created by Yossi Barel on 06/04/16.
//

#ifndef DRUMMAP_LFO_H
#define DRUMMAP_LFO_H

#include "SuperpoweredAdvancedAudioPlayer.h"
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <android/log.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>
#include <math.h>
#include <pthread.h>
#include "SuperpoweredSimple.h"
#include "SuperpoweredAdvancedAudioPlayer.h"
#include "SuperpoweredAndroidAudioIO.h"
#include "SuperpoweredMixer.h"
#include <iostream>
#include <map>
#include <vector>
#include <time.h>
#include <string.h>
#include "WaveSinusCreator.h"
#include "WaveSawCreator.h"
#include "WaveRectangleCreator.h"
#include "WaveTriangleCreator.h"
#include "ChannelListener.h"

#include "SuperpoweredFilter.h"

#include "SuperpoweredRecorder.h"
#include "SuperpoweredWhoosh.h"
#include "YBSuperpoweredGate.h"
#include "YBSuperpoweredWhoosh.h"

#include "YBSuperpoweredEcho.h"
#include "YBSuperpoweredFlanger.h"
#include "YBSuperpoweredLimiter.h"
#include "YBSuperpoweredReverb.h"
#include "YBSuperpoweredRoll.h"

using namespace std;
enum EnumWaveForm
{
	SINUS, SAW, RECTANGLE, TRIANGLE
};

enum ModIndex
{
	NON_MOD = -1, LFO_1, LFO_2, LFO_1_2, CONTROL_X1, CONTROL_Y1, CONTROL_X2, CONTROL_Y2, SENSOR_X, SENSOR_Y, ADSR_ENV, CURVE_1, CURVE_2, CLEAR_ALL = 100
};

class LFO
{
public:
	LFO(int sampleRate, int bufferSize);

	~LFO();

	double proccess(double currentMS);

	void setRate(double rate);

	void setAmplitude(double amp);

	void setLfoWave(int type);

	int getLfoWave();

	double getAmplitude();

	double getRate();

	double getRateValue()
	{
		return mRateValue;
	}

	void setBpm(double bpm);

private:
	WaveCreator *mWaveCurrent;
	WaveSinusCreator *mWaveSinus;
	WaveSawCreator *mWaveSaw;
	WaveTriangleCreator *mWaveTriangle;
	WaveRectangleCreator *mWaveRectangle;
	EnumWaveForm mWaveFrom;
	double mRate, mAmplitude;


	double mRateValue;
	double mBpm;

};


#endif //DRUMMAP_LFO_H
