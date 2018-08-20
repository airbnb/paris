package com.airbnb.paris.utils

import android.content.Context
import android.support.annotation.FontRes
import android.support.v4.content.res.ResourcesCompat

fun Context.getFont(@FontRes id: Int) = ResourcesCompat.getFont(this, id)
