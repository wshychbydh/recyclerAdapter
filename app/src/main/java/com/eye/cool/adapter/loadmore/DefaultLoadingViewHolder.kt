package com.eye.cool.adapter.loadmore

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import com.eye.cool.adapter.support.DataViewHolder
import com.eye.cool.adapter.support.LayoutName
import kotlinx.android.synthetic.main.adapter_loading_view_holder.view.*


@LayoutName("adapter_loading_view_holder")
class DefaultLoadingViewHolder(view: View) : DataViewHolder<LoadMore>(view) {

  override fun updateViewByData(data: LoadMore) {
    super.updateViewByData(data)
    if (!data.text.isNullOrEmpty()) {
      itemView.adapterLoadingTv.text = data.text
    }

    if (data.drawable != null) {
      itemView.adapterLoadingIv.setImageDrawable(data.drawable)
    }

    val anim = ObjectAnimator.ofFloat(itemView.adapterLoadingTv, "rotation", 0.0f, 359.0f)
    anim.repeatCount = -1
    anim.duration = 1000
    anim.interpolator = LinearInterpolator()
    anim.start()
  }
}