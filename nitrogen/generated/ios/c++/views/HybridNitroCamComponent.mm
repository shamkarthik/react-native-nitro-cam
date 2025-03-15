///
/// HybridNitroCamComponent.mm
/// This file was generated by nitrogen. DO NOT MODIFY THIS FILE.
/// https://github.com/mrousavy/nitro
/// Copyright © 2025 Marc Rousavy @ Margelo
///

#import "HybridNitroCamComponent.hpp"
#import <memory>
#import <react/renderer/componentregistry/ComponentDescriptorProvider.h>
#import <React/RCTViewComponentView.h>
#import <React/RCTComponentViewFactory.h>
#import <React/UIView+ComponentViewProtocol.h>
#import <NitroModules/NitroDefines.hpp>
#import <UIKit/UIKit.h>

#import "HybridNitroCamSpecSwift.hpp"
#import "NitroCam-Swift-Cxx-Umbrella.hpp"

using namespace facebook;
using namespace margelo::nitro::nitrocam;
using namespace margelo::nitro::nitrocam::views;

/**
 * Represents the React Native View holder for the Nitro "NitroCam" HybridView.
 */
@interface HybridNitroCamComponent: RCTViewComponentView
@end

@implementation HybridNitroCamComponent {
  std::shared_ptr<HybridNitroCamSpecSwift> _hybridView;
}

+ (void) load {
  [super load];
  [RCTComponentViewFactory.currentComponentViewFactory registerComponentViewClass:[HybridNitroCamComponent class]];
}

+ (react::ComponentDescriptorProvider) componentDescriptorProvider {
  return react::concreteComponentDescriptorProvider<HybridNitroCamComponentDescriptor>();
}

- (instancetype) init {
  if (self = [super init]) {
    std::shared_ptr<HybridNitroCamSpec> hybridView = NitroCam::NitroCamAutolinking::createNitroCam();
    _hybridView = std::dynamic_pointer_cast<HybridNitroCamSpecSwift>(hybridView);
    [self updateView];
  }
  return self;
}

- (void) updateView {
  // 1. Get Swift part
  NitroCam::HybridNitroCamSpec_cxx& swiftPart = _hybridView->getSwiftPart();

  // 2. Get UIView*
  void* viewUnsafe = swiftPart.getView();
  UIView* view = (__bridge_transfer UIView*) viewUnsafe;

  // 3. Update RCTViewComponentView's [contentView]
  [self setContentView:view];
}

- (void) updateProps:(const react::Props::Shared&)props
            oldProps:(const react::Props::Shared&)oldProps {
  // 1. Downcast props
  const auto& newViewPropsConst = *std::static_pointer_cast<HybridNitroCamProps const>(props);
  auto& newViewProps = const_cast<HybridNitroCamProps&>(newViewPropsConst);
  NitroCam::HybridNitroCamSpec_cxx& swiftPart = _hybridView->getSwiftPart();

  // 2. Update each prop individually
  swiftPart.beforeUpdate();

  // isFrontCamera: boolean
  if (newViewProps.isFrontCamera.isDirty) {
    swiftPart.setIsFrontCamera(newViewProps.isFrontCamera.value);
    newViewProps.isFrontCamera.isDirty = false;
  }
  // flash: enum
  if (newViewProps.flash.isDirty) {
    swiftPart.setFlash(static_cast<int>(newViewProps.flash.value));
    newViewProps.flash.isDirty = false;
  }
  // zoom: number
  if (newViewProps.zoom.isDirty) {
    swiftPart.setZoom(newViewProps.zoom.value);
    newViewProps.zoom.isDirty = false;
  }

  swiftPart.afterUpdate();

  // 3. Update hybridRef if it changed
  if (newViewProps.hybridRef.isDirty) {
    // hybridRef changed - call it with new this
    const auto& maybeFunc = newViewProps.hybridRef.value;
    if (maybeFunc.has_value()) {
      maybeFunc.value()(_hybridView);
    }
    newViewProps.hybridRef.isDirty = false;
  }

  // 4. Continue in base class
  [super updateProps:props oldProps:oldProps];
}

@end
