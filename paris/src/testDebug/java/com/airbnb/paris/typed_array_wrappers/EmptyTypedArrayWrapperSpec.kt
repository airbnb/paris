package com.airbnb.paris

import com.airbnb.paris.typed_array_wrappers.EmptyTypedArrayWrapper
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

class EmptyTypedArrayWrapperSpec : StringSpec({

    val wrapper = EmptyTypedArrayWrapper

    "invalid methods" {
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
})
