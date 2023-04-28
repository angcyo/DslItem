package com.angcyo.item2.widget.recycler

import android.graphics.drawable.Drawable
import android.view.View
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 属性改变时, 自动触发[android.view.View.invalidate]
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/06
 */
class InvalidateProperty<VIEW : View, VALUE>(var value: VALUE) : ReadWriteProperty<VIEW, VALUE> {

    override fun getValue(thisRef: VIEW, property: KProperty<*>): VALUE {
        val v = value
        if (v is Drawable) {
            if (v.callback != thisRef) {
                v.callback = thisRef
            }
        }
        return v
    }

    override fun setValue(thisRef: VIEW, property: KProperty<*>, value: VALUE) {
        val old = this.value
        this.value = value
        if (old != value) {
            thisRef.invalidate()
        }
    }

}