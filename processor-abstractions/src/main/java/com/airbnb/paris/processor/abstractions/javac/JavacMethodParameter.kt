/*
 * Copyright (C) 2020 The Android Open Source Project
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

import com.airbnb.paris.processor.abstractions.javac.JavacProcessingEnv
import com.airbnb.paris.processor.abstractions.javac.JavacTypeElement
import com.airbnb.paris.processor.abstractions.javac.kotlin.KmType
import com.airbnb.paris.processor.abstractions.javac.kotlin.KmValueParameter
import javax.lang.model.element.VariableElement

class JavacMethodParameter(
    env: JavacProcessingEnv,
    private val executable: JavacExecutableElement,
    containing: JavacTypeElement,
    element: VariableElement,
    val kotlinMetadata: KmValueParameter?
) : JavacVariableElement(env, containing, element) {
    override val name: String
        get() = kotlinMetadata?.name ?: super.name
    override val kotlinType: KmType?
        get() = kotlinMetadata?.type
    override val fallbackLocationText: String
        get() = if (executable is JavacMethodElement && executable.isSuspendFunction() &&
            this === executable.parameters.last()
        ) {
            "return type of ${executable.fallbackLocationText}"
        } else {
            "$name in ${executable.fallbackLocationText}"
        }
}
