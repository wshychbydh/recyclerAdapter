package com.eye.cool.adapter.support

import android.view.View
import kotlinx.android.synthetic.main.adapter_loading_view_holder.view.*

@LayoutName("adapter_loading_view_holder")
class DefaultLoadingViewHolder(itemView: View) : DataViewHolder<Loading>(itemView) {
  override fun updateViewByData(data: Loading) {
    super.updateViewByData(data)

    if (data.contentView == null) {
      itemView.adapterLoadingPb.visibility = View.VISIBLE
      itemView.adapterLoadingTv.visibility = View.VISIBLE
      itemView.adapterLoadingContainer.visibility = View.GONE
      if (!data.text.isNullOrEmpty()) {
        itemView.adapterLoadingTv.text = data.text
      }
    } else {
      itemView.adapterLoadingPb.visibility = View.GONE
      itemView.adapterLoadingTv.visibility = View.GONE
      itemView.adapterLoadingContainer.visibility = View.VISIBLE
      itemView.adapterLoadingContainer.addView(data.contentView)
    }
  }
}
