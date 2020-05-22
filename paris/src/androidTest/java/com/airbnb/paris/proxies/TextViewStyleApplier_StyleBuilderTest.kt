package com.airbnb.paris.proxies

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import android.widget.TextViewStyleApplier.StyleBuilder
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.airbnb.paris.styles.ProgrammaticStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TextViewStyleApplier_StyleBuilderTest {

    companion object {
        private const val ARBITRARY_RESOURCE_ID_1 = 2
        private const val ARBITRARY_RESOURCE_ID_2 = 4

        private val ARBITRARY_DP_VALUES = listOf(Integer.MIN_VALUE, -150, 0, 10, 20, 50, 200, 800, Integer.MAX_VALUE)
        private val ARBITRARY_TEXT_SIZE_VALUES = listOf(0, 1, 2, 3, 5, 15, 42, Integer.MAX_VALUE)
    }

    private val context = InstrumentationRegistry.getTargetContext()!!
    private lateinit var view: TextView
    private lateinit var programmaticStyleBuilder: ProgrammaticStyle.Builder
    private lateinit var styleBuilder: StyleBuilder

    private fun programmaticStyle(builderFunctions: ProgrammaticStyle.Builder.() -> Any) =
        ProgrammaticStyle.builder().debugName("test").builderFunctions()

    private fun ProgrammaticStyle.Builder.assertEqualsTextViewStyleBuilder(builderFunctions: StyleBuilder.() -> StyleBuilder) {
        assertEquals(build(), StyleBuilder().debugName("test").builderFunctions().build())
    }

    private fun ProgrammaticStyle.Builder.assertNotEqualsTextViewStyleBuilder(builderFunctions: StyleBuilder.() -> StyleBuilder) {
        assertNotEquals(build(), StyleBuilder().debugName("test").builderFunctions().build())
    }

    private fun assertEqualStylesDp(attrRes: Int, builderFunctions: StyleBuilder.(Int) -> StyleBuilder, values: List<Int>) {
        for (value in values) {
            programmaticStyle {
                putDp(attrRes, value)
                val programmaticStyle = build()
                val textViewStyle = StyleBuilder()
                    .debugName("test")
                    .builderFunctions(value)
                    .build()
                assertEquals(programmaticStyle, textViewStyle)
            }
        }
    }

    @Before
    fun setup() {
        view = TextView(context) // TODO Remove
        programmaticStyleBuilder = ProgrammaticStyle.builder().debugName("test")
        styleBuilder = StyleBuilder().debugName("test")
    }

    @Test
    fun auto() {
        for (mapping in (VIEW_MAPPINGS + TEXT_VIEW_MAPPINGS)) {
            mapping as BaseViewMapping<Any, *, TextView, Any>

            // For normal values
            mapping.testValues.forEach {
                setup()

                programmaticStyleBuilder.put(mapping.attrRes, it)
                mapping.setStyleBuilderValueFunction(styleBuilder, it)
                assertEquals(programmaticStyleBuilder.build(), styleBuilder.build())
            }

            // For resource ids
            setup()

            programmaticStyleBuilder.putRes(mapping.attrRes, ARBITRARY_RESOURCE_ID_1)
            mapping.setStyleBuilderResFunction(styleBuilder, ARBITRARY_RESOURCE_ID_1)
            assertEquals(programmaticStyleBuilder.build(), styleBuilder.build())
        }
    }

    @Test
    fun drawables() {
        val drawableRed = ColorDrawable(Color.RED)
        val drawableGreen = ColorDrawable(Color.GREEN)
        programmaticStyle {
            put(android.R.attr.drawableBottom, drawableRed)
            put(android.R.attr.drawableLeft, drawableGreen)
            putRes(android.R.attr.drawableRight, ARBITRARY_RESOURCE_ID_1)
            putRes(android.R.attr.drawableTop, ARBITRARY_RESOURCE_ID_2)

            assertEqualsTextViewStyleBuilder {
                drawableBottom(drawableRed)
                drawableLeft(drawableGreen)
                drawableRightRes(ARBITRARY_RESOURCE_ID_1)
                drawableTopRes(ARBITRARY_RESOURCE_ID_2)
            }

            assertNotEqualsTextViewStyleBuilder {
                drawableBottom(drawableGreen)
                drawableLeft(drawableRed)
                drawableRightRes(ARBITRARY_RESOURCE_ID_2)
                drawableTopRes(ARBITRARY_RESOURCE_ID_1)
            }
        }
    }

    @Test
    fun lineSpacingExtra_dp() {
        listOf(Integer.MIN_VALUE, -5, 0, 1, 2, 3, 5, 15, Integer.MAX_VALUE).forEach {
            programmaticStyle {
                putDp(android.R.attr.lineSpacingExtra, it)

                assertEqualsTextViewStyleBuilder {
                    lineSpacingExtraDp(it)
                }
            }
        }
    }

    @Test
    fun minWidth_dp() {
        assertEqualStylesDp(android.R.attr.minWidth, StyleBuilder::minWidthDp, ARBITRARY_DP_VALUES)
    }

    @Test
    fun textSize_dp() {
        assertEqualStylesDp(android.R.attr.textSize, StyleBuilder::textSizeDp, ARBITRARY_TEXT_SIZE_VALUES)
    }
}
