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
import org.mockito.*
import org.mockito.Mockito.*


@RunWith(AndroidJUnit4::class)
class TextViewProxyStyleApplierTest {

    companion object {
        private val ARBITRARY_DIMENSIONS = listOf(Integer.MIN_VALUE, -150, 0, 10, 20, 50, 200, 800, Integer.MAX_VALUE)
        private val ARBITRARY_FLOATS = listOf(-5f, 0f, 8f, 10f, 11.5f, 17f)
        private val ARBITRARY_INTS = listOf(Integer.MIN_VALUE, -5, 0, 1, 2, 3, 5, 15, Integer.MAX_VALUE)
        private val BOOLS = listOf(true, false)
    }

    private lateinit var context: Context
    private lateinit var res: Resources
    private lateinit var view: TextView

    private fun apply(builderFunctions: TextViewProxyStyleApplier.StyleBuilder.() -> TextViewProxyStyleApplier.StyleBuilder) =
            TextViewProxyStyleApplier(view).apply(
                    TextViewProxyStyleApplier.StyleBuilder()
                            .debugName("test")
                            .builderFunctions()
                            .build())

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        res = context.resources
        view = TextView(context)
    }

    @Test
    fun applyDrawables() {
        val drawableBottom = ColorDrawable(Color.RED)
        val drawableLeft = ColorDrawable(Color.GREEN)
        val drawableRight = ColorDrawable(Color.BLUE)
        val drawableTop = ColorDrawable(Color.YELLOW)
        apply {
            drawableBottom(drawableBottom)
            drawableLeft(drawableLeft)
            drawableRight(drawableRight)
            drawableTop(drawableTop)
        }

        assertEquals(drawableBottom, view.compoundDrawables[3])
        assertEquals(drawableLeft, view.compoundDrawables[0])
        assertEquals(drawableRight, view.compoundDrawables[2])
        assertEquals(drawableTop, view.compoundDrawables[1])
    }

    @Test
    fun applyDrawables_noReset() {
        // If a style is applied that doesn't change the drawable then it should still be there

        val drawableLeft = ColorDrawable(Color.RED)
        val drawableTop = ColorDrawable(Color.GREEN)
        view.setCompoundDrawables(drawableLeft, null, null, null)
        apply {
            drawableTop(drawableTop)
        }

        assertEquals(drawableLeft, view.compoundDrawables[0])
        assertEquals(drawableTop, view.compoundDrawables[1])
    }

    @Test
    fun applyEllipsize() {
        mapOf(
                1 to TextUtils.TruncateAt.START,
                2 to TextUtils.TruncateAt.MIDDLE,
                3 to TextUtils.TruncateAt.END,
                4 to TextUtils.TruncateAt.MARQUEE).forEach {
            apply {
                ellipsize(it.key)
            }

            assertEquals(it.value, view.ellipsize)
        }
    }

    @Test
    fun applyGravity() {
        listOf(
                Gravity.BOTTOM,
                Gravity.CENTER,
                Gravity.CENTER_VERTICAL,
                Gravity.START,
                Gravity.TOP).forEach {
            apply {
                gravity(it)
            }

            assertTrue(view.gravity and it == it)
        }
    }

    @Test
    fun applyLetterSpacing() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ARBITRARY_FLOATS.forEach {
                apply { letterSpacing(it) }
                assertEquals(it, view.letterSpacing)
            }
        }
    }

    @Test
    fun applyLines() {
        ARBITRARY_INTS.forEach {
            apply { lines(it) }
            assertEquals(it, view.minLines)
            assertEquals(it, view.maxLines)
        }
    }

    @Test
    fun applyLineSpacingExtra() {
        ARBITRARY_INTS.forEach {
            apply { lineSpacingExtra(it) }
            assertEquals(it.toFloat(), view.lineSpacingExtra)
        }
    }

    @Test
    fun applyLineSpacingMultiplier() {
        ARBITRARY_FLOATS.forEach {
            apply { lineSpacingMultiplier(it) }
            assertEquals(it, view.lineSpacingMultiplier)
        }
    }

    @Test
    fun applyMaxLines() {
        ARBITRARY_INTS.forEach {
            apply { maxLines(it) }
            assertEquals(it, view.maxLines)
        }
    }

    @Test
    fun applyMinLines() {
        ARBITRARY_INTS.forEach {
            apply { minLines(it) }
            assertEquals(it, view.minLines)
        }
    }

    @Test
    fun applyMinWidth() {
        ARBITRARY_DIMENSIONS.forEach {
            apply { minWidth(it) }
            assertEquals(it, view.minWidth)
        }
    }

    @Test
    fun applySingleLine_true() {
        apply { singleLine(true) }
        assertEquals(1, view.minLines)
        assertEquals(1, view.maxLines)
    }

    @Test
    fun applySingleLine_false() {
        view.maxLines = 1
        apply { singleLine(false) }
        assertEquals(Integer.MAX_VALUE, view.maxLines)
    }

    @Test
    fun applyTextAllCaps_true() {
        view = spy(view)
        apply { textAllCaps(true) }
        Mockito.verify(view).setAllCaps(true)
    }

    @Test
    fun applyTextAllCaps_false() {
        view = spy(view)
        apply { textAllCaps(false) }
        Mockito.verify(view).setAllCaps(false)
    }

    @Test
    fun applyTextColor() {
        val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
        )
        val colorStateList = ColorStateList(states, intArrayOf(Color.RED, Color.GREEN))
        apply { textColor(colorStateList) }
        assertEquals(colorStateList, view.textColors)
    }

    @Test
    fun applyTextColorHint() {
        val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
        )
        val colorStateList = ColorStateList(states, intArrayOf(Color.RED, Color.GREEN))
        apply { textColorHint(colorStateList) }
        assertEquals(colorStateList, view.hintTextColors)
    }

    @Test
    fun applyTextSize() {
        ARBITRARY_DIMENSIONS
                .filter { it >= 0 }
                .forEach {
                    apply { textSize(it) }
                    assertEquals(it.toFloat(), view.textSize)
                }
    }
}
