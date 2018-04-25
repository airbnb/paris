package com.airbnb.paris

import android.content.Context
import android.content.res.Resources
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.airbnb.paris.attribute_values.ResourceId
import com.airbnb.paris.styles.MultiStyle
import com.airbnb.paris.styles.ProgrammaticStyle
import com.airbnb.paris.test.R
import com.airbnb.paris.typed_array_wrappers.MapTypedArrayWrapper
import com.airbnb.paris.typed_array_wrappers.MultiTypedArrayWrapper
import com.airbnb.paris.utils.getFloat
import com.airbnb.paris.utils.getStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MultiTypedArrayWrapperTest {

    private lateinit var context: Context
    private lateinit var res: Resources
    private lateinit var multi: MultiTypedArrayWrapper

    private val attrResToValueResMaps = listOf(
            emptyMap(),
            mapOf(R.attr.formatBoolean to ResourceId(R.bool.format_boolean)),
            mapOf(
                    R.attr.formatBoolean to ResourceId(R.bool.format_boolean_2),
                    R.attr.formatColor to ResourceId(R.color.format_color),
                    R.attr.formatDimension to ResourceId(R.dimen.format_dimension),
                    // This attr is not included in R.styleable.Format, as a result it should be ignored by
                    // the MapTypedArrayWrapper
                    R.attr.background to ResourceId(R.color.format_color)
            ),
            mapOf(
                    R.attr.formatColor to ResourceId(R.color.format_color_2),
                    R.attr.formatInteger to ResourceId(R.integer.format_integer),
                    R.attr.background to ResourceId(R.color.format_color_2)
            )
    )

    private fun newFormatWrappers() =
            attrResToValueResMaps.map { newFormatWrapper(it) }

    private fun newFormatWrapper(attrResToValueResMap: Map<Int, Any>) =
            MapTypedArrayWrapper(context, R.styleable.Formats, attrResToValueResMap)

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        res = context.resources
    }

    @Test
    fun getIndexCount() {
        multi = MultiTypedArrayWrapper(newFormatWrappers(), R.styleable.Formats)
        assertEquals(4, multi.getIndexCount())
    }

    @Test
    fun getIndex() {
        multi = MultiTypedArrayWrapper(newFormatWrappers(), R.styleable.Formats)
        val indexes = mutableListOf(
                R.styleable.Formats_formatBoolean,
                R.styleable.Formats_formatColor,
                R.styleable.Formats_formatDimension,
                R.styleable.Formats_formatInteger
        )
        (0 until indexes.size).forEach { at ->
            indexes.remove(multi.getIndex(at))
        }
        assertTrue(indexes.isEmpty())
    }

    @Test
    fun hasValue() {
        multi = MultiTypedArrayWrapper(newFormatWrappers(), R.styleable.Formats)
        listOf(
                R.styleable.Formats_formatBoolean,
                R.styleable.Formats_formatColor,
                R.styleable.Formats_formatDimension,
                R.styleable.Formats_formatInteger
        ).forEach { index ->
            assertEquals(true, multi.hasValue(index))
        }
    }

    @Test
    fun getBoolean() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatBoolean to ResourceId(R.bool.format_boolean)),
                        mapOf(R.attr.formatBoolean to ResourceId(R.bool.format_boolean_2))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = res.getBoolean(R.bool.format_boolean_2)
        assertEquals(actual, multi.getBoolean(R.styleable.Formats_formatBoolean))
    }

    @Test
    fun getColor() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatColor to ResourceId(R.color.format_color)),
                        mapOf(R.attr.formatColor to ResourceId(R.color.format_color_2))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = res.getColor(R.color.format_color_2)
        assertEquals(actual, multi.getColor(R.styleable.Formats_formatColor))
    }

    @Test
    fun getColorStateList() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatColor to ResourceId(R.color.format_color)),
                        mapOf(R.attr.formatColor to ResourceId(R.color.format_color_2))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = res.getColorStateList(R.color.format_color_2)
        assertEquals(actual, multi.getColorStateList(R.styleable.Formats_formatColor))
    }

    @Test
    fun getDimensionPixelSize() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatDimension to ResourceId(R.dimen.format_dimension)),
                        mapOf(R.attr.formatDimension to ResourceId(R.dimen.format_dimension_2))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = res.getDimensionPixelSize(R.dimen.format_dimension_2)
        assertEquals(actual, multi.getDimensionPixelSize(R.styleable.Formats_formatDimension))
    }

    @Test
    fun getDrawable() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatReference to ResourceId(R.drawable.format_drawable)),
                        mapOf(R.attr.formatReference to ResourceId(R.drawable.format_drawable_2))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = res.getDrawable(R.drawable.format_drawable_2)
        assertEquals(actual.constantState, multi.getDrawable(R.styleable.Formats_formatReference)?.constantState)
    }

    @Test
    fun getFloat() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatFloat to ResourceId(R.dimen.format_float)),
                        mapOf(R.attr.formatFloat to ResourceId(R.dimen.format_float_2))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = res.getFloat(R.dimen.format_float_2)
        assertEquals(actual, multi.getFloat(R.styleable.Formats_formatFloat))
    }

    @Test
    fun getFraction() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatFraction to ResourceId(R.fraction.format_fraction)),
                        mapOf(R.attr.formatFraction to ResourceId(R.fraction.format_fraction_2))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = res.getFraction(R.fraction.format_fraction_2, 1, 2)
        assertEquals(actual, multi.getFraction(R.styleable.Formats_formatFraction, 1, 2))
    }

    @Test
    fun getInt() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatInteger to ResourceId(R.integer.format_integer)),
                        mapOf(R.attr.formatInteger to ResourceId(R.integer.format_integer_2))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = res.getInteger(R.integer.format_integer_2)
        assertEquals(actual, multi.getInt(R.styleable.Formats_formatInteger))
    }

    @Test
    fun getLayoutDimension() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatDimension to ResourceId(R.dimen.format_layout_dimension_match_parent)),
                        mapOf(R.attr.formatDimension to ResourceId(R.dimen.format_layout_dimension_wrap_content)),
                        mapOf(R.attr.formatDimension to ResourceId(R.dimen.format_dimension))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = res.getDimension(R.dimen.format_dimension).toInt()
        assertEquals(actual, multi.getLayoutDimension(R.styleable.Formats_formatDimension))
    }

    @Test
    fun getResourceId() {
        // Using R.string.format_string as an arbitrary resource
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatReference to ResourceId(R.string.format_string)),
                        mapOf(R.attr.formatReference to ResourceId(R.string.format_string_2))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = R.string.format_string_2
        assertEquals(actual, multi.getResourceId(R.styleable.Formats_formatReference))
    }

    @Test
    fun getString() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatString to ResourceId(R.string.format_string)),
                        mapOf(R.attr.formatString to ResourceId(R.string.format_string_2))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = res.getString(R.string.format_string_2)
        assertEquals(actual, multi.getString(R.styleable.Formats_formatString))
    }

    @Test
    fun getStyle_resourceStyle_singleStyle() {
        multi = MultiTypedArrayWrapper(
                listOf(mapOf(R.attr.formatReference to ResourceId(R.style.Green)))
                        .map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = res.getStyle(R.style.Green)
        assertEquals(actual, multi.getStyle(R.styleable.Formats_formatReference))
    }

    @Test
    fun getStyle_resourceStyle_multiStyle() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatReference to ResourceId(R.style.Green)),
                        mapOf(R.attr.formatReference to ResourceId(R.style.Red))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = MultiStyle("a_MultiTypedArrayWrapper_MultiStyle", listOf(res.getStyle(R.style.Green), res.getStyle(R.style.Red)))
        assertEquals(actual, multi.getStyle(R.styleable.Formats_formatReference))
    }

    @Test
    fun getStyle_programmaticStyle() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatReference to ProgrammaticStyle.builder()
                                .put(R.attr.formatBoolean, true)
                                .build()),
                        mapOf(R.attr.formatReference to ProgrammaticStyle.builder()
                                .put(R.attr.formatString, "my string")
                                .build())
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val subStyle = multi.getStyle(R.styleable.Formats_formatReference)
        val subTypedArray = subStyle.obtainStyledAttributes(context, R.styleable.Formats)

        assertEquals(true, subTypedArray.getBoolean(R.styleable.Formats_formatBoolean))
        assertEquals("my string", subTypedArray.getString(R.styleable.Formats_formatString))
    }

    @Test
    fun getStyle_programmaticStyle_precedence() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatReference to ProgrammaticStyle.builder()
                                .put(R.attr.formatString, "string1")
                                .build()),
                        mapOf(R.attr.formatReference to ProgrammaticStyle.builder()
                                .put(R.attr.formatString, "string2")
                                .build())
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val subStyle = multi.getStyle(R.styleable.Formats_formatReference)
        val subTypedArray = subStyle.obtainStyledAttributes(context, R.styleable.Formats)

        assertEquals("string2", subTypedArray.getString(R.styleable.Formats_formatString))
    }

    @Test
    fun getText() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatString to ResourceId(R.string.format_char_sequence)),
                        mapOf(R.attr.formatString to ResourceId(R.string.format_char_sequence_2))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = res.getText(R.string.format_char_sequence_2)
        assertEquals(actual, multi.getText(R.styleable.Formats_formatString))
    }

    @Test
    fun getTextArray() {
        multi = MultiTypedArrayWrapper(
                listOf(
                        mapOf(R.attr.formatReference to ResourceId(R.array.format_string_array)),
                        mapOf(R.attr.formatReference to ResourceId(R.array.format_string_array_2))
                ).map { newFormatWrapper(it) },
                R.styleable.Formats
        )
        val actual = res.getTextArray(R.array.format_string_array_2)
        assertEquals(actual, multi.getTextArray(R.styleable.Formats_formatReference))
    }
}
