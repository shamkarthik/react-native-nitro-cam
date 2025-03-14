///
/// HybridNitroCamComponent.cpp
/// This file was generated by nitrogen. DO NOT MODIFY THIS FILE.
/// https://github.com/mrousavy/nitro
/// Copyright © 2025 Marc Rousavy @ Margelo
///

#include "HybridNitroCamComponent.hpp"

#include <string>
#include <exception>
#include <utility>
#include <NitroModules/NitroDefines.hpp>
#include <NitroModules/JSIConverter.hpp>
#include <react/renderer/core/RawValue.h>
#include <react/renderer/core/ShadowNode.h>
#include <react/renderer/core/ComponentDescriptor.h>
#include <react/renderer/components/view/ViewProps.h>

namespace margelo::nitro::nitrocam::views {

  extern const char HybridNitroCamComponentName[] = "NitroCam";

  HybridNitroCamProps::HybridNitroCamProps(const react::PropsParserContext& context,
                                           const HybridNitroCamProps& sourceProps,
                                           const react::RawProps& rawProps):
    react::ViewProps(context, sourceProps, rawProps, filterObjectKeys),
    isRed([&]() -> CachedProp<bool> {
      try {
        const react::RawValue* rawValue = rawProps.at("isRed", nullptr, nullptr);
        if (rawValue == nullptr) return sourceProps.isRed;
        const auto& [runtime, value] = (std::pair<jsi::Runtime*, jsi::Value>)*rawValue;
        return CachedProp<bool>::fromRawValue(*runtime, value, sourceProps.isRed);
      } catch (const std::exception& exc) {
        throw std::runtime_error(std::string("NitroCam.isRed: ") + exc.what());
      }
    }()),
    isFrontCamera([&]() -> CachedProp<bool> {
      try {
        const react::RawValue* rawValue = rawProps.at("isFrontCamera", nullptr, nullptr);
        if (rawValue == nullptr) return sourceProps.isFrontCamera;
        const auto& [runtime, value] = (std::pair<jsi::Runtime*, jsi::Value>)*rawValue;
        return CachedProp<bool>::fromRawValue(*runtime, value, sourceProps.isFrontCamera);
      } catch (const std::exception& exc) {
        throw std::runtime_error(std::string("NitroCam.isFrontCamera: ") + exc.what());
      }
    }()),
    flash([&]() -> CachedProp<FlashMode> {
      try {
        const react::RawValue* rawValue = rawProps.at("flash", nullptr, nullptr);
        if (rawValue == nullptr) return sourceProps.flash;
        const auto& [runtime, value] = (std::pair<jsi::Runtime*, jsi::Value>)*rawValue;
        return CachedProp<FlashMode>::fromRawValue(*runtime, value, sourceProps.flash);
      } catch (const std::exception& exc) {
        throw std::runtime_error(std::string("NitroCam.flash: ") + exc.what());
      }
    }()),
    zoom([&]() -> CachedProp<double> {
      try {
        const react::RawValue* rawValue = rawProps.at("zoom", nullptr, nullptr);
        if (rawValue == nullptr) return sourceProps.zoom;
        const auto& [runtime, value] = (std::pair<jsi::Runtime*, jsi::Value>)*rawValue;
        return CachedProp<double>::fromRawValue(*runtime, value, sourceProps.zoom);
      } catch (const std::exception& exc) {
        throw std::runtime_error(std::string("NitroCam.zoom: ") + exc.what());
      }
    }()),
    hybridRef([&]() -> CachedProp<std::optional<std::function<void(const std::shared_ptr<margelo::nitro::nitrocam::HybridNitroCamSpec>& /* ref */)>>> {
      try {
        const react::RawValue* rawValue = rawProps.at("hybridRef", nullptr, nullptr);
        if (rawValue == nullptr) return sourceProps.hybridRef;
        const auto& [runtime, value] = (std::pair<jsi::Runtime*, jsi::Value>)*rawValue;
        return CachedProp<std::optional<std::function<void(const std::shared_ptr<margelo::nitro::nitrocam::HybridNitroCamSpec>& /* ref */)>>>::fromRawValue(*runtime, value.asObject(*runtime).getProperty(*runtime, "f"), sourceProps.hybridRef);
      } catch (const std::exception& exc) {
        throw std::runtime_error(std::string("NitroCam.hybridRef: ") + exc.what());
      }
    }()) { }

  HybridNitroCamProps::HybridNitroCamProps(const HybridNitroCamProps& other):
    react::ViewProps(),
    isRed(other.isRed),
    isFrontCamera(other.isFrontCamera),
    flash(other.flash),
    zoom(other.zoom),
    hybridRef(other.hybridRef) { }

  bool HybridNitroCamProps::filterObjectKeys(const std::string& propName) {
    switch (hashString(propName)) {
      case hashString("isRed"): return true;
      case hashString("isFrontCamera"): return true;
      case hashString("flash"): return true;
      case hashString("zoom"): return true;
      case hashString("hybridRef"): return true;
      default: return false;
    }
  }

  HybridNitroCamComponentDescriptor::HybridNitroCamComponentDescriptor(const react::ComponentDescriptorParameters& parameters)
    : ConcreteComponentDescriptor(parameters,
                                  react::RawPropsParser(/* enableJsiParser */ true)) {}

  react::Props::Shared HybridNitroCamComponentDescriptor::cloneProps(const react::PropsParserContext& context,
                                                                     const react::Props::Shared& props,
                                                                     react::RawProps rawProps) const {
    // 1. Prepare raw props parser
    rawProps.parse(rawPropsParser_);
    // 2. Copy props with Nitro's cached copy constructor
    return HybridNitroCamShadowNode::Props(context, /* & */ rawProps, props);
  }

#ifdef ANDROID
  void HybridNitroCamComponentDescriptor::adopt(react::ShadowNode& shadowNode) const {
    // This is called immediately after `ShadowNode` is created, cloned or in progress.
    // On Android, we need to wrap props in our state, which gets routed through Java and later unwrapped in JNI/C++.
    auto& concreteShadowNode = dynamic_cast<HybridNitroCamShadowNode&>(shadowNode);
    const HybridNitroCamProps& props = concreteShadowNode.getConcreteProps();
    HybridNitroCamState state;
    state.setProps(props);
    concreteShadowNode.setStateData(std::move(state));
  }
#endif

} // namespace margelo::nitro::nitrocam::views
