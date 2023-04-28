package com.angcyo.item.style

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.annotation.ItemInitEntryPoint
import com.angcyo.dsladapter.item.IDslItemConfig
import com.angcyo.item.R
import com.angcyo.item._dimen
import com.angcyo.widget.span.undefined_float

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/09/18
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
interface ITextItem : IAutoInitItem {

    /**配置类 */
    var textItemConfig: TextItemConfig

    /**初始化*/
    @ItemInitEntryPoint
    fun initTextItem(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        itemHolder.tv(textItemConfig.itemTextViewId)?.apply {
            textItemConfig.itemTextStyle.updateStyle(this)
        }
    }

    fun configTextStyle(action: TextStyleConfig.() -> Unit) {
        textItemConfig.itemTextStyle.action()
    }

    /**加粗样式*/
    fun boldStyle() {
        configTextStyle {
            textBold = true
            if (textSize == undefined_float) {
                textSize = _dimen(R.dimen.text_sub_size).toFloat()
            }
        }
    }
}

var ITextItem.itemText: CharSequence?
    get() = textItemConfig.itemText
    set(value) {
        textItemConfig.itemText = value
    }

var ITextItem.itemHint: CharSequence?
    get() = textItemConfig.itemTextStyle.hint
    set(value) {
        textItemConfig.itemTextStyle.hint = value
    }

class TextItemConfig : IDslItemConfig {

    /**[R.id.lib_text_view]*/
    var itemTextViewId: Int = R.id.lib_text_view

    /**条目文本*/
    var itemText: CharSequence? = null
        set(value) {
            field = value
            itemTextStyle.text = value
        }

    /**统一样式配置*/
    var itemTextStyle: TextStyleConfig = TextStyleConfig()

}