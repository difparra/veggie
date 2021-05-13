package com.diegoparra.veggie.products.data.products.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "Variations",
        primaryKeys = ["varId", "relatedMainId"],
        foreignKeys = [
            ForeignKey(entity = MainEntity::class,
                    parentColumns = ["mainId"],
                    childColumns = ["relatedMainId"],
                    onDelete = ForeignKey.CASCADE)
        ],
        indices = [
            Index(value = ["varId"]),
            Index(value = ["relatedMainId"])
        ]
)
data class VariationEntity (
        val varId: String,
        val relatedMainId: String,
        val unit: String,
        val weightGr: Int,
        val price: Int,
        val discount: Float,
        val stock: Boolean,
        val maxOrder: Int,
        val label: String?,
        val details: List<String>?
)