package com.angcyo.item

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.UndefinedDrawable
import com.angcyo.dsladapter.setBgDrawable
import com.angcyo.widget.span.undefined_color
import com.angcyo.widget.span.undefined_float


/**
 * 带有Label的item
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/23
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class DslBaseLabelItem : DslAdapterItem() {

    /**左边的Label文本*/
    var itemLabelText: CharSequence? = null
        set(value) {
            field = value
            itemLabelTextStyle.text = value
        }

    /**统一样式配置*/
    var itemLabelTextStyle = TextStyleConfig()

    init {
        itemLayoutId = R.layout.dsl_label_item
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.gone(R.id.lib_label_view, itemLabelText == null)

        itemHolder.tv(R.id.lib_label_view)?.apply {
            itemLabelTextStyle.updateStyle(this)
        }
    }

    open fun configLabelTextStyle(action: TextStyleConfig.() -> Unit) {
        itemLabelTextStyle.action()
    }
}

/**文本样式配置*/
open class TextStyleConfig {
    var text: CharSequence? = null
    var hint: CharSequence? = null
    var textBold: Boolean = false
    var textColor: Int = undefined_color
    var textColors: ColorStateList? = null
    var textSize: Float = undefined_float
    var textGravity: Int = Gravity.LEFT or Gravity.CENTER_VERTICAL
    var backgroundDrawable: Drawable? = UndefinedDrawable()

    /**生效*/
    open fun updateStyle(textView: TextView) {
        with(textView) {
            if (backgroundDrawable is UndefinedDrawable) {
                backgroundDrawable = background
            }
            setBgDrawable(backgroundDrawable)

            text = this@TextStyleConfig.text
            hint = this@TextStyleConfig.hint

            gravity = textGravity

            setBoldText(textBold)

            //颜色, 防止复用. 所以在未指定的情况下, 要获取默认的颜色.
            val colors = when {
                this@TextStyleConfig.textColors != null -> {
                    this@TextStyleConfig.textColors
                }
                textColor != undefined_color -> {
                    ColorStateList.valueOf(textColor)
                }
                else -> {
                    textColors
                }
            }
            if (colors != this@TextStyleConfig.textColors) {
                this@TextStyleConfig.textColors = colors
            }
            setTextColor(colors)

            //字体大小同理.
            val size = if (this@TextStyleConfig.textSize != undefined_float) {
                this@TextStyleConfig.textSize
            } else {
                textSize
            }
            this@TextStyleConfig.textSize = size
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }
}
