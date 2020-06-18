package com.eye.cool.adapter.paging

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.annotation.NonNull
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.eye.cool.adapter.support.DataViewHolder
import com.eye.cool.adapter.support.GlobalConfig
import com.eye.cool.adapter.support.LayoutId
import com.eye.cool.adapter.support.LayoutName

/**
 *
 * If status is displayed, it is displayed in full screen, without data.
 *
 * Created by ycb on 2020/3/27 0027
 */
open class StatePagingAdapter<T>(
    @NonNull val diffCallback: DiffUtil.ItemCallback<T> = DefaultCallback()
) : PagedListAdapter<T, DataViewHolder<Any>>(diffCallback) {

  private val viewHolder = SparseArray<Class<out DataViewHolder<*>>>()

  private var inflater: LayoutInflater? = null
  private var clickObserver: View.OnClickListener? = null
  private var checkObserver: CompoundButton.OnCheckedChangeListener? = null
  private var longClickObserver: View.OnLongClickListener? = null

  @Volatile
  private var status: Any? = GlobalConfig.loading

  @Volatile
  private var dataCode: Int? = null

  protected var empty: Any = GlobalConfig.empty

  init {
    viewHolder.put(getHashCode(GlobalConfig.loading), GlobalConfig.loadingVh)
    viewHolder.put(getHashCode(GlobalConfig.empty), GlobalConfig.emptyVh)
  }

  /**
   * @param status Any status you have registered for
   */
  fun submitStatus(status: Any) {
    if (viewHolder.indexOfKey(getHashCode(status)) < 0) {
      throw IllegalStateException("Class(${status.javaClass.name}) is not registered!")
    }
    this.status = status
    notifyDataSetChanged()
  }

  override fun submitList(pagedList: PagedList<T>?) {
    submitList(pagedList, true)
  }

  fun submitList(pagedList: PagedList<T>?, empty: Any) {
    if (pagedList.isNullOrEmpty() && viewHolder.indexOfKey(getHashCode(empty)) > -1) {
      this.empty = empty
      submitStatus(empty)
    } else {
      status = null
      super.submitList(pagedList)
    }
  }

  fun submitList(pagedList: PagedList<T>?, showEmpty: Boolean) {
    if (pagedList.isNullOrEmpty() && showEmpty && isEmptyRegistered()) {
      submitStatus(empty)
    } else {
      status = null
      super.submitList(pagedList)
    }
  }

  override fun getItem(position: Int): T? {
    return if (status == null) super.getItem(position) else null
  }

  override fun getItemCount(): Int {
    return if (status == null) super.getItemCount() else 1
  }

  override fun getItemViewType(position: Int): Int {
    val statusHashCode: Int? = if (status == null) null else getHashCode(status!!)
    return statusHashCode ?: dataCode
    ?: throw java.lang.IllegalStateException("The data's ViewHolder must be set")
  }

  protected fun getHashCode(data: Any): Int {
    return if (data is Class<*>) {
      data.name.hashCode()
    } else data.javaClass.name.hashCode()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder<Any> {
    val clazz = viewHolder.get(viewType)
        ?: throw IllegalArgumentException("You should call registerViewHolder() first !")
    var layoutId = clazz.getAnnotation(LayoutId::class.java)?.value

    if (layoutId == null || layoutId == 0) {
      val layoutName = clazz.getAnnotation(LayoutName::class.java)?.value
      if (!layoutName.isNullOrEmpty()) {
        layoutId = parent.resources.getIdentifier(layoutName, "layout", parent.context.packageName)
      }
    }

    require(!(layoutId == null || layoutId == 0)) { clazz.simpleName + " must be has annotation of @LayoutId or @LayoutName" }

    if (inflater == null) {
      inflater = LayoutInflater.from(parent.context.applicationContext)
    }

    val itemView = inflater!!.inflate(layoutId, parent, false)
    val holder: DataViewHolder<Any>
    try {
      holder =
          clazz.getConstructor(View::class.java).newInstance(itemView) as DataViewHolder<Any>
    } catch (e: Exception) {
      throw RuntimeException(e)
    }

    return holder
  }

  override fun onBindViewHolder(holder: DataViewHolder<Any>, position: Int) {
    holder.clickListener = clickObserver
    holder.longClickListener = longClickObserver
    holder.checkedListener = checkObserver
    holder.dataSize = itemCount
    val data = if (status == null) getItem(position) else status
    holder.updateViewByData(data ?: return)
  }

  protected fun isEmptyRegistered(): Boolean {
    return viewHolder.indexOfKey(getHashCode(empty)) > -1
  }

  /**
   * Register a data view-holder with data class, only one data view-holder can be registered.
   *
   * @param cls Data's class
   * @param viewHolder Data's view-holder class
   */
  fun registerDataViewHolder(
      cls: Class<*>,
      viewHolder: Class<out DataViewHolder<*>>
  ) {
    registerViewHolder(cls, viewHolder, true)
  }

  /**
   * Register a state view-holder with state class.
   *
   * @param cls Any custom status
   * @param viewHolder Status's view-holder class
   */
  fun registerStateViewHolder(
      cls: Class<*>,
      viewHolder: Class<out DataViewHolder<*>>
  ) {
    registerViewHolder(cls, viewHolder, false)
  }

  /**
   * Only used to replace empty view, default Empty
   *
   * @param empty A instance or class. If it is class, it will call newInstance to generate the instance.
   * @param clazz The clazz to replace {@link DefaultEmptyViewHolder}.
   */
  fun replaceEmptyViewHolder(empty: Any, clazz: Class<out DataViewHolder<*>>) {
    val replaceEmpty = if (empty is Class<*>) empty.newInstance() else empty
    viewHolder.remove(getHashCode(this.empty))
    viewHolder.put(getHashCode(replaceEmpty), clazz)
    this.empty = replaceEmpty
  }

  /**
   * Only used to replace default status view, default Loading
   *
   * @param status A instance or class. If it is class, it will call newInstance to generate the instance.
   * @param clazz The clazz to replace {@link DefaultLoadingViewHolder}
   */
  fun replaceDefaultStatusViewHolder(status: Any, clazz: Class<out DataViewHolder<*>>) {
    if (this.status != null) {
      viewHolder.remove(getHashCode(this.status!!))
    }
    viewHolder.put(getHashCode(status), clazz)
    this.status = if (status is Class<*>) status.newInstance() else status
  }

  /**
   * Register ViewHolder by dataClass, data is exclusive.
   *
   * @param cls Data's class
   * @param viewHolder Data's view-holder class
   */
  fun registerViewHolder(
      cls: Class<*>,
      viewHolder: Class<out DataViewHolder<*>>,
      isDataHolder: Boolean
  ) {
    val hashCode = cls.name.hashCode()
    if (isDataHolder) {
      if (this.dataCode != null) {
        if (dataCode != hashCode) {
          throw IllegalStateException("The data's view-holder is already exists")
        }
      } else {
        this.dataCode = hashCode
      }
    }
    this.viewHolder.put(hashCode, viewHolder)
  }

  fun setOnClickListener(clickListener: View.OnClickListener) {
    this.clickObserver = clickListener
  }

  fun setOnCheckedChangeListener(checkedListener: CompoundButton.OnCheckedChangeListener) {
    this.checkObserver = checkedListener
  }

  fun setOnLongClickListener(longClickListener: View.OnLongClickListener) {
    this.longClickObserver = longClickListener
  }

  private class DefaultCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldConcert: T, newConcert: T): Boolean {
      val oldCode = oldConcert.hashCode()
      val newCode = newConcert.hashCode()
      return oldCode == newCode
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldConcert: T, newConcert: T): Boolean {
      return oldConcert == newConcert
    }
  }
}