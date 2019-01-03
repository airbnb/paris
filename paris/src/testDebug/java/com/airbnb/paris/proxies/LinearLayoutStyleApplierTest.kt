package com.airbnb.paris.proxies

import android.content.Context
import android.widget.LinearLayout
import android.widget.LinearLayoutStyleApplier
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class LinearLayoutStyleApplierTest {

    private lateinit var context: Context
    private lateinit var linearLayout: LinearLayout
    private lateinit var applier: LinearLayoutStyleApplier
    private lateinit var builder: LinearLayoutStyleApplier.StyleBuilder

    @Before
    fun setup() {
        context = RuntimeEnvironment.application

        linearLayout = LinearLayout(context)
        linearLayout.layoutParams = LinearLayout.LayoutParams(100, 100)
        applier = LinearLayoutStyleApplier(linearLayout)
        builder = LinearLayoutStyleApplier.StyleBuilder()
    }

    @Test
    fun layoutWeight() {
        val weight = 0.91f
        applier.apply(builder.layoutWeight(weight).build())
        assertEquals(weight, (linearLayout.layoutParams as LinearLayout.LayoutParams).weight)
    }
}
