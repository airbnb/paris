package com.airbnb.paris.proxies

import android.content.Context
import android.view.ViewGroup
import android.view.ViewGroupStyleApplier
import android.widget.LinearLayout
import com.airbnb.paris.styles.ProgrammaticStyle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ViewGroupStyleBuilderTest {

    private lateinit var context: Context
    private lateinit var viewGroup: ViewGroup
    private lateinit var builder: ViewGroupStyleApplier.StyleBuilder

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        // An arbitrary ViewGroup
        viewGroup = LinearLayout(context)
        builder = ViewGroupStyleApplier.StyleBuilder()
    }

    @Test
    fun animateLayoutChanges() {
        val style = builder.animateLayoutChanges(true).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.animateLayoutChanges, true)
                .build(),
            style
        )
    }

    @Test
    fun clipChildren() {
        val style = builder.clipChildren(true).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.clipChildren, true)
                .build(),
            style
        )
    }

    @Test
    fun clipToPadding() {
        val style = builder.clipToPadding(true).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.clipToPadding, true)
                .build(),
            style
        )
    }
}
