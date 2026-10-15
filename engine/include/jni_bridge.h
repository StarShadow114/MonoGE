#ifndef JNI_BRIDGE_H
#define JNI_BRIDGE_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_init(JNIEnv *env, jclass clazz);

JNIEXPORT void JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_update(JNIEnv *env, jclass clazz, jfloat dt);

JNIEXPORT void JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_render(JNIEnv *env, jclass clazz);

JNIEXPORT void JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_shutdown(JNIEnv *env, jclass clazz);

JNIEXPORT jstring JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_getVersion(JNIEnv *env, jclass clazz);

#ifdef __cplusplus
}
#endif

#endif // JNI_BRIDGE_H
