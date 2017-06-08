package com.airbnb.paris

import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.TypedValue
import android.widget.TextView

class TextViewStyleApplier(view: TextView) : StyleApplier<TextViewStyleApplier, TextView>(view) {

    override fun attributes(): IntArray {
        return R.styleable.Paris_TextView
    }

    override fun applyParent(style: Style) {
        ViewStyleApplier(view).apply(style)
    }

    override fun processAttributes(style: Style, a: TypedArrayWrapper) {
        var drawableLeft: Drawable? = null
        var drawableTop: Drawable? = null
        var drawableRight: Drawable? = null
        var drawableBottom: Drawable? = null
        if (a.hasValue(R.styleable.Paris_TextView_android_drawableLeft)) {
            drawableLeft = StyleUtils.getDrawable(view.context, a, R.styleable.Paris_TextView_android_drawableLeft);
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_drawableTop)) {
            drawableTop = StyleUtils.getDrawable(view.context, a, R.styleable.Paris_TextView_android_drawableTop);
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_drawableRight)) {
            drawableRight = StyleUtils.getDrawable(view.context, a, R.styleable.Paris_TextView_android_drawableRight);
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_drawableBottom)) {
            drawableBottom = StyleUtils.getDrawable(view.context, a, R.styleable.Paris_TextView_android_drawableBottom);
        }
        if (drawableLeft != null || drawableTop != null || drawableRight != null || drawableBottom != null) {
            val drawables = view.compoundDrawables
            view.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft ?: drawables[0],
                    drawableTop ?: drawables[1],
                    drawableRight ?: drawables[2],
                    drawableBottom ?: drawables[3])
        }

        if (a.hasValue(R.styleable.Paris_TextView_android_ellipsize)) {
            setEllipsize(view, a.getInt(R.styleable.Paris_TextView_android_ellipsize, -1))
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_gravity)) {
            view.gravity = a.getInt(R.styleable.Paris_TextView_android_gravity, -1)
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_letterSpacing)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.letterSpacing = a.getFloat(R.styleable.Paris_TextView_android_letterSpacing, 0f)
            }
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_lines)) {
            view.setLines(a.getInt(R.styleable.Paris_TextView_android_lines, -1))
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_lineSpacingExtra)) {
            view.setLineSpacing(a.getDimensionPixelSize(R.styleable.Paris_TextView_android_lineSpacingExtra, 0).toFloat(), view.lineSpacingMultiplier)
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_lineSpacingMultiplier)) {
            view.setLineSpacing(view.lineSpacingExtra, a.getFloat(R.styleable.Paris_TextView_android_lineSpacingMultiplier, 1f))
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_maxLines)) {
            view.maxLines = a.getInt(R.styleable.Paris_TextView_android_maxLines, -1)
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_minLines)) {
            view.minLines = a.getInt(R.styleable.Paris_TextView_android_minLines, -1)
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_minWidth)) {
            view.minWidth = a.getDimensionPixelSize(R.styleable.Paris_TextView_android_minWidth, -1)
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_singleLine)) {
            view.setSingleLine(a.getBoolean(R.styleable.Paris_TextView_android_singleLine, false))
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_textAllCaps)) {
            view.setAllCaps(a.getBoolean(R.styleable.Paris_TextView_android_textAllCaps, false))
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_textColor)) {
            view.setTextColor(a.getColorStateList(R.styleable.Paris_TextView_android_textColor))
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_textColorHint)) {
            view.setHintTextColor(a.getColorStateList(R.styleable.Paris_TextView_android_textColorHint))
        }
        if (a.hasValue(R.styleable.Paris_TextView_android_textSize)) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimensionPixelSize(R.styleable.Paris_TextView_android_textSize, 0).toFloat())
        }
    }

    private fun setEllipsize(view: TextView, value: Int) {
        view.ellipsize = when (value) {
            1 -> TextUtils.TruncateAt.START
            2 -> TextUtils.TruncateAt.MIDDLE
            3 -> TextUtils.TruncateAt.END
            4 -> TextUtils.TruncateAt.MARQUEE
            else -> throw IllegalStateException("Wrong value for ellipsize")
        }
    }
}