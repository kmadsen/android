#include <jni.h>
#include "logcat.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_kmadsen_compass_native_NativeLib_init(
        JNIEnv* pEnv, jobject jobj) {
    logcat_info();
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_kmadsen_compass_native_NativeLib_multiply(
        JNIEnv* pEnv, jobject jobj,
        jint num1,
        jint num2) {
    return num1 * num2;
}

