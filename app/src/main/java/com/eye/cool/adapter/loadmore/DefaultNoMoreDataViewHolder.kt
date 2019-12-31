package com.eye.cool.adapter.loadmore

import android.view.View
import com.eye.cool.adapter.support.DataViewHolder
import com.eye.cool.adapter.support.LayoutName
import kotlinx.android.synthetic.main.adapter_no_data_viewholder.view.*

@LayoutName("adapter_no_data_viewholder")
class DefaultNoMoreDataViewHolder(view: View) : DataViewHolder<NoMoreData>(view) {

  override fun updateViewByData(data: NoMoreData) {
    super.updateViewByData(data)
    if (!data.data.isNullOrEmpty()) {
      itemView.noMoreDataTv.text = data.data
    }
  }
}