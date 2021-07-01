package com.eye.cool.adapter.paging

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource

class DefaultDataSourceFactory<T>(
    private val initPage: Int = 1,
    private val loader: IDataLoader<T>
) : DataSource.Factory<Int, T>() {

  override fun create(): DataSource<Int, T> = PageDataSource(initPage, loader)

  class PageDataSource<T>(
      val initPage: Int,
      val loader: IDataLoader<T>
  ) : PageKeyedDataSource<Int, T>() {

    override inline fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, T>
    ) {
      val data = loader.loadData(initPage, params.requestedLoadSize)
      data?.let {
        callback.onResult(it, null, initPage + 1)
      }
    }

    override inline fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {

      val data =
          loader.loadData(params.key + 1, params.requestedLoadSize)
      data?.let {
        callback.onResult(it, params.key + 1)
      }
    }

    override inline fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {

      val data = loader.loadData(params.key - 1, params.requestedLoadSize)
      data?.let {
        callback.onResult(it, params.key - 1)
      }
    }
  }

  fun interface IDataLoader<T> {
    fun loadData(page: Int, pageSize: Int): List<T>?
  }
}