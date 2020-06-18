# Android RecyclerView通用适配器


### 功能介绍：

1、支持多个ViewHolder展示

2、支持自动加载更多

3、支持自定义任意状态的视图

4、支持OnClickListener, OnCheckedLister, OnLongClickLister, GlobalDataObserver(从宿主获取数据)

5、支持Paging

6、支持全局参数配置

#### 使用方法：

1、在root目录的build.gradle目录中添加
```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

2、在项目的build.gradle中添加依赖
```
    dependencies {
        implementation 'com.github.wshychbydh:recyclerAdapter:1.2.0'
    }
```

**注**：如果编译的时候报重复的'META-INF/app_release.kotlin_module'时，在app的build.gradle文件的android下添加
```
    packagingOptions {
        exclude 'META-INF/app_release.kotlin_module'
    }
```
报其他类似的重复错误时，添加方式同上。

3、构建RecyclerView的ViewHolder
```
   @LayoutId(R.layout.layout_sample) 或 @LayoutName("layout_sample")
   class YourViewHolder(itemView: View) : DataViewHolder<YourData>(itemView) {
     override fun updateViewByData(data: YourData) {
       super.updateViewByData(data)
       //todo itemView中的数据显示
     }
   }
```

4、构建RecyclerAdapter示例
```
    val adapter = RecyclerAdapter()

    adapter.registerViewHolder(YourData::class.java, YourViewHolder::class.java)   //注册一个或多个ViewHolder
    adapter.replaceEmptyViewHolder(YourEmpty, YourEmptyViewHolder::class.java)     //替换默认空视图（可选）
    adapter.replaceLoadingViewHolder(YourLoading, YourEmptyViewHolder::class.java) //替换默认加载视图（可选）

    recyclerView.adapter = adapter

    //绑定view的事件，并在ViewHolder注册事件，如：registerClickListener(view)
    recyclerView.setOnClickListener()
    recyclerView.setOnCheckedChangeListener()
    recyclerView.setOnLongClickListener()
    recyclerView.setGlobalDataObserver()   //适用于从宿主获取数据等情况

    adapter.appendData(data)  //叠加数据 
    adapter.updateData(data)  //更新数据
    adapter.updateData(data, showEmpty)  //更新数据, 当数据为空时是否展示空视图
    adapter.notifyItemData(data)  //更新某一栏的数据 
    adapter.clearData()       //清空数据 
    adapter.removeData(data, false)  //删除数据。为时ture，当剩余数据为null时显示注册的空视图（Empty）
```

5、使用LoadMoreAdapter，仅支持LinearLayoutManager (或StatePageAdapter实现自动加载更多)
```
    LoadMoreAdapter.Builder(recyclerView)    //绑定recyclerview和adapter并赋予加载更多能力

        .setDefaultCount(10)    //默认一次加载的数据数量，当数量不足时认为没有更多数据（可选）          

        //注册ViewHolder
        .registerViewHolder(YourData::class.java, YourViewHolder::class.java)
        //注册其他任意状态的视图，如Empty，Loading等（可选）
        .registerSpecViewHolder(YourStatus::class.java, YourViewHolder::class.java) 
        
        //替换默认空视图（可选）
        .replaceEmptyViewHolder(YourEmpty, YourEmptyViewHolder::class.java)
        //替换默认加载视图（可选） 
        .replaceLoadingViewHolder(YourLoading, YourLoadingViewHolder::class.java) 
        //替换默认LoadMore（可选）
        .replaceLoadMoreViewHolder(YourLoadMore, DefaultLoadMoreViewHolder::class.java)  
        //替换默认LoadMore（可选）
        .replaceNoMoreDataViewHolder(YourNoMoreData, DefaultNoMoreDataViewHolder::class.java)  

        .showNoMoreStatusAlways(Boolean)    //数据量小于PageSize时，是否显示NoMoreData，默认false
        .showLoadMore(Boolean)              //显示加载更多view，默认true
        .showNoMoreData(Boolean)            //显示没有更多数据view，默认true
 
        .setLoadMoreListener {
              //1、加载更多数据loadData
              //2、加载数据成功后调用adapter.updateData(data)
              //3、加载更多且未获取到数据时，可不调用updateData更新状态，但需调用setStatus重置当前列表状态
        }

        //绑定事件
        .setOnClickListener()           //（可选）
        .setOnLongClickListener()       //（可选）
        .setOnCheckedChangeListener()   //（可选）

        //从宿主共享数据给ViewHolder
        .setGlobalDataObserver { key-> return value }     //（可选）

        .build()

     
    adapter.updateData(data)  //叠加数据
    adapter.appendData(data)  //更新数据
    adapter.appendData(data, showEmpty)  //更新数据，当数据为空时显示空视图
    adapter.appendData(data, showEmpty, enableLoadMore)  //更新数据，并开启或禁用LoadMore能力

```

6、Paging配合StatePageAdapter的使用：
```
    recyclerView.layoutManager = WrappedLinearLayoutManager(this) //layoutManager必须用WrappedLinearLayoutManager替代

    adapter.registerStateViewHolder(cls, viewHolder)   //注册状态的ViewHolder
    adapter.registerDataViewHolder(cls, viewHolder)    //注册数据的ViewHolder

    adapter.submitStatus(status)  //提交状态，如加载中，空显示，异常等注册过的状态
    adapter.submitList(data)      //提交数据
    adapter.submitList(data, showEmpty)     //提交数据，当数据为空时显示空视图
```

7、全局参数配置：（页面的配置优先级高于全局配置）
```
    GlobalConfig
       .setDefaultCount(10)             //默认一次加载的数据数量，默认10
       .showLoadMore(true)              //支持加载更多，默认true
       .showNoMoreData(true)            //无更多数据时，显示对应视图，默认true
       .showNoMoreStatusAlways(false)   //数据量小于PageSize时，是否显示NoMoreData，默认false
       .setDefaultEmpty(YourEmpty, YourEmptyViewHolder::class.java)                //替换Empty
       .setDefaultLoading(YourLoading, YourLoadingViewHolder::class.java)          //替换Loading
       .setDefaultLoadMore(YourLoadMore, YourLoadMoreViewHolder::class.java)       //替换LoadMore
       .setDefaultNoMoreData(YourNoMoreData, YourNoMoreDataViewHolder::class.java) //替换NoMoreData
```

#####   

**Demo地址：(https://github.com/wshychbydh/SampleDemo)**    
    
##

###### **欢迎fork，更希望你能贡献commit.** (*￣︶￣)    

###### 联系方式 wshychbydh@gmail.com

[![](https://jitpack.io/v/wshychbydh/recyclerAdapter.svg)](https://jitpack.io/#wshychbydh/recyclerAdapter)
