package com.angcyo.item

import android.animation.Animator

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/31
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class RAnimatorListener : Animator.AnimatorListener {

    private var isCancel = false

    var onAnimatorFinish: (animator: Animator, fromCancel: Boolean) -> Unit =
        { animator, fromCancel ->
            _onAnimatorFinish(animator, fromCancel)
        }

    override fun onAnimationRepeat(animation: Animator) {
    }

    override fun onAnimationEnd(animation: Animator) {
        if (isCancel) {
            //当动画被取消的时候, 系统会回调onAnimationCancel, 然后 onAnimationEnd
            //所以, 这里过滤一下
        } else {
            onAnimatorFinish(animation, false)
        }
    }

    override fun onAnimationCancel(animation: Animator) {
        isCancel = true
        onAnimatorFinish(animation, true)
    }

    override fun onAnimationStart(animation: Animator) {
        isCancel = false
    }

    open fun _onAnimatorFinish(animator: Animator, fromCancel: Boolean) {

    }

}