package com.eye.cool.adapter.support

import com.eye.cool.adapter.loadmore.DefaultLoadMoreViewHolder
import com.eye.cool.adapter.loadmore.DefaultNoMoreDataViewHolder
import com.eye.cool.adapter.loadmore.LoadMore
import com.eye.cool.adapter.loadmore.NoMoreData

/**
 * Created by ycb on 2020/6/18 0018
 */
object GlobalConfig {

  internal var empty: Any = Empty()
  internal var emptyVh: Class<out DataViewHolder<*>> = DefaultEmptyViewHolder::class.java
  internal var loading: Any = Loading()
  internal var loadingVh: Class<out DataViewHolder<*>> = DefaultLoadingViewHolder::class.java

  internal var noMoreData: Any = NoMoreData()
  internal var noMoreDataVh: Class<out DataViewHolder<*>> = DefaultNoMoreDataViewHolder::class.java
  internal var loadMore: Any = LoadMore()
  internal var loadMoreVh: Class<out DataViewHolder<*>> = DefaultLoadMoreViewHolder::class.java

  internal var defaultCount = 10

  internal var showLoadMore = true
  internal var showNoMoreData = true
  internal var showNoMoreStatusAlways = false

  @JvmStatic
  @Throws(InstantiationException::class, IllegalAccessException::class)
  fun setDefaultEmpty(empty: Any, clazz: Class<out DataViewHolder<*>>): GlobalConfig {
    this.empty = if (empty is Class<*>) {
      empty.newInstance()
    } else empty
    this.emptyVh = clazz
    return this
  }

  @JvmStatic
  @Throws(InstantiationException::class, IllegalAccessException::class)
  fun setDefaultLoading(loading: Any, clazz: Class<out DataViewHolder<*>>): GlobalConfig {
    this.loading = if (loading is Class<*>) {
      loading.newInstance()
    } else loading
    this.loadingVh = clazz
    return this
  }

  @JvmStatic
  @Throws(InstantiationException::class, IllegalAccessException::class)
  fun setDefaultLoadMore(loadMore: Any, clazz: Class<out DataViewHolder<*>>): GlobalConfig {
    this.loadMore = if (loadMore is Class<*>) {
      loadMore.newInstance()
    } else loadMore
    this.loadMoreVh = clazz
    return this
  }

  @JvmStatic
  @Throws(InstantiationException::class, IllegalAccessException::class)
  fun setDefaultNoMoreData(noMoreData: Any, clazz: Class<out DataViewHolder<*>>): GlobalConfig {
    this.noMoreData = if (noMoreData is Class<*>) {
      noMoreData.newInstance()
    } else noMoreData
    this.noMoreDataVh = clazz
    return this
  }

  @JvmStatic
  fun setDefaultCount(count: Int): GlobalConfig {
    defaultCount = count
    return this
  }

  @JvmStatic
  fun showLoadMore(showLoadMore: Boolean): GlobalConfig {
    this.showLoadMore = showLoadMore
    return this
  }

  @JvmStatic
  fun showNoMoreData(showNoMoreData: Boolean): GlobalConfig {
    this.showNoMoreData = showNoMoreData
    return this
  }

  @JvmStatic
  fun showNoMoreStatusAlways(showNoMoreStatusAlways: Boolean): GlobalConfig {
    this.showNoMoreStatusAlways = showNoMoreStatusAlways
    return this
  }
}