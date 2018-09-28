package com.airbnb.paris.proxies

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeprecatedTextViewProxyTest {

    private lateinit var context: Context
    private lateinit var res: Resources
    private lateinit var view: TextView
    private lateinit var proxy: TextViewProxy

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        res = context.resources
        view = TextView(context)
        proxy = TextViewProxy(view)
    }

    @Test
    fun setDrawables() {
        // Sets drawables on all sides

        val drawableBottom = ColorDrawable(Color.GREEN)
        proxy.setDrawableBottom(drawableBottom)

        val drawableLeft = ColorDrawable(Color.RED)
        proxy.setDrawableLeft(drawableLeft)

        val drawableRight = ColorDrawable(Color.BLACK)
        proxy.setDrawableRight(drawableRight)

        val drawableTop = ColorDrawable(Color.YELLOW)
        proxy.setDrawableTop(drawableTop)

        // Assumes the style parameter isn't used
        proxy.afterStyle(null)

        assertEquals(drawableBottom, view.compoundDrawables[3])
        assertEquals(drawableLeft, view.compoundDrawables[0])
        assertEquals(drawableRight, view.compoundDrawables[2])
        assertEquals(drawableTop, view.compoundDrawables[1])
    }

    @Test(expected = IllegalStateException::class)
    fun setEllipsize_invalidValue() {
        proxy.setEllipsize(5)
    }

    @Test
    fun setTextAllCaps_true() {
        proxy.setTextAllCaps(true)
        // TODO This doesn't work for some reason
        //assertTrue(view.transformationMethod is AllCapsTransformationMethod)
    }

    @Test
    fun setTextAllCaps_false() {
        proxy.setTextAllCaps(false)
        // TODO This would be true regardless
        assertNull(view.transformationMethod)
    }
}
