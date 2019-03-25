package top.iwill.mylib

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Scroller
import java.lang.Exception

/**
 * @description: 个人自定义的刷新布局
 * @author: btcw
 * @date: 2019/3/12
 */
class RefreshLayout : LinearLayout {

    private val mScroller by lazy { Scroller(context) }

    private var mRefreshListener: RefreshListener? = null

    private var REFRESHING = false

    //判断是否拦截事件的上一个y坐标的标记值
    private var tagY = 0

    //计算滑动距离(offY)的上一个y的坐标值
    private var lastY = 0

    private var LoadOnce: Boolean = false

    private var mHeaderHeight = 0

    private var mHeaderLp: MarginLayoutParams? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        this.orientation = LinearLayout.VERTICAL
        if (childCount == 1){
            val progressBar = ProgressBar(context)
            addView(progressBar, 0)
        }
    }

    //negative Margin 负外边距 隐藏顶部布局
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed && !LoadOnce) {
            mHeaderHeight = getChildAt(0).measuredHeight
            mHeaderLp = getChildAt(0).layoutParams as MarginLayoutParams
            mHeaderLp?.topMargin = -mHeaderHeight
            getChildAt(0).layoutParams = mHeaderLp
            LoadOnce = true
        }
    }

    /**
     *  重写onInterceptTouchEvent()方法，判断下滑时候拦截事件
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val interrupt: Boolean
        val y = ev?.rawY?.toInt() ?: 0
        interrupt = when (ev?.action) {
            MotionEvent.ACTION_MOVE -> (y - tagY) > 2
            else -> false
        }
        tagY = y
        return interrupt
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mScroller.isFinished && REFRESHING.not())
            event?.let {
                val y = event.y.toInt()
                val offY = y - lastY
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> if (offY > 0) scrollBy(0, -offY / 2)
                    MotionEvent.ACTION_UP -> {
                        if (scrollY + mHeaderHeight < 0) {
                            startRefreshing()
                        } else {
                            smoothToScroll(-scrollY)
                        }
                    }
                    else -> {
                    }
                }
                lastY = y
                return true
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

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.currY)
            postInvalidate()
        }
    }

    fun startRefreshing() {
        REFRESHING = true
        smoothToScroll(scrollY + mHeaderHeight)
        mRefreshListener?.onRefreshing()
    }

    fun stopRefreshing() {
        REFRESHING = false
        smoothToScroll(scrollY)
        mRefreshListener?.onStopRefreshing()
    }

    /**
     * 设置监听
     */
    fun setRefreshListener(listener: RefreshListener) {
        this.mRefreshListener = listener
    }

    /**
     * kotlin注册监听
     */
    fun registerRefreshListener(listener: RefreshListenerBuilder.() -> Unit) {
        val builder = RefreshListenerBuilder().also(listener)
        setRefreshListener(object : RefreshListener {
            override fun onRefreshing() {
                builder.onRefreshing?.invoke()
            }

            override fun onStopRefreshing() {
                builder.onStopRefreshing?.invoke()
            }
        })
    }

    class RefreshListenerBuilder {
        internal var onRefreshing: (() -> Unit)? = null
        internal var onStopRefreshing: (() -> Unit)? = null

        fun onRefreshing(function: (() -> Unit)?) {
            onRefreshing = function
        }

        fun onStopRefreshing(function: (() -> Unit)?) {
            onStopRefreshing = function
        }
    }


    interface RefreshListener {
        /**
         * 刷新回调
         */
        fun onRefreshing()

        /**
         * 停止刷新回调
         */
        fun onStopRefreshing()
    }
}