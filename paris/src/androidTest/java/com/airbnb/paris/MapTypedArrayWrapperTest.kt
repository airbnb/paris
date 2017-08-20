package com.airbnb.paris

import android.content.Context
import android.content.res.Resources
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.view.ViewGroup
import com.airbnb.paris.test.R
import com.airbnb.paris.utils.getFloat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapTypedArrayWrapperTest {

    private lateinit var context: Context
    private lateinit var res: Resources
    private lateinit var wrapper: MapTypedArrayWrapper

    private val bigAttrResToValueResMap = mapOf(
            R.attr.formatBoolean to R.bool.format_boolean,
            R.attr.formatColor to R.color.format_color,
            R.attr.formatDimension to R.dimen.format_dimension
    )
    private val attrResToValueResMaps = listOf(
            emptyMap(),
            mapOf(R.attr.formatBoolean to R.bool.format_boolean),
            bigAttrResToValueResMap
    )

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        res = context.resources
    }

    @Test
    fun isNull() {
        val map = mapOf(R.attr.formatReference to R.drawable.null_)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        assertTrue(wrapper.isNull(R.styleable.Formats_formatReference))
    }

    @Test
    fun getIndexCount() {
        attrResToValueResMaps.forEach {
            wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, it)
            assertEquals(it.size, wrapper.getIndexCount())
        }
    }

    @Test
    fun getIndex() {
        attrResToValueResMaps.forEach { attributeMap ->
            wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, attributeMap)
            // TODO There's an assumption here about the way keys are ordered
            val keys = attributeMap.keys.toList()
            (0 until attributeMap.size).forEach { at ->
                assertEquals(keys[at], wrapper.getIndex(at))
            }
        }
    }

    @Test
    fun hasValue() {
        attrResToValueResMaps.forEach { attributeMap ->
            wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, attributeMap)
            (0 until R.styleable.Formats.size).forEach { index ->
                val shouldHaveValue = attributeMap.contains(R.styleable.Formats[index])
                assertEquals(shouldHaveValue, wrapper.hasValue(index))
            }
        }
    }

    @Test
    fun getBoolean() {
        val map = mapOf(R.attr.formatBoolean to R.bool.format_boolean)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = res.getBoolean(R.bool.format_boolean)
        assertEquals(actual, wrapper.getBoolean(R.styleable.Formats_formatBoolean, !actual))
    }

    @Test
    fun getColor() {
        val map = mapOf(R.attr.formatColor to R.color.format_color)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = res.getColor(R.color.format_color)
        assertEquals(actual, wrapper.getColor(R.styleable.Formats_formatColor, actual-1))
    }

    @Test
    fun getColorStateList() {
        val map = mapOf(R.attr.formatColor to R.color.format_color)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = res.getColorStateList(R.color.format_color)
        assertEquals(actual, wrapper.getColorStateList(R.styleable.Formats_formatColor))
    }

    @Test
    fun getDimensionPixelSize() {
        val map = mapOf(R.attr.formatDimension to R.dimen.format_dimension)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = res.getDimensionPixelSize(R.dimen.format_dimension)
        assertEquals(actual, wrapper.getDimensionPixelSize(R.styleable.Formats_formatDimension, actual-1))
    }

    @Test
    fun getDrawable() {
        val map = mapOf(R.attr.formatReference to R.drawable.format_drawable)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = res.getDrawable(R.drawable.format_drawable)
        assertEquals(actual.constantState, wrapper.getDrawable(R.styleable.Formats_formatReference).constantState)
    }

    @Test
    fun getFloat() {
        val map = mapOf(R.attr.formatFloat to R.dimen.format_float)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = res.getFloat(R.dimen.format_float)
        assertEquals(actual, wrapper.getFloat(R.styleable.Formats_formatFloat, actual-1))
    }

    @Test
    fun getFraction() {
        val map = mapOf(R.attr.formatFraction to R.fraction.format_fraction)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = res.getFraction(R.fraction.format_fraction, 1, 2)
        assertEquals(actual, wrapper.getFraction(R.styleable.Formats_formatFraction, 1, 2, actual-1))
    }

    @Test
    fun getInt() {
        val map = mapOf(R.attr.formatInteger to R.integer.format_integer)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = res.getInteger(R.integer.format_integer)
        assertEquals(actual, wrapper.getInt(R.styleable.Formats_formatInteger, actual-1))
    }

    @Test
    fun getLayoutDimension_matchParent() {
        val map = mapOf(R.attr.formatDimension to R.dimen.format_layout_dimension_match_parent)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = ViewGroup.LayoutParams.MATCH_PARENT
        assertEquals(actual, wrapper.getLayoutDimension(R.styleable.Formats_formatDimension, actual-1))
    }

    @Test
    fun getLayoutDimension_wrapContent() {
        val map = mapOf(R.attr.formatDimension to R.dimen.format_layout_dimension_wrap_content)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = ViewGroup.LayoutParams.WRAP_CONTENT
        assertEquals(actual, wrapper.getLayoutDimension(R.styleable.Formats_formatDimension, actual-1))
    }

    @Test
    fun getLayoutDimension_px() {
        val map = mapOf(R.attr.formatDimension to R.dimen.format_dimension)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = res.getDimensionPixelSize(R.dimen.format_dimension)
        assertEquals(actual, wrapper.getLayoutDimension(R.styleable.Formats_formatDimension, actual-1))
    }

    @Test
    fun getResourceId() {
        // Using R.string.format_string as an arbitrary resource
        val map = mapOf(R.attr.formatReference to R.string.format_string)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = R.string.format_string
        assertEquals(actual, wrapper.getResourceId(R.styleable.Formats_formatReference, actual-1))
    }

    @Test
    fun getString() {
        val map = mapOf(R.attr.formatString to R.string.format_string)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = res.getString(R.string.format_string)
        assertEquals(actual, wrapper.getString(R.styleable.Formats_formatString))
    }

    @Test
    fun getText() {
        val map = mapOf(R.attr.formatString to R.string.format_char_sequence)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = res.getText(R.string.format_char_sequence)
        assertEquals(actual, wrapper.getText(R.styleable.Formats_formatString))
    }

    @Test
    fun getTextArray() {
        val map = mapOf(R.attr.formatReference to R.array.format_string_array)
        wrapper = MapTypedArrayWrapper(res, R.styleable.Formats, map)
        val actual = res.getTextArray(R.array.format_string_array)
        assertEquals(actual, wrapper.getTextArray(R.styleable.Formats_formatReference))
    }
}
