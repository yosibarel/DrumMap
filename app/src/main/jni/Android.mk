LOCAL_PATH := $(call my-dir)
SUPERPOWERED_PATH := $(LOCAL_PATH) //../../../../../Superpowered

include $(CLEAR_VARS)
LOCAL_MODULE := Superpowered
ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
	LOCAL_SRC_FILES := ../libs/armeabi-v7a/libSuperpoweredAndroidARM.a
else
	ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
		LOCAL_SRC_FILES := ../libs/arm64-v8a/libSuperpoweredAndroidARM64.a
	else
		ifeq ($(TARGET_ARCH_ABI),x86_64)
			LOCAL_SRC_FILES := ../libs/x86_64/libSuperpoweredAndroidX86_64.a
		else
			LOCAL_SRC_FILES := ../libs/x86/libSuperpoweredAndroidX86.a
		endif
	endif
endif
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_LDLIBS := -llog

LOCAL_MODULE := drummapjni
LOCAL_SRC_FILES := \
	Filter.cpp \
	com_yossibarel_drummap_DrumMapJni.cpp\
	WaveSinusCreator.cpp \
	WaveRectangleCreator.cpp \
	WaveTriangleCreator.cpp \
	WaveSawCreator.cpp \
	FxControler.cpp \
	Fx.cpp \
	SeqControler.cpp \
	LFO.cpp \
	ADSRFxControler.cpp \
	ADSRControler.cpp \
	Recorder.cpp \
    LFOControler.cpp \
    XYControler.cpp \
	DrumMap.cpp \
	PianoRoll.cpp \
	Channel.cpp\
	MainGridControler.cpp\
 	SuperpoweredAndroidAudioIO.cpp

LOCAL_C_INCLUDES := $(SUPERPOWERED_PATH)

LOCAL_LDLIBS := -llog -landroid -lOpenSLES
LOCAL_STATIC_LIBRARIES := Superpowered

include $(BUILD_SHARED_LIBRARY)