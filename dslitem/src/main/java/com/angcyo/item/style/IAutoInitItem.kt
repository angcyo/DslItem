package com.angcyo.item.style

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.annotation.ItemInitEntryPoint
import com.angcyo.dsladapter.item.IDslItem

/**
 * 自动初始化, 继承此类的item, 可以实现自动初始化
 * [com.angcyo.dsladapter.DslAdapterItem._initItemConfig]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/09/23
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
interface IAutoInitItem : IDslItem {

    /**自动初始化入口, 统一入口*/
    @ItemInitEntryPoint
    override fun initItemConfig(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.initItemConfig(itemHolder, itemPosition, adapterItem, payloads)

        //分发具体的初始化方法
        if (this is IBadgeItem) {
            initBadgeItem(itemHolder, itemPosition, adapterItem, payloads)
        }
        if (this is IDesItem) {
            initDesItem(itemHolder, itemPosition, adapterItem, payloads)
        }
        if (this is ITextItem) {
            initTextItem(itemHolder, itemPosition, adapterItem, payloads)
        }
        if (this is IEditItem) {
            initEditItem(itemHolder, itemPosition, adapterItem, payloads)
        }
        if (this is ILabelItem) {
            initLabelItem(itemHolder, itemPosition, adapterItem, payloads)
        }
        if (this is ITextInfoItem) {
            initInfoTextItem(itemHolder, itemPosition, adapterItem, payloads)
        }
        if (this is IBodyItem) {
            initBodyItem(itemHolder, itemPosition, adapterItem, payloads)
        }
        if (this is IButtonItem) {
            initButtonItem(itemHolder, itemPosition, adapterItem, payloads)
        }
        if (this is INestedRecyclerItem) {
            initNestedRecyclerItem(itemHolder, itemPosition, adapterItem, payloads)
        }
        if (this is ICheckItem) {
            initCheckItem(itemHolder, itemPosition, adapterItem, payloads)
        }
        if (this is ISwitchItem) {
            initSwitchItem(itemHolder, itemPosition, adapterItem, payloads)
        }
        if (this is ICheckGroupItem) {
            initCheckGroupItem(itemHolder, itemPosition, adapterItem, payloads)
        }
    }
}