package com.airbnb.paris.proxies

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewStyleApplier
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.airbnb.paris.R
import com.airbnb.paris.extensions.layoutMargin
import com.airbnb.paris.extensions.layoutMarginBottom
import com.airbnb.paris.extensions.layoutMarginEnd
import com.airbnb.paris.extensions.layoutMarginHorizontal
import com.airbnb.paris.extensions.layoutMarginLeft
import com.airbnb.paris.extensions.layoutMarginRight
import com.airbnb.paris.extensions.layoutMarginStart
import com.airbnb.paris.extensions.layoutMarginTop
import com.airbnb.paris.extensions.layoutMarginVertical
import com.airbnb.paris.extensions.padding
import com.airbnb.paris.extensions.paddingBottom
import com.airbnb.paris.extensions.paddingEnd
import com.airbnb.paris.extensions.paddingHorizontal
import com.airbnb.paris.extensions.paddingLeft
import com.airbnb.paris.extensions.paddingRight
import com.airbnb.paris.extensions.paddingStart
import com.airbnb.paris.extensions.paddingTop
import com.airbnb.paris.extensions.paddingVertical
import com.airbnb.paris.extensions.viewStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

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

    @Test
    fun background_tintRes() {
        // First set the tint to something else
        view.backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.black)
        applier.apply(builder.backgroundTintRes(android.R.color.holo_red_dark).build())
        assertEquals(
            ContextCompat.getColorStateList(context, android.R.color.holo_red_dark),
            view.backgroundTintList
        )
    }

    @Test
    fun background_tintColor() {
        // First set the tint to something else
        view.backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.black)
        applier.apply(builder.backgroundTint(ContextCompat.getColor(context, android.R.color.holo_red_dark)).build())
        assertEquals(
            ContextCompat.getColorStateList(context, android.R.color.holo_red_dark),
            view.backgroundTintList
        )
    }

    @Test
    fun background_tintColorStateNull() {
        // First set the tint to something else
        view.backgroundTintList = null
        applier.apply(builder.backgroundTint(null).build())
        assertNull(view.backgroundTintList)
    }

    @Test
    fun background_tintColorStateList() {
        // First set the tint to something else
        view.backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.black)
        applier.apply(builder.backgroundTint(ContextCompat.getColorStateList(context, android.R.color.holo_red_dark)).build())
        assertEquals(
            ContextCompat.getColorStateList(context, android.R.color.holo_red_dark),
            view.backgroundTintList
        )
    }

    @Test
    fun background_tintModeNull() {
        // First set the tint mode to something else
        view.backgroundTintMode = null
        applier.apply(builder.backgroundTintMode(-1).build())
        assertNull(view.backgroundTintMode)
    }

    @Test
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
            layoutMarginHorizontal(10)
            layoutMarginVertical(10)
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
    @Config(sdk = [(Build.VERSION_CODES.JELLY_BEAN)])
    fun layout_marginEnd_requiresApi() {
        // layout_marginEnd requires JELLY_BEAN_MR1 (17) so here the attribute should be ignored.
        applier.apply(viewStyle {
            layoutMarginEnd(10)
        })
        // The margin doesn't get set so the layout parameters should still be null.
        assertNull(view.layoutParams)
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
    @Config(sdk = [(Build.VERSION_CODES.JELLY_BEAN)])
    fun layout_marginStart_requiresApi() {
        // layout_marginStart requires JELLY_BEAN_MR1 (17) so here the attribute should be ignored.
        applier.apply(viewStyle {
            layoutMarginStart(10)
        })
        // The margin doesn't get set so the layout parameters should still be null.
        assertNull(view.layoutParams)
    }

    @Test
    fun layout_marginHorizontal_precedence() {
        applier.apply(viewStyle {
            layoutMarginHorizontal(20)
            layoutMarginLeft(10)
            layoutMarginRight(10)
        })
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        assertEquals(20, layoutParams.leftMargin)
        assertEquals(20, layoutParams.rightMargin)
    }

    @Test
    @Config(sdk = [(Build.VERSION_CODES.N_MR1)])
    fun layout_marginHorizontal_requiresApi() {
        // layout_marginHorizontal requires O (26) so here the attribute should be ignored.
        applier.apply(viewStyle {
            layoutMarginHorizontal(10)
        })
        // The margin doesn't get set so the layout parameters should still be null.
        assertNull(view.layoutParams)
    }

    @Test
    fun layout_marginVertical_precedence() {
        applier.apply(viewStyle {
            layoutMarginVertical(20)
            layoutMarginTop(10)
            layoutMarginBottom(10)
        })
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        assertEquals(20, layoutParams.topMargin)
        assertEquals(20, layoutParams.bottomMargin)
    }

    @Test
    @Config(sdk = [(Build.VERSION_CODES.N_MR1)])
    fun layout_marginVertical_requiresApi() {
        // layout_marginVertical requires O (26) so here the attribute should be ignored.
        applier.apply(viewStyle {
            layoutMarginVertical(10)
        })
        // The margin doesn't get set so the layout parameters should still be null.
        assertNull(view.layoutParams)
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

    @Test
    fun layoutWeight() {
        val weight = 0.43f
        view.layoutParams = LinearLayout.LayoutParams(100, 100)
        applier.apply(builder.layoutWeight(weight).build())
        assertEquals(weight, (view.layoutParams as LinearLayout.LayoutParams).weight)
    }
}
