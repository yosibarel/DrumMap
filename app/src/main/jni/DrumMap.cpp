//
// Created by Yossi Barel on 28/03/16.
//

#include "DrumMap.h"
#include <unistd.h>

const int DrumMap::mColors[NUM_CHANNELS] = {0xff6666ff, 0xff6699ff, 0xff66ccff, 0xff66ffff, 0xff66ffcc, 0xff66ff66, 0xff66cc00, 0xffffff00, 0xffffcc00, 0xffff6600, 0xffff0000, 0xffff0066, 0xffff00cc, 0xffff00ff, 0xff6600cc, 0xff6600ff};

void editWaveChannelCallback(void *clientData, SuperpoweredAdvancedAudioPlayerEvent event, void *value)
{
	((DrumMap *) clientData)->playerEventCallback(event, value);
}


double now_ms(void)
{

	struct timespec res;
	clock_gettime(CLOCK_REALTIME, &res);
	return 1000.0 * res.tv_sec + (double) res.tv_nsec / 1e6;

}

static bool audioProcessing(void *clientdata, short int *audioIO, int numberOfSamples, int samplerate)
{
	return ((DrumMap *) clientdata)->process(audioIO, numberOfSamples, samplerate);
}

static double position = 0;
static float *tempBuffer[4];


void *test(void *s)
{
	long i = 0;
	while (i < 1000000000)
	{
		i++;
		double start = now_ms();
		double d;
		for (int j = 0; j < 500; ++j)
		{
			d = 50 * 60.0 / 0.2 * sin(370.0) + abs(-j) * 40.0;
			double f = d;
		}
		double end = now_ms() - start;
		__android_log_print(ANDROID_LOG_DEBUG, "ndk", "test time %f", end);
		usleep(50000);

	}
}

void *lfoThread(void *pdrumMap)
{
	DrumMap *drumMap = (DrumMap *) pdrumMap;

	double lfo1;
	double lfo2;
	while (drumMap->getIsLfoActive())
	{


		usleep(50000);
	}
}

double DrumMap::bpmToMs()
{
	return (60000.0 / mBpm) * (2.0 / QUANTIZE[mQuntizeIndex]);
}

int DrumMap::getIndexQuantize()
{
	return (int) (mMainPosition / bpmToMs());
}

double DrumMap::QUANTIZE[8] = {0.125, 0.25, 0.5, 1.0, 2.0, 4.0, 8.0, 16.0};

bool DrumMap::process(short int *audioIO, unsigned int numberOfSamples, int sampleRate)
{


	pthread_mutex_lock(&mutex);

	double startProcessMs = now_ms();
	mMainPosition += ((double) numberOfSamples * 1000.0) / mSampleRate;
	int currentQuantize = getIndexQuantize();
	if (mState == NORML && mRecorder->proccess(audioIO, numberOfSamples))
	{
		pthread_mutex_unlock(&mutex);
		return true;
	}
	mMainGridControler->process();
	int numChannelProccessing = 0;
	for (int i = 0; i < NUM_CHANNELS; ++i)
		mbufferProccessing[i / 4][i % 4] = NULL;

	int indexProccessOrder = 0;
	bool isDrawWaveForm = false;
	for (int i = 0; i < NUM_CHANNELS; ++i)
	{
		mChannels[i]->proccessPiano();
	}
	for (int k = 0; k < NUM_CHANNELS; ++k)
	{
		mbufferProccessing[k / 4][k % 4] = NULL;
	}

	for (int i = 0; i < NUM_CHANNELS; ++i)
	{
		if (mLastQuantize != currentQuantize)
		{
			if (currentChannelProccessing[i] == QASK_START)
				currentChannelProccessing[i] = QSTART;
			if (currentChannelProccessing[i] == QASK_STOP)
				currentChannelProccessing[i] = QSTOP;

		}
		if (currentChannelProccessing[i] == QSTART || currentChannelProccessing[i] == QASK_STOP)
		{
			float *buffer = mChannels[i]->proccess((int) mMainPosition);

			if (buffer != NULL)
			{
				mProccessIndexOrder[indexProccessOrder++] = i;

				mbufferProccessing[numChannelProccessing / 4][numChannelProccessing % 4] = buffer;
				mInputLevel[numChannelProccessing / 4][(numChannelProccessing % 4) * 2] = mChannels[i]->getLeft();
				mInputLevel[numChannelProccessing / 4][(numChannelProccessing % 4) * 2 + 1] = mChannels[i]->getRight();
				numChannelProccessing++;


				//process send fx
				if (mChannels[i]->isSendFx1())
					mFxControler1->process(buffer, mChannels[i]->getSendFx1());
			}
		}
	}

	tempBuffer[0] = tempBuffer[1] = tempBuffer[2] = tempBuffer[3] = NULL;
	int limit = (16 - (16 - numChannelProccessing) + 3) / 4;
	for (int i = 0; i < limit; ++i)
	{
		mMixer->process(mbufferProccessing[i], mSubStereoBuffersOutput[i], mInputLevel[i], mOutputLevel, mInputMeters[i], NULL, numberOfSamples);


		tempBuffer[i] = mSubStereoBuffersOutput[i][0];
	}
	for (int j = 0; j < numChannelProccessing; ++j)
	{
		mChannels[mProccessIndexOrder[j]]->mViewMeterLeft = mInputMeters[j / 4][(j * 2) % 8] * mChannels[mProccessIndexOrder[j]]->getLeft();
		mChannels[mProccessIndexOrder[j]]->mViewMeterRight = mInputMeters[j / 4][(j * 2) % 8 + 1] * mChannels[mProccessIndexOrder[j]]->getRight();
	}
	if (numChannelProccessing != 0)
		mMainMixer->process(tempBuffer, mMainStereoBufferOutput, mMainInputLevel, mOutputLevel, mInputMeters[0], NULL, numberOfSamples);


	position += ((double) numberOfSamples / (double) mSampleRate) * 1000;
	if (mSoloChannel != -1)
	{
		SuperpoweredFloatToShortInt(mChannels[mSoloChannel]->getBuffer(), audioIO, numberOfSamples);
	} else
	{
		SuperpoweredFloatToShortInt(mMainStereoBufferOutput[0], audioIO, numberOfSamples);
	}
	if (mState == STATE_RECORD)
		mRecorder->proccess(audioIO, numberOfSamples);
	mLastQuantize = currentQuantize;
	__android_log_print(ANDROID_LOG_DEBUG, "processtime", "process time = %f", now_ms() - startProcessMs);
	pthread_mutex_unlock(&mutex);

	return numChannelProccessing != 0 || mState == STATE_RECORD;
}

void DrumMap::onPianoKeyDown(int index)
{
	currentChannelProccessing[index] = QSTART;
}

void DrumMap::onPianoKeyRelsese(int index)
{
	currentChannelProccessing[index] = QSTOP;
}

void DrumMap::onNeedStopProccess(int index)
{
	currentChannelProccessing[index] = QSTOP;
}

DrumMap::DrumMap(int sampleRate, int bufferSize, void (*funcCallBack)(CallBackJavaTypeMessage, int), const char *tempRecordFile)
{
	pthread_mutex_init(&mutex, NULL);
	mSampleRate = sampleRate;
	mBufferSize = bufferSize;
	mChannels = new Channel *[NUM_CHANNELS];
	for (int i = 0; i < NUM_CHANNELS; ++i)
	{
		mChannels[i] = new Channel(bufferSize, sampleRate, i, this);
		mChannels[i]->setChannelColor(mColors[i]);
		currentChannelProccessing[i] = QSTOP;
	}
	mRecorder = new Recorder(tempRecordFile, sampleRate, bufferSize);

	mMainMixer = new SuperpoweredStereoMixer();
	mSubStereoBuffersOutput[0][0] = (float *) malloc((mBufferSize) * sizeof(float) * 2);
	mSubStereoBuffersOutput[0][1] = NULL;
	mSubStereoBuffersOutput[1][0] = (float *) malloc((mBufferSize) * sizeof(float) * 2);
	mSubStereoBuffersOutput[1][1] = NULL;
	mSubStereoBuffersOutput[2][0] = (float *) malloc((mBufferSize) * sizeof(float) * 2);
	mSubStereoBuffersOutput[2][1] = NULL;
	mSubStereoBuffersOutput[3][0] = (float *) malloc((mBufferSize) * sizeof(float) * 2);
	mSubStereoBuffersOutput[3][1] = NULL;


	mMainStereoBufferOutput[0] = (float *) malloc((mBufferSize) * sizeof(float) * 2);
	mMainStereoBufferOutput[1] = NULL;

	mMixer = new SuperpoweredStereoMixer();

	mMainInputLevel[0] = 1;
	mMainInputLevel[1] = 1;
	mMainInputLevel[2] = 1;
	mMainInputLevel[3] = 1;
	mMainInputLevel[4] = 1;
	mMainInputLevel[5] = 1;
	mMainInputLevel[6] = 1;
	mMainInputLevel[7] = 1;

	mInputLevel[0][0] = 1;
	mInputLevel[0][1] = 1;
	mInputLevel[0][2] = 1;
	mInputLevel[0][3] = 1;
	mInputLevel[0][4] = 1;
	mInputLevel[0][5] = 1;
	mInputLevel[0][6] = 1;
	mInputLevel[0][7] = 1;
	mInputLevel[0][0] = 1;
	mInputLevel[0][1] = 1;
	mInputLevel[0][2] = 1;
	mInputLevel[0][3] = 1;
	mInputLevel[0][4] = 1;
	mInputLevel[0][5] = 1;
	mInputLevel[0][6] = 1;
	mInputLevel[0][7] = 1;
	mInputLevel[1][0] = 1;
	mInputLevel[1][1] = 1;
	mInputLevel[1][2] = 1;
	mInputLevel[1][3] = 1;
	mInputLevel[1][4] = 1;
	mInputLevel[1][5] = 1;
	mInputLevel[1][6] = 1;
	mInputLevel[1][7] = 1;
	mInputLevel[2][0] = 1;
	mInputLevel[2][1] = 1;
	mInputLevel[2][2] = 1;
	mInputLevel[2][3] = 1;
	mInputLevel[2][4] = 1;
	mInputLevel[2][5] = 1;
	mInputLevel[2][6] = 1;
	mInputLevel[2][7] = 1;
	mInputLevel[3][0] = 1;
	mInputLevel[3][1] = 1;
	mInputLevel[3][2] = 1;
	mInputLevel[3][3] = 1;
	mInputLevel[3][4] = 1;
	mInputLevel[3][5] = 1;
	mInputLevel[3][6] = 1;
	mInputLevel[3][7] = 1;


	mOutputLevel[0] = 1;
	mOutputLevel[1] = 1;
	mMainPosition = 0.0;
	mBpm = 140;
	mQuntizeIndex = 0;
	mSoloChannel = -1;
	mFxControler1 = new FxControler(mSampleRate, mBufferSize);
	mFxControler2 = new FxControler(mSampleRate, mBufferSize);

	mTempEditBuffer = (float *) malloc(mBufferSize * sizeof(float) * 2);
	mIsQuantize = false;
	mEditPlayer = new SuperpoweredAdvancedAudioPlayer(this, editWaveChannelCallback, mSampleRate, 0);
	xyControler = new XYControler(this);
	mMainGridControler = new MainGridControler(bufferSize, sampleRate, this);
	mAudioSystem = new SuperpoweredAndroidAudioIO(sampleRate, bufferSize, true, true, audioProcessing, this, SL_ANDROID_RECORDING_PRESET_VOICE_RECOGNITION, bufferSize * 2);
	mEditBuffer = NULL;
	mFuncCallBack = funcCallBack;
	mIsLfoActive = true;
	mState = NORML;
	//pthread_t pt;
	//pthread_create(&pt, NULL, test, NULL);

}

DrumMap::~DrumMap()
{

	free(mTempEditBuffer);
	mAudioSystem->stop();
	mIsLfoActive = false;
	for (int i = 0; i < NUM_CHANNELS; ++i)
	{
		delete(mChannels[i]);
	}
	delete(mRecorder);
	delete(mChannels);
	delete(mMixer);
	delete(mMainMixer);
	delete(mAudioSystem);
	delete(xyControler);
	delete(mMainGridControler);
}



void DrumMap::startPlayChannelCallBack(int indexChannel)
{
	//mChannels[indexChannel]->setPianoPositionBeat(0);
	playPiano(indexChannel);
}

void DrumMap::stopPlayChannelCallBack(int indexChannel)
{
	pausePiano(indexChannel);


}

void DrumMap::setLFORateWave(int indexChannel, int lfoType, double rate)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setLFORateWave(lfoType, rate);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::setLFOTypeWave(int indexChannel, int lfoType, int waveType)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setLFOTypeWave(lfoType, waveType);
	pthread_mutex_unlock(&mutex);
}


void DrumMap::setLFOAmplitudeWave(int indexChannel, int lfoType, double amplitude)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setLFOAmplitudeWave(lfoType, amplitude);
	pthread_mutex_unlock(&mutex);

}

/*void DrumMap::setLFOPresentWave(int indexChannel, double present)
{

	//mChannels[indexChannel]->setLFOPresentWave(present);
}*/
void DrumMap::setLFOPresentWave(int indexChannel, double present)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setLFOPresentWave(present);
	pthread_mutex_unlock(&mutex);
}


void DrumMap::setEnvADSR(int envType, int indexChannel, int type, double value)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setEnvADSR(envType, type, value);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::keyDown(int indexChannel)
{
	pthread_mutex_lock(&mutex);

	if (mIsQuantize && currentChannelProccessing[indexChannel] != QSTART)
		currentChannelProccessing[indexChannel] = QASK_START;
	else
		currentChannelProccessing[indexChannel] = QSTART;
	mChannels[indexChannel]->keyDown();
	pthread_mutex_unlock(&mutex);
}

void DrumMap::keyRelese(int indexChannel)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->keyRelese();
	pthread_mutex_unlock(&mutex);

}

void DrumMap::setFx(int indexChannel, int fxFey, int fxKeyParam, float value)
{

	//pthread_mutex_lock(&mutex);
	if (indexChannel == -1)
		mFxControler1->setFx(fxFey, fxKeyParam, value);
	else if (indexChannel == -2)
		mFxControler1->setFx(fxFey, fxKeyParam, value);
	else
		mChannels[indexChannel]->setFx(fxFey, fxKeyParam, value);
//	pthread_mutex_unlock(&mutex);
}

bool DrumMap::addFx(int indexChannel, int fxType)
{
	bool res;
	pthread_mutex_lock(&mutex);
	if (indexChannel == -1)
		res = mFxControler1->addFx(fxType);
	else if (indexChannel == -2)
		res = mFxControler2->addFx(fxType);
	else
		res = mChannels[indexChannel]->addFx(fxType);
	pthread_mutex_unlock(&mutex);
	return res;
}

void DrumMap::openChannelFile(int channelIndex, const char *filePath, long len)
{
	pthread_mutex_lock(&mutex);
	mChannels[channelIndex]->openChannelFile(filePath, len);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::onLoadFileEvent(SuperpoweredAdvancedAudioPlayerEvent event, int indexChannel)
{

	if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess)
	{
		mFuncCallBack(FILE_LOADED_, indexChannel);
	} else if (event == SuperpoweredAdvancedAudioPlayerEvent_EOF)
	{

		currentChannelProccessing[indexChannel] = QSTOP;


		mFuncCallBack(EOF_, indexChannel);
	} else if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadError)
	{
		mFuncCallBack(ERROR_LOAD_, indexChannel);
	}

}

double DrumMap::getPositionPrecent(int index)
{
	return mChannels[index]->getPositionPrecent();
}

long DrumMap::getDurationMs(int channelIndex)
{
	return mChannels[channelIndex]->getDurationMs();
}

void DrumMap::loopBetween(jint channelIndex, jdouble start, jdouble end, jboolean startPlay)
{
	pthread_mutex_lock(&mutex);
	mChannels[channelIndex]->loopBetween(start, end, startPlay);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::setReverse(int indexChannel, bool flag)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setReverse(flag);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::sendFxToLFO(string indexLfo, int indexChannel, int fxType, int keyEffectParam, int lfo1, int lfo2, double start, double end, bool isUpdate)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->sendFxToLFO(indexLfo, fxType, keyEffectParam, lfo1, lfo2, start, end, isUpdate);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::sendFxToControlXY(string mIndexControl, int control, int indexChannel, int mFxType, int keyEffectParam, double start, double end, bool isUpdate)
{
	pthread_mutex_lock(&mutex);
	xyControler->addFx(mIndexControl, control, indexChannel, mFxType, keyEffectParam, start, end, isUpdate);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::setXYControlValue(int ctrl, double val)
{
	pthread_mutex_lock(&mutex);
	xyControler->setControlValue(ctrl, val);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::scretch(double val)
{
	pthread_mutex_lock(&mutex);
	for (int i = 0; i < NUM_CHANNELS; ++i)
	{
		if (scratchChannels[i])
			mChannels[i]->scretch(val);
	}
	pthread_mutex_unlock(&mutex);
}

void DrumMap::resetRecord()
{
	pthread_mutex_lock(&mutex);
	mRecorder->reset();
	pthread_mutex_unlock(&mutex);
}

void DrumMap::startRecord(const char *fileName, bool needMutex)
{
	if (needMutex)
		pthread_mutex_lock(&mutex);
	mRecorder->startRecord(fileName);
	if (needMutex)
		pthread_mutex_unlock(&mutex);
}

void DrumMap::stopRecord()
{
	pthread_mutex_lock(&mutex);
	mRecorder->stopRecord();
	mState = NORML;
	pthread_mutex_unlock(&mutex);
}

bool DrumMap::startPlayRecord()
{
	return mRecorder->startPlay();
}

void DrumMap::stopPlayRecord()
{
	pthread_mutex_lock(&mutex);
	mRecorder->stopPlay();
	pthread_mutex_unlock(&mutex);
}

double DrumMap::getRecordPosition()
{
	return mRecorder->getPositionMs();
}

void DrumMap::setScratchEnable(int channelIndex, bool enable)
{
	pthread_mutex_lock(&mutex);
	scratchChannels[channelIndex] = enable;
	pthread_mutex_unlock(&mutex);
}

void DrumMap::setStopPlayOnRelease(int index, bool isStop)
{
	pthread_mutex_lock(&mutex);
	mChannels[index]->setStopPlayOnRelease(isStop);
	pthread_mutex_unlock(&mutex);

}

bool DrumMap::getStopPlayOnRelease(int indexChannel)
{
	return mChannels[indexChannel]->getStopPlayOnRelease();
}

void DrumMap::setPitchBend(double pitch)
{
	pthread_mutex_lock(&mutex);
	for (int i = 0; i < NUM_CHANNELS; ++i)
	{
		if (scratchChannels[i])
			mChannels[i]->setPitchBand(pitch);

	}
	pthread_mutex_unlock(&mutex);

}

void DrumMap::setBpm(double bpm)
{
	pthread_mutex_lock(&mutex);
	mBpm = bpm;
	for (int i = 0; i < NUM_CHANNELS; ++i)
	{
		mChannels[i]->setBpm(bpm);

	}
	mMainGridControler->setBpm(mBpm);
	mFxControler2->setBpm(bpm);
	mFxControler1->setBpm(mBpm);
	pthread_mutex_unlock(&mutex);

}

void DrumMap::enableSequencerPattern(int indexChannel, int indexPattern, bool enable)
{
	pthread_mutex_lock(&mutex);

	mChannels[indexChannel]->enableSequencerPattern(indexPattern, enable);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::setValueSequencerPattern(int indexChannel, int indexPattern, int indexValue, double val)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setValueSequencerPattern(indexPattern, indexValue, val);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::enableSequencer(int indexChannel, bool enable)
{
	pthread_mutex_lock(&mutex);
	if (enable)
	{

		if (mIsQuantize)

			currentChannelProccessing[indexChannel] = QASK_START;
		else
			currentChannelProccessing[indexChannel] = QSTART;
	} else
		currentChannelProccessing[indexChannel] = QSTOP;
	mChannels[indexChannel]->enableSequencer(enable);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::setSeqRate(int indexChannel, int indexValue)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setSeqRate(indexValue);
	pthread_mutex_unlock(&mutex);
}

double DrumMap::getSeqPattern(int indexChannel, int indexPattern, int indexValue)
{
	return mChannels[indexChannel]->getSeqPattern(indexPattern, indexValue);

}

int DrumMap::getSeqIndexPlaying(int indexChannel)
{
	return mChannels[indexChannel]->getSeqIndexPlaying();
}

void DrumMap::resetSeq(int indexChannel, int indexValue)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->resetSeq(indexValue);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::setPitchChannel(int indexChannel, double pitch)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setPitch(pitch);
	pthread_mutex_unlock(&mutex);
}

vector<int> DrumMap::getActiveEffect(int indexChannel)
{
	if (indexChannel == -1)
		return mFxControler1->getActiveEffect();
	else if (indexChannel == -2)
		return mFxControler2->getActiveEffect();

	return mChannels[indexChannel]->getActiveEffect();
}

void DrumMap::setPlayFromStartLoop(int indexChannel, bool isPlayFromStartLoop)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setPlayFromStartLoop(isPlayFromStartLoop);
	pthread_mutex_unlock(&mutex);
}

bool DrumMap::getPlayFromStartLoop(int indexChannel)
{

	return mChannels[indexChannel]->getPlayFromStartLoop();

}

void DrumMap::setLoop(int indexChannel, bool isLoop)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setLoop(isLoop);
	pthread_mutex_unlock(&mutex);
}


void DrumMap::startGlobalRecord(const char *filePath)
{
	pthread_mutex_lock(&mutex);
	mState = STATE_RECORD;
	startRecord(filePath, false);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::enableFilter(int indexChannel, bool enable)
{
	mChannels[indexChannel]->enableFilter(enable);
}

void DrumMap::sendFxToADSR(int indexChannel, string mIndexControl, int fxType, int keyEffectParam, double start, double end, bool isUpdate)
{
	mChannels[indexChannel]->sendFxToADSR(mIndexControl, fxType, keyEffectParam, start, end, isUpdate);
}

void DrumMap::sendFxToCurve(string indexString, int indexChannel, int curve, int fxType, int keyEffectParam, double start, double end, bool isUpdate)
{
	mChannels[indexChannel]->sendFxToCurve(indexString, curve, fxType, keyEffectParam, start, end, isUpdate);
}

void DrumMap::setVolume(int indexChannel, double vol)
{
	mChannels[indexChannel]->setMainVolume(vol);
}

double DrumMap::getFxValue(int indexChannel, int fxType, int fxKeyParam)
{
	if (indexChannel == -1)
		return mFxControler1->getFxValue(fxType, fxKeyParam);
	else if (indexChannel == -2)
		return mFxControler2->getFxValue(fxType, fxKeyParam);
	return mChannels[indexChannel]->getFxValue(fxType, fxKeyParam);
}

double DrumMap::getEnvADSR(int type, int indexChannel, int param)
{
	return mChannels[indexChannel]->getEnvADSR(type, param);
}

int DrumMap::getLFOTypeWave(int indexChannel, int lfoType)
{
	return mChannels[indexChannel]->getLFOTypeWave(lfoType);
}

double DrumMap::getLFOAmplitudeWave(int indexChannel, int lfoType)
{
	return mChannels[indexChannel]->getLFOAmplitudeWave(lfoType);
}

double DrumMap::getLFORateWave(int indexChannel, int lfo)
{
	return mChannels[indexChannel]->getLFORateWave(lfo);
}

double DrumMap::getLfoWavePresent(int indexChannel)
{
	return mChannels[indexChannel]->getLfoWavePresent();
}

double DrumMap::getVolume(int indexChannel)
{
	return mChannels[indexChannel]->getVolume();
}

int DrumMap::getModState(int indexChannel, string indexMod)
{
	if (indexChannel < 0)
		return -1;
	ModIndex mod = xyControler->getModState(indexMod);
	if (mod == NON_MOD)
		mod = mChannels[indexChannel]->getModState(indexMod);
	return (int) mod;
}

void DrumMap::removeModFx(int indexChannel, string indexMod, int keyEffectParam, int mod)
{
	pthread_mutex_lock(&mutex);
	if (mod == CLEAR_ALL)
	{
		xyControler->clearAllSendMod(indexChannel);
		mChannels[indexChannel]->clearAllSendMod();
	} else
	{
		if (mod >= CONTROL_X1 && mod <= SENSOR_Y)
			xyControler->removeFx(indexMod, mod - (int) CONTROL_X1);
		else
			mChannels[indexChannel]->removeModFx(mod, indexMod, keyEffectParam);

	}
	pthread_mutex_unlock(&mutex);

}


int DrumMap::getCurrentFilter(int indexChannel)
{
	return mChannels[indexChannel]->getCurrentFilter();
}

double DrumMap::getMainPositionMs()
{
	return mMainGridControler->getPositionMs();
}

void DrumMap::setMainQuantize(int quntizeIndex)
{

	if (!mIsQuantize)
	{
		mQuntizeIndex = quntizeIndex;
		return;
	}

	pthread_mutex_lock(&mutex);
	mQuntizeIndex = quntizeIndex;
	for (int i = 0; i < NUM_CHANNELS; ++i)
	{
		if (currentChannelProccessing[i] == QSTART)
		{
			currentChannelProccessing[i] = QASK_START;
			mChannels[i]->resetSeq();
		}
	}
	mLastQuantize = getIndexQuantize();
	pthread_mutex_unlock(&mutex);
}

void DrumMap::setQuantizeEnable(bool enable)
{
	if (enable)
	{
		pthread_mutex_lock(&mutex);
		mIsQuantize = enable;
		for (int i = 0; i < NUM_CHANNELS; ++i)
		{
			if (currentChannelProccessing[i] == QSTART)
			{
				currentChannelProccessing[i] = QASK_START;
				mChannels[i]->resetSeq();
			}

		}
		mLastQuantize = getIndexQuantize();
		pthread_mutex_unlock(&mutex);
	} else
		mIsQuantize = enable;
}

bool DrumMap::getIsPlay(int indexChannel)
{
	return currentChannelProccessing[indexChannel] != QSTOP;
}

void DrumMap::playChannel(int indexChannel)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->resetSeq();
	if (mIsQuantize)
		currentChannelProccessing[indexChannel] = QASK_START;
	else
		currentChannelProccessing[indexChannel] = QSTART;
	pthread_mutex_unlock(&mutex);
}

void DrumMap::stopPlayChannel(int indexChannel)
{
	pthread_mutex_lock(&mutex);
	if (mIsQuantize)
		currentChannelProccessing[indexChannel] = QASK_STOP;
	else
		currentChannelProccessing[indexChannel] = QSTOP;
	pthread_mutex_unlock(&mutex);
}

void DrumMap::setStereo(int indexChannel, double left, double right, int progressStereo)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setStereo(left, right, progressStereo);
	pthread_mutex_unlock(&mutex);
}

int DrumMap::getStereoProgress(int indexChannel)
{
	return mChannels[indexChannel]->getStereoProgress();
}

void DrumMap::setMainChannelPitch(int indexChannel, double value)
{
	mChannels[indexChannel]->setMainChannelPitch(value);
}

double DrumMap::getMainChannelPitch(int indexChannel)
{
	return mChannels[indexChannel]->getMainChannelPitch();
}

void DrumMap::initViewMeter(float **pDouble)
{
	for (int i = 0; i < NUM_CHANNELS; ++i)
	{
		(*pDouble)[i * 2] = mChannels[i]->mViewMeterLeft;
		(*pDouble)[(i * 2) + 1] = mChannels[i]->mViewMeterRight;
	}
}

float DrumMap::getViewMeterChannelLeft(int indexChannel)
{
	return mChannels[indexChannel]->mViewMeterLeft;
}

float DrumMap::getViewMeterChannelRight(int indexChannel)
{
	return mChannels[indexChannel]->mViewMeterRight;
}

int DrumMap::getSeqRateProgress(int indexChannel)
{
	return mChannels[indexChannel]->getSeqRateProgress();
}

void DrumMap::setSeqStepState(int state, int indexChannel)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setSeqStepState(state);
	pthread_mutex_unlock(&mutex);
}

int DrumMap::getSeqStepState(int indexChannel)
{
	return mChannels[indexChannel]->getSeqStepState();
}

void DrumMap::initWaveform(int indexChannel, float **buffer)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->initWaveform(buffer);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::openChannelFileForEdit(int indexChannel, const char *path, long len)
{

	mEditPlayer->open(path, 0, len);
}

void *pFuncProccessEditWave(void *pdrumMap)
{
	DrumMap *drumMap = (DrumMap *) pdrumMap;
	drumMap->proccessEditWave();
}

void DrumMap::playerEventCallback(SuperpoweredAdvancedAudioPlayerEvent event, void *value)
{
	if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess)
	{

		pthread_t pt;
		pthread_create(&pt, NULL, pFuncProccessEditWave, this);
	} else if (event == SuperpoweredAdvancedAudioPlayerEvent_EOF)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "ndk", "EDIT_FINISH");
		stateEdit = EDIT_FINISH;
	} else if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadError)
	{

	}

}

void DrumMap::proccessEditWave()
{
	if (mEditBuffer != NULL)
		free(mEditBuffer);
	int sizeBuffer = ((int) ((mEditPlayer->durationMs / 1000.0) * mSampleRate * 2 * sizeof(float)));
	__android_log_print(ANDROID_LOG_DEBUG, "proccessEditWave", "durationMs %d buffSize %d", mEditPlayer->durationMs, sizeBuffer);
	mEditBuffer = (float *) malloc(sizeBuffer);


	mEditPlayer->play(false);
	stateEdit = EDIT_NOT_PROCCESS;
	int stateEdit = 0;
	float *pEditBuffer = mEditBuffer;
	int unitCount = mBufferSize * 2 * sizeof(float);
	int counter = 0;
	do
	{
		if (mEditPlayer->process(mTempEditBuffer, false, mBufferSize, 1))
		{
			stateEdit = EDIT_PROCCESS;
			memcpy(pEditBuffer, mTempEditBuffer, mBufferSize * 2 * sizeof(float));
			counter += unitCount;
			pEditBuffer += mBufferSize * 2;
			__android_log_print(ANDROID_LOG_DEBUG, "ndk", "counter %d buffSize %d", counter, sizeBuffer);
		} else if (stateEdit == EDIT_PROCCESS)
		{
			stateEdit = EDIT_FINISH;
		}
		__android_log_print(ANDROID_LOG_DEBUG, "proccessEditWave", "counter %d buffSize %d", counter, sizeBuffer);

	} while (stateEdit != EDIT_FINISH && counter < sizeBuffer);
	__android_log_print(ANDROID_LOG_DEBUG, "ndk", "end proccess edit");
//	free(mTempEditBuffer);
	mFuncCallBack(BUFFER_EDIT_LOADED, counter);
}

float **DrumMap::getEditBuffer()
{
	return &mEditBuffer;
}

const char *DrumMap::getFilePath(int indexChannel)
{
	return mChannels[indexChannel]->getFilePath();
}

double DrumMap::getStopPlayPosition(int indexChannel)
{
	return mChannels[indexChannel]->getStopPlayPosition();
}

double DrumMap::getStartPlayPosition(int indexChannel)
{
	return mChannels[indexChannel]->getStartPlayPosition();
}

bool DrumMap::getIsLoaded(int indexChannel)
{
	return mChannels[indexChannel]->getIsLoaded();
}

void DrumMap::setPositionMs(int indexChannel, double positionMs)
{
	mChannels[indexChannel]->setPosition(positionMs);

}

bool DrumMap::getIsLoop(int indexChannel)
{
	return mChannels[indexChannel]->getIsLoop();
}

void DrumMap::playPiano(int indexChannel)
{
	mChannels[indexChannel]->playPiano();
}


void DrumMap::pausePiano(int indexChannel)
{
	currentChannelProccessing[indexChannel] = QSTOP;
	mChannels[indexChannel]->pausePiano();
}


int DrumMap::addMidiPiano(int indexChannel, int key, float start, float mEnd)
{
	pthread_mutex_lock(&mutex);
	int retVal = mChannels[indexChannel]->addMidiPiano(key, start, mEnd);
	pthread_mutex_unlock(&mutex);
	return retVal;
}

void DrumMap::removeMidiPiano(int indexChannel, int keyIndex)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->removeMidiPiano(keyIndex);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::setStartEndLoopPositionPrecentPiano(int indexChannel, double start, double end)
{
	mChannels[indexChannel]->setStartEndLoopPositionPrecentPiano(start, end);
}

double DrumMap::getStartLoopMsPiano(int indexChannel)
{
	return mChannels[indexChannel]->getStartLoopMsPiano();
}

double DrumMap::getEndLoopMsPiano(int indexChannel)
{
	return mChannels[indexChannel]->getEndLoopMsPiano();
}

void DrumMap::setPositionMsPiano(int indexChannel, double position)
{
	mChannels[indexChannel]->setPositionMsPiano(position);
}

double DrumMap::getPositionMsPiano(int indexChannel)
{
	return mChannels[indexChannel]->getPositionMsPiano();
}

void DrumMap::setDurationMsPiano(int indexChannel, double duration)
{
	mChannels[indexChannel]->setDurationMsPiano(duration);
}

double DrumMap::getDurationMsPiano(int indexChannel)
{
	return mChannels[indexChannel]->getDurationMsPiano();
}

double DrumMap::getPianoPositionBeat(int indexChannel)
{
	return mChannels[indexChannel]->getPianoPositionBeat();
}

Midi *DrumMap::getMidis(int indexChannel)
{
	return mChannels[indexChannel]->getMidis();
}

void DrumMap::setPianoPositionBeat(int indexChannel, double pianoPosition)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setPianoPositionBeat(pianoPosition);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::setPianoBeatsDuration(int indexChannel, float durationBeat)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setPianoBeatsDuration(durationBeat);
	pthread_mutex_unlock(&mutex);
}

float DrumMap::getPianoDurationBeat(int indexChannel)
{
	return mChannels[indexChannel]->getPianoDurationBeat();
}

float DrumMap::getPianoStartPlayPosition(int indexChannel)
{
	return mChannels[indexChannel]->getPianoStartPlayPosition();
}

float DrumMap::getPianoStopPlayPosition(int indexChannel)
{
	return mChannels[indexChannel]->getPianoStopPlayPosition();
}

bool DrumMap::getIsPianoPlay(int indexChannel)
{
	return mChannels[indexChannel]->isPianoPlay();
}

void DrumMap::setSeqState(int indexChannel, int state)
{
	mChannels[indexChannel]->setSeqState(state);
}

int DrumMap::getSeqState(int indexChannel)
{
	return mChannels[indexChannel]->getSeqState();
}

int DrumMap::getSeqIndexEnable(int indexChannel)
{
	return mChannels[indexChannel]->getSeqIndexEnable();
}

void DrumMap::setSeqIndexEnable(int indexChannel, int indexSeq)
{
	mChannels[indexChannel]->setSeqIndexEnable(indexSeq);
}

ChannelItem *DrumMap::getChannelItems()
{
	return mMainGridControler->getChannelItems();
}

int DrumMap::addChannelItem(int channel, double start, double end)
{
	return mMainGridControler->addChannelItem(channel, start, end);
}

double DrumMap::getMainPositionBeat()
{
	return mMainGridControler->getPositionBeat();
}

double DrumMap::getMainDurationBeat()
{
	return mMainGridControler->getDurationBeat();
}

void DrumMap::removeChannelItem(int index)
{
	mMainGridControler->removeChannelItem(index);
}

bool DrumMap::getIsMainGridPlay()
{
	return mMainGridControler->isPlay();
}

void DrumMap::playMainGrid()
{
	mMainGridControler->play();
}

void DrumMap::pauseMainGrid()
{
	pthread_mutex_lock(&mutex);
	mMainGridControler->pause();
	pthread_mutex_unlock(&mutex);
}

float DrumMap::getMainGridStopPlayPosition()
{
	return mMainGridControler->getStopPlayPosition();
}

float DrumMap::getMainGridStartPlayPosition()
{
	return mMainGridControler->getStartPlayPosition();
}

void DrumMap::setMainGridBeatsDuration(float durationBeat)
{
	mMainGridControler->setBeatsDuration(durationBeat);
}

void DrumMap::setMainGridPositionBeat(double positionBeat)
{
	mMainGridControler->setPositionBeat(positionBeat);
}

void DrumMap::setStartEndLoopPositionPrecentMainGrid(double startPercent, double endPercent)
{
	mMainGridControler->setStartEndLoopPositionPercent(startPercent, endPercent);
}

int DrumMap::getChannelColor(int indexChannel)
{
	return mChannels[indexChannel]->getChannelColor();
}

void DrumMap::setChannelColor(int indexChannel, int color)
{
	mChannels[indexChannel]->setChannelColor(color);
}

void DrumMap::resetChannelItemAdded()
{
	mMainGridControler->resetChannelItemAdded();
}

void DrumMap::updateChannelAddedEnd(double end)
{
	mMainGridControler->updateChannelAddedEnd(end);
}

void DrumMap::updateMidiAddedEnd(int indexChannel, float endMidiBeat)
{

	mChannels[indexChannel]->updateMidiAddedEnd(endMidiBeat);
}

void DrumMap::resetMidiAdded(int indexChannel)
{
	mChannels[indexChannel]->resetMidiAdded();
}

void DrumMap::initRecordWaveform(float **pBuffer)
{
	memcpy(*pBuffer, mRecorder->getBuffer(), mBufferSize * sizeof(float) * 2);
}

void DrumMap::setIsPianoCursorFollowPosition(int indexChannel, bool isFollow)
{
	mChannels[indexChannel]->setIsPianoCursorFollowPosition(isFollow);
}

bool DrumMap::getIsPianoCursorFollowPosition(int indexChannel)
{
	return mChannels[indexChannel]->getIsPianoCursorFollowPosition();
}

bool DrumMap::getIsGridCursorFollowPosition()
{
	return mMainGridControler->getIsCursorFollowPosition();
}

void DrumMap::setIsGridCursorFollowPosition(bool isFollow)
{
	mMainGridControler->setIsCursorFollowPosition(isFollow);
}

void DrumMap::updateMidiPiano(int indexChannel, int indexMidi, int note, float start, float end)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->updateMidiPiano(indexMidi, note, start, end);
	pthread_mutex_unlock(&mutex);
}

bool DrumMap::getIsMuted(int indexChannel)
{
	return mChannels[indexChannel]->isMuted();
}

bool DrumMap::getIsSolo(int indexChannel)
{
	return indexChannel == mSoloChannel;
}

void DrumMap::setMuted(int indexChannel, bool muted)
{
	pthread_mutex_lock(&mutex);
	mChannels[indexChannel]->setMuted(muted);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::setSolo(int indexChannel, bool solo)
{
	pthread_mutex_lock(&mutex);
	if (solo)
		mSoloChannel = indexChannel;
	else if (!solo && indexChannel == mSoloChannel)
		mSoloChannel = -1;
	pthread_mutex_unlock(&mutex);
}

void DrumMap::randSeq(int indexChannel, int indexValue)
{
	mChannels[indexChannel]->randSeq(indexValue);
}

void DrumMap::replaceFxChannelPosition(int indexChannel, int from, int to)
{
	pthread_mutex_lock(&mutex);
	if (indexChannel == -1)
		mFxControler1->replaceFxPosition(from, to);
	else if (indexChannel == -2)
		mFxControler2->replaceFxPosition(from, to);
	else
		mChannels[indexChannel]->replaceFxPosition(from, to);
	pthread_mutex_unlock(&mutex);
}

void DrumMap::deleteEffectChannel(int indexChannel, int index)
{
	pthread_mutex_lock(&mutex);
	if (indexChannel == -1)
		mFxControler1->deleteEffectChannel(index);
	else if (indexChannel == -2)
		mFxControler2->deleteEffectChannel(index);
	else
		mChannels[indexChannel]->deleteEffectChannel(index);
	pthread_mutex_unlock(&mutex);
}

bool DrumMap::isEnbableSendFx1(int indexChannel)
{
	return mChannels[indexChannel]->isSendFx1();
}

bool DrumMap::isEnbableSendFx2(int indexChannel)
{
	return mChannels[indexChannel]->isSendFx2();
}

double DrumMap::getSendFx1(int indexChannel)
{
	return mChannels[indexChannel]->getSendFx1();
}

double DrumMap::getSendFx2(int indexChannel)
{
	return mChannels[indexChannel]->getSendFx2();
}

void DrumMap::enableSendFx1(int indexChannel, bool enable)
{
	mChannels[indexChannel]->setIsSendFx1(enable);
}

void DrumMap::enableSendFx2(int indexChannel, bool enable)
{
	mChannels[indexChannel]->setIsSendFx2(enable);
}

void DrumMap::setSendFx1(int indexChannel, double val)
{
	mChannels[indexChannel]->setSendFx1(val);
}

void DrumMap::setSendFx2(int indexChannel, double val)
{
	mChannels[indexChannel]->setSendFx2(val);
}


