package ru.skillbranch.devintensive.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Color.parseColor
import android.graphics.drawable.Drawable
import android.icu.util.ValueIterator
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.annotation.*
import androidx.annotation.Dimension.DP
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.animation.doOnRepeat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpToPx
import ru.skillbranch.devintensive.extensions.pxToDp
import java.lang.Integer.max
import kotlin.math.truncate
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

        private val bgColors = arrayOf(
            Color.parseColor("#7BC862"),
            Color.parseColor("#E17076"),
            Color.parseColor("#FAA774"),
            Color.parseColor("#6EC9CB"),
            Color.parseColor("#65AADD"),
            Color.parseColor("#A695E7"),
            Color.parseColor("#2196F3")
        )
    }

    @Px
    private var borderWidth: Float = context.dpToPx(DEFAULT_BORDER_WIDTH)
    @ColorInt
    private var borderColor = DEFAULT_BORDER_COLOR
    private var initials: String = "??"

    @ColorInt
    private var bgColor = Color.BLUE
    private var avatarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var initalsPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var viewRect = Rect()
    private var borderRect = Rect()
    private var size = 0

    private var isAvatarMode = true

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, context.dpToPx(DEFAULT_BORDER_WIDTH))
            initials = a.getString(R.styleable.CircleImageView_cv_initials) ?: "??"
            a.recycle()
        }
        scaleType = ScaleType.CENTER_CROP
        setup()
        setOnLongClickListener{
            handleLongClick()
        }

    }

    private fun handleLongClick() : Boolean {
        //Анимация
        val va = ValueAnimator.ofInt(width, width*2).apply {
            duration = 600
            interpolator = FastOutSlowInInterpolator()
            repeatMode = ValueAnimator.REVERSE
            repeatCount = 1
        }
        va.addUpdateListener {
            size = it.animatedValue as Int
            requestLayout()
        }
        va.doOnRepeat { toggleMode() }
        va.start()

        return true
    }

    private fun toggleMode() {
        isAvatarMode = !isAvatarMode
        invalidate()
    }

    private fun setup() {
        with(borderPaint) {
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderColor
        }
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

    fun setBorderColor(@ColorInt color: Int) {
        borderColor = color
        borderPaint.color = borderColor
        invalidate()
    }

    fun setBgColor(@ColorInt color: Int) = this.apply { bgColor = color }

    fun setInitials(initials: String) {
        this.initials = initials
        if (!isAvatarMode) {
            invalidate()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(max(initSize, size), max(initSize, size))
    }

    //Дефолтный размер ширины, в том случае если ширина явна не определена
    private fun resolveDefaultSize(spec: Int): Int = when(MeasureSpec.getMode(spec)) {
        MeasureSpec.UNSPECIFIED -> context.dpToPx(DEFAULT_CIRCLE_SIZE).toInt()
        MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
        MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
        else -> MeasureSpec.getSize(spec)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (isAvatarMode) prepareShader(width, height)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (isAvatarMode) prepareShader(width, height)
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        if (isAvatarMode) prepareShader(width, height)
    }

    override fun onSaveInstanceState(): Parcelable {
        Log.d("M_CircleImageView","onSaveInstanceState $id")
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.isAvatarMode = isAvatarMode
        savedState.borderWidth = borderWidth
        savedState.borderColor = borderColor
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.d("M_CircleImageView","onSaveInstanceState $id")
        if (state is SavedState) {
            super.onRestoreInstanceState(state)
            isAvatarMode = state. isAvatarMode
            borderWidth = state.borderWidth
            borderColor = state.borderColor

            with(borderPaint) {
                color = borderColor
                strokeWidth = borderWidth
            }
        } else {
            super.onRestoreInstanceState(state)
        }
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
        if (drawable != null && isAvatarMode) drawAvatar(canvas)
        else drawInitials(canvas)
        drawBorder(canvas)
    }

    private fun prepareShader(w: Int, h: Int) {
        if (w == 0 || drawable == null) return
        val srcBitmap = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)
        avatarPaint.shader = BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    private fun drawAvatar (canvas: Canvas) {
        canvas.drawOval(viewRect.toRectF(), avatarPaint)
    }


    private fun drawBorder(canvas: Canvas) {
        with(borderPaint) {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
        }
        val half = (borderWidth / 2).toInt()
        borderRect.set(viewRect)
        borderRect.inset(half, half)
        canvas.drawOval(borderRect.toRectF(), borderPaint)
    }

    private fun drawInitials(canvas: Canvas) {
        initalsPaint.color = initialsToColor(initials)
        canvas.drawOval(viewRect.toRectF(), initalsPaint)

        with(initalsPaint) {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = height * 0.33f
        }

        val offsetY = (initalsPaint.descent() + initalsPaint.ascent()) / 2
        canvas.drawText(initials, viewRect.exactCenterX(), viewRect.exactCenterY() - offsetY, initalsPaint)
    }

    private fun initialsToColor(letters: String): Int {
        val b: Byte = letters[0].toByte()
        val len: Int = bgColors.size
        val d: Double = b/len.toDouble()
        val index: Int = ((d - truncate(d))*len).toInt()
        return bgColors[index]
    }

    private class SavedState : BaseSavedState, Parcelable {

        var isAvatarMode: Boolean = true
        var borderWidth: Float = 0f
        var borderColor: Int = 0

        constructor(superState: Parcelable?) : super(superState)

        constructor(src: Parcel) : super(src){
            //restore state from parcel
            isAvatarMode = src.readInt() == 1
            borderWidth = src.readFloat()
            borderColor = src.readInt()
        }

        override fun writeToParcel(dst: Parcel, flags: Int) {
            //write state to Parcel
            super.writeToParcel(dst, flags)
            dst.writeInt(if (isAvatarMode) 1 else 0)
            dst.writeFloat(borderWidth)
            dst.writeInt(borderColor)
        }

        override fun describeContents() = 0

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }

    }

}