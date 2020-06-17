package com.eye.cool.adapter.support

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Created by ycb on 18/6/14.
 */
open class RecyclerAdapter : RecyclerView.Adapter<DataViewHolder<Any>>() {

  private val viewHolder = SparseArray<Class<out DataViewHolder<*>>>()
  private var clickListener: View.OnClickListener? = null
  private var checkedListener: CompoundButton.OnCheckedChangeListener? = null
  private var longClickListener: View.OnLongClickListener? = null
  private var globalDataObserver: ((key: Any?) -> Any)? = null
  private var inflater: LayoutInflater? = null

  protected val data = ArrayList<Any>()
  protected var empty: Any = Empty()
  protected var loading: Any = Loading()

  init {
    registerViewHolder(Loading::class.java, DefaultLoadingViewHolder::class.java)
    registerViewHolder(Empty::class.java, DefaultEmptyViewHolder::class.java)
  }

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

    if (inflater == null) {
      inflater = LayoutInflater.from(parent.context.applicationContext)
    }

    try {
      val itemView = inflater!!.inflate(layoutId, parent, false)
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
   * @param dataClazz Data Class
   * @param clazz     ViewHolder Class
   */
  fun registerViewHolder(dataClazz: Class<*>, clazz: Class<out DataViewHolder<*>>) {
    viewHolder.put(dataClazz.name.hashCode(), clazz)
  }

  /**
   * Remove registered view holder by class
   *
   * @param dataClazz The class's view holder will be removed
   */
  fun removeViewHolder(dataClazz: Class<*>) {
    viewHolder.remove(dataClazz.name.hashCode())
  }

  /**
   * Only used to replace empty view, default Empty
   *
   * @param empty empty object
   * @param clazz Empty view holder class
   */
  open fun replaceEmptyViewHolder(empty: Any = Empty(), clazz: Class<out DataViewHolder<*>>) {
    viewHolder.put(empty.javaClass.name.hashCode(), clazz)
    this.empty = empty
  }

  /**
   * Only used to replace loading view, default Loading
   *
   * @param loading loading object
   * @param clazz Loading view holder class
   */
  open fun replaceLoadingViewHolder(loading: Any = Loading(), clazz: Class<out DataViewHolder<*>>) {
    viewHolder.put(loading.javaClass.name.hashCode(), clazz)
    this.loading = loading
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

  protected fun isEmptyRegistered(): Boolean {
    return viewHolder.indexOfKey(empty.javaClass.name.hashCode()) > -1
  }

  open fun loading() {
    if (viewHolder.indexOfKey(loading.javaClass.name.hashCode()) > -1) {
      this.data.clear()
      this.data.add(loading)
      doNotifyDataSetChanged()
    }
  }

  open fun showEmpty() {
    if (isEmptyRegistered()) {
      this.data.clear()
      this.data.add(empty)
      doNotifyDataSetChanged()
    }
  }

  open fun clearData() {
    clearData(true)
  }

  open fun clearData(showEmpty: Boolean) {
    if (this.data.isNullOrEmpty()) return
    this.data.clear()
    if (showEmpty && isEmptyRegistered()) {
      this.data.add(empty)
    }
    doNotifyDataSetChanged()
  }

  open fun appendData(data: Collection<Any>?) {
    if (data.isNullOrEmpty()) return
    this.data.addAll(data)
    doNotifyDataSetChanged()
  }

  /**
   * Removing the data can cause the list to be empty and then display empty
   */
  open fun removeData(data: Any, showEmpty: Boolean = false) {
    if (this.data.isNullOrEmpty()) return
    if (data is Collection<*>) {
      removeData(data.filterNotNull(), showEmpty)
    } else {
      this.data.remove(data)
      if (this.data.isNullOrEmpty() && showEmpty && isEmptyRegistered()) {
        this.data.add(empty)
      }
      doNotifyDataSetChanged()
    }
  }

  /**
   * Removing the data can cause the list to be empty and then display empty
   */
  open fun removeData(data: Collection<Any>, showEmpty: Boolean = false) {
    if (this.data.isNullOrEmpty()) return
    this.data.removeAll(data)
    if (this.data.isNullOrEmpty() && showEmpty && isEmptyRegistered()) {
      this.data.add(empty)
    }
    doNotifyDataSetChanged()
  }

  open fun appendData(index: Int, data: Collection<Any>?) {
    if (data.isNullOrEmpty()) return
    this.data.addAll(index, data)
    doNotifyDataSetChanged()
  }

  open fun appendData(data: Any?) {
    if (data == null) return
    if (data is Collection<*>) {
      appendData(data.filterNotNull())
    } else {
      this.data.add(data)
      doNotifyDataSetChanged()
    }
  }

  open fun appendData(index: Int, data: Any?) {
    if (data == null) return
    if (data is Collection<*>) {
      appendData(index, data.filterNotNull())
    } else {
      this.data.add(index, data)
      doNotifyDataSetChanged()
    }
  }

  open fun updateData(data: Collection<Any>?) {
    updateData(data, true)
  }

  /**
   * @param data the data to be shown
   * @param showEmpty Whether to display an empty view if the data is empty
   */
  open fun updateData(data: Collection<Any>?, showEmpty: Boolean) {
    if (data.isNullOrEmpty()) {
      if (showEmpty && isEmptyRegistered()) {
        this.data.clear()
        this.data.add(empty)
        doNotifyDataSetChanged()
      }
    } else {
      this.data.clear()
      this.data.addAll(data)
      doNotifyDataSetChanged()
    }
  }

  /**
   * @param data the data to be shown
   */
  open fun updateData(data: Any?) {
    updateData(data, true)
  }

  /**
   * @param data the data to be shown
   * @param showEmpty Whether to display an empty view if the data is empty
   */
  open fun updateData(data: Any?, showEmpty: Boolean) {
    if (data is Collection<*>) {
      updateData(data.filterNotNull(), showEmpty)
    } else {
      if (data == null) {
        if (showEmpty && isEmptyRegistered()) {
          this.data.clear()
          this.data.add(empty)
          doNotifyDataSetChanged()
        }
      } else {
        this.data.clear()
        this.data.add(data)
        doNotifyDataSetChanged()
      }
    }
  }

  /**
   * @param data the data that has changed
   *
   * @see #notifyItemChanged(int)
   */
  open fun notifyItemData(data: Any) {
    if (data is Collection<*>) {
      data.filterNotNull().forEach {
        notifyItemData(it)
      }
    } else {
      val position = findDataPosition(data)
      if (position >= 0) {
        notifyItemChanged(position)
      }
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
