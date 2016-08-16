#include "com_yossibarel_drummap_DrumMapJni.h"
#include "DrumMap.h"
#include "PianoRoll.h"

static DrumMap *drumMap;
static JavaVM *g_jvm = NULL;
static jclass jcls;
static jobject jobj;
static jmethodID jnicallback;

static jfloatArray tracksViewMeters;
static jfloatArray waveForm;

void callBackToJava(CallBackJavaTypeMessage callBackType, int channel)
{
	__android_log_print(ANDROID_LOG_DEBUG, "ndk", "callBackToJava");
	JNIEnv *env;
	g_jvm->GetEnv((void **) &env, JNI_VERSION_1_6);
	g_jvm->AttachCurrentThread(&env, NULL);


	jnicallback = env->GetMethodID(jcls, "jnicallback", "(II)V");
	env->CallVoidMethod(jobj, jnicallback, channel, (int) callBackType);


	g_jvm->DetachCurrentThread();


}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_drumMap(JNIEnv *env, jobject obj, jint sampleRate, jint bufferSize, jstring tempFileRecord)
{
	env->GetJavaVM(&g_jvm);
	jcls = (jclass) env->NewGlobalRef(env->FindClass("com/yossibarel/drummap/DrumMapJni"));
	jobj = (jobject) env->NewGlobalRef(obj);
	const char *ctempFileRecord = env->GetStringUTFChars(tempFileRecord, 0);

	drumMap = new DrumMap(sampleRate, bufferSize, callBackToJava, ctempFileRecord);
	env->ReleaseStringUTFChars(tempFileRecord, ctempFileRecord);

	jfloatArray jviewMeters = env->NewFloatArray(NUM_CHANNELS * 2);
	tracksViewMeters = (jfloatArray) env->NewGlobalRef(jviewMeters);

	jfloatArray jwaveForm = env->NewFloatArray(bufferSize * 2);
	waveForm = (jfloatArray) env->NewGlobalRef(jwaveForm);
}


JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setEnvADSR(JNIEnv *, jobject, jint envType, jint index, jint type, jdouble value)
{
	drumMap->setEnvADSR(envType, index, type, value);
}


JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_keyDown(JNIEnv *env, jobject instance, jint index)
{

	drumMap->keyDown(index);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_keyRelese(JNIEnv *env, jobject instance, jint index)
{

	drumMap->keyRelese(index);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setFx(JNIEnv *env, jobject instance, jint mIndexChannel, jint fxKey, jint fxKeyParam, jfloat damp)
{

	drumMap->setFx(mIndexChannel, fxKey, fxKeyParam, damp);

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_addEffect(JNIEnv *env, jobject instance, jint mChannelIndex, jint indexFx)
{

	return drumMap->addFx(mChannelIndex, indexFx);

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_openChannelFile(JNIEnv *env, jobject instance, jint channelIndex, jstring path_, jlong length)
{
	const char *path = env->GetStringUTFChars(path_, 0);

	drumMap->openChannelFile(channelIndex, path, length);

	env->ReleaseStringUTFChars(path_, path);
}

JNIEXPORT jlong JNICALL Java_com_yossibarel_drummap_DrumMapJni_getDurationMs(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getDurationMs(mIndexChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_loopBetween(JNIEnv *env, jobject instance, jint channelIndex, jdouble start, jdouble end, jboolean startPlay)
{

	drumMap->loopBetween(channelIndex, start, end, startPlay);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setReverse(JNIEnv *env, jobject instance, jint indexChannel, jboolean flag)
{

	drumMap->setReverse(indexChannel, flag);

}


JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setLFORateWave(JNIEnv *env, jobject instance, jint mChannelIndex, jint lfo, jdouble rate)
{
	drumMap->setLFORateWave(mChannelIndex, lfo, rate);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setLFOPresentWave(JNIEnv *env, jobject instance, jint mChannelIndex, jdouble present)
{

	drumMap->setLFOPresentWave(mChannelIndex, present);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setLFOAmplitudeWave(JNIEnv *env, jobject instance, jint mChannelIndex, jint lfo, jdouble amp)
{

	drumMap->setLFOAmplitudeWave(mChannelIndex, lfo, amp);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setLFOTypeWave(JNIEnv *env, jobject instance, jint mChannelIndex, jint lfo, jint type)
{

	drumMap->setLFOTypeWave(mChannelIndex, lfo, type);

}


JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_sendFxToLFO(JNIEnv *env, jobject instance, jstring indexLfo, jint channelIndex, jint fxType, jint keyEffectParam, jint lfo1, jint lfo2, jdouble start, jdouble end, jboolean isUpdate)
{

	const char *cIndexLFo = env->GetStringUTFChars(indexLfo, 0);


	std::string sIndexLfo(cIndexLFo);
	drumMap->sendFxToLFO(sIndexLfo, channelIndex, fxType, keyEffectParam, lfo1, lfo2, start, end, isUpdate);
	env->ReleaseStringUTFChars(indexLfo, cIndexLFo);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_sendFxToControlXY(JNIEnv *env, jobject instance, jstring mIndexControl, jint indexChannel, jint control, jint mFxType, jint keyEffectParam, jdouble start, jdouble end, jboolean isUpdate)
{
	const char *cIndexControl = env->GetStringUTFChars(mIndexControl, 0);
	drumMap->sendFxToControlXY(string(cIndexControl), control, indexChannel, mFxType, keyEffectParam, start, end, isUpdate);

	env->ReleaseStringUTFChars(mIndexControl, cIndexControl);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_sendFxToADSR(JNIEnv *env, jobject instance, jstring mIndexControl, jint indexChannel, jint mFxType, jint keyEffectParam, jdouble start, jdouble end, jboolean isUpdate)
{
	const char *cIndexControl = env->GetStringUTFChars(mIndexControl, 0);
	drumMap->sendFxToADSR(indexChannel, string(cIndexControl), mFxType, keyEffectParam, start, end, isUpdate);
	env->ReleaseStringUTFChars(mIndexControl, cIndexControl);
}


JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_sendFxToCurve(JNIEnv *env, jobject instance, jstring indexString_, jint indexChannel, jint curve, jint mFxType, jint keyEffectParam, jdouble start, jdouble end, jboolean isUpdate)
{
	const char *indexString = env->GetStringUTFChars(indexString_, 0);

	drumMap->sendFxToCurve(string(indexString), indexChannel, curve, mFxType, keyEffectParam, start, end, isUpdate);

	env->ReleaseStringUTFChars(indexString_, indexString);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setControlXYValue(JNIEnv *env, jobject instance, jint control, jdouble val)
{

	drumMap->setXYControlValue(control, val);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_scretch(JNIEnv *env, jobject instance, jdouble value)
{

	drumMap->scretch(value);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_resetRecord(JNIEnv *env, jobject instance)
{

	drumMap->resetRecord();

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_startRecord(JNIEnv *env, jobject instance, jstring srecordFile)
{
	const char *crecordFile = env->GetStringUTFChars(srecordFile, 0);

	drumMap->startRecord(crecordFile, true);

	env->ReleaseStringUTFChars(srecordFile, crecordFile);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_stopRecord(JNIEnv *env, jobject instance)
{

	drumMap->stopRecord();

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_startPlayRecord(JNIEnv *env, jobject instance)
{

	return drumMap->startPlayRecord();

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_stopPlayRecord(JNIEnv *env, jobject instance)
{

	drumMap->stopPlayRecord();

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getRecordPosition(JNIEnv *env, jobject instance)
{

	return drumMap->getRecordPosition();

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setScratchEnable(JNIEnv *env, jobject instance, jint channel, jboolean scratchEnable)
{

	drumMap->setScratchEnable(channel, scratchEnable);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setStopPlayOnRelease(JNIEnv *env, jobject instance, jint mIndexChannel, jboolean checked)
{

	drumMap->setStopPlayOnRelease(mIndexChannel, checked);

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_getStopPlayOnRelease(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getStopPlayOnRelease(mIndexChannel);

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getPositionPrecent(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getPositionPrecent(mIndexChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setPitchBand(JNIEnv *env, jobject instance, jdouble pitch)
{

	drumMap->setPitchBend(pitch);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_enableSequencerPattern(JNIEnv *env, jobject instance, jint indexChannel, jint indexPattern, jboolean enable)
{

	drumMap->enableSequencerPattern(indexChannel, indexPattern, enable);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setValueSequencerPattern(JNIEnv *env, jobject instance, jint indexChannel, jint indexPattern, jint indexValue, jdouble val)
{

	drumMap->setValueSequencerPattern(indexChannel, indexPattern, indexValue, val);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_enableSequencer(JNIEnv *env, jobject instance, jint indexChannel, jboolean enable)
{

	drumMap->enableSequencer(indexChannel, enable);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setSeqRate(JNIEnv *env, jobject instance, jint mIndexChannel, jint progress)
{

	drumMap->setSeqRate(mIndexChannel, progress);

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getSeqPattern(JNIEnv *env, jobject instance, jint indexChannel, jint indexPattern, jint indexValue)
{

	return drumMap->getSeqPattern(indexChannel, indexPattern, indexValue);

}

JNIEXPORT jint JNICALL Java_com_yossibarel_drummap_DrumMapJni_getSeqIndexPlaying(JNIEnv *env, jobject instance, jint indexChannel)
{

	return drumMap->getSeqIndexPlaying(indexChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_resetSeq(JNIEnv *env, jobject instance, jint mIndexChannel, jint mIndexValue)
{

	drumMap->resetSeq(mIndexChannel, mIndexValue);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setPitch(JNIEnv *env, jobject instance, jint mIndexChannel, jdouble pitch)
{

	drumMap->setPitchChannel(mIndexChannel, pitch);

}

JNIEXPORT jintArray JNICALL Java_com_yossibarel_drummap_DrumMapJni_getActiveEffect(JNIEnv *env, jobject instance, jint mChannelIndex)
{

	jintArray result;

	vector<int> vecFxOrder = drumMap->getActiveEffect(mChannelIndex);
	result = env->NewIntArray(vecFxOrder.size());

	// fill a temp structure to use to populate the java int array
	jint fill[vecFxOrder.size()];
	int i = 0;
	for (std::vector<int>::iterator iter = vecFxOrder.begin(); iter != vecFxOrder.end(); ++iter, ++i)
	{
		fill[i] = (*iter);
	}
	// move from the temp structure to the java structure
	(env)->SetIntArrayRegion(result, 0, vecFxOrder.size(), fill);
	return result;

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setPlayFromStartLoop(JNIEnv *env, jobject instance, jint indexChannel, jboolean isPlayFromStartLoop)
{

	drumMap->setPlayFromStartLoop(indexChannel, isPlayFromStartLoop);

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_getPlayFromStartLoop(JNIEnv *env, jobject instance, jint indexChannel)
{

	return drumMap->getPlayFromStartLoop(indexChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setLoop(JNIEnv *env, jobject instance, jint indexChannel, jboolean isLoop)
{

	drumMap->setLoop(indexChannel, isLoop);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setBpm(JNIEnv *env, jobject instance, jdouble bpm)
{

	drumMap->setBpm(bpm);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_sendFxToSensorXY(JNIEnv *env, jobject instance, jstring mIndexLfo_, jint mIndex, jint control, jint mFxType, jint keyEffectParam)
{
	const char *mIndexLfo = env->GetStringUTFChars(mIndexLfo_, 0);

	// TODO

	env->ReleaseStringUTFChars(mIndexLfo_, mIndexLfo);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_startGlobalRecord(JNIEnv *env, jobject instance, jstring file_)
{
	const char *file = env->GetStringUTFChars(file_, 0);

	drumMap->startGlobalRecord(file);

	env->ReleaseStringUTFChars(file_, file);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_enableFilter(JNIEnv *env, jobject instance, jint indexChannel, jboolean enable)
{

	drumMap->enableFilter(indexChannel, enable);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setVolume(JNIEnv *env, jobject instance, jint indexChannel, jdouble vol)
{

	drumMap->setVolume(indexChannel, vol);

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getFxValue(JNIEnv *env, jobject instance, jint indexChannel, jint fxType, jint fxKeyParam)
{

	return drumMap->getFxValue(indexChannel, fxType, fxKeyParam);

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getEnvADSR(JNIEnv *env, jobject instance, jint adsrType, jint indexChannel, jint adsrParam)
{

	return drumMap->getEnvADSR(adsrType, indexChannel, adsrParam);

}

JNIEXPORT jint JNICALL Java_com_yossibarel_drummap_DrumMapJni_getLFOTypeWave(JNIEnv *env, jobject instance, jint channelIndex, jint lfoType)
{

	return drumMap->getLFOTypeWave(channelIndex, lfoType);

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getLFOAmplitudeWave(JNIEnv *env, jobject instance, jint mChannelIndex, jint lfoType)
{

	return drumMap->getLFOAmplitudeWave(mChannelIndex, lfoType);

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getLFORateWave(JNIEnv *env, jobject instance, jint indexChannel, jint lfo)
{

	return drumMap->getLFORateWave(indexChannel, lfo);

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getLfoWavePresent(JNIEnv *env, jobject instance, jint mChannelIndex)
{

	return drumMap->getLfoWavePresent(mChannelIndex);

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getVolume(JNIEnv *env, jobject instance, jint indexChannel)
{

	return drumMap->getVolume(indexChannel);

}

JNIEXPORT jint JNICALL Java_com_yossibarel_drummap_DrumMapJni_getModState(JNIEnv *env, jobject instance, jint indexChannel, jstring indexMod_)
{
	const char *indexMod = env->GetStringUTFChars(indexMod_, 0);

	return drumMap->getModState(indexChannel, string(indexMod));

	env->ReleaseStringUTFChars(indexMod_, indexMod);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_removeModFx(JNIEnv *env, jobject instance, jint indexChannel, jstring indexMod_, jint keyEffectParam, jint mod)
{
	const char *indexMod = env->GetStringUTFChars(indexMod_, 0);

	drumMap->removeModFx(indexChannel, string(indexMod), keyEffectParam, mod);

	env->ReleaseStringUTFChars(indexMod_, indexMod);
}

JNIEXPORT jint JNICALL Java_com_yossibarel_drummap_DrumMapJni_getCurrentFilter(JNIEnv *env, jobject instance, jint indexChannel)
{

	return drumMap->getCurrentFilter(indexChannel);

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getMainPosition(JNIEnv *env, jobject instance)
{

	return drumMap->getMainPositionMs();

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setMainQuantize(JNIEnv *env, jobject instance, jint quntizeIndex)
{

	drumMap->setMainQuantize(quntizeIndex);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setQuantizeEnable(JNIEnv *env, jobject instance, jboolean enable)
{

	drumMap->setQuantizeEnable(enable);

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_getIsPlay(JNIEnv *env, jobject instance, jint indexChannel)
{

	return drumMap->getIsPlay(indexChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_playChannel(JNIEnv *env, jobject instance, jint channelIndex)
{

	drumMap->playChannel(channelIndex);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_stopPlayChannel(JNIEnv *env, jobject instance, jint channelIndex)
{

	drumMap->stopPlayChannel(channelIndex);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setStereo(JNIEnv *env, jobject instance, jint mIndexChannel, jdouble left, jdouble right, int progress)
{

	drumMap->setStereo(mIndexChannel, left, right, progress);

}

JNIEXPORT jint JNICALL Java_com_yossibarel_drummap_DrumMapJni_getStereoProgress(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getStereoProgress(mIndexChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setMainChannelPitch(JNIEnv *env, jobject instance, jint mIndexChannel, jdouble mPitch)
{

	drumMap->setMainChannelPitch(mIndexChannel, mPitch);
}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getMainChannelPitch(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getMainChannelPitch(mIndexChannel);

}

JNIEXPORT jfloatArray JNICALL Java_com_yossibarel_drummap_DrumMapJni_getAllViewMeters(JNIEnv *env, jobject instance)
{


	float vm[32];
	float *f = vm;


	drumMap->initViewMeter(&f);


	env->SetFloatArrayRegion(tracksViewMeters, 0, 32, vm);
	return tracksViewMeters;

}

JNIEXPORT jfloat JNICALL Java_com_yossibarel_drummap_DrumMapJni_getViewMeterChannelLeft(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getViewMeterChannelLeft(mIndexChannel);

}

JNIEXPORT jfloat JNICALL Java_com_yossibarel_drummap_DrumMapJni_getViewMeterChannelRight(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getViewMeterChannelRight(mIndexChannel);

}

JNIEXPORT jint JNICALL Java_com_yossibarel_drummap_DrumMapJni_getSeqRateProgress(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getSeqRateProgress(mIndexChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setSeqStepState(JNIEnv *env, jobject instance, jint state, jint indexChannel)
{

	drumMap->setSeqStepState(state, indexChannel);

}

JNIEXPORT jint JNICALL Java_com_yossibarel_drummap_DrumMapJni_getSeqStepState(JNIEnv *env, jobject instance, jint indexChannel)
{

	drumMap->getSeqStepState(indexChannel);

}


JNIEXPORT jfloatArray JNICALL Java_com_yossibarel_drummap_DrumMapJni_getWaveform(JNIEnv *env, jobject instance, jint mIndexChannel, jint bufferSize)
{
	float wf[bufferSize * 2];
	float *pwf = wf;


	drumMap->initWaveform(mIndexChannel, &pwf);


	env->SetFloatArrayRegion(waveForm, 0, bufferSize * 2, wf);
	return waveForm;


}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_openChannelFileForEdit(JNIEnv *env, jobject instance, jint channelIndex, jstring jpath, jlong length)
{
	const char *path = env->GetStringUTFChars(jpath, 0);

	drumMap->openChannelFileForEdit(channelIndex, path, length);

	env->ReleaseStringUTFChars(jpath, path);
}

JNIEXPORT jfloatArray JNICALL Java_com_yossibarel_drummap_DrumMapJni_getEditBuffer(JNIEnv *env, jobject instance, jint sizeByte)
{


	float **editBuffer = drumMap->getEditBuffer();
	jfloatArray jarr = env->NewFloatArray(sizeByte / sizeof(float));

	env->SetFloatArrayRegion(jarr, 0, sizeByte / sizeof(float), *editBuffer);

	//env->ReleaseFloatArrayElements(jarr, *editBuffer, NULL);
	free(*editBuffer);
	*editBuffer = NULL;
	return jarr;

}

JNIEXPORT jbyteArray JNICALL Java_com_yossibarel_drummap_DrumMapJni_getFilePath(JNIEnv *env, jobject instance, jint mIndexChannel)
{


	const char *filePath = drumMap->getFilePath(mIndexChannel);
	if (filePath == NULL)
		return NULL;

	int byteCount = strlen(filePath);
	const jbyte *pNativeMessage = reinterpret_cast<const jbyte *>(filePath);
	jbyteArray bytes = env->NewByteArray(byteCount);
	env->SetByteArrayRegion(bytes, 0, byteCount, pNativeMessage);

	return bytes;
}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getStopPlayPosition(JNIEnv *env, jobject instance, jint mChannelIndex)
{

	return drumMap->getStopPlayPosition(mChannelIndex);

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getStartPlayPosition(JNIEnv *env, jobject instance, jint mChannelIndex)
{

	return drumMap->getStartPlayPosition(mChannelIndex);

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_getIsLoaded(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getIsLoaded(mIndexChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setPosition(JNIEnv *env, jobject instance, jint mChannelIndex, jdouble positionMs)
{

	drumMap->setPositionMs(mChannelIndex, positionMs);

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_getIsLoop(JNIEnv *env, jobject instance, jint mChannelIndex)
{

	return drumMap->getIsLoop(mChannelIndex);

}

JNIEXPORT jint JNICALL Java_com_yossibarel_drummap_DrumMapJni_getBpm(JNIEnv *env, jobject instance)
{

	return drumMap->getBpm();

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_playPiano(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	drumMap->playPiano(mIndexChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_pausePiano(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	drumMap->pausePiano(mIndexChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_removeMidiPiano(JNIEnv *env, jobject instance, jint mIndexChannel, jint mIndex)
{

	drumMap->removeMidiPiano(mIndexChannel, mIndex);

}

JNIEXPORT jint JNICALL Java_com_yossibarel_drummap_DrumMapJni_addMidiPiano(JNIEnv *env, jobject instance, jint mIndexChannel, jint key, jfloat mStart, jfloat mEnd)
{

	return drumMap->addMidiPiano(mIndexChannel, key, mStart, mEnd);
}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getPianoPosition(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getPositionMsPiano(mIndexChannel);

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getPianoPositionBeat(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getPianoPositionBeat(mIndexChannel);

}

JNIEXPORT jfloatArray JNICALL Java_com_yossibarel_drummap_DrumMapJni_getMidiFloat(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	Midi *mHead = drumMap->getMidis(mIndexChannel);
	//(float *) malloc(midiList.size()*sizeof(float)*3);
	int size = 0;
	for (Midi *node = mHead; node != NULL; node = node->next)
	{
		++size;
	}
	float midifloat[size * 4];
	int i = 0;
	for (Midi *node = mHead; node != NULL; node = node->next)
	{
		midifloat[i * 4] = node->mStart;
		midifloat[i * 4 + 1] = node->mLength;
		midifloat[i * 4 + 2] = node->mKey;
		midifloat[i * 4 + 3] = node->mIndex;
		++i;
	}


	jfloatArray jarr = env->NewFloatArray(size * 4);

	env->SetFloatArrayRegion(jarr, 0, size * 4, midifloat);



	//env->ReleaseFloatArrayElements(jarr, *editBuffer, NULL);
	//free(midifloat);

	return jarr;

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setPianoPositionBeat(JNIEnv *env, jobject instance, jint mIndexChannel, jdouble pianoPosition)
{

	drumMap->setPianoPositionBeat(mIndexChannel, pianoPosition);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setPianoBeatsDuration(JNIEnv *env, jobject instance, jint mIndexChannel, jfloat mDuration)
{

	drumMap->setPianoBeatsDuration(mIndexChannel, mDuration);

}

JNIEXPORT jfloat JNICALL Java_com_yossibarel_drummap_DrumMapJni_getPianoDurationBeat(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getPianoDurationBeat(mIndexChannel);

}

JNIEXPORT jfloat JNICALL Java_com_yossibarel_drummap_DrumMapJni_getPianoStartPlayPosition(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getPianoStartPlayPosition(mIndexChannel);

}

JNIEXPORT jfloat JNICALL Java_com_yossibarel_drummap_DrumMapJni_getPianoStopPlayPosition(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getPianoStopPlayPosition(mIndexChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setStartEndLoopPositionPrecentPiano(JNIEnv *env, jobject instance, jint indexChannel, jdouble start, jdouble end)
{

	drumMap->setStartEndLoopPositionPrecentPiano(indexChannel, start, end);

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_getIsPianoPlay(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getIsPianoPlay(mIndexChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setSeqState(JNIEnv *env, jobject instance, jint state, jint mIndexChannel)
{

	drumMap->setSeqState(mIndexChannel, state);

}

JNIEXPORT jint JNICALL Java_com_yossibarel_drummap_DrumMapJni_getSeqState(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getSeqState(mIndexChannel);

}

JNIEXPORT jint JNICALL Java_com_yossibarel_drummap_DrumMapJni_getSeqIndexEnable(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getSeqIndexEnable(mIndexChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setSeqIndexEnable(JNIEnv *env, jobject instance, jint mIndexChannel, jint indexSeq)
{

	drumMap->setSeqIndexEnable(mIndexChannel, indexSeq);

}

JNIEXPORT jint JNICALL Java_com_yossibarel_drummap_DrumMapJni_addChannelItem(JNIEnv *env, jobject instance, jint channel, jdouble start, jdouble end)
{

	return drumMap->addChannelItem(channel, start, end);

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getMainPositionBeat(JNIEnv *env, jobject instance)
{

	return drumMap->getMainPositionBeat();

}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getMainDurationBeat(JNIEnv *env, jobject instance)
{

	return drumMap->getMainDurationBeat();

}

JNIEXPORT jfloatArray JNICALL Java_com_yossibarel_drummap_DrumMapJni_getChannelItemsFloat(JNIEnv *env, jobject instance)
{

	ChannelItem *mHead = drumMap->getChannelItems();
	//(float *) malloc(midiList.size()*sizeof(float)*3);
	int size = 0;
	for (ChannelItem *node = mHead; node != NULL; node = node->next)
	{
		++size;
	}
	float channelFloat[size * 4];
	int i = 0;
	for (ChannelItem *node = mHead; node != NULL; node = node->next)
	{
		channelFloat[i * 4] = node->mStart;
		channelFloat[i * 4 + 1] = node->mLength;
		channelFloat[i * 4 + 2] = node->mChannel;
		channelFloat[i * 4 + 3] = node->mIndex;
		++i;
	}


	jfloatArray jarr = env->NewFloatArray(size * 4);

	env->SetFloatArrayRegion(jarr, 0, size * 4, channelFloat);



	//env->ReleaseFloatArrayElements(jarr, *editBuffer, NULL);
	//free(midifloat);

	return jarr;

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_removeChannelItem(JNIEnv *env, jobject instance, jint index)
{

	drumMap->removeChannelItem(index);

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_getIsMainGridPlay(JNIEnv *env, jobject instance)
{

	return drumMap->getIsMainGridPlay();

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_playMainGrid(JNIEnv *env, jobject instance)
{

	drumMap->playMainGrid();

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_pauseMainGrid(JNIEnv *env, jobject instance)
{

	drumMap->pauseMainGrid();

}

JNIEXPORT jfloat JNICALL Java_com_yossibarel_drummap_DrumMapJni_getMainGridStartPlayPosition(JNIEnv *env, jobject instance)
{

	return drumMap->getMainGridStartPlayPosition();

}

JNIEXPORT jfloat JNICALL Java_com_yossibarel_drummap_DrumMapJni_getMainGridStopPlayPosition(JNIEnv *env, jobject instance)
{

	return drumMap->getMainGridStopPlayPosition();

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setMainGridBeatsDuration(JNIEnv *env, jobject instance, jfloat mDuration)
{

	drumMap->setMainGridBeatsDuration(mDuration);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setMainGridPositionBeat(JNIEnv *env, jobject instance, jdouble positionBeat)
{

	drumMap->setMainGridPositionBeat(positionBeat);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setStartEndLoopPositionPrecentMainGrid(JNIEnv *env, jobject instance, jdouble startPlayPositionPercent, jdouble endPlayPositionPercent)
{

	drumMap->setStartEndLoopPositionPrecentMainGrid(startPlayPositionPercent, endPlayPositionPercent);

}

JNIEXPORT jint JNICALL Java_com_yossibarel_drummap_DrumMapJni_getChannelColor(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getChannelColor(mIndexChannel);

}


JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setChannelColor(JNIEnv *env, jobject instance, jint mIndexChannel, jint color)
{
	drumMap->setChannelColor(mIndexChannel, color);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_resetChannelItemAdded(JNIEnv *env, jobject instance)
{

	drumMap->resetChannelItemAdded();

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_updateChannelAddedEnd(JNIEnv *env, jobject instance, jdouble mEnd)
{

	drumMap->updateChannelAddedEnd(mEnd);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_updateMidiAddedEnd(JNIEnv *env, jobject instance, jint mIndexChannel, jfloat mEnd)
{

	drumMap->updateMidiAddedEnd(mIndexChannel, mEnd);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_resetMidiAdded(JNIEnv *env, jobject instance, jint indexChannel)
{

	drumMap->resetMidiAdded(indexChannel);
}

JNIEXPORT jfloatArray JNICALL Java_com_yossibarel_drummap_DrumMapJni_getRecordWaveform(JNIEnv *env, jobject instance, jint bufferSize)
{

	float wf[bufferSize * 2];
	float *pwf = wf;


	drumMap->initRecordWaveform(&pwf);


	env->SetFloatArrayRegion(waveForm, 0, bufferSize * 2, wf);
	return waveForm;

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setIsPianoCursorFollowPosition(JNIEnv *env, jobject instance, jint mIndexChannel, jboolean isFollow)
{

	drumMap->setIsPianoCursorFollowPosition(mIndexChannel, isFollow);

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_getIsPianoCursorFollowPosition(JNIEnv *env, jobject instance, jint mIndexChannel)
{

	return drumMap->getIsPianoCursorFollowPosition(mIndexChannel);

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_getIsGridCursorFollowPosition(JNIEnv *env, jobject instance)
{

	return drumMap->getIsGridCursorFollowPosition();

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setIsGridCursorFollowPosition(JNIEnv *env, jobject instance, jboolean isFollow)
{

	drumMap->setIsGridCursorFollowPosition(isFollow);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_updateMidiPiano(JNIEnv *env, jobject instance, jint mIndexChannel, jint mIndexKey, jint note, jfloat mStart, jfloat mEnd)
{

	drumMap->updateMidiPiano(mIndexChannel, mIndexKey, note, mStart, mEnd);

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_getIsMuted(JNIEnv *env, jobject instance, jint mChannel)
{

	return drumMap->getIsMuted(mChannel);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setMuteed(JNIEnv *env, jobject instance, jint mChannel, jboolean checked)
{

	drumMap->setMuted(mChannel, checked);
}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_getIsSolo(JNIEnv *env, jobject instance, jint mChannel)
{

	return drumMap->getIsSolo(mChannel);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setSolo(JNIEnv *env, jobject instance, jint mChannel, jboolean checked)
{

	drumMap->setSolo(mChannel, checked);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_randSeq(JNIEnv *env, jobject instance, jint mIndexChannel, jint mIndexValue)
{

	drumMap->randSeq(mIndexChannel, mIndexValue);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_replaceFxChannelPosition(JNIEnv *env, jobject instance, jint mChannel, jint fromPosition, jint toPosition)
{

	drumMap->replaceFxChannelPosition(mChannel, fromPosition, toPosition);

}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_deleteEffectChannel(JNIEnv *env, jobject instance, jint mIndexChannel, jint index)
{

	drumMap->deleteEffectChannel(mIndexChannel, index);
}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_isEnbableSendFx1(JNIEnv *env, jobject instance, jint mChannel)
{

	return drumMap->isEnbableSendFx1(mChannel);

}

JNIEXPORT jboolean JNICALL Java_com_yossibarel_drummap_DrumMapJni_isEnbableSendFx2(JNIEnv *env, jobject instance, jint mChannel)
{

	return drumMap->isEnbableSendFx2(mChannel);
}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getSendFx1(JNIEnv *env, jobject instance, jint mChannel)
{

	return drumMap->getSendFx1(mChannel);
}

JNIEXPORT jdouble JNICALL Java_com_yossibarel_drummap_DrumMapJni_getSendFx2(JNIEnv *env, jobject instance, jint mChannel)
{

	return drumMap->getSendFx2(mChannel);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_enableSendFx1(JNIEnv *env, jobject instance, jint mChannel, jboolean checked)
{

	drumMap->enableSendFx1(mChannel, checked);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_enableSendFx2(JNIEnv *env, jobject instance, jint mChannel, jboolean checked)
{

	drumMap->enableSendFx2(mChannel, checked);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setSendFx1(JNIEnv *env, jobject instance, jint mChannel, jdouble v)
{

	drumMap->setSendFx1(mChannel, v);
}

JNIEXPORT void JNICALL Java_com_yossibarel_drummap_DrumMapJni_setSendFx2(JNIEnv *env, jobject instance, jint mChannel, jdouble v)
{

	drumMap->setSendFx2(mChannel, v);
}