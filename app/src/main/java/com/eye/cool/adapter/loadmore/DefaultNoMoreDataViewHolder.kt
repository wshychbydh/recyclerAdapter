package com.eye.cool.adapter.loadmore

import android.view.View
import com.eye.cool.adapter.support.DataViewHolder
import com.eye.cool.adapter.support.LayoutName
import kotlinx.android.synthetic.main.adapter_no_data_view_holder.view.*

@LayoutName("adapter_no_data_view_holder")
class DefaultNoMoreDataViewHolder(view: View) : DataViewHolder<NoMoreData>(view) {

  override fun updateViewByData(data: NoMoreData) {
    super.updateViewByData(data)
    if (!data.text.isNullOrEmpty()) {
      itemView.noMoreDataTv.text = data.text
    }

    if (data.drawable != null) {
      itemView.noMoreDataTv.setCompoundDrawables(data.drawable, null, null, null)
    }
  }
}