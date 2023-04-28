package com.angcyo.item.style

import android.graphics.Color
import android.view.View
import com.angcyo.dpi
import com.angcyo.item.R
import com.angcyo.item._color
import com.angcyo.widget.DslButton
import com.angcyo.widget.colorState
import com.angcyo.widget.span.undefined_float

/**
 * 按钮样式配置
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/09
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
open class ButtonStyleConfig : TextStyleConfig() {

    companion object {
        //主题渐变样式, 标准的填充色按钮
        val BUTTON_STYLE_THEME = 1

        //填充颜色的样式, 无渐变, 纯色填充+波纹效果
        val BUTTON_STYLE_SOLID = 2

        //边框->主题渐变填充
        val BUTTON_STYLE_FILL = 3
    }

    /**特定样式*/
    var style: Int = BUTTON_STYLE_THEME

    /**样式[BUTTON_STYLE_THEME] [BUTTON_STYLE_FILL]时, 渐变的颜色*/
    var styleThemeColors = intArrayOf(
        _color(R.color.colorPrimary),
        _color(R.color.colorPrimaryDark)
    )

    /**样式[BUTTON_STYLE_THEME] [BUTTON_STYLE_FILL]时, 文本的颜色*/
    var styleThemeTextColor = Color.WHITE

    /**样式[BUTTON_STYLE_SOLID]时, solid的颜色*/
    var styleSolidSolidColor = Color.WHITE

    /**样式[BUTTON_STYLE_FILL]时, solid的颜色*/
    var styleFillSolidColor = Color.TRANSPARENT

    /**样式[BUTTON_STYLE_SOLID] [BUTTON_STYLE_FILL]时, 文本的颜色*/
    var styleSolidTextColor = _color(R.color.text_general_color)
        set(value) {
            field = value
            textColor = value
        }

    /**[BUTTON_STYLE_FILL]*/
    var styleFillStrokeWidth = 2 * dpi

    /**[BUTTON_STYLE_FILL]*/
    var styleFillStrokeColor = _color(R.color.colorPrimary)

    var buttonRadius: Float = undefined_float

    init {
        themeStyle()
    }

    override fun updateStyle(view: View) {
        super.updateStyle(view)

        if (view is DslButton) {

            //不支持不同圆角大小的样式
            if (buttonRadius == undefined_float) {
                buttonRadius = view.normalRadii.first()
            }
            view.setButtonRadius(buttonRadius)

            when (style) {
                BUTTON_STYLE_THEME -> {
                    view.enableTextStyle = true
                    view.setButtonGradientColors(styleThemeColors)
                    view.setButtonTextColor(textColor)
                    view.setButtonStrokeWidth(0)
                }

                BUTTON_STYLE_SOLID -> {
                    view.enableTextStyle = true
                    view.setButtonGradientColors(null)
                    view.setButtonSolidColor(styleSolidSolidColor)
                    view.setButtonTextColor(textColor)
                    view.setButtonStrokeWidth(0)
                }

                BUTTON_STYLE_FILL -> {
                    view.enableTextStyle = false
                    view.setButtonGradientColors(null)
                    view.setButtonSolidColor(styleFillSolidColor)
                    view.setButtonStrokeWidth(styleFillStrokeWidth)
                    view.setButtonStrokeColor(styleFillStrokeColor)

                    view.pressStrokeWidth = 0
                    view.pressGradientColors = styleThemeColors
                }
            }

            view.updateDrawable()
        }
    }

    /**使用主题样式, 颜色渐变*/
    fun themeStyle() {
        style = BUTTON_STYLE_THEME
        textColor = styleThemeTextColor
    }

    /**使用渐变样式*/
    fun gradientStyle(gradientStartColor: Int, gradientEndColor: Int = gradientStartColor) {
        themeStyle()
        styleThemeColors = intArrayOf(gradientStartColor, gradientEndColor)
    }

    /**填充颜色的样式*/
    fun solidStyle() {
        style = BUTTON_STYLE_SOLID
        textColor = styleSolidTextColor
    }

    /**边框 填充样式*/
    fun fillStyle() {
        style = BUTTON_STYLE_FILL
        textColors = colorState(
            DslButton.ATTR_PRESSED to styleThemeTextColor,
            DslButton.ATTR_NORMAL to styleThemeColors[0]
        )
    }

    /**白底黑字样式*/
    fun whiteStyle() {
        style = BUTTON_STYLE_SOLID
        textColor = styleSolidTextColor
        styleSolidSolidColor = Color.WHITE
    }
}