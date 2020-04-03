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
        implementation 'com.github.wshychbydh:recyclerAdapter:1.0.1'
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
    val adapter = RecyclerAdapter()
    adapter.registerViewHolder(YourData::class.java, YourViewHolder::class.java) //注册一个或多个ViewHolder
    recyclerView.adapter = adapter
```

5、使用LoadMoreAdapter，仅支持LinearLayoutManager
```
    val adapter = LoadMoreAdapter()
    recyclerView.adapter = adapter
    adapter.registerViewHolder(YourData::class.java, YourViewHolder::class.java) //注册一个或多个ViewHolder
    
    adapter.setDefaultCount(10)                       //默认一次加载的数据数量，当数量不足这个值时任务
    adapter.setLoading(Loading("加载更多中..."))      //加载更多提示，默认@See DefaultLoadingViewHolder
    adapter.setNoData(NoMoreData("没有更多数据"))     //无更多数据提示，默认@See DefaultNoMoreDataViewHolder
    adapter.setLoadMoreListener {
      //1、加载更多数据
      //2、adapter.updateData(data)
    }
```

6、添加/刷新数据：

```
    adapter.updateData(data)   //刷新数据
    adapter.appendData(data)   //追加数据
```


#####   

**Demo地址：(https://github.com/wshychbydh/SampleDemo)**    
    
##

###### **欢迎fork，更希望你能贡献commit.** (*￣︶￣)    

###### 联系方式 wshychbydh@gmail.com

[![](https://jitpack.io/v/wshychbydh/recyclerAdapter.svg)](https://jitpack.io/#wshychbydh/recyclerAdapter)
