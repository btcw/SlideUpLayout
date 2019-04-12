package top.iwill.slideuplayout

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * @description:底部上拉布局
 * @author: btcw
 * @date: 2019/3/23
 */
class SlideUpLayout : FrameLayout {

    private var mSecondChild: ViewGroup? = null
    private var mDraggableView: View? = null
    private var mDraggableHeight = 0
    private var mMoveProgress = 1f
    private var mMaxMoveMargin = 0f
    private var mMoveMargin = 0f
    private var mDraggingTag = false
    private var mMaxTopMargin = 0
    private var mMoveProgressListener: MoveProgressListener? = null
    private val mViewDragHelper by lazy { ViewDragHelper.create(this, 1f, ViewDragCallback()) }
    /* declare-styleable */
    //默认显示的高度  Default and shrink height
    private var mShowHeight = 0f
    //全展开状态下，离顶部的距离   Expanded top margin
    private var mTopMargin = 0f
    //默认显示百分比，这个优先级高于showHeight。 shrink height with percent, This priority is higher than 'showHeight'.
    private var mShowPercent = 0f
    //处理向上还是向下滑动的分割位置，在这个百分比下，自动向下滑动，反之..。 This percentage determines the direction of automatic sliding.
    private var mSeparatePercent = -1f
    //上滑布局的子项是否响应拖动   whether the second layout's other children can be dragged
    private var mChildDraggable = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.SlideUpLayout, 0, 0)
        mShowHeight = ta.getDimension(R.styleable.SlideUpLayout_showHeight, 0f)
        mShowPercent = ta.getFloat(R.styleable.SlideUpLayout_showPercent, 0f) / 100f
        mTopMargin = ta.getDimension(R.styleable.SlideUpLayout_topMargin, 0f)
        mSeparatePercent = ta.getFloat(R.styleable.SlideUpLayout_separatePercent, -100f) / 100f
        mChildDraggable = ta.getBoolean(R.styleable.SlideUpLayout_childDraggable, false)
        ta.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount == 2)
            mSecondChild = getChildAt(1) as ViewGroup
        mDraggableView = mSecondChild?.getChildAt(0)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDraggableView?.let { mDraggableHeight = it.measuredHeight }
        if (mShowPercent != 0f) mShowHeight = (measuredHeight - mTopMargin) * mShowPercent   //优先使用百分比
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (i == 0) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                setMeasuredDimension(child.measuredWidth, child.measuredHeight)
            } else {
                measureChild(
                    child,
                    widthMeasureSpec,
                    ViewGroup.getChildMeasureSpec(
                        heightMeasureSpec,
                        child.paddingTop + child.paddingBottom,
                        measuredHeight - mTopMargin.toInt()
                    )
                )
            }

        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (i == 1) {
                mMaxTopMargin = (measuredHeight - mShowHeight).toInt()
                mMaxMoveMargin = mMaxTopMargin - mTopMargin
                child.layout(left, top + mMaxTopMargin, right, child.measuredHeight + mMaxTopMargin)
            } else {
                child.layout(left, top, right, bottom)
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return ev?.let { mViewDragHelper.shouldInterceptTouchEvent(it) } ?: false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //将触摸事件传递给ViewDragHelper
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                val slideLayoutTop = mSecondChild?.top ?: 0
                mDraggingTag = mChildDraggable || (slideLayoutTop < it.y && (slideLayoutTop + mDraggableHeight) > it.y)
            }
            if (mDraggingTag) mViewDragHelper.processTouchEvent(event)
        }
        return mDraggingTag
    }

    override fun computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    inner class ViewDragCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(p0: View, p1: Int): Boolean {
            return p0 != getChildAt(0)
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
            mMoveProgressListener?.onRelease(releasedChild)
            if (mSeparatePercent == -1f) return
            if (mMoveProgress >= mSeparatePercent) {

                slideToTop(releasedChild)
            } else {

                slideToBottom(releasedChild)
            }
            ViewCompat.postInvalidateOnAnimation(this@SlideUpLayout)
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            mMoveMargin -= dy
            mMoveProgress = mMoveMargin / mMaxMoveMargin
            mMoveProgressListener?.onMove(mMoveProgress)
            return top
        }
    }

    /**
     * 上滑  Slide to top
     */
    private fun slideToTop(releasedChild: View) {
        mSecondChild?.let { mViewDragHelper.smoothSlideViewTo(it, 0, mTopMargin.toInt()) }
        mMoveMargin = mMaxMoveMargin
        mMoveProgressListener?.onSlideToTop(releasedChild)
    }

    /**
     * 下滑到底部 Slide to bottom
     */
    private fun slideToBottom(releasedChild: View) {
        mSecondChild?.let {
            mViewDragHelper.smoothSlideViewTo(it, 0, mMaxTopMargin)
        }
        mMoveMargin = 0f
        mMoveProgressListener?.onSlideToBottom(releasedChild)
    }

    fun setMoveProgressListener(listener: MoveProgressListener) {
        this.mMoveProgressListener = listener
    }


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


    fun registerProgressListener(listener: MoveProgressBuilder.() -> Unit) {
        val builder = MoveProgressBuilder().also(listener)
        mMoveProgressListener = object : MoveProgressListener {
            override fun onMove(progress: Float) {
                builder.onMove?.invoke(progress)
            }

            override fun onRelease(child: View) {
                builder.onRelease?.invoke(child)
            }

            override fun onSlideToTop(child: View) {
                builder.onSlideToTop?.invoke(child)
            }

            override fun onSlideToBottom(child: View) {
                builder.onSlideToBottom?.invoke(child)
            }
        }
    }

    class MoveProgressBuilder {
        internal var onMove: ((progress: Float) -> Unit)? = null
        internal var onRelease: ((child: View) -> Unit)? = null
        internal var onSlideToTop: ((child: View) -> Unit)? = null
        internal var onSlideToBottom: ((child: View) -> Unit)? = null

        fun onMove(function: ((progress: Float) -> Unit)? = null) {
            this.onMove = function
        }

        fun onRelease(function: ((child: View) -> Unit)? = null) {
            onRelease = function
        }

        fun onSlideToTop(function: ((child: View) -> Unit)? = null) {
            onSlideToTop = function
        }

        fun onSlideToBottom(function: ((child: View) -> Unit)? = null) {
            onSlideToBottom = function
        }

    }
}