package com.angcyo.item

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.github.SwitchButton
import com.angcyo.dsladapter.DslViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/08/09
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslSwitchInfoItem : DslBaseInfoItem() {

    /**是否选中*/
    var itemSwitchChecked = false

    /**状态回调, 提供一个可以完全覆盖的方法*/
    var itemSwitchChanged: (checked: Boolean) -> Unit = {
        onItemSwitchChanged(it)
    }

    init {
        itemExtendLayoutId = R.layout.dsl_extent_switch_item
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.v<SwitchButton>(R.id.lib_switch_view)?.apply {

            setOnCheckedChangeListener(object : SwitchButton.OnCheckedChangeListener {
                override fun onCheckedChanged(view: SwitchButton, isChecked: Boolean) {
                    val old = itemSwitchChecked
                    itemSwitchChecked = isChecked
                    if (old != itemSwitchChecked) {
                        itemSwitchChanged(itemSwitchChecked)
                    }
                }
            })

            //刷新界面的时候, 不执行动画
            val old = isEnableEffect
            isEnableEffect = false
            isChecked = itemSwitchChecked
            isEnableEffect = old
        }
    }

    /**提供一个可以被重写的子类方法*/
    open fun onItemSwitchChanged(checked: Boolean) {

    }
}