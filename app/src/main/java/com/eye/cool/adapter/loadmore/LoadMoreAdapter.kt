package com.eye.cool.adapter.loadmore

import android.view.View
import android.widget.CompoundButton
import androidx.annotation.IntDef
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eye.cool.adapter.support.DataViewHolder
import com.eye.cool.adapter.support.RecyclerAdapter

/**
 * Support for RecyclerView.LinearLayoutManager
 *
 * Created by ycb on 18/4/18.
 */
class LoadMoreAdapter : RecyclerAdapter {

  @Deprecated("Use build instead.")
  constructor()

  companion object {
    const val STATUS_NONE = 0
    const val STATUS_LOAD_MORE = 1
    const val STATUS_NO_MORE_DATA = 2
  }

  @Retention(AnnotationRetention.RUNTIME)
  @IntDef(STATUS_NONE, STATUS_LOAD_MORE, STATUS_NO_MORE_DATA)
  annotation class Status

  private var loadMoreListener: ILoadMoreListener? = null
  private var noMoreData: Any? = NoMoreData()
  private var loadMore: Any? = LoadMore()
  private var defaultCount = 10
  private var enableLoadMore = false
  private var status = STATUS_LOAD_MORE
  private var showNoMoreStatusAlways = false
  private var showLoadMore = true
  private var showNoMoreData = true
  private var recyclerView: RecyclerView? = null

  init {
    registerViewHolder(LoadMore::class.java, DefaultLoadMoreViewHolder::class.java)
    registerViewHolder(NoMoreData::class.java, DefaultNoMoreDataViewHolder::class.java)
    registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
      override fun onChanged() {
        if (enableLoadMore) {
          recyclerView?.postDelayed({
            if (isNeedAutoLoadMore()) { //All the data doesn't fill the screen
              enableLoadMore = false
              loadMoreListener?.onLoadMore()
            }
          }, 100L)
        }
      }
    })
  }

  override fun doNotifyDataSetChanged() {
    data.remove(loadMore)
    data.remove(noMoreData)
    if (!showNoMoreStatusAlways && data.size < defaultCount) {
      status = STATUS_NONE
    }
    when (status) {
      STATUS_LOAD_MORE -> {
        if (loadMore != null && showLoadMore) {
          data.add(loadMore!!)
        }
      }
      STATUS_NO_MORE_DATA -> {
        if (noMoreData != null && showNoMoreData) {
          data.add(noMoreData!!)
        }
      }
    }
    super.doNotifyDataSetChanged()
  }

  /**
   * If the data is less than the maximum, whether the state is displayed，default false
   */
  @Deprecated("Use builder's showNoMoreData() instead.")
  fun showStatusAlways(showStatusAlways: Boolean) {
    this.showNoMoreStatusAlways = showStatusAlways
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

  @Deprecated("Use A and B to automatically determine the status")
  fun updateStatus(@Status status: Int) {
    if (this.status == status) return
    this.status = status
    data.remove(loadMore)
    data.remove(noMoreData)
    enableLoadMore = status == STATUS_LOAD_MORE
    super.doNotifyDataSetChanged()
  }

  @Deprecated("Use updateStatus instead.", ReplaceWith("updateStatus(status)"))
  fun setStatus(@Status status: Int) {
    updateStatus(status)
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
        STATUS_NO_MORE_DATA
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
    this.recyclerView = recyclerView
    empowerLoadMoreAbility(true)
  }

  private fun empowerLoadMoreAbility(enable: Boolean) {
    val rv = this.recyclerView ?: return
    rv.removeOnScrollListener(scrollListener)
    if (enable) {
      rv.addOnScrollListener(scrollListener)
    }
  }

  private fun isNeedAutoLoadMore(): Boolean {
    if (itemCount < defaultCount) return false
    val rv = this.recyclerView ?: return false
    val lm = rv.layoutManager as? LinearLayoutManager ?: return false
    val firstPosition = lm.findFirstVisibleItemPosition()
    val lastPosition = lm.findLastCompletelyVisibleItemPosition()
    return firstPosition == 0 && lastPosition + 1 == itemCount
  }

  private val scrollListener = object : RecyclerView.OnScrollListener() {
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
      super.onScrollStateChanged(recyclerView, newState)
      if (!enableLoadMore) return
      if (status != STATUS_LOAD_MORE) return
      if (itemCount < defaultCount) return
      val layoutManager = recyclerView.layoutManager
      if (layoutManager is LinearLayoutManager) {
        //   val lastItemPosition = layoutManager.findLastVisibleItemPosition()
        val lastItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
        if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPosition + 1 == itemCount) {
          enableLoadMore = false
          loadMoreListener?.onLoadMore()
        }
      }
    }
  }


  class Builder(recyclerView: RecyclerView) {

    private val adapter = LoadMoreAdapter()

    init {
      recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
      recyclerView.adapter = adapter
      adapter.recyclerView = recyclerView
      adapter.empowerLoadMoreAbility(true)
    }

    /**
     * 1.registerViewHolder(YourLoadMore::class.java, YourLoadingViewHolder::class.java)
     * 2.setLoading(YourLoadMore::class.java)
     * 3.If set to null, LoadMore will not be displayed
     *
     * @param data Any data model associated with ViewHolder
     */
    fun setLoadMore(data: Any?): Builder {
      adapter.loadMore = data
      return this
    }

    /**
     * 1.registerViewHolder(YourNoMoreData::class.java, YourNoMoreDataViewHolder::class.java)
     * 2.setNoData(YourNoMoreData::class.java)
     * 3.If set to null, NoMoreData will not be displayed
     *
     * @param data Any data model associated with ViewHolder
     */
    fun setNoData(data: Any?): Builder {
      adapter.noMoreData = data
      return this
    }

    /**
     * Maximum data per request
     * @param defaultCount default 10
     */
    fun setDefaultCount(defaultCount: Int): Builder {
      adapter.defaultCount = defaultCount
      return this
    }

    /**
     * Load more listener
     *
     * @param listener
     */
    fun setLoadMoreListener(listener: ILoadMoreListener): Builder {
      adapter.loadMoreListener = listener
      return this
    }

    /**
     * Register ViewHolder by dataClass, data is exclusive.
     *
     * @param dataClazz Data Class
     * @param clazz     ViewHolder Class
     */
    fun registerViewHolder(dataClazz: Class<*>, clazz: Class<out DataViewHolder<*>>): Builder {
      adapter.registerViewHolder(dataClazz, clazz)
      return this
    }

    /**
     * {@link View.OnClickListener}
     *
     * @param clickListener
     */
    fun setOnClickListener(clickListener: View.OnClickListener): Builder {
      adapter.setOnClickListener(clickListener)
      return this
    }

    /**
     * {@link CompoundButton.setOnCheckedChangeListener}
     *
     * @param checkedListener
     */
    fun setOnCheckedChangeListener(checkedListener: CompoundButton.OnCheckedChangeListener): Builder {
      adapter.setOnCheckedChangeListener(checkedListener)
      return this
    }

    /**
     * {@link View.OnLongClickListener}
     *
     * @param longClickListener
     */
    fun setOnLongClickListener(longClickListener: View.OnLongClickListener): Builder {
      adapter.setOnLongClickListener(longClickListener)
      return this
    }

    /**
     * Share value to ViewHolder by key
     *
     * @param globalDataObserver
     */
    fun setGlobalDataObserver(globalDataObserver: ((key: Any?) -> Any)?): Builder {
      adapter.setGlobalDataObserver(globalDataObserver)
      return this
    }

    /**
     * Display load more view
     *
     * @param showLoadMore default true
     */
    fun showLoadMore(showLoadMore: Boolean): Builder {
      adapter.showLoadMore = showLoadMore
      return this
    }

    /**
     * Display the appropriate view when no more data is available
     *
     * @param showNoMoreData default true
     * @param showAlways If the data is less than the maximum,
     * whether the no more data state is displayed, default false
     */
    fun showNoMoreData(showNoMoreData: Boolean, showAlways: Boolean = false): Builder {
      adapter.showNoMoreData = showNoMoreData
      adapter.showNoMoreStatusAlways = showAlways
      return this
    }

    fun build(): LoadMoreAdapter {
      return adapter
    }
  }
}
