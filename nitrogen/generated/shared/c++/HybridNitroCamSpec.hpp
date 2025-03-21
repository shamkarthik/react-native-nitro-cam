///
/// HybridNitroCamSpec.hpp
/// This file was generated by nitrogen. DO NOT MODIFY THIS FILE.
/// https://github.com/mrousavy/nitro
/// Copyright © 2025 Marc Rousavy @ Margelo
///

#pragma once

#if __has_include(<NitroModules/HybridObject.hpp>)
#include <NitroModules/HybridObject.hpp>
#else
#error NitroModules cannot be found! Are you sure you installed NitroModules properly?
#endif

// Forward declaration of `FlashMode` to properly resolve imports.
namespace margelo::nitro::nitrocam { enum class FlashMode; }

#include "FlashMode.hpp"
#include <string>

namespace margelo::nitro::nitrocam {

  using namespace margelo::nitro;

  /**
   * An abstract base class for `NitroCam`
   * Inherit this class to create instances of `HybridNitroCamSpec` in C++.
   * You must explicitly call `HybridObject`'s constructor yourself, because it is virtual.
   * @example
   * ```cpp
   * class HybridNitroCam: public HybridNitroCamSpec {
   * public:
   *   HybridNitroCam(...): HybridObject(TAG) { ... }
   *   // ...
   * };
   * ```
   */
  class HybridNitroCamSpec: public virtual HybridObject {
    public:
      // Constructor
      explicit HybridNitroCamSpec(): HybridObject(TAG) { }

      // Destructor
      ~HybridNitroCamSpec() override = default;

    public:
      // Properties
      virtual bool getIsFrontCamera() = 0;
      virtual void setIsFrontCamera(bool isFrontCamera) = 0;
      virtual FlashMode getFlash() = 0;
      virtual void setFlash(FlashMode flash) = 0;
      virtual double getZoom() = 0;
      virtual void setZoom(double zoom) = 0;

    public:
      // Methods
      virtual void switchCamera() = 0;
      virtual void setFlashMode(FlashMode mode) = 0;
      virtual void setZoomLevel(double level) = 0;
      virtual std::string takePhoto() = 0;

    protected:
      // Hybrid Setup
      void loadHybridMethods() override;

    protected:
      // Tag for logging
      static constexpr auto TAG = "NitroCam";
  };

} // namespace margelo::nitro::nitrocam
