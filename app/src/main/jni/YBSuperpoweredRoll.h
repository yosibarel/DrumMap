//
// Created by Yossi Barel on 07/08/16.
//

#ifndef DRUMMAP_YBSUPERPOWEREDROLL_H
#define DRUMMAP_YBSUPERPOWEREDROLL_H

#include "SuperpoweredRoll.h"
#include "Mixable.h"

class YBSuperpoweredRoll : public Mixable {
public:
    YBSuperpoweredRoll(int sampleRate) {
        mSuperpoweredRoll=new SuperpoweredRoll(sampleRate);
    }

    SuperpoweredRoll *mSuperpoweredRoll;

    void setBpm(int param);

    void enable(bool flag);

    double getWet();

    bool getEnable();

    void setBeat(double d);

    void setWet(float d);

private:
    SuperpoweredRoll* mSuperpoweredRoll;
};


#endif //DRUMMAP_YBSUPERPOWEREDROLL_H
