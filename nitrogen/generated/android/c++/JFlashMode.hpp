///
/// JFlashMode.hpp
/// This file was generated by nitrogen. DO NOT MODIFY THIS FILE.
/// https://github.com/mrousavy/nitro
/// Copyright © 2025 Marc Rousavy @ Margelo
///

#pragma once

#include <fbjni/fbjni.h>
#include "FlashMode.hpp"

namespace margelo::nitro::nitrocam {

  using namespace facebook;

  /**
   * The C++ JNI bridge between the C++ enum "FlashMode" and the the Kotlin enum "FlashMode".
   */
  struct JFlashMode final: public jni::JavaClass<JFlashMode> {
  public:
    static auto constexpr kJavaDescriptor = "Lcom/margelo/nitro/nitrocam/FlashMode;";

  public:
    /**
     * Convert this Java/Kotlin-based enum to the C++ enum FlashMode.
     */
    [[maybe_unused]]
    [[nodiscard]]
    FlashMode toCpp() const {
      static const auto clazz = javaClassStatic();
      static const auto fieldOrdinal = clazz->getField<int>("_ordinal");
      int ordinal = this->getFieldValue(fieldOrdinal);
      return static_cast<FlashMode>(ordinal);
    }

  public:
    /**
     * Create a Java/Kotlin-based enum with the given C++ enum's value.
     */
    [[maybe_unused]]
    static jni::alias_ref<JFlashMode> fromCpp(FlashMode value) {
      static const auto clazz = javaClassStatic();
      static const auto fieldAUTO = clazz->getStaticField<JFlashMode>("AUTO");
      static const auto fieldON = clazz->getStaticField<JFlashMode>("ON");
      static const auto fieldOFF = clazz->getStaticField<JFlashMode>("OFF");
      
      switch (value) {
        case FlashMode::AUTO:
          return clazz->getStaticFieldValue(fieldAUTO);
        case FlashMode::ON:
          return clazz->getStaticFieldValue(fieldON);
        case FlashMode::OFF:
          return clazz->getStaticFieldValue(fieldOFF);
        default:
          std::string stringValue = std::to_string(static_cast<int>(value));
          throw std::invalid_argument("Invalid enum value (" + stringValue + "!");
      }
    }
  };

} // namespace margelo::nitro::nitrocam
