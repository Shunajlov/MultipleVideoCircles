package com.ihavenodomain.multiplevideocircles.utils

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import kotlin.math.ceil

// region Display metrics

fun Int.toPx(context: Context): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(), context.resources.displayMetrics
).toInt()

fun Float.toPx(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this, context.resources.displayMetrics
)

fun Int.toDp(context: Context): Int {
    val metrics = context.resources.displayMetrics
    return ceil((this / metrics.density).toDouble()).toInt()
}

fun Float.toDp(context: Activity): Float {
    val metrics = context.resources.displayMetrics
    return ceil((this / metrics.density).toDouble()).toFloat()
}

// endregion