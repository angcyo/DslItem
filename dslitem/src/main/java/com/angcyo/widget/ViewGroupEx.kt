package com.angcyo.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.angcyo.dsladapter.size
import com.angcyo.item.R
import kotlin.math.absoluteValue
import kotlin.math.min

/**
 * Kotlin ViewGroup的扩展
 * Created by angcyo on 2017-07-26.
 */

fun <T : View> View?.find(@IdRes id: Int): T? {
    return this?.findViewById(id)
}

fun <T> ViewGroup.resetChild(
    list: List<T>?,
    layoutId: Int,
    init: (itemView: View, item: T, itemIndex: Int) -> Unit = { _, _, _ -> }
) {
    resetChild(list.size(), layoutId) { itemView, itemIndex ->
        val item = list!!.get(itemIndex)
        init(itemView, item, itemIndex)
    }
}

fun ViewGroup.resetChild(
    size: Int,
    layoutId: Int,
    init: (itemView: View, itemIndex: Int) -> Unit = { _, _ -> }
) {
    //如果布局id不一样, 说明child不一样, 需要remove
    for (index in childCount - 1 downTo 0) {
        val tag = getChildAt(index).getTag(R.id.tag)
        if (tag == null || (tag is Int && tag != layoutId)) {
            removeViewAt(index)
        }
    }

    resetChildCount(size) { childIndex, childView ->
        if (childView == null) {
            val itemView = LayoutInflater.from(context).inflate(layoutId, this, false)
            itemView.setTag(R.id.tag, layoutId)
            itemView
        } else {
            childView
        }
    }

    for (i in 0 until size) {
        init(getChildAt(i), i)
    }
}

/**将子View的数量, 重置到指定的数量*/
fun ViewGroup.resetChildCount(
    newSize: Int,
    createOrInitView: (childIndex: Int, childView: View?) -> View
) {
    val oldSize = childCount
    val count = newSize - oldSize
    if (count > 0) {
        //需要补充子View
        for (i in 0 until count) {
            addView(createOrInitView.invoke(oldSize + i, null))
        }
    } else if (count < 0) {
        //需要移除子View
        for (i in 0 until count.absoluteValue) {
            removeViewAt(oldSize - 1 - i)
        }
    }

    //初始化
    for (i in 0 until min(oldSize, newSize)) {
        createOrInitView.invoke(i, getChildAt(i))
    }
}