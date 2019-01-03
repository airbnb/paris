package com.airbnb.paris.proxies

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class LinearLayoutProxyTest {
    private lateinit var context: Context
    private lateinit var linearLayout: LinearLayout
    private lateinit var proxy: LinearLayoutProxy

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        linearLayout = LinearLayout(context)
        linearLayout.layoutParams = LinearLayout.LayoutParams(100, 100)
        proxy = LinearLayoutProxy(linearLayout)
    }

    @Test
    fun setLayoutWeight() {
        val weight = 0.78f

        proxy.setLayoutWeight(weight)
        assertEquals(weight, (linearLayout.layoutParams as LinearLayout.LayoutParams).weight)
    }
}