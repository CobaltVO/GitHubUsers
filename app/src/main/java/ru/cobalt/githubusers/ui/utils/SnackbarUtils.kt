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

fun View.snack(@StringRes message: Int) {
    return Snackbar
        .make(this, message, Snackbar.LENGTH_SHORT)
        .applyStyle()
        .show()
}

fun View.snack(
    @StringRes message: Int,
    @StringRes buttonName: Int,
    listener: View.OnClickListener
) {
    return Snackbar
        .make(this, message, Snackbar.LENGTH_INDEFINITE)
        .applyStyle()
        .applyButtonStyle()
        .setAction(buttonName, listener)
        .show()
}

fun View.snack(
    message: String,
    @StringRes buttonName: Int,
    listener: View.OnClickListener
) {
    return Snackbar
        .make(this, message, Snackbar.LENGTH_INDEFINITE)
        .applyStyle()
        .applyButtonStyle()
        .setAction(buttonName, listener)
        .show()
}

fun View.snack(
    message: String,
    buttonName: String,
    listener: View.OnClickListener
) {
    return Snackbar
        .make(this, message, Snackbar.LENGTH_INDEFINITE)
        .applyStyle()
        .applyButtonStyle()
        .setAction(buttonName, listener)
        .show()
}