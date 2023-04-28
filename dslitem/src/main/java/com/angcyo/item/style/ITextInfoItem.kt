package com.angcyo.item.style

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.annotation.ItemInitEntryPoint
import com.angcyo.dsladapter.item.IDslItemConfig
import com.angcyo.item.R
import com.angcyo.dsladapter.DslViewHolder

/**
 * 带Text的item
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/07/16
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
interface ITextInfoItem : IAutoInitItem {

    var textInfoItemConfig: TextInfoItemConfig

    /**初始化*/
    @ItemInitEntryPoint
    fun initInfoTextItem(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        itemHolder.tv(textInfoItemConfig.itemInfoTextViewId)?.apply {
            textInfoItemConfig.itemInfoTextStyle.updateStyle(this)
        }
    }

    fun configInfoTextStyle(action: TextStyleConfig.() -> Unit) {
        textInfoItemConfig.itemInfoTextStyle.action()
    }
}

var ITextInfoItem.itemInfoText: CharSequence?
    get() = textInfoItemConfig.itemInfoText
    set(value) {
        textInfoItemConfig.itemInfoText = value
    }

class TextInfoItemConfig : IDslItemConfig {
    /**[R.id.lib_text_view]*/
    var itemInfoTextViewId: Int = R.id.lib_text_view

    /**条目文本*/
    var itemInfoText: CharSequence? = null
        set(value) {
            field = value
            itemInfoTextStyle.text = value
        }

    /**统一样式配置*/
    var itemInfoTextStyle: TextStyleConfig = TextStyleConfig()
}