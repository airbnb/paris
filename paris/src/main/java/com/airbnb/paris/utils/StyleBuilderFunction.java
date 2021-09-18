package com.airbnb.paris.utils;

import com.airbnb.paris.StyleBuilder;

import androidx.annotation.NonNull;

public interface StyleBuilderFunction<T extends StyleBuilder> {

    void invoke(@NonNull T builder);
}
