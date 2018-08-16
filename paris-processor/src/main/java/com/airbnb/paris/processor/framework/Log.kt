package com.airbnb.paris.processor.framework

import javax.tools.Diagnostic

class Message(val kind: Diagnostic.Kind, val message: CharSequence)


