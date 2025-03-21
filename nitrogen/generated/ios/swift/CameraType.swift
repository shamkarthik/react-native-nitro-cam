///
/// CameraType.swift
/// This file was generated by nitrogen. DO NOT MODIFY THIS FILE.
/// https://github.com/mrousavy/nitro
/// Copyright © 2025 Marc Rousavy @ Margelo
///

import NitroModules

/**
 * Represents an instance of `CameraType`, backed by a C++ struct.
 */
public typealias CameraType = margelo.nitro.nitrocam.CameraType

public extension CameraType {
  private typealias bridge = margelo.nitro.nitrocam.bridge.swift

  /**
   * Create a new instance of `CameraType`.
   */
  init(id: String, placement: String, type: [FocalType]) {
    self.init(std.string(id), std.string(placement), { () -> bridge.std__vector_FocalType_ in
      var __vector = bridge.create_std__vector_FocalType_(type.count)
      for __item in type {
        __vector.push_back(__item)
      }
      return __vector
    }())
  }

  var id: String {
    @inline(__always)
    get {
      return String(self.__id)
    }
    @inline(__always)
    set {
      self.__id = std.string(newValue)
    }
  }
  
  var placement: String {
    @inline(__always)
    get {
      return String(self.__placement)
    }
    @inline(__always)
    set {
      self.__placement = std.string(newValue)
    }
  }
  
  var type: [FocalType] {
    @inline(__always)
    get {
      return self.__type.map({ __item in __item })
    }
    @inline(__always)
    set {
      self.__type = { () -> bridge.std__vector_FocalType_ in
        var __vector = bridge.create_std__vector_FocalType_(newValue.count)
        for __item in newValue {
          __vector.push_back(__item)
        }
        return __vector
      }()
    }
  }
}
