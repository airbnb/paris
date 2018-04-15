package com.airbnb.paris.typed_array_wrappers

import android.content.Context
import com.airbnb.paris.R
import com.airbnb.paris.styles.ResourceStyle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Most TypedArrayWrapper methods are forwarded to an underlying TypedArray, they are not tested
 * here as it doesn't seem necessary
 */
@RunWith(RobolectricTestRunner::class)
class TypedArrayTypedArrayWrapperTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
    }

    @Test
    fun getResourceId_null() {
        // We only test the getResourceId case that uses our alternate null resources, otherwise
        // the call is forwarded to the underlying TypedArray
        val typedArray = context.obtainStyledAttributes(
                R.style.TestTypedArrayTypedArrayWrapper_GetResourceId_NullBackground,
                R.styleable.Paris_View
        )
        TypedArrayTypedArrayWrapper(typedArray).run {
            assertEquals(0, getResourceId(R.styleable.Paris_View_android_background))
        }
    }

    @Test
    fun getStyle_empty() {
        val typedArray = context.obtainStyledAttributes(
                R.style.TestTypedArrayTypedArrayWrapper_GetStyle_StyleWithNoSubStyle,
                R.styleable.TestTypedArrayTypedArrayWrapper_Styleable
        )
        TypedArrayTypedArrayWrapper(typedArray).run {
            assertEquals(
                    ResourceStyle(0),
                    getStyle(R.styleable.TestTypedArrayTypedArrayWrapper_Styleable_testTypedArrayTypedArrayWrapper_style)
            )
        }
    }

    @Test
    fun getStyle_valid() {
        val typedArray = context.obtainStyledAttributes(
                R.style.TestTypedArrayTypedArrayWrapper_GetStyle_StyleWithSubStyle,
                R.styleable.TestTypedArrayTypedArrayWrapper_Styleable
        )
        TypedArrayTypedArrayWrapper(typedArray).run {
            assertEquals(
                    ResourceStyle(R.style.TestTypedArrayTypedArrayWrapper_GetStyle_SubStyle),
                    getStyle(R.styleable.TestTypedArrayTypedArrayWrapper_Styleable_testTypedArrayTypedArrayWrapper_style)
            )
        }
    }
}
