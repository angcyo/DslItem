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
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.angcyo.dsladapter.L
import com.angcyo.item.base.LibInitProvider
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

fun View?.padding(p: Int) {
    this?.setPadding(p, p, p, p)
}

@ColorInt
fun _color(@ColorRes id: Int): Int {
    return getColor(id)
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