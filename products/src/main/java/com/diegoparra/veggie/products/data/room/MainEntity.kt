package com.diegoparra.veggie.products.data.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.diegoparra.veggie.core.customNormalisation

@Entity(tableName = "Main",
        indices = [
            Index(value = ["relatedTagId"]),
            Index(value = ["mainVarId"], unique = true)
        ]
)
data class MainEntity(
        @PrimaryKey val mainId: String,
        val relatedTagId: String,
        val mainVarId: String,
        val name: String,
        val normalised_name: String = name.customNormalisation(),                //      Just to make easier search ignoring case and accents
        val imageUrl: String,
        val updatedAtInMillis: Long
)