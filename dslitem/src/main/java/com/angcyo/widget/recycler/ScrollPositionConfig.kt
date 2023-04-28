package com.angcyo.widget.recycler

import androidx.recyclerview.widget.RecyclerView

/**
 * 记录[RecyclerView]的滚动位置状态
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/19
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

data class ScrollPositionConfig(
    //位置
    var adapterPosition: Int = RecyclerView.NO_POSITION,

    //上下距离
    var left: Int = 0,
    var top: Int = 0
)