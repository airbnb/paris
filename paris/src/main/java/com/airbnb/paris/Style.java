package com.airbnb.paris;

import android.support.annotation.Nullable;
import android.view.View;

import com.google.auto.value.AutoValue;

import java.util.Set;

public interface Style<T extends View> {

    /**
     * Config objects are automatically passed from {@link BaseStyle} to {@link BaseStyle}. They
     * are simply a collection of objects with some helpers to sort through them. It is up to each
     * style to declare, retrieve, and act upon the configuration option that they are interested
     * in.
     *
     * Option objects can be as simple as enum values, or as complex as fully fledged objects, such
     * as listeners.
     */
    @AutoValue
    abstract class Config {

        public interface Option {
            int hashCode();
            boolean equals(Object object);
        }

//        @AutoValue.Builder
//        abstract static class Builder {
//            abstract ImmutableSet.Builder<Option> optionsBuilder();
//            public Builder addOption(Option value) {
//                optionsBuilder().add(value);
//                return this;
//            }
//
//            public abstract Config build();
//        }
//
//        static Builder builder() {
//            return new AutoValue_Style_Config.Builder();
//        }
//
//        abstract Builder toBuilder();

        abstract Set<Option> options();

        public boolean contains(Option option) {
            return options().contains(option);
        }

        @Nullable
        public <T extends Option> T get(Class<? extends T> optionClass) {
            for (Option option : options()) {
                if (optionClass.isInstance(option)) {
                    return optionClass.cast(option);
                }
            }
            return null;
        }
    }

    void applyTo(T view);
}
