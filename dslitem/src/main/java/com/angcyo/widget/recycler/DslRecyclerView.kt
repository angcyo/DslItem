package com.angcyo.widget.recycler

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.item.R
import com.angcyo.item.isTouchDown
import com.angcyo.item.isTouchFinish
import com.angcyo.widget.ITouchDelegate
import com.angcyo.widget.ITouchHold
import com.angcyo.widget.TouchActionDelegate
import java.lang.ref.WeakReference
import java.util.*

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/23
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

typealias FocusTransitionChanged = (from: View?, to: View?) -> Unit

open class DslRecyclerView : RecyclerView, ITouchHold, ITouchDelegate {

    companion object {
        //用于在多个RV之间共享之前的焦点View
        var _lastFocusViewRef: WeakReference<View>? = null

        /**焦点过渡改变回调, 如果在此回调中处理了视图的属性, 要注意RecyclerView中的复用问题*/
        var _focusTransitionChangedList: MutableList<FocusTransitionChanged> = mutableListOf()

        fun addFocusTransitionChangeListener(listener: FocusTransitionChanged) {
            if (!_focusTransitionChangedList.contains(listener)) {
                _focusTransitionChangedList.add(listener)
            }
        }

        fun removeFocusTransitionChangeListener(listener: FocusTransitionChanged) {
            if (_focusTransitionChangedList.contains(listener)) {
                _focusTransitionChangedList.remove(listener)
            }
        }

        /**全局统一设置焦点视图*/
        fun setFocusView(parent: View, view: View?) {
            val old = _lastFocusViewRef?.get()
            if (old != view) {
                _lastFocusViewRef?.clear()
                _lastFocusViewRef = null
                if (view != null) {
                    _lastFocusViewRef = WeakReference(view)
                }
                _focusTransitionChangedList.forEach {
                    it.invoke(old, view)
                }
                ViewCompat.postInvalidateOnAnimation(parent)
            }
        }
    }

    val _touchDelegate = TouchActionDelegate()

    /** 通过[V] [H] [GV2] [GH3] [SV2] [SV3] 方式, 设置 [LayoutManager] */
    var layout: String? = null
        set(value) {
            field = value
            value?.run { resetLayoutManager(this) }
        }

    val scrollHelper = ScrollHelper()

    /**是否激活焦点过渡监听*/
    var enableFocusTransition = false
        set(value) {
            field = value
            if (value) {
                //激活绘图顺序
                isFocusable = true
                isFocusableInTouchMode = true
                isChildrenDrawingOrderEnabled = true
            }
        }

    constructor(context: Context) : super(context) {
        initAttribute(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttribute(context, attrs)
    }

    private fun initAttribute(context: Context, attributeSet: AttributeSet? = null) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DslRecyclerView)
        typedArray.getString(R.styleable.DslRecyclerView_r_layout_manager)?.let {
            layout = it
        }
        typedArray.getBoolean(
            R.styleable.DslRecyclerView_r_enable_focus_transition,
            enableFocusTransition
        )
        typedArray.recycle()

        scrollHelper.attach(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (layoutManager == null) {
            //layout属性的支持
            layout?.run { layout = this }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    /**
     * 锁定滚动到目标位置
     * [position] 目标位置, 负数表示倒数第几个
     * [duration] 锁定多少毫秒
     * [config] 自定义配置
     * */
    fun lockScroll(
        position: Int = NO_POSITION,
        duration: Long = -1,
        config: ScrollHelper.LockDrawListener .() -> Unit = {}
    ) {
        scrollHelper.lockPositionByDraw {
            lockPosition = position
            lockDuration = duration
            config()
        }
    }

    //<editor-fold desc="焦点相关处理, TV开发使用居多">

    override fun onChildDetachedFromWindow(child: View) {
        super.onChildDetachedFromWindow(child)
        if (child == _lastFocusViewRef?.get()) {
            //清理焦点
            setFocusView(this, null)
            clearChildFocus(child)
        }
    }

    /**这个方法可以改变child绘制的顺序.
     * [childCount] 表示当前界面需要绘制的child数量
     * [drawingPosition] 表示当前需要绘制的child位置
     * @return 返回值表示, [drawingPosition]位置真正应该绘制的[drawingPosition].
     * * */
    override fun getChildDrawingOrder(childCount: Int, drawingPosition: Int): Int {
        _lastFocusViewRef?.get()?.let { focusView ->
            val focusIndex = indexOfChild(focusView)
            if (focusIndex == -1) {
                return drawingPosition
            }
            return when {
                drawingPosition == childCount - 1 -> focusIndex
                drawingPosition < focusIndex -> drawingPosition
                else -> drawingPosition + 1
            }
        }
        return super.getChildDrawingOrder(childCount, drawingPosition)
    }

    /**在层级结构中, 查找当前具有焦点的[View]*/
    override fun findFocus(): View? {
        return super.findFocus()?.apply {
            if (enableFocusTransition) {
                setFocusView(this@DslRecyclerView, this)
            }
        }
    }

    /**通过当前具有焦点[focused]的[View], 按照[direction]方向查找焦点*/
    override fun focusSearch(focused: View, direction: Int): View? {
        return super.focusSearch(focused, direction)?.apply {
            if (enableFocusTransition) {
                ViewCompat.postInvalidateOnAnimation(this@DslRecyclerView)
            }
        }
    }

    override fun focusSearch(direction: Int): View? {
        return super.focusSearch(direction)?.apply {
            //L.v(this)
        }
    }

    override fun dispatchUnhandledMove(focused: View?, direction: Int): Boolean {
        //L.v("$focused $direction")
        return super.dispatchUnhandledMove(focused, direction)
    }

    override fun restoreDefaultFocus(): Boolean {
        //L.v("...")
        return super.restoreDefaultFocus()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        //L.v(event)
        return try {
            super.dispatchKeyEvent(event)
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent?): Boolean {
        //L.v(event)
        return super.dispatchKeyEventPreIme(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        //L.d("keyCode:$keyCode")
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        //L.d("keyCode:$keyCode")
        return super.onKeyLongPress(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        //L.d("keyCode:$keyCode")
        return super.onKeyUp(keyCode, event)
    }

    override fun requestChildFocus(child: View?, focused: View?) {
        super.requestChildFocus(child, focused)
        //L.v("$child $focused")
    }

    override fun requestChildRectangleOnScreen(
        child: View,
        rect: Rect,
        immediate: Boolean
    ): Boolean {
        //L.v()
        return super.requestChildRectangleOnScreen(child, rect, immediate)
    }

    override fun addFocusables(views: ArrayList<View>?, direction: Int, focusableMode: Int) {
        //L.v(views)
        super.addFocusables(views, direction, focusableMode)

//        val old = _lastFocusViewRef?.get()
//        if (this.hasFocus() || old == null) {
//            super.addFocusables(views, direction, focusableMode)
//        } else {
//            //将当前的view放到Focusable views列表中，再次移入焦点时会取到该view,实现焦点记忆功能
//            views?.add(old)
//        }
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        //L.d("gainFocus:$gainFocus $direction $previouslyFocusedRect")
    }

    override fun clearChildFocus(child: View?) {
        super.clearChildFocus(child)
        //L.d()
    }

    override fun clearFocus() {
        super.clearFocus()
        //L.d()
    }

    //</editor-fold desc="焦点相关处理, TV开发使用居多">

    //<editor-fold desc="Touch Over">

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.isTouchDown()) {
            this.isTouchHold = true
        } else if (ev.isTouchFinish()) {
            this.isTouchHold = false
        }

        getTouchActionDelegate().dispatchTouchEvent(ev)

        val result = super.dispatchTouchEvent(ev)
        if (enableFocusTransition && ev.isTouchFinish()) {
            //如果是通过TouchEvent改变的focus, 则需要手动触发一次[findFocus]
            findFocus()
        }
        return result
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        getTouchActionDelegate().onInterceptTouchEvent(ev)
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        getTouchActionDelegate().onTouchEvent(ev)
        return super.onTouchEvent(ev)
    }

    override fun getTouchActionDelegate(): TouchActionDelegate {
        return _touchDelegate
    }

    /**是否还在touch中*/
    override var isTouchHold: Boolean = false

    //</editor-fold desc="Touch Over">

    //<editor-fold desc="Edge Effect">

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?
    ): Boolean {
        return super.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow
        )
    }

    //</editor-fold desc="Edge Effect">

}