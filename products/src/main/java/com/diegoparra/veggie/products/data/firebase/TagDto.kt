package com.diegoparra.veggie.products.data.firebase

import com.google.gson.annotations.SerializedName

data class TagDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)