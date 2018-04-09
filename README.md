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

## Documentation

See examples and browse complete documentation at the [Paris Wiki](../../wiki).

If you still have questions, feel free to create a new issue.

## Contributing

Pull requests are welcome! We'd love help improving this library. Feel free to browse through open issues to look for things that need work. If you have a feature request or bug, please open a new issue so we can track it.

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
