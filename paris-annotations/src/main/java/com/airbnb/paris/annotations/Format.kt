package com.airbnb.paris.annotations

// TODO  Better support for enums
enum class Format {

    /**
     * This is meant to be used as a default value by [Attr]. When set to [DEFAULT] the context is
     * used to determine the actual format
     */
    DEFAULT,

    BOOLEAN,
    CHARSEQUENCE,
    CHARSEQUENCE_ARRAY,
    COLOR,
    COLOR_STATE_LIST,
    DIMENSION,
    DIMENSION_PIXEL_OFFSET,
    DIMENSION_PIXEL_SIZE,
    DRAWABLE,
    FLOAT,
    FRACTION,
    INT,
    INTEGER,
    NON_RESOURCE_STRING,
    RESOURCE_ID,
    STRING;

    // For extensions
    companion object {}
}
