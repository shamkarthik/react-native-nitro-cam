///
/// HybridNitroCamUtilSpec.swift
/// This file was generated by nitrogen. DO NOT MODIFY THIS FILE.
/// https://github.com/mrousavy/nitro
/// Copyright © 2025 Marc Rousavy @ Margelo
///

import Foundation
import NitroModules

/// See ``HybridNitroCamUtilSpec``
public protocol HybridNitroCamUtilSpec_protocol: HybridObject {
  // Properties
  

  // Methods
  func getCameraDevices() throws -> [CameraType]
}

/// See ``HybridNitroCamUtilSpec``
public class HybridNitroCamUtilSpec_base {
  private weak var cxxWrapper: HybridNitroCamUtilSpec_cxx? = nil
  public func getCxxWrapper() -> HybridNitroCamUtilSpec_cxx {
  #if DEBUG
    guard self is HybridNitroCamUtilSpec else {
      fatalError("`self` is not a `HybridNitroCamUtilSpec`! Did you accidentally inherit from `HybridNitroCamUtilSpec_base` instead of `HybridNitroCamUtilSpec`?")
    }
  #endif
    if let cxxWrapper = self.cxxWrapper {
      return cxxWrapper
    } else {
      let cxxWrapper = HybridNitroCamUtilSpec_cxx(self as! HybridNitroCamUtilSpec)
      self.cxxWrapper = cxxWrapper
      return cxxWrapper
    }
  }
}

/**
 * A Swift base-protocol representing the NitroCamUtil HybridObject.
 * Implement this protocol to create Swift-based instances of NitroCamUtil.
 * ```swift
 * class HybridNitroCamUtil : HybridNitroCamUtilSpec {
 *   // ...
 * }
 * ```
 */
public typealias HybridNitroCamUtilSpec = HybridNitroCamUtilSpec_protocol & HybridNitroCamUtilSpec_base
