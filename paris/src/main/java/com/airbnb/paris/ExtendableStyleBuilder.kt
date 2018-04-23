package com.airbnb.paris

import android.view.View

/**
 * Meant to be extended with attribute and linked style functions
 */
class ExtendableStyleBuilder<V : View> : StyleBuilder<ExtendableStyleBuilder<V>, StyleApplier<*, V>>() {

    // Makes the builder public so that extensions can access it
    public override var builder = super.builder

    // Makes the function public so that extensions can access it
    @Suppress("RedundantOverride")
    public override fun consumeProgrammaticStyleBuilder() {
        super.consumeProgrammaticStyleBuilder()
    }
}
