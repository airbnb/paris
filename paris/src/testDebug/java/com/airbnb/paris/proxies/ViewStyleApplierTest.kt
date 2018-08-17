package com.airbnb.paris.proxies

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.view.ViewStyleApplier
import com.airbnb.paris.R
import com.airbnb.paris.extensions.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ViewStyleApplierTest {

    private lateinit var context: Context
    private lateinit var view: View
    private lateinit var applier: ViewStyleApplier
    private lateinit var builder: ViewStyleApplier.StyleBuilder

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        view = View(context)
        applier = ViewStyleApplier(view)
        builder = ViewStyleApplier.StyleBuilder()
    }

    @Test
    fun background_null() {
        // Since null is the default first set the background to something else
        view.background = ColorDrawable(Color.WHITE)
        applier.apply(builder.background(null).build())
        assertEquals(null, view.background)
    }

    @Test
    fun background_nullRes() {
        // Since null is the default first set the background to something else
        view.background = ColorDrawable(Color.WHITE)
        applier.apply(builder.backgroundRes(R.drawable.null_).build())
        assertEquals(null, view.background)
    }

    fun background_tintRes() {
        // First set the tint to something else
        view.backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.black)
        applier.apply(builder.backgroundTintRes(android.R.color.holo_red_dark).build())
        assertEquals(
                ContextCompat.getColorStateList(context, android.R.color.holo_red_dark),
                view.backgroundTintList
        )
    }

    fun background_tintColor() {
        // First set the tint to something else
        view.backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.black)
        applier.apply(builder.backgroundTint(ContextCompat.getColor(context, android.R.color.holo_red_dark)).build())
        assertEquals(
                ContextCompat.getColorStateList(context, android.R.color.holo_red_dark),
                view.backgroundTintList
        )
    }

    fun background_tintColorStateNull() {
        // First set the tint to something else
        view.backgroundTintList = null
        applier.apply(builder.backgroundTint(null).build())
        assertNull(view.backgroundTintList)
    }

    fun background_tintColorStateList() {
        // First set the tint to something else
        view.backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.black)
        applier.apply(builder.backgroundTint(ContextCompat.getColorStateList(context, android.R.color.holo_red_dark)).build())
        assertEquals(
                ContextCompat.getColorStateList(context, android.R.color.holo_red_dark),
                view.backgroundTintList
        )
    }

    fun background_tintModeNull() {
        // First set the tint mode to something else
        view.backgroundTintMode = null
        applier.apply(builder.backgroundTintMode(-1).build())
        assertNull(view.backgroundTintMode)
    }

    fun background_tintMode() {
        // First set the tint mode to something else
        view.backgroundTintMode = PorterDuff.Mode.SRC_OVER
        applier.apply(builder.backgroundTintMode(ViewProxy.PORTERDUFF_MODE_ADD).build())
        assertEquals(
                PorterDuff.Mode.ADD,
                view.backgroundTintMode
        )
    }


    @Test
    fun foreground_null() {
        // Since null is the default first set the foreground to something else
        view.foreground = ColorDrawable(Color.WHITE)
        applier.apply(builder.foreground(null).build())
        assertEquals(null, view.foreground)
    }

    @Test
    fun foreground_nullRes() {
        // Since null is the default first set the foreground to something else
        view.foreground = ColorDrawable(Color.WHITE)
        applier.apply(builder.foregroundRes(R.drawable.null_).build())
        assertEquals(null, view.foreground)
    }

    @Test
    fun layout_margin_precedence() {
        applier.apply(viewStyle {
            layoutMargin(20)
            layoutMarginBottom(10)
            layoutMarginEnd(10)
            layoutMarginLeft(10)
            layoutMarginRight(10)
            layoutMarginStart(10)
            layoutMarginTop(10)
        })
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        assertEquals(20, layoutParams.bottomMargin)
        assertEquals(20, layoutParams.leftMargin)
        assertEquals(20, layoutParams.marginEnd)
        assertEquals(20, layoutParams.marginStart)
        assertEquals(20, layoutParams.rightMargin)
        assertEquals(20, layoutParams.topMargin)
    }

    @Test
    fun layout_marginEnd_precedence() {
        applier.apply(viewStyle {
            layoutMarginEnd(20)
            layoutMarginRight(10)
        })
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        assertEquals(20, layoutParams.marginEnd)
    }

    @Test
    fun layout_marginStart_precedence() {
        applier.apply(viewStyle {
            layoutMarginStart(20)
            layoutMarginLeft(10)
        })
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        assertEquals(20, layoutParams.marginStart)
    }

    @Test
    fun padding_precedence() {
        // android:padding takes precedence over android:paddingBottom, android:paddingHorizontal, android:paddingLeft, android:paddingRight,
        // android:paddingTop and android:paddingVertical
        applier.apply(viewStyle {
            padding(20)
            paddingBottom(10)
            paddingHorizontal(10)
            paddingLeft(10)
            paddingRight(10)
            paddingTop(10)
            paddingVertical(10)
        })
        assertEquals(20, view.paddingBottom)
        assertEquals(20, view.paddingLeft)
        assertEquals(20, view.paddingRight)
        assertEquals(20, view.paddingTop)
    }

    @Test
    fun paddingHorizontal_precedence() {
        // android:paddingHorizontal supersedes android:paddingLeft and android:paddingRight
        applier.apply(viewStyle {
            paddingHorizontal(20)
            paddingLeft(10)
            paddingRight(10)
        })
        assertEquals(20, view.paddingLeft)
        assertEquals(20, view.paddingRight)
    }

    @Test
    fun paddingVertical_precedence() {
        // android:paddingHorizontal supersedes android:paddingLeft and android:paddingRight
        applier.apply(viewStyle {
            paddingVertical(20)
            paddingBottom(10)
            paddingTop(10)
        })
        assertEquals(20, view.paddingBottom)
        assertEquals(20, view.paddingTop)
    }

    @Test
    fun paddingEnd_precedence() {
        // android:paddingEnd supersedes everything else (android:padding, android:paddingRight, android:paddingVertical)
        applier.apply(viewStyle {
            paddingEnd(20)
            padding(10)
            paddingRight(10)
            paddingVertical(10)
        })
        assertEquals(20, view.paddingEnd)
    }

    @Test
    fun paddingStart_precedence() {
        // android:paddingStart supersedes everything else (android:padding, android:paddingHorizontal, android:paddingLeft)
        applier.apply(viewStyle {
            paddingStart(20)
            padding(10)
            paddingHorizontal(10)
            paddingLeft(10)
        })
        assertEquals(20, view.paddingStart)
    }

    @Test
    fun visibility_visible() {
        // Since visible is the default first set the visibility to something else
        view.visibility = View.GONE
        applier.apply(builder.visibility(View.VISIBLE).build())
        assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun visibility_invisible() {
        applier.apply(builder.visibility(View.INVISIBLE).build())
        assertEquals(View.INVISIBLE, view.visibility)
    }

    @Test
    fun visibility_gone() {
        applier.apply(builder.visibility(View.GONE).build())
        assertEquals(View.GONE, view.visibility)
    }
}
