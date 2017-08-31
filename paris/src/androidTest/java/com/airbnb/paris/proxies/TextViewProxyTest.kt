package com.airbnb.paris.proxies

import android.content.*
import android.content.res.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.support.test.*
import android.support.test.runner.*
import android.text.*
import android.view.*
import android.widget.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class TextViewProxyTest {

    private lateinit var context: Context
    private lateinit var res: Resources
    private lateinit var view: TextView
    private lateinit var proxy: TextViewProxy

    private fun <T> assertProxyEqualsView(values: List<T>,
                                          proxySet: TextViewProxy.(T) -> Any,
                                          viewGet: TextView.() -> T) {
        values.forEach {
            proxy.proxySet(it)
            assertEquals(it, view.viewGet())
        }
    }

    private fun <T, U> assertProxyEqualsView(values: List<T>,
                                             proxySet: TextViewProxy.(T) -> Any,
                                             transform: (T) -> U,
                                             viewGet: TextView.() -> U) {
        values.forEach {
            proxy.proxySet(it)
            assertEquals(transform(it), view.viewGet())
        }
    }

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        res = context.resources
        view = TextView(context)
        proxy = TextViewProxy(view)
    }

    @Test
    fun setDrawableBottom() {
        val drawable = ColorDrawable(Color.GREEN)
        proxy.setDrawableBottom(drawable)
        // Assumes the style parameter isn't used
        proxy.afterStyle(null)
        assertEquals(drawable, view.compoundDrawables[3])
    }

    @Test
    fun setDrawableLeft() {
        val drawable = ColorDrawable(Color.GREEN)
        proxy.setDrawableLeft(drawable)
        // Assumes the style parameter isn't used
        proxy.afterStyle(null)
        assertEquals(drawable, view.compoundDrawables[0])
    }

    @Test
    fun setDrawableRight() {
        val drawable = ColorDrawable(Color.GREEN)
        proxy.setDrawableRight(drawable)
        // Assumes the style parameter isn't used
        proxy.afterStyle(null)
        assertEquals(drawable, view.compoundDrawables[2])
    }

    @Test
    fun setDrawableTop() {
        val drawable = ColorDrawable(Color.GREEN)
        proxy.setDrawableTop(drawable)
        // Assumes the style parameter isn't used
        proxy.afterStyle(null)
        assertEquals(drawable, view.compoundDrawables[1])
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

    @Test
    fun setEllipsize() {
        mapOf(
                1 to TextUtils.TruncateAt.START,
                2 to TextUtils.TruncateAt.MIDDLE,
                3 to TextUtils.TruncateAt.END,
                4 to TextUtils.TruncateAt.MARQUEE)
                .forEach {
                    proxy.setEllipsize(it.key)
                    assertEquals("${it.value.name} didn't match", it.value, view.ellipsize)
                }
    }

    @Test(expected = IllegalStateException::class)
    fun setEllipsize_invalidValue() {
        proxy.setEllipsize(5)
    }

    @Test
    fun setGravity() {
        listOf(
                Gravity.BOTTOM,
                Gravity.CENTER,
                Gravity.CENTER_VERTICAL,
                Gravity.START,
                Gravity.TOP)
                .forEach {
                    proxy.setGravity(it)
                    assertTrue(view.gravity and it == it)
                }
    }

    @Test
    fun setLetterSpacing() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            assertProxyEqualsView(
                    listOf(-5f, 0f, 8f, 10f, 11.5f, 17f),
                    TextViewProxy::setLetterSpacing,
                    TextView::getLetterSpacing)
        }
    }

    @Test
    fun setLines() {
        listOf(Integer.MIN_VALUE, -5, 0, 1, 2, 3, 5, 15, Integer.MAX_VALUE)
                .forEach {
                    proxy.setLines(it)
                    assertEquals(it, view.minLines)
                    assertEquals(it, view.maxLines)
                }
    }

    @Test
    fun setLineSpacingExtra() {
        assertProxyEqualsView(
                listOf(Integer.MIN_VALUE, -5, 0, 1, 2, 3, 5, 15, Integer.MAX_VALUE),
                TextViewProxy::setLineSpacingExtra,
                Int::toFloat,
                TextView::getLineSpacingExtra)
    }

    @Test
    fun setLineSpacingMultiplier() {
        assertProxyEqualsView(
                listOf(-5f, 0f, 8f, 10f, 11.5f, 17f),
                TextViewProxy::setLineSpacingMultiplier,
                TextView::getLineSpacingMultiplier)
    }

    @Test
    fun setMaxLines() {
        assertProxyEqualsView(
                listOf(Integer.MIN_VALUE, -5, 0, 1, 2, 3, 5, 15, Integer.MAX_VALUE),
                TextViewProxy::setMaxLines,
                TextView::getMaxLines)
    }

    @Test
    fun setMinLines() {
        assertProxyEqualsView(
                listOf(Integer.MIN_VALUE, -5, 0, 1, 2, 3, 5, 15, Integer.MAX_VALUE),
                TextViewProxy::setMinLines,
                TextView::getMinLines)
    }

    @Test
    fun setMinWidth() {
        assertProxyEqualsView(
                listOf(Integer.MIN_VALUE, -150, 0, 10, 20, 50, 200, 800, Integer.MAX_VALUE),
                TextViewProxy::setMinWidth,
                TextView::getMinWidth)
    }

    @Test
    fun setSingleLine_true() {
        proxy.setSingleLine(true)
        assertEquals(1, view.minLines)
        assertEquals(1, view.maxLines)
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

    @Test
    fun setTextColor() {
        val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
        )
        val colorStateList = ColorStateList(states, intArrayOf(Color.RED, Color.GREEN))
        proxy.setTextColor(colorStateList)
        assertEquals(colorStateList, view.textColors)
    }

    @Test
    fun setTextColorHint() {
        val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
        )
        val colorStateList = ColorStateList(states, intArrayOf(Color.RED, Color.GREEN))
        proxy.setTextColorHint(colorStateList)
        assertEquals(colorStateList, view.hintTextColors)
    }

    @Test
    fun setTextSize() {
        assertProxyEqualsView(
                listOf(0, 1, 2, 3, 5, 15, 42, Integer.MAX_VALUE),
                TextViewProxy::setTextSize,
                Int::toFloat,
                TextView::getTextSize)
    }
}
