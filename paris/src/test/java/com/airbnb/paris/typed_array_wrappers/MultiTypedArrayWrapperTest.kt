package com.airbnb.paris.typed_array_wrappers

import android.content.Context
import com.airbnb.paris.R
import com.airbnb.paris.attribute_values.ResourceId
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class MultiTypedArrayWrapperTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
    }

    @Test
    fun getColorStateList_null() {
        MultiTypedArrayWrapper(
            listOf(
                MapTypedArrayWrapper(
                    context, R.styleable.Paris_TextView, mapOf(
                        android.R.attr.textColor to null
                    )
                )
            ),
            R.styleable.Paris_TextView
        ).let {
            assertEquals(null, it.getColorStateList(R.styleable.Paris_TextView_android_textColor))
        }
    }

    @Test
    fun getColorStateList_nullRes() {
        MultiTypedArrayWrapper(
            listOf(
                MapTypedArrayWrapper(
                    context, R.styleable.Paris_TextView, mapOf(
                        android.R.attr.textColor to ResourceId(R.color.null_)
                    )
                )
            ),
            R.styleable.Paris_TextView
        ).let {
            assertEquals(null, it.getColorStateList(R.styleable.Paris_TextView_android_textColor))
        }
    }

    @Test
    fun getDrawable_null() {
        MultiTypedArrayWrapper(
            listOf(
                MapTypedArrayWrapper(
                    context, R.styleable.Paris_View, mapOf(
                        android.R.attr.background to null
                    )
                )
            ),
            R.styleable.Paris_View
        ).let {
            assertEquals(null, it.getDrawable(R.styleable.Paris_View_android_background))
        }
    }

    @Test
    fun getDrawable_nullRes() {
        MultiTypedArrayWrapper(
            listOf(
                MapTypedArrayWrapper(
                    context, R.styleable.Paris_View, mapOf(
                        android.R.attr.background to ResourceId(R.drawable.null_)
                    )
                )
            ),
            R.styleable.Paris_View
        ).let {
            assertEquals(null, it.getDrawable(R.styleable.Paris_View_android_background))
        }
    }

    @Test
    fun getResourceId_null() {
        MultiTypedArrayWrapper(
            listOf(
                MapTypedArrayWrapper(
                    context, R.styleable.Paris_View, mapOf(
                        android.R.attr.background to ResourceId(R.drawable.null_)
                    )
                )
            ),
            R.styleable.Paris_View
        ).let {
            assertEquals(0, it.getResourceId(R.styleable.Paris_View_android_background))
        }
    }
}
