package com.angcyo.dslitem.demo.dslitem

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dslitem.demo.R
import com.bumptech.glide.Glide

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2023/04/28
 */
class SingleImageItem : DslAdapterItem() {

    var itemImageUri: String? = null

    init {
        itemLayoutId = R.layout.item_single_image
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.img(R.id.lib_image_view)?.let {
            Glide.with(itemHolder.itemView).load(itemImageUri).centerCrop().into(it)
        }
    }

}