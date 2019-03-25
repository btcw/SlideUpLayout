package top.iwill.mylib

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Scroller
import kotlin.math.min

/**
 * @description: 底部上拉布局
 * @author: btcw
 * @date: 2019/3/22
 */
class BottomSlideLayout : LinearLayout {

    private var mTopMargin = 0f

    private var mHeaderHeight = 0

    private val mScroller by lazy { Scroller(context) }

    //计算滑动距离(offY)的上一个y的坐标值
    private var lastY = 0

//    private var LoadOnce: Boolean = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.BottomSlideLayout, 0, 0)
        mTopMargin = ta.getDimension(R.styleable.BottomSlideLayout_TopMargin, 0f)
        ta.recycle()
    }

    init {
        orientation = LinearLayout.VERTICAL
    }


//    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//        super.onLayout(changed, l, t, r, b)
//        if (changed && !LoadOnce) {
//            val mlp = layoutParams as MarginLayoutParams
//            mlp.bottomMargin = -mTopMargin.toInt()
//            layoutParams = mlp
//            LoadOnce = true
//        }
//    }

    /*
    这里可以把布局隐藏
            val mlp = child.layoutParams as MarginLayoutParams
            mlp.bottomMargin = -child.measuredHeight
            child.layoutParams = mlp
     */

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var negativeHeight = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (i == 0) mHeaderHeight = child.measuredHeight
//            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            negativeHeight += child.measuredHeight
        }
        setMeasuredDimension(
            measuredWidth,
            min(negativeHeight, context.getDisplayMetrics().heightPixels)
        )  //有意思的方法，之后继续研究这个
    }

    /*
    onInterceptTouchEvent()是用于处理事件（重点onInterceptTouchEvent这个事件是从父控件开始往子控件传的，
    直到有拦截或者到没有这个事件的view，然后就往回从子到父控件，这次是onTouch的）（类似于预处理，当然也可以不处理）
    并改变事件的传递方向，也就是决定是否允许Touch事件继续向下（子控件）传递，
    一但返回True（代表事件在当前的viewGroup中会被处理），则向下传递之路被截断（所有子控件将没有机会参与Touch事件），
    同时把事件传递给当前的控件的onTouchEvent()处理；返回false，则把事件交给子控件的onInterceptTouchEvent()

    笔记：如果不拦截（也就是返回false），那么也会出发onTouchEvent()，因为事件会继续向下传递，直到最后一个view，
    然后改变反向，向上传递（如果事件没被消费），就会再次出发本层的onTouchEvent(),继续向上传递。
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return ev?.let {
            it.action == MotionEvent.ACTION_DOWN && (it.y < mHeaderHeight)
        } ?: false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.y < mHeaderHeight) {
                val y = event.y.toInt()
                val offY = y - lastY
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> offsetTopAndBottom(offY)
//
//                        scrollBy(0, -offY / 2)
//                    MotionEvent.ACTION_UP -> {
//                        if (scrollY + mHeaderHeight < 0) {
//                        } else {
//                            smoothToScroll(-scrollY)
//                        }
//                    }
                    else -> {
                    }
                }
                lastY = y
                return true
            }
        }
        return false
    }

    /** 弹性滑动
     * @param offsetY 滑动距离,下为正,上为复
     */
    private fun smoothToScroll(offsetY: Int) {
        mScroller.startScroll(0, scrollY, 0, offsetY, 500)
        invalidate()
    }

}