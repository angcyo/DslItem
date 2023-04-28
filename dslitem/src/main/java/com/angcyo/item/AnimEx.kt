package com.angcyo.item

import android.animation.*
import android.content.Context
import android.graphics.Camera
import android.graphics.Matrix
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.animation.*
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.RequiresApi
import com.angcyo.dsladapter.mH
import com.angcyo.dsladapter.mW
import com.angcyo.item.Anim.ANIM_DURATION
import com.angcyo.item.base.LibInitProvider
import com.angcyo.item.base.RAnimationListener
import java.lang.ref.WeakReference

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/20
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

object Anim {
    /**动画默认时长*/
    var ANIM_DURATION = 300L
}

/**从指定资源id中, 加载动画[Animation]*/
fun animationOf(context: Context = LibInitProvider.contentProvider, @AnimRes id: Int): Animation? {
    try {
        if (id == 0 || id == -1) {
            return null
        }
        return AnimationUtils.loadAnimation(context, id)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

/**从指定资源id中, 加载动画[Animator]*/
fun animatorOf(
    context: Context = LibInitProvider.contentProvider, @AnimatorRes id: Int
): Animator? {
    try {
        if (id == 0 || id == -1) {
            return null
        }
        return AnimatorInflater.loadAnimator(context, id)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun anim(from: Int, to: Int, config: AnimatorConfig.() -> Unit = {}): ValueAnimator {
    return _animator(ValueAnimator.ofInt(from, to), config)
}

fun anim(from: Float, to: Float, config: AnimatorConfig.() -> Unit = {}): ValueAnimator {
    return _animator(ValueAnimator.ofFloat(from, to), config)
}

fun _animator(animator: ValueAnimator, config: AnimatorConfig.() -> Unit = {}): ValueAnimator {
    val animatorConfig = AnimatorConfig()

    animator.duration = ANIM_DURATION
    animator.interpolator = LinearInterpolator()
    animator.addUpdateListener {
        animatorConfig.onAnimatorUpdateValue(it.animatedValue, it.animatedFraction)
    }
    animator.addListener(RAnimatorListener().apply {
        onAnimatorFinish = { _, _ ->
            animatorConfig.onAnimatorEnd(animator)
        }
    })

    animatorConfig.config()
    animatorConfig.onAnimatorConfig(animator)

    animator.start()
    return animator
}

class AnimatorConfig {

    /**动画时长*/
    var animatorDuration = ANIM_DURATION

    /**配置动画, 比如时长, 差值器等*/
    var onAnimatorConfig: (animator: ValueAnimator) -> Unit = {
        it.duration = animatorDuration
    }

    /**动画值改变的监听*/
    var onAnimatorUpdateValue: (value: Any, fraction: Float) -> Unit = { _, _ -> }

    /**动画结束的监听*/
    var onAnimatorEnd: (animator: ValueAnimator) -> Unit = {}
}

/**缩放属性动画*/
fun View.scale(
    from: Float,
    to: Float,
    duration: Long = ANIM_DURATION,
    interpolator: Interpolator = LinearInterpolator(),
    onEnd: () -> Unit = {}
): ValueAnimator {
    return anim(from, to) {
        onAnimatorUpdateValue = { value, _ ->
            scaleX = value as Float
            scaleY = scaleX
        }

        onAnimatorConfig = {
            it.duration = duration
            it.interpolator = interpolator
            onAnimatorEnd = { _ -> onEnd() }
        }
    }
}

/**平移属性动画*/
fun View.translationX(
    from: Float,
    to: Float,
    duration: Long = ANIM_DURATION,
    interpolator: Interpolator = LinearInterpolator(),
    onEnd: () -> Unit = {}
): ValueAnimator {
    return anim(from, to) {
        onAnimatorUpdateValue = { value, _ ->
            translationX = value as Float
        }

        onAnimatorConfig = {
            it.duration = duration
            it.interpolator = interpolator
            onAnimatorEnd = { _ -> onEnd() }
        }
    }
}

fun View.translationY(
    from: Float,
    to: Float,
    duration: Long = ANIM_DURATION,
    interpolator: Interpolator = LinearInterpolator(),
    onEnd: () -> Unit = {}
): ValueAnimator {
    return anim(from, to) {
        onAnimatorUpdateValue = { value, _ ->
            translationY = value as Float
        }

        onAnimatorConfig = {
            it.duration = duration
            it.interpolator = interpolator
            onAnimatorEnd = { _ -> onEnd() }
        }
    }
}

/**补间动画*/
fun View.rotateAnimation(
    fromDegrees: Float = 0f,
    toDegrees: Float = 360f,
    duration: Long = ANIM_DURATION,
    interpolator: Interpolator = LinearInterpolator(),
    config: RotateAnimation.() -> Unit = {},
    onEnd: (animation: Animation) -> Unit = {}
): RotateAnimation {
    return RotateAnimation(
        fromDegrees,
        toDegrees,
        RotateAnimation.RELATIVE_TO_SELF,
        0.5f,
        RotateAnimation.RELATIVE_TO_SELF,
        0.5f
    ).apply {
        this.duration = duration
        this.interpolator = interpolator
        setAnimationListener(object : RAnimationListener() {
            override fun onAnimationEnd(animation: Animation) {
                onEnd(animation)
            }
        })
        config()
        this@rotateAnimation.startAnimation(this)
    }
}

/**动画结束的回调*/
fun Animation.onAnimationEnd(onEnd: (animation: Animation) -> Unit = {}) {
    setAnimationListener(object : RAnimationListener() {
        override fun onAnimationEnd(animation: Animation) {
            onEnd(animation)
        }
    })
}

/**颜色渐变动画*/
fun colorAnimator(
    fromColor: Int,
    toColor: Int,
    infinite: Boolean = false,
    interpolator: Interpolator = LinearInterpolator(),
    duration: Long = ANIM_DURATION,
    onEnd: (cancel: Boolean) -> Unit = {},
    config: ValueAnimator.() -> Unit = {},
    onUpdate: (animator: ValueAnimator, color: Int) -> Unit
): ValueAnimator {
    //颜色动画
    val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
    colorAnimator.addUpdateListener { animation ->
        val color = animation.animatedValue as Int
        onUpdate(animation, color)
    }
    colorAnimator.addListener(object : RAnimatorListener() {
        override fun _onAnimatorFinish(animator: Animator, fromCancel: Boolean) {
            super._onAnimatorFinish(animator, fromCancel)
            onEnd(fromCancel)
        }
    })
    colorAnimator.interpolator = interpolator
    colorAnimator.duration = duration
    if (infinite) {
        colorAnimator.repeatCount = ValueAnimator.INFINITE
        colorAnimator.repeatMode = ValueAnimator.REVERSE
    }
    colorAnimator.config()
    colorAnimator.start()
    return colorAnimator
}

/**
 * 抖动 放大缩小
 */
fun View.scaleAnimator(
    from: Float = 0.5f,
    to: Float = 1f,
    interpolator: Interpolator = BounceInterpolator(),
    onEnd: () -> Unit = {}
) {
    scaleAnimator(from, from, to, to, interpolator, onEnd)
}

fun View.scaleAnimator(
    fromX: Float = 0.5f,
    fromY: Float = 0.5f,
    toX: Float = 1f,
    toY: Float = 1f,
    interpolator: Interpolator = BounceInterpolator(),
    onEnd: () -> Unit = {}
) {
    scaleX = fromX
    scaleY = fromY
    animate().scaleX(toX).scaleY(toY).setInterpolator(interpolator).setDuration(ANIM_DURATION)
        .withEndAction { onEnd() }.start()
}

/**[Rect]动画*/
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
fun rectAnimator(
    startRect: Rect,
    endRect: Rect,
    duration: Long = ANIM_DURATION,
    interpolator: Interpolator? = DecelerateInterpolator(),
    block: (Rect) -> Unit
): ValueAnimator {
    return ObjectAnimator.ofObject(RectEvaluator(), startRect, endRect).apply {
        this.duration = duration
        this.interpolator = interpolator
        addUpdateListener {
            block(it.animatedValue as Rect)
        }
        start()
    }
}

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
fun rectAnimatorFraction(
    startRect: Rect,
    endRect: Rect,
    duration: Long = ANIM_DURATION,
    interpolator: Interpolator? = DecelerateInterpolator(),
    block: (rect: Rect, fraction: Float) -> Unit
): ValueAnimator {
    return ObjectAnimator.ofObject(RectEvaluator(), startRect, endRect).apply {
        this.duration = duration
        this.interpolator = interpolator
        addUpdateListener {
            block(it.animatedValue as Rect, it.animatedFraction)
        }
        start()
    }
}

/**清空属性动画的相关属性*/
fun View.clearAnimatorProperty(
    scale: Boolean = true, translation: Boolean = true, alpha: Boolean = true
) {
    if (scale) {
        scaleX = 1f
        scaleY = 1f
    }

    if (translation) {
        translationX = 0f
        translationY = 0f
    }

    if (alpha) {
        this.alpha = 1f
    }
}

fun View.setAnimator(animator: Animator) {
    setTag(R.id.lib_tag_animator, WeakReference(animator))
}

/**取消动画[Animator] [Animation] [Animate]*/
fun View.cancelAnimator() {
    val tag = getTag(R.id.lib_tag_animator)
    var animator: Animator? = null
    if (tag is WeakReference<*>) {
        val any = tag.get()
        if (any is Animator) {
            animator = any
        }
    } else if (tag is Animator) {
        animator = tag
    }
    animator?.cancel()

    //animation
    clearAnimation()
    //animate
    animate().cancel()
}

/**[Camera]*/
fun rotateCameraAnimator(
    from: Float = 0f,
    to: Float = 180f,
    config: AnimatorConfig.() -> Unit = {},
    action: Camera.(value: Float) -> Unit
): ValueAnimator {
    val animatorConfig = AnimatorConfig()
    animatorConfig.config()
    return anim(from, to) {
        onAnimatorConfig = {
            it.repeatCount = ValueAnimator.INFINITE
            it.repeatMode = ValueAnimator.REVERSE

            animatorConfig.onAnimatorConfig(it)
        }

        onAnimatorUpdateValue = { value, fraction ->
            animatorConfig.onAnimatorUpdateValue(value, fraction)
            val camera = Camera()
            action(camera, value as Float)
        }

        onAnimatorEnd = {
            animatorConfig.onAnimatorEnd(it)
        }
    }
}

/**
 * x轴旋转角度的动画
 * [from] 从多少角度
 * [to] 到多少角度
 * */
fun View.rotateXAnimator(
    from: Float = 0f, to: Float = 180f, config: AnimatorConfig.() -> Unit = {
        animatorDuration = 1_000
    }
): ValueAnimator {
    return rotateCameraAnimator(from, to, config) { value ->
        rotateX(value)
        val matrix = Matrix()
        getMatrix(matrix)
        val centerX = mW() / 2f
        val centerY = mH() / 2f
        matrix.preTranslate(-centerX, -centerY)
        matrix.postTranslate(centerX, centerY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            animationMatrix = matrix
        }
    }
}

/**Y轴旋转动画*/
fun View.rotateYAnimator(
    from: Float = 0f, to: Float = 180f, config: AnimatorConfig.() -> Unit = {
        animatorDuration = 1_000
    }
): ValueAnimator {
    return rotateCameraAnimator(from, to, config) { value ->
        rotateY(value)
        val matrix = Matrix()
        getMatrix(matrix)
        val centerX = mW() / 2f
        val centerY = mH() / 2f
        matrix.preTranslate(-centerX, -centerY)
        matrix.postTranslate(centerX, centerY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            animationMatrix = matrix
        }
    }
}

/**无限循环*/
fun Animation.infinite(mode: Int = ValueAnimator.RESTART) {
    //repeatMode = ValueAnimator.REVERSE
    repeatMode = mode
    repeatCount = ValueAnimator.INFINITE
}
