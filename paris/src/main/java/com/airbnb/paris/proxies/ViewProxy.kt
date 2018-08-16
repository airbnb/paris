package com.airbnb.paris.proxies

import android.animation.AnimatorInflater
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.AnyRes
import android.support.annotation.Px
import android.support.annotation.RequiresApi
import android.support.v4.view.ViewCompat
import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.airbnb.paris.R2
import com.airbnb.paris.annotations.AfterStyle
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.LayoutDimension
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.styles.Style
import com.airbnb.paris.utils.*

/**
 * The order of the methods in a styleable class dictates the order in which attributes are applied. This class relies on this fact to enforce the
 * precedence of paddings. It's unorthodox but it simplifies the logic quite a bit. DO NOT RELY ON THIS UNDOCUMENTED FEATURE IF YOUR PROJECT IMPORTS
 * THIS LIBRARY.
 */
@Styleable("Paris_View")
class ViewProxy(view: View) : BaseProxy<ViewProxy, View>(view) {

    private var ignoreLayoutWidthAndHeight: Boolean = false
    private var width: Int? = null
    private var height: Int? = null
    private var margin: Int? = null
    private var marginBottom: Int? = null
    private var marginEnd: Int? = null
    private var marginLeft: Int? = null
    private var marginRight: Int? = null
    private var marginStart: Int? = null
    private var marginTop: Int? = null

    @AfterStyle
    fun afterStyle(@Suppress("UNUSED_PARAMETER") style: Style?) {
        val isMarginSet = listOf(margin, marginBottom, marginEnd, marginLeft, marginRight, marginStart, marginTop).any { it != null }

        if (!ignoreLayoutWidthAndHeight) {
            if ((width != null) xor (height != null)) {
                throw IllegalArgumentException("Width and height must either both be set, or not be set at all. It can't be one and not the other.")
            }

            val width = width
            val height = height
            if (width != null && height != null) {
                var params: LayoutParams? = view.layoutParams
                if (params == null) {
                    params = if (isMarginSet) MarginLayoutParams(width, height) else LayoutParams(width, height)
                } else {
                    params.width = width
                    params.height = height
                }
                view.layoutParams = params
            }
        }

        if (isMarginSet) {
            val marginParams: MarginLayoutParams
            if (view.layoutParams != null) {
                marginParams = view.layoutParams as MarginLayoutParams
            } else {

                marginParams = MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    marginParams.layoutDirection = view.layoutDirection
                }
            }

            val margin = margin
            if (margin != null) {
                marginParams.setMargins(margin, margin, margin, margin)
            } else {
                marginBottom?.let { marginParams.bottomMargin = it }
                marginLeft?.let { marginParams.leftMargin = it }
                marginRight?.let { marginParams.rightMargin = it }
                marginTop?.let { marginParams.topMargin = it }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    // Note: setting negatives marginEnd or marginStart doesn't work (the view resets them to 0)
                    marginEnd?.let { marginParams.marginEnd = it }
                    marginStart?.let { marginParams.marginStart = it }
                }
            }
            view.layoutParams = marginParams
        }

        ignoreLayoutWidthAndHeight = false
        width = null
        height = null
        margin = null
        marginBottom = null
        marginEnd = null
        marginLeft = null
        marginRight = null
        marginStart = null
        marginTop = null
    }

    @Attr(R2.styleable.Paris_View_android_layout_width)
    fun setLayoutWidth(@LayoutDimension width: Int) {
        this.width = width
    }

    @Attr(R2.styleable.Paris_View_android_layout_height)
    fun setLayoutHeight(@LayoutDimension height: Int) {
        this.height = height
    }

    @Attr(R2.styleable.Paris_View_android_layout_gravity)
    fun setLayoutGravity(gravity: Int) {
        val params = view.layoutParams
        if (params != null) {
            if (params is FrameLayout.LayoutParams) {
                params.gravity = gravity
            } else if (params is LinearLayout.LayoutParams) {
                params.gravity = gravity
            }
            view.layoutParams = params
        }
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginBottom)
    fun setLayoutMarginBottom(@Px marginBottom: Int) {
        this.marginBottom = marginBottom
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginLeft)
    fun setLayoutMarginLeft(@Px marginLeft: Int) {
        this.marginLeft = marginLeft
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginRight)
    fun setLayoutMarginRight(@Px marginRight: Int) {
        this.marginRight = marginRight
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginTop)
    fun setLayoutMarginTop(@Px marginTop: Int) {
        this.marginTop = marginTop
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginEnd)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setLayoutMarginEnd(@Px marginEnd: Int) {
        this.marginEnd = marginEnd
    }

    @Attr(R2.styleable.Paris_View_android_layout_marginStart)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setLayoutMarginStart(@Px marginStart: Int) {
        this.marginStart = marginStart
    }

    @Attr(R2.styleable.Paris_View_android_layout_margin)
    fun setLayoutMargin(@Px margin: Int) {
        this.margin = margin
    }

    @Attr(R2.styleable.Paris_View_android_alpha)
    fun setAlpha(alpha: Float) {
        view.alpha = alpha
    }

    @Attr(R2.styleable.Paris_View_android_background)
    fun setBackground(drawable: Drawable?) {
        view.background = drawable
    }

    @Attr(R2.styleable.Paris_View_android_backgroundTint)
    fun setBackgroundTint(colorStateList: ColorStateList?) {
        ViewCompat.setBackgroundTintList(view, colorStateList)
    }

    @Attr(R2.styleable.Paris_View_android_backgroundTintMode)
    fun setBackgroundTintMode(tintMode: Int) {
        ViewCompat.setBackgroundTintMode(view, parseTintMode(tintMode))
    }

    private fun parseTintMode(value: Int): PorterDuff.Mode? {
        return when (value) {
            PORTERDUFF_MODE_SRC_OVER -> PorterDuff.Mode.SRC_OVER
            PORTERDUFF_MODE_SRC_IN -> PorterDuff.Mode.SRC_IN
            PORTERDUFF_MODE_SRC_ATOP -> PorterDuff.Mode.SRC_ATOP
            PORTERDUFF_MODE_MULTIPLY -> PorterDuff.Mode.MULTIPLY
            PORTERDUFF_MODE_SCREEN -> PorterDuff.Mode.SCREEN
            PORTERDUFF_MODE_ADD -> PorterDuff.Mode.ADD
            else -> null
        }
    }


    @Attr(R2.styleable.Paris_View_android_contentDescription)
    fun setContentDescription(contentDescription: CharSequence?) {
        view.contentDescription = contentDescription
    }

    @Attr(R2.styleable.Paris_View_android_elevation)
    fun setElevation(@Px elevation: Int) {
        ViewCompat.setElevation(view, elevation.toFloat())
    }

    @Attr(R2.styleable.Paris_View_android_foreground)
    fun setForeground(drawable: Drawable?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.foreground = drawable
        }
    }

    @Attr(R2.styleable.Paris_View_android_minHeight)
    fun setMinHeight(@Px minHeight: Int) {
        view.minimumHeight = minHeight
    }

    @Attr(R2.styleable.Paris_View_android_minWidth)
    fun setMinWidth(@Px minWidth: Int) {
        view.minimumWidth = minWidth
    }

    @Attr(R2.styleable.Paris_View_android_paddingBottom)
    fun setPaddingBottom(@Px padding: Int) {
        view.setPaddingBottom(padding)
    }

    @Attr(R2.styleable.Paris_View_android_paddingLeft)
    fun setPaddingLeft(@Px padding: Int) {
        view.setPaddingLeft(padding)
    }

    @Attr(R2.styleable.Paris_View_android_paddingRight)
    fun setPaddingRight(@Px padding: Int) {
        view.setPaddingRight(padding)
    }

    @Attr(R2.styleable.Paris_View_android_paddingTop)
    fun setPaddingTop(@Px padding: Int) {
        view.setPaddingTop(padding)
    }

    @Attr(R2.styleable.Paris_View_android_paddingHorizontal)
    fun setPaddingHorizontal(@Px padding: Int) {
        view.setPaddingHorizontal(padding)
    }

    @Attr(R2.styleable.Paris_View_android_paddingVertical)
    fun setPaddingVertical(@Px padding: Int) {
        view.setPaddingVertical(padding)
    }

    @Attr(R2.styleable.Paris_View_android_padding)
    fun setPadding(@Px padding: Int) {
        view.setPadding(padding, padding, padding, padding)
    }

    @Attr(R2.styleable.Paris_View_android_paddingEnd)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setPaddingEnd(@Px padding: Int) {
        view.setPaddingEnd(padding)
    }

    @Attr(R2.styleable.Paris_View_android_paddingStart)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setPaddingStart(@Px padding: Int) {
        view.setPaddingStart(padding)
    }

    @Attr(R2.styleable.Paris_View_android_stateListAnimator)
    fun setStateListAnimator(@AnyRes animatorRes: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val animator = if (animatorRes != 0) {
                AnimatorInflater.loadStateListAnimator(view.context, animatorRes)
            } else {
                null
            }
            view.stateListAnimator = animator
        }
    }

    @Attr(R2.styleable.Paris_View_android_visibility)
    fun setVisibility(visibility: Int) {
        view.visibility = VISIBILITY_MAP.get(visibility)
    }

    @Attr(R2.styleable.Paris_View_ignoreLayoutWidthAndHeight)
    fun setIgnoreLayoutWidthAndHeight(ignore: Boolean) {
        ignoreLayoutWidthAndHeight = ignore
    }

    companion object {

        const val PORTERDUFF_MODE_SRC_OVER = 3
        const val PORTERDUFF_MODE_SRC_IN = 5
        const val PORTERDUFF_MODE_SRC_ATOP = 9
        const val PORTERDUFF_MODE_MULTIPLY = 14
        const val PORTERDUFF_MODE_SCREEN = 15
        const val PORTERDUFF_MODE_ADD = 16

        private val VISIBILITY_MAP = SparseIntArray().apply {
            // Visibility values passed to setVisibility are assumed to be one of View.VISIBLE (0),
            // INVISIBLE (4) and GONE (8) if passed to a style builder as a "direct" value, or an int
            // between 0 and 2 if passed as an enum resource (either through a builder or XML style).
            // This maps all the possible inputs to the correct visibility.
            put(View.VISIBLE, View.VISIBLE)
            put(View.INVISIBLE, View.INVISIBLE)
            put(View.GONE, View.GONE)
            put(1, View.INVISIBLE)
            put(2, View.GONE)
        }
    }
}
