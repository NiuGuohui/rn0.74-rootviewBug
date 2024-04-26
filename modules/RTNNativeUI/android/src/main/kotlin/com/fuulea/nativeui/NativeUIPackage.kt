package com.fuulea.nativeui

import com.facebook.react.TurboReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider

class NativeUIPackage : TurboReactPackage() {
  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? =
    if (name == NativeUISpec.NAME) NativeUIModule(reactContext) else null

  override fun getReactModuleInfoProvider() = ReactModuleInfoProvider {
    mapOf(
      NativeUISpec.NAME to ReactModuleInfo(
        NativeUISpec.NAME,
        NativeUISpec.NAME,
        false,
        false,
        true,
        false,
        true
      )
    )
  }
}
