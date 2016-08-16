//
// Created by Yossi Barel on 07/04/16.
//

#ifndef DRUMMAP_XYCONTROLLISTENER_H
#define DRUMMAP_XYCONTROLLISTENER_H

class XYControlerListener
{
public:
	virtual void setFx(int indexChannel, int fxFey, int fxKeyParam, float value) = 0;
};


#endif //DRUMMAP_XYCONTROLLISTENER_H
