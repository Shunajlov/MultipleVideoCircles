package com.ihavenodomain.multiplevideocircles.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.LifecycleOwner


/**
 * Receive current lifecycleOwner
 */
fun Context.lifecycleOwner(): LifecycleOwner? {
    var curContext = this
    var maxDepth = 20
    while (maxDepth-- > 0 && this !is LifecycleOwner) {
        curContext = (this as ContextWrapper).baseContext
    }
    return if (curContext is LifecycleOwner) {
        this as LifecycleOwner
    } else {
        null
    }
}