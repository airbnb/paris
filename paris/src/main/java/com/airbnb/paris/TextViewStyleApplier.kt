package com.airbnb.paris

import android.os.Build
import android.text.TextUtils
import android.util.TypedValue
import android.widget.TextView

open class TextViewStyleApplier(view: TextView) : StyleApplier<TextView>(view) {

    override fun attributes(): IntArray {
        return R.styleable.TextView
    }

    override fun applyParents(style: Style) {
        ViewStyleApplier(view).apply(style)
    }

    override fun processAttribute(style: Style, a: TypedArrayWrapper, index: Int) {
        if (index == R.styleable.TextView_android_ellipsize) {
            setEllipsize(view, a.getInt(index, -1))
        } else if (index == R.styleable.TextView_android_gravity) {
            view.gravity = a.getInt(index, -1)
        } else if (index == R.styleable.TextView_android_letterSpacing) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.letterSpacing = a.getFloat(index, 0f)
            }
        } else if (index == R.styleable.TextView_android_lineSpacingExtra) {
            view.setLineSpacing(a.getDimensionPixelSize(index, 0).toFloat(), view.lineSpacingMultiplier)
        } else if (index == R.styleable.TextView_android_lineSpacingMultiplier) {
            view.setLineSpacing(view.lineSpacingExtra, a.getFloat(index, 1f))
        } else if (index == R.styleable.TextView_android_maxLines) {
            view.maxLines = a.getInt(index, -1)
        } else if (index == R.styleable.TextView_android_minWidth) {
            view.minWidth = a.getDimensionPixelSize(index, -1)
        } else if (index == R.styleable.TextView_android_textAllCaps) {
            view.setAllCaps(a.getBoolean(index, false))
        } else if (index == R.styleable.TextView_android_textColor) {
            view.setTextColor(a.getColorStateList(index))
        } else if (index == R.styleable.TextView_android_textSize) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimensionPixelSize(index, 0).toFloat())
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