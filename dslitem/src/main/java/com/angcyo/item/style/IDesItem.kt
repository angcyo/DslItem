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
interface IDesItem : IAutoInitItem {

    /**统一样式配置*/
    var desItemConfig: DesItemConfig

    /**初始化*/
    @ItemInitEntryPoint
    fun initDesItem(itemHolder: DslViewHolder,
                    itemPosition: Int,
                    adapterItem: DslAdapterItem,
                    payloads: List<Any>) {
        itemHolder.tv(desItemConfig.itemDesViewId)?.apply {
            desItemConfig.itemDesStyle.updateStyle(this)
        }
    }

    fun configDesStyle(action: TextStyleConfig.() -> Unit) {
        desItemConfig.itemDesStyle.action()
    }
}

var IDesItem.itemDes: CharSequence?
    get() = desItemConfig.itemDes
    set(value) {
        desItemConfig.itemDes = value
    }

class DesItemConfig : IDslItemConfig {

    var itemDesViewId: Int = R.id.lib_des_view

    /**文本*/
    var itemDes: CharSequence? = null
        set(value) {
            field = value
            itemDesStyle.text = value
        }

    /**统一样式配置*/
    var itemDesStyle = TextStyleConfig()
}