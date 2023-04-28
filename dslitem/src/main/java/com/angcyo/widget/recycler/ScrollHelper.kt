package com.angcyo.widget.recycler

import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.angcyo.dpi
import com.angcyo.dsladapter.L
import com.angcyo.dsladapter.mH
import com.angcyo.dsladapter.nowTime
import com.angcyo.widget.recycler.ScrollHelper.Companion.DEFAULT_SCROLL_STEP
import kotlin.math.absoluteValue
import kotlin.math.min

/**
 * 滚动机制:
 * 1. 不带动画滚动至位置可见 scrollToPosition
 * 2. 带动画滚动至位置可见 smoothScrollToPosition
 *
 * 上述2个方法, 均无法准确控制position, 只要position出现在界面即完成滚动.
 *
 * 1. 不带动画精确滚动 scrollBy
 * 2. 带动画精确滚动 smoothScrollBy
 *
 * 上述2个方法, 可以精确控制position出现在界面上的位置. 比如 置顶, 尾部, 居中等.
 * 但是, 当position没有出现在界面上时, 很难使用这2个方法精确控制.
 *
 * 综上: 要想精确控制position, 应先使用[scrollToPosition or smoothScrollToPosition]保证目标
 * position可见, 再使用[scrollBy or smoothScrollBy]精确控制目标的位置
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/09/28
 */
class ScrollHelper {

    companion object {
        /**滚动类别: 默认不特殊处理. 滚动到item显示了就完事*/
        const val SCROLL_TYPE_NORMAL = 0

        /**滚动类别: 将item滚动到第一个位置*/
        const val SCROLL_TYPE_TOP = 1

        /**滚动类别: 将item滚动到最后一个位置*/
        const val SCROLL_TYPE_BOTTOM = 2

        /**滚动类别: 将item滚动到居中位置*/
        const val SCROLL_TYPE_CENTER = 3

        var DEFAULT_SCROLL_STEP = 16

        var DEFAULT_ANIM_DELAY = 16L
    }

    internal var recyclerView: RecyclerView? = null

    fun attach(recyclerView: RecyclerView) {
        if (this.recyclerView == recyclerView) {
            return
        }
        detach()
        this.recyclerView = recyclerView
    }

    fun detach() {
        recyclerView = null
    }

    fun itemCount(): Int {
        return recyclerView?.layoutManager?.itemCount ?: 0
    }

    fun lastItemPosition(): Int {
        return itemCount() - 1
    }

    /**负数表示反序, 倒数第几个*/
    fun parsePosition(position: Int): Int {
        return if (position < 0) {
            itemCount() + position
        } else {
            position
        }
    }

    fun scrollToLast(
        scrollParams: ScrollParams = _defaultScrollParams().apply {
            scrollType = SCROLL_TYPE_BOTTOM
            isFromAddItem
        }, action: ScrollParams.() -> Unit = {}
    ) {
        scrollParams.action()
        startScroll(lastItemPosition(), scrollParams)
    }

    fun _defaultScrollParams(): ScrollParams {
        return ScrollParams()
    }

    fun startScroll(scrollParams: ScrollParams = _defaultScrollParams()) {
        startScroll(scrollParams.scrollPosition, scrollParams)
    }

    fun scroll(position: Int, scrollParams: ScrollParams = _defaultScrollParams()) {
        startScroll(position, scrollParams)
    }

    fun startScroll(position: Int, scrollParams: ScrollParams = _defaultScrollParams()) {
        val targetPosition = parsePosition(position)
        if (check(targetPosition)) {
            scrollParams.scrollPosition = targetPosition

            if (scrollParams.stepScroll) {
                //步进滚动
                scrollWithStep(scrollParams)
            } else {
                recyclerView?.stopScroll()

                if (isPositionVisible(targetPosition)) {
                    //目标可见时的滚动
                    scrollWithVisible(scrollParams)
                } else {
                    //先滚动出目标
                    scrollWithNoVisible(scrollParams)
                }
            }
        }
    }

    private var lockLayoutListener: LockLayoutListener? = null

    /**短时间之内, 锁定滚动到0的位置*/
    fun lockScrollToFirst(config: LockDrawListener.() -> Unit = {}) {
        lockPositionByDraw {
            scrollType = SCROLL_TYPE_TOP
            lockPosition = 0
            firstScrollAnim = true
            scrollAnim = true
            force = true
            firstForce = true
            enableLock = true
            lockDuration = 60
            autoDetach = true
            isFromAddItem = false
            config()
        }
    }

    /**短时间之内, 锁定滚动到倒数第一个的位置*/
    fun lockScrollToLast(config: LockDrawListener.() -> Unit = {}) {
        lockPositionByDraw {
            scrollType = SCROLL_TYPE_BOTTOM
            lockPosition = -1
            firstScrollAnim = true
            scrollAnim = true
            force = true
            firstForce = true
            enableLock = true
            lockDuration = 60
            autoDetach = true
            isFromAddItem = false
            config()
        }
    }

    /**
     * 当界面有变化时, 自动滚动到最后一个位置
     * [unlockPosition]
     * */
    fun lockPosition(config: LockLayoutListener.() -> Unit = {}): LockLayoutListener? {
        var result: LockLayoutListener? = null
        if (lockLayoutListener == null && recyclerView != null) {
            lockLayoutListener = LockLayoutListener().apply {
                scrollType = SCROLL_TYPE_CENTER
                autoDetach = true
                config()
                attach(recyclerView!!)
            }
            result = lockLayoutListener
        }
        return result
    }

    fun lockPositionByDraw(config: LockDrawListener.() -> Unit = {}): LockDrawListener? {
        var result: LockDrawListener? = null
        recyclerView?.let {
            result = LockDrawListener().apply {
                //默认将目标滚动到中间位置
                scrollType = SCROLL_TYPE_CENTER
                autoDetach = true
                config()
                attach(it)
            }
            it.postInvalidateOnAnimation()
        }
        return result
    }

    fun lockPositionByLayout(config: LockLayoutListener.() -> Unit = {}): LockLayoutListener? {
        var result: LockLayoutListener? = null
        recyclerView?.let {
            result = LockLayoutListener().apply {
                scrollType = SCROLL_TYPE_CENTER
                autoDetach = true
                config()
                attach(it)
            }
            it.requestLayout()
        }
        return result
    }

    fun unlockPosition() {
        lockLayoutListener?.detach()
        lockLayoutListener = null
    }

    internal fun scrollWithNoVisible(scrollParams: ScrollParams) {
        val targetPosition = parsePosition(scrollParams.scrollPosition)
        if (scrollParams.scrollAnim) {
            if (scrollParams.isFromAddItem) {
                if (recyclerView?.itemAnimator is SimpleItemAnimator) {
                    //itemAnimator 自带动画
                    recyclerView?.scrollToPosition(targetPosition)
                } else {
                    recyclerView?.smoothScrollToPosition(targetPosition)
                }
            } else {
                recyclerView?.smoothScrollToPosition(targetPosition)
            }
        } else {
            if (scrollParams.isFromAddItem) {
                val itemAnimator = recyclerView?.itemAnimator
                if (itemAnimator != null) {
                    //有默认的动画
                    recyclerView?.itemAnimator = null
                    OnNoAnimScrollIdleListener(itemAnimator).attach(recyclerView!!)
                }
            }
            recyclerView?.scrollToPosition(targetPosition)
        }
        if (scrollParams.scrollType != SCROLL_TYPE_NORMAL) {
            //不可见时, 需要现滚动到可见位置, 再进行微调
            OnScrollIdleListener(scrollParams).attach(recyclerView!!)
        }
    }

    /**当需要滚动的目标位置已经在屏幕上可见*/
    internal fun scrollWithVisible(scrollParams: ScrollParams) {
        when (scrollParams.scrollType) {
            SCROLL_TYPE_NORMAL -> {
                //nothing
            }

            SCROLL_TYPE_TOP -> {
                viewByPosition(scrollParams.scrollPosition)?.also { child ->
                    recyclerView?.apply {
                        val dx = layoutManager!!.getDecoratedLeft(child) -
                                paddingLeft - scrollParams.scrollOffset

                        val dy = layoutManager!!.getDecoratedTop(child) -
                                paddingTop - scrollParams.scrollOffset

                        if (scrollParams.scrollAnim) {
                            smoothScrollBy(dx, dy)
                        } else {
                            scrollBy(dx, dy)
                        }
                    }
                }
            }

            SCROLL_TYPE_BOTTOM -> {
                viewByPosition(scrollParams.scrollPosition)?.also { child ->
                    recyclerView?.apply {
                        val dx =
                            layoutManager!!.getDecoratedRight(child) -
                                    measuredWidth + paddingRight + scrollParams.scrollOffset
                        val dy =
                            layoutManager!!.getDecoratedBottom(child) -
                                    measuredHeight + paddingBottom + scrollParams.scrollOffset

                        if (scrollParams.scrollAnim) {
                            smoothScrollBy(dx, dy)
                        } else {
                            scrollBy(dx, dy)
                        }
                    }
                }
            }

            SCROLL_TYPE_CENTER -> {
                viewByPosition(scrollParams.scrollPosition)?.also { child ->

                    recyclerView?.apply {
                        val recyclerCenterX =
                            (measuredWidth - paddingLeft - paddingRight) / 2 + paddingLeft

                        val recyclerCenterY =
                            (measuredHeight - paddingTop - paddingBottom) / 2 + paddingTop

                        val dx = layoutManager!!.getDecoratedLeft(child) - recyclerCenterX +
                                layoutManager!!.getDecoratedMeasuredWidth(child) / 2 + scrollParams.scrollOffset

                        val dy = layoutManager!!.getDecoratedTop(child) - recyclerCenterY +
                                layoutManager!!.getDecoratedMeasuredHeight(child) / 2 + scrollParams.scrollOffset

                        if (scrollParams.scrollAnim) {
                            smoothScrollBy(dx, dy)
                        } else {
                            scrollBy(dx, dy)
                        }
                    }
                }
            }
        }
    }

    internal fun scrollWithStep(scrollParams: ScrollParams) {
        var dx = 0
        var dy = scrollParams.stepScrollSize * dpi

        val scrollPosition = scrollParams.scrollPosition

        //结束时, view的top坐标
        var endViewTop = 0

        //当前view的top坐标
        var currentViewTop = 0

        var refPosition = RecyclerView.NO_POSITION

        when (scrollParams.scrollType) {
            SCROLL_TYPE_TOP -> {
                endViewTop = recyclerView?.paddingTop ?: 0
                refPosition = recyclerView.findFirstVisibleItemPosition()
            }

            SCROLL_TYPE_BOTTOM -> {
                endViewTop = recyclerView.mH() - (recyclerView?.paddingBottom ?: 0) -
                        viewByPosition(scrollPosition).mH()

                refPosition = recyclerView.findLastVisibleItemPosition()
            }

            SCROLL_TYPE_CENTER -> {
                endViewTop = (recyclerView?.paddingTop ?: 0) + recyclerView.mH() / 2 -
                        viewByPosition(scrollPosition).mH() / 2
                refPosition = recyclerView.findFirstVisibleItemPosition()
            }

            else -> {
                if (isPositionVisible(scrollPosition)) {
                    //目标可见, 停止滚动
                    dx = 0
                    dy = 0
                }
            }
        }

        if (refPosition != RecyclerView.NO_POSITION) {
            if ((refPosition - scrollPosition).absoluteValue > 1) {
                //相差1个以上, 关闭step
                scrollWithNoVisible(scrollParams)
                return
            }

            currentViewTop = when {
                isPositionVisible(scrollPosition) -> recyclerView.getPositionTop(scrollPosition)
                scrollPosition > refPosition -> Int.MAX_VALUE
                else -> Int.MIN_VALUE
            }

            if (currentViewTop > endViewTop) {
                dy = min(dy, currentViewTop - endViewTop)
            } else {
                dy = -min(dy, endViewTop - currentViewTop)
            }
        }

        if (scrollParams.scrollAnim) {
            recyclerView?.smoothScrollBy(dx, dy)
        } else {
            recyclerView?.scrollBy(dx, dy)
        }
    }

    /**是否滚动到了目标*/
    private fun isScrollToPosition(targetPosition: Int, scrollType: Int): Boolean {
        val scrollPosition = parsePosition(targetPosition)
        return when (scrollType) {
            SCROLL_TYPE_TOP -> recyclerView.getPositionTop(scrollPosition) == 0
            SCROLL_TYPE_BOTTOM -> recyclerView.getPositionBottom(scrollPosition) == recyclerView.mH()
            SCROLL_TYPE_CENTER -> recyclerView.getPositionTop(scrollPosition) ==
                    (recyclerView.mH() - viewByPosition(scrollPosition).mH()) / 2

            else -> isPositionVisible(scrollPosition)
        }
    }

    /**位置是否可见*/
    private fun isPositionVisible(position: Int): Boolean {
        return recyclerView?.layoutManager.isPositionVisible(position)
    }

    private fun viewByPosition(position: Int): View? {
        return recyclerView?.layoutManager?.findViewByPosition(position)
    }

    /**检查是否可以操作滚动*/
    private fun check(position: Int): Boolean {
        if (recyclerView == null) {
            L.e("请先调用[attach]方法.")
            return false
        }

        if (recyclerView?.adapter == null) {
            L.w("忽略, [adapter] is null")
            return false
        }

        if (recyclerView?.layoutManager == null) {
            L.w("忽略, [layoutManager] is null")
            return false
        }

        val itemCount = itemCount()
        val p = parsePosition(position)
        if (p < 0 || p >= itemCount) {
            L.w("忽略, [position] 需要在 [0,$itemCount) 之间.")
            return false
        }

        return true
    }

    fun log(recyclerView: RecyclerView? = this.recyclerView) {
        recyclerView?.viewTreeObserver?.apply {
            this.addOnDrawListener {
                L.i("onDraw")
            }
            this.addOnGlobalFocusChangeListener { oldFocus, newFocus ->
                L.i("on...$oldFocus ->$newFocus")
            }
            this.addOnGlobalLayoutListener {
                L.w("this....")
            }
            //此方法回调很频繁
            this.addOnPreDrawListener {
                //L.v("this....")
                true
            }
            this.addOnScrollChangedListener {
                L.i("this....${recyclerView.scrollState}")
            }
            this.addOnTouchModeChangeListener {
                L.i("this....")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                this.addOnWindowFocusChangeListener {
                    L.i("this....")
                }
            }
        }
    }

    private abstract inner class OnScrollListener : ViewTreeObserver.OnScrollChangedListener,
        IAttachListener {
        var attachView: View? = null

        override fun attach(view: View) {
            detach()
            attachView = view
            view.viewTreeObserver.addOnScrollChangedListener(this)
        }

        override fun detach() {
            attachView?.viewTreeObserver?.removeOnScrollChangedListener(this)
        }

        override fun onScrollChanged() {
            onScrollChanged(recyclerView?.scrollState ?: RecyclerView.SCROLL_STATE_IDLE)
            detach()
        }

        abstract fun onScrollChanged(state: Int)
    }

    /**滚动结束之后, 根据类别, 继续滚动.*/
    private inner class OnScrollIdleListener(val scrollParams: ScrollParams) :
        OnScrollListener() {

        override fun onScrollChanged(state: Int) {
            if (state == RecyclerView.SCROLL_STATE_IDLE) {
                scrollWithVisible(scrollParams)
            }
        }
    }

    /**临时去掉动画滚动, 之后恢复动画*/
    private inner class OnNoAnimScrollIdleListener(val itemAnimator: RecyclerView.ItemAnimator?) :
        OnScrollListener() {

        override fun onScrollChanged(state: Int) {
            if (state == RecyclerView.SCROLL_STATE_IDLE) {
                recyclerView?.itemAnimator = itemAnimator
            }
        }
    }

    abstract inner class LockScrollListener : ViewTreeObserver.OnGlobalLayoutListener,
        ViewTreeObserver.OnDrawListener,
        IAttachListener, Runnable {

        /**激活滚动动画*/
        var scrollAnim: Boolean = true
            set(value) {
                field = value
                if (!value && firstScrollAnim) {
                    firstScrollAnim = false
                }
            }

        /**激活第一个滚动的动画*/
        var firstScrollAnim: Boolean = true

        /**设置了[stepScroll]之后, [lockDuration]超时了, 但也还是会滚动到目标为止
         * 只在滚动目标的位置相差不太远时生效*/
        var stepScroll: Boolean = false
            set(value) {
                field = value
                if (value) {
                    //激活step之后, 建议不要开始smooth滚动方式
                    scrollAnim = false
                }
            }

        var stepScrollSize: Int = DEFAULT_SCROLL_STEP

        /**激活了滚动动画时, 调用延迟设置,
         * 如果延迟设置的低, smooth相当于没动画了*/
        var animDelay: Long = DEFAULT_ANIM_DELAY

        /**不检查界面 情况, 强制滚动到最后的位置. 关闭后. 会智能判断*/
        var force: Boolean = false

        /**第一次时, 是否强制滚动. 先触发一次滚动, 之后再微调至目标*/
        var firstForce: Boolean = true

        /**滚动阈值, 倒数第几个可见时, 就允许滚动*/
        var scrollThreshold = 2

        /**锁定需要滚动的position, 负数表示倒数第几个*/
        var lockPosition = RecyclerView.NO_POSITION

        var scrollType = SCROLL_TYPE_NORMAL
        var scrollOffset = 0
        var isFromAddItem = true

        /**是否激活锁定滚动功能*/
        var enableLock = true

        /**滚动到目标后, 自动调用[detach]*/
        var autoDetach = false

        /**锁定时长, 毫秒
         * 这段时间之内, 都会触发滚动*/
        var lockDuration: Long = -1

        //记录开始的统计时间
        var _lockStartTime = 0L

        override fun run() {

            val itemCount = itemCount()
            if (!enableLock || itemCount <= 0) {
                return
            }

            val isScrollAnim = if (firstForce) firstScrollAnim && scrollAnim else scrollAnim

            val position = parsePosition(lockPosition)

            val scrollParams = ScrollParams(
                position,
                scrollType,
                isScrollAnim,
                scrollOffset,
                isFromAddItem,
                stepScroll,
                stepScrollSize
            )

            if (force || firstForce || stepScroll) {
                scroll(position, scrollParams)
                onScrollTrigger()
                L.i("锁定滚动至->$position $force $firstForce $stepScroll")
            } else {
                val lastItemPosition = lastItemPosition()
                if (lastItemPosition != RecyclerView.NO_POSITION) {
                    //智能判断是否可以锁定
                    if (position == 0) {
                        //滚动到顶部
                        val findFirstVisibleItemPosition =
                            recyclerView?.layoutManager.findFirstVisibleItemPosition()

                        if (findFirstVisibleItemPosition <= scrollThreshold) {
                            scroll(position, scrollParams)
                            onScrollTrigger()
                            L.i("锁定滚动至->$position")
                        }
                    } else {
                        val findLastVisibleItemPosition =
                            recyclerView?.layoutManager.findLastVisibleItemPosition()

                        if (lastItemPosition - findLastVisibleItemPosition <= scrollThreshold) {
                            //最后第一个或者最后第2个可见, 智能判断为可以滚动到尾部
                            scroll(position, scrollParams)
                            onScrollTrigger()
                            L.i("锁定滚动至->$position")
                        }
                    }
                }
            }

            firstForce = false
        }

        var attachView: View? = null

        override fun attach(view: View) {
            detach()
            attachView = view
        }

        override fun detach() {
            attachView?.removeCallbacks(this)
        }

        /**[ViewTreeObserver.OnDrawListener]*/
        override fun onDraw() {
            initLockStartTime()
            onLockScroll()
        }

        /**[ViewTreeObserver.OnGlobalLayoutListener]*/
        override fun onGlobalLayout() {
            initLockStartTime()
            onLockScroll()
        }

        open fun initLockStartTime() {
            if (_lockStartTime <= 0) {
                _lockStartTime = nowTime()
            }
        }

        open fun isLockTimeout(): Boolean {
            if (stepScroll) {
                //只有在目标可见的时候, 才统计时间
                if (isPositionVisible(parsePosition(lockPosition))) {
                    //目标可见
                } else {
                    _lockStartTime = nowTime()
                    return false
                }
            }
            val timeout = if (lockDuration > 0) {
                val nowTime = nowTime()
                nowTime - _lockStartTime > lockDuration
            } else {
                false
            }

            if (stepScroll) {
                if (timeout) {
                    val nowTime = nowTime()
                    return isScrollToPosition(
                        lockPosition,
                        scrollType
                    ) || nowTime - _lockStartTime > lockDuration * 10
                }
            }
            return timeout
        }

        /**是否第一个调用[post]*/
        var _isFirstPost = true

        open fun onLockScroll() {
            //attachView?.removeCallbacks(this)
            if (enableLock) {
                if (isLockTimeout()) {
                    //锁定超时, 放弃操作
                    if (autoDetach) {
                        attachView?.post {
                            detach()
                        }
                    } else {
                        L.w("锁定已超时, 跳过操作.")
                    }
                } else {
                    if ((stepScroll || scrollAnim || firstScrollAnim) && animDelay > 0 && !_isFirstPost) {
                        attachView?.postDelayed(this, animDelay)
                    } else {
                        attachView?.post(this)
                    }
                    _isFirstPost = false
                }
            }
        }

        open fun onScrollTrigger() {
            if (autoDetach) {
                if (isLockTimeout() || lockDuration == -1L) {
                    detach()
                }
            }
        }
    }

    /**锁定滚动到最后一个位置*/
    inner class LockLayoutListener : LockScrollListener() {

        override fun attach(view: View) {
            super.attach(view)
            view.viewTreeObserver.addOnGlobalLayoutListener(this)
        }

        override fun detach() {
            super.detach()
            attachView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        }
    }

    /**滚动到0*/
    inner class LockDrawListener : LockScrollListener() {

        override fun attach(view: View) {
            super.attach(view)
            view.viewTreeObserver.addOnDrawListener(this)
        }

        override fun detach() {
            super.detach()
            attachView?.viewTreeObserver?.removeOnDrawListener(this)
        }
    }

    private interface IAttachListener {
        fun attach(view: View)

        fun detach()
    }
}

//滚动参数
data class ScrollParams(
    /**滚动目标, 负数反向取值*/
    var scrollPosition: Int = RecyclerView.NO_POSITION,
    /**滚动类型, [可见就行] [贴顶显示] [贴底显示] [居中显示]*/
    var scrollType: Int = ScrollHelper.SCROLL_TYPE_NORMAL,
    /**是否需要动画*/
    var scrollAnim: Boolean = true,
    /**滚动到当前位置时, 额外的偏移*/
    var scrollOffset: Int = 0,
    /**是否由AddItem导致的偏移*/
    var isFromAddItem: Boolean = true,
    /**步进滚动, 即使用[scrollBy] [smoothScrollBy]进行滚动*/
    var stepScroll: Boolean = false,
    /**一次滚动多少距离, 会自动乘以 dpi*/
    var stepScrollSize: Int = DEFAULT_SCROLL_STEP,
)

fun RecyclerView?.findFirstVisibleItemPosition(): Int {
    return this?.layoutManager.findFirstVisibleItemPosition()
}

/**获取目标位置child, 顶部的距离*/
fun RecyclerView?.getPositionTop(position: Int, def: Int = Int.MAX_VALUE): Int {
    val view = this?.layoutManager?.findViewByPosition(position) ?: return def
    return layoutManager?.getDecoratedTop(view) ?: def
}

fun RecyclerView?.getPositionBottom(position: Int, def: Int = Int.MAX_VALUE): Int {
    val view = this?.layoutManager?.findViewByPosition(position) ?: return def
    return layoutManager?.getDecoratedBottom(view) ?: def
}

fun RecyclerView.LayoutManager?.findFirstVisibleItemPosition(): Int {
    var result = RecyclerView.NO_POSITION
    this?.also { layoutManager ->
        var firstItemPosition: Int = -1
        if (layoutManager is LinearLayoutManager) {
            firstItemPosition = layoutManager.findFirstVisibleItemPosition()
        } else if (layoutManager is StaggeredGridLayoutManager) {
            firstItemPosition =
                layoutManager.findFirstVisibleItemPositions(null).firstOrNull() ?: -1
        }
        result = firstItemPosition
    }
    return result
}

fun RecyclerView?.findLastVisibleItemPosition(): Int {
    return this?.layoutManager.findLastVisibleItemPosition()
}

fun RecyclerView.LayoutManager?.findLastVisibleItemPosition(): Int {
    var result = RecyclerView.NO_POSITION
    this?.also { layoutManager ->
        var lastItemPosition: Int = -1
        if (layoutManager is LinearLayoutManager) {
            lastItemPosition = layoutManager.findLastVisibleItemPosition()
        } else if (layoutManager is StaggeredGridLayoutManager) {
            lastItemPosition =
                layoutManager.findLastVisibleItemPositions(null).lastOrNull() ?: -1
        }
        result = lastItemPosition
    }
    return result
}

fun RecyclerView?.isPositionVisible(position: Int): Boolean {
    return this?.layoutManager.isPositionVisible(position)
}

fun RecyclerView.LayoutManager?.isPositionVisible(position: Int): Boolean {
    return position >= 0 && position in findFirstVisibleItemPosition()..findLastVisibleItemPosition()
}