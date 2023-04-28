package com.angcyo.item.style

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.annotation.ItemInitEntryPoint
import com.angcyo.dsladapter.item.IDslItemConfig
import com.angcyo.item.R
import com.angcyo.dsladapter.DslViewHolder

/**
 * 带Label的item
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/28
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
interface ILabelItem : IAutoInitItem {

    /**统一样式配置*/
    var labelItemConfig: LabelItemConfig

    /**初始化*/
    @ItemInitEntryPoint
    fun initLabelItem(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        itemHolder.tv(labelItemConfig.itemLabelViewId)?.apply {
            labelItemConfig.itemLabelTextStyle.updateStyle(this)
        }
    }

    fun configLabelTextStyle(action: TextStyleConfig.() -> Unit) {
        labelItemConfig.itemLabelTextStyle.action()
    }
}

var ILabelItem.itemLabelText: CharSequence?
    get() = labelItemConfig.itemLabelText
    set(value) {
        labelItemConfig.itemLabelText = value
    }

var ILabelItem.itemLabel: CharSequence?
    get() = labelItemConfig.itemLabelText
    set(value) {
        labelItemConfig.itemLabelText = value
    }

/**label视图的最小宽度*/
var ILabelItem.itemLabelMinWidth: Int
    get() = labelItemConfig.itemLabelTextStyle.viewMinWidth
    set(value) {
        configLabelTextStyle {
            viewMinWidth = value
        }
    }

class LabelItemConfig : IDslItemConfig {

    /**[R.id.lib_label_view]*/
    var itemLabelViewId: Int = R.id.lib_label_view

    /**Label文本*/
    var itemLabelText: CharSequence? = null
        set(value) {
            field = value
            itemLabelTextStyle.text = value
        }

    /**统一样式配置*/
    var itemLabelTextStyle: TextStyleConfig = TextStyleConfig()
}