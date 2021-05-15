package com.diegoparra.veggie.products.firebase

import com.google.gson.annotations.SerializedName

data class TagDtoList(
        @SerializedName("tags_array") val tagsArray: List<TagDto>
)

data class TagDto (
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String
)