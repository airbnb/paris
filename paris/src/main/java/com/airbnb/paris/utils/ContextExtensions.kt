package com.airbnb.paris.utils

import android.content.Context
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat

fun Context.getFont(@FontRes id: Int) = ResourcesCompat.getFont(this, id)
