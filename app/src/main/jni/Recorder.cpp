//
// Created by Yossi Barel on 09/04/16.
//

#include "Recorder.h"

#include <unistd.h>

static void pplayerEventCallback(void *clientData, SuperpoweredAdvancedAudioPlayerEvent event, void *value)
{
	((Recorder *) clientData)->playerEventCallback(event, value);
}

Recorder::Recorder(const char *tempFile, int sampleRate, int bufferSize)
{

	mSampleRate = sampleRate;
	mBuffer = (float *) malloc((bufferSize) * sizeof(float) * 2);
	mRecorder = new SuperpoweredRecorder(tempFile, sampleRate);
	mPlayer = new SuperpoweredAdvancedAudioPlayer(this, pplayerEventCallback, (int) mSampleRate, 0);
	mFileRecord = NULL;
}

Recorder::~Recorder()
{
	reset();
	delete (mPlayer);
	delete (mRecorder);
	free(mBuffer);
}

void Recorder::playerEventCallback(SuperpoweredAdvancedAudioPlayerEvent event, void *value)
{
	if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess)
	{
		mIsCanPlay = true;
		mPlayer->setPosition(0, true, false);
	} else if (event == SuperpoweredAdvancedAudioPlayerEvent_EOF)
	{
		mPosition = 0;
		mRecordState = NONE;
		mPlayer->setPosition(0, true, false);
	} else if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadError)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "ndk2", "error load file -  %s", (const char *) value);
	}
}

void Recorder::startRecord(const char *fileName)
{
	mPosition = 0;
	mFileRecord = createWAV(fileName, mSampleRate, 2);
	mFileName = string(fileName);


	//mRecorder->start(fileName);

	mRecordState = RECORD;

}

void Recorder::stopRecord()
{
	mRecordState = NONE;
	long length = 0;
	//mRecorder->stop();
	if (mFileRecord != NULL)
	{
		length = ftell(mFileRecord);
		closeWAV(mFileRecord);
	}

	mFileRecord = NULL;
	/*FILE *fp = fopen(mFileName, "rb");

	fseek(fp, 0, SEEK_END);    // Go to end
	long length = ftell(fp);
	fclose(fp);*/


	if (length > 0)
		mPlayer->open(mFileName.c_str(), 0, (int) length);

}

bool Recorder::startPlay()
{


	if (mIsCanPlay)
	{
		mPosition = 0;
		mPlayer->play(false);
		mRecordState = PLAY;
		return true;
	}
	return false;

}

void Recorder::stopPlay()
{

	mPlayer->pause();
	mRecordState = NONE;
}

bool Recorder::proccess(short int *audio, int numberOfSample)
{
	if (mRecordState == RECORD)
	{
		mPosition += ((double) numberOfSample / mSampleRate) * 1000.0;
		//SuperpoweredShortIntToFloat(audio, mBuffer, numberOfSample);
		SuperpoweredShortIntToFloat(audio, mBuffer, numberOfSample);
		fwrite(audio, sizeof(short int), numberOfSample * 2, mFileRecord);
		return true;
	} else if (mRecordState == PLAY)
	{
		mPosition += ((double) numberOfSample / mSampleRate) * 1000.0;
		mPlayer->process(mBuffer, false, numberOfSample);
		SuperpoweredFloatToShortInt(mBuffer, audio, numberOfSample);
		return true;
	}
	return false;
}

void Recorder::reset()
{


	if (mRecordState == RECORD)
	{
		mRecorder->stop();
		if (mFileRecord != NULL)
			closeWAV(mFileRecord);
	}
	if (mRecordState == PLAY)
		mPlayer->pause();
	mPosition = 0;
	mRecordState = NONE;
	mIsCanPlay = false;
}

const void *Recorder::getBuffer()
{
	return mBuffer;
}
