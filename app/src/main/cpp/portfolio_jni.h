// portfolio_jni.h
#pragma once

#include <jni.h>

extern "C" {

JNIEXPORT jlong JNICALL Java_com_example_PortfolioPulsar_NativeWrapper_createPortfolio(JNIEnv *env, jobject instance);
JNIEXPORT void JNICALL Java_com_example_PortfolioPulsar_NativeWrapper_destroyPortfolio(JNIEnv *env, jobject instance, jlong portfolioPtr);

JNIEXPORT void JNICALL Java_com_example_PortfolioPulsar_NativeWrapper_addStock(JNIEnv *env, jobject instance, jlong portfolioPtr, jstring symbol, jint quantity, jdouble purchasePrice);
JNIEXPORT void JNICALL Java_com_example_PortfolioPulsar_NativeWrapper_removeStock(JNIEnv *env, jobject instance, jlong portfolioPtr, jstring symbol);

// Add more JNI functions as needed

}