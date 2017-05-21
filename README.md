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

## Style Links

XML resource files can get big and chaotic. For styles it can become hard to find which are available for any given view type. To remedy this, Paris lets your custom views explicitly declare their supported styles.
```java
@Styleable(styles = {
        @Style(name = "Red", id = R.style.MyView_Red),
        @Style(name = "Green", id = R.style.MyView_Green),
        @Style(name = "Blue", id = R.style.MyView_Blue)
})
public class MyView extends View {
    ...
}
```

Now when styling a view of type `MyView` you'll have access to helper methods for each of those styles.
```java
Paris.style(myView).applyRed();
Paris.style(myView).applyGreen();
Paris.style(myView).applyBlue(); // Same as calling ...apply(R.style.MyView_Blue)
```

Note: This doesn't prohibit the application of other styles.

## How do I...

* ... apply **multiple styles** to a view?
* ... add support for my custom view's **custom attributes**?
* ... **share** custom attribute logic across multiple views?
