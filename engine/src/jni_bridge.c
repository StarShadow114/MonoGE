#include "jni_bridge.h"
#include "monolith.h"
#include "log.h"

JNIEXPORT jint JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_init(JNIEnv *env, jclass clazz)
{
    (void)env;
    (void)clazz;
    LOGI("JNI: init() called from Kotlin");
    return monolith_init();
}

JNIEXPORT void JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_update(JNIEnv *env, jclass clazz, jfloat dt)
{
    (void)env;
    (void)clazz;
    monolith_update(dt);
}

JNIEXPORT void JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_render(JNIEnv *env, jclass clazz)
{
    (void)env;
    (void)clazz;
    monolith_render();
}

JNIEXPORT void JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_shutdown(JNIEnv *env, jclass clazz)
{
    (void)env;
    (void)clazz;
    LOGI("JNI: shutdown() called from Kotlin");
    monolith_shutdown();
}

JNIEXPORT jstring JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_getVersion(JNIEnv *env, jclass clazz)
{
    (void)clazz;
    return (*env)->NewStringUTF(env, MONOLITH_VERSION_STRING);
}
