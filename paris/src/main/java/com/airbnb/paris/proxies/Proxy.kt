package com.airbnb.paris.proxies

import android.view.View

/**
 * @param P The [Proxy] implementation itself.
 * @param V The [View] type that is being proxied.
 */
interface Proxy<out P, out V : View> {
    val proxy: P
    val view: V
}
