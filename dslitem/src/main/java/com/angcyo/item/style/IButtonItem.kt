package com.angcyo.item.style

import android.view.Gravity
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.annotation.ItemInitEntryPoint
import com.angcyo.dsladapter.item.IDslItemConfig
import com.angcyo.item.R
import com.angcyo.widget.DslButton
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.item.button

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/09/24
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
interface IButtonItem : IAutoInitItem {

    var buttonItemConfig: ButtonItemConfig

    @ItemInitEntryPoint
    fun initButtonItem(itemHolder: DslViewHolder,
                       itemPosition: Int,
                       adapterItem: DslAdapterItem,
                       payloads: List<Any>) {
        itemHolder.itemView.isClickable = false

        itemHolder.button(buttonItemConfig.itemButtonViewId)?.apply {
            buttonItemConfig.itemButtonStyle.updateStyle(this)
            buttonItemConfig.itemButtonConfig(this)

            if (this@IButtonItem is DslAdapterItem) {
                setOnClickListener(_clickListener)
                setOnLongClickListener(_longClickListener)
            }
        }
    }

    fun configButtonStyle(action: ButtonStyleConfig.() -> Unit) {
        buttonItemConfig.itemButtonStyle.action()
    }
}

var IButtonItem.itemButtonText: CharSequence?
    get() = buttonItemConfig.itemButtonText
    set(value) {
        buttonItemConfig.itemButtonText = value
    }

class ButtonItemConfig : IDslItemConfig {

    var itemButtonViewId: Int = R.id.lib_button

    /**按钮显示的文本*/
    var itemButtonText: CharSequence? = null
        set(value) {
            field = value
            itemButtonStyle.text = value
        }

    /**按钮样式配置项*/
    var itemButtonStyle = ButtonStyleConfig().apply {
        textGravity = Gravity.CENTER
    }

    /**按钮配置回调*/
    var itemButtonConfig: (DslButton) -> Unit = {

    }
}