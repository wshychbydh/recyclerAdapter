package com.eye.cool.adapter.paging

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by ycb on 2020/3/30 0030
 * Use with the StatePageAdapter
 */
open class StateLinearLayoutManager : LinearLayoutManager {

  constructor(
      context: Context
  ) : super(context)

  constructor(
      context: Context,
      orientation: Int,
      reverseLayout: Boolean
  ) : super(context, orientation, reverseLayout)

  constructor(
      context: Context,
      attrs: AttributeSet,
      defStyleAttr: Int,
      defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

  override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
    try {
      super.onLayoutChildren(recycler, state)
    } catch (e: IndexOutOfBoundsException) {
      e.printStackTrace()
    }
  }
}