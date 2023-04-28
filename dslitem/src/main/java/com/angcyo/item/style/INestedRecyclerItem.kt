package com.angcyo.item.style

import androidx.recyclerview.widget.RecyclerView
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.annotation.ItemInitEntryPoint
import com.angcyo.dsladapter.item.IDslItemConfig
import com.angcyo.dsladapter.item.IFragmentItem
import com.angcyo.item.R
import com.angcyo.item.base.LibInitProvider
import com.angcyo.widget.recycler.*
import java.lang.ref.WeakReference

/**
 * 内嵌一个RecyclerView的Item
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/09/26
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
interface INestedRecyclerItem : IAutoInitItem {

    var nestedRecyclerItemConfig: NestedRecyclerItemConfig

    /**初始化[INestedRecyclerItem]*/
    @ItemInitEntryPoint
    fun initNestedRecyclerItem(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        itemHolder.rv(nestedRecyclerItemConfig.itemNestedRecyclerViewId)?.apply {
            onBindNestedRecyclerView(this, itemHolder, itemPosition, adapterItem, payloads)
        }
    }

    /**回收*/
    fun onNestedRecyclerViewRecycled(itemHolder: DslViewHolder, itemPosition: Int) {
        itemHolder.rv(nestedRecyclerItemConfig.itemNestedRecyclerViewId)?.apply {
            layoutManager = null
            adapter = null
        }
    }

    /**绑定[RecyclerView]*/
    fun onBindNestedRecyclerView(
        recyclerView: RecyclerView,
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        //列表
        recyclerView.apply {
            //优先清空[OnScrollListener]
            clearOnScrollListeners()
            clearItemDecoration()
            initDsl()

            if (layoutManager != nestedRecyclerItemConfig.itemNestedLayoutManager) {
                layoutManager = nestedRecyclerItemConfig.itemNestedLayoutManager
            }

            //关键地方, 如果每次都赋值[adapter], 系统会重置所有缓存.
            if (adapter != nestedRecyclerItemConfig.itemNestedAdapter) {
                adapter = nestedRecyclerItemConfig.itemNestedAdapter
            }

            //配置
            nestedRecyclerItemConfig.itemNestedRecyclerViewConfig(this)

            //渲染数据
            if (adapter is DslAdapter) {
                val dslAdapter = adapter as DslAdapter
                dslAdapter._recyclerView = this
                onRenderNestedAdapter(dslAdapter)
            }

            //恢复滚动位置
            if (nestedRecyclerItemConfig.itemKeepScrollPosition) {
                nestedRecyclerItemConfig._scrollPositionConfig?.run { restoreScrollPosition(this) }
            }

            //记录滚动位置
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    nestedRecyclerItemConfig._scrollPositionConfig = saveScrollPosition()
                }
            }.apply {
                nestedRecyclerItemConfig._onScrollListener = this
                addOnScrollListener(this)
            }
        }
    }

    /**开始界面渲染*/
    fun onRenderNestedAdapter(dslAdapter: DslAdapter) {
        val item = this
        dslAdapter.apply {
            nestedRecyclerItemConfig.itemNestedItemList?.let {
                clearItems()
                if (item is DslAdapterItem) {
                    it.forEach {
                        if (it.itemParentRef?.get() == item) {
                            //一样的对象
                        } else {
                            it.itemParentRef = WeakReference(item)
                        }
                    }
                }
                dataItems.addAll(it)
                _updateAdapterItems()
            }

            nestedRecyclerItemConfig.itemRenderNestedAdapter(this)

            if (this@INestedRecyclerItem is IFragmentItem) {
                adapterItems.forEach {
                    if (it is IFragmentItem && it.itemFragment == null) {
                        it.itemFragment = this@INestedRecyclerItem.itemFragment
                    }
                }
            }

            /*if (item is DslAdapterItem && item.itemParentRef?.get() == null) {
                updateNow()
            } else {
                notifyDataChanged()
            }*/
            //强刷界面
            notifyDataChanged()
        }
    }
}

var INestedRecyclerItem.itemNestedAdapter
    get() = nestedRecyclerItemConfig.itemNestedAdapter
    set(value) {
        nestedRecyclerItemConfig.itemNestedAdapter = value
    }

var INestedRecyclerItem.itemNestedLayoutManager
    get() = nestedRecyclerItemConfig.itemNestedLayoutManager
    set(value) {
        nestedRecyclerItemConfig.itemNestedLayoutManager = value
    }

fun INestedRecyclerItem.renderNestedAdapter(init: DslAdapter.() -> Unit) {
    nestedRecyclerItemConfig.itemRenderNestedAdapter = init
}

fun <T : DslAdapterItem> INestedRecyclerItem.addNestedItem(item: T, init: T.() -> Unit = {}) {
    if (nestedRecyclerItemConfig.itemNestedItemList == null) {
        nestedRecyclerItemConfig.itemNestedItemList = mutableListOf()
    }
    nestedRecyclerItemConfig.itemNestedItemList?.add(item)
    item.init()
}

class NestedRecyclerItemConfig : IDslItemConfig {

    var itemNestedRecyclerViewId: Int = R.id.lib_nested_recycler_view

    /**内嵌适配器*/
    var itemNestedAdapter: DslAdapter = DslAdapter().apply {
        //关闭内部情感图状态
        dslAdapterStatusItem.itemEnable = false
    }

    /**布局管理,
     * 请注意使用属性:[recycleChildrenOnDetach]*/
    var itemNestedLayoutManager: RecyclerView.LayoutManager =
        LinearLayoutManagerWrap(LibInitProvider.contentProvider).apply {
            recycleChildrenOnDetach = true
        }

    /**自动恢复滚动位置*/
    var itemKeepScrollPosition = true

    /**渲染内部[DslAdapter]数据*/
    var itemRenderNestedAdapter: DslAdapter.() -> Unit = {}

    /**简单的item数据集合, 如果使用了此属性, 请勿在[itemRenderNestedAdapter]重复渲染*/
    var itemNestedItemList: MutableList<DslAdapterItem>? = null

    /**内部[RecyclerView]配置回调*/
    var itemNestedRecyclerViewConfig: RecyclerView.() -> Unit = {
        noItemAnim()
    }

    var _onScrollListener: RecyclerView.OnScrollListener? = null

    var _scrollPositionConfig: ScrollPositionConfig? = null
}