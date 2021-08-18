/*
 * Copyright 2021 The Android Open Source Project
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

package com.airbnb.paris.processor.abstractions.ksp

import com.airbnb.paris.processor.abstractions.XElement
import com.airbnb.paris.processor.abstractions.XRoundEnv
import com.airbnb.paris.processor.abstractions.XTypeElement
import com.google.devtools.ksp.symbol.KSClassDeclaration
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

class KspRoundEnv(
    private val env: KspProcessingEnv
) : XRoundEnv {
    override val rootElements: Set<XElement>
        get() = TODO("not supported")

    override fun getTypeElementsAnnotatedWith(klass: Class<out Annotation>): Set<XTypeElement> {
        return env.resolver.getSymbolsWithAnnotation(
            klass.canonicalName
        ).filterIsInstance<KSClassDeclaration>()
            .map {
                env.wrapClassDeclaration(it)
            }.toSet()
    }

    override fun getElementsAnnotatedWith(klass: Class<out Annotation>): Set<XElement> {
        return env.resolver.getSymbolsWithAnnotation(klass.canonicalName)
            .map { element ->
                when (element) {
                    else -> error("Unsupported $element")
                }
            }.toSet()
    }
}