//
// Created by Yossi Barel on 28/03/16.
//

#include "Channel.h"

#define ENV_WAVE 0
#define ENV_FX 1

#define NUM_UNIT_PRCS_REMOVE_FROM_END 10

static void pplayerEventCallback(void *clientData, SuperpoweredAdvancedAudioPlayerEvent event, void *value)
{
	((Channel *) clientData)->playerEventCallback(event, value);
}

Channel::Channel(int bufferSize, int sampleRate, int index, ChannelListener *listener)
{
	mKeyDown = false;
	mBufferSize = bufferSize;
	mSampleRate = sampleRate;
	mIndex = index;
	mPlayEnergy = 0;

	mBufferProccess = (float *) malloc((mBufferSize) * sizeof(float) * 2);
	mBufferSilence = (float *) malloc((mBufferSize) * sizeof(float) * 2);

	for (int i = 0; i < mBufferSize * 2; ++i)
	{
		mBufferSilence[i] = 0;
	}
	//memset(mBufferSilence, 0.0f, (mBufferSize) * sizeof(float) * 2);
	mSeqState = SEQ_OFF;
	mVolume = 1.0;
	mMainVolume = 0.8;

	mPitch = 0;
	mMainPitch = 0;
	mFxPitch = 0.0;
	mIsEmpty = true;
	mListener = listener;
	mPianoRoll = new PianoRoll(bufferSize, sampleRate, this);
	mFxControler = new FxControler(mSampleRate, mBufferSize);
	mSeqControler = new SeqControler(mSampleRate, mBufferSize, this);
	mLFOControler = new LFOControler(mSampleRate, mBufferSize, this);
	mADSRControler = new ADSRControler(bufferSize, sampleRate);
	mADSRFxControler = new ADSRFxControler(bufferSize, sampleRate, this);
	mPlayer = new SuperpoweredAdvancedAudioPlayer(this, pplayerEventCallback, (int) mSampleRate, 0);
	mIsSequencer = false;
	mIsLoop = false;
	mIsPlaynigFromStartOnKeyDown = true;
	mIsReverse = false;
	mLeft = 1.0;
	mRight = 1.0;
	mStereoProgress = 100;
	mFilePath = NULL;
	mNumKeyPlaying = 0;
	mMuted = false;
	mUnitLoopProccessMs = ((double) mBufferSize / mSampleRate) * 1000.0;
	mIsSendFx1 = false;
	mIsSendFx2 = false;
	mSendFx1 = 0;
	mSendFx2 = 0;
	mViewMeterRight = 0;
	mViewMeterLeft = 0;
	//__android_log_print(ANDROID_LOG_DEBUG, "ndk", "beats %f",temp);
}

Channel::~Channel()
{
	if (mFilePath != NULL)
		free(mFilePath);
	free(mBufferProccess);

	delete(mFxControler);
	delete(mPlayer);
}

float *Channel::proccess(double currentPosition)
{
	if (mIsEmpty)
		return NULL;


	if (mIsSequencer)
	{
		return mSeqControler->proccess(this, currentPosition, mPitch);
	}
	mVolume = 1.0;
	return internalProccess(currentPosition);


}

void *threadNotifyStopPlay(void *channel)
{
	Channel *ch = (Channel *) channel;
	ch->playerEventCallback(SuperpoweredAdvancedAudioPlayerEvent_EOF, NULL);
}

float *Channel::internalProccess(double currentPosition)
{
	double adsr = mADSRControler->proccess();

	if (adsr > 0.0)
	{//__android_log_print(ANDROID_LOG_DEBUG, "ndk", "mPositionMs %f", mEnvA);


		bool isProcess = mPlayer->process(mBufferProccess, false, mBufferSize, mVolume * adsr * mMainVolume);


		mLFOControler->proccess(mBufferSize);
		mADSRFxControler->proccess();
		mFxControler->process(mBufferProccess,1);

		if (mPlayer->positionMs >= (mEndPosition))
		{
			mPlayer->setPosition(mStartPosition, false, false);

			if (!mIsLoop && !mIsSequencer)
			{
				mPlayer->pause();
				pthread_t pt;
				pthread_create(&pt, NULL, threadNotifyStopPlay, this);

				//return mBufferSilence;
			}
		}
		if (!isProcess)
			return mBufferSilence;
		return mBufferProccess;
	} else if (!mIsSequencer)
		mListener->onNeedStopProccess(mIndex);
	return mBufferProccess;
}


void Channel::setEnvADSR(int envType, int type, double value)
{
	switch (envType)
	{
		case ENV_WAVE:
			mADSRControler->setADSRValue(type, value);
			break;
		case ENV_FX:
			mADSRFxControler->setADSRValue(type, value);
			break;

	}

}

/*void Channel::keyDown()
{
	if (mIsEmpty)
		return;
	if (mIsSequencer)
	{
		mPlayer->exitLoop();
		return;
	}
	mPlayer->setPitchShiftCents((mPitch + mMainPitch + mFxPitch) * 100.0);
	__android_log_print(ANDROID_LOG_DEBUG, "ndk", "setPitchShiftCents %f", (mPitch + mMainPitch + mFxPitch) * 100.0);
	mKeyDown = true;

	if (mIsLoop)
		mPlayer->loopBetween(mStartPosition, mEndPosition, mIsPlaynigFromStartOnKeyDown || mPlayer->positionMs >= mEndPosition, 255, false);
	else
	{
		mPlayer->exitLoop();
		if (mIsPlaynigFromStartOnKeyDown || mPlayer->positionMs >= mEndPosition)
			mPlayer->setPosition(mStartPosition, false, false);
	}
	mPlayer->play(false);
	mADSRControler->keyDown();
	mADSRFxControler->keyDown();

}*/
void Channel::keyDown()
{
	if (mIsEmpty)
		return;

	if (mSeqState == SEQ_ON)
	{
		mIsSequencer = true;
	} else if (mSeqState == SEQ_RETRIGER)
	{
		mSeqControler->setRetriger();
		mIsSequencer = true;
	} else
	{
		if (mIsSequencer)
		{
			//mPlayer->exitLoop();
			return;
		}
		__android_log_print(ANDROID_LOG_DEBUG, "pianoKey", "keyDown");

		mPlayer->setPitchShiftCents((mPitch + mMainPitch + mFxPitch) * 100.0);
		__android_log_print(ANDROID_LOG_DEBUG, "ndk", "setPitchShiftCents %f", (mPitch + mMainPitch + mFxPitch) * 100.0);
		mKeyDown = true;


		if (mIsPlaynigFromStartOnKeyDown || mPlayer->positionMs >= mEndPosition - mUnitLoopProccessMs * NUM_UNIT_PRCS_REMOVE_FROM_END || mPianoRoll->isPlay())
			mPlayer->setPosition(mStartPosition, false, false);

		mPlayer->play(false);
		mADSRControler->keyDown();
		mADSRFxControler->keyDown();

	}
}

void Channel::seqTrigger()
{

	mKeyDown = true;
	//mPlayer->exitLoop();
	mPlayer->setPosition(mStartPosition, false, false);
	//mPlayer->loopBetween(mStartPosition, mEndPosition, true, 255, false);
	mADSRControler->keyDown();
	mADSRFxControler->keyDown();
	mPlayer->play(false);
}

void Channel::keyRelese()
{

	if (mSeqState == SEQ_ON)
	{
		mIsSequencer = true;
	} else if (mSeqState == SEQ_RETRIGER)
	{

		mIsSequencer = false;
	} else
	{
		if (mIsSequencer)
			return;
		mKeyDown = false;

		__android_log_print(ANDROID_LOG_DEBUG, "pianoKey", "keyRelese");
		//if (mIsStopOnRelease)
		//	mPlayer->pause();
		mADSRControler->keyRelese();
		mADSRFxControler->keyRelese();
	}
}

void Channel::setFx(int keyFx, int fxKeyParam, float value)
{
	if (mIsEmpty)
		return;
	if (keyFx == FX_STEREO)
	{


		if (value > 0.5)
		{
			mLeft = (double) (0.5 - (value - 0.5)) / 0.5;
			mRight = 1.0;
		} else
		{
			mLeft = 1.0;
			mRight = (double) value / 0.5;
		}
	} else if (keyFx == FX_PITCH)
	{
		mFxPitch = (value - 0.5) * 24.0;
		mPlayer->setPitchShiftCents(mFxPitch * 100.0);
	} else
		mFxControler->setFx(keyFx, fxKeyParam, value);
}

bool Channel::addFx(int fxType)
{
	return mFxControler->addFx(fxType);
}

void Channel::openChannelFile(const char *filePath, long len)
{

	mFxControler->reset();
	mIsEmpty = true;
	if (mFilePath != NULL)
		free(mFilePath);
	mFilePath = (char *) malloc(strlen(filePath) + 1);
	strcpy(mFilePath, filePath);
	mPlayer->open(filePath, 0, len);
}

void Channel::playerEventCallback(SuperpoweredAdvancedAudioPlayerEvent event, void *value)
{
	if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess)
	{


		__android_log_print(ANDROID_LOG_DEBUG, "ndk", "NDK Success load file");
		mStartPosition = 0;
		mEndPosition = mPlayer->durationMs - mUnitLoopProccessMs * NUM_UNIT_PRCS_REMOVE_FROM_END;// - (((double) mBufferSize * 16000.0) / (double) mSampleRate);
		//mPlayer->setFirstBeatMs(0);
		mPlayer->play(false);
		mIsEmpty = false;
		//mListener->onLoadFileEvent(event,mIndex);


	} else if (event == SuperpoweredAdvancedAudioPlayerEvent_EOF)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "ndk", "NDK EOF load file");
		mPlayer->setPosition(mStartPosition, false, false);;
		mPlayer->play(false);
		mPlayer->process(mBufferProccess, false, mBufferSize, 0);

		//if (mIsLoop)
		//mPlayer->play(false);

		//	mKeyDown = false;


	} else if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadError)
	{
		__android_log_print(ANDROID_LOG_DEBUG, "ndk", "NDK Faile load file");
	}
	mListener->onLoadFileEvent(event, mIndex);
}

long Channel::getDurationMs()
{
	return mPlayer->durationMs - mUnitLoopProccessMs * NUM_UNIT_PRCS_REMOVE_FROM_END;//- (((double) mBufferSize * 16000.0) / (double) mSampleRate);
}

void Channel::loopBetween(jdouble start, jdouble end, jboolean startPlay)
{
	__android_log_print(ANDROID_LOG_DEBUG, "loopBetween", "start %f end %f", start, end);
	mStartPosition = start;
	mEndPosition = end;
	/*if (mIsLoop)
		mPlayer->loopBetween(start, end, false, 255, false);
	else*/
	double pos = mPlayer->positionMs;
	if (pos > mEndPosition || pos < mStartPosition)
		mPlayer->setPosition(mStartPosition, false, false);

}

void Channel::setReverse(bool flag)
{
	mPlayer->setReverse(flag, 0);
	mIsReverse = flag;

}


void Channel::setLFORateWave(int lfo, double rate)
{
	mLFOControler->setLfoRate(lfo, rate);
}

void Channel::setLFOTypeWave(int lfoType, int waveType)
{
	mLFOControler->setLfoWave(lfoType, waveType);
}

void Channel::setLFOAmplitudeWave(int lfo, double amp)
{
	mLFOControler->setLfoAmplitude(lfo, amp);
}

void Channel::setLFOPresentWave(double present)
{
	mLFOControler->setPresent(present);

}

void Channel::scretch(double val)
{
	mPlayer->scratch(val * 4.0, 1.0);
}

void Channel::setStopPlayOnRelease(bool isStop)
{
	mIsStopOnRelease = isStop;
}

bool Channel::getStopPlayOnRelease()
{
	return mIsStopOnRelease;
}

double Channel::getPositionPrecent()
{
	return mPlayer->positionPercent;
}

void Channel::setPitchBand(double pitch)
{
	if (pitch >= 0.0)
		mPlayer->pitchBend(pitch * 12, false, false, 3000);
	else
		mPlayer->endContinuousPitchBend();
}

void Channel::setBpm(double bpm)
{
	mBpm = bpm;

	mSeqControler->setBpm(bpm);
	mFxControler->setBpm(bpm);
	mLFOControler->setBpm(bpm);
	mPlayer->setBpm(bpm);
	mPianoRoll->setBpm(bpm);

}

void Channel::enableSequencerPattern(int indexPattern, bool enable)
{
	mSeqControler->enableSequencerPattern(indexPattern, enable);


}

void Channel::setPitchShift(double pitch)
{

	mPlayer->setPitchShiftCents((((pitch - 0.5) * 24.0) + mPitch + mMainPitch + mFxPitch) * 100.0);
	//mPlayer->pitchBend(pitch * 0.29 + 0.01, false, false, 999);
}

void Channel::setValueSequencerPattern(int indexPattern, int iindexValue, double val)
{

	mSeqControler->setValueSequencerPattern(indexPattern, iindexValue, val);
}

void Channel::enableSequencer(bool enable)
{
	mIsSequencer = enable;
	if (!mIsSequencer)
	{
		keyRelese();
		mPlayer->setPitchShiftCents((mMainPitch + mFxPitch) * 100.0);
	} else
	{
		//mPlayer->loopBetween(mStartPosition, mEndPosition, true, 255, false);

		//keyDown();
		mSeqControler->startSeq();
		mPlayer->exitLoop();
		mIsPlaynigFromStartOnKeyDown = true;
	}
}

void Channel::setSeqRate(int indexValue)
{
	mSeqControler->setRate(indexValue);
}

double Channel::getSeqPattern(int indexPattern, int indexValue)
{
	return mSeqControler->getPatternValue(indexPattern, indexValue);
}


void Channel::resetSeq(int indexValue)
{
	mSeqControler->resetValue(indexValue);
}

void Channel::setPitch(double pitch)
{
	mPitch = pitch;

}

vector<int> Channel::getActiveEffect()
{
	return mFxControler->getActiveEffect();
}

void Channel::setPlayFromStartLoop(bool isPlayFromStartLoop)
{

	mIsPlaynigFromStartOnKeyDown = isPlayFromStartLoop;
}

bool Channel::getPlayFromStartLoop()
{
	return mIsPlaynigFromStartOnKeyDown;
}

void Channel::setLoop(bool isLoop)
{
	mIsLoop = isLoop;
	if (isLoop)
		mPlayer->loopBetween(mStartPosition, mEndPosition, false, 255, false);
	else
		mPlayer->exitLoop();
}

void Channel::enableFilter(bool enable)
{
	mFxControler->enableFilter(enable);
}

void Channel::sendFxToLFO(string indexLfo, int fxType, int keyEffectParam, int lfo1, int lfo2, double start, double end, bool isUpdate)
{
	mLFOControler->addFx(indexLfo, fxType, keyEffectParam, lfo1, lfo2, start, end, isUpdate);
}

void Channel::sendFxToADSR(string mIndexControl, int fxType, int keyEffectParam, double start, double end, bool isUpdate)
{
	mADSRFxControler->addFx(mIndexControl, fxType, keyEffectParam, start, end, isUpdate);
}

void Channel::sendFxToCurve(string indexString, int curve, int fxType, int keyEffectParam, double start, double end, bool isUpdate)
{
	mSeqControler->sendFxToCurve(indexString, curve, fxType, keyEffectParam, start, end, isUpdate);
}

void Channel::setMainVolume(double vol)
{
	mMainVolume = vol;
}

double Channel::getFxValue(int fxType, int fxKeyParam)
{
	return mFxControler->getFxValue(fxType, fxKeyParam);
}

double Channel::getEnvADSR(int type, int param)
{
	if (type == ENV_WAVE)
		return mADSRControler->getParamValue(param);
	return mADSRFxControler->getParamValue(param);
}

int Channel::getLFOTypeWave(int lfoType)
{
	return mLFOControler->getLFOTypeWave(lfoType);
}

double Channel::getLFOAmplitudeWave(int lfoType)
{
	return mLFOControler->getLfoAmplitude(lfoType);
}

double Channel::getLFORateWave(int lfo)
{
	return mLFOControler->getLfoRate(lfo);
}

double Channel::getLfoWavePresent()
{
	return mLFOControler->getLfoWavePresent();
}

double Channel::getVolume()
{
	return mMainVolume;
}

ModIndex Channel::getModState(string modIndex)
{
	ModIndex mod = mLFOControler->getModState(modIndex);
	if (mod == NON_MOD)
		mod = mADSRFxControler->getModState(modIndex);
	if (mod == NON_MOD)
		mod = mSeqControler->getModState(modIndex);
	return mod;

}

void Channel::removeModFx(int mod, string indexMod, int keyEffectParam)
{
	if (keyEffectParam == FX_PITCH)
		mFxPitch = 0.0;
	else if (keyEffectParam == FX_STEREO)
	{
		mRight = 1.0;
		mLeft = 1.0;
	}
	if (mod >= LFO_1 && mod <= LFO_1_2)
		mLFOControler->removeFx(mod, indexMod);
	else if (mod == (int) ADSR_ENV)
	{
		mADSRFxControler->removeFx(mod, indexMod);
	} else if (mod == CURVE_1 || mod == CURVE_2)
		mSeqControler->removeFx(mod - (int) CURVE_1, indexMod);

}

int Channel::getCurrentFilter()
{
	return mFxControler->getCurrentFilter();
}


void Channel::resetSeq()
{
	mSeqControler->startSeq();
}

void Channel::setStereo(float left, float right, int progress)
{
	mLeft = left;
	mRight = right;
	mStereoProgress = progress;
	__android_log_print(ANDROID_LOG_DEBUG, "stereo", "left=%f right=%f", mLeft, mRight);
}

int Channel::getStereoProgress()
{
	return mStereoProgress;
}

void Channel::setMainChannelPitch(double pitch)
{
	mMainPitch = pitch;
}

double Channel::getMainChannelPitch()
{
	return mMainPitch;
}

int Channel::getSeqRateProgress()
{
	return mSeqControler->getRateProgress();
}

void Channel::setSeqStepState(int state)
{
	mSeqControler->setStepState(state);
}

int Channel::getSeqStepState()
{
	return mSeqControler->getStepState();
}

void Channel::initWaveform(float **pBuffer)
{
	memcpy(*pBuffer, mBufferProccess, mBufferSize * sizeof(float) * 2);

}

const char *Channel::getFilePath()
{
	return mFilePath;
}

double Channel::getStopPlayPosition()
{
	return mEndPosition;
}

double Channel::getStartPlayPosition()
{
	return mStartPosition;
}

bool Channel::getIsLoaded()
{
	return !mIsEmpty;
}

void Channel::setPosition(double positionMs)
{
	mPlayer->setPosition(positionMs, false, false);
}

bool Channel::getIsLoop()
{
	return mIsLoop;
}

void Channel::playPiano()
{


	mPianoRoll->play();
}

void Channel::pausePiano()
{
	mPianoRoll->pause();
	mPlayer->pause();
}

int Channel::addMidiPiano(int key, float start, float mEnd)
{
	return mPianoRoll->addMidi(key, start, mEnd);
}

void Channel::removeMidiPiano(int keyIndex)
{
	mPianoRoll->removeMidi(keyIndex);
}

void Channel::setStartEndLoopPositionPrecentPiano(double start, double end)
{
	mPianoRoll->setStartEndLoopPositionPercent(start, end);
}

double Channel::getStartLoopMsPiano()
{
	return mPianoRoll->getStartLoopMs();
}

double Channel::getEndLoopMsPiano()
{
	return mPianoRoll->getEndLoopMs();
}

void Channel::setPositionMsPiano(double position)
{
	mPianoRoll->setPositionMs(position);
}

double Channel::getPositionMsPiano()
{
	return mPianoRoll->getPositionMs();
}

void Channel::setDurationMsPiano(double duration)
{
	mPianoRoll->setDurationMs(duration);
}

double Channel::getDurationMsPiano()
{
	return mPianoRoll->getDurationMs();
}

void Channel::keyPianoDown(double key)
{
	mListener->onPianoKeyDown(mIndex);
	setPitch(key);
	keyDown();
}

void Channel::keyPianoRelese()
{
	//mListener->onPianoKeyRelsese(mIndex);
	keyRelese();
}

bool Channel::isPianoPlay()
{
	return mPianoRoll->isPlay();
}

void Channel::proccessPiano()
{
	mPianoRoll->process();
}

double Channel::getPianoPositionBeat()
{
	return mPianoRoll->getPianoPositionBeat();
}

Midi *Channel::getMidis()
{
	return mPianoRoll->getMidis();
}

void Channel::setPianoPositionBeat(double pianoPosition)
{
	mPianoRoll->setPositionBeat(pianoPosition);
}

void Channel::setPianoBeatsDuration(float durationBeat)
{
	mPianoRoll->setPianoBeatsDuration(durationBeat);
}

float Channel::getPianoDurationBeat()
{
	return mPianoRoll->getPianoDurationBeat();
}

float Channel::getPianoStartPlayPosition()
{
	return mPianoRoll->getPianoStartPlayPosition();
}

float Channel::getPianoStopPlayPosition()
{
	return mPianoRoll->getPianoStopPlayPosition();
}

void Channel::setSeqState(int state)
{
	mSeqState = (SequencerState) state;
}

int Channel::getSeqState()
{
	return mSeqState;
}

int Channel::getSeqIndexEnable()
{
	return mSeqControler->getSeqIndexEnable();
}

void Channel::setSeqIndexEnable(int indexSeq)
{
	mSeqControler->setSeqIndexEnable(indexSeq);
}

int Channel::getChannelColor()
{
	return mColor;
}

void Channel::setChannelColor(int color)
{
	mColor = color;
}

void Channel::clearAllSendMod()
{

	mLFOControler->removeAll();

	mADSRFxControler->removeAll();

	mSeqControler->removeAll();

}

void Channel::updateMidiAddedEnd(float endMidiBeat)
{
	mPianoRoll->updateMidiAddedEnd(endMidiBeat);

}

void Channel::resetMidiAdded()
{
	mPianoRoll->resetMidiAdded();
}

void Channel::setIsPianoCursorFollowPosition(bool isFollow)
{
	mPianoRoll->setIsPianoCursorFollowPosition(isFollow);
}

bool Channel::getIsPianoCursorFollowPosition()
{
	return mPianoRoll->getIsCursorFollowPosition();
}

void Channel::updateMidiPiano(int indexMidi, int note, float start, float end)
{
	mPianoRoll->updateMidiPiano(indexMidi, note, start, end);
}

void Channel::setMuted(bool muted)
{
	mMuted = muted;
}

bool Channel::isMuted()
{
	return mMuted;
}

float *Channel::getBuffer()
{
	return mBufferProccess;
}

void Channel::randSeq(int indexValue)
{
	mSeqControler->randSeq(indexValue);
}

void Channel::replaceFxPosition(int from, int to)
{
	mFxControler->replaceFxPosition(from, to);

}

void Channel::deleteEffectChannel(int index)
{
	mFxControler->deleteEffectChannel(index);
}
