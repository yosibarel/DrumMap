//
// Created by Yossi Barel on 11/05/16.
//

#include "PianoRoll.h"

#define REMOVE_ALL -1

bool PianoRoll::isNeedPlay(Midi *midi)
{
	return !midi->mIsPlay && midi->mStart <= mPositionBeat && midi->mEnd > mPositionBeat;
}

bool PianoRoll::isNeedStop(Midi *midi)
{
	return midi->mIsPlay && midi->mEnd <= mPositionBeat;
}

void PianoRoll::process()
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


		for (Midi *node = mHead; node != NULL; node = node->next)
		{
			if (isNeedStop(node))
			{
				node->mIsPlay = false;
				mListener->keyPianoRelese();
			}
			if (isNeedPlay(node))
			{
				__android_log_print(ANDROID_LOG_DEBUG, "pianondk", "midi start %f midi end %f midi play %s position %f", node->mStart, node->mEnd, node->mIsPlay ? "true" : "false", mPositionMs);

				node->mIsPlay = true;
				mListener->keyPianoDown(node->mKey);
			}

		}

		mPositionMs += mUnitProccesMs;

		mPositionBeat += msToBeat(mUnitProccesMs);

	}

}

PianoRoll::PianoRoll(int bufferSize, int sampleRate, PianoListener *listener)
{
	mBufferSize = bufferSize;
	mSampleRate = sampleRate;
	mUnitProccesMs = ((double) mBufferSize / mSampleRate) * 1000.0;
	mPositionMs = 0;
	mListener = listener;
	mBpm = 140;
	mDurationBeat = 4;
	mDuration = beatToMs(mDurationBeat);
	mStartLoopPositionBeat = 0;
	mEndLoopPositionBeat = mDurationBeat;
	mPositionBeat = 0;
	mHead = NULL;
	mIndexMidi = 0;
	mIsFollowCursorPosition = false;

	mIsPlay = false;
}


PianoRoll::~PianoRoll()
{

}

double PianoRoll::beatToMs(double beat)
{
	return beat * (60000.0 / mBpm);
}

double PianoRoll::msToBeat(double ms)
{
	return ms / (60000.0 / mBpm);
}

int PianoRoll::addMidi(int key, float start, float end)
{
	Midi *midi = new Midi();
	midi->mStart = start;
	midi->mEnd = end;
	midi->mLength = end - start;
	midi->mKey = key;
	midi->mIsPlay = false;
	midi->mIndex = mIndexMidi++;
	midi->next = NULL;
	//mMidiList.insert(mMidiList.end(), midi);
	__android_log_print(ANDROID_LOG_DEBUG, "pianondk", "midi start %f midi end %f midi play %s position %f", midi->mStart, midi->mEnd, midi->mIsPlay ? "true" : "false", mPositionMs);

	if (mHead == NULL)
	{
		mHead = midi;
	} else if (mHead->next == NULL)
	{
		if (midi->mStart < mHead->mStart)
		{
			midi->next = mHead;
			mHead = midi;
		} else
			mHead->next = midi;
	} else
	{
		bool isInsert = false;
		Midi *node = mHead;
		for (; !isInsert && node->next != NULL; node = node->next)
		{
			if (node->next->mStart > midi->mStart)
			{
				midi->next = node->next;
				node->next = midi;
				isInsert = true;
			}
		}
		if (!isInsert)
			node->next = midi;
	}
	mMidiAdded = midi;
	return midi->mIndex;


}

void PianoRoll::removeMidi(int keyIndex)
{
	if (keyIndex == REMOVE_ALL)
	{
		Midi *node = mHead;
		while (node != NULL)
		{
			Midi *temp = node;
			node = node->next;
			delete (temp);
		}
		mHead = NULL;
	} else
	{
		if (mHead->mIndex == keyIndex)
		{
			Midi *temp = mHead;
			mHead = mHead->next;
			delete temp;

		} else if (mHead->next->mIndex == keyIndex)
		{
			Midi *temp = mHead->next;
			mHead->next = temp->next;
			delete temp;
		} else
		{
			Midi *node = mHead;
			bool mIsDeleted = false;
			for (; !mIsDeleted && node->next != NULL; node = node->next)
			{
				if (node->next->mIndex == keyIndex)
				{
					Midi *temp = node->next;
					node->next = temp->next;
					mIsDeleted = true;
					delete temp;
				}
			}

		}
	}
}
//Midi *midi = mMidiList[keyIndex];
//mMidiList.erase(mMidiList.begin() + keyIndex);
//	delete(midi);


void PianoRoll::play()
{
	mIsPlay = true;
}

void PianoRoll::pause()
{
	mIsPlay = false;
}

void PianoRoll::setStartEndLoopPositionPercent(double start, double end)
{
	mStartLoopPositionBeat = start * mDurationBeat;
	mEndLoopPositionBeat = end * mDurationBeat;
	__android_log_print(ANDROID_LOG_DEBUG, "pianostartend", "start = %d end = %d", (int) mStartLoopPositionBeat, (int) mEndLoopPositionBeat);
	if (mPositionBeat < mStartLoopPositionBeat || mPositionBeat > mEndLoopPositionBeat)
	{
		mPositionBeat = mStartLoopPositionBeat;
		mPositionMs = beatToMs(mPositionBeat);
	}


}

double PianoRoll::getStartLoopMs()
{
	return mStartLoopPositionBeat;
}

double PianoRoll::getEndLoopMs()
{
	return mEndLoopPositionBeat;
}

void PianoRoll::setPositionMs(double position)
{
	mPositionMs = position;
}

double PianoRoll::getPositionMs()
{
	return mPositionMs;
}

void PianoRoll::setDurationMs(double duration)
{
	mDuration = duration;
	mDurationBeat = msToBeat(mDuration);
}

double PianoRoll::getDurationMs()
{
	mDuration;
}

bool PianoRoll::isPlay()
{
	return mIsPlay;
}

double PianoRoll::getBpm()
{
	return mBpm;
}

void PianoRoll::setBpm(double bpm)
{
	mBpm = bpm;
}

double PianoRoll::getPianoPositionBeat()
{
	return mPositionBeat;
}

Midi *PianoRoll::getMidis()
{
	return mHead;
}

void PianoRoll::setPositionBeat(double positionBeat)
{

	setUnplayAll();
	mPositionBeat = positionBeat;
	mPositionMs = beatToMs(positionBeat);
	__android_log_print(ANDROID_LOG_DEBUG, "mPositionMs", "mPositionMs %f", mPositionMs);
}

void PianoRoll::setPianoBeatsDuration(float durationBeat)
{
	mDurationBeat = durationBeat;
	mDuration = beatToMs(durationBeat);
	if (mEndLoopPositionBeat > durationBeat)
		mEndLoopPositionBeat = durationBeat;

}

float PianoRoll::getPianoDurationBeat()
{
	return mDurationBeat;
}

void PianoRoll::setUnplayAll()
{


	for (Midi *node = mHead; node != NULL; node = node->next)
	{
		node->mIsPlay = false;
	}

}

float PianoRoll::getPianoStartPlayPosition()
{
	return mStartLoopPositionBeat;
}

float PianoRoll::getPianoStopPlayPosition()
{
	return mEndLoopPositionBeat;
}

void PianoRoll::updateMidiAddedEnd(float endMidiBeat)
{
	if (mMidiAdded != NULL)
	{
		mMidiAdded->mEnd = endMidiBeat;
		mMidiAdded->mLength = mMidiAdded->mEnd - mMidiAdded->mStart;
	}
}

void PianoRoll::resetMidiAdded()
{
	mMidiAdded = NULL;
}

void PianoRoll::setIsPianoCursorFollowPosition(bool isFollow)
{
	mIsFollowCursorPosition = isFollow;
}

bool PianoRoll::getIsCursorFollowPosition()
{
	return mIsFollowCursorPosition;
}

void PianoRoll::updateMidiPiano(int indexMidi, int note, float start, float end)
{
	Midi *midi = getMidiByIndexMidi(indexMidi);
	midi->mStart = start;
	midi->mEnd = end;
	midi->mLength = midi->mEnd - midi->mStart;
	midi->mIsPlay = false;
	midi->mKey = note;
}

Midi *PianoRoll::getMidiByIndexMidi(int midiIndex)
{


	for (Midi *node = mHead; node != NULL; node = node->next)
	{
		if (node->mIndex == midiIndex)
			return node;
	}
	return NULL;
}
