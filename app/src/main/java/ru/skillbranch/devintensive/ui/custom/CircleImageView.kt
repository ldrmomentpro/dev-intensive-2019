package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.Color.parseColor
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpToPx
import ru.skillbranch.devintensive.extensions.pxToDp
import android.graphics.Paint as Paint

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    companion object{
        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_CIRCLE_SIZE = 112
    }

    @Px
    private var borderWidth: Float = context.dpToPx(DEFAULT_BORDER_WIDTH)
    @ColorInt
    private var borderColor = DEFAULT_BORDER_COLOR
    private var initials: String = ""
    private var circleSize = DEFAULT_CIRCLE_SIZE
    @ColorInt
    private var bgColor = Color.BLUE
    private var avatarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var initalsPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var viewRect = Rect()

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, context.dpToPx(DEFAULT_BORDER_WIDTH))
            initials = a.getString(R.styleable.CircleImageView_cv_initials) ?: ""
            a.recycle()
        }
        scaleType = ScaleType.CENTER_CROP
    }

    @Dimension(unit = DP)
    fun getBorderWidth():Int = context.pxToDp(borderWidth)
    fun setBorderWith(@Dimension(unit = DP) dp: Int) {
        borderWidth = context.dpToPx(dp)
        borderPaint.strokeWidth = borderWidth
        invalidate()
    }

    @ColorInt
    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        borderPaint.color = borderColor
        this.invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = colorId
        borderPaint.color = borderColor
        invalidate()
    }

    fun setBgColor(@ColorInt color: Int) = this.apply { bgColor = color }

    fun setInitials(initials: String) {
        this.initials = initials
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(initSize,initSize)
    }

    private fun resolveDefaultSize(spec: Int): Int = when(MeasureSpec.getMode(spec)) {
        MeasureSpec.UNSPECIFIED -> context.dpToPx(DEFAULT_CIRCLE_SIZE).toInt()
        MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
        MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
        else -> MeasureSpec.getSize(spec)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (w == 0) return
        with(viewRect) {
            left = 0
            right = w
            top = 0
            bottom = h
        }
        prepareShader(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (drawable != null) drawAvatar(canvas)
        else drawInitials(canvas)
        drawBorder(canvas)
    }

    private fun prepareShader(w: Int, h: Int) {
        if (w == 0 || drawable == null) return
        val srcBitmap = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)
        avatarPaint.shader = BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    private fun drawAvatar (canvas: Canvas) {
        canvas.drawOval(viewRect.toRectF(), initalsPaint)
    }

    private fun drawBorder(canvas: Canvas) {
        with(borderPaint) {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
        }
        val half = (borderWidth / 2).toInt()
        viewRect.inset(half, half)
        canvas.drawOval(viewRect.toRectF(), borderPaint)
    }

    private fun drawInitials(canvas: Canvas) {
        initalsPaint.color = bgColor
        canvas.drawOval(viewRect.toRectF(), initalsPaint)

        with(initalsPaint) {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = height * 0.33f
        }

        val offsetY = (initalsPaint.descent() + initalsPaint.ascent()) / 2
        canvas.drawText(initials, viewRect.exactCenterX(), viewRect.exactCenterY() - offsetY, initalsPaint)
    }
}