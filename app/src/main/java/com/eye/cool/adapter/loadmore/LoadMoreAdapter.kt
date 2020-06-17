package com.eye.cool.adapter.loadmore

import android.view.View
import android.widget.CompoundButton
import androidx.annotation.IntDef
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eye.cool.adapter.support.*

/**
 * Support for RecyclerView.LinearLayoutManager
 *
 * Created by ycb on 18/4/18.
 */
class LoadMoreAdapter : RecyclerAdapter {

  private constructor()

  companion object {
    const val STATUS_NONE = 0
    const val STATUS_LOAD_MORE = 1
    const val STATUS_NO_MORE_DATA = 2
    const val STATUS_SPEC = 3  //Any other status, such as loading, empty, etc.
  }

  @Retention(AnnotationRetention.RUNTIME)
  @IntDef(STATUS_NONE, STATUS_LOAD_MORE, STATUS_NO_MORE_DATA, STATUS_SPEC)
  annotation class Status

  private var loadMoreListener: ILoadMoreListener? = null
  private var noMoreData: Any = NoMoreData()
  private var loadMore: Any = LoadMore()
  private var defaultCount = 10
  private var enableLoadMore = false
  private var status = STATUS_SPEC
  private var showNoMoreStatusAlways = false
  private var showLoadMore = true
  private var showNoMoreData = true
  private var recyclerView: RecyclerView? = null

  private val specData = hashSetOf(
      Loading::class.java.name.hashCode(),
      Empty::class.java.name.hashCode(),
      Spec::class.java.name.hashCode()
  )

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
    if (!showNoMoreStatusAlways && data.size < defaultCount && status != STATUS_SPEC) {
      status = STATUS_NONE
    }
    when (status) {
      STATUS_LOAD_MORE -> {
        if (showLoadMore) {
          data.add(loadMore)
        }
      }
      STATUS_NO_MORE_DATA -> {
        if (showNoMoreData) {
          data.add(noMoreData)
        }
      }
    }
    super.doNotifyDataSetChanged()
  }

  private fun registerSpecViewHolder(specDataClazz: Class<*>, clazz: Class<out DataViewHolder<*>>) {
    super.registerViewHolder(specDataClazz, clazz)
    specData.add(specDataClazz.name.hashCode())
  }

  /**
   * Only used to replace empty view, default Empty
   *
   * @param empty empty object
   * @param clazz Empty view holder class
   */
  @Deprecated("Use builder instead.")
  override fun replaceEmptyViewHolder(empty: Any, clazz: Class<out DataViewHolder<*>>) {
    super.replaceEmptyViewHolder(empty, clazz)
    specData.remove(Empty::class.java.name.hashCode())
    specData.add(empty.javaClass.name.hashCode())
  }

  /**
   * Only used to replace loading view, default Loading
   *
   * @param loading loading object
   * @param clazz Loading view holder class
   */
  @Deprecated("Use builder instead.")
  override fun replaceLoadingViewHolder(loading: Any, clazz: Class<out DataViewHolder<*>>) {
    super.replaceLoadingViewHolder(loading, clazz)
    specData.remove(Loading::class.java.name.hashCode())
    specData.add(loading.javaClass.name.hashCode())
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

  override fun updateData(data: Collection<Any>?) {
    val enableLoadMore = when {
      data.isNullOrEmpty() -> false
      this.data.containsAll(data) -> false
      else -> data.size >= defaultCount
    }
    updateData(data, true, enableLoadMore)
  }

  override fun updateData(data: Collection<Any>?, showEmpty: Boolean) {
    val enableLoadMore = when {
      data.isNullOrEmpty() -> false
      this.data.containsAll(data) -> false
      else -> data.size >= defaultCount
    }
    updateData(data, showEmpty, enableLoadMore)
  }

  fun updateData(data: Collection<Any>?, showEmpty: Boolean, enableLoadMore: Boolean) {
    if (data.isNullOrEmpty()) {
      if (showEmpty && isEmptyRegistered()) {
        this.data.clear()
        this.data.add(empty)
        status = STATUS_SPEC
        this.enableLoadMore = false
        doNotifyDataSetChanged()
      } else {
        enableLoadMore(enableLoadMore)
      }
    } else {
      this.data.clear()
      this.data.addAll(data)
      updateStatus(enableLoadMore)
      doNotifyDataSetChanged()
    }
  }

  /**
   * If updated data instance of Empty or Loading or another status you want, It is better to use {@link updateSpecData}.
   *
   * @param data Any data you want to update
   */
  override fun updateData(data: Any?) {
    updateData(data, true)
  }

  override fun updateData(data: Any?, showEmpty: Boolean) {
    if (data is Collection<*>) {
      updateData(data.filterNotNull())
    } else {
      if (data != null && specData.contains(data.javaClass.name.hashCode())) {
        status = STATUS_SPEC
        enableLoadMore = false
      }
      super.updateData(data, showEmpty)
    }
  }

  /**
   * Do not call this method to update data {@link updateData}
   *
   * @param data Any state object that you have registered, such as loading, empty, etc.
   */
  fun updateSpecData(data: Any) {
    specData.add(data.javaClass.name.hashCode())
    status = STATUS_SPEC
    enableLoadMore = false
    super.updateData(data, false)
  }

  override fun appendData(data: Collection<Any>?) {
    val enableLoadMore = when {
      data.isNullOrEmpty() -> false
      data.size < defaultCount -> false
      else -> true
    }
    appendData(data, enableLoadMore)
  }

  /**
   * Overlay data on top of the original data
   *
   * @param data
   * @param enableLoadMore
   */
  fun appendData(data: Collection<Any>?, enableLoadMore: Boolean) {
    if (!data.isNullOrEmpty()) {
      this.data.addAll(data)
    }
    updateStatus(enableLoadMore)
    doNotifyDataSetChanged()
  }

  private fun updateStatus(enableLoadMore: Boolean) {
    this.enableLoadMore = enableLoadMore
    status = when {
      enableLoadMore -> {
        STATUS_LOAD_MORE
      }
      itemCount == 1 && (specData.contains(data[0].javaClass.name.hashCode())) -> {
        STATUS_SPEC
      }
      itemCount >= defaultCount || (itemCount > 0 && showNoMoreStatusAlways) -> {
        STATUS_NO_MORE_DATA
      }
      else -> {
        STATUS_NONE
      }
    }
  }

  /**
   * At some case, you need to turn on load more ability, use it
   *
   * @param enable enable load more ability
   */
  fun enableLoadMore(enable: Boolean) {
    if (enableLoadMore == enable) return
    updateStatus(enable)
    doNotifyDataSetChanged()
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
     * Only used to replace empty view, default Empty
     *
     * @param empty empty object
     * @param clazz Empty view holder class
     */
    fun replaceEmptyViewHolder(empty: Any = Empty(), clazz: Class<out DataViewHolder<*>>): Builder {
      adapter.replaceEmptyViewHolder(empty, clazz)
      return this
    }

    /**
     * Only used to replace loading view, default Loading
     *
     * @param loading loading object
     * @param clazz Loading view holder class
     */
    fun replaceLoadingViewHolder(loading: Any = Loading(), clazz: Class<out DataViewHolder<*>>): Builder {
      adapter.replaceLoadingViewHolder(loading, clazz)
      return this
    }

    /**
     * Register special data class. Normal data class use {@link registerViewHolder}
     *
     * A special data state that can only be displayed one at a time.
     *
     * @param specDataClazz Spec data class, such as Loading, etc
     * @param clazz     ViewHolder Class
     */
    fun registerSpecViewHolder(specDataClazz: Class<*>, clazz: Class<out DataViewHolder<*>>) {
      adapter.registerSpecViewHolder(specDataClazz, clazz)
    }

    /**
     * Replace default load more view holder, If you don't want to show, @see #showLoadMore
     *
     * @param data Any data model associated with ViewHolder, If set to null, LoadMore will not be displayed
     * @param clazz the clazz to replace {@link DefaultLoadMoreViewHolder}
     */
    fun replaceLoadMoreViewHolder(data: Any, clazz: Class<out DataViewHolder<*>>): Builder {
      adapter.removeViewHolder(adapter.loadMore!!.javaClass)
      adapter.removeViewHolder(LoadMore::class.java)
      adapter.loadMore = data
      adapter.registerViewHolder(data.javaClass, clazz)
      return this
    }

    /**
     * Replace default no more data view holder, If you don't want to show, @see #showNoMoreData
     *
     * @param data Any data model associated with ViewHolder
     * @param clazz the clazz to replace {@link DefaultNoMoreDataViewHolder}
     */
    fun replaceNoMoreDataViewHolder(data: Any, clazz: Class<out DataViewHolder<*>>): Builder {
      adapter.removeViewHolder(adapter.noMoreData.javaClass)
      adapter.removeViewHolder(NoMoreData::class.java)
      adapter.noMoreData = data
      adapter.registerViewHolder(data.javaClass, clazz)
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
     */
    fun showNoMoreData(showNoMoreData: Boolean): Builder {
      adapter.showNoMoreData = showNoMoreData
      return this
    }

    /**
     * If the data is less than the maximum, whether the no more data state is displayed
     *
     * @param showNoMoreStatusAlways  default false
     */
    fun showNoMoreStatusAlways(showNoMoreStatusAlways: Boolean = false): Builder {
      adapter.showNoMoreStatusAlways = showNoMoreStatusAlways
      return this
    }

    fun build(): LoadMoreAdapter {
      return adapter
    }
  }
}
