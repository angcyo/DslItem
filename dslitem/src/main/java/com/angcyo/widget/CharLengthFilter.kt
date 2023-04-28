package com.angcyo.widget

import android.text.InputFilter
import android.text.Spanned

/**
 * 使用英文字符数过滤, 一个汉字等于2个英文, 一个emoji表情等于2个汉字
 * Created by angcyo on 2018-08-10.
 * Email:angcyo@126.com
 */
class CharLengthFilter(var maxLen: Int) : InputFilter {

    companion object {
        const val MAX_CHAR: Char = 255.toChar()
    }

    /**
     * 将 dest 字符中, 的dstart 位置到 dend 位置的字符串,
     * 替换成 source 字符中, 的start 位置到 end 对应的字符串
     */
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        var dIndex = 0
        var count = 0
        var dCount = 0 //dest 中, 需要替换掉多少个char
        //当前已经存在char数量
        while (count <= maxLen && dIndex < dest.length) {
            val c = dest[dIndex++]
            if (c <= MAX_CHAR) {
                count += 1
                if (dIndex in dstart until dend) {
                    dCount += 1
                }
            } else {
                if (dIndex in dstart until dend) {
                    dCount += 1
                }
                count += 2
            }
        }
        count -= dCount
        if (count > maxLen) {
            return dest.subSequence(0, dIndex - 1)
        }
        //本次需要输入的char数量
        var sIndex = 0
        while (count <= maxLen && sIndex < source.length) {
            val c = source[sIndex++]
            count = if (c <= MAX_CHAR) {
                count + 1
            } else {
                count + 2
            }
        }
        return if (count > maxLen) { //已经存在的char长度, + 输入的char长度, 大于限制长度
            //越界
            sIndex--
            source.subSequence(0, sIndex)
        } else { // keep original
            null
        }
    }
}