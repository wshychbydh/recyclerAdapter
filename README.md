# Android RecyclerView通用适配器


### 功能介绍：

1、支持多个ViewHolder自动适配

2、支持LinearLayoutManager加载更多

3、支持Paging

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
        implementation 'com.github.wshychbydh:recyclerAdapter:1.0.2'
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

4、构建RecyclerAdapter实例
```
    val adapter = RecyclerAdapter()    //注册一个或多个ViewHolder
    adapter.registerViewHolder(YourData::class.java, YourViewHolder::class.java)
    recyclerView.adapter = adapter
```

5、使用LoadMoreAdapter，仅支持LinearLayoutManager
```
    val adapter = LoadMoreAdapter()  //注册一个或多个ViewHolder
    adapter.registerViewHolder(YourData::class.java, YourViewHolder::class.java)
    adapter.registerViewHolder(Loading::class.java, DefaultLoadingViewHolder::class.java) //加载中 （可选）
    adapter.registerViewHolder(NoMoreData::class.java, DefaultNoMoreDataViewHolder::class.java) //无更多数据 （可选）
    recyclerView.adapter = adapter

    adapter.setDefaultCount(10)                       //默认一次加载的数据数量，当数量不足时认为没有更多数据
    adapter.setLoading(Loading("加载更多中..."))      //加载更多提示，默认@See DefaultLoadingViewHolder
    adapter.setNoData(NoMoreData("没有更多数据"))     //无更多数据提示，默认@See DefaultNoMoreDataViewHolder
    adapter.setLoadMoreListener {
      //1、加载更多数据loadData
      //2、加载数据成功后调用adapter.updateData(data)
    }
```

6、添加/刷新数据：

```
    adapter.updateData(data)   //刷新数据
    adapter.appendData(data)   //追加数据
    adapter.removeData(data, false)  //删除数据，当剩余数据为null时是否显示注册的空视图（需注册空视图，否则会报错）
```

7、Paging配合StatePageAdapter的使用：
```
    recyclerView.layoutManager = WrappedLinearLayoutManager(this) //layoutManager必须用WrappedLinearLayoutManager替代

    adapter.registerStateViewHolder(cls, viewHolder)   //注册状态的ViewHolder
    adapter.registerDataViewHolder(cls, viewHolder)    //注册数据的ViewHolder

    adapter.submitStatus(status)  //提交状态，如加载中，空显示，异常等
    adapter.submitList(data)      //提交数据
```

#####   

**Demo地址：(https://github.com/wshychbydh/SampleDemo)**    
    
##

###### **欢迎fork，更希望你能贡献commit.** (*￣︶￣)    

###### 联系方式 wshychbydh@gmail.com

[![](https://jitpack.io/v/wshychbydh/recyclerAdapter.svg)](https://jitpack.io/#wshychbydh/recyclerAdapter)
