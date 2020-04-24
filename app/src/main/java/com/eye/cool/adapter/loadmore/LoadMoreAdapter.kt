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
    const val STATUS_NONE = 0
    const val STATUS_LOAD_MORE = 1
    const val STATUS_NO_DATA = 2
  }

  @Retention(AnnotationRetention.RUNTIME)
  @IntDef(STATUS_NONE, STATUS_LOAD_MORE, STATUS_NO_DATA)
  annotation class Status

  private var loadMoreListener: ILoadMoreListener? = null
  private var noMoreData: Any? = NoMoreData()
  private var loadMore: Any? = LoadMore()
  private var defaultCount = 10
  private var loadMoreAble = false
  private var status = STATUS_LOAD_MORE
  private var showStatusAlways = false

  init {
    registerViewHolder(LoadMore::class.java, DefaultLoadingViewHolder::class.java)
    registerViewHolder(NoMoreData::class.java, DefaultNoMoreDataViewHolder::class.java)
  }

  override fun doNotifyDataSetChanged() {
    data.remove(loadMore)
    data.remove(noMoreData)
    if (!showStatusAlways && data.size < defaultCount) {
      status = STATUS_NONE
    }
    when (status) {
      STATUS_LOAD_MORE -> {
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

  fun showStatusAlways(showStatusAlways: Boolean) {
    this.showStatusAlways = showStatusAlways
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

  fun setStatus(@Status status: Int = STATUS_NONE) {
    if (this.status == status) return
    this.status = status
    data.remove(loadMore)
    data.remove(noMoreData)
    loadMoreAble = status == STATUS_LOAD_MORE
    super.doNotifyDataSetChanged()
  }

  override fun updateData(data: List<Any>?) {
    if (data == null) {
      updateStatus(false)
    } else {
      if (this.data.containsAll(data)) {
        updateStatus(false)
      } else {
        updateStatus(data.size >= defaultCount)
      }
    }
    super.updateData(data)
  }

  override fun appendData(data: List<Any>?) {
    if (data != null && data.size < defaultCount) {
      updateStatus(false)
    } else {
      updateStatus(data?.size ?: 0 >= defaultCount)
    }
    super.appendData(data)
  }

  private fun updateStatus(loadMoreAble: Boolean) {
    this.loadMoreAble = loadMoreAble
    status = when {
      loadMoreAble -> {
        STATUS_LOAD_MORE
      }
      itemCount >= defaultCount -> {
        STATUS_NO_DATA
      }
      else -> {
        STATUS_NONE
      }
    }
  }

  fun enableLoadMore(enable: Boolean) {
    updateStatus(enable)
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
        if (status == STATUS_NO_DATA) return
        if (itemCount < defaultCount) return
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is androidx.recyclerview.widget.LinearLayoutManager) {
          //   val lastItemPosition = layoutManager.findLastVisibleItemPosition()
          val lastItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
          if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPosition + 1 == itemCount) {
            loadMoreAble = false
            loadMoreListener?.onLoadMore()
          }
        }
      }
    })
  }
}
