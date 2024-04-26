package com.fuulea.nativeui

import android.view.ViewGroup
import com.facebook.react.ReactApplication
import com.facebook.react.ReactRootView
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.config.ReactFeatureFlags
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.facebook.react.uimanager.util.ReactFindViewUtil

class NativeUIModule(reactContext: ReactApplicationContext) : NativeUISpec(reactContext) {
  private val popovers = mutableMapOf<String, Popover>()

  override fun createPopover(anchorViewId: String, moduleName: String, options: ReadableMap) {
    reactApplicationContext.currentActivity?.let { activity ->
      activity.runOnUiThread {
        val popover = Popover(activity)
        popover.setWidth(options.getInt("width"))
        popover.setHeight(options.getInt("height"))
        popover.setDismissListener { sendPopoverVisibleChangeEvent(anchorViewId, false) }
        reactApplicationContext.currentActivity?.let { activity ->
          // make a root view
          val a = ReactRootView(activity)
          a.setIsFabric(ReactFeatureFlags.enableFabricRenderer)
          val instanceManager = (activity.application as ReactApplication?)?.reactNativeHost?.reactInstanceManager
          a.startReactApplication(instanceManager, moduleName)
          popover.setContentView(a)
          popovers[anchorViewId] = popover
        }
      }
    }
  }

  override fun changePopoverVisible(anchorViewId: String, visible: Boolean, destory: Boolean?) {
    reactApplicationContext.currentActivity?.let {
      it.runOnUiThread {
        popovers[anchorViewId]?.let { popover ->
          if (popover.isShowing() == visible) return@runOnUiThread
          if (visible) {
            (ReactFindViewUtil.findView(it.window.decorView.rootView, anchorViewId) as ViewGroup?)?.let { anchor ->
              popover.show(anchor)
            }
          } else if (destory == true) {
            popovers.remove(anchorViewId)
            val content = popover.getContentView()
            if (content is ReactRootView) content.unmountReactApplication()
            popover.release()
          } else {
            popover.dismiss()
          }
          sendPopoverVisibleChangeEvent(anchorViewId, visible)
        }
      }
    }
  }

  private fun sendPopoverVisibleChangeEvent(anchorViewId: String, visible: Boolean) {
    val data = Arguments.createMap()
    data.putBoolean("visible", visible)
    data.putString("viewId", anchorViewId)
    reactApplicationContext.getJSModule(RCTDeviceEventEmitter::class.java).emit("popoverVisibleChange", data)
  }

  override fun invalidate() {
    reactApplicationContext.currentActivity?.runOnUiThread {
      popovers.values.forEach {
        val content = it.getContentView()
        if (content is ReactRootView) content.unmountReactApplication()
        it.release()
      }
      popovers.clear()
      System.gc()
    }
    super.invalidate()
  }
}


