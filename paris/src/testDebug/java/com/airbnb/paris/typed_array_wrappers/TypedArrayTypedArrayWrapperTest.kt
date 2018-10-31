package com.airbnb.paris.typed_array_wrappers

import android.content.Context
import android.graphics.Typeface
import com.airbnb.paris.R
import com.airbnb.paris.styles.ResourceStyle
import com.airbnb.paris.utils.ShadowResourcesCompat
import com.airbnb.paris.utils.assertTypefaceEquals
import com.airbnb.paris.utils.getFont
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Most TypedArrayWrapper methods are forwarded to an underlying TypedArray, they are not tested
 * here as it doesn't seem necessary
 */
@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowResourcesCompat::class])
class TypedArrayTypedArrayWrapperTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
    }

    @Test
    fun getColorStateList_null() {
        val typedArray = context.obtainStyledAttributes(
            R.style.Test_TypedArrayTypedArrayWrapper_GetColorStateList_NullTextColor,
            R.styleable.Paris_TextView
        )
        TypedArrayTypedArrayWrapper(context, typedArray).run {
            assertEquals(null, getColorStateList(R.styleable.Paris_TextView_android_textColor))
        }
    }

    @Test
    fun getDrawable_null() {
        val typedArray = context.obtainStyledAttributes(
            R.style.Test_TypedArrayTypedArrayWrapper_GetDrawable_NullBackground,
            R.styleable.Paris_View
        )
        TypedArrayTypedArrayWrapper(context, typedArray).run {
            assertEquals(null, getDrawable(R.styleable.Paris_View_android_background))
        }
    }

    @Test
    fun getResourceId_null() {
        // We only test the getResourceId case that uses our alternate null resources, otherwise
        // the call is forwarded to the underlying TypedArray
        val typedArray = context.obtainStyledAttributes(
            R.style.Test_TypedArrayTypedArrayWrapper_GetResourceId_NullBackground,
            R.styleable.Paris_View
        )
        TypedArrayTypedArrayWrapper(context, typedArray).run {
            assertEquals(0, getResourceId(R.styleable.Paris_View_android_background))
        }
    }

    @Test
    fun getString_null() {
        val typedArray = context.obtainStyledAttributes(
            R.style.Test_TypedArrayTypedArrayWrapper_GetString_NullText,
            R.styleable.Paris_TextView
        )
        TypedArrayTypedArrayWrapper(context, typedArray).run {
            assertEquals(null, getString(R.styleable.Paris_TextView_android_text))
        }
    }

    @Test
    fun getStyle_empty() {
        val typedArray = context.obtainStyledAttributes(
            R.style.Test_TypedArrayTypedArrayWrapper_GetStyle_StyleWithNoSubStyle,
            R.styleable.Test_TypedArrayTypedArrayWrapper_Styleable
        )
        TypedArrayTypedArrayWrapper(context, typedArray).run {
            assertEquals(
                ResourceStyle(0),
                getStyle(R.styleable.Test_TypedArrayTypedArrayWrapper_Styleable_test_typedArrayTypedArrayWrapper_style)
            )
        }
    }

    @Test
    fun getStyle_valid() {
        val typedArray = context.obtainStyledAttributes(
            R.style.Test_TypedArrayTypedArrayWrapper_GetStyle_StyleWithSubStyle,
            R.styleable.Test_TypedArrayTypedArrayWrapper_Styleable
        )
        TypedArrayTypedArrayWrapper(context, typedArray).run {
            assertEquals(
                ResourceStyle(R.style.Test_TypedArrayTypedArrayWrapper_GetStyle_SubStyle),
                getStyle(R.styleable.Test_TypedArrayTypedArrayWrapper_Styleable_test_typedArrayTypedArrayWrapper_style)
            )
        }
    }

    @Test
    fun getText_null() {
        val typedArray = context.obtainStyledAttributes(
            R.style.Test_TypedArrayTypedArrayWrapper_GetText_NullText,
            R.styleable.Paris_TextView
        )
        TypedArrayTypedArrayWrapper(context, typedArray).run {
            assertEquals(null, getText(R.styleable.Paris_TextView_android_text))
        }
    }

    @Test
    fun getFont_null() {
        val typedArray = context.obtainStyledAttributes(
            R.style.Test_TypedArrayTypedArrayWrapper_GetFont_Null,
            R.styleable.Paris_TextView
        )
        TypedArrayTypedArrayWrapper(context, typedArray).run {
            assertTypefaceEquals(null, getFont(R.styleable.Paris_TextView_android_fontFamily))
        }
    }

    @Test
    fun getFont_string() {
        val typedArray = context.obtainStyledAttributes(
            R.style.Test_TypedArrayTypedArrayWrapper_GetFont_String,
            R.styleable.Paris_TextView
        )
        TypedArrayTypedArrayWrapper(context, typedArray).run {
            assertTypefaceEquals(Typeface.create("sans-serif", Typeface.NORMAL), getFont(R.styleable.Paris_TextView_android_fontFamily))
        }
    }

    @Test
    fun getFont_resource() {
        val typedArray = context.obtainStyledAttributes(
            R.style.Test_TypedArrayTypedArrayWrapper_GetFont_Resource,
            R.styleable.Paris_TextView
        )
        TypedArrayTypedArrayWrapper(context, typedArray).run {
            assertTypefaceEquals(context.getFont(R.font.roboto_regular), getFont(R.styleable.Paris_TextView_android_fontFamily))
        }
    }
}
