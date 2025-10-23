package com.example.birdapp.model

data class Ave(
    val uid: String,
    val name: Name,
    val images: Images,
    val _links: Links
) {
    data class Name(
        val spanish: String,
        val english: String
    )

    data class Images(
        val main: String
    )

    data class Links(
        val self: Href
    ) {
        data class Href(val href: String)
    }
}
