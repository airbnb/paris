/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.airbnb.paris.processor.abstractions.ksp.synthetic

import com.airbnb.paris.processor.abstractions.XAnnotated
import com.airbnb.paris.processor.abstractions.XEquality
import com.airbnb.paris.processor.abstractions.XExecutableParameterElement
import com.airbnb.paris.processor.abstractions.XType
import com.airbnb.paris.processor.abstractions.ksp.KspAnnotated
import com.airbnb.paris.processor.abstractions.ksp.KspAnnotated.UseSiteFilter.Companion.NO_USE_SITE
import com.airbnb.paris.processor.abstractions.ksp.KspExecutableElement
import com.airbnb.paris.processor.abstractions.ksp.KspProcessingEnv
import com.airbnb.paris.processor.abstractions.ksp.KspType
import com.airbnb.paris.processor.abstractions.ksp.requireContinuationClass
import com.airbnb.paris.processor.abstractions.ksp.returnTypeAsMemberOf
import com.airbnb.paris.processor.abstractions.ksp.swapResolvedType
import com.google.devtools.ksp.symbol.Variance

/**
 * XProcessing adds an additional argument to each suspend function for the continiuation because
 * this is what KAPT generates and Room needs it as long as it generates java code.
 */
class KspSyntheticContinuationParameterElement(
    private val env: KspProcessingEnv,
    private val containing: KspExecutableElement
) : XExecutableParameterElement,
    XEquality,
    XAnnotated by KspAnnotated.create(
        env = env,
        delegate = null, // does not matter, this is synthetic and has no annotations.
        filter = NO_USE_SITE
    ) {

    override val name: String by lazy {
        // kotlin names this as pN where N is the # of arguments
        // seems like kapt doesn't handle conflicts with declared arguments but we should
        val desiredName = "p${containing.declaration.parameters.size}"

        if (containing.declaration.parameters.none { it.name?.asString() == desiredName }) {
            desiredName
        } else {
            "_syntheticContinuation"
        }
    }

    override val equalityItems: Array<out Any?> by lazy {
        arrayOf("continuation", containing)
    }

    override val type: XType by lazy {
        val continuation = env.resolver.requireContinuationClass()
        val contType = continuation.asType(
            listOf(
                env.resolver.getTypeArgument(
                    checkNotNull(containing.declaration.returnType) {
                        "cannot find return type for $this"
                    },
                    Variance.CONTRAVARIANT
                )
            )
        )
        env.wrap(
            ksType = contType,
            allowPrimitives = false
        )
    }

    override val fallbackLocationText: String
        get() = "return type of ${containing.fallbackLocationText}"

    override fun asMemberOf(other: XType): XType {
        check(other is KspType)
        val continuation = env.resolver.requireContinuationClass()
        val asMember = containing.declaration.returnTypeAsMemberOf(
            resolver = env.resolver,
            ksType = other.ksType
        )
        val returnTypeRef = checkNotNull(containing.declaration.returnType) {
            "cannot find return type reference for $this"
        }
        val returnTypeAsTypeArgument = env.resolver.getTypeArgument(
            returnTypeRef.swapResolvedType(asMember),
            Variance.CONTRAVARIANT
        )
        val contType = continuation.asType(listOf(returnTypeAsTypeArgument))
        return env.wrap(
            ksType = contType,
            allowPrimitives = false
        )
    }

    override fun kindName(): String {
        return "synthetic continuation parameter"
    }

    override fun equals(other: Any?): Boolean {
        return XEquality.equals(this, other)
    }

    override fun hashCode(): Int {
        return XEquality.hashCode(equalityItems)
    }
}