package com.airbnb.paris.test

import android.content.Context
import android.graphics.Color
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import com.airbnb.paris.MultiStyle
import com.airbnb.paris.SimpleStyle
import com.airbnb.paris.StyleApplierUtils
import com.airbnb.paris.proxy.TextViewProxyStyleApplier
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StyleApplierUtilsTest {

    lateinit var context: Context
    lateinit var textViewApplier: TextViewProxyStyleApplier
    lateinit var myViewApplier: MyViewStyleApplier

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        // Necessary to test AppCompat attributes like "?attr/selectableItemBackground"
        // TODO Not working for background() test
        context.setTheme(R.style.Theme_AppCompat)
        textViewApplier = TextViewProxyStyleApplier(TextView(context))
        myViewApplier = MyViewStyleApplier(MyView(context))
    }

    @Test
    fun sameStyle() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1))
    }

    @Test
    fun sameAttributes() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_2))
    }

    @Test(expected = AssertionError::class)
    fun differentAttributes() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textSizePadding))
    }

    @Test
    fun inheritance() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_withInheritance))
    }

    @Test
    fun multiStyleResCombinationNoOverlap() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                MultiStyle("MultiStyle_textColorTextSizePadding",
                        R.style.StyleApplierUtilsTest_TextView_textColor,
                        R.style.StyleApplierUtilsTest_TextView_textSizePadding
                )
        )
    }

    @Test
    fun multiStyleResCombinationWithOverlap() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                MultiStyle("MultiStyle_textColorTextSizePadding",
                        R.style.StyleApplierUtilsTest_TextView_textColorTextSize,
                        R.style.StyleApplierUtilsTest_TextView_textSizePadding
                )
        )
    }

    @Test(expected = AssertionError::class)
    fun multiStyleResCombinationMissingAttribute() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                MultiStyle("MultiStyle_textColorTextSize",
                        R.style.StyleApplierUtilsTest_TextView_textColor,
                        R.style.StyleApplierUtilsTest_TextView_textSize
                )
        )
    }

    @Test(expected = AssertionError::class)
    fun multiStyleResCombinationAdditionalAttribute() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSize),
                MultiStyle("MultiStyle_textColorTextSizePadding",
                        R.style.StyleApplierUtilsTest_TextView_textColorTextSize,
                        R.style.StyleApplierUtilsTest_TextView_textSizePadding
                )
        )
    }

    @Test
    fun multiStyleCombinationWithOverlap() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                MultiStyle("MultiStyle_textColorTextSizePadding",
                        SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSize),
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
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                MultiStyle("MultiStyle_textColorTextSizePadding",
                        SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColor),
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
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSizePadding_1),
                MultiStyle("MultiStyle_textColorTextSize",
                        SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColor),
                        textViewApplier.builder().textSize(R.dimen.test_text_view_text_size).build()
                )
        )
    }

    @Test(expected = AssertionError::class)
    fun multiStyleCombinationAdditionalAttribute() {
        StyleApplierUtils.assertSameAttributes(textViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColorTextSize),
                MultiStyle("MultiStyle_textColorTextSizePadding",
                        SimpleStyle(R.style.StyleApplierUtilsTest_TextView_textColor),
                        textViewApplier.builder()
                                .textSize(R.dimen.test_text_view_text_size)
                                .padding(R.dimen.test_view_padding)
                                .build()
                )
        )
    }

    @Test
    fun subStylesSameStyle() {
        StyleApplierUtils.assertSameAttributes(myViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1),
                SimpleStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1))
    }

    @Test
    fun subStylesSameAttributes() {
        StyleApplierUtils.assertSameAttributes(myViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1),
                SimpleStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_2))
    }

    @Test(expected = AssertionError::class)
    fun subStylesDifferentAttributes() {
        StyleApplierUtils.assertSameAttributes(myViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1),
                SimpleStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textSizePadding))
    }

    @Test
    fun emptyStyleSameStyle() {
        StyleApplierUtils.assertSameAttributes(myViewApplier,
                SimpleStyle(R.style.Empty),
                SimpleStyle(R.style.Empty))
    }

    @Test(expected = AssertionError::class)
    fun emptyStyleDifferentAttributes() {
        StyleApplierUtils.assertSameAttributes(myViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1),
                SimpleStyle(R.style.Empty))
    }

    @Test
    fun background() {
        // TODO Why isn't this working?
        //StyleApplierUtils.assertSameAttributes(myViewApplier,
        //        Style(R.style.StyleApplierUtilsTest_MyView_background),
        //        Style(R.style.StyleApplierUtilsTest_MyView_background_other))
    }

    @Test
    fun defaultValues() {
        // Because MyView specifies a default value for active this should be fine

        StyleApplierUtils.assertSameAttributes(myViewApplier,
                SimpleStyle(R.style.StyleApplierUtilsTest_MyView_active),
                SimpleStyle(R.style.Empty))
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
