package com.app.fiskas.fiskas;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by igorqua on 13.12.2017.
 */
@Retention(CLASS) @Target(FIELD)
public @interface Bind {
    /** View ID to which the field will be bound. */
    int[] value();
}
