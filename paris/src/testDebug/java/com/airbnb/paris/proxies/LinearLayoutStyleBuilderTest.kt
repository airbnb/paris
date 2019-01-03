package com.airbnb.paris.proxies

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayoutStyleApplier
import com.airbnb.paris.styles.ProgrammaticStyle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class LinearLayoutStyleBuilderTest {

    private lateinit var context: Context
    private lateinit var linearLayout: LinearLayout
    private lateinit var builder: LinearLayoutStyleApplier.StyleBuilder

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        linearLayout = LinearLayout(context)
        linearLayout.layoutParams = LinearLayout.LayoutParams(100, 100)
        builder = LinearLayoutStyleApplier.StyleBuilder()
    }

    @Test
    fun layoutWeight() {
        val weight = 0.4f
        val style = builder.layoutWeight(weight).build()

        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.layout_weight, weight)
                .build(),
            style
        )
    }

}