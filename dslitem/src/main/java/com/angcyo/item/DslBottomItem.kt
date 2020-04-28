package com.angcyo.item

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder

/**
 * 在[RecyclerView]底部显示的文本item
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/29
 */
open class DslBottomItem : DslAdapterItem() {

    /**显示的文本*/
    var itemText: CharSequence? = null

    init {
        itemLayoutId = R.layout.dsl_bottom_item
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.tv(R.id.lib_text_view)?.text = itemText
    }

    override fun _initItemSize(itemHolder: DslViewHolder) {
        //super._initItemSize(itemHolder)
    }
}