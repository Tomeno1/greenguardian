package model

data class Plant(
    val name: String,
    val description: String,
    val imageUrl: Int,
    val recommendations: String,
    val phMin: Double,
    val phMax: Double,
    val tempMin: Int,
    val tempMax: Int
)
