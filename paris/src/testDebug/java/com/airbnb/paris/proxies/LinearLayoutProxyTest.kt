package com.airbnb.paris.proxies

import android.content.Context
import android.view.Gravity
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
    private lateinit var viewGroup: LinearLayout
    private lateinit var proxy: LinearLayoutProxy

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        viewGroup = LinearLayout(context)
        proxy = LinearLayoutProxy(viewGroup)
    }

    @Test
    fun testGravity() {
        viewGroup.gravity = Gravity.NO_GRAVITY
        proxy.setGravity(Gravity.CENTER_VERTICAL)
        assertEquals(viewGroup.gravity, Gravity.CENTER_VERTICAL)
    }
}
