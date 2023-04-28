package com.angcyo.widget

import android.view.MotionEvent

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/11/19
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
interface ITouchDelegate {
    fun getTouchActionDelegate(): TouchActionDelegate
}

fun ITouchDelegate.onDispatchTouchEventAction(action: (ev: MotionEvent) -> Unit): TouchListener {
    val listener = object : TouchListener {
        override fun onDispatchTouchEventAction(ev: MotionEvent) {
            action(ev)
        }
    }
    getTouchActionDelegate()?.touchListener?.add(listener)
    return listener
}

fun ITouchDelegate.onInterceptTouchEventAction(action: (ev: MotionEvent) -> Unit): TouchListener {
    val listener = object : TouchListener {
        override fun onInterceptTouchEventAction(ev: MotionEvent) {
            action(ev)
        }
    }
    getTouchActionDelegate()?.touchListener?.add(listener)
    return listener
}

fun ITouchDelegate.onTouchEventAction(action: (ev: MotionEvent) -> Unit): TouchListener {
    val listener = object : TouchListener {
        override fun onTouchEventAction(ev: MotionEvent) {
            action(ev)
        }
    }
    getTouchActionDelegate()?.touchListener?.add(listener)
    return listener
}