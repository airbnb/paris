package com.airbnb.paris

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.airbnb.paris.attribute_values.ColorValue
import com.airbnb.paris.attribute_values.DpValue
import com.airbnb.paris.attribute_values.ResourceId
import com.airbnb.paris.attribute_values.Styles
import com.airbnb.paris.styles.MultiStyle
import com.airbnb.paris.styles.ResourceStyle
import com.airbnb.paris.styles.Style
import com.airbnb.paris.test.R
import com.airbnb.paris.typed_array_wrappers.MapTypedArrayWrapper
import com.airbnb.paris.utils.dpToPx
import com.airbnb.paris.utils.getFloat
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("FunctionName")
@RunWith(AndroidJUnit4::class)
class MapTypedArrayWrapperTest {

    private lateinit var context: Context
    private lateinit var res: Resources
    private lateinit var wrapper: MapTypedArrayWrapper

    private val attrResToValueResMaps = listOf(
        emptyMap(),
        mapOf(R.attr.formatBoolean to ResourceId(R.bool.format_boolean)),
        mapOf(
            R.attr.formatBoolean to ResourceId(R.bool.format_boolean),
            R.attr.formatColor to ResourceId(R.color.format_color),
            R.attr.formatDimension to ResourceId(R.dimen.format_dimension),
            // This attr is not included in R.styleable.Format, as a result it should be ignored by
            // the MapTypedArrayWrapper
            R.attr.background to ResourceId(R.color.format_color)
        )
    )

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        res = context.resources
    }

    @Test
    fun getIndexCount() {
        attrResToValueResMaps.forEach {
            wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, it)
            val indexes = it.keys.map { R.styleable.Formats.indexOf(it) }.filter { it != -1 }
            assertEquals(indexes.size, wrapper.getIndexCount())
        }
    }

    @Test
    fun getIndex() {
        attrResToValueResMaps.forEach { attributeMap ->
            wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, attributeMap)
            // TODO There's an assumption here about the way indexes are ordered
            val indexes = attributeMap.keys.map { R.styleable.Formats.indexOf(it) }.filter { it != -1 }
            (0 until indexes.size).forEach { at ->
                // Ensures that all the indexes are valid
                Assert.assertNotEquals(-1, indexes[at])
                assertEquals(indexes[at], wrapper.getIndex(at))
            }
        }
    }

    @Test
    fun hasValue() {
        attrResToValueResMaps.forEach { attributeMap ->
            wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, attributeMap)
            (0 until R.styleable.Formats.size).forEach { index ->
                val shouldHaveValue = attributeMap.contains(R.styleable.Formats[index])
                assertEquals(shouldHaveValue, wrapper.hasValue(index))
            }
        }
    }

    @Test
    fun getBoolean() {
        val map = mapOf(R.attr.formatBoolean to ResourceId(R.bool.format_boolean))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = res.getBoolean(R.bool.format_boolean)
        assertEquals(actual, wrapper.getBoolean(R.styleable.Formats_formatBoolean))
    }

    @Test
    fun getColor() {
        val map = mapOf(R.attr.formatColor to ResourceId(R.color.format_color))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = res.getColor(R.color.format_color)
        assertEquals(actual, wrapper.getColor(R.styleable.Formats_formatColor))
    }

    @Test
    fun getColor_colorValueToColorInt() {
        val map = mapOf(R.attr.formatColor to ColorValue(Color.BLUE))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        assertEquals(Color.BLUE, wrapper.getColor(R.styleable.Formats_formatColor))
    }

    @Test
    fun getColorStateList() {
        val map = mapOf(R.attr.formatColor to ResourceId(R.color.format_color))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = res.getColorStateList(R.color.format_color)
        assertEquals(actual, wrapper.getColorStateList(R.styleable.Formats_formatColor))
    }

    @Test
    fun getColorStateList_colorValueToColorStateList() {
        val map = mapOf(R.attr.formatColor to ColorValue(Color.BLUE))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        assertEquals(Color.BLUE, wrapper.getColorStateList(R.styleable.Formats_formatColor)?.defaultColor)
    }

    @Test
    fun getDimensionPixelSize() {
        val map = mapOf(R.attr.formatDimension to ResourceId(R.dimen.format_dimension))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = res.getDimensionPixelSize(R.dimen.format_dimension)
        assertEquals(actual, wrapper.getDimensionPixelSize(R.styleable.Formats_formatDimension))
    }

    @Test
    fun getDimensionPixelSizeDpConversion() {
        val map = mapOf(R.attr.formatDimension to DpValue(10))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = res.dpToPx(10)
        assertEquals(actual, wrapper.getDimensionPixelSize(R.styleable.Formats_formatDimension))
    }

    @Ignore("Comparing drawables does not work in CI tests")
    @Test
    fun getDrawable() {
        val map = mapOf(R.attr.formatReference to ResourceId(R.drawable.format_drawable))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = ResourcesCompat.getDrawable(res, R.drawable.format_drawable, null)
        assertEquals(actual?.constantState, wrapper.getDrawable(R.styleable.Formats_formatReference)?.constantState)
    }

    @Test
    fun getFloat() {
        val map = mapOf(R.attr.formatFloat to ResourceId(R.dimen.format_float))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = res.getFloat(R.dimen.format_float)
        assertEquals(actual, wrapper.getFloat(R.styleable.Formats_formatFloat))
    }

    @Test
    fun getFraction() {
        val map = mapOf(R.attr.formatFraction to ResourceId(R.fraction.format_fraction))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = res.getFraction(R.fraction.format_fraction, 1, 2)
        assertEquals(actual, wrapper.getFraction(R.styleable.Formats_formatFraction, 1, 2))
    }

    @Test
    fun getInt() {
        val map = mapOf(R.attr.formatInteger to ResourceId(R.integer.format_integer))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = res.getInteger(R.integer.format_integer)
        assertEquals(actual, wrapper.getInt(R.styleable.Formats_formatInteger))
    }

    @Test
    fun getLayoutDimension_matchParent() {
        val map = mapOf(R.attr.formatDimension to ResourceId(R.dimen.format_layout_dimension_match_parent))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = ViewGroup.LayoutParams.MATCH_PARENT
        assertEquals(actual, wrapper.getLayoutDimension(R.styleable.Formats_formatDimension))
    }

    @Test
    fun getLayoutDimension_wrapContent() {
        val map = mapOf(R.attr.formatDimension to ResourceId(R.dimen.format_layout_dimension_wrap_content))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = ViewGroup.LayoutParams.WRAP_CONTENT
        assertEquals(actual, wrapper.getLayoutDimension(R.styleable.Formats_formatDimension))
    }

    @Test
    fun getLayoutDimension_px() {
        val map = mapOf(R.attr.formatDimension to ResourceId(R.dimen.format_dimension))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = res.getDimension(R.dimen.format_dimension).toInt()
        assertEquals(actual, wrapper.getLayoutDimension(R.styleable.Formats_formatDimension))
    }

    @Test
    fun getResourceId() {
        // Using R.string.format_string as an arbitrary resource
        val map = mapOf(R.attr.formatReference to ResourceId(R.string.format_string))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = R.string.format_string
        assertEquals(actual, wrapper.getResourceId(R.styleable.Formats_formatReference))
    }

    @Test
    fun getString() {
        val map = mapOf(R.attr.formatString to ResourceId(R.string.format_string))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = res.getString(R.string.format_string)
        assertEquals(actual, wrapper.getString(R.styleable.Formats_formatString))
    }

    @Test
    fun getStyle() {
        val map = mapOf(R.attr.formatReference to ResourceId(R.style.Green))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = ResourceStyle(R.style.Green)
        assertEquals(actual, wrapper.getStyle(R.styleable.Formats_formatReference))
    }

    @Test
    fun getStyle_styles() {
        // Attribute values of type "Styles" should be converted to a MultiStyle

        val styles = mutableListOf<Style>(ResourceStyle(R.style.Red), ResourceStyle(R.style.Green))
        val map = mapOf(R.attr.formatReference to Styles(styles))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = MultiStyle("a_MapTypedArrayWrapper_MultiStyle", styles)
        assertEquals(actual, wrapper.getStyle(R.styleable.Formats_formatReference))
    }

    @Test
    fun getText() {
        val map = mapOf(R.attr.formatString to ResourceId(R.string.format_char_sequence))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = res.getText(R.string.format_char_sequence)
        assertEquals(actual, wrapper.getText(R.styleable.Formats_formatString))
    }

    @Test
    fun getTextArray() {
        val map = mapOf(R.attr.formatReference to ResourceId(R.array.format_string_array))
        wrapper = MapTypedArrayWrapper(context, R.styleable.Formats, map)
        val actual = res.getTextArray(R.array.format_string_array)
        assertEquals(actual, wrapper.getTextArray(R.styleable.Formats_formatReference))
    }
}
