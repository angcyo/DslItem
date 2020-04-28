package com.angcyo.widget.edit

import android.text.Editable
import android.text.TextWatcher

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/06/06
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class SingleTextWatcher : TextWatcher {
    override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(editable: Editable?) {}
}