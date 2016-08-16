//
// Created by Yossi Barel on 05/06/16.
//

#include "MainGridControler.h"

bool MainGridControler::isNeedPlay(ChannelItem *channelItem)
{
	return !channelItem->mIsPlay && channelItem->mStart <= mPositionBeat && channelItem->mEnd > mPositionBeat;
}

bool MainGridControler::isNeedStop(ChannelItem *channelItem)
{
	return channelItem->mIsPlay && channelItem->mEnd <= mPositionBeat;
}

void MainGridControler::process()
{
	if (mIsPlay)
	{
		if (mPositionBeat >= mEndLoopPositionBeat)
		{
			setUnplayAll();
			mPositionMs = beatToMs(mStartLoopPositionBeat);
			mPositionBeat = mStartLoopPositionBeat;

		}
		if (mPositionBeat >= mDurationBeat)
		{
			setUnplayAll();
			mPositionMs = beatToMs(mStartLoopPositionBeat);
			mPositionBeat = mStartLoopPositionBeat;
		}


		for (ChannelItem *node = mHead; node != NULL; node = node->next)
		{
			if (isNeedStop(node))
			{
				node->mIsPlay = false;
				mListener->stopPlayChannelCallBack(node->mChannel);
			}
			if (isNeedPlay(node))
			{
				__android_log_print(ANDROID_LOG_DEBUG, "pianondk", "midi start %f midi end %f midi play %s position %f", node->mStart, node->mEnd, node->mIsPlay ? "true" : "false", mPositionMs);
				node->mIsPlay = true;
				mListener->startPlayChannelCallBack(node->mChannel);
			}
		}

		mPositionMs += mUnitProccesMs;
		mPositionBeat += msToBeat(mUnitProccesMs);
	}
}

MainGridControler::MainGridControler(int bufferSize, int sampleRate, MainGridControllerListener *listener)
{
	mBufferSize = bufferSize;
	mSampleRate = sampleRate;
	mUnitProccesMs = ((double) mBufferSize / mSampleRate) * 1000.0;
	mPositionMs = 0;
	mListener = listener;
	mBpm = 140;
	mDurationBeat = 128;
	mDuration = beatToMs(mDurationBeat);
	mStartLoopPositionBeat = 0;
	mEndLoopPositionBeat = mDurationBeat;
	mPositionBeat = 0;
	mHead = NULL;
	mIndexChannelItem = 0;
	mChannelItemAdded = NULL;
	mIsCursorFollowPosition = false;
	mIsPlay = false;
}

MainGridControler::~MainGridControler()
{

}

double MainGridControler::beatToMs(double beat)
{
	return beat * (60000.0 / mBpm);
}

double MainGridControler::msToBeat(double ms)
{
	return ms / (60000.0 / mBpm);
}

int MainGridControler::addChannelItem(int channel, float start, float end)
{
	ChannelItem *channelItem = new ChannelItem();
	channelItem->mStart = start;
	channelItem->mEnd = end;
	channelItem->mLength = end - start;
	channelItem->mChannel = channel;
	channelItem->mIsPlay = false;
	channelItem->mIndex = mIndexChannelItem++;
	channelItem->next = NULL;
	if (mHead == NULL)
	{
		mHead = channelItem;
	} else if (mHead->next == NULL)
	{
		if (channelItem->mStart < mHead->mStart)
		{
			channelItem->next = mHead;
			mHead = channelItem;
		} else
			mHead->next = channelItem;
	} else
	{
		bool isInsert = false;
		ChannelItem *node = mHead;
		for (; !isInsert && node->next != NULL; node = node->next)
		{
			if (node->next->mStart > channelItem->mStart)
			{
				channelItem->next = node->next;
				node->next = channelItem;
				isInsert = true;
			}
		}
		if (!isInsert)
			node->next = channelItem;
	}
	mChannelItemAdded = channelItem;
	return channelItem->mIndex;
}

void MainGridControler::removeChannelItem(int keyIndex)
{
	if (mHead->mIndex == keyIndex)
	{
		ChannelItem *temp = mHead;
		mHead = mHead->next;
		delete temp;

	} else if (mHead->next->mIndex == keyIndex)
	{
		ChannelItem *temp = mHead->next;
		mHead->next = temp->next;
		delete temp;
	} else
	{
		ChannelItem *node = mHead;
		bool mIsDeleted = false;
		for (; !mIsDeleted && node->next != NULL; node = node->next)
		{
			if (node->next->mIndex == keyIndex)
			{
				ChannelItem *temp = node->next;
				node->next = temp->next;
				mIsDeleted = true;
				delete temp;
			}
		}

	}
	//Midi *midi = mMidiList[keyIndex];
	//mMidiList.erase(mMidiList.begin() + keyIndex);
//	delete(midi);
}

void MainGridControler::play()
{
	mIsPlay = true;
}

void MainGridControler::pause()
{
	mIsPlay = false;
	for (ChannelItem *node = mHead; node != NULL; node = node->next)
		node->mIsPlay = false;
	for (int i = 0; i < 16; ++i)
	{
		mListener->stopPlayChannelCallBack(i);
	}
}

void MainGridControler::setStartEndLoopPositionPercent(double start, double end)
{
	mStartLoopPositionBeat = start * mDurationBeat;
	mEndLoopPositionBeat = end * mDurationBeat;
	if (mPositionBeat < mStartLoopPositionBeat || mPositionBeat > mEndLoopPositionBeat)
	{
		mPositionBeat = mStartLoopPositionBeat;
		mPositionMs = beatToMs(mPositionBeat);
	}


}

double MainGridControler::getStartLoopMs()
{
	return mStartLoopPositionBeat;
}

double MainGridControler::getEndLoopMs()
{
	return mEndLoopPositionBeat;
}

void MainGridControler::setPositionMs(double position)
{
	mPositionMs = position;
}

double MainGridControler::getPositionMs()
{
	return mPositionMs;
}

void MainGridControler::setDurationMs(double duration)
{
	mDuration = duration;
}

double MainGridControler::getDurationMs()
{
	mDuration;
}

bool MainGridControler::isPlay()
{
	return mIsPlay;
}

double MainGridControler::getBpm()
{
	return mBpm;
}

void MainGridControler::setBpm(double bpm)
{
	mBpm = bpm;
}

double MainGridControler::getPositionBeat()
{
	return mPositionBeat;
}

ChannelItem *MainGridControler::getChannelItems()
{
	return mHead;
}

void MainGridControler::setPositionBeat(double positionBeat)
{

	setUnplayAll();
	mPositionBeat = positionBeat;
	mPositionMs = beatToMs(positionBeat);
	__android_log_print(ANDROID_LOG_DEBUG, "mPositionMs", "mPositionMs %f", mPositionMs);
}

void MainGridControler::setBeatsDuration(float durationBeat)
{
	mDurationBeat = durationBeat;
	mDuration = beatToMs(durationBeat);
	if (mEndLoopPositionBeat > durationBeat)
		mEndLoopPositionBeat = durationBeat;

}

float MainGridControler::getDurationBeat()
{
	return mDurationBeat;
}

void MainGridControler::setUnplayAll()
{


	for (ChannelItem *node = mHead; node != NULL; node = node->next)
	{
		node->mIsPlay = false;

	}
	for (int i = 0; i < 16; ++i)
	{
		mListener->stopPlayChannelCallBack(i);
	}

}

float MainGridControler::getStartPlayPosition()
{
	return mStartLoopPositionBeat;
}

float MainGridControler::getStopPlayPosition()
{
	return mEndLoopPositionBeat;
}

void MainGridControler::resetChannelItemAdded()
{
	mChannelItemAdded = NULL;

}

void MainGridControler::updateChannelAddedEnd(double end)
{
	if (mChannelItemAdded != NULL)
	{
		mChannelItemAdded->mEnd = end;
		mChannelItemAdded->mLength = mChannelItemAdded->mEnd - mChannelItemAdded->mStart;
	}
}

bool MainGridControler::getIsCursorFollowPosition()
{
	return mIsCursorFollowPosition;
}

bool MainGridControler::setIsCursorFollowPosition(bool isFollow)
{
	return mIsCursorFollowPosition;
}
