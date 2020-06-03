package com.eye.cool.adapter.loadmore

import android.view.View
import android.widget.CompoundButton
import androidx.annotation.IntDef
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eye.cool.adapter.support.DataViewHolder
import com.eye.cool.adapter.support.RecyclerAdapter

/**
 * Only for LinearLayout
 * Created by ycb on 18/4/18.
 */
class LoadMoreAdapter : RecyclerAdapter {

  @Deprecated("Use build instead.")
  constructor()

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
  private var enableLoadMore = false
  private var status = STATUS_LOAD_MORE
  private var showStatusAlways = false

  init {
    registerViewHolder(LoadMore::class.java, DefaultLoadMoreViewHolder::class.java)
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

  /**
   * If the data is less than the maximum, whether the state is displayed，default false
   */
  @Deprecated("Use build instead.")
  fun showStatusAlways(showStatusAlways: Boolean) {
    this.showStatusAlways = showStatusAlways
  }

  /**
   * 1.registerViewHolder(YourLoadMore::class.java, YourLoadingViewHolder::class.java)
   * 2.setLoading(YourLoadMore::class.java)
   * 3.If set to null, LoadMore will not be displayed
   */
  @Deprecated("Use build instead.")
  fun setLoadMore(data: Any?) {
    this.loadMore = data
  }

  /**
   * 1.registerViewHolder(YourNoMoreData::class.java, YourNoMoreDataViewHolder::class.java)
   * 2.setNoData(YourNoMoreData::class.java)
   * 3.If set to null, NoMoreData will not be displayed
   */
  @Deprecated("Use build instead.")
  fun setNoData(data: Any?) {
    this.noMoreData = data
  }

  /**
   * Maximum data per request
   */
  @Deprecated("Use build instead.")
  fun setDefaultCount(defaultCount: Int) {
    this.defaultCount = defaultCount
  }

  fun setStatus(@Status status: Int) {
    if (this.status == status) return
    this.status = status
    data.remove(loadMore)
    data.remove(noMoreData)
    enableLoadMore = status == STATUS_LOAD_MORE
    super.doNotifyDataSetChanged()
  }

  override fun updateData(data: List<Any>?) {
    val enableLoadMore = when {
      data == null -> false
      this.data.containsAll(data) -> false
      else -> data.size >= defaultCount
    }
    updateData(data, enableLoadMore)
  }

  fun updateData(data: List<Any>?, enableLoadMore: Boolean) {
    updateStatus(enableLoadMore)
    super.updateData(data)
  }

  override fun appendData(data: List<Any>?) {
    val enableLoadMore = when {
      data == null -> false
      data.size < defaultCount -> false
      else -> true
    }
    appendData(data, enableLoadMore)
  }

  fun appendData(data: List<Any>?, enableLoadMore: Boolean) {
    updateStatus(enableLoadMore)
    super.appendData(data)
  }

  private fun updateStatus(enableLoadMore: Boolean) {
    this.enableLoadMore = enableLoadMore
    status = when {
      enableLoadMore -> {
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
    if (enableLoadMore == enable) return
    updateStatus(enable)
    doNotifyDataSetChanged()
  }

  @Deprecated("Use build instead.")
  fun setLoadMoreListener(listener: ILoadMoreListener) {
    this.loadMoreListener = listener
  }

  @Deprecated("Use build instead.")
  fun empowerLoadMoreAbility(recyclerView: RecyclerView) {
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (!enableLoadMore) return
        if (status != STATUS_LOAD_MORE) return
        if (itemCount < defaultCount) return
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is androidx.recyclerview.widget.LinearLayoutManager) {
          //   val lastItemPosition = layoutManager.findLastVisibleItemPosition()
          val lastItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
          if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPosition + 1 == itemCount) {
            enableLoadMore = false
            loadMoreListener?.onLoadMore()
          }
        }
      }
    })
  }

  class Builder(recyclerView: RecyclerView) {

    private val adapter = LoadMoreAdapter()

    init {
      recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
      adapter.empowerLoadMoreAbility(recyclerView)
      recyclerView.adapter = adapter
    }

    /**
     * If the data is less than the maximum, whether the state is displayed，default false
     */
    fun showStatusAlways(showStatusAlways: Boolean): Builder {
      adapter.showStatusAlways = showStatusAlways
      return this
    }

    /**
     * 1.registerViewHolder(YourLoadMore::class.java, YourLoadingViewHolder::class.java)
     * 2.setLoading(YourLoadMore::class.java)
     * 3.If set to null, LoadMore will not be displayed
     */
    fun setLoadMore(data: Any?): Builder {
      adapter.loadMore = data
      return this
    }

    /**
     * 1.registerViewHolder(YourNoMoreData::class.java, YourNoMoreDataViewHolder::class.java)
     * 2.setNoData(YourNoMoreData::class.java)
     * 3.If set to null, NoMoreData will not be displayed
     */
    fun setNoData(data: Any?): Builder {
      adapter.noMoreData = data
      return this
    }

    /**
     * Maximum data per request
     */
    fun setDefaultCount(defaultCount: Int): Builder {
      adapter.defaultCount = defaultCount
      return this
    }

    /**
     * Load more listener
     */
    fun setLoadMoreListener(listener: ILoadMoreListener): Builder {
      adapter.loadMoreListener = listener
      return this
    }

    /**
     * Register ViewHolder by dataClass, data is exclusive.
     *
     * @param dataClazz data Class
     * @param clazz     ViewHolder Class
     */
    fun registerViewHolder(dataClazz: Class<*>, clazz: Class<out DataViewHolder<*>>): Builder {
      adapter.registerViewHolder(dataClazz, clazz)
      return this
    }

    /**
     * Register a callback to be invoked when this view is clicked. If this view is not
     * clickable, it becomes clickable.
     *
     * @param clickListener The callback that will run
     *
     * @see #setClickable(boolean)
     */
    fun setOnClickListener(clickListener: View.OnClickListener): Builder {
      adapter.setOnClickListener(clickListener)
      return this
    }

    fun setOnCheckedChangeListener(checkedListener: CompoundButton.OnCheckedChangeListener): Builder {
      adapter.setOnCheckedChangeListener(checkedListener)
      return this
    }

    fun setOnLongClickListener(longClickListener: View.OnLongClickListener): Builder {
      adapter.setOnLongClickListener(longClickListener)
      return this
    }

    fun setGlobalDataObserver(globalDataObserver: ((key: Any?) -> Any)?): Builder {
      adapter.setGlobalDataObserver(globalDataObserver)
      return this
    }

    fun build(): LoadMoreAdapter {
      return adapter
    }
  }
}
