//
// Created by Yossi Barel on 17/04/16.
//

#include "ADSRFxControler.h"

ADSRFxControler::ADSRFxControler(int bufferSize, int sampleRate, MODFxListener *listener)
{
	mADSR = new ADSRControler(bufferSize, sampleRate);
	mSampleRate = sampleRate;
	mBufferSize = bufferSize;
	mListener = listener;
}

void ADSRFxControler::setADSRValue(int param, double val)
{
	mADSR->setADSRValue(param, val);
}

void ADSRFxControler::proccess()
{

	double val = mADSR->proccess();
	for (map<string, ADSRFx *>::iterator iter = mADSRSend.begin(); iter != mADSRSend.end(); ++iter)
	{
		ADSRFx *adsrFx = iter->second;
		mListener->setFx(adsrFx->keyFx, adsrFx->keyFxParam, adsrFx->start + val * (adsrFx->end - adsrFx->start));
	}

}

void ADSRFxControler::addFx(string index, int keyFx, int keyFxParam, double start, double end, bool isUpdate)
{
	if (!isUpdate)
	{
		ADSRFx *fx = new ADSRFx();
		fx->keyFx = keyFx;
		fx->keyFxParam = keyFxParam;
		fx->start = start;
		fx->end = end;
		mADSRSend[index] = fx;
	} else
	{
		mADSRSend[index]->start = start;
		mADSRSend[index]->end = end;
	}
}

void ADSRFxControler::removeFx(int keyFx, string modIndex)
{
	ADSRFx *fx = mADSRSend[modIndex];
	mADSRSend.erase(modIndex);
	delete (fx);
}

void ADSRFxControler::keyDown()
{
	mADSR->keyDown();
}

void ADSRFxControler::keyRelese()
{
	mADSR->keyRelese();
}

double ADSRFxControler::getParamValue(int param)
{
	return mADSR->getParamValue(param);
}

ModIndex ADSRFxControler::getModState(string modIndex)
{
	return mADSRSend.find(modIndex) != mADSRSend.end() ? ADSR_ENV : NON_MOD;
}

void ADSRFxControler::removeAll()
{
	for (map<string, ADSRFx *>::iterator iter = mADSRSend.begin(); iter != mADSRSend.end(); ++iter)
	{
		ADSRFx *adsrFx = iter->second;
		delete(adsrFx);

	}
	mADSRSend.clear();
}
