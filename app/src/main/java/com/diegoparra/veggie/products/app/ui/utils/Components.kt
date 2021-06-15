package com.diegoparra.veggie.products.app.ui.utils

import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.QtyButton
import com.diegoparra.veggie.core.android.getColorFromAttr
import com.diegoparra.veggie.core.android.getResourcesFromAttr
import com.diegoparra.veggie.products.domain.Label
import com.google.android.material.chip.Chip
import com.google.android.material.color.MaterialColors


fun Chip.loadProductLabel(label: Label) {
    when (label) {
        is Label.Hidden -> {
            isVisible = false
        }
        is Label.NoStock -> {
            text = context.getString(R.string.label_no_stock)
            chipBackgroundColor =
                ColorStateList.valueOf(context.getColorFromAttr(R.attr.colorOnSurface))
            isVisible = true
        }
        is Label.Discounted -> {
            text = context.getString(
                R.string.label_discounted,
                (label.discount * 100).toInt().toString() + "%"
            )
            chipBackgroundColor =
                ColorStateList.valueOf(context.getColorFromAttr(R.attr.colorSecondary))
            isVisible = true
        }
        is Label.DisplayLabel -> {
            text = label.suggestedLabel
            val randomAttrColors =
                context.resources.getIntArray(context.getResourcesFromAttr(R.attr.colorsRandom))
            chipBackgroundColor = ColorStateList.valueOf(when (label.suggestedLabel.lowercase()) {
                "recomendado" -> randomAttrColors[0]
                "popular" -> randomAttrColors.getOrElse(1) { randomAttrColors[0] }
                else -> randomAttrColors.getOrElse(2) { randomAttrColors[0] }
            })
            isVisible = true
        }
    }
}

fun ViewGroup.loadEnabledState(stock: Boolean) {
    val currentEnabledState = isEnabled
    if (currentEnabledState != stock) {
        isEnabled = stock
        children.forEach {
            if (it.hasOnClickListeners()) {
                it.isEnabled = stock
            }
            it.alpha = if (stock) MaterialColors.ALPHA_FULL else MaterialColors.ALPHA_DISABLED
        }
    }
}

fun Collection<QtyButton>.setQuantityState(quantity: Int, maxOrder: Int) {
    if (this.isEmpty()) return
    this.forEach {
        it.setQuantityState(quantity = quantity, maxOrder = maxOrder)
    }
}