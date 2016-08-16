//
// Created by Yossi Barel on 11/05/16.
//

#ifndef DRUMMAP_PIANOROLL_H
#define DRUMMAP_PIANOROLL_H

#include "PianoListener.h"

typedef struct Midi
{

	int mKey;
	float mStart;
	float mEnd;
	float mLength;
	bool mIsPlay;
	int mIndex;
	Midi *next;
} Midi;

class PianoRoll
{

public :

	PianoRoll(int bufferSize, int sampleRate, PianoListener *listener);

	~PianoRoll();

	int addMidi(int key, float start, float mEnd);

	void removeMidi(int keyIndex);


	void process();

	void play();

	void pause();

	void setStartEndLoopPositionPercent(double start, double end);

	double getStartLoopMs();

	double getEndLoopMs();

	void setPositionMs(double position);

	double getPositionMs();

	void setDurationMs(double duration);

	double getDurationMs();

	bool isPlay();

	void setBpm(double bpm);

	double getBpm();


	double getPianoPositionBeat();

	Midi *getMidis();

	void setPositionBeat(double positionBeat);

	void setPianoBeatsDuration(float durationBeat);

	float getPianoDurationBeat();

	float getPianoStartPlayPosition();

	float getPianoStopPlayPosition();

	void updateMidiAddedEnd(float endMidiBeat);

	void resetMidiAdded();

	void setIsPianoCursorFollowPosition(bool isFollow);

	bool getIsCursorFollowPosition();

	void updateMidiPiano(int indexMidi, int note, float start, float end);

private :

	int mIndexMidi;
	Midi *mCurrentMidi;
	Midi *mHead;

	void setUnplayAll();

	double beatToMs(double beat);

	double msToBeat(double ms);

	bool isNeedPlay(Midi *midi);

	bool isNeedStop(Midi *midi);

	int mBufferSize;
	int mSampleRate;
	double mUnitProccesMs;
	double mDuration;
	double mPositionMs;
	double mStartLoopPositionBeat;
	double mEndLoopPositionBeat;
	double mPositionBeat;
	bool mIsPlay;
	double mBpm;
	PianoListener *mListener;

	double mDurationBeat;
	Midi *mMidiAdded;

	bool mIsFollowCursorPosition;

	Midi *getMidiByIndexMidi(int midiIndex);
};


#endif //DRUMMAP_PIANOROLL_H
