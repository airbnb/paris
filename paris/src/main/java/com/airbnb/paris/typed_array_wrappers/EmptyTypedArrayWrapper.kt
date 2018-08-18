package com.airbnb.paris.typed_array_wrappers

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import com.airbnb.paris.styles.Style

internal object EmptyTypedArrayWrapper : TypedArrayWrapper() {

    override fun getIndexCount(): Int {
        return 0
    }

    override fun getIndex(at: Int): Int {
        throw illegalStateException()
    }

    override fun hasValue(index: Int): Boolean {
        return false
    }

    override fun getBoolean(index: Int): Boolean {
        throw illegalStateException()
    }

    override fun getColor(index: Int): Int {
        throw illegalStateException()
    }

    override fun getColorStateList(index: Int): ColorStateList? {
        throw illegalStateException()
    }

    override fun getDimensionPixelSize(index: Int): Int {
        throw illegalStateException()
    }

    override fun getDrawable(index: Int): Drawable? {
        throw illegalStateException()
    }

    override fun getFloat(index: Int): Float {
        throw illegalStateException()
    }

    override fun getFraction(index: Int, base: Int, pbase: Int): Float {
        throw illegalStateException()
    }

    override fun getFont(index: Int): Typeface? {
        throw illegalStateException()
    }

    override fun getInt(index: Int): Int {
        throw illegalStateException()
    }

    override fun getLayoutDimension(index: Int): Int {
        throw illegalStateException()
    }

    override fun getResourceId(index: Int): Int {
        throw illegalStateException()
    }

    override fun getString(index: Int): String? {
        throw illegalStateException()
    }

    override fun getText(index: Int): CharSequence? {
        throw illegalStateException()
    }

    override fun getTextArray(index: Int): Array<CharSequence>? {
        throw illegalStateException()
    }

    override fun getStyle(index: Int): Style {
        throw illegalStateException()
    }

    override fun recycle() {
        // Nothing to do here
    }
    
    private fun illegalStateException(): Exception {
        return IllegalStateException("This ${TypedArrayWrapper::class.java.simpleName} is empty")
    }
}
