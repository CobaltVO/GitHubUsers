package ru.cobalt.githubusers.ui.utils

import android.graphics.Color
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import ru.cobalt.githubusers.R

fun View.snack(@StringRes message: Int) {
    return Snackbar
        .make(this, message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(
            MaterialColors.getColor(
                context,
                R.attr.colorPrimaryVariant,
                Color.BLACK
            )
        )
        .setTextColor(
            MaterialColors.getColor(
                context,
                R.attr.textColorPrimary,
                Color.BLACK
            )
        )
        .show()
}

fun View.snack(
    @StringRes message: Int,
    @StringRes buttonName: Int,
    listener: View.OnClickListener
) {
    return Snackbar
        .make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setBackgroundTint(
            MaterialColors.getColor(
                context,
                R.attr.colorPrimaryVariant,
                Color.BLACK
            )
        )
        .setTextColor(
            MaterialColors.getColor(
                context,
                R.attr.textColorPrimary,
                Color.BLACK
            )
        )
        .setActionTextColor(
            MaterialColors.getColor(
                context,
                R.attr.textColorPrimary,
                Color.BLACK
            )
        )
        .setAction(buttonName, listener)
        .show()
}