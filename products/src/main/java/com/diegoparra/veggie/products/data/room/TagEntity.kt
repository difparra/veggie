package com.diegoparra.veggie.products.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tags")
data class TagEntity (
        @PrimaryKey val tagId: String,
        val tagName: String
)