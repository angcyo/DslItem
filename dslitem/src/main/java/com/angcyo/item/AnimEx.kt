package com.angcyo.item

import android.animation.*
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Camera
import android.graphics.Matrix
import android.graphics.Rect
import android.os.Build
import android.util.Property
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.*
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.RequiresApi
import androidx.core.animation.addListener
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import com.angcyo.item.Anim.ANIM_DURATION
import com.angcyo.library.animation.YRotateAnimation
import com.angcyo.library.app
import com.angcyo.library.component.MatrixEvaluator
import com.angcyo.library.component.RAnimationListener
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
fun animationOf(context: Context = app(), @AnimRes id: Int): Animation? {
    try {
        if (id == 0 || id == -1) {
            return null
        }
        return AnimationUtils.loadAnimation(context, id)
    } catch (e: Exception) {
        //e.printStackTrace()
        L.w(e.message)
        return null
    }
}

/**从指定资源id中, 加载动画[Animator]*/
fun animatorOf(context: Context = app(), @AnimatorRes id: Int): Animator? {
    try {
        if (id == 0 || id == -1) {
            return null
        }
        return AnimatorInflater.loadAnimator(context, id)
    } catch (e: Exception) {
        //e.printStackTrace()
        L.w(e.message)
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

/**
 * 揭露动画
 * https://developer.android.com/training/animation/reveal-or-hide-view#Reveal
 * */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun View.reveal(action: RevealConfig.() -> Unit = {}) {
    this.doOnPreDraw {
        val config = RevealConfig()
        if (config.centerX == 0) {
            config.centerX = this.measuredWidth / 2
        }
        if (config.centerY == 0) {
            config.centerY = this.measuredHeight / 2
        }
        if (config.endRadius == 0f) {
            config.endRadius = c(config.centerX.toDouble(), config.centerY.toDouble()).toFloat()
        }

        //第一次获取基础数据
        config.action()

        ViewAnimationUtils.createCircularReveal(
            this,
            config.centerX,
            config.centerY,
            config.startRadius,
            config.endRadius
        ).apply {
            duration = config.duration

            config.animator = this
            //第二次获取动画数据
            config.action()
            start()
        }
    }
}

data class RevealConfig(
    var animator: Animator? = null,

    //如果为0, 则默认是视图的中心
    var centerX: Int = 0,
    var centerY: Int = 0,

    //动画开始的半径
    var startRadius: Float = 0f,
    //如果为0, 默认是视图的对角半径
    var endRadius: Float = 0f,

    //动画时长
    var duration: Long = ANIM_DURATION
)

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

/**一组颜色变化的动画*/
fun colorListAnimator(
    colorList: List<Int>,
    infinite: Boolean = false,
    interpolator: Interpolator = LinearInterpolator(),
    duration: Long = colorList.size() * 1000L,
    onEnd: (cancel: Boolean) -> Unit = {},
    config: ValueAnimator.() -> Unit = {},
    onUpdate: (animator: ValueAnimator, color: Int) -> Unit
): ValueAnimator {
    //是否需要反序
    var reverse = false
    val animator = ValueAnimator.ofFloat(0f, 1f)
    animator.addUpdateListener { animation ->
        val section = colorList.size()
        if (section <= 1) {
            onUpdate(animation, colorList[0])
        } else {
            //每一段能运行的时间
            val sectionTime = duration / (section - 1)
            //当前在那一段
            val time = animation.currentPlayTime * 1f % duration //取模调整时间
            val currentStep: Int = (time / sectionTime).floor().toInt()

            //获取需要变化的颜色
            val startColor: Int
            val endColor: Int
            if (reverse) {
                val startIndex = section - currentStep - 1
                endColor = colorList[startIndex]
                startColor = colorList.getOrNull(startIndex - 1) ?: colorList.first()
            } else {
                startColor = colorList[currentStep]
                endColor = colorList.getOrNull(currentStep + 1) ?: colorList.last()
            }

            //当前的进度
            val animatedValue = animation.animatedValue as Float
            val sectionProgress = 1f / (section - 1)
            val currentProgress: Float =
                interpolator.getInterpolation(animatedValue % sectionProgress / sectionProgress)

            onUpdate(animation, evaluateColor(currentProgress, startColor, endColor))
        }
    }
    animator.addListener(object : RAnimatorListener() {

        override fun onAnimationRepeat(animation: Animator) {
            super.onAnimationRepeat(animation)
            if (animator.repeatMode == ValueAnimator.REVERSE) {
                reverse = !reverse
            }
        }

        override fun _onAnimatorFinish(animator: Animator, fromCancel: Boolean) {
            super._onAnimatorFinish(animator, fromCancel)
            onEnd(fromCancel)
        }
    })
    animator.interpolator = LinearInterpolator()
    animator.duration = duration
    if (infinite) {
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
    }
    animator.config()
    animator.start()
    return animator
}

/**背景变化动画*/
fun View.bgColorAnimator(
    fromColor: Int,
    toColor: Int,
    infinite: Boolean = false,
    interpolator: Interpolator = LinearInterpolator(),
    duration: Long = ANIM_DURATION,
    onEnd: (cancel: Boolean) -> Unit = {},
    config: ValueAnimator.() -> Unit = {}
): ValueAnimator {
    //背景动画
    return colorAnimator(
        fromColor,
        toColor,
        infinite,
        interpolator,
        duration,
        onEnd,
        config
    ) { _, color ->
        setBackgroundColor(color)
    }
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
    animate()
        .scaleX(toX)
        .scaleY(toY)
        .setInterpolator(interpolator)
        .setDuration(ANIM_DURATION)
        .withEndAction { onEnd() }
        .start()
}

/**[Matrix]改变动画*/
fun matrixAnimator(
    startMatrix: Matrix,
    endMatrix: Matrix,
    duration: Long = ANIM_DURATION,
    interpolator: Interpolator? = DecelerateInterpolator(),
    finish: (isCancel: Boolean) -> Unit = {},
    block: (Matrix) -> Unit
): ValueAnimator {
    return ObjectAnimator.ofObject(MatrixEvaluator(), startMatrix, endMatrix).apply {
        this.duration = duration
        this.interpolator = interpolator
        this.addUpdateListener {
            block(it.animatedValue as Matrix)
        }
        this.addListener(onEnd = { finish(false) }, onCancel = { finish(true) })
        start()
    }
}

fun matrixAnimatorFraction(
    startMatrix: Matrix,
    endMatrix: Matrix,
    duration: Long = ANIM_DURATION,
    interpolator: Interpolator? = DecelerateInterpolator(),
    block: (matrix: Matrix, fraction: Float) -> Unit
): ValueAnimator {
    return ObjectAnimator.ofObject(MatrixEvaluator(), startMatrix, endMatrix).apply {
        this.duration = duration
        this.interpolator = interpolator
        addUpdateListener {
            block(it.animatedValue as Matrix, it.animatedFraction)
        }
        start()
    }
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

/**clip动画, 从左到右展开显示
 * [com.angcyo.library.ex.AnimEx.clipBoundsAnimator]
 * */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View.clipBoundsAnimatorFromLeft(
    start: Rect = Rect(0, 0, 0, mH()),
    end: Rect = Rect(0, 0, mW(), mH()),
    duration: Long = ANIM_DURATION,
    interpolator: Interpolator = LinearInterpolator(),
    onEndAction: () -> Unit = {}
): ObjectAnimator = clipBoundsAnimator(start, end, duration, interpolator, onEndAction)

/**clip动画, 从右到左隐藏
 * [com.angcyo.library.ex.AnimEx.clipBoundsAnimator]
 * */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View.clipBoundsAnimatorFromRightHide(
    start: Rect = Rect(0, 0, mW(), mH()),
    end: Rect = Rect(0, 0, 0, mH()),
    duration: Long = ANIM_DURATION,
    interpolator: Interpolator = LinearInterpolator(),
    onEndAction: () -> Unit = {}
): ObjectAnimator = clipBoundsAnimator(start, end, duration, interpolator, onEndAction)

/**clip动画
 * [androidx.transition.ChangeClipBounds]
 * */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View.clipBoundsAnimator(
    start: Rect = Rect(mW() / 2, mH() / 2, mW() / 2, mH() / 2),
    end: Rect = Rect(0, 0, mW(), mH()),
    duration: Long = ANIM_DURATION,
    interpolator: Interpolator = LinearInterpolator(),
    onEndAction: () -> Unit = {}
): ObjectAnimator {
    ViewCompat.setClipBounds(this, start)
    val evaluator = RectEvaluator(Rect())
    val animator: ObjectAnimator = ObjectAnimator.ofObject<View, Rect>(
        this, object : Property<View?, Rect>(Rect::class.java, "clipBounds") {
            override fun get(view: View?): Rect? {
                return ViewCompat.getClipBounds(view!!)
            }

            override fun set(view: View?, clipBounds: Rect?) {
                ViewCompat.setClipBounds(view!!, clipBounds)
            }
        },
        evaluator, start, end
    )
    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            ViewCompat.setClipBounds(this@clipBoundsAnimator, null)
            if (end.width() <= 0 || end.height() <= 0) {
                visibility = View.GONE
            }
            onEndAction()
        }
    })
    animator.duration = duration
    animator.interpolator = interpolator
    animator.start()
    setAnimator(animator)
    return animator
}

/**清空属性动画的相关属性*/
fun View.clearAnimatorProperty(
    scale: Boolean = true,
    translation: Boolean = true,
    alpha: Boolean = true
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
    from: Float = 0f,
    to: Float = 180f,
    config: AnimatorConfig.() -> Unit = {
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
    from: Float = 0f,
    to: Float = 180f,
    config: AnimatorConfig.() -> Unit = {
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

/**Y轴旋转动画*/
fun View.rotateYAnimation(
    from: Float = 0f,
    to: Float = 180f,
    config: YRotateAnimation.() -> Unit = {
        duration = 1_000
    }
): Animation {
    val animation = YRotateAnimation()
    animation.from = from
    animation.to = to
    animation.repeatCount = ValueAnimator.INFINITE
    animation.repeatMode = ValueAnimator.REVERSE

    animation.config()

    startAnimation(animation)
    return animation
}

/**无限循环*/
fun Animation.infinite(mode: Int = ValueAnimator.RESTART) {
    //repeatMode = ValueAnimator.REVERSE
    repeatMode = mode
    repeatCount = ValueAnimator.INFINITE
}
