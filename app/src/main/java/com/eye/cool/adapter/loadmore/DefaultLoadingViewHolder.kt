package com.eye.cool.adapter.loadmore

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import com.eye.cool.adapter.support.DataViewHolder
import com.eye.cool.adapter.support.LayoutName
import kotlinx.android.synthetic.main.adapter_loading_viewholder.view.*


@LayoutName("adapter_loading_viewholder")
class DefaultLoadingViewHolder(view: View) : DataViewHolder<Loading>(view) {

  override fun updateViewByData(data: Loading) {
    super.updateViewByData(data)
    if (!data.data.isNullOrEmpty()) {
      itemView.tv_loading.text = data.data
    }
    val anim = ObjectAnimator.ofFloat(itemView.iv_loading, "rotation", 0.0f, 359.0f)
    anim.repeatCount = -1
    anim.duration = 1000
    anim.interpolator = LinearInterpolator()
    anim.start()
  }
}