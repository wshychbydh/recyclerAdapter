package com.eye.cool.adapter.support

import android.view.View
import android.widget.CompoundButton
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by ycb on 18/4/18.
 * Subclasses have one and only one construct parameter (view)
 */
abstract class DataViewHolder<D>(itemView: View) : RecyclerView.ViewHolder(itemView) {

  var clickListener: View.OnClickListener? = null
  var checkedListener: CompoundButton.OnCheckedChangeListener? = null
  var longClickListener: View.OnLongClickListener? = null
  var globalDataObserver: ((key: Any?) -> Any?)? = null
  var dataSize = 0

  protected var data: D? = null
    private set

  protected fun isFirstPosition(): Boolean {
    return adapterPosition == 0
  }

  protected fun isLastPosition(): Boolean {
    return dataSize == 0 || adapterPosition == dataSize - 1
  }

  @CallSuper
  open fun updateViewByData(data: D) {
    this.data = data
  }

  /**
   * bind with data
   * @param view the view to setOnClickListener
   * @param clickAble if false removeOnClickListener
   */
  fun registerClickListener(view: View, clickAble: Boolean) {
    registerClickListener(view, if (clickAble) data else null)
  }

  /**
   * bind with data
   */
  fun registerClickListener(view: View) {
    registerClickListener(view, data)
  }

  /**
   * bind with special data
   *
   * @param view the view to setOnClickListener
   * @param bindData the data to be bind as view.tag , if is null, the view will remove click listener
   */
  fun <T> registerClickListener(view: View, bindData: T? = null) {
    if (clickListener != null) {
      if (bindData == null) {
        view.isClickable = false
        view.setOnClickListener(null)
      } else {
        view.tag = bindData
        view.setOnClickListener(clickListener)
      }
    }
  }

  fun registerCheckedListener(view: CompoundButton, checkable: Boolean) {
    registerCheckedListener(view, if (checkable) data else null)
  }

  fun registerCheckedListener(view: CompoundButton) {
    registerCheckedListener(view, data)
  }

  /**
   * bind with special data
   *
   * @param view the view to setOnCheckedChangeListener
   * @param bindData the data to be bind as view.tag , if is null, the view will remove checked listener
   */
  fun <T> registerCheckedListener(view: CompoundButton, bindData: T? = null) {
    if (checkedListener != null) {
      if (bindData == null) {
        view.setOnCheckedChangeListener(null)
      } else {
        view.tag = bindData
        view.setOnCheckedChangeListener(checkedListener)
      }
    }
  }

  fun registerLongClickListener(view: View, longClickAble: Boolean) {
    registerLongClickListener(view, if (longClickAble) data else null)
  }

  fun registerLongClickListener(view: View) {
    registerLongClickListener(view, data)
  }

  /**
   * bind with special data
   * @param view the view to setOnLongClickListener
   * @param bindData the data to be bind as view.tag , if is null, the view will remove long click listener
   */
  fun <T> registerLongClickListener(view: View, bindData: T? = null) {
    if (longClickListener != null) {
      if (bindData == null) {
        view.isLongClickable = false
        view.setOnLongClickListener(null)
      } else {
        view.tag = bindData
        view.setOnLongClickListener(longClickListener)
      }
    }
  }

  fun onViewLongClicked(view: View) {
    longClickListener?.onLongClick(view)
  }

  fun onViewClicked(view: View) {
    clickListener?.onClick(view)
  }
}
