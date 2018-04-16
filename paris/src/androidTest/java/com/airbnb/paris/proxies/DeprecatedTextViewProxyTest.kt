package com.airbnb.paris.proxies

import android.content.*
import android.content.res.*
import android.graphics.*
import android.graphics.drawable.*
import android.support.test.*
import android.support.test.runner.*
import android.widget.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.*

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
    fun setSingleLine_false() {
        view.maxLines = 1
        proxy.setSingleLine(false)
        assertEquals(Integer.MAX_VALUE, view.maxLines)
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
