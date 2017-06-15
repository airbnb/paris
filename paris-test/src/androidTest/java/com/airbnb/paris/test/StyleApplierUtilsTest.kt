package com.airbnb.paris.test

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import com.airbnb.paris.Style
import com.airbnb.paris.StyleApplierUtils
import com.airbnb.paris.TextViewStyleApplier
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StyleApplierUtilsTest {

    lateinit var context: Context
    lateinit var applier: TextViewStyleApplier

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        applier = TextViewStyleApplier(TextView(context))
    }

    @Test
    fun sameStyle() {
        StyleApplierUtils.assertSameAttributes(applier,
                Style(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding),
                Style(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding))
    }

    @Test
    fun sameAttributes() {
        StyleApplierUtils.assertSameAttributes(applier,
                Style(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding),
                Style(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding))
    }

    @Test(expected = AssertionError::class)
    fun differentAttributes() {
        StyleApplierUtils.assertSameAttributes(applier,
                Style(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding),
                Style(R.style.StyleApplierUtilsTest_TextView_textSizePadding))
    }

    @Test
    fun inheritance() {
        StyleApplierUtils.assertSameAttributes(applier,
                Style(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding),
                Style(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_withInheritance))
    }
}
