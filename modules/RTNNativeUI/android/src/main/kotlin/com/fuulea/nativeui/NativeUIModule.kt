package com.fuulea.nativeui

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.facebook.react.ReactApplication
import com.facebook.react.ReactRootView
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.config.ReactFeatureFlags
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.facebook.react.uimanager.util.ReactFindViewUtil
import com.fuulea.ui.FuuleaDialog
import com.fuulea.ui.FuuleaPopover
import com.fuulea.ui.FuuleaToast
import com.fuulea.ui.R

class NativeUIModule(reactContext: ReactApplicationContext) : NativeUISpec(reactContext) {
  private val popovers = mutableMapOf<String, FuuleaPopover>()

  override fun createPopover(anchorViewId: String, moduleName: String, options: ReadableMap) {
    reactApplicationContext.currentActivity?.let { activity ->
      activity.runOnUiThread {
        val popover = FuuleaPopover(activity)
        popover.setWidth(options.getInt("width"))
        popover.setHeight(options.getInt("height"))
        popover.setDismissListener { sendPopoverVisibleChangeEvent(anchorViewId, false) }
        when (options.getString("location")) {
          "top" -> popover.setLocation(FuuleaPopover.LocationType.TOP)
          "bottom" -> popover.setLocation(FuuleaPopover.LocationType.BOTTOM)
          "left" -> popover.setLocation(FuuleaPopover.LocationType.LEFT)
          "right" -> popover.setLocation(FuuleaPopover.LocationType.RIGHT)
        }
        when (options.getString("position")) {
          "start" -> popover.setPosition(Gravity.START)
          "center" -> popover.setPosition(Gravity.CENTER)
          "end" -> popover.setPosition(Gravity.END)
        }
        if (options.hasKey("offsetX")) popover.setOffset(offsetX = options.getInt("offsetX"))
        if (options.hasKey("offsetY")) popover.setOffset(offsetY = options.getInt("offsetY"))
        if (options.hasKey("hideArrow")) popover.setArrowVisible(!options.getBoolean("hideArrow"))
        if (options.hasKey("backgroundColor")) popover.setBackground(Color.parseColor(options.getString("backgroundColor")))
        // 判断是否运行在RN环境下
        reactApplicationContext.currentActivity?.let { activity ->
          // 创建一个RN根容器
          val a = ReactRootView(activity)
          a.setIsFabric(ReactFeatureFlags.enableFabricRenderer)
          // 获取RN运行时实例
          val instanceManager = (activity.application as ReactApplication?)?.reactNativeHost?.reactInstanceManager
          // 视图的Props
          val props = Bundle()
          props.putString("rootNode", anchorViewId)
          // 绘制RN视图
          a.startReactApplication(instanceManager, moduleName, props)
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
            // 获取RN视图中的锚点元素，然后显示气泡弹窗
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
          // 通知JS层
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


