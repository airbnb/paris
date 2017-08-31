package com.airbnb.paris.proxies

import android.content.res.*
import android.graphics.*
import android.graphics.drawable.*
import android.support.test.*
import android.support.test.runner.*
import android.view.*
import android.widget.*
import com.airbnb.paris.proxies.TextViewProxyStyleApplier.*
import com.airbnb.paris.styles.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class TextViewProxyStyleApplier_StyleBuilderTest {

    companion object {
        private const val ARBITRARY_RESOURCE_1 = 2
        private const val ARBITRARY_RESOURCE_2 = 4

        private val ARBITRARY_DP_VALUES = listOf(Integer.MIN_VALUE, -150, 0, 10, 20, 50, 200, 800, Integer.MAX_VALUE)
        private val ARBITRARY_TEXT_SIZE_VALUES = listOf(0, 1, 2, 3, 5, 15, 42, Integer.MAX_VALUE)
        private val BOOL_VALUES = listOf(true, false)
    }

    private val context = InstrumentationRegistry.getTargetContext()!!
    private lateinit var view: TextView

    private fun programmaticStyle(builderFunctions: ProgrammaticStyle.Builder.() -> Any) =
            ProgrammaticStyle.builder().debugName("test").builderFunctions()

    private fun ProgrammaticStyle.Builder.assertEqualsTextViewStyleBuilder(builderFunctions: StyleBuilder.() -> StyleBuilder) {
        assertEquals(build(), StyleBuilder().debugName("test").builderFunctions().build())
    }

    private fun ProgrammaticStyle.Builder.assertNotEqualsTextViewStyleBuilder(builderFunctions: StyleBuilder.() -> StyleBuilder) {
        assertNotEquals(build(), StyleBuilder().debugName("test").builderFunctions().build())
    }

    private fun <T : Any> assertEqualStylesValue(attrRes: Int, builderFunctions: StyleBuilder.(T) -> StyleBuilder, values: List<T>) {
        for (value in values) {
            programmaticStyle {
                put(attrRes, value)
                val programmaticStyle = build()
                val textViewStyle =StyleBuilder()
                        .debugName("test")
                        .builderFunctions(value)
                        .build()
                assertEquals(programmaticStyle, textViewStyle)
            }
        }
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

    private fun assertEqualStylesRes(attrRes: Int, builderFunctions: StyleBuilder.(Int) -> StyleBuilder) {
        programmaticStyle {
            putRes(attrRes, ARBITRARY_RESOURCE_1)
            val programmaticStyle = build()
            val textViewStyle = StyleBuilder()
                    .debugName("test")
                    .builderFunctions(ARBITRARY_RESOURCE_1)
                    .build()
            assertEquals(programmaticStyle, textViewStyle)
        }
    }

    @Before
    fun setup() {
        view = TextView(context)
    }

    @Test
    fun drawables() {
        val drawableRed = ColorDrawable(Color.RED)
        val drawableGreen = ColorDrawable(Color.GREEN)
        programmaticStyle {
            put(android.R.attr.drawableBottom, drawableRed)
            put(android.R.attr.drawableLeft, drawableGreen)
            putRes(android.R.attr.drawableRight, ARBITRARY_RESOURCE_1)
            putRes(android.R.attr.drawableTop, ARBITRARY_RESOURCE_2)

            assertEqualsTextViewStyleBuilder {
                drawableBottom(drawableRed)
                drawableLeft(drawableGreen)
                drawableRight(ARBITRARY_RESOURCE_1)
                drawableTop(ARBITRARY_RESOURCE_2)
            }

            assertNotEqualsTextViewStyleBuilder {
                drawableBottom(drawableGreen)
                drawableLeft(drawableRed)
                drawableRight(ARBITRARY_RESOURCE_2)
                drawableTop(ARBITRARY_RESOURCE_1)
            }
        }
    }

    @Test
    fun ellipsize_value() {
        (1..4).forEach {
            programmaticStyle {
                put(android.R.attr.ellipsize, it)

                assertEqualsTextViewStyleBuilder {
                    ellipsize(it)
                }
            }
        }
    }

    @Test
    fun ellipsize_res() {
        programmaticStyle {
            putRes(android.R.attr.ellipsize, ARBITRARY_RESOURCE_1)

            assertEqualsTextViewStyleBuilder {
                ellipsizeRes(ARBITRARY_RESOURCE_1)
            }
        }
    }

    @Test
    fun gravity_value() {
        listOf(Gravity.START, Gravity.CENTER_HORIZONTAL, Gravity.TOP).forEach {
            programmaticStyle {
                put(android.R.attr.gravity, it)

                assertEqualsTextViewStyleBuilder {
                    gravity(it)
                }
            }
        }
    }

    @Test
    fun gravity_res() {
        programmaticStyle {
            putRes(android.R.attr.gravity, ARBITRARY_RESOURCE_1)

            assertEqualsTextViewStyleBuilder {
                gravityRes(ARBITRARY_RESOURCE_1)
            }
        }
    }

    @Test
    fun letterSpacing_value() {
        listOf(-5f, 0f, 8f, 10f, 11.5f, 17f).forEach {
            programmaticStyle {
                put(android.R.attr.letterSpacing, it)

                assertEqualsTextViewStyleBuilder {
                    letterSpacing(it)
                }
            }
        }
    }

    @Test
    fun letterSpacing_res() {
        programmaticStyle {
            putRes(android.R.attr.letterSpacing, ARBITRARY_RESOURCE_1)

            assertEqualsTextViewStyleBuilder {
                letterSpacing(ARBITRARY_RESOURCE_1)
            }
        }
    }

    @Test
    fun lines_value() {
        listOf(Integer.MIN_VALUE, -5, 0, 1, 2, 3, 5, 15, Integer.MAX_VALUE).forEach {
            programmaticStyle {
                put(android.R.attr.lines, it)

                assertEqualsTextViewStyleBuilder {
                    lines(it)
                }
            }
        }
    }

    @Test
    fun lines_res() {
        programmaticStyle {
            putRes(android.R.attr.lines, ARBITRARY_RESOURCE_1)

            assertEqualsTextViewStyleBuilder {
                linesRes(ARBITRARY_RESOURCE_1)
            }
        }
    }

    @Test
    fun lineSpacingExtra_value() {
        listOf(Integer.MIN_VALUE, -5, 0, 1, 2, 3, 5, 15, Integer.MAX_VALUE).forEach {
            programmaticStyle {
                put(android.R.attr.lineSpacingExtra, it)

                assertEqualsTextViewStyleBuilder {
                    lineSpacingExtra(it)
                }
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
    fun lineSpacingExtra_res() {
        programmaticStyle {
            putRes(android.R.attr.lineSpacingExtra, ARBITRARY_RESOURCE_1)

            assertEqualsTextViewStyleBuilder {
                lineSpacingExtraRes(ARBITRARY_RESOURCE_1)
            }
        }
    }

    @Test
    fun lineSpacingMultiplier_value() {
        listOf(-5f, 0f, 8f, 10f, 11.5f, 17f).forEach {
            programmaticStyle {
                put(android.R.attr.lineSpacingMultiplier, it)

                assertEqualsTextViewStyleBuilder {
                    lineSpacingMultiplier(it)
                }
            }
        }
    }

    @Test
    fun lineSpacingMultiplier_res() {
        programmaticStyle {
            putRes(android.R.attr.lineSpacingMultiplier, ARBITRARY_RESOURCE_1)

            assertEqualsTextViewStyleBuilder {
                lineSpacingMultiplier(ARBITRARY_RESOURCE_1)
            }
        }
    }

    @Test
    fun maxLines_value() {
        listOf(Integer.MIN_VALUE, -5, 0, 1, 2, 3, 5, 15, Integer.MAX_VALUE).forEach {
            programmaticStyle {
                put(android.R.attr.maxLines, it)

                assertEqualsTextViewStyleBuilder {
                    maxLines(it)
                }
            }
        }
    }

    @Test
    fun maxLines_res() {
        programmaticStyle {
            putRes(android.R.attr.maxLines, ARBITRARY_RESOURCE_1)

            assertEqualsTextViewStyleBuilder {
                maxLinesRes(ARBITRARY_RESOURCE_1)
            }
        }
    }

    @Test
    fun minLines_value() {
        listOf(Integer.MIN_VALUE, -5, 0, 1, 2, 3, 5, 15, Integer.MAX_VALUE).forEach {
            programmaticStyle {
                put(android.R.attr.minLines, it)

                assertEqualsTextViewStyleBuilder {
                    minLines(it)
                }
            }
        }
    }

    @Test
    fun minLines_res() {
        programmaticStyle {
            putRes(android.R.attr.minLines, ARBITRARY_RESOURCE_1)

            assertEqualsTextViewStyleBuilder {
                minLinesRes(ARBITRARY_RESOURCE_1)
            }
        }
    }

    @Test
    fun minWidth_value() {
        assertEqualStylesValue(android.R.attr.minWidth, StyleBuilder::minWidth, ARBITRARY_DP_VALUES)
    }

    @Test
    fun minWidth_dp() {
        assertEqualStylesDp(android.R.attr.minWidth, StyleBuilder::minWidthDp, ARBITRARY_DP_VALUES)
    }

    @Test
    fun minWidth_res() {
        assertEqualStylesRes(android.R.attr.minWidth, StyleBuilder::minWidthRes)
    }

    @Test
    fun singleLine_value() {
        assertEqualStylesValue(android.R.attr.singleLine, StyleBuilder::singleLine, BOOL_VALUES)
    }

    @Test
    fun singleLine_res() {
        assertEqualStylesRes(android.R.attr.singleLine, StyleBuilder::singleLine)
    }

    @Test
    fun textAllCaps_value() {
        assertEqualStylesValue(android.R.attr.textAllCaps, StyleBuilder::textAllCaps, BOOL_VALUES)
    }

    @Test
    fun textAllCaps_res() {
        assertEqualStylesRes(android.R.attr.textAllCaps, StyleBuilder::textAllCaps)
    }

    @Test
    fun textColor_value() {
        val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
        )
        val colorStateList = ColorStateList(states, intArrayOf(Color.RED, Color.GREEN))
        assertEqualStylesValue(android.R.attr.textColor, StyleBuilder::textColor, listOf(colorStateList))
    }

    @Test
    fun textColor_res() {
        assertEqualStylesRes(android.R.attr.textColor, StyleBuilder::textColor)
    }

    @Test
    fun textColorHint_value() {
        val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
        )
        val colorStateList = ColorStateList(states, intArrayOf(Color.RED, Color.GREEN))
        assertEqualStylesValue(android.R.attr.textColorHint, StyleBuilder::textColorHint, listOf(colorStateList))
    }

    @Test
    fun textColorHint_res() {
        assertEqualStylesRes(android.R.attr.textColorHint, StyleBuilder::textColorHint)
    }

    @Test
    fun textSize_value() {
        assertEqualStylesValue(android.R.attr.textSize, StyleBuilder::textSize, ARBITRARY_TEXT_SIZE_VALUES)
    }

    @Test
    fun textSize_dp() {
        assertEqualStylesDp(android.R.attr.textSize, StyleBuilder::textSizeDp, ARBITRARY_TEXT_SIZE_VALUES)
    }

    @Test
    fun textSize_res() {
        assertEqualStylesRes(android.R.attr.textSize, StyleBuilder::textSizeRes)
    }
}
