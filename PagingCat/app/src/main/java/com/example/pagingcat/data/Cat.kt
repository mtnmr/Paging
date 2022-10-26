package com.example.pagingcat.data

import kotlinx.serialization.Serializable


data class Cat(
    val id: String,
    val url: String,
    val breeds: List<Breed>
) {
    fun firstBreed() = breeds.firstOrNull()
}

data class Breed(
    val name: String,
    val description: String
)

