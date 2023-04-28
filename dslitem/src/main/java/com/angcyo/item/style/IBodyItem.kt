package com.angcyo.item.style

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.annotation.ItemInitEntryPoint
import com.angcyo.dsladapter.item.IDslItemConfig
import com.angcyo.item.R
import com.angcyo.dsladapter.DslViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/09/23
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
interface IBodyItem : IAutoInitItem {

    /**统一样式配置*/
    var bodyItemConfig: BodyItemConfig

    /**初始化*/
    @ItemInitEntryPoint
    fun initBodyItem(itemHolder: DslViewHolder,
                     itemPosition: Int,
                     adapterItem: DslAdapterItem,
                     payloads: List<Any>) {
        itemHolder.tv(bodyItemConfig.itemBodyViewId)?.apply {
            bodyItemConfig.itemBodyStyle.updateStyle(this)
        }
    }

    fun configBodyStyle(action: TextStyleConfig.() -> Unit) {
        bodyItemConfig.itemBodyStyle.action()
    }
}

var IBodyItem.itemBodyText: CharSequence?
    get() = bodyItemConfig.itemBodyText
    set(value) {
        bodyItemConfig.itemBodyText = value
    }

class BodyItemConfig : IDslItemConfig {

    var itemBodyViewId: Int = R.id.lib_body_view

    /**文本*/
    var itemBodyText: CharSequence? = null
        set(value) {
            field = value
            itemBodyStyle.text = value
        }

    /**统一样式配置*/
    var itemBodyStyle = TextStyleConfig()
}