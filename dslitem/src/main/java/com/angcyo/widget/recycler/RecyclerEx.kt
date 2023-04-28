package com.angcyo.widget.recycler

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.*
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslItemDecoration
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.HoverItemDecoration
import com.angcyo.dsladapter.dslSpanSizeLookup
import com.angcyo.dsladapter.payload
import com.angcyo.item.getCurrVelocity
import com.angcyo.item.getMember

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/26
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

//<editor-fold desc="DslAdapter相关">

/**清空原有的[ItemDecoration]*/
fun RecyclerView.clearItemDecoration() {
    for (i in itemDecorationCount - 1 downTo 0) {
        removeItemDecorationAt(i)
    }
}

/**判断[RecyclerView]是否已经存在指定的[ItemDecoration]*/
fun RecyclerView.haveItemDecoration(predicate: (ItemDecoration) -> Boolean): Boolean {
    var result = false
    for (i in 0 until itemDecorationCount) {
        val itemDecoration = getItemDecorationAt(i)
        if (predicate(itemDecoration)) {
            result = true
            break
        }
    }
    return result
}

/**移除指定的[ItemDecoration]*/
fun RecyclerView.removeItemDecoration(predicate: (ItemDecoration) -> Boolean): Boolean {
    var result = false
    for (i in itemDecorationCount - 1 downTo 0) {
        val itemDecoration = getItemDecorationAt(i)
        if (predicate(itemDecoration)) {
            removeItemDecorationAt(i)
            result = true
        }
    }
    return result
}

/**[DslAdapter]必备的组件*/
fun RecyclerView.initDsl() {
    var haveItemDecoration = false
    var haveHoverItemDecoration = false
    for (i in 0 until itemDecorationCount) {
        val itemDecoration = getItemDecorationAt(i)
        if (itemDecoration is DslItemDecoration) {
            haveItemDecoration = true
        } else if (itemDecoration is HoverItemDecoration) {
            haveHoverItemDecoration = true
        }
    }
    if (!haveItemDecoration) {
        DslItemDecoration().attachToRecyclerView(this)
    }
    if (!haveHoverItemDecoration) {
        HoverItemDecoration().attachToRecyclerView(this)
    }
}

/**
 * [initDslAdapter]
 * dsl在[com.angcyo.dsladapter.DslAdapter.render]方法中执行
 * */
fun RecyclerView.renderDslAdapter(
    append: Boolean = false, //当已经是adapter时, 是否追加item. 需要先关闭 new
    new: Boolean = true, //始终创建新的adapter, 为true时, 则append无效
    updateState: Boolean = true,
    action: DslAdapter.() -> Unit = {}
) {
    initDslAdapter(append, new) {
        render(updateState) {
            action()
        }
    }
}

/**快速初始化[DslAdapter]
 * [initDsl]
 * [dslAdapter]*/
fun RecyclerView.initDslAdapter(
    append: Boolean = false, //当已经是adapter时, 是否追加item. 需要先关闭 new
    new: Boolean = true, //始终创建新的adapter, 为true时, 则append无效
    action: DslAdapter.() -> Unit = {}
): DslAdapter {
    initDsl()
    if (layoutManager == null) {
        resetLayoutManager("v")
    }
    return dslAdapter(append, new, action)
}

fun RecyclerView.dslAdapter(
    append: Boolean = false, //当已经是adapter时, 是否追加item. 需要先关闭 new
    new: Boolean = true, //始终创建新的adapter, 为true时, 则append无效
    init: DslAdapter.() -> Unit
): DslAdapter {

    var dslAdapter: DslAdapter? = null

    fun newAdapter() {
        dslAdapter = DslAdapter()
        adapter = dslAdapter

        dslAdapter!!.init()
    }

    if (new) {
        newAdapter()
    } else {
        if (adapter is DslAdapter) {
            dslAdapter = adapter as DslAdapter

            if (!append) {
                dslAdapter!!.clearItems()
            }

            dslAdapter!!.init()
        } else {
            newAdapter()
        }
    }

    return dslAdapter!!
}

//</editor-fold desc="DslAdapter相关">

//<editor-fold desc="基础">

/** 通过[V] [H] [GV2] [GH3] [SV2] [SV3] 方式, 设置 [LayoutManager] */
fun RecyclerView.resetLayoutManager(match: String) {
    val oldLayoutManager = layoutManager
    var layoutManager: LayoutManager? = null
    var spanCount = 1
    var orientation = VERTICAL

    if (TextUtils.isEmpty(match) || "V".equals(match, ignoreCase = true)) {
        if (oldLayoutManager is LinearLayoutManagerWrap) {
            if (oldLayoutManager.orientation != LinearLayoutManager.VERTICAL) {
                layoutManager =
                    LinearLayoutManagerWrap(context, LinearLayoutManager.VERTICAL, false)
            }
        } else {
            layoutManager = LinearLayoutManagerWrap(context, LinearLayoutManager.VERTICAL, false)
        }
    } else {
        //线性布局管理器
        if ("H".equals(match, ignoreCase = true)) {
            if (oldLayoutManager is LinearLayoutManagerWrap) {
                if (oldLayoutManager.orientation != LinearLayoutManager.HORIZONTAL) {
                    layoutManager =
                        LinearLayoutManagerWrap(context, LinearLayoutManager.HORIZONTAL, false)
                }
            } else {
                layoutManager =
                    LinearLayoutManagerWrap(context, LinearLayoutManager.HORIZONTAL, false)
            }
        } else { //读取其他配置信息(数量和方向)
            val type = match.substring(0, 1)
            if (match.length >= 3) {
                try {
                    spanCount = Integer.valueOf(match.substring(2)) //数量
                } catch (e: Exception) {
                }
            }
            if (match.length >= 2) {
                if ("H".equals(match.substring(1, 2), ignoreCase = true)) {
                    orientation = StaggeredGridLayoutManager.HORIZONTAL //方向
                }
            }
            //交错布局管理器
            if ("S".equals(type, ignoreCase = true)) {
                if (oldLayoutManager is StaggeredGridLayoutManagerWrap) {
                    if (oldLayoutManager.spanCount != spanCount || oldLayoutManager.orientation != orientation) {
                        layoutManager =
                            StaggeredGridLayoutManagerWrap(
                                spanCount,
                                orientation
                            )
                    }
                } else {
                    layoutManager =
                        StaggeredGridLayoutManagerWrap(
                            spanCount,
                            orientation
                        )
                }
            } else if ("G".equals(type, ignoreCase = true)) {
                if (oldLayoutManager is GridLayoutManagerWrap) {
                    if (oldLayoutManager.spanCount != spanCount || oldLayoutManager.orientation != orientation) {
                        layoutManager =
                            GridLayoutManagerWrap(
                                context,
                                spanCount,
                                orientation,
                                false
                            )
                    }
                } else {
                    layoutManager =
                        GridLayoutManagerWrap(
                            context,
                            spanCount,
                            orientation,
                            false
                        )
                }
            }
        }
    }

    if (layoutManager is GridLayoutManager) {
        val gridLayoutManager = layoutManager
        gridLayoutManager.dslSpanSizeLookup(this)
    } else if (layoutManager is LinearLayoutManager) {
        layoutManager.recycleChildrenOnDetach = true
    }

    if (layoutManager != null) {
        this.layoutManager = layoutManager
    }
}

/**
 * 取消RecyclerView的默认动画
 * */
fun RecyclerView.noItemAnim(animator: ItemAnimator? = null) {
    itemAnimator = animator
}

/**
 * 取消默认的change动画
 * */
fun RecyclerView.noItemChangeAnim(no: Boolean = true) {
    if (itemAnimator == null) {
        itemAnimator = DefaultItemAnimator().apply {
            supportsChangeAnimations = !no
        }
    } else if (itemAnimator is SimpleItemAnimator) {
        (itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            !no
    }
}

/**
 * 设置RecyclerView的默认动画
 * */
fun RecyclerView.setItemAnim(animator: ItemAnimator? = DefaultItemAnimator()) {
    itemAnimator = animator
}

/**第一个item是否完全可见*/
fun RecyclerView.isFirstItemVisibleCompleted(): Boolean {
    val linearLayoutManager = layoutManager as? LinearLayoutManager? ?: return false
    val firstPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
    return firstPosition == 0
}

/**最后一个item是否完全可见*/
fun RecyclerView.isLastItemVisibleCompleted(): Boolean {
    val linearLayoutManager = layoutManager as? LinearLayoutManager? ?: return false
    val lastPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
    val childCount = linearLayoutManager.childCount
    val firstPosition = linearLayoutManager.findFirstVisibleItemPosition()
    return lastPosition >= firstPosition + childCount - 1
}

/**列表中所有的item, 均已显示*/
fun RecyclerView.isAllItemVisibleCompleted(): Boolean {
    val linearLayoutManager = layoutManager as? LinearLayoutManager? ?: return false
    val firstPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
    val lastPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()

    if (firstPosition == 0 && lastPosition - firstPosition <= adapter?.itemCount ?: 0) {
        return true
    }
    return false
}

/**在列表可滚动的情况下, 顶部item已经全部显示*/
fun RecyclerView.isTopItemVisibleCompleted(): Boolean {
    val linearLayoutManager = layoutManager as? LinearLayoutManager? ?: return false
    val firstPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
    val lastPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()

    if (firstPosition == 0 && lastPosition + 1 < adapter?.itemCount ?: 0) {
        return true
    }
    return false
}

//</editor-fold desc="基础">

//<editor-fold desc="ViewHolder相关">

/**
 * 获取[RecyclerView]指定位置[index]的[DslViewHolder], 负数表示倒数开始的index
 * [isLayoutIndex] 界面上存在, 类似 [LayoutPosition] [AdapterPosition] 的区别
 * */
operator fun RecyclerView.get(index: Int, isLayoutIndex: Boolean = false): DslViewHolder? {

    var result: DslViewHolder?

    if (isLayoutIndex) {
        val layoutIndex = if (index >= 0) {
            //正向取child
            index
        } else {
            //反向取child
            childCount + index
        }

        result = findViewHolderForLayoutPosition(layoutIndex) as? DslViewHolder
        if (result == null) {
            val child: View? = getChildAt(layoutIndex)
            result = child?.run { getChildViewHolder(this) as? DslViewHolder }
        }
    } else {
        val adapterIndex = if (index >= 0) {
            //正向取child
            index
        } else {
            //反向取child
            (adapter?.itemCount ?: 0) + index
        }

        result = findViewHolderForAdapterPosition(adapterIndex) as? DslViewHolder
    }

    return result
}

/**获取[RecyclerView]界面上存在的所有[DslViewHolder]*/
fun RecyclerView.allViewHolder(): List<DslViewHolder> {
    val result = mutableListOf<DslViewHolder>()
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        (getChildViewHolder(child) as? DslViewHolder)?.run { result.add(this) }
    }
    return result
}

/**本地更新[RecyclerView]界面,
 * [position] 指定需要更新的位置, 负数表示全部*/
fun RecyclerView.localUpdateItem(position: Int = -1, payloads: List<Any> = payload()) {
    if (adapter !is DslAdapter) {
        return
    }
    val allViewHolder = allViewHolder()
    if (position >= allViewHolder.size) {
        return
    }
    allViewHolder.forEach { viewHolder ->
        val adapterPosition = viewHolder.adapterPosition
        val adapterItem = (adapter as DslAdapter).getItemData(adapterPosition)
        adapterItem?.run {
            if (position >= 0 && position == adapterPosition) {
                //只更新指定的位置
                itemBind(viewHolder, adapterPosition, adapterItem, payloads)
                return
            } else {
                itemBind(viewHolder, adapterPosition, adapterItem, payloads)
            }
        }
    }
}

fun RecyclerView.localUpdateItem(action: (adapterItem: DslAdapterItem, itemHolder: DslViewHolder, itemPosition: Int) -> Unit) {
    if (adapter !is DslAdapter) {
        return
    }
    allViewHolder().forEach { viewHolder ->
        val adapterPosition = viewHolder.adapterPosition
        val adapterItem = (adapter as DslAdapter).getItemData(adapterPosition)
        adapterItem?.run {
            if (adapterPosition >= 0) {
                action(adapterItem, viewHolder, adapterPosition)
            }
        }
    }
}
//</editor-fold desc="ViewHolder相关">

//<editor-fold desc="滚动相关">

/**
 * 获取[RecyclerView] [Fling] 时的速率
 * */
fun RecyclerView?.getLastVelocity(): Float {
    var currVelocity = 0f
    try {
        val mViewFlinger = this.getMember(RecyclerView::class.java, "mViewFlinger")
        var mScroller = mViewFlinger.getMember("mScroller")
        if (mScroller == null) {
            mScroller = mViewFlinger.getMember("mOverScroller")
        }
        currVelocity = mScroller.getCurrVelocity()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return currVelocity
}

fun Int.scrollStateStr(): String {
    return when (this) {
        SCROLL_STATE_SETTLING -> "SCROLL_STATE_SETTLING"
        SCROLL_STATE_DRAGGING -> "SCROLL_STATE_DRAGGING"
        SCROLL_STATE_IDLE -> "SCROLL_STATE_IDLE"
        else -> "Unknown"
    }
}

fun RecyclerView.scrollHelper(action: ScrollHelper.() -> Unit = {}): ScrollHelper {
    return ScrollHelper().apply {
        attach(this@scrollHelper)
        action()
    }
}

/**滚动到尾部*/
fun RecyclerView.scrollToEnd(smooth: Boolean = false) {
    val count = adapter?.itemCount ?: 0
    val position = count - 1
    if (position in 0 until count) {
        if (smooth) {
            smoothScrollToPosition(position)
        } else {
            scrollToPosition(position)
        }
    }
}

/**滚动到顶部*/
fun RecyclerView.scrollToFirst(smooth: Boolean = false) {
    val count = adapter?.itemCount ?: 0
    if (count > 0) {
        val position = 0
        if (smooth) {
            smoothScrollToPosition(position)
        } else {
            scrollToPosition(position)
        }
    }
}

/**保存当前的滚动位置*/
fun RecyclerView.saveScrollPosition(): ScrollPositionConfig {
    val result = ScrollPositionConfig()

    if (childCount > 0) {
        val childAt = getChildAt(0)
        val layoutParams = childAt.layoutParams as LayoutParams

        result.adapterPosition = layoutParams.viewAdapterPosition

        result.left = layoutManager?.getDecoratedLeft(childAt) ?: 0
        result.top = layoutManager?.getDecoratedTop(childAt) ?: 0
    }

    return result
}

/**恢复滚动位置*/
fun RecyclerView.restoreScrollPosition(config: ScrollPositionConfig) {
    if (config.adapterPosition >= 0) {
        val lm = layoutManager
        when (lm) {
            is LinearLayoutManager -> lm.scrollToPositionWithOffset(
                config.adapterPosition,
                if (lm.orientation == HORIZONTAL) config.left else config.top
            )

            is StaggeredGridLayoutManager -> lm.scrollToPositionWithOffset(
                config.adapterPosition,
                if (lm.orientation == HORIZONTAL) config.left else config.top
            )

            else -> scrollToPosition(config.adapterPosition)
        }
    }
}

/**监听滚动状态改变*/
fun RecyclerView.onScrollStateChangedAction(action: (recyclerView: RecyclerView, newState: Int) -> Unit): OnScrollListener {
    val listener = object : OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            action(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }
    addOnScrollListener(listener)
    return listener
}

/**监听滚动改变*/
fun RecyclerView.onScrolledAction(action: (recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit): OnScrollListener {
    val listener = object : OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            action(recyclerView, dx, dy)
        }
    }
    addOnScrollListener(listener)
    return listener
}

//</editor-fold desc="滚动相关">