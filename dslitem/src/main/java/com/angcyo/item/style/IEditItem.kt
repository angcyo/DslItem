package com.angcyo.item.style

import android.text.InputFilter
import android.text.InputType
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.annotation.ItemInitEntryPoint
import com.angcyo.dsladapter.item.IDslItemConfig
import com.angcyo.item.DslBaseEditItem
import com.angcyo.item.R
import com.angcyo.item.clearListeners
import com.angcyo.item.onTextChange
import com.angcyo.item.restoreSelection

/**
 * 输入框item
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/28
 * Copyright (c) 2020 angcyo. All rights reserved.
 */

/**文本改变通知回调*/
typealias TextChangeAction = (CharSequence) -> Unit

interface IEditItem : IAutoInitItem {

    /**配置项*/
    var editItemConfig: EditItemConfig

    /**初始化*/
    @ItemInitEntryPoint
    fun initEditItem(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        itemHolder.ev(editItemConfig.itemEditTextViewId)?.apply {
            clearListeners()

            //[EditStyleConfig]样式初始化
            editItemConfig.itemEditTextStyle.updateStyle(this)

            onTextChange {
                editItemConfig._lastEditSelectionStart = selectionStart
                editItemConfig._lastEditSelectionEnd = selectionEnd

                editItemConfig.itemEditText = it
            }

            //放在最后监听, 防止首次setInputText, 就触发事件.
            onTextChange(shakeDelay = editItemConfig.itemTextChangeShakeDelay) {
                if (this@IEditItem is DslAdapterItem) {
                    itemChanging = true
                }
                onItemEditTextChange(itemHolder, it)
            }

            restoreSelection(
                editItemConfig._lastEditSelectionStart,
                editItemConfig._lastEditSelectionEnd
            )
        }
    }

    fun configEditTextStyle(action: EditStyleConfig.() -> Unit) {
        editItemConfig.itemEditTextStyle.action()
    }

    /**清除之前的监听*/
    fun clearEditListeners(itemHolder: DslViewHolder) {
        itemHolder.ev(editItemConfig.itemEditTextViewId)?.clearListeners()
    }

    /**编辑的文本改变后*/
    fun onItemEditTextChange(itemHolder: DslViewHolder, text: CharSequence) {
        editItemConfig.itemTextChangeAction?.invoke(text)
    }
}

var IEditItem.itemEditText: CharSequence?
    get() = editItemConfig.itemEditText
    set(value) {
        editItemConfig.itemEditText = value
    }

var IEditItem.itemEditHint: CharSequence?
    get() = editItemConfig.itemEditTextStyle.hint
    set(value) {
        editItemConfig.itemEditTextStyle.hint = value
    }

/**
 * 输入类型
 * [InputType.TYPE_CLASS_TEXT]
 * [InputType.TYPE_CLASS_NUMBER]
 *
 * [InputType.TYPE_TEXT_FLAG_MULTI_LINE]
 *
 * [InputType.TYPE_NUMBER_FLAG_DECIMAL]
 * [InputType.TYPE_NUMBER_FLAG_SIGNED]
 * */
var IEditItem.itemEditInputType: Int
    get() = editItemConfig.itemEditTextStyle.editInputType
    set(value) {
        editItemConfig.itemEditTextStyle.editInputType = value
    }


var IEditItem.itemMaxInputLength: Int
    get() = editItemConfig.itemEditTextStyle.editMaxInputLength
    set(value) {
        editItemConfig.itemEditTextStyle.editMaxInputLength = value
    }

/**输入过滤*/
var IEditItem.itemInputFilterList: MutableList<InputFilter>
    get() = editItemConfig.itemEditTextStyle.editInputFilterList
    set(value) {
        editItemConfig.itemEditTextStyle.editInputFilterList = value
    }

/**输入过滤*/
var IEditItem.itemEditDigits: String?
    get() = editItemConfig.itemEditTextStyle.editDigits
    set(value) {
        editItemConfig.itemEditTextStyle.editDigits = value
    }

var IEditItem.itemTextChangeAction: TextChangeAction?
    get() = editItemConfig.itemTextChangeAction
    set(value) {
        editItemConfig.itemTextChangeAction = value
    }

class EditItemConfig : IDslItemConfig {

    /**[R.id.lib_edit_view]*/
    var itemEditTextViewId: Int = R.id.lib_edit_view

    /**输入框内容*/
    var itemEditText: CharSequence? = null
        set(value) {
            field = value
            itemEditTextStyle.text = value
        }

    /**是否可编辑*/
    var itemNoEditModel: Boolean? = null
        set(value) {
            field = value
            if (value == true) {
                itemEditTextStyle.hint = null
            }
        }

    /**统一样式配置*/
    var itemEditTextStyle: EditStyleConfig = EditStyleConfig()

    /**文本改变*/
    var itemTextChangeAction: TextChangeAction? = null

    /**文本改变去频限制, 负数表示不开启, 如果短时间内关闭界面了, 可能会获取不到最新的输入框数据*/
    var itemTextChangeShakeDelay: Long = DslBaseEditItem.DEFAULT_INPUT_SHAKE_DELAY

    //用于恢复光标的位置
    var _lastEditSelectionStart: Int = -1

    var _lastEditSelectionEnd: Int = -1
}