# Paris
Paris lets you define and apply styles programmatically to Android views, including custom attributes.

* Apply styles programmatically at any time.
* Combine multiple styles together.
* Create styles programmatically (as opposed to using XML).
* Use annotations to easily support custom attributes (inspired by [Barber](https://github.com/hzsweers/barber)).
* Declare explicitly supported styles for your custom views.
* And much more...

## Installation

In your project's `build.gradle`:
```gradle
dependencies {
    implementation 'com.airbnb.android:paris:1.0.0'
    // If you are using Paris annotations
    implementation 'com.airbnb.android:paris-annotations:1.0.0'
    annotationProcessor 'com.airbnb.android:paris-processor:1.0.0'
}
```

To use Paris in a library module see [Library Modules](../../wiki/Library-Modules).

## Quick Start

### Applying an XML-Defined Style

```java
Paris.style(myView).apply(R.style.MyStyle);
```

Where `myView` is an arbitrary view instance and `MyStyle` an XML-defined style. Many but not all attributes are supported, for more see [Supported View Types and Attributes](../../wiki/Supported-View-Types-and-Attributes).

### Combining 2 or More Styles

```java
Paris.styleBuilder(myView)
        .add(R.style.StyleA)
        .add(R.style.StyleB)
        ...
        .apply();
```

In cases where there's some overlap the attribute value from the last style added prevails. For more see [Combining Styles](../../wiki/Building-and-Applying-Styles#combining-styles).

### Defining Styles Programmatically

```java
Paris.styleBuilder(textView)
        .textColor(Color.GREEN) // Using an actual value
        .textSizeRes(R.dimen.my_text_size_small) // Or a resource
        .apply();
```

Can be combined with style resources as well:
```java
Paris.styleBuilder(textView)
        .add(R.style.MyGreenTextView)
        .textSizeRes(R.dimen.my_text_size_small)
        .apply();
```

For more see [Defining Styles Programmatically](../../wiki/Building-and-Applying-Styles#defining-styles-programmatically).

### Custom View Attributes

Attributes are declared as followed:
```xml
<declare-styleable name="MyView">
    <attr name="title" format="string" />
    <attr name="image" format="reference" />
    <attr name="imageSize" format="dimension" />
</declare-styleable>
```

The custom view is annotated with `@Styleable` and `@Attr`:
```java
// The value here corresponds to the name chosen in declare-styleable
@Styleable("MyView")
public class MyView extends ViewGroup {

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Enables the custom attributes when used in XML layouts
        Paris.style(this).apply(attrs);
    }

    @Attr(R.styleable.MyView_title)
    public void setTitle(String title) {
        ...
    }

    @Attr(R.styleable.MyView_image)
    public void setImage(Drawable image) {
        ...
    }

    @Attr(R.styleable.MyView_imageSize)
    public void setImageSize(@Px int imageSize) {
        ...
    }
}
```

The `@Attr`-annotated methods will be called by Paris when the view is inflated with an `AttributeSet` or when a style is applied.

For more see [Custom View Attributes](../../wiki/Custom-View-Attributes).

### Styling Subviews

Attributes are declared as followed for the 2 subviews we'd like to be able to style:
```xml
<declare-styleable name="MyHeader">
    <attr name="titleStyle" format="reference" />
    <attr name="subtitleStyle" format="reference" />
    ...
</declare-styleable>
```

The subview fields are annotated with `@StyleableChild`:
```java
@Styleable("MyHeader")
public class MyHeader extends ViewGroup {

    @StyleableChild(R.styleable.MyHeader_titleStyle)
    TextView title;
    
    @StyleableChild(R.styleable.MyHeader_subtitleStyle)
    TextView subtitle;
    
    ...
    // Make sure to call Paris.style(this).apply(attrs) during initialization
}
```

The title and subtitle styles can now be part of `MyHeader` styles:
```xml
<MyHeader
    ...
    app:titleStyle="@style/Title2"
    app:subtitleStyle="@style/Regular" />
```

```java
Paris.styleBuilder(myHeader)
        .titleStyle(R.style.Title2) // Defined in XML
        .subtitleStyle((builder) -> builder // Defined programmatically
                .textColorRes(R.color.text_color_regular)
                .textSizeRes(R.dimen.text_size_regular))
        .apply();
```

For more see [Styling Subviews](../../wiki/Custom-Views#styling-subviews).

### Linking Styles to Views

```java
@Styleable
public class MyView extends View {

    // For styles defined in XML
    @Style
    static final int RED_STYLE = R.style.MyView_Red;

    // For styles defined programmatically
    @Style
    static void greenStyle(MyViewStyleApplier.StyleBuilder builder) {
        builder.background(R.color.green);
    }

    ...
}
```

Helper methods are generated for each linked style:
```java
Paris.style(myView).applyRed(); // Equivalent to .apply(R.style.MyView_Red)
Paris.style(myView).applyGreen(); // No equivalent

Paris.styleBuilder(myView)
        .addRed() // Equivalent to .add(R.style.MyView_Red)
        .addGreen() // No equivalent
        ...
        .apply();
```

For more see [Linking Styles to Custom Views](../../wiki/Custom-Views#linking-styles-to-custom-views).

## Documentation

See examples and browse complete documentation at the [Paris Wiki](../../wiki).

If you still have questions, feel free to create a new issue.

## Contributing

We love contributions! Check out our [contributing guidelines](CONTRIBUTING.md) and be sure to follow our [code of conduct](CODE_OF_CONDUCT.md).

## License

```
Copyright 2018 Airbnb, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
