#include <jni.h>
#include "NitroCamOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::nitrocam::initialize(vm);
}
