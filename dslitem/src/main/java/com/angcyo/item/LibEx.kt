package com.angcyo.item

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.text.TextPaint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.OverScroller
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.widget.ScrollerCompat
import androidx.core.widget.TextViewCompat
import com.angcyo.dsladapter.DslViewHolder
import com.angcyo.dsladapter.L
import com.angcyo.dsladapter.internal.ThrottleClickListener
import com.angcyo.item.base.LibInitProvider
import com.angcyo.widget.DslButton
import com.angcyo.widget.edit.SingleTextWatcher
import com.angcyo.widget.span.undefined_int
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/27
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */

fun Int.remove(value: Int): Int = this and value.inv()
fun Int.add(value: Int): Int = this or value

fun TextView.addFlags(add: Boolean, flat: Int) {
    val paint: TextPaint = paint
    paint.addPaintFlags(add, flat)
    postInvalidate()
}

fun Paint.addPaintFlags(add: Boolean, flat: Int) {
    flags = if (add) {
        flags.add(flat)
    } else {
        flags.remove(flat)
    }
}

/**文本的宽度*/
fun Paint.textWidth(text: String?): Float {
    if (text == null) {
        return 0f
    }
    return measureText(text)
}

/**文本的高度*/
fun Paint?.textHeight(): Float = this?.run { descent() - ascent() } ?: 0f

/**
 * 设置是否加粗文本
 */
fun TextView.setBoldText(bool: Boolean) {
    addFlags(bool, Paint.FAKE_BOLD_TEXT_FLAG)
}

fun TextView.setLeftIco(id: Int) {
    setLeftIco(loadDrawable(id))
}

fun TextView.setLeftIco(drawable: Drawable?) {
    val compoundDrawables: Array<Drawable?> = compoundDrawables
    setCompoundDrawablesWithIntrinsicBounds(
        drawable,
        compoundDrawables[1],
        compoundDrawables[2],
        compoundDrawables[3]
    )
}

fun TextView.setRightIco(@DrawableRes id: Int) {
    setRightIco(loadDrawable(id))
}

fun TextView.setRightIco(drawable: Drawable?) {
    val compoundDrawables: Array<Drawable?> = compoundDrawables
    setCompoundDrawablesWithIntrinsicBounds(
        compoundDrawables[0],
        compoundDrawables[1],
        drawable,
        compoundDrawables[3]
    )
}

fun View.loadDrawable(@DrawableRes id: Int): Drawable? {
    return context?.loadDrawable(id)
}

fun Context.loadDrawable(id: Int): Drawable? {
    if (id <= 0) {
        return null
    }
    return try {
        ContextCompat.getDrawable(this, id)?.initBounds()
    } catch (e: Exception) {
        L.w(e)
        null
    }
}

/**初始化bounds*/
fun Drawable.initBounds(width: Int = undefined_int, height: Int = undefined_int): Drawable {
    if (bounds.isEmpty) {
        val w = if (width == undefined_int) minimumWidth else width
        val h = if (height == undefined_int) minimumHeight else height
        bounds.set(0, 0, w, h)
    }
    return this
}

fun ViewGroup.inflate(@LayoutRes layoutId: Int, attachToRoot: Boolean = true): View {
    if (layoutId == -1) {
        return this
    }
    val rootView = LayoutInflater.from(context).inflate(layoutId, this, false)
    if (attachToRoot) {
        addView(rootView)
    }
    return rootView
}

fun TextView?.string(trim: Boolean = true): String {
    if (this == null) {
        return ""
    }

    var rawText = if (TextUtils.isEmpty(text)) {
        ""
    } else {
        text.toString()
    }
    if (trim) {
        rawText = rawText.trim { it <= ' ' }
    }
    return rawText
}

/**只要文本改变就通知*/
fun EditText.onTextChange(
    defaultText: CharSequence? = string(),
    shakeDelay: Long = -1L,//去频限制, 负数表示不开启
    listener: (CharSequence) -> Unit
) {
    addTextChangedListener(object : SingleTextWatcher() {
        var mainHandle: Handler? = null

        val callback: Runnable = Runnable {
            listener.invoke(lastText ?: "")
        }

        init {
            if (shakeDelay >= 0) {
                mainHandle = Handler(Looper.getMainLooper())
            }
        }

        var lastText: CharSequence? = defaultText

        override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
            super.onTextChanged(sequence, start, before, count)
            mainHandle?.removeCallbacks(callback)

            val text = sequence?.toString() ?: ""
            if (TextUtils.equals(lastText, text)) {
            } else {
                lastText = text
                if (mainHandle == null) {
                    callback.run()
                } else {
                    mainHandle?.postDelayed(callback, shakeDelay)
                }
            }
        }
    })
}

/**
 * 从一个对象中, 获取指定的成员对象
 */
fun Any?.getMember(member: String): Any? {
    return this?.run { this.getMember(this.javaClass, member) }
}

fun Any?.getMember(
    cls: Class<*>,
    member: String
): Any? {
    var result: Any? = null
    try {
        val memberField = cls.getDeclaredField(member)
        memberField.isAccessible = true
        result = memberField[this]
    } catch (e: Exception) {
        //L.i("错误:" + cls.getSimpleName() + " ->" + e.getMessage());
    }
    return result
}

fun Any?.getCurrVelocity(): Float {
    return when (this) {
        is OverScroller -> currVelocity
        is ScrollerCompat -> currVelocity
        else -> {
            0f
        }
    }
}

fun MotionEvent.isTouchDown(): Boolean {
    return actionMasked == MotionEvent.ACTION_DOWN
}

fun MotionEvent.isTouchFinish(): Boolean {
    return actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL
}

fun MotionEvent.isTouchMove(): Boolean {
    return actionMasked == MotionEvent.ACTION_MOVE
}

/**清空所有[TextWatcher]*/
fun TextView.clearListeners() {
    try {
        val mListeners: ArrayList<*>? =
            getMember(TextView::class.java, "mListeners") as? ArrayList<*>
        mListeners?.clear()
    } catch (e: Exception) {
        L.e(e)
    }
}

fun TextView?.setMaxLine(maxLine: Int = 1) {
    this?.run {
        if (maxLine <= 1) {
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
        } else {
            isSingleLine = false
            maxLines = maxLine
        }
    }
}

/**设置文本, 并且将光标至于文本最后面*/
fun TextView.setInputText(text: CharSequence? = null, selection: Boolean = true) {
    setText(text)
    if (selection && this is EditText) {
        setSelection(min(text?.length ?: 0, getText().length))
    }
}

fun View?.padding(p: Int) {
    this?.setPadding(p, p, p, p)
}

@ColorInt
fun _color(@ColorRes id: Int): Int {
    return getColor(id)
}

@Px
fun _dimen(@DimenRes id: Int, context: Context = LibInitProvider.contentProvider): Int {
    return getDimen(id, context)
}

@Px
fun getDimen(@DimenRes id: Int, context: Context = LibInitProvider.contentProvider): Int {
    return context.getDimen(id)
}

@Px
fun Context.getDimen(@DimenRes id: Int): Int {
    return resources.getDimensionPixelOffset(id)
}

@ColorInt
fun getColor(@ColorRes id: Int): Int {
    return ContextCompat.getColor(LibInitProvider.contentProvider, id)
}

fun View?.visible(value: Boolean = true) {
    this?.visibility = if (value) View.VISIBLE else View.GONE
}

fun View?.gone(value: Boolean = true) {
    this?.visibility = if (value) View.GONE else View.VISIBLE
}

fun Int.getSize(): Int {
    return View.MeasureSpec.getSize(this)
}

fun Int.getMode(): Int {
    return View.MeasureSpec.getMode(this)
}

/**match_parent*/
fun Int.isExactly(): Boolean {
    return getMode() == View.MeasureSpec.EXACTLY
}

/**wrap_content*/
fun Int.isAtMost(): Boolean {
    return getMode() == View.MeasureSpec.AT_MOST
}

fun Int.isUnspecified(): Boolean {
    return getMode() == View.MeasureSpec.UNSPECIFIED
}

/**未指定大小*/
fun Int.isNotSpecified(): Boolean {
    return isAtMost() || isUnspecified()
}

/**点击事件*/
fun View?.clickIt(action: (View) -> Unit) {
    this?.setOnClickListener(action)
}

/**隐藏软键盘*/
fun View.hideSoftInput() {
    val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(windowToken, 0)
}

/**显示软键盘*/
fun View.showSoftInput() {
    if (this is EditText) {
        requestFocus()
        val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.showSoftInput(this, 0)
    }
}

fun TextView.addFilter(filter: InputFilter) {
    val oldFilters = filters
    val newFilters = arrayOfNulls<InputFilter>(oldFilters.size + 1)
    System.arraycopy(oldFilters, 0, newFilters, 0, oldFilters.size)
    newFilters[oldFilters.size] = filter
    filters = newFilters
}

/**移除指定[InputFilter]*/
fun TextView.removeFilter(predicate: InputFilter.() -> Boolean) {
    val oldFilters = filters
    val removeList = mutableListOf<InputFilter>()
    oldFilters.forEach {
        if (it.predicate()) {
            removeList.add(it)
        }
    }
    if (removeList.isEmpty()) {
        return
    }
    val list = oldFilters.toMutableList().apply {
        removeAll(removeList)
    }
    filters = list.toTypedArray()
}

fun TextView.leftIco() = TextViewCompat.getCompoundDrawablesRelative(this)[0]
fun TextView.topIco() = TextViewCompat.getCompoundDrawablesRelative(this)[1]
fun TextView.rightIco() = TextViewCompat.getCompoundDrawablesRelative(this)[2]
fun TextView.bottomIco() = TextViewCompat.getCompoundDrawablesRelative(this)[3]

/**恢复选中范围*/
fun EditText.restoreSelection(start: Int, stop: Int) {
    val length = text.length
    val _start = if (start in 0..length) {
        start
    } else {
        -1
    }

    val _stop = if (stop in 0..length) {
        stop
    } else {
        -1
    }

    if (_stop >= 0) {
        val min = min(max(0, _start), _stop)
        val max = max(max(0, _start), _stop)
        setSelection(min, max)
    } else if (_start >= 0) {
        setSelection(_start)
    }
}

fun Collection<*>?.size() = this?.size ?: 0


/**判断2个列表中的数据是否改变过*/
fun <T> Collection<T>?.isChange(other: List<T>?): Boolean {
    if (this.size() != other.size()) {
        return true
    }
    this?.forEachIndexed { index, t ->
        if (t != other?.getOrNull(index)) {
            return true
        }
    }
    return false
}

fun DslViewHolder.button(@IdRes id: Int): DslButton? = v(id)

fun Any?.string(def: CharSequence = ""): CharSequence {
    return when {
        this == null -> return def
        this is TextView -> text ?: def
        this is CharSequence -> this
        else -> this.toString()
    }
}

fun Any.toStr(): String = when (this) {
    is String -> this
    else -> toString()
}

/**点击事件节流处理*/
fun View?.throttleClickIt(action: (View) -> Unit) {
    this?.setOnClickListener(ThrottleClickListener(action = action))
}

val View.drawLeft get() = paddingLeft
val View.drawTop get() = paddingTop
val View.drawRight get() = measuredWidth - paddingRight
val View.drawBottom get() = measuredHeight - paddingBottom
val View.drawWidth get() = drawRight - drawLeft
val View.drawHeight get() = drawBottom - drawTop
val View.drawCenterX get() = drawLeft + drawWidth / 2
val View.drawCenterY get() = drawTop + drawHeight / 2