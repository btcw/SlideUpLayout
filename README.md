# [SlideUpLayout](https://github.com/btcw/SlideUpLayout)
### 简介
>一个可以实现底部上滑布局的布局，支持xml布局预览

![TIM图片20190326140516.gif](https://upload-images.jianshu.io/upload_images/9090596-f3f1111d3ef98dbd.gif?imageMogr2/auto-orient/strip)

### 导入

```Gradle
implementation 'top.iwill.mylib:slideuplayout:1.0.2'
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


####3.接口
```
   interface MoveProgressListener {
        /**
         * 滑动进度监听，有可能大于1
         */
        fun onMove(progress: Float)

        /**
         * 释放时间，不再触摸
         */
        fun onRelease(child: View)

        /**
         * 滑动到顶部
         */
        fun onSlideToTop(child: View)

        /**
         * 滑动到底部
         */
        fun onSlideToBottom(child: View)
    }

```

>Java:
用setMoveProgressListener(listener:MoveProgressListener)进行监听注册

>Kotlin(回调方法可选):
```
       content.registerProgressListener {
            onMove {

            }
            onRelease{

            }
            onSlideToTop{

            }
            onSlideToBottom{

            }
        }
```
