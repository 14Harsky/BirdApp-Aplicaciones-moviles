package com.example.birdapp.model

import com.google.gson.annotations.SerializedName

data class Ave(
    val uid: String,
    val name: Name,
    val images: Images,
    @SerializedName("_links")
    val links: Links
) {
    data class Name(
        val spanish: String,
        val english: String,
        val latin: String?
    )

    data class Images(
        val main: String
    )

    data class Links(
        val self: String
    )
}
