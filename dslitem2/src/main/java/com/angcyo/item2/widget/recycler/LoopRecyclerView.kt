package com.angcyo.item2.widget.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.angcyo.github.widget.recycler.LoopSnapHelper
import com.angcyo.item2.R
import com.angcyo.widget.recycler.DslRecyclerView
import com.leochuan.AutoPlaySnapHelper

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/18
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class LoopRecyclerView(context: Context, attributeSet: AttributeSet? = null) :
    DslRecyclerView(context, attributeSet) {

    /**是否自动开始循环滚动*/
    var autoStartLoop: Boolean = true

    /**循环滚动助手*/
    var loopSnapHelper: LoopSnapHelper =
        LoopSnapHelper(AutoPlaySnapHelper.TIME_INTERVAL, AutoPlaySnapHelper.RIGHT)

    /**一个一个滑动*/
    var snapByOne: Boolean = true

    val enableLoop: Boolean
        get() = (adapter?.itemCount ?: 0) > 1

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.LoopRecyclerView)
        val timeInterval = typedArray.getInt(
            R.styleable.LoopRecyclerView_r_loop_interval,
            3000
        )
        val direction =
            typedArray.getInt(
                R.styleable.LoopRecyclerView_r_loop_direction,
                AutoPlaySnapHelper.RIGHT
            )
        autoStartLoop =
            typedArray.getBoolean(R.styleable.LoopRecyclerView_r_auto_start, autoStartLoop)

        snapByOne = typedArray.getBoolean(R.styleable.LoopRecyclerView_r_snap_by_one, snapByOne)

        loopSnapHelper.loopDuration = typedArray.getInt(
            R.styleable.LoopRecyclerView_r_loop_duration,
            loopSnapHelper.loopDuration
        )

        typedArray.recycle()

        loopSnapHelper.setTimeInterval(timeInterval)
        loopSnapHelper.setDirection(direction)
        loopSnapHelper.snapScrollOne = snapByOne
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        _startInner()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pause()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            _startInner()
        } else {
            pause()
        }
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)

        if (!isInEditMode) {
            loopSnapHelper.detachFromRecyclerView()
            //如果在super的构造方法里面调用了setLayoutManager, 此时[loopSnapHelper]还未初始化, 就会报空指针
            loopSnapHelper.attachToRecyclerView(this) //会自动开启无线循环
            _startInner()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val result = super.dispatchTouchEvent(ev)
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> pause()
            MotionEvent.ACTION_UP -> _startInner()
        }
        return result
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(ev)
    }

    override fun scrollBy(x: Int, y: Int) {
        super.scrollBy(x, y)
    }

    override fun scrollTo(x: Int, y: Int) {
        super.scrollTo(x, y)
    }

    fun _startInner() {
        if (autoStartLoop) {
            start()
        }
    }

    open fun start() {
        if (enableLoop) {
            //大于一个时才滚动
            loopSnapHelper.start()
        } else {
            pause()
        }
    }

    open fun pause() {
        loopSnapHelper.pause()
    }
}
