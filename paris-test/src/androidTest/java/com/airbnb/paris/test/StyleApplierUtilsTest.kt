package com.airbnb.paris.test

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.*
import com.airbnb.paris.StyleApplierUtils
import com.airbnb.paris.styles.ResourceStyle
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StyleApplierUtilsTest {

    private val context = InstrumentationRegistry.getTargetContext()!!
    lateinit var myView: MyView
    lateinit var myViewApplier: MyViewStyleApplier

    init {
        // Necessary to test AppCompat attributes like "?attr/selectableItemBackground"
        // TODO Not working for background() test
        context.setTheme(R.style.Theme_AppCompat)
    }

    @Before
    fun setup() {
        myView = MyView(context)
        myViewApplier = MyViewStyleApplier(myView)
    }

    @Test
    fun subStylesSameStyle() {
        StyleApplierUtils.assertSameAttributes(myViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1),
                ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1))
    }

    @Test
    fun subStylesSameAttributes() {
        StyleApplierUtils.assertSameAttributes(myViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1),
                ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_2))
    }

    @Test(expected = AssertionError::class)
    fun subStylesDifferentAttributes() {
        StyleApplierUtils.assertSameAttributes(myViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1),
                ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textSizePadding))
    }

    @Test
    fun emptyStyleSameStyle() {
        StyleApplierUtils.assertSameAttributes(myViewApplier,
                ResourceStyle(R.style.Empty),
                ResourceStyle(R.style.Empty))
    }

    @Test(expected = AssertionError::class)
    fun emptyStyleDifferentAttributes() {
        StyleApplierUtils.assertSameAttributes(myViewApplier,
                ResourceStyle(R.style.StyleApplierUtilsTest_MyView_titleStyle_textColorTextSizePadding_1),
                ResourceStyle(R.style.Empty))
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
                ResourceStyle(R.style.StyleApplierUtilsTest_MyView_active),
                ResourceStyle(R.style.Empty))
    }

    @Test
    fun styleNotAppliedToSubview() {
        // Checks that running the same attributes assertion doesn't actually modify the applier's
        // subviews in any way

        myView.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, 10f)
        Assert.assertEquals(10f, myView.title.textSize)
        StyleApplierUtils.assertSameAttributes(myViewApplier,
                myViewApplier.builder()
                        .titleStyle({ it.textSize(15) })
                        .build(),
                myViewApplier.builder()
                        .titleStyle({ it.textSize(15) })
                        .build()
        )
        Assert.assertEquals(10f, myView.title.textSize)
    }
}
