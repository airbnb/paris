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

package com.airbnb.paris.processor.abstractions.javac

import com.airbnb.paris.processor.abstractions.XFieldElement
import com.airbnb.paris.processor.abstractions.XHasModifiers
import com.airbnb.paris.processor.abstractions.XTypeElement
import com.airbnb.paris.processor.abstractions.javac.JavacProcessingEnv
import com.airbnb.paris.processor.abstractions.javac.JavacTypeElement
import com.airbnb.paris.processor.abstractions.javac.kotlin.KmProperty
import com.airbnb.paris.processor.abstractions.javac.kotlin.KmType
import javax.lang.model.element.VariableElement

class JavacFieldElement(
    env: JavacProcessingEnv,
    containing: JavacTypeElement,
    element: VariableElement
) : JavacVariableElement(env, containing, element),
    XFieldElement,
    XHasModifiers by JavacHasModifiers(element) {

    private val kotlinMetadata: KmProperty? by lazy {
        (enclosingTypeElement as? JavacTypeElement)?.kotlinMetadata?.getPropertyMetadata(name)
    }

    override val kotlinType: KmType?
        get() = kotlinMetadata?.type

    override val enclosingTypeElement: XTypeElement by lazy {
        element.requireEnclosingType(env)
    }
}
