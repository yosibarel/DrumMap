//
// Created by Yossi Barel on 12/04/16.
//

#include "SeqControler.h"
#include "Channel.h"

double SeqControler::bpmToMs()
{
	return (60000.0 / mBpm) * (1.0 / mRate);
}

double SeqControler::stepsArr[6] = {0.25, 0.5, 1.0, 2.0, 3.0, 4.0};

double SeqControler::getPrecentFromCurrentIndex(double len)
{
	double bm = bpmToMs();
	double mod = (bm * (1.1 - len));
	if (mod < 1.0)
		mod = 1.0;
	double precent = ((int) mCurrentTime % (int) (mod)) / (bm);
	//1.0-precent

	return precent;
}

double SeqControler::RATES[6] = {8.0, 4.0, 2.0, 1.0, 0.5, 0.25};

double lastPosition = 0;

SeqControler::SeqControler(int sampleRate, int bufferSize, MODFxListener *listener)
{
	mSampleRate = sampleRate;
	mBufferSize = bufferSize;
	mListener = listener;
	setBpm(140.0);
	mCurrentTime = 0;
	mCurrentIndex = 0;
	for (int i = 0; i < 16; ++i)
	{
		mPatterns[i] = new SeqParrern();
		mPatterns[i]->isEnable = false;
		mPatterns[i]->volume = 1.0;
		mPatterns[i]->pitch = 0.5;
		mPatterns[i]->length = 1.0;
		mPatterns[i]->stepDur = 0.5;
		mPatterns[i]->curve[0] = 0.0;
		mPatterns[i]->curve[1] = 0.0;

	}
	mMaxSteps = 16;
	mIndexRate = 4;
	mReversePluse = 1;
	mRate = RATES[mIndexRate];
	mLastIndex = -1;
	mIsRetriger = false;
	mStepState = SEQ_NORMAL;


}

int SeqControler::getCurrentIndex()
{
	return mCurrentIndex;
}

void SeqControler::enableSequencerPattern(int indexPattern, bool enable)
{

	mPatterns[indexPattern]->isEnable = enable;
}

int SeqControler::getIndexOfSeq()
{
	return (int) mCurrentTime >= stepsArr[(int) (mPatterns[mCurrentIndex]->stepDur * 5)] * bpmToMs() ? 1 : 0;

}

bool SeqControler::isGoToNext()
{

	if (getIndexOfSeq() == 1 || mIsRetriger)
	{
		mCurrentTime = 0;
		return true;
	}
	return false;
}

float *SeqControler::proccess(Channel *channel, double currentPosition, double pitch)
{

	mCurrentTime += ((double) mBufferSize / mSampleRate) * 1000.0;
	bool isNext = isGoToNext();
	if (isNext)
	{
		mCurrentIndex = getTheNextIndex();
		__android_log_print(ANDROID_LOG_DEBUG, "currentPosition", "dif %f index=%d", mCurrentTime, mCurrentIndex);

	}
	if (isNext && mPatterns[mCurrentIndex]->isEnable)
	{
		channel->seqTrigger();
		lastPosition = mCurrentTime;
	}
	if (isNext)
	{
		for (int i = 0; i < 2; ++i)
		{
			for (map<string, CurveFx *>::iterator iter = curveSend[i].begin(); iter != curveSend[i].end(); ++iter)
			{
				CurveFx *cfx = iter->second;
				mListener->setFx(cfx->keyFx, cfx->keyFxParam, cfx->start + mPatterns[mCurrentIndex]->curve[i] * (cfx->end - cfx->start));
			}
		}
	}

	channel->setPitchShift(mPatterns[mCurrentIndex]->pitch);

	double precent = getPrecentFromCurrentIndex(mPatterns[mCurrentIndex]->length);
	channel->setVolume((1.0 - precent) * mPatterns[mCurrentIndex]->volume);
	if (precent > 0.9)
		channel->setVolume(0);

	return channel->internalProccess(currentPosition);

}

/*
float *SeqControler::proccess(Channel *channel, double currentPosition, double pitch)
{
	mCurrentTime += ((double) mBufferSize / mSampleRate) * 1000.0;

	int index = getIndexOfSeq();
	if (index != mCurrentIndex && mPatterns[index]->isEnable)
	{
		channel->seqTrigger();
		__android_log_print(ANDROID_LOG_DEBUG, "currentPosition", "dif %f index=%d", mCurrentTime - lastPosition, index);
		lastPosition = mCurrentTime;
	}
	if (index != mCurrentIndex)
	{
		for (int i = 0; i < 2; ++i)
		{
			for (map<string, CurveFx *>::iterator iter = curveSend[i].begin(); iter != curveSend[i].end(); ++iter)
			{
				CurveFx *cfx = iter->second;
				mListener->setFx(cfx->keyFx, cfx->keyFxParam, mPatterns[index]->curve[i]);
			}
		}
	}
	mCurrentIndex = index;
	channel->setPitchShift(mPatterns[mCurrentIndex]->pitch);

	double precent = getPrecentFromCurrentIndex(mPatterns[mCurrentIndex]->length);
	channel->setVolume((1.0 - precent) * mPatterns[mCurrentIndex]->volume);
	if (precent > 0.9)
		channel->setVolume(0);

	return channel->internalProccess(currentPosition);

}
*/

void SeqControler::setValueSequencerPattern(int indexPattern, int indexValue, double val)
{
	switch (indexValue)
	{
		case SEQ_ENABLE:
			mPatterns[indexPattern]->isEnable = val > 0.5;
			break;
		case SEQ_VOLUME:
			mPatterns[indexPattern]->volume = val;
			break;
		case SEQ_PITCH:
			mPatterns[indexPattern]->pitch = val;
			break;
		case SEQ_LENGTH:
			mPatterns[indexPattern]->length = val;
			break;
		case SEQ_STEP_DUR:
			mPatterns[indexPattern]->stepDur = val;
			break;
		case SEQ_CURVE_1:
			mPatterns[indexPattern]->curve[0] = val;
			break;
		case SEQ_CURVE_2:
			mPatterns[indexPattern]->curve[1] = val;
			break;

	}
//	__android_log_print(ANDROID_LOG_DEBUG, "seq", "value in %d = %f", indexValue, mPatterns[indexPattern]->length, indexValue);
}


double SeqControler::getPatternValue(int indexPattern, int indexValue)
{
	double val = 0.0;
	switch (indexValue)
	{
		case SEQ_ENABLE:
			val = mPatterns[indexPattern]->isEnable ? 1.0 : 0.0;
			break;
		case SEQ_VOLUME:
			val = mPatterns[indexPattern]->volume;
			break;
		case SEQ_PITCH:
			val = mPatterns[indexPattern]->pitch;
			break;
		case SEQ_LENGTH:
			val = mPatterns[indexPattern]->length;
			break;
		case SEQ_STEP_DUR:
			val = mPatterns[indexPattern]->stepDur;
			break;
		case SEQ_CURVE_1:
			val = mPatterns[indexPattern]->curve[0];
			break;
		case SEQ_CURVE_2:
			val = mPatterns[indexPattern]->curve[1];
			break;

	};
	return val;
}

void SeqControler::resetValue(int indexValue)
{
	switch (indexValue)
	{

		case 0:
			for (int i = 0; i < 16; ++i)
			{
				mPatterns[i]->volume = 1.0;
			}
			break;
		case 1:
			for (int i = 0; i < 16; ++i)
			{
				mPatterns[i]->pitch = 0.5;
			}
			break;
		case 2:
			for (int i = 0; i < 16; ++i)
			{
				mPatterns[i]->length = 1.0;
			}
			break;
		case 3:
			for (int i = 0; i < 16; ++i)
			{
				mPatterns[i]->stepDur = 0.4;
			}
			break;
		case 4:
			for (int i = 0; i < 16; ++i)
			{
				mPatterns[i]->curve[0] = 0.0;
			}
			break;
		case 5:
			for (int i = 0; i < 16; ++i)
			{
				mPatterns[i]->curve[1] = 0.0;
			}
			break;

	};
}

void SeqControler::sendFxToCurve(string indexString, int curve, int fxType, int keyEffectParam, double start, double end, bool isUpdate)
{
	if (!isUpdate)
	{
		CurveFx *curveFx = new CurveFx();
		curveFx->keyFx = fxType;
		curveFx->keyFxParam = keyEffectParam;
		curveFx->start = start;
		curveFx->end = end;
		curveSend[curve][indexString] = curveFx;
	} else
	{
		curveSend[curve][indexString]->start = start;
		curveSend[curve][indexString]->end = end;
	}
}

ModIndex SeqControler::getModState(string modIndex)
{

	for (int i = 0; i < 2; ++i)
	{
		if (curveSend[i].find(modIndex) != curveSend[i].end())
		{
			return (ModIndex) (i + CURVE_1);
		}
	}
	return NON_MOD;
}

void SeqControler::removeFx(int mod, string indexMod)
{
	CurveFx *fx = curveSend[mod][indexMod];
	curveSend[mod].erase(indexMod);
	delete (fx);
}

void SeqControler::startSeq()
{
	mCurrentTime = 0;
	mCurrentIndex = 0;
}

int SeqControler::getRateProgress()
{
	return mIndexRate;
}

int SeqControler::getTheNextIndex()
{
	switch ((SeqStepState) mStepState)
	{
		case SEQ_NORMAL:
			++mCurrentIndex;
			if (mCurrentIndex > mMaxSteps - 1 || mIsRetriger)
			{
				mCurrentIndex = 0;
				mIsRetriger = false;
			}
			break;
		case SEQ_GO_AND_BACK:
			mCurrentIndex += mReversePluse;
			if (mCurrentIndex > mMaxSteps - 1)
			{

				mCurrentIndex = mMaxSteps - 1;
				mReversePluse = -1;
			} else if (mCurrentIndex < 0 || mIsRetriger)
			{
				mIsRetriger = false;
				mCurrentIndex = 0;
				mReversePluse = 1;
			}
			break;
		case SEQ_REVERSE:
			--mCurrentIndex;
			if (mCurrentIndex < 0 || mIsRetriger)
			{
				mIsRetriger = false;
				mCurrentIndex = mMaxSteps - 1;

			}
			break;
		case SEQ_RANDOM:
			mCurrentIndex = (rand() % (int) ((mMaxSteps - 1) - 0 + 1));
			break;
	}
	return mCurrentIndex;
}

void SeqControler::setIndexPlay(int indexPattern)
{
	mCurrentIndex = indexPattern;
}

int SeqControler::getSeqIndexEnable()
{
	return mMaxSteps - 1;
}

void SeqControler::setSeqIndexEnable(int indexSeq)
{
	mMaxSteps = indexSeq + 1;
}

void SeqControler::removeAll()
{


	for (map<string, CurveFx *>::iterator iter = curveSend[0].begin(); iter != curveSend[0].end(); ++iter)
	{
		CurveFx *cfx = iter->second;
		delete(cfx);
	}
	curveSend[0].clear();
	for (map<string, CurveFx *>::iterator iter = curveSend[1].begin(); iter != curveSend[1].end(); ++iter)
	{
		CurveFx *cfx = iter->second;
		delete(cfx);
	}
	curveSend[1].clear();

}

void SeqControler::setRetriger()
{
	mCurrentIndex = 0;
	mIsRetriger = true;

}

void SeqControler::randSeq(int indexVal)
{
	switch (indexVal)
	{

		case 0:
			for (int i = 0; i < 16; ++i)
			{
				mPatterns[i]->volume = (rand() % 100 / 100.0);
			}
			break;
		case 1:
			for (int i = 0; i < 16; ++i)
			{
				mPatterns[i]->pitch = (rand() % 100 / 100.0);
			}
			break;
		case 2:
			for (int i = 0; i < 16; ++i)
			{
				mPatterns[i]->length = (rand() % 100 / 100.0);
			}
			break;
		case 3:
			for (int i = 0; i < 16; ++i)
			{
				mPatterns[i]->stepDur = (rand() % 100 / 100.0);
			}
			break;
		case 4:
			for (int i = 0; i < 16; ++i)
			{
				mPatterns[i]->curve[0] = (rand() % 100 / 100.0);
			}
			break;
		case 5:
			for (int i = 0; i < 16; ++i)
			{
				mPatterns[i]->curve[1] = (rand() % 100 / 100.0);
			}
			break;

	};
}
