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
import com.eye.cool.adapter.support.LayoutId

/**
 * Created by ycb on 2020/3/27 0027
 */
open class StatePageAdapter<T>(
    @NonNull val diffCallback: DiffUtil.ItemCallback<T> = DefaultCallback()
) : PagedListAdapter<T, DataViewHolder<Any>>(diffCallback) {

  private val viewHolders = SparseArray<Class<out DataViewHolder<*>>>()

  private var inflater: LayoutInflater? = null
  private var clickObserver: View.OnClickListener? = null
  private var checkObserver: CompoundButton.OnCheckedChangeListener? = null
  private var longClickObserver: View.OnLongClickListener? = null

  @Volatile
  private var status: Any? = null

  @Volatile
  private var dataCode: Int? = null

  /**
   * @param status Any status you have registered for
   */
  fun submitStatus(status: Any) {
    val hasCode = status.javaClass.simpleName.hashCode()
    if (viewHolders.indexOfKey(hasCode) < 0) {
      throw IllegalStateException("Class(${status.javaClass.simpleName}) is not registered!")
    }
    this.status = status
    currentList?.clear()
    notifyDataSetChanged()
  }

  override fun submitList(pagedList: PagedList<T>?) {
    status = null
    super.submitList(pagedList)
  }

  override fun getItem(position: Int): T? {
    return if (status == null) super.getItem(position) else null
  }

  override fun getItemCount(): Int {
    return if (status == null) super.getItemCount() else 1
  }

  override fun getItemViewType(position: Int): Int {
    return status?.javaClass?.simpleName?.hashCode() ?: dataCode
    ?: throw java.lang.IllegalStateException("The data's ViewHolder must be set")
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder<Any> {
    val clazz = viewHolders.get(viewType)
        ?: throw IllegalArgumentException("You should call registerViewHolder() first !")
    var layoutId = clazz.getAnnotation(LayoutId::class.java)?.value

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

  /**
   * Register a data viewholder with data class, only one data viewholder can be registered.
   *
   * @param cls data Class
   * @param viewHolder     ViewHolder Class
   */
  fun registerDataViewHolder(
      cls: Class<*>,
      viewHolder: Class<out DataViewHolder<*>>
  ) {
    registerViewHolder(cls, viewHolder, true)
  }

  /**
   * Register a state viewholder with state class.
   *
   * @param cls data Class
   * @param viewHolder     ViewHolder Class
   */
  fun registerStateViewHolder(
      cls: Class<*>,
      viewHolder: Class<out DataViewHolder<*>>
  ) {
    registerViewHolder(cls, viewHolder, false)
  }

  /**
   * Register ViewHolder by dataClass, data is exclusive.
   *
   * @param cls data Class
   * @param viewHolder     ViewHolder Class
   */
  fun registerViewHolder(
      cls: Class<*>,
      viewHolder: Class<out DataViewHolder<*>>,
      isDataHolder: Boolean
  ) {
    val hashCode = cls.simpleName.hashCode()
    if (isDataHolder) {
      if (this.dataCode != null) {
        throw IllegalStateException("A data's ViewHolder already exists")
      } else {
        this.dataCode = hashCode
      }
    }
    viewHolders.put(hashCode, viewHolder)
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