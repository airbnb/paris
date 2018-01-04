package com.airbnb.paris.proxies

import android.view.*

interface Proxy<out P, out V : View> {
    val proxy: P
    val view: V
}
