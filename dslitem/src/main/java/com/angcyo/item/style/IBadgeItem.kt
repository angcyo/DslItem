package com.angcyo.item.style

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.annotation.ItemInitEntryPoint
import com.angcyo.dsladapter.item.IDslItemConfig
import com.angcyo.item.R
import com.angcyo.widget.IBadgeView

/**
 * 角标文本[BadgeTextView]item
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/09/23
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
interface IBadgeItem : IAutoInitItem {

    var badgeItemConfig: BadgeItemConfig

    /**初始化*/
    @ItemInitEntryPoint
    fun initBadgeItem(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        itemHolder.view(badgeItemConfig.itemBadgeViewId)?.apply {
            if (this is IBadgeView) {
                dslBadeDrawable.apply {
                    drawBadge = true
                    badgeText = badgeItemConfig.itemBadgeText
                    requestLayout()
                }
            }
        }
    }
}

var IBadgeItem.itemBadgeText: String?
    get() = badgeItemConfig.itemBadgeText
    set(value) {
        badgeItemConfig.itemBadgeText = value
    }


class BadgeItemConfig : IDslItemConfig {

    var itemBadgeViewId: Int = R.id.lib_badge_view

    /**[com.angcyo.drawable.text.DslBadgeDrawable.badgeText]*/
    var itemBadgeText: String? = null
}