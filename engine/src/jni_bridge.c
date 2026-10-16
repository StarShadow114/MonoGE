#include "jni_bridge.h"
#include "monolith.h"
#include "input.h"

#ifdef __ANDROID__
#include <jni.h>

extern input_queue_t g_input;

JNIEXPORT jint JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_init(JNIEnv *env, jobject thiz, jobject surface, jint w, jint h) {
    (void)env; (void)thiz;
    return monolith_init((void *)surface, w, h);
}

JNIEXPORT void JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_update(JNIEnv *env, jobject thiz, jfloat dt) {
    (void)env; (void)thiz;
    monolith_update(dt);
}

JNIEXPORT void JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_render(JNIEnv *env, jobject thiz) {
    (void)env; (void)thiz;
    monolith_render();
}

JNIEXPORT void JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_shutdown(JNIEnv *env, jobject thiz) {
    (void)env; (void)thiz;
    monolith_shutdown();
}

JNIEXPORT jstring JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_getVersion(JNIEnv *env, jobject thiz) {
    (void)thiz;
    return (*env)->NewStringUTF(env, monolith_get_version());
}

JNIEXPORT void JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_injectTouch(JNIEnv *env, jobject thiz, jint action, jint x, jint y) {
    (void)env; (void)thiz;
    input_inject_touch(&g_input, action, x, y);
}

JNIEXPORT void JNICALL
Java_com_starshadowstudio_game_noa_MonolithNative_injectKey(JNIEnv *env, jobject thiz, jint action, jint keycode) {
    (void)env; (void)thiz;
    input_inject_key(&g_input, action, keycode);
}

#else

/* Non-Android stub implementations - satisfy linker */
int Java_com_starshadowstudio_game_noa_MonolithNative_init(void) { return 0; }
void Java_com_starshadowstudio_game_noa_MonolithNative_update(float dt) { (void)dt; }
void Java_com_starshadowstudio_game_noa_MonolithNative_render(void) {}
void Java_com_starshadowstudio_game_noa_MonolithNative_shutdown(void) {}
char* Java_com_starshadowstudio_game_noa_MonolithNative_getVersion(void) { return "0.2.0-stage2"; }
void Java_com_starshadowstudio_game_noa_MonolithNative_injectTouch(int a, int x, int y) { (void)a; (void)x; (void)y; }
void Java_com_starshadowstudio_game_noa_MonolithNative_injectKey(int a, int k) { (void)a; (void)k; }

#endif
