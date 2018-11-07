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
