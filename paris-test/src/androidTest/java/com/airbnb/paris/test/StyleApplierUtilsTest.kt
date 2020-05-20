package com.airbnb.paris.test

import android.util.LayoutDirection
import android.util.TypedValue
import android.view.ViewGroup
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.airbnb.paris.StyleApplierUtils
import com.airbnb.paris.styles.ResourceStyle
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StyleApplierUtilsTest {

    private val context = InstrumentationRegistry.getTargetContext()!!
    lateinit var myOtherView: MyOtherView
    lateinit var myViewApplier: MyOtherViewStyleApplier

    init {
        // Necessary to test AppCompat attributes like "?attr/selectableItemBackground"
        // TODO Not working for background() test
        context.setTheme(R.style.Theme_AppCompat)
    }

    @Before
    fun setup() {
        myOtherView = MyOtherView(context)
        myViewApplier = MyOtherViewStyleApplier(myOtherView)
    }

    @Test
    fun subStylesSameStyle() {
        StyleApplierUtils.assertSameAttributes(
            myViewApplier,
            ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1),
            ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1)
        )
    }

    @Test
    fun subStylesSameAttributes() {
        StyleApplierUtils.assertSameAttributes(
            myViewApplier,
            ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1),
            ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_2)
        )
    }

    @Test(expected = AssertionError::class)
    fun subStylesDifferentAttributes() {
        StyleApplierUtils.assertSameAttributes(
            myViewApplier,
            ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1),
            ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textSizePadding)
        )
    }

    @Test
    fun emptyStyleSameStyle() {
        StyleApplierUtils.assertSameAttributes(
            myViewApplier,
            ResourceStyle(R.style.Empty),
            ResourceStyle(R.style.Empty)
        )
    }

    @Test(expected = AssertionError::class)
    fun emptyStyleDifferentAttributes() {
        StyleApplierUtils.assertSameAttributes(
            myViewApplier,
            ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1),
            ResourceStyle(R.style.Empty)
        )
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

        StyleApplierUtils.assertSameAttributes(
            myViewApplier,
            ResourceStyle(R.style.StyleApplierUtilsTest_MyView_active),
            ResourceStyle(R.style.Empty)
        )
    }

    @Test
    fun styleNotAppliedToSubview() {
        // Checks that running the same attributes assertion doesn't actually modify the applier's
        // subviews in any way

        myOtherView.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, 10f)
        Assert.assertEquals(10f, myOtherView.title.textSize)
        StyleApplierUtils.assertSameAttributes(
            myViewApplier,
            myViewApplier.builder()
                .titleStyle({ it.textSize(15) })
                .build(),
            myViewApplier.builder()
                .titleStyle({ it.textSize(15) })
                .build()
        )
        Assert.assertEquals(10f, myOtherView.title.textSize)
    }

    @Test
    fun testSetPaddingLTR() {
        Assert.assertEquals(0, myOtherView.getPaddingLeft())
        Assert.assertEquals(0, myOtherView.getPaddingRight())

        myViewApplier.builder().paddingStart(100).apply()

        Assert.assertEquals(0, myOtherView.getPaddingRight())
        Assert.assertEquals(100, myOtherView.getPaddingLeft())

        myViewApplier.builder().paddingEnd(200).apply()

        Assert.assertEquals(200, myOtherView.getPaddingRight())
        Assert.assertEquals(100, myOtherView.getPaddingLeft())
    }

    @Test
    fun testSetPaddingRTL() {
        Assert.assertEquals(0, myOtherView.getPaddingLeft())
        Assert.assertEquals(0, myOtherView.getPaddingRight())

        myOtherView.setLayoutDirection(LayoutDirection.RTL);

        myViewApplier.builder().paddingStart(100).apply()

        Assert.assertEquals(100, myOtherView.getPaddingRight())
        Assert.assertEquals(0, myOtherView.getPaddingLeft())

        myViewApplier.builder().paddingEnd(200).apply()

        Assert.assertEquals(100, myOtherView.getPaddingRight())
        Assert.assertEquals(200, myOtherView.getPaddingLeft())
    }

    @Test
    fun testSetLayoutMarginLTR() {
        myViewApplier
            .builder()
            .layoutMarginLeft(0)
            .layoutMarginRight(0)
            .apply()

        Assert.assertEquals(0, (myOtherView.getLayoutParams() as ViewGroup.MarginLayoutParams).leftMargin)
        Assert.assertEquals(0, (myOtherView.getLayoutParams() as ViewGroup.MarginLayoutParams).rightMargin)

        myViewApplier.builder().layoutMarginStart(100).apply()

        Assert.assertEquals(100, (myOtherView.getLayoutParams() as ViewGroup.MarginLayoutParams).leftMargin)
        Assert.assertEquals(0, (myOtherView.getLayoutParams() as ViewGroup.MarginLayoutParams).rightMargin)

        myViewApplier.builder().layoutMarginEnd(200).apply()

        Assert.assertEquals(100, (myOtherView.getLayoutParams() as ViewGroup.MarginLayoutParams).leftMargin)
        Assert.assertEquals(200, (myOtherView.getLayoutParams() as ViewGroup.MarginLayoutParams).rightMargin)
    }

    @Test
    fun testSetLayoutMarginRTL() {
        myViewApplier
            .builder()
            .layoutMarginLeft(0)
            .layoutMarginRight(0)
            .apply()

        Assert.assertEquals(0, (myOtherView.getLayoutParams() as ViewGroup.MarginLayoutParams).leftMargin)
        Assert.assertEquals(0, (myOtherView.getLayoutParams() as ViewGroup.MarginLayoutParams).rightMargin)

        myOtherView.setLayoutDirection(LayoutDirection.RTL);

        myViewApplier.builder().layoutMarginStart(100).apply()

        Assert.assertEquals(0, (myOtherView.getLayoutParams() as ViewGroup.MarginLayoutParams).leftMargin)
        Assert.assertEquals(100, (myOtherView.getLayoutParams() as ViewGroup.MarginLayoutParams).rightMargin)

        myViewApplier.builder().layoutMarginEnd(200).apply()

        Assert.assertEquals(200, (myOtherView.getLayoutParams() as ViewGroup.MarginLayoutParams).leftMargin)
        Assert.assertEquals(100, (myOtherView.getLayoutParams() as ViewGroup.MarginLayoutParams).rightMargin)
    }
}
