# Paris
Programmatic style application for Android views, including custom attributes.

* Apply styles programmatically in addition to XML
* Use annotations to easily support custom attributes (inspired by [Barber](https://github.com/hzsweers/barber))
* Declare explicitly supported styles for your custom views

## Basic Usage

Styles are defined in XML as your normally would:
```xml
<style name="Title1">
    <item name="android:textColor">#000000</item>
    <item name="android:textSize">30sp</item>
    <item name="android:paddingTop">20dp</item>
</style>
```

And can now be applied programmatically at any time, like so:
```java
Paris.style(textView).apply(R.style.Title1);
```

You can find the list of currently supported attributes [here](paris/src/main/res/values/attrs.xml).

## How do I...

* ... apply **multiple styles** to a view?
* ... add support for my custom view's **custom attributes**?
* ... **share** custom attribute logic across multiple views?
