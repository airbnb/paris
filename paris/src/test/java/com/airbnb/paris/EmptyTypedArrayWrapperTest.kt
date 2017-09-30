package com.airbnb.paris

import com.airbnb.paris.typed_array_wrappers.*
import io.kotlintest.matchers.*
import io.kotlintest.properties.*
import io.kotlintest.specs.*

class EmptyTypedArrayWrapperTest : StringSpec() {

    init {
        val wrapper = EmptyTypedArrayWrapper

        "invalid methods" {
            forAll { index: Int ->
                shouldThrow<IllegalStateException> {
                    wrapper.isNull(index)
                }
                true
            }

            forAll { at: Int ->
                shouldThrow<IllegalStateException> {
                    wrapper.getIndex(at)
                }
                true
            }

            forAll { index: Int ->
                shouldThrow<IllegalStateException> {
                    wrapper.getBoolean(index)
                }
                true
            }

            // TODO etc
        }

        "valid methods" {
            wrapper.getIndexCount() shouldBe 0

            forAll { index: Int -> !wrapper.hasValue(index) }

            // Checks that this doesn't throw
            wrapper.recycle()
        }
    }
}
