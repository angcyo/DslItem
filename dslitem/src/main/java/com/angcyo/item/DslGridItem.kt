package com.angcyo.item

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import com.angcyo.dsladapter.*

/**
 * 普通的网格item
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/22
 */
open class DslGridItem : DslAdapterItem() {

    /**显示的文本*/
    var itemText: CharSequence? = null

    /**图标*/
    var itemIcon: Int = -1

    /**图标背景*/
    var itemImageBg: Drawable? = null
    var itemImagePadding: Int = 12 * dpi
    var itemTextPadding: Int = 8 * dpi

    /**图标是1:1的大小*/
    var itemIconSize: Int = ViewGroup.LayoutParams.WRAP_CONTENT

    /**开启智能分割线, 只有在非边界的item才绘制*/
    var itemGridInsert = -1

    init {
        itemLayoutId = R.layout.dsl_grid_item
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        //文本
        itemHolder.tv(R.id.lib_text_view)?.apply {
            text = itemText
            padding(itemTextPadding)
        }

        //图标
        itemHolder.img(R.id.lib_image_view)?.apply {
            padding(itemImagePadding)

            setImageDrawable(loadDrawable(itemIcon))
            setWidth(itemIconSize)

            setBgDrawable(itemImageBg)
        }

        //智能分割线
        if (itemGridInsert > 0) {
            itemGroupParams.apply {
                //itemLeftInsert = itemGridInsert
                //itemTopInsert = itemGridInsert
                itemRightInsert = itemGridInsert
                itemBottomInsert = itemGridInsert
                if (isEdgeLeft()) {
                    itemLeftInsert = 0
                }
                if (isEdgeTop()) {
                    itemTopInsert = 0
                }
                if (isEdgeRight()) {
                    itemRightInsert = 0
                }
                if (isEdgeBottom()) {
                    itemBottomInsert = 0
                }
            }
        }
    }
}