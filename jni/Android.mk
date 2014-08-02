LOCAL_PATH := $(call my-dir)

# Build libpyjni.so
include $(CLEAR_VARS)
LOCAL_MODULE    := pyjni
LOCAL_SRC_FILES := pyjni.c
LOCAL_CFLAGS := -I /home/mariogrip/Desktop/Android-Electrum/python-for-android/dist/default/python-install/include/python2.7/
LOCAL_LDFLAGS += -L /home/mariogrip/Desktop/Android-Electrum/python-for-android/dist/default/libs/armeabi/
LOCAL_SHARED_LIBRARIES += python2.7  # This line links to libpython2.7
LOCAL_LDLIBS += -llog                # This line links to the Android log
include $(BUILD_SHARED_LIBRARY)

# Include libpython2.7.so
include $(CLEAR_VARS)
LOCAL_MODULE := python2.7
LOCAL_SRC_FILES := /home/mariogrip/Desktop/Android-Electrum/python-for-android/dist/default/libs/armeabi/libpython2.7.so
include $(PREBUILT_SHARED_LIBRARY)