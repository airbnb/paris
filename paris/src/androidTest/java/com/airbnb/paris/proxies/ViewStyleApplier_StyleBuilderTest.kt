package com.airbnb.paris.proxies

import android.view.View
import android.view.ViewStyleApplier.StyleBuilder
import android.widget.TextView
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.airbnb.paris.styles.ProgrammaticStyle
import com.airbnb.paris.test.R
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewStyleApplier_StyleBuilderTest {

    companion object {
        private const val ARBITRARY_RESOURCE_ID = 2
    }

    private val context = InstrumentationRegistry.getTargetContext()!!
    private val res = context.resources!!
    private lateinit var view: View
    private lateinit var programmaticStyleBuilder: ProgrammaticStyle.Builder
    private lateinit var styleBuilder: StyleBuilder

    @Before
    fun setup() {
        view = View(context)
        programmaticStyleBuilder = ProgrammaticStyle.builder().debugName("test")
        styleBuilder = StyleBuilder().debugName("test")
    }

    @Test
    fun auto() {
        for (mapping in VIEW_MAPPINGS) {
            mapping as BaseViewMapping<Any, *, TextView, Any>

            // For normal values
            mapping.testValues.forEach {
                setup()

                programmaticStyleBuilder.put(mapping.attrRes, it)
                mapping.setStyleBuilderValueFunction(styleBuilder, it)
                assertEquals(programmaticStyleBuilder.build(), styleBuilder.build())
            }

            // For resource ids
            setup()

            programmaticStyleBuilder.putRes(mapping.attrRes, ARBITRARY_RESOURCE_ID)
            mapping.setStyleBuilderResFunction(styleBuilder, ARBITRARY_RESOURCE_ID)
            assertEquals(programmaticStyleBuilder.build(), styleBuilder.build())
        }
    }

    @Test
    fun background() {
        assertNull(view.background)
        StyleBuilder()
            .backgroundRes(R.drawable.format_drawable)
            .applyTo(view)
        Assert.assertEquals(
            res.getDrawable(R.drawable.format_drawable).constantState,
            view.background.constantState
        )
    }
}
