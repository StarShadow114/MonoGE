#ifndef _JNI_H
#define _JNI_H

#include <stdint.h>

/* JNI types */
typedef int32_t  jint;
typedef int64_t  jlong;
typedef float    jfloat;
typedef double   jdouble;
typedef char     jchar;
typedef int8_t   jbyte;
typedef int16_t  jshort;
typedef int32_t  jboolean;
typedef int64_t  jsize;

typedef void*    jobject;
typedef void*    jclass;
typedef void*    jstring;
typedef void*    jmethodID;
typedef void*    jfieldID;
typedef void*    JNIEnv;

/* JNI calling conventions - THE MISSING PART */
#define JNIEXPORT
#define JNICALL

#endif /* _JNI_H */
