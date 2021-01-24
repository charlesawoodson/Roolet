package com.charlesawoodson.roolet.extensions

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils.loadLayoutAnimation
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

fun ViewGroup.inflate(@LayoutRes resId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(resId, this, attachToRoot)
}

fun ImageButton.setDrawableTint(@ColorRes colorRes: Int) {
    drawable.mutate().colorFilter = PorterDuffColorFilter(
        ContextCompat.getColor(context, colorRes),
        PorterDuff.Mode.MULTIPLY
    )
}

fun Drawable.setDrawableTint(context: Context, @ColorRes colorRes: Int) {
    mutate().colorFilter = PorterDuffColorFilter(
        ContextCompat.getColor(context, colorRes),
        PorterDuff.Mode.MULTIPLY
    )
}

fun ImageView.setDrawableTint(@ColorRes colorRes: Int) {
    drawable?.mutate()?.colorFilter =
        PorterDuffColorFilter(
            ContextCompat.getColor(context, colorRes),
            PorterDuff.Mode.MULTIPLY
        )
}

/**
 * Creates a simple cascading animation used to animate the group's
 * children after the first layout.
 */
fun RecyclerView.withLayoutAnimation(@AnimRes res: Int) {
    layoutAnimation = loadLayoutAnimation(context, res)
}
