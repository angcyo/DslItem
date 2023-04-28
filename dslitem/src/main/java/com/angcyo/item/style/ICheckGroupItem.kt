package com.angcyo.item.style

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.annotation.ItemInitEntryPoint
import com.angcyo.dsladapter.item.IDslItemConfig
import com.angcyo.extend.IToText
import com.angcyo.extend.IToValue
import com.angcyo.item.R
import com.angcyo.item.string
import com.angcyo.item.toStr
import com.angcyo.widget.DslSelector
import com.angcyo.widget.find
import com.angcyo.widget.resetChild

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/06/20
 */
interface ICheckGroupItem : IAutoInitItem {

    /**配置项*/
    var checkGroupItemConfig: CheckGroupItemConfig

    @ItemInitEntryPoint
    fun initCheckGroupItem(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        itemHolder.v<ViewGroup>(checkGroupItemConfig.itemCheckGroupViewId)
            ?.apply {

                resetChild(
                    checkGroupItemConfig.itemCheckItems.size,
                    checkGroupItemConfig.itemCheckLayoutId
                ) { itemView, itemIndex ->
                    val item = checkGroupItemConfig.itemCheckItems[itemIndex]
                    itemView.tag = checkGroupItemConfig.itemCheckItemToValue(item)  //保存数据
                    itemView.find<TextView>(R.id.lib_text_view)?.text =
                        checkGroupItemConfig.itemCheckItemToText(item)
                }

                /**安装选择组件*/
                checkGroupItemConfig.itemSelectorHelper.install(this) {
                    dslMultiMode = checkGroupItemConfig.itemMultiMode
                    dslMinSelectLimit =
                        if (checkGroupItemConfig.itemMultiMode) checkGroupItemConfig.itemMinSelectLimit else 1

                    onSelectItemView = this@ICheckGroupItem::onCheckInterceptSelectView
                    onSelectViewChange = this@ICheckGroupItem::onCheckSelectViewChange
                    onSelectIndexChange = this@ICheckGroupItem::onCheckSelectIndexChange
                }

                val indexList = mutableListOf<Int>()
                checkGroupItemConfig.itemCheckedItems.forEach {
                    indexList.add(checkGroupItemConfig.itemCheckItems.indexOf(it))
                }
                checkGroupItemConfig.itemSelectorHelper.selector(
                    indexList,
                    fromUser = checkGroupItemConfig.itemFirstNotifyChange
                )
                checkGroupItemConfig.itemFirstNotifyChange = false
            }
    }

    /**是否需要拦截选中*/
    fun onCheckInterceptSelectView(
        itemView: View,
        index: Int,
        select: Boolean,
        fromUser: Boolean
    ): Boolean {
        return false
    }

    /**选中后的view改变的回调*/
    fun onCheckSelectViewChange(
        fromView: View?,
        selectViewList: List<View>,
        reselect: Boolean,
        fromUser: Boolean
    ) {

    }

    /**选中后的index改变的回调*/
    fun onCheckSelectIndexChange(
        fromIndex: Int,
        selectIndexList: List<Int>,
        reselect: Boolean,
        fromUser: Boolean
    ) {
        checkGroupItemConfig._itemCheckedIndexList.clear()
        checkGroupItemConfig._itemCheckedIndexList.addAll(selectIndexList)

        //清空之前
        checkGroupItemConfig.itemCheckedItems.clear()

        //当前选中项
        selectIndexList.forEach {
            checkGroupItemConfig.itemCheckedItems.add(checkGroupItemConfig.itemCheckItems[it])
        }

        //回调
        checkGroupItemConfig.itemCheckChangedAction(fromIndex, selectIndexList, reselect, fromUser)

        //更新依赖
        if (fromUser) {
            if (this is DslAdapterItem) {
                itemChanging = true
            }
        }
    }

    /**Dsl*/
    fun configCheckGroupItem(block: CheckGroupItemConfig.() -> Unit) {
        checkGroupItemConfig.block()
    }
}

/**需要选择的项*/
var ICheckGroupItem.itemCheckItems: List<Any>
    get() = checkGroupItemConfig.itemCheckItems
    set(value) {
        checkGroupItemConfig.itemCheckItems = value
    }

/**选中的项*/
var ICheckGroupItem.itemCheckedItems: MutableList<Any>
    get() = checkGroupItemConfig.itemCheckedItems
    set(value) {
        checkGroupItemConfig.itemCheckedItems = value
    }

/**只读属性, 选中的索引值*/
val ICheckGroupItem._itemCheckedIndexList: List<Int>
    get() = checkGroupItemConfig._itemCheckedIndexList

/**布局id*/
var ICheckGroupItem.itemCheckLayoutId: Int
    get() = checkGroupItemConfig.itemCheckLayoutId
    set(value) {
        checkGroupItemConfig.itemCheckLayoutId = value
    }

/**回调*/
var ICheckGroupItem.itemCheckChangedAction: (fromIndex: Int, selectIndexList: List<Int>, reselect: Boolean, fromUser: Boolean) -> Unit
    get() = checkGroupItemConfig.itemCheckChangedAction
    set(value) {
        checkGroupItemConfig.itemCheckChangedAction = value
    }

class CheckGroupItemConfig : IDslItemConfig {

    /**需要操作的控件id[R.id.lib_flow_layout]*/
    var itemCheckGroupViewId: Int = R.id.lib_flow_layout

    /**选项列表*/
    var itemCheckItems = listOf<Any>()

    /**选中的列表*/
    var itemCheckedItems = mutableListOf<Any>()

    /**选项布局*/
    var itemCheckLayoutId: Int = R.layout.layout_check

    /**是否是多选模式*/
    var itemMultiMode = false

    /**多选时, 最小选中数量*/
    var itemMinSelectLimit = 0

    /**将选项[item], 转成可以显示在界面的 文本类型*/
    var itemCheckItemToText: (item: Any) -> CharSequence? = { item ->
        if (item is IToText) {
            item.toText()
        } else {
            item.string()
        }
    }

    /**将选项[item], 转成表单上传的数据*/
    var itemCheckItemToValue: (item: Any) -> Any? = { item ->
        if (item is IToValue) {
            item.toValue()
        } else {
            item.toStr()
        }
    }

    var itemFirstNotifyChange = true

    /**回调*/
    var itemCheckChangedAction: (fromIndex: Int, selectIndexList: List<Int>, reselect: Boolean, fromUser: Boolean) -> Unit =
        { fromIndex, selectIndexList, reselect, fromUser ->

        }

    /**单选/多选支持*/
    val itemSelectorHelper = DslSelector()

    //内部使用
    var _itemCheckedIndexList = mutableListOf<Int>()
}