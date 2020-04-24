package com.eye.cool.adapter.support

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Created by ycb on 18/6/14.
 */
open class RecyclerAdapter : RecyclerView.Adapter<DataViewHolder<Any>>() {

  private val viewHolder = SparseArray<Class<out DataViewHolder<*>>>()
  protected val data = ArrayList<Any>()
  private var clickListener: View.OnClickListener? = null
  private var checkedListener: CompoundButton.OnCheckedChangeListener? = null
  private var longClickListener: View.OnLongClickListener? = null
  private var globalDataObserver: ((key: Any?) -> Any)? = null

  override fun getItemCount(): Int {
    return data.size
  }

  override fun getItemViewType(position: Int): Int {
    return data[position].javaClass.name.hashCode()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder<Any> {
    val clazz = viewHolder.get(viewType)
        ?: throw IllegalArgumentException("You should call registerViewHolder() first!")
    var layoutId = clazz.getAnnotation(LayoutId::class.java)?.value
    if (layoutId == null || layoutId == 0) {
      val layoutName = clazz.getAnnotation(LayoutName::class.java)?.value
      if (!layoutName.isNullOrEmpty()) {
        layoutId = parent.resources.getIdentifier(layoutName, "layout", parent.context.packageName)
      }
    }

    require(!(layoutId == null || layoutId == 0)) { clazz.simpleName + " must be has @LayoutId or @LayoutName annotation" }

    try {
      val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
      return clazz.getConstructor(View::class.java).newInstance(itemView) as DataViewHolder<Any>
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }

  override fun onBindViewHolder(holder: DataViewHolder<Any>, position: Int) {
    holder.clickListener = clickListener
    holder.longClickListener = longClickListener
    holder.checkedListener = checkedListener
    holder.dataSize = data.size
    holder.globalDataObserver = globalDataObserver
    holder.updateViewByData(data[position])
  }

  /**
   * Register ViewHolder by dataClass, data is exclusive.
   *
   * @param dataClazz data Class
   * @param clazz     ViewHolder Class
   */
  fun registerViewHolder(dataClazz: Class<*>, clazz: Class<out DataViewHolder<*>>) {
    viewHolder.put(dataClazz.name.hashCode(), clazz)
  }

  /**
   * Register a callback to be invoked when this view is clicked. If this view is not
   * clickable, it becomes clickable.
   *
   * @param clickListener The callback that will run
   *
   * @see #setClickable(boolean)
   */
  fun setOnClickListener(clickListener: View.OnClickListener) {
    this.clickListener = clickListener
  }

  fun setOnCheckedChangeListener(checkedListener: CompoundButton.OnCheckedChangeListener) {
    this.checkedListener = checkedListener
  }

  fun setOnLongClickListener(longClickListener: View.OnLongClickListener) {
    this.longClickListener = longClickListener
  }

  fun setGlobalDataObserver(globalDataObserver: ((key: Any?) -> Any)?) {
    this.globalDataObserver = globalDataObserver
  }

  open fun clearData() {
    this.data.clear()
    doNotifyDataSetChanged()
  }

  open fun appendData(data: List<Any>?) {
    if (!data.isNullOrEmpty()) {
      this.data.addAll(data)
      doNotifyDataSetChanged()
    }
  }

  /**
   * Removing the data can cause the list to be empty and then display empty
   */
  open fun removeData(data: Any, showEmpty: Boolean = false) {
    if (this.data.isNullOrEmpty()) return
    if (this.data.contains(data) && this.data.size == 1) {
      if (showEmpty) {
        updateData(Empty())
      }
    } else if (this.data.remove(data)) {
      doNotifyDataSetChanged()
    }
  }

  /**
   * Removing the data can cause the list to be empty and then display empty
   */
  open fun removeData(data: List<Any>, showEmpty: Boolean = false) {
    if (this.data.isNullOrEmpty()) return
    data.forEach {
      this.data.remove(it)
    }
    if (this.data.isNullOrEmpty()) {
      if (showEmpty) {
        if (viewHolder.indexOfKey(Empty::class.java.name.hashCode()) > -1) {
          updateData(Empty())
        }
      }
    } else {
      doNotifyDataSetChanged()
    }
  }

  open fun appendData(index: Int, data: List<Any>?) {
    if (!data.isNullOrEmpty()) {
      this.data.addAll(index, data)
      doNotifyDataSetChanged()
    }
  }

  open fun appendData(data: Any?) {
    if (data != null) {
      this.data.add(data)
      doNotifyDataSetChanged()
    }
  }

  open fun appendData(index: Int, data: Any?) {
    if (data != null) {
      this.data.add(index, data)
      doNotifyDataSetChanged()
    }
  }

  open fun updateData(data: List<Any>?) {
    this.data.clear()
    if (data != null && data.isNotEmpty()) {
      this.data.addAll(data)
    }
    doNotifyDataSetChanged()
  }

  open fun updateData(data: Any?) {
    this.data.clear()
    if (null != data) {
      this.data.add(data)
    }
    doNotifyDataSetChanged()
  }

  open fun notifyItemData(data: Any) {
    val position = findDataPosition(data)
    if (position >= 0) {
      notifyItemChanged(position)
    }
  }

  @CallSuper
  open fun doNotifyDataSetChanged() {
    notifyDataSetChanged()
  }

  open fun findDataPosition(data: Any): Int {
    return this.data.indexOf(data)
  }

  open fun isLastPosition(position: Int): Boolean {
    return position == itemCount - 1
  }

  open fun getLastData(): Any? {
    return if (data.isEmpty()) {
      null
    } else data[data.size - 1]
  }
}
