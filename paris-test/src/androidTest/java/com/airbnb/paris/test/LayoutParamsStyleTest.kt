package com.airbnb.paris.test

import android.content.Context
import android.content.res.Resources
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import com.airbnb.paris.LayoutParamsStyle
import com.airbnb.paris.Paris
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LayoutParamsStyleTest {

    lateinit var context: Context
    lateinit var res: Resources
    lateinit var view: View

    var params: ViewGroup.LayoutParams? = null

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        res = context.resources
        view = View(context)
    }

    @Test
    fun layoutParamsWidthHeight() {
        params = view.layoutParams
        assertNull(params)

        Paris.change(view).apply(R.style.Test_LayoutParams_width_height)

        params = view.layoutParams
        assertNotNull(params)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_layout_params_width), params!!.width)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_layout_params_height), params!!.height)
    }

    @Test
    fun layoutParamsIgnoreWidthHeight() {
        params = view.layoutParams
        assertNull(params)

        Paris.change(view)
                .addOption(LayoutParamsStyle.Option.IgnoreLayoutWidthAndHeight)
                .apply(R.style.Test_LayoutParams_width_height)

        params = view.layoutParams
        assertNull(params)
    }
}
