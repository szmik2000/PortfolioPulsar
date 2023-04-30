// portfolio_jni.cpp
#include "portfolio_jni.h"
#include "portfolio.h"
#include "stock.h"

JNIEXPORT jlong JNICALL Java_com_example_PortfolioPulsar_NativeWrapper_createPortfolio(JNIEnv *env, jobject instance) {
Portfolio *portfolio = new Portfolio();
return reinterpret_cast<jlong>(portfolio);
}

JNIEXPORT void JNICALL Java_com_example_PortfolioPulsar_NativeWrapper_destroyPortfolio(JNIEnv *env, jobject instance, jlong portfolioPtr) {
Portfolio *portfolio = reinterpret_cast<Portfolio *>(portfolioPtr);
delete portfolio;
}

JNIEXPORT void JNICALL Java_com_example_PortfolioPulsar_NativeWrapper_addStock(JNIEnv *env, jobject instance, jlong portfolioPtr, jstring symbol, jint quantity, jdouble purchasePrice) {
Portfolio *portfolio = reinterpret_cast<Portfolio *>(portfolioPtr);
const char *symbolChars = env->GetStringUTFChars(symbol, nullptr);
Stock stock(symbolChars, quantity, purchasePrice);
env->ReleaseStringUTFChars(symbol, symbolChars);
portfolio->addStock(stock);
}

JNIEXPORT void JNICALL Java_com_example_PortfolioPulsar_NativeWrapper_removeStock(JNIEnv *env, jobject instance, jlong portfolioPtr, jstring symbol) {
Portfolio *portfolio = reinterpret_cast<Portfolio *>(portfolioPtr);
const char *symbolChars = env->GetStringUTFChars(symbol, nullptr);
std::string symbolStr(symbolChars);
env->ReleaseStringUTFChars(symbol, symbolChars);
portfolio->removeStock(symbolStr);
}

// Add more JNI functions as needed
