package com.airbnb.paris.proxies

import android.widget.ImageView
import android.widget.ImageViewStyleApplier.StyleBuilder
import androidx.test.runner.AndroidJUnit4
import com.airbnb.paris.styles.ProgrammaticStyle
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageViewStyleApplier_StyleBuilderTest {

    private lateinit var programmaticStyleBuilder: ProgrammaticStyle.Builder
    private lateinit var styleBuilder: StyleBuilder

    @Before
    fun setup() {
        programmaticStyleBuilder = ProgrammaticStyle.builder().debugName("test")
        styleBuilder = StyleBuilder().debugName("test")
    }

    @Test
    fun auto() {
        for (mapping in IMAGE_VIEW_MAPPINGS) {
            mapping as BaseViewMapping<Any, ImageViewProxy, ImageView, Any>

            // For normal values
            mapping.testValues.forEach {
                setup()

                programmaticStyleBuilder.put(mapping.attrRes, it)
                mapping.setStyleBuilderValueFunction(styleBuilder, it)
                Assert.assertEquals(programmaticStyleBuilder.build(), styleBuilder.build())
            }

            // For resource ids
            setup()

            programmaticStyleBuilder.putRes(mapping.attrRes, ARBITRARY_RESOURCE_ID)
            mapping.setStyleBuilderResFunction(styleBuilder, ARBITRARY_RESOURCE_ID)
            Assert.assertEquals(programmaticStyleBuilder.build(), styleBuilder.build())
        }
    }
}
