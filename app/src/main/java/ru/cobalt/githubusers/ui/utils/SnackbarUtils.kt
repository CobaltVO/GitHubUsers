package ru.cobalt.githubusers.ui.utils

import android.graphics.Color
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import ru.cobalt.githubusers.R

private fun Snackbar.applyStyle(): Snackbar = this
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

private fun Snackbar.applyButtonStyle(): Snackbar = this
    .setActionTextColor(
        MaterialColors.getColor(
            context,
            R.attr.textColorPrimary,
            Color.BLACK
        )
    )

fun View.snack(
    @StringRes message: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
) {
    return Snackbar
        .make(this, message, duration)
        .applyStyle()
        .show()
}

fun View.snack(
    @StringRes message: Int,
    @StringRes buttonName: Int,
    duration: Int = Snackbar.LENGTH_INDEFINITE,
    listener: View.OnClickListener,
) {
    return Snackbar
        .make(this, message, duration)
        .applyStyle()
        .applyButtonStyle()
        .setAction(buttonName, listener)
        .show()
}

fun View.snack(
    message: String,
    @StringRes buttonName: Int,
    duration: Int = Snackbar.LENGTH_INDEFINITE,
    listener: View.OnClickListener,
) {
    return Snackbar
        .make(this, message, duration)
        .applyStyle()
        .applyButtonStyle()
        .setAction(buttonName, listener)
        .show()
}

fun View.snack(
    message: String,
    buttonName: String,
    duration: Int = Snackbar.LENGTH_INDEFINITE,
    listener: View.OnClickListener,
) {
    return Snackbar
        .make(this, message, duration)
        .applyStyle()
        .applyButtonStyle()
        .setAction(buttonName, listener)
        .show()
}