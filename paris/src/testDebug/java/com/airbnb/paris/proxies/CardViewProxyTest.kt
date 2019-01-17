package com.airbnb.paris.proxies

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.cardview.widget.CardView
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class CardViewProxyTest {

    private lateinit var context: Context
    private lateinit var view: CardView
    private lateinit var proxy: CardViewProxy

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        view = CardView(context)
        proxy = CardViewProxy(view)
    }

    @Test
    fun setRadius() {
        view.radius = 1f
        proxy.setRadius(2)
        assertEquals(
                view.radius,
                2
        )
    }

    @Test
    fun setBackgroundColor() {
        proxy.setBackgroundColor(ColorStateList.valueOf(Color.BLACK))
        assertEquals(
                view.cardBackgroundColor,
                ColorStateList.valueOf(Color.BLACK)
        )
    }

    @Test
    fun setElevation() {
        view.elevation = 1f
        proxy.setElevation(2)
        assertEquals(
                view.elevation,
                2
        )
    }

    @Test
    fun setContentPadding() {
        view.setContentPadding(1, 1, 1, 1)
        proxy.setContentPadding(2)
        assertEquals(
                listOf(view.contentPaddingLeft, view.contentPaddingTop, view.contentPaddingRight, view.contentPaddingBottom),
                listOf(2, 2, 2, 2)
        )
    }
}
