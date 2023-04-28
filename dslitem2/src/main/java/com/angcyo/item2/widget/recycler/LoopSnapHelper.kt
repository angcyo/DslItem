package com.angcyo.github.widget.recycler

import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.github.dslitem.ILoopAdapterItem
import com.leochuan.AutoPlaySnapHelper
import com.leochuan.ViewPagerLayoutManager

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/18
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class LoopSnapHelper(val interval: Int, direction: Int) :
    AutoPlaySnapHelper(interval, direction) {

    var loopInterpolator: Interpolator? = DecelerateInterpolator()

    var loopDuration: Int = 1000//UNDEFINED_DURATION

    /**替换默认的时间间隔*/
    fun hookInterval() {
        if (mRecyclerView.adapter is DslAdapter && mRecyclerView.layoutManager is ViewPagerLayoutManager) {
            val layoutManager: ViewPagerLayoutManager =
                mRecyclerView.layoutManager as ViewPagerLayoutManager

            val currentPosition =
                layoutManager.currentPosition * if (layoutManager.reverseLayout) -1 else 1

            (mRecyclerView.adapter as DslAdapter).getItemData(currentPosition, true)?.apply {
                if (this is ILoopAdapterItem) {
                    //替换默认的时间间隔
                    setTimeInterval(this.getLoopInterval())
                }
            }
        }
    }

    /**恢复默认的时间间隔*/
    fun restoreInterval() {
        //恢复默认的时间间隔
        setTimeInterval(interval)
    }

    override fun onRun(layoutManager: ViewPagerLayoutManager) {
        if (mRecyclerView?.adapter?.itemCount ?: 0 <= 0) {
            return
        }
        hookInterval()

        val currentPosition =
            layoutManager.currentPositionOffset * if (layoutManager.reverseLayout) -1 else 1

        val targetPosition = if (direction == RIGHT) currentPosition + 1 else currentPosition - 1

        val delta: Int = layoutManager.getOffsetToPosition(targetPosition)
        if (layoutManager.orientation == RecyclerView.VERTICAL) {
            mRecyclerView?.smoothScrollBy(0, delta, loopInterpolator, loopDuration)
        } else {
            mRecyclerView?.smoothScrollBy(delta, 0, loopInterpolator, loopDuration)
        }

        handler.postDelayed(autoPlayRunnable, timeInterval.toLong())

        restoreInterval()
    }

    override fun start() {
        if (mRecyclerView == null) {
            return
        }

        if (mRecyclerView.layoutManager == null) {
            return
        }

        if (mRecyclerView.layoutManager !is ViewPagerLayoutManager) {
            return
        }
        hookInterval()
        super.start()
        restoreInterval()
    }

    fun detachFromRecyclerView() {
        if (mRecyclerView != null) {
            destroyCallbacks()
        }
        mRecyclerView = null
    }
}