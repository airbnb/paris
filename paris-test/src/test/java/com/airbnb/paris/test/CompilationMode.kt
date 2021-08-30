package com.airbnb.paris.test

enum class CompilationMode(val testKapt: Boolean, val testKSP: Boolean) {
    KSP(testKapt = false, testKSP = true),
    KAPT(testKapt = true, testKSP = false),
    ALL(testKapt = true, testKSP = true)
}