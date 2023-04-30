#include <jni.h>
#include <string>
#include <algorithm>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_PortfolioPulsar_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++XD";
    return env->NewStringUTF(hello.c_str());
}

std::string caesarCipher(const std::string &input, int shift) {
    std::string output = input;

    for (char &c : output) {
        if (isalpha(c)) {
            char offset = isupper(c) ? 'A' : 'a';
            c = static_cast<char>((c - offset + shift) % 26 + offset);
        }
    }
    return output;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_PortfolioPulsar_NativeWrapper_encode(JNIEnv* env, jobject /* this */, jstring input) {
    const char *inputChars = env->GetStringUTFChars(input, nullptr);
    std::string encodedMessage = caesarCipher(inputChars, 3);
    env->ReleaseStringUTFChars(input, inputChars);
    return env->NewStringUTF(encodedMessage.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_PortfolioPulsar_NativeWrapper_decode(JNIEnv* env, jobject /* this */, jstring input) {
    const char *inputChars = env->GetStringUTFChars(input, nullptr);
    std::string decodedMessage = caesarCipher(inputChars, 23); // Reverse shift of 3
    env->ReleaseStringUTFChars(input, inputChars);
    return env->NewStringUTF(decodedMessage.c_str());
}