#ifndef LOG_H
#define LOG_H

#ifdef __ANDROID__
#include <android/log.h>
#define LOG_TAG "Monolith"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,   LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,    LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,    LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,   LOG_TAG, __VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,   LOG_TAG, __VA_ARGS__)
#else
#include <stdio.h>
#define LOGV(fmt, ...) fprintf(stdout, "[V/Monolith] " fmt "\n", ##__VA_ARGS__)
#define LOGD(fmt, ...) fprintf(stdout, "[D/Monolith] " fmt "\n", ##__VA_ARGS__)
#define LOGI(fmt, ...) fprintf(stdout, "[I/Monolith] " fmt "\n", ##__VA_ARGS__)
#define LOGW(fmt, ...) fprintf(stderr, "[W/Monolith] " fmt "\n", ##__VA_ARGS__)
#define LOGE(fmt, ...) fprintf(stderr, "[E/Monolith] " fmt "\n", ##__VA_ARGS__)
#define LOGF(fmt, ...) fprintf(stderr, "[F/Monolith] " fmt "\n", ##__VA_ARGS__)
#endif

#endif // LOG_H
