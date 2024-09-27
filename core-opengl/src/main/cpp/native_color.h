//
// Created by Administrator on 2024/9/26.
//
#include <jni.h>
/* Header for class com_onzhou_opengles_color_NativeColorRenderer */

#ifndef COM_ONZHOU_OPENGLES_COLOR_NATIVECOLORRENDERER
#define COM_ONZHOU_OPENGLES_COLOR_NATIVECOLORRENDERER
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL surfaceCreated(JNIEnv *, jobject, jint);

JNIEXPORT void JNICALL surfaceChanged(JNIEnv *, jobject, jint, jint);

JNIEXPORT void JNICALL onDrawFrame(JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif