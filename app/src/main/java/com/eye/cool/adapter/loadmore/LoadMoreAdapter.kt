package com.eye.cool.adapter.loadmore

import androidx.recyclerview.widget.RecyclerView
import com.eye.cool.adapter.support.RecyclerAdapter

/**
 * Only for LinearLayout
 * Created by cool on 18/4/18.
 */
class LoadMoreAdapter : RecyclerAdapter() {

  companion object {
    const val STATUS_DEFAULT = 0
    const val STATUS_LOADING = 1
    const val STATUS_NO_DATA = 2
  }

  private var loadMoreListener: ILoadMoreListener? = null
  private var noMoreData: Any? = NoMoreData()
  private var loading: Any? = Loading()
  private var defaultCount = 10
  private var loadMoreAble = false
  private var status = STATUS_LOADING

  init {
    registerViewHolder(Loading::class.java, DefaultLoadingViewHolder::class.java)
    registerViewHolder(NoMoreData::class.java, DefaultNoMoreDataViewHolder::class.java)
  }

  override fun doNotifyDataSetChanged() {
    data.remove(loading)
    data.remove(noMoreData)
    if (data.size < defaultCount) status = STATUS_DEFAULT
    when (status) {
      STATUS_LOADING -> {
        if (loading != null) {
          data.add(loading!!)
        }
      }
      STATUS_NO_DATA -> {
        if (noMoreData != null) {
          data.add(noMoreData!!)
        }
      }
    }
    super.doNotifyDataSetChanged()
  }

  fun setLoading(data: Any?) {
    this.loading = data
  }

  fun setNoData(data: Any?) {
    this.noMoreData = data
  }

  /**
   * Maximum data per request
   */
  fun setDefaultCount(defaultCount: Int) {
    this.defaultCount = defaultCount
  }

  override fun updateData(data: List<Any>?) {
    status = STATUS_DEFAULT
    enableLoadMoreData(data?.size ?: 0 >= defaultCount)
    super.updateData(data)
  }

  override fun appendData(data: List<Any>?) {
    if (data != null && data.size < defaultCount) {
      enableLoadMoreData(false)
    } else {
      enableLoadMoreData(data?.size ?: 0 >= defaultCount)
    }
    super.appendData(data)
  }

  private fun enableLoadMoreData(enable: Boolean) {
    this.loadMoreAble = enable
    if (!loadMoreAble) {
      if (itemCount >= defaultCount) {
        status = STATUS_NO_DATA
      }
    }
  }

  fun enableLoadMore(enable: Boolean) {
    enableLoadMoreData(enable)
    doNotifyDataSetChanged()
  }

  fun setLoadMoreListener(listener: ILoadMoreListener) {
    this.loadMoreListener = listener
  }

  fun empowerLoadMoreAbility(recyclerView: RecyclerView) {
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (!loadMoreAble) return
        if (status == STATUS_LOADING || status == STATUS_NO_DATA) return
        if (itemCount < defaultCount) return
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is androidx.recyclerview.widget.LinearLayoutManager) {
          //   val lastItemPosition = layoutManager.findLastVisibleItemPosition()
          val lastItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
          if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPosition + 1 == itemCount) {
            status = STATUS_LOADING
            doNotifyDataSetChanged()
            loadMoreListener?.onLoadMore()
          }
        }
      }
    })
  }
}
