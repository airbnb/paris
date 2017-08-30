package com.airbnb.paris.utils;

import com.airbnb.paris.StyleBuilder;

public interface StyleBuilderFunction<T extends StyleBuilder> {

    void invoke(T builder);
}
