package com.airbnb.paris.views.kotlin

import android.content.Context
import com.airbnb.paris.extensions.addTest
import com.airbnb.paris.extensions.style
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WithJvmStaticStylePropertyViewExtensionsTest {

    private lateinit var context: Context
    private lateinit var withJvmStaticStylePropertyView: WithJvmStaticStylePropertyView

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        withJvmStaticStylePropertyView = WithJvmStaticStylePropertyView(context)
    }

    @Test
    fun style_builderTest() {
        // Tests that the extension to set the linked "test" style exists
        withJvmStaticStylePropertyView.style { addTest() }
    }
}
