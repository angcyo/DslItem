package com.angcyo.item.style

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.angcyo.dsladapter.UndefinedDrawable
import com.angcyo.dsladapter.setBgDrawable
import com.angcyo.dsladapter.setWidthHeight
import com.angcyo.dsladapter.undefined_size
import com.angcyo.widget.span.undefined_int

/**
 * View基础样式配置
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/09
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */

open class ViewStyleConfig {

    /**限制最小宽高*/
    var viewWidth: Int = undefined_size
    var viewMinWidth: Int = undefined_size

    /**指定宽高*/
    var viewHeight: Int = undefined_size
    var viewMinHeight: Int = undefined_size

    /**视图可见性[visibility]*/
    var viewVisibility: Int = undefined_int

    /**padding值*/
    var paddingLeft: Int = undefined_size
    var paddingRight: Int = undefined_size
    var paddingTop: Int = undefined_size
    var paddingBottom: Int = undefined_size

    /**背景*/
    var backgroundDrawable: Drawable? = UndefinedDrawable()

    /**部分布局支持*/
    var layoutGravity: Int = Gravity.NO_GRAVITY

    /**
     * 需要parent为[ConstraintLayout]
     * [androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.dimensionRatio]*/
    var viewDimensionRatio: String? = null

    /**更新样式*/
    open fun updateStyle(view: View) {
        with(view) {
            //初始化默认值
            if (this@ViewStyleConfig.paddingLeft == undefined_size) {
                this@ViewStyleConfig.paddingLeft = paddingLeft
            }
            if (this@ViewStyleConfig.paddingRight == undefined_size) {
                this@ViewStyleConfig.paddingRight = paddingRight
            }
            if (this@ViewStyleConfig.paddingTop == undefined_size) {
                this@ViewStyleConfig.paddingTop = paddingTop
            }
            if (this@ViewStyleConfig.paddingBottom == undefined_size) {
                this@ViewStyleConfig.paddingBottom = paddingBottom
            }

            //可见性
            if (viewVisibility == undefined_int) {
                viewVisibility = visibility
            }
            visibility = viewVisibility

            //设置
            setPadding(
                this@ViewStyleConfig.paddingLeft,
                this@ViewStyleConfig.paddingTop,
                this@ViewStyleConfig.paddingRight,
                this@ViewStyleConfig.paddingBottom
            )

            val lp = layoutParams

            if (backgroundDrawable is UndefinedDrawable) {
                backgroundDrawable = background
            }
            setBgDrawable(backgroundDrawable)

            //初始化默认值
            if (viewMinWidth == undefined_size && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewMinWidth = minimumWidth
            }
            if (viewMinHeight == undefined_size && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewMinHeight = minimumHeight
            }
            //设置
            if (viewMinWidth != undefined_size) {
                minimumWidth = viewMinWidth
                when (view) {
                    is ConstraintLayout -> view.minWidth = viewMinWidth
                }
            }
            if (viewMinHeight != undefined_size) {
                minimumHeight = viewMinHeight
                when (view) {
                    is ConstraintLayout -> view.minHeight = viewMinHeight
                }
            }

            //初始化默认值
            if (viewWidth == undefined_size) {
                viewWidth = lp.width
            }
            if (viewHeight == undefined_size) {
                viewHeight = lp.height
            }
            //设置
            setWidthHeight(viewWidth, viewHeight)
            if (lp is ConstraintLayout.LayoutParams) {
                if (viewDimensionRatio == null) {
                    viewDimensionRatio = lp.dimensionRatio
                } else {
                    lp.dimensionRatio = viewDimensionRatio
                    layoutParams = lp
                }
            }

            //Gravity
            if (lp is FrameLayout.LayoutParams) {
                val oldGravity = lp.gravity
                if (layoutGravity == Gravity.NO_GRAVITY) {
                    layoutGravity = oldGravity
                }
                lp.gravity = layoutGravity
                if (oldGravity != layoutGravity) {
                    layoutParams = lp
                }
            }
        }
    }
}