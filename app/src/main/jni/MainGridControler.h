//
// Created by Yossi Barel on 05/06/16.
//

#ifndef DRUMMAP_MAINGRIDCONTROLER_H
#define DRUMMAP_MAINGRIDCONTROLER_H
#include "Recorder.h"
#include "MainGridControllerListener.h"
typedef struct ChannelItem
{

	int mChannel;
	float mStart;
	float mEnd;
	float mLength;
	bool mIsPlay;
	int mIndex;
	ChannelItem * next;
} ChannelItem;
class MainGridControler
{
public :

	MainGridControler(int bufferSize, int sampleRate, MainGridControllerListener *listener);

	~MainGridControler();

	int addChannelItem(int key, float start, float mEnd);

	void removeChannelItem(int keyIndex);


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


	double getPositionBeat();

	ChannelItem * getChannelItems();

	void setPositionBeat(double positionBeat);

	void setBeatsDuration(float durationBeat);

	float getDurationBeat();

	float getStartPlayPosition();

	float getStopPlayPosition();

	void resetChannelItemAdded();

	void updateChannelAddedEnd(double end);

	bool getIsCursorFollowPosition();

	bool setIsCursorFollowPosition(bool isFollow);

private :

	int mIndexChannelItem;
	ChannelItem *mCurrentChannelItem;
	ChannelItem* mHead;
	void setUnplayAll();
	double beatToMs(double beat);
	double msToBeat(double ms);
	bool isNeedPlay(ChannelItem *channelItem);

	bool isNeedStop(ChannelItem *channelItem);

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
	MainGridControllerListener *mListener;

	double mDurationBeat;


	ChannelItem *mChannelItemAdded;


	bool mIsCursorFollowPosition;
};


#endif //DRUMMAP_MAINGRIDCONTROLER_H
