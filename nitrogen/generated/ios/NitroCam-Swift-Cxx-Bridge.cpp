///
/// NitroCam-Swift-Cxx-Bridge.cpp
/// This file was generated by nitrogen. DO NOT MODIFY THIS FILE.
/// https://github.com/mrousavy/nitro
/// Copyright © 2025 Marc Rousavy @ Margelo
///

#include "NitroCam-Swift-Cxx-Bridge.hpp"

// Include C++ implementation defined types
#include "HybridNitroCamSpecSwift.hpp"
#include "NitroCam-Swift-Cxx-Umbrella.hpp"

namespace margelo::nitro::nitrocam::bridge::swift {

  // pragma MARK: std::shared_ptr<margelo::nitro::nitrocam::HybridNitroCamSpec>
  std::shared_ptr<margelo::nitro::nitrocam::HybridNitroCamSpec> create_std__shared_ptr_margelo__nitro__nitrocam__HybridNitroCamSpec_(void* _Nonnull swiftUnsafePointer) {
    NitroCam::HybridNitroCamSpec_cxx swiftPart = NitroCam::HybridNitroCamSpec_cxx::fromUnsafe(swiftUnsafePointer);
    return std::make_shared<margelo::nitro::nitrocam::HybridNitroCamSpecSwift>(swiftPart);
  }
  void* _Nonnull get_std__shared_ptr_margelo__nitro__nitrocam__HybridNitroCamSpec_(std__shared_ptr_margelo__nitro__nitrocam__HybridNitroCamSpec_ cppType) {
    std::shared_ptr<margelo::nitro::nitrocam::HybridNitroCamSpecSwift> swiftWrapper = std::dynamic_pointer_cast<margelo::nitro::nitrocam::HybridNitroCamSpecSwift>(cppType);
  #ifdef NITRO_DEBUG
    if (swiftWrapper == nullptr) [[unlikely]] {
      throw std::runtime_error("Class \"HybridNitroCamSpec\" is not implemented in Swift!");
    }
  #endif
    NitroCam::HybridNitroCamSpec_cxx& swiftPart = swiftWrapper->getSwiftPart();
    return swiftPart.toUnsafe();
  }

} // namespace margelo::nitro::nitrocam::bridge::swift
