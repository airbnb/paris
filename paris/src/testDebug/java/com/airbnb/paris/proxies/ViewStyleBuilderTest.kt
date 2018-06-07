package com.airbnb.paris.proxies

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewStyleApplier
import com.airbnb.paris.R
import com.airbnb.paris.attribute_values.ResourceId
import com.airbnb.paris.styles.ProgrammaticStyle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ViewStyleBuilderTest {

    private lateinit var context: Context
    private lateinit var view: View
    private lateinit var builder: ViewStyleApplier.StyleBuilder

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        view = View(context)
        builder = ViewStyleApplier.StyleBuilder()
    }

    @Test
    fun foreground_null() {
        val style = builder.foreground(null).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.foreground, null)
                .build(),
            style
        )
    }

    @Test
    fun background_null() {
        val style = builder.background(null).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.background, null)
                .build(),
            style
        )
    }

    @Test
    fun backgroundTintRes() {
        val style = builder.backgroundTintRes(android.R.color.black).build()
        assertEquals(
                ProgrammaticStyle.builder()
                        .put(
                                android.R.attr.backgroundTint,
                                ResourceId(android.R.color.black)
                        )
                        .build(),
                style
        )
    }

    @Test
    fun backgroundTintColorStateList() {
        val style = builder.backgroundTint(ContextCompat.getColorStateList(context, android.R.color.black)).build()
        assertEquals(
                ProgrammaticStyle.builder()
                        .put(
                                android.R.attr.backgroundTint,
                                ContextCompat.getColorStateList(context, android.R.color.black)
                        )
                        .build(),
                style
        )
    }

    @Test
    fun backgroundTintMode() {
        val style = builder.backgroundTintMode(ViewProxy.PORTERDUFF_MODE_MULTIPLY).build()
        assertEquals(
                ProgrammaticStyle.builder()
                        .put(
                                android.R.attr.backgroundTintMode,
                                ViewProxy.PORTERDUFF_MODE_MULTIPLY
                        )
                        .build(),
                style
        )
    }

    @Test
    fun visibility_visible() {
        val style = builder.visibility(View.VISIBLE).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.visibility, View.VISIBLE)
                .build(),
            style
        )
    }

    @Test
    fun visibility_visibleRes() {
        val style = builder.visibilityRes(R.integer.test_view_style_builder_visible).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(
                    android.R.attr.visibility,
                    ResourceId(R.integer.test_view_style_builder_visible)
                )
                .build(),
            style
        )
    }

    @Test
    fun visibility_invisible() {
        val style = builder.visibility(View.INVISIBLE).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.visibility, View.INVISIBLE)
                .build(),
            style
        )
    }

    @Test
    fun visibility_invisibleRes() {
        val style = builder.visibilityRes(R.integer.test_view_style_builder_invisible).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(
                    android.R.attr.visibility,
                    ResourceId(R.integer.test_view_style_builder_invisible)
                )
                .build(),
            style
        )
    }

    @Test
    fun visibility_gone() {
        val style = builder.visibility(View.GONE).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.visibility, View.GONE)
                .build(),
            style
        )
    }

    @Test
    fun visibility_goneRes() {
        val style = builder.visibilityRes(R.integer.test_view_style_builder_gone).build()
        assertEquals(
            ProgrammaticStyle.builder()
                .put(android.R.attr.visibility, ResourceId(R.integer.test_view_style_builder_gone))
                .build(),
            style
        )
    }
}
