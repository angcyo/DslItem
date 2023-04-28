package com.angcyo.widget

import android.view.MotionEvent

/**
 * Touch监听
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/11/19
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
interface TouchListener {

    fun onDispatchTouchEventAction(ev: MotionEvent) {

    }

    fun onInterceptTouchEventAction(ev: MotionEvent) {

    }

    fun onTouchEventAction(ev: MotionEvent) {

    }
}