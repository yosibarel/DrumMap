//
// Created by Yossi Barel on 11/05/16.
//

#ifndef DRUMMAP_PIANOLISTENER_H
#define DRUMMAP_PIANOLISTENER_H

#include "ADSRFxControler.h"

class PianoListener
{
public:
	virtual void keyPianoDown(double key) = 0;

	virtual void keyPianoRelese() = 0;
};

#endif //DRUMMAP_PIANOLISTENER_H
