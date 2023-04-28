package com.angcyo.item.style

import android.view.View
import android.widget.ImageView

/**
 * 图片控件样式
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/09/23
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class ImageStyleConfig : ViewStyleConfig() {

    var imageScaleType: ImageView.ScaleType? = null

    override fun updateStyle(view: View) {
        super.updateStyle(view)

        if (view is ImageView) {
            //
            if (imageScaleType == null) {
                imageScaleType = view.scaleType
            }
            view.scaleType = imageScaleType

            //
        }
    }
}