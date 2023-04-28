package com.angcyo.item.style

import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import com.angcyo.dsladapter.*
import com.angcyo.dsladapter.annotation.ItemInitEntryPoint
import com.angcyo.dsladapter.item.IDslItemConfig
import com.angcyo.item.R
import com.angcyo.dsladapter.DslViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/02/25
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
interface ICheckItem : IAutoInitItem {

    /**配置项*/
    var checkItemConfig: CheckItemConfig

    @ItemInitEntryPoint
    fun initCheckItem(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        val item = this
        itemHolder.view(checkItemConfig.itemCheckViewId)?.apply {
            when (this) {
                is ImageView -> setImageResource(checkItemConfig.itemCheckResId)
                is CompoundButton -> setButtonDrawable(checkItemConfig.itemCheckResId)
                else -> setBackgroundResource(checkItemConfig.itemCheckResId)
            }
        }

        //[DslAdapterItem]
        if (item is DslAdapterItem) {
            item.itemDslAdapter?.apply {
                if (itemSelectorHelper.selectorModel == ItemSelectorHelper.MODEL_NORMAL) {
                    //开启多选模式
                    multiModel()
                }
            }
            item.itemClick = this::onItemClick
            item.onItemSelectorChange = this::onItemSelectorChange

            //重新初始化一下Listener
            item._initItemListener(itemHolder)

            //状态
            itemHolder.selected(checkItemConfig.itemCheckViewId, item.itemIsSelected)
        }
    }

    /**点击事件的绑定*/
    fun onItemClick(view: View) {
        val item = this
        if (item is DslAdapterItem) {
            //切换选中状态
            item.select {
                //不通知界面更新, 因为如果使用此方式更新界面, item原本的背景波纹效果会丢失
                notifyItemChanged = false
            }
        }
    }

    /**选中状态改变后, 通过本地更新的方式更新界面*/
    fun onItemSelectorChange(selectorParams: SelectorParams) {
        val item = this
        if (item is DslAdapterItem) {
            item.itemViewHolder()?.apply {
                selected(checkItemConfig.itemCheckViewId, item.itemIsSelected)
            }
        }
    }
}

class CheckItemConfig : IDslItemConfig {

    /**需要操作的控件id[R.id.lib_check_view]*/
    var itemCheckViewId: Int = R.id.lib_check_view

    /**选中状态提示资源*/
    var itemCheckResId: Int = R.drawable.lib_check_selector

}