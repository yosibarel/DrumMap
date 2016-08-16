//
// Created by Yossi Barel on 05/06/16.
//

#ifndef DRUMMAP_MAINGRIDCONTROLLERLISTENER_H
#define DRUMMAP_MAINGRIDCONTROLLERLISTENER_H


class MainGridControllerListener
{

public:
	virtual void startPlayChannelCallBack(int channel) = 0;

	virtual void stopPlayChannelCallBack(int channel) = 0;
};


#endif //DRUMMAP_MAINGRIDCONTROLLERLISTENER_H

