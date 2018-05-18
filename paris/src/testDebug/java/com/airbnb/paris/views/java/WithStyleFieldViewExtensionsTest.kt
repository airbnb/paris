package com.airbnb.paris.views.java

import android.content.Context
import com.airbnb.paris.extensions.addTest
import com.airbnb.paris.extensions.style
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WithStyleFieldViewExtensionsTest {

    private lateinit var context: Context
    private lateinit var withStyleFieldView: WithStyleFieldView

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        withStyleFieldView = WithStyleFieldView(context)
    }

    @Test
    fun style_builderTest() {
        // Tests that the extension to set the linked "test" style exists
        withStyleFieldView.style { addTest() }
    }
}
