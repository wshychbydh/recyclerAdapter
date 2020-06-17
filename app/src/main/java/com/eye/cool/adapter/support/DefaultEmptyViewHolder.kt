package com.eye.cool.adapter.support

import android.view.View
import kotlinx.android.synthetic.main.adapter_empty_view_holder.view.*

@LayoutName("adapter_empty_view_holder")
class DefaultEmptyViewHolder(itemView: View) : DataViewHolder<Empty>(itemView) {
  override fun updateViewByData(data: Empty) {
    super.updateViewByData(data)
    if (data.drawable != null) {
      itemView.textView.setCompoundDrawablesWithIntrinsicBounds(null, data.drawable, null, null)
    }
    if (!data.text.isNullOrEmpty()) {
      itemView.textView.text = data.text
    }
  }
}
