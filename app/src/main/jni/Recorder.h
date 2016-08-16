//
// Created by Yossi Barel on 09/04/16.
//

#ifndef DRUMMAP_RECORDER_H
#define DRUMMAP_RECORDER_H

#include "XYControler.h"

enum RecordState
{
	NONE, RECORD, PLAY
};

class Recorder
{
public:

	Recorder(const char *tempFile, int sampleRate, int bufferSize);

	~Recorder();

	void playerEventCallback(SuperpoweredAdvancedAudioPlayerEvent event, void *value);

	void startRecord(const char *fileName);

	void stopRecord();

	bool startPlay();

	void stopPlay();

	bool proccess(short int *audio, int numberOfSample);

	double getPositionMs()
	{
		return mPosition;
	}

	RecordState getState()
	{
		return mRecordState;
	}

	void reset();

	const void *getBuffer();

private:

	FILE * mFileRecord;
	float *mBuffer;
	string mFileName;
	int mSampleRate;
	double mPosition;
	RecordState mRecordState;
	SuperpoweredRecorder *mRecorder;
	SuperpoweredAdvancedAudioPlayer *mPlayer;

	bool mIsCanPlay;
};


#endif //DRUMMAP_RECORDER_H
