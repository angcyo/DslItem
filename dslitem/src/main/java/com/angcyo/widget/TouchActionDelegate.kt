package com.angcyo.widget

import android.view.MotionEvent

/**
 * 手势代理
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/11/19
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
open class TouchActionDelegate {

    val touchListener = mutableSetOf<TouchListener>()

    fun dispatchTouchEvent(ev: MotionEvent) {
        touchListener.forEach {
            it.onDispatchTouchEventAction(ev)
        }
    }

    fun onInterceptTouchEvent(ev: MotionEvent) {
        touchListener.forEach {
            it.onInterceptTouchEventAction(ev)
        }
    }

    fun onTouchEvent(ev: MotionEvent) {
        touchListener.forEach {
            it.onTouchEventAction(ev)
        }
    }

}