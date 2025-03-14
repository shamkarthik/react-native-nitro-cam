///
/// NitroCamAutolinking.mm
/// This file was generated by nitrogen. DO NOT MODIFY THIS FILE.
/// https://github.com/mrousavy/nitro
/// Copyright © 2025 Marc Rousavy @ Margelo
///

#import <Foundation/Foundation.h>
#import <NitroModules/HybridObjectRegistry.hpp>
#import "NitroCam-Swift-Cxx-Umbrella.hpp"
#import <type_traits>

#include "HybridNitroCamSpecSwift.hpp"

@interface NitroCamAutolinking : NSObject
@end

@implementation NitroCamAutolinking

+ (void) load {
  using namespace margelo::nitro;
  using namespace margelo::nitro::nitrocam;

  HybridObjectRegistry::registerHybridObjectConstructor(
    "NitroCam",
    []() -> std::shared_ptr<HybridObject> {
      std::shared_ptr<margelo::nitro::nitrocam::HybridNitroCamSpec> hybridObject = NitroCam::NitroCamAutolinking::createNitroCam();
      return hybridObject;
    }
  );
}

@end
