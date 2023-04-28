package com.angcyo.item.style

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.annotation.ItemInitEntryPoint
import com.angcyo.dsladapter.item.IDslItemConfig
import com.angcyo.github.SwitchButton
import com.angcyo.item.R
import com.angcyo.dsladapter.DslViewHolder

/**
 * [com.angcyo.github.SwitchButton]
 * [com.angcyo.item.DslPropertySwitchItem]
 * [com.angcyo.item.DslSwitchInfoItem]
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/06/08
 */
interface ISwitchItem : IAutoInitItem {

    /**统一样式配置*/
    var switchItemConfig: SwitchItemConfig

    @ItemInitEntryPoint
    fun initSwitchItem(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        itemHolder.v<SwitchButton>(switchItemConfig.itemSwitchViewId)?.apply {
            setOnCheckedChangeListener(null)

            //刷新界面的时候, 不执行动画
            val old = isEnableEffect
            isEnableEffect = false
            isChecked = itemSwitchChecked
            isEnableEffect = old

            setOnCheckedChangeListener(object : SwitchButton.OnCheckedChangeListener {
                override fun onCheckedChanged(view: SwitchButton, isChecked: Boolean) {
                    val checked = itemSwitchChecked
                    itemSwitchChecked = isChecked
                    if (checked != itemSwitchChecked) {
                        if (this@ISwitchItem is DslAdapterItem) {
                            itemChanging = true
                        }
                        switchItemConfig.itemSwitchChangedAction(itemSwitchChecked)
                    }
                }
            })
        }
    }

    /**config*/
    fun configSwitchItem(action: SwitchItemConfig.() -> Unit) {
        switchItemConfig.action()
    }
}

var ISwitchItem.itemSwitchChecked: Boolean
    get() = switchItemConfig.itemSwitchChecked
    set(value) {
        switchItemConfig.itemSwitchChecked = value
    }

var ISwitchItem.itemSwitchChangedAction: (checked: Boolean) -> Unit
    get() = switchItemConfig.itemSwitchChangedAction
    set(value) {
        switchItemConfig.itemSwitchChangedAction = value
    }

class SwitchItemConfig : IDslItemConfig {

    /**[R.id.lib_switch_view]*/
    var itemSwitchViewId: Int = R.id.lib_switch_view

    /**是否选中*/
    var itemSwitchChecked = false

    /**状态回调, 提供一个可以完全覆盖的方法*/
    var itemSwitchChangedAction: (checked: Boolean) -> Unit = {
    }
}