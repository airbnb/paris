package com.airbnb.paris.proxy

import android.view.View

interface Proxy<out P, out V : View> {
    val proxy: P
    val view: V
}
