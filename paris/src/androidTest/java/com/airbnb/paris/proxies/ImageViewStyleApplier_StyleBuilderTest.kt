package com.airbnb.paris.proxies

import androidx.test.runner.*
import android.widget.*
import android.widget.ImageViewStyleApplier.*
import com.airbnb.paris.styles.*
import org.junit.*
import org.junit.runner.*

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
