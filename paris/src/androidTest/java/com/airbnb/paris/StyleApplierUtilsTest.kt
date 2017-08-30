package com.airbnb.paris

import android.content.Context
import android.graphics.Color
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import com.airbnb.paris.proxies.TextViewProxyStyleApplier
import com.airbnb.paris.styles.MultiStyle
import com.airbnb.paris.styles.ResourceStyle
import com.airbnb.paris.test.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StyleApplierUtilsTest {

    lateinit var context: Context
    lateinit var textViewApplier: TextViewProxyStyleApplier

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        // Necessary to test AppCompat attributes like "?attr/selectableItemBackground"
        // TODO Not working for background() test
        context.setTheme(R.style.Theme_AppCompat)
        textViewApplier = TextViewProxyStyleApplier(TextView(context))
    }

    @Test
    fun sameStyle() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1))
    }

    @Test
    fun sameAttributes() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_2))
    }

    @Test(expected = AssertionError::class)
    fun differentAttributes() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textSizePadding))
    }

    @Test
    fun inheritance() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_withInheritance))
    }

    @Test
    fun multiStyleResCombinationNoOverlap() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                MultiStyle("MultiStyle_textColorTextSizePadding",
                        R.style.StyleApplierUtilsTest_TextView_textColor,
                        R.style.StyleApplierUtilsTest_TextView_textSizePadding
                )
        )
    }

    @Test
    fun multiStyleResCombinationWithOverlap() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                MultiStyle("MultiStyle_textColorTextSizePadding",
                        R.style.StyleApplierUtilsTest_TextView_textColorTextSize,
                        R.style.StyleApplierUtilsTest_TextView_textSizePadding
                )
        )
    }

    @Test(expected = AssertionError::class)
    fun multiStyleResCombinationMissingAttribute() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                MultiStyle("MultiStyle_textColorTextSize",
                        R.style.StyleApplierUtilsTest_TextView_textColor,
                        R.style.StyleApplierUtilsTest_TextView_textSize
                )
        )
    }

    @Test(expected = AssertionError::class)
    fun multiStyleResCombinationAdditionalAttribute() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSize),
                MultiStyle("MultiStyle_textColorTextSizePadding",
                        R.style.StyleApplierUtilsTest_TextView_textColorTextSize,
                        R.style.StyleApplierUtilsTest_TextView_textSizePadding
                )
        )
    }

    @Test
    fun multiStyleCombinationWithOverlap() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                MultiStyle("MultiStyle_textColorTextSizePadding",
                        ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSize),
                        textViewApplier.builder()
                                .textSize(R.dimen.test_text_view_text_size)
                                .padding(R.dimen.test_view_padding)
                                .build()
                )
        )
    }

    @Test
    fun multiStyleCombinationNoOverlap() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                MultiStyle("MultiStyle_textColorTextSizePadding",
                        ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColor),
                        textViewApplier.builder()
                                .textSize(R.dimen.test_text_view_text_size)
                                .padding(R.dimen.test_view_padding)
                                .build()
                )
        )
    }

    @Test(expected = AssertionError::class)
    fun multiStyleCombinationMissingAttribute() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                MultiStyle("MultiStyle_textColorTextSize",
                        ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColor),
                        textViewApplier.builder().textSize(R.dimen.test_text_view_text_size).build()
                )
        )
    }

    @Test(expected = AssertionError::class)
    fun multiStyleCombinationAdditionalAttribute() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSize),
                MultiStyle("MultiStyle_textColorTextSizePadding",
                        ResourceStyle(R.style.StyleApplierUtilsTest_TextView_textColor),
                        textViewApplier.builder()
                                .textSize(R.dimen.test_text_view_text_size)
                                .padding(R.dimen.test_view_padding)
                                .build()
                )
        )
    }

    @Test(expected = AssertionError::class)
    fun missingFromBoth() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                textViewApplier.builder()
                        .textSize(R.dimen.test_text_view_text_size)
                        .build(),
                textViewApplier.builder()
                        .padding(R.dimen.test_view_padding)
                        .build(),
                textViewApplier.builder()
                        .textColor(Color.GREEN)
                        .build()
        )
    }
}