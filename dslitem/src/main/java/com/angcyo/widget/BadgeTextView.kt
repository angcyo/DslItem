package com.angcyo.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.angcyo.drawable.DslAttrBadgeDrawable
import com.angcyo.item.isNotSpecified

/**
 * 单纯的用来绘制角标的控件
 * 使用属性[app:r_badge_text="xxx"]设置角标
 *
 * badgePaddingLeft
 * badgePaddingRight
 * badgePaddingTop
 * badgePaddingBottom
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/09/23
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class BadgeTextView : AppCompatTextView, IBadgeView {

    /**角标绘制*/
    override var dslBadeDrawable = DslAttrBadgeDrawable()

    constructor(context: Context) : super(context) {
        initAttribute(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttribute(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttribute(context, attrs)
    }

    private fun initAttribute(context: Context, attributeSet: AttributeSet?) {
        dslBadeDrawable.apply {
            badgeOffsetX = 0
            badgeOffsetY = 0
            initAttribute(context, attributeSet)
            callback = this@BadgeTextView
            dslGravity.gravityRelativeCenter = false
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        dslBadeDrawable.apply {
            setBounds(0, 0, measuredWidth, measuredHeight)
            draw(canvas)
        }
    }

    /*override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        //在构造方法中调用此方法[dslBadeDrawable]为空
        dslBadeDrawable?.badgeText = text.toString()
    }*/

    /**角标的文本, 空字符串会绘制成小圆点*/
    fun updateBadge(text: String? = null, action: DslAttrBadgeDrawable.() -> Unit = {}) {
        dslBadeDrawable.apply {
            drawBadge = true
            //badgeGravity = Gravity.TOP or Gravity.RIGHT
            badgeText = text
            //badgeCircleRadius
            //badgeOffsetY = 4 * dpi
            //cornerRadius(25 * dp)
            action()
        }
        postInvalidateOnAnimation()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //of kotlin
        //val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        //val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        if (widthMode.isNotSpecified() && heightMode.isNotSpecified()) {
            setMeasuredDimension(
                dslBadeDrawable.intrinsicWidth + paddingLeft + paddingRight,
                dslBadeDrawable.intrinsicHeight + paddingTop + paddingBottom
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}