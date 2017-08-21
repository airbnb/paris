package com.airbnb.paris;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static android.support.annotation.VisibleForTesting.PRIVATE;

/**
 * Meant to be extended by the generated Paris class
 */
class ParisBase {

    // TODO Remove?
    static <T extends StyleApplier> T process(final T applier) {
        applier.setOnStyleApply(new Function1<Style, Unit>() {
            @Override
            public Unit invoke(Style style) {
                setLastStyleApplied(applier.getView(), style);
                return Unit.INSTANCE;
            }
        });
        return applier;
    }

    @Nullable
    public static Style getLastStyleApplied(View view) {
        return (Style) view.getTag(R.id.paris_tag_view_style);
    }

    @VisibleForTesting(otherwise = PRIVATE)
    static void setLastStyleApplied(View view, Style style) {
        view.setTag(R.id.paris_tag_view_style, style);
    }
}
