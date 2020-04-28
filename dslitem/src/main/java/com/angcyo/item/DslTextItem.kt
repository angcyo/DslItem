package com.angcyo.item

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder

/**
 * 简单的文本显示item
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/23
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
open class DslTextItem : DslAdapterItem() {

    /**文本*/
    var itemText: CharSequence? = null
        set(value) {
            field = value
            itemTextStyle.text = value
        }

    /**统一样式配置*/
    var itemTextStyle = TextStyleConfig()

    init {
        itemLayoutId = R.layout.dsl_text_item
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.tv(R.id.lib_text_view)?.apply {
            itemTextStyle.updateStyle(this)
        }
    }

    open fun configTextStyle(action: TextStyleConfig.() -> Unit) {
        itemTextStyle.action()
    }
}