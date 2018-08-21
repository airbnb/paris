package com.airbnb.paris.typed_array_wrappers

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import com.airbnb.paris.R
import com.airbnb.paris.attribute_values.ResourceId
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

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowResourcesCompat::class])
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


    @Test
    fun getFont_null() {
        MultiTypedArrayWrapper(
            listOf(
                MapTypedArrayWrapper(
                    context, R.styleable.Paris_TextView, mapOf(
                        android.R.attr.fontFamily to null
                    )
                )
            ),
            R.styleable.Paris_TextView
        ).let {
            assertEquals(null, it.getFont(R.styleable.Paris_TextView_android_fontFamily))
        }
    }

    @Test
    fun getFont_nullRes() {
        MultiTypedArrayWrapper(
            listOf(
                MapTypedArrayWrapper(
                    context, R.styleable.Paris_TextView, mapOf(
                        android.R.attr.fontFamily to ResourceId(R.font.null_)
                    )
                )
            ),
            R.styleable.Paris_TextView
        ).let {
            assertEquals(null, it.getFont(R.styleable.Paris_TextView_android_fontFamily))
        }
    }

    @Test
    fun getFont_string() {
        MultiTypedArrayWrapper(
            listOf(
                MapTypedArrayWrapper(
                    context, R.styleable.Paris_TextView, mapOf(
                        android.R.attr.fontFamily to "sans-serif"
                    )
                )
            ),
            R.styleable.Paris_TextView
        ).let {
            assertTypefaceEquals(Typeface.create("sans-serif", Typeface.NORMAL), it.getFont(R.styleable.Paris_TextView_android_fontFamily))
        }
    }

    @Test
    fun getFont_resource() {
        MultiTypedArrayWrapper(
            listOf(
                MapTypedArrayWrapper(
                    context, R.styleable.Paris_TextView, mapOf(
                        android.R.attr.fontFamily to ResourceId(R.font.roboto_regular)
                    )
                )
            ),
            R.styleable.Paris_TextView
        ).let {
            assertTypefaceEquals(context.getFont(R.font.roboto_regular), it.getFont(R.styleable.Paris_TextView_android_fontFamily))
        }
    }
}
