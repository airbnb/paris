package com.airbnb.paris.proxies

import android.content.*
import android.content.res.*
import android.graphics.*
import android.graphics.drawable.*
import android.support.test.*
import android.support.test.runner.*
import android.widget.*
import android.widget.TextViewStyleApplier.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.*
import org.mockito.*
import org.mockito.Mockito.*


@RunWith(AndroidJUnit4::class)
class TextViewStyleApplierTest {

    private lateinit var context: Context
    private lateinit var res: Resources
    private lateinit var view: TextView
    private lateinit var styleApplier: TextViewStyleApplier
    private lateinit var styleBuilder: StyleBuilder

    private fun apply(builderFunctions: StyleBuilder.() -> StyleBuilder) =
            TextViewStyleApplier(view).apply(
                    StyleBuilder()
                            .debugName("test")
                            .builderFunctions()
                            .build())

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        res = context.resources
        view = TextView(context)
        styleApplier = TextViewStyleApplier(view)
        styleBuilder = StyleBuilder()
    }

    @Test
    fun auto() {
        for (mapping in (VIEW_MAPPINGS + TEXT_VIEW_MAPPINGS)) {
            mapping as BaseViewMapping<Any, *, TextView, Any>

            setup()

            mapping.testValues.forEach {
                // Set the value on the style builder
                mapping.setStyleBuilderValueFunction(styleBuilder, it)
                // Apply the style to the view
                styleApplier.apply(styleBuilder.build())
                // Check that the value was correctly applied
                mapping.assertViewSet(view, it)
            }
        }
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
}
