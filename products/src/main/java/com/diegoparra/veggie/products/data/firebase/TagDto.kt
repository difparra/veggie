package com.diegoparra.veggie.products.data.firebase

import com.google.gson.annotations.SerializedName

@JvmInline
value class TagDtoList(@SerializedName("tags_array") val tagsArray: List<TagDto>)

data class TagDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)