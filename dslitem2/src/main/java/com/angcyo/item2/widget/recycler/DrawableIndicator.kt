package com.angcyo.item2.widget.recycler

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import com.angcyo.dpi
import com.angcyo.item.anim
import com.angcyo.item.drawHeight
import com.angcyo.item.getMode
import com.angcyo.item.getSize
import com.angcyo.item.loadDrawable
import com.angcyo.item2.R
import kotlin.math.max

/**
 * 2种Drawable切换的指示器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/18
 */
class DrawableIndicator(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    /**影响宽高*/
    var drawableSize: Int by RequestLayoutProperty(6 * dpi)

    /**影响宽度*/
    var drawableSizeFocus: Int by RequestLayoutProperty(12 * dpi)

    var indicatorDrawable: Drawable? = loadDrawable(R.drawable.lib_indicator_normal)
    var indicatorDrawableFocus: Drawable? = loadDrawable(R.drawable.lib_indicator_focus)

    /**指示器数量*/
    var indicatorCount: Int by RequestLayoutProperty(0)

    /**当前显示*/
    var indicatorIndex: Int by InvalidateProperty(0)

    /**间隙*/
    var indicatorSpace: Int by RequestLayoutProperty(4 * dpi)

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DrawableIndicator)

        if (typedArray.hasValue(R.styleable.DrawableIndicator_r_indicator_drawable)) {
            indicatorDrawable =
                typedArray.getDrawable(R.styleable.DrawableIndicator_r_indicator_drawable)
        }
        if (typedArray.hasValue(R.styleable.DrawableIndicator_r_indicator_drawable_focus)) {
            indicatorDrawableFocus =
                typedArray.getDrawable(R.styleable.DrawableIndicator_r_indicator_drawable_focus)
        }

        drawableSize = typedArray.getDimensionPixelOffset(
            R.styleable.DrawableIndicator_r_indicator_size,
            drawableSize
        )
        drawableSizeFocus = typedArray.getDimensionPixelOffset(
            R.styleable.DrawableIndicator_r_indicator_size_focus,
            drawableSizeFocus
        )
        indicatorSpace = typedArray.getDimensionPixelOffset(
            R.styleable.DrawableIndicator_r_indicator_space,
            indicatorSpace
        )
        typedArray.recycle()

        if (isInEditMode) {
            indicatorCount = 5
            indicatorIndex = 2
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSize = widthMeasureSpec.getSize()
        var heightSize = heightMeasureSpec.getSize()
        if (widthMeasureSpec.getMode() != MeasureSpec.EXACTLY) {
            widthSize = paddingLeft + paddingRight +
                    drawableSize * (indicatorCount - 1) + drawableSizeFocus +
                    indicatorSpace * (indicatorCount - 1)
        }
        if (heightMeasureSpec.getMode() != MeasureSpec.EXACTLY) {
            heightSize = paddingTop + paddingBottom + max(drawableSize, drawableSizeFocus)
        }
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            max(widthSize, suggestedMinimumWidth),
            max(heightSize, suggestedMinimumHeight)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (indicatorCount > 1) {
            //大于1个时, 才绘制
            var left = paddingLeft
            var top = paddingTop
            for (i in 0 until indicatorCount) {
                val width: Int = when {
                    _animator?.isStarted == true && i == indicatorIndex -> (drawableSize - (drawableSize - drawableSizeFocus) * _animatorFraction).toInt()
                    _animator?.isStarted == true && i == _animatorFromIndex -> (drawableSizeFocus - (drawableSizeFocus - drawableSize) * _animatorFraction).toInt()
                    i == indicatorIndex -> drawableSizeFocus
                    else -> drawableSize
                }

                val height = drawableSize

                top = paddingTop + (drawHeight - height) / 2

                val drawable = if (i == indicatorIndex) {
                    indicatorDrawableFocus
                } else {
                    indicatorDrawable
                }
                drawable?.apply {
                    val right = left + width
                    setBounds(left, top, right, top + height)
                    left = right + indicatorSpace
                    draw(canvas)
                }
            }
        }
    }

    var _animator: ValueAnimator? = null
    var _animatorFromIndex: Int = -1
    var _animatorFraction: Float = 1f

    /**使用动画的方式切换*/
    fun animatorToIndex(index: Int) {
        _animator?.cancel()
        _animator = null

        _animatorFromIndex = indicatorIndex
        indicatorIndex = index

        if (_animatorFromIndex == indicatorIndex) {
            return
        }

        _animator = anim(0f, 1f) {
            onAnimatorUpdateValue = { value, fraction ->
                _animatorFraction = fraction
                postInvalidateOnAnimation()
            }
        }
    }
}