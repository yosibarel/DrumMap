//
// Created by Yossi Barel on 31/03/16.
//

#ifndef DRUMMAP_CHANNELLISTENER_H
#define DRUMMAP_CHANNELLISTENER_H

#endif //DRUMMAP_CHANNELLISTENER_H

class ChannelListener
{
public :
	virtual void onNeedStopProccess(int index) = 0;

	virtual void onLoadFileEvent(SuperpoweredAdvancedAudioPlayerEvent event, int indexChannel) = 0;

	virtual void onPianoKeyDown(int index)=0;
	virtual void onPianoKeyRelsese(int index)=0;
};



