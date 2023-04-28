package com.angcyo.item2

import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.item.DslNestedRecyclerItem
import com.angcyo.item.base.LibInitProvider
import com.angcyo.item2.widget.recycler.DrawableIndicator
import com.leochuan.ScaleLayoutManager
import com.leochuan.ViewPagerLayoutManager

/**
 * 轮播图切换item
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/18
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslBannerItem : DslNestedRecyclerItem() {

    init {
        itemLayoutId = R.layout.dsl_banner_item

        nestedRecyclerItemConfig.itemNestedLayoutManager =
            ScaleLayoutManager(LibInitProvider.contentProvider, 0).apply {
                recycleChildrenOnDetach = true
                isFullItem = true //全屏item
                maxVisibleItemCount = 3 //最大可见item数量
                infinite = true //无限滚动
                itemSpace = 0 //item之间的间隙
                minScale = 1f
                maxScale = 1f
                maxAlpha = 1f
                minAlpha = 1f
            }

        itemHeight = LinearLayout.LayoutParams.WRAP_CONTENT
    }

    val pagerLayoutManager: ViewPagerLayoutManager?
        get() = nestedRecyclerItemConfig.itemNestedLayoutManager as? ViewPagerLayoutManager

    override fun onBindNestedRecyclerView(
        recyclerView: RecyclerView,
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onBindNestedRecyclerView(
            recyclerView, itemHolder, itemPosition, adapterItem, payloads
        )

        val drawableIndicator: DrawableIndicator? = itemHolder.v(R.id.lib_drawable_indicator)

        //page切换监听
        pagerLayoutManager?.setOnPageChangeListener(object :
            ViewPagerLayoutManager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                //L.v(state)
            }

            override fun onPageSelected(position: Int) {
                //L.v(position), 相当页面滑动也会通知.
                nestedRecyclerItemConfig._scrollPositionConfig?.adapterPosition = position
                drawableIndicator?.animatorToIndex(position)
            }
        })

        //列表
        recyclerView.apply {
            drawableIndicator?.indicatorCount = nestedRecyclerItemConfig.itemNestedAdapter.itemCount

            nestedRecyclerItemConfig.itemNestedAdapter.onDispatchUpdatesOnce {
                drawableIndicator?.indicatorCount = it.itemCount
            }

            if (nestedRecyclerItemConfig.itemKeepScrollPosition) {
                nestedRecyclerItemConfig._scrollPositionConfig?.run {
                    scrollToPosition(adapterPosition)
                }
            }
        }
    }
}