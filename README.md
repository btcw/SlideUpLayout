# [SlideUpLayout](https://github.com/btcw/SlideUpLayout)
### 简介
>一个可以实现底部上滑布局的布局，支持xml布局预览

![image.png](https://upload-images.jianshu.io/upload_images/9090596-fd1e6bef487f292a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 导入

```Gradle
implementation 'top.iwill.mylib:slideuplayout:1.0.1'
```

### 使用说明

#### 1.添加为根布局
第一个Child布局为正常布局(LinearLayout布局1)，第二个为上拉布局(LinearLayout布局2)。支持正常预览
布局2的第一个默认为上拉的控件，不能消费onTouch事件。如果需要全局拖动，设置app:childDraggable="true"。如果上拉布局(布局2)包含一个listview/recyclerview/scrollview，需要设置childDraggable为true并在顶部的时候不消费onTouch事件，就可以衔接上滑动了。

```xml
<?xml version="1.0" encoding="utf-8"?>
<top.iwill.slideuplayout.SlideUpLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:separatePercent="50"
        app:topMargin="100dp"
        app:showHeight="120dp"
        app:childDraggable="true"
        tools:context=".MainActivity">

    <!--LinearLayout布局1-->
    <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
    <!--LinearLayout布局2-->
    <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical"
            android:background="@color/colorPrimary">

        <ImageView
                android:id="@+id/header"
                android:layout_width="match_parent"
                android:src="@drawable/ic_launcher_background"
                android:layout_height="100dp"/>

        <ListView
                android:id="@+id/listview"
                android:alpha="0.5"
                android:layout_width="match_parent"
                android:layout_height="match_parent"/>

    </LinearLayout>
</top.iwill.slideuplayout.SlideUpLayout>
```

#### 2.属性
>app:showHeight  未展开状态下布局展示高度(dimension)

>app:showPercent   未展开状态下布局展示百分比(float)，和showHeight一样的，不过这个是百分比，适合在预览的时候查看全部布局

>app:topMargin 完全展开状态，上滑布局顶部距离父布局顶部的距离(dimension)

>app:separatePercent 判断是该自动上滑到展开状态还是自动下滑到未展开状态的百分比。100是完全展开位置，0是未展开的默认位置。float∈[0,100]。```注意如果不设置则不会自动上滑或者下滑。```

>app:childDraggable 是否子布局可以响应滑动(Boolean)。默认false


