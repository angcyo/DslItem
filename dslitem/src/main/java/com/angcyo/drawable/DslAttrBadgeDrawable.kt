package com.angcyo.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import com.angcyo.dpi
import com.angcyo.item.R

/**
 * 角标
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/13
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslAttrBadgeDrawable : DslBadgeDrawable() {

    /**是否要绘制角标*/
    var drawBadge: Boolean = true

    override fun initAttribute(context: Context, attributeSet: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.DslAttrBadgeDrawable)
        drawBadge = typedArray.getBoolean(R.styleable.DslAttrBadgeDrawable_r_draw_badge, drawBadge)
        gradientSolidColor =
            typedArray.getColor(
                R.styleable.DslAttrBadgeDrawable_r_badge_solid_color,
                Color.RED
            )
        badgeTextColor =
            typedArray.getColor(
                R.styleable.DslAttrBadgeDrawable_r_badge_text_color,
                Color.WHITE
            )
        badgeGravity = typedArray.getInt(
            R.styleable.DslAttrBadgeDrawable_r_badge_gravity,
            Gravity.TOP or Gravity.RIGHT
        )
        badgeOffsetX = typedArray.getDimensionPixelOffset(
            R.styleable.DslAttrBadgeDrawable_r_badge_offset_x,
            badgeOffsetX
        )
        badgeCircleRadius = typedArray.getDimensionPixelOffset(
            R.styleable.DslAttrBadgeDrawable_r_badge_circle_radius,
            4 * dpi
        )
        val badgeRadius = typedArray.getDimensionPixelOffset(
            R.styleable.DslAttrBadgeDrawable_r_badge_radius,
            10 * dpi
        )
        cornerRadius(badgeRadius.toFloat())
        badgeOffsetY = typedArray.getDimensionPixelOffset(
            R.styleable.DslAttrBadgeDrawable_r_badge_offset_y,
            badgeOffsetY
        )
        badgePaddingLeft = typedArray.getDimensionPixelOffset(
            R.styleable.DslAttrBadgeDrawable_r_badge_padding_left,
            4 * dpi
        )
        badgePaddingRight = typedArray.getDimensionPixelOffset(
            R.styleable.DslAttrBadgeDrawable_r_badge_padding_right,
            4 * dpi
        )
        badgePaddingTop = typedArray.getDimensionPixelOffset(
            R.styleable.DslAttrBadgeDrawable_r_badge_padding_top,
            0
        )
        badgePaddingBottom = typedArray.getDimensionPixelOffset(
            R.styleable.DslAttrBadgeDrawable_r_badge_padding_bottom,
            0
        )
        //脚本文本内容
        badgeText = typedArray.getString(R.styleable.DslAttrBadgeDrawable_r_badge_text)
        badgeTextSize = typedArray.getDimensionPixelOffset(
            R.styleable.DslAttrBadgeDrawable_r_badge_text_size,
            12 * dpi
        ).toFloat()

        //自定义的背景
        originDrawable =
            typedArray.getDrawable(R.styleable.DslAttrBadgeDrawable_r_badge_bg_drawable)

        badgeTextOffsetX = typedArray.getDimensionPixelOffset(
            R.styleable.DslAttrBadgeDrawable_r_badge_text_offset_x,
            badgeTextOffsetX
        )

        badgeTextOffsetY = typedArray.getDimensionPixelOffset(
            R.styleable.DslAttrBadgeDrawable_r_badge_text_offset_y,
            badgeTextOffsetY
        )

        badgeCircleOffsetX = typedArray.getDimensionPixelOffset(
            R.styleable.DslAttrBadgeDrawable_r_badge_circle_offset_x,
            badgeTextOffsetX
        )

        badgeCircleOffsetY = typedArray.getDimensionPixelOffset(
            R.styleable.DslAttrBadgeDrawable_r_badge_circle_offset_y,
            badgeTextOffsetY
        )
        typedArray.recycle()
        super.initAttribute(context, attributeSet)
    }

    override fun draw(canvas: Canvas) {
        if (drawBadge) {
            super.draw(canvas)
        }
    }
}