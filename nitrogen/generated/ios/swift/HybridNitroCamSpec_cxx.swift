///
/// HybridNitroCamSpec_cxx.swift
/// This file was generated by nitrogen. DO NOT MODIFY THIS FILE.
/// https://github.com/mrousavy/nitro
/// Copyright © 2025 Marc Rousavy @ Margelo
///

import Foundation
import NitroModules

/**
 * A class implementation that bridges HybridNitroCamSpec over to C++.
 * In C++, we cannot use Swift protocols - so we need to wrap it in a class to make it strongly defined.
 *
 * Also, some Swift types need to be bridged with special handling:
 * - Enums need to be wrapped in Structs, otherwise they cannot be accessed bi-directionally (Swift bug: https://github.com/swiftlang/swift/issues/75330)
 * - Other HybridObjects need to be wrapped/unwrapped from the Swift TCxx wrapper
 * - Throwing methods need to be wrapped with a Result<T, Error> type, as exceptions cannot be propagated to C++
 */
public class HybridNitroCamSpec_cxx {
  /**
   * The Swift <> C++ bridge's namespace (`margelo::nitro::nitrocam::bridge::swift`)
   * from `NitroCam-Swift-Cxx-Bridge.hpp`.
   * This contains specialized C++ templates, and C++ helper functions that can be accessed from Swift.
   */
  public typealias bridge = margelo.nitro.nitrocam.bridge.swift

  /**
   * Holds an instance of the `HybridNitroCamSpec` Swift protocol.
   */
  private var __implementation: any HybridNitroCamSpec

  /**
   * Holds a weak pointer to the C++ class that wraps the Swift class.
   */
  private var __cxxPart: bridge.std__weak_ptr_margelo__nitro__nitrocam__HybridNitroCamSpec_

  /**
   * Create a new `HybridNitroCamSpec_cxx` that wraps the given `HybridNitroCamSpec`.
   * All properties and methods bridge to C++ types.
   */
  public init(_ implementation: any HybridNitroCamSpec) {
    self.__implementation = implementation
    self.__cxxPart = .init()
    /* no base class */
  }

  /**
   * Get the actual `HybridNitroCamSpec` instance this class wraps.
   */
  @inline(__always)
  public func getHybridNitroCamSpec() -> any HybridNitroCamSpec {
    return __implementation
  }

  /**
   * Casts this instance to a retained unsafe raw pointer.
   * This acquires one additional strong reference on the object!
   */
  public func toUnsafe() -> UnsafeMutableRawPointer {
    return Unmanaged.passRetained(self).toOpaque()
  }

  /**
   * Casts an unsafe pointer to a `HybridNitroCamSpec_cxx`.
   * The pointer has to be a retained opaque `Unmanaged<HybridNitroCamSpec_cxx>`.
   * This removes one strong reference from the object!
   */
  public class func fromUnsafe(_ pointer: UnsafeMutableRawPointer) -> HybridNitroCamSpec_cxx {
    return Unmanaged<HybridNitroCamSpec_cxx>.fromOpaque(pointer).takeRetainedValue()
  }

  /**
   * Gets (or creates) the C++ part of this Hybrid Object.
   * The C++ part is a `std::shared_ptr<margelo::nitro::nitrocam::HybridNitroCamSpec>`.
   */
  public func getCxxPart() -> bridge.std__shared_ptr_margelo__nitro__nitrocam__HybridNitroCamSpec_ {
    let cachedCxxPart = self.__cxxPart.lock()
    if cachedCxxPart.__convertToBool() {
      return cachedCxxPart
    } else {
      let newCxxPart = bridge.create_std__shared_ptr_margelo__nitro__nitrocam__HybridNitroCamSpec_(self.toUnsafe())
      __cxxPart = bridge.weakify_std__shared_ptr_margelo__nitro__nitrocam__HybridNitroCamSpec_(newCxxPart)
      return newCxxPart
    }
  }

  

  /**
   * Get the memory size of the Swift class (plus size of any other allocations)
   * so the JS VM can properly track it and garbage-collect the JS object if needed.
   */
  @inline(__always)
  public var memorySize: Int {
    return MemoryHelper.getSizeOf(self.__implementation) + self.__implementation.memorySize
  }

  // Properties
  public final var isFrontCamera: Bool {
    @inline(__always)
    get {
      return self.__implementation.isFrontCamera
    }
    @inline(__always)
    set {
      self.__implementation.isFrontCamera = newValue
    }
  }
  
  public final var flash: Int32 {
    @inline(__always)
    get {
      return self.__implementation.flash.rawValue
    }
    @inline(__always)
    set {
      self.__implementation.flash = margelo.nitro.nitrocam.FlashMode(rawValue: newValue)!
    }
  }
  
  public final var zoom: Double {
    @inline(__always)
    get {
      return self.__implementation.zoom
    }
    @inline(__always)
    set {
      self.__implementation.zoom = newValue
    }
  }

  // Methods
  @inline(__always)
  public final func switchCamera() -> bridge.Result_void_ {
    do {
      try self.__implementation.switchCamera()
      return bridge.create_Result_void_()
    } catch (let __error) {
      let __exceptionPtr = __error.toCpp()
      return bridge.create_Result_void_(__exceptionPtr)
    }
  }
  
  @inline(__always)
  public final func setFlashMode(mode: Int32) -> bridge.Result_void_ {
    do {
      try self.__implementation.setFlashMode(mode: margelo.nitro.nitrocam.FlashMode(rawValue: mode)!)
      return bridge.create_Result_void_()
    } catch (let __error) {
      let __exceptionPtr = __error.toCpp()
      return bridge.create_Result_void_(__exceptionPtr)
    }
  }
  
  @inline(__always)
  public final func setZoomLevel(level: Double) -> bridge.Result_void_ {
    do {
      try self.__implementation.setZoomLevel(level: level)
      return bridge.create_Result_void_()
    } catch (let __error) {
      let __exceptionPtr = __error.toCpp()
      return bridge.create_Result_void_(__exceptionPtr)
    }
  }
  
  @inline(__always)
  public final func takePhoto() -> bridge.Result_std__string_ {
    do {
      let __result = try self.__implementation.takePhoto()
      let __resultCpp = std.string(__result)
      return bridge.create_Result_std__string_(__resultCpp)
    } catch (let __error) {
      let __exceptionPtr = __error.toCpp()
      return bridge.create_Result_std__string_(__exceptionPtr)
    }
  }
  
  public final func getView() -> UnsafeMutableRawPointer {
    return Unmanaged.passRetained(__implementation.view).toOpaque()
  }
  
  public final func beforeUpdate() {
    __implementation.beforeUpdate()
  }
  
  public final func afterUpdate() {
    __implementation.afterUpdate()
  }
}
