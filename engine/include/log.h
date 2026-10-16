#ifndef LOG_H
#define LOG_H

#ifdef __ANDROID__
#include <android/log.h>
#define LOG_TAG "MonolithEngine"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#else
#include <stdio.h>
#define LOGD(...) printf("[D] " __VA_ARGS__); printf("\n")
#define LOGI(...) printf("[I] " __VA_ARGS__); printf("\n")
#define LOGW(...) printf("[W] " __VA_ARGS__); printf("\n")
#define LOGE(...) printf("[E] " __VA_ARGS__); printf("\n")
#endif

#endif /* LOG_H */
