package com.eye.cool.adapter.loadmore

import androidx.annotation.IntDef
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

  @Retention(AnnotationRetention.RUNTIME)
  @IntDef(STATUS_DEFAULT, STATUS_LOADING, STATUS_NO_DATA)
  annotation class Status

  private var loadMoreListener: ILoadMoreListener? = null
  private var noMoreData: Any? = NoMoreData()
  private var loadMore: Any? = LoadMore()
  private var defaultCount = 10
  private var loadMoreAble = false
  private var status = STATUS_LOADING

  init {
    registerViewHolder(LoadMore::class.java, DefaultLoadingViewHolder::class.java)
    registerViewHolder(NoMoreData::class.java, DefaultNoMoreDataViewHolder::class.java)
  }

  override fun doNotifyDataSetChanged() {
    data.remove(loadMore)
    data.remove(noMoreData)
    if (data.size < defaultCount) status = STATUS_DEFAULT
    when (status) {
      STATUS_LOADING -> {
        if (loadMore != null) {
          data.add(loadMore!!)
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

  /**
   * 1.registerViewHolder(YourLoadMore::class.java, YourLoadingViewHolder::class.java)
   * 2.setLoading(YourLoadMore::class.java)
   */
  fun setLoadMore(data: Any?) {
    this.loadMore = data
  }

  /**
   * 1.registerViewHolder(YourNoMoreData::class.java, YourNoMoreDataViewHolder::class.java)
   * 2.setNoData(YourNoMoreData::class.java)
   */
  fun setNoData(data: Any?) {
    this.noMoreData = data
  }

  /**
   * Maximum data per request
   */
  fun setDefaultCount(defaultCount: Int) {
    this.defaultCount = defaultCount
  }

  fun setStatus(@Status status: Int = STATUS_DEFAULT) {
    data.remove(loadMore)
    data.remove(noMoreData)
    data.add(status)
    super.doNotifyDataSetChanged()
  }

  override fun updateData(data: List<Any>?) {
    status = STATUS_DEFAULT
    enableLoadMoreData(data?.size ?: 0 >= defaultCount)
    super.updateData(data)
  }

  override fun appendData(data: List<Any>?) {
    status = STATUS_DEFAULT
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
