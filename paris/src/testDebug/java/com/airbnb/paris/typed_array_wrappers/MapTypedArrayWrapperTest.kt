package com.airbnb.paris.typed_array_wrappers

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import com.airbnb.paris.R
import com.airbnb.paris.attribute_values.ResourceId
import com.airbnb.paris.utils.ShadowResourcesCompat
import com.airbnb.paris.utils.assertTypefaceEquals
import com.airbnb.paris.utils.getFont
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowResourcesCompat::class])
class MapTypedArrayWrapperTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
    }

    @Test
    fun getIndexCount_empty() {
        MapTypedArrayWrapper(context, R.styleable.Paris_View, emptyMap()).let {
            assertEquals(0, it.getIndexCount())
        }
    }

    @Test
    fun getIndexCount_single() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_View, mapOf(
                android.R.attr.alpha to .5f
            )
        ).let {
            assertEquals(1, it.getIndexCount())
        }
    }

    @Test
    fun getIndexCount_multiple() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_View, mapOf(
                android.R.attr.alpha to .5f,
                android.R.attr.background to Color.WHITE,
                android.R.attr.padding to 10
            )
        ).let {
            assertEquals(3, it.getIndexCount())
        }
    }

    @Test
    fun getIndexCount_superset() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_View, mapOf(
                android.R.attr.alpha to .5f,
                // textColor is not a view attribute so it should be ignored
                android.R.attr.textColor to Color.WHITE
            )
        ).let {
            assertEquals(1, it.getIndexCount())
        }
    }

    @Test
    fun getIndex_valid() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_View, mapOf(
                android.R.attr.padding to 10
            )
        ).let {
            assertEquals(R.styleable.Paris_View_android_padding, it.getIndex(0))
        }
    }

    @Test(expected = Exception::class)
    fun getIndex_nonExistent() {
        MapTypedArrayWrapper(context, R.styleable.Paris_View, emptyMap()).getIndex(0)
    }

    @Test
    fun hasValue_empty() {
        MapTypedArrayWrapper(context, R.styleable.Paris_View, emptyMap()).let {
            assertFalse(it.hasValue(0))
        }
    }

    @Test
    fun hasValue_true() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_View, mapOf(
                android.R.attr.alpha to .5f
            )
        ).let {
            assertTrue(it.hasValue(R.styleable.Paris_View_android_alpha))
        }
    }

    @Test
    fun hasValue_false() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_View, mapOf(
                android.R.attr.alpha to .5f
            )
        ).let {
            assertFalse(it.hasValue(R.styleable.Paris_View_android_background))
        }
    }

    @Test(expected = Exception::class)
    fun getBoolean_empty() {
        MapTypedArrayWrapper(context, R.styleable.Paris_View, emptyMap()).getBoolean(0)
    }

    @Test
    fun getBoolean_valid() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_View, mapOf(
                R.attr.ignoreLayoutWidthAndHeight to true
            )
        ).let {
            assertTrue(it.getBoolean(R.styleable.Paris_View_ignoreLayoutWidthAndHeight))
        }
    }

    @Test
    fun getBoolean_wrongAttributeType() {
        // Surprisingly this works because, while attribute formats are enforced in XML, there is no
        // way of doing so programmatically
        MapTypedArrayWrapper(
            context, R.styleable.Paris_View, mapOf(
                android.R.attr.alpha to true
            )
        ).getBoolean(R.styleable.Paris_View_android_alpha)
    }

    @Test(expected = Exception::class)
    fun getBoolean_wrongValueType() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_View, mapOf(
                R.attr.ignoreLayoutWidthAndHeight to Color.WHITE
            )
        ).getBoolean(R.styleable.Paris_View_ignoreLayoutWidthAndHeight)
    }

    @Test
    fun getColorStateList_null() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_TextView, mapOf(
                android.R.attr.textColor to null
            )
        ).let {
            assertEquals(null, it.getColorStateList(R.styleable.Paris_TextView_android_textColor))
        }
    }

    @Test
    fun getColorStateList_nullRes() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_TextView, mapOf(
                android.R.attr.textColor to ResourceId(R.color.null_)
            )
        ).let {
            assertEquals(null, it.getColorStateList(R.styleable.Paris_TextView_android_textColor))
        }
    }

    @Test
    fun getDrawable_null() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_View, mapOf(
                android.R.attr.background to null
            )
        ).let {
            assertEquals(null, it.getDrawable(R.styleable.Paris_View_android_background))
        }
    }

    @Test
    fun getDrawable_nullRes() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_View, mapOf(
                android.R.attr.background to ResourceId(R.drawable.null_)
            )
        ).let {
            assertEquals(null, it.getDrawable(R.styleable.Paris_View_android_background))
        }
    }

    @Test
    fun getResourceId_null() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_View, mapOf(
                android.R.attr.background to ResourceId(R.drawable.null_)
            )
        ).let {
            assertEquals(0, it.getResourceId(R.styleable.Paris_View_android_background))
        }
    }

    @Test
    fun getFont_null() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_TextView, mapOf(
                android.R.attr.fontFamily to null
            )
        ).let {
            assertTypefaceEquals(null, it.getFont(R.styleable.Paris_TextView_android_fontFamily))
        }
    }

    @Test
    fun getFont_nullRes() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_TextView, mapOf(
                android.R.attr.fontFamily to ResourceId(R.font.null_)
            )
        ).let {
            assertTypefaceEquals(null, it.getFont(R.styleable.Paris_TextView_android_fontFamily))
        }
    }

    @Test
    fun getFont_string() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_TextView, mapOf(
                android.R.attr.fontFamily to "sans-serif"
            )
        ).let {
            assertTypefaceEquals(Typeface.create("sans-serif", Typeface.NORMAL), it.getFont(R.styleable.Paris_TextView_android_fontFamily))
        }
    }

    @Test
    fun getFont_resource() {
        MapTypedArrayWrapper(
            context, R.styleable.Paris_TextView, mapOf(
                android.R.attr.fontFamily to ResourceId(R.font.roboto_regular)
            )
        ).let {
            assertTypefaceEquals(context.getFont(R.font.roboto_regular), it.getFont(R.styleable.Paris_TextView_android_fontFamily))
        }
    }
}
