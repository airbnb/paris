# 1.8.0
- Support KSP
- ParisConfig annotation can no longer be used on package elements, only on class or interfaces

- Fix
# 1.7.3 (April 13, 2021)

- Fix generated code consistency in module class (#154)

# 1.7.2 (November 11, 2020)

- Fixed ArrayOutOfBoundsException on certain devices when ImageView scaleType is set.

# 1.7.1 (July 27, 2020)

- Change `ParisConfig` annotation to be applicable to class or interface types. New recommendation is to use it on an interface instead of a package
to avoid a bug with spurious incremental annotation failure.

- Fixed annotation processor bugs with Kotlin 1.4. Project should now be compatible with Kotlin 1.4.x - previous Paris versions would crash at compile time.

# 1.6.0 (July 21, 2020)

- Adding support for importantForAccessibility attribute (#138)

# 1.5.0 (May 20, 2020)

- Adds support for incremental annotation processing

# 1.4.0 (February 14, 2020)

- Added support for namespaced resources.

# 1.3.1 (December 16, 2019)

- Fixed precedence of `android:textAppearance` attribute.
- Fixed theme attributes not being applied through programmatic styles.

# 1.3.0 (July 16, 2019)

- New attribute support:
  - View: `android:layout_weight`
  - TextView
    - `android:line_height`
    - `android:text_appearance`
- Now using Kotlin nullable types in generated extension functions, rather than `@Nullable`.

# 1.2.1 (January 2, 2019)

- Updated KotlinPoet dependency to 1.0.0.
- Added `@RequiresApi` to style builder functions when appropriate.

# 1.2.0 (November 6, 2018)

- Set Android version to 28.
- Migrated to AndroidX.
- Updated dependencies (ButterKnife, ConstraintLayout, Gradle, Kotlin, Mockito, Testing Compile).
- Fixed Samsung-specific crash related to `android:elevation` on Android version 19.

# 1.1.0 (October 23, 2018)

- Updated the sample app.
- Annotations are now included in the "paris" artifact.
- New attribute support:
  - View
    - `android:backgroundTint`
    - `android:backgroundTintMode`
    - `android:layout_marginHorizontal`
    - `android:layout_marginVertical`
  - TextView
    - `android:drawablePadding`
    - `android:fontFamily`
    - `android:inputType`
    - `android:hint`
    - `android:maxWidth`
    - `android:textStyle`
- Added support for null attribute values.
- Kotlin support:
  - Extensions are now generated for styleable views as an alternative to calling `Paris…`.
  - Added support for `@Styleable` Kotlin classes.
- Clarified various error messages.
- Bugs:
  - Fixed password inputType + singleLine bug.
  - Fixed precedence of margin and padding attribute.
  - Fixed compound drawables not using intrinsic bounds.

# 1.0.0 (April 13, 2018)

- Apply styles programmatically at any time.
- Combine multiple styles together.
- Create styles programmatically (as opposed to using XML).
- Use annotations to easily support custom attributes.
- Declare explicitly supported styles for your custom views.
- And much more...
