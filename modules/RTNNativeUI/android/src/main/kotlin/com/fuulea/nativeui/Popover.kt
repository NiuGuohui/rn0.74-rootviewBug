package com.fuulea.nativeui

import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import com.fuulea.nativeui.databinding.FuuleaPopoverLayoutBinding

class Popover(private val context: Context) {
  private val windowBinding = FuuleaPopoverLayoutBinding.inflate(LayoutInflater.from(context))
  private val popup: PopupWindow = PopupWindow(windowBinding.root, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
  private var dismissListener: (() -> Unit)? = null

  init {
    popup.isFocusable = true
    popup.isOutsideTouchable = true
    popup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    windowBinding.popoverContent.clipToOutline = true
    windowBinding.popoverContent.outlineProvider = object : ViewOutlineProvider() {
      override fun getOutline(view: View, outline: Outline) {
        outline.setRoundRect(0, 0, view.width, view.height, dpToPx(context, 4))
      }
    }
    popup.setOnDismissListener { dismissListener?.invoke() }
  }

  fun setWidth(width: Int) {
    popup.width = dpToPx(
      context,
      width
    ).toInt() + windowBinding.popoverContent.marginLeft + windowBinding.popoverContent.marginRight
  }

  fun setHeight(height: Int) {
    popup.height = dpToPx(
      context,
      height
    ).toInt() + windowBinding.popoverContent.marginTop + windowBinding.popoverContent.marginBottom
  }

  fun setContentView(content: View) {
    windowBinding.popoverContent.removeAllViews()
    windowBinding.popoverContent.addView(content)
  }

  fun getContentView(): View? = windowBinding.popoverContent.getChildAt(0)

  fun setDismissListener(listener: () -> Unit) {
    dismissListener = listener
  }

  fun show(target: View) {
    val lp = windowBinding.popoverArrow.layoutParams as FrameLayout.LayoutParams
    lp.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
    windowBinding.popoverArrow.layoutParams = lp
    popup.showAsDropDown(target, target.width, (popup.height + target.height) / 2)
  }

  fun isShowing() = popup.isShowing

  fun dismiss() = if (popup.isShowing) popup.dismiss() else Unit

  fun release() {
    dismiss()
    windowBinding.popoverContent.removeAllViews()
  }

  companion object {
    private fun dpToPx(context: Context, value: Int) =
      TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), context.resources.displayMetrics)
  }
}
