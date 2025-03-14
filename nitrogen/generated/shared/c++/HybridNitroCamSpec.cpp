///
/// HybridNitroCamSpec.cpp
/// This file was generated by nitrogen. DO NOT MODIFY THIS FILE.
/// https://github.com/mrousavy/nitro
/// Copyright © 2025 Marc Rousavy @ Margelo
///

#include "HybridNitroCamSpec.hpp"

namespace margelo::nitro::nitrocam {

  void HybridNitroCamSpec::loadHybridMethods() {
    // load base methods/properties
    HybridObject::loadHybridMethods();
    // load custom methods/properties
    registerHybrids(this, [](Prototype& prototype) {
      prototype.registerHybridGetter("isRed", &HybridNitroCamSpec::getIsRed);
      prototype.registerHybridSetter("isRed", &HybridNitroCamSpec::setIsRed);
    });
  }

} // namespace margelo::nitro::nitrocam
