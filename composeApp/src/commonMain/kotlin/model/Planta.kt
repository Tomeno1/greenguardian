package model

// --- Plant ---
// Modelo que representa una planta y sus características específicas para cultivo hidropónico.
data class Plant(
    val name: String,             // Nombre de la planta
    val description: String,      // Descripción breve de la planta
    val imageUrl: Int,            // Recurso de imagen asociado a la planta
    val recommendations: String,  // Recomendaciones para el cultivo de la planta
    val phMin: Double,            // Valor mínimo de pH adecuado para el cultivo
    val phMax: Double,            // Valor máximo de pH adecuado para el cultivo
    val tempMin: Int,             // Temperatura mínima adecuada para el cultivo
    val tempMax: Int              // Temperatura máxima adecuada para el cultivo
)
