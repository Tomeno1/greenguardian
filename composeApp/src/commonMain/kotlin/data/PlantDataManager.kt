package data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import components.getImageResourceByName
import model.Plant


// --- PlantDataManager ---
// Objeto que gestiona una lista de plantas para el sistema hidropónico,
// proporcionando acceso y funciones para manipular los datos de las plantas.
object PlantDataManager {

    // Lista de plantas gestionada internamente, con estado mutable para cambios dinámicos en la UI
    private val _plants = mutableStateListOf(
        Plant(
            name = "Lechuga",
            description = "La lechuga es una de las plantas más populares para el cultivo hidropónico debido a su rápido crecimiento y facilidad de cultivo.",
            imageUrl = getImageResourceByName("lechuga"),
            recommendations = "La lechuga crece bien en sistemas NFT y de raíz flotante. Asegúrate de que las raíces estén bien aireadas y de mantener una buena circulación de nutrientes.",
            phMin = 5.5,
            phMax = 6.0,
            tempMin = 15,
            tempMax = 20
        ),
        Plant(
            name = "Albahaca",
            description = "La albahaca es una planta aromática ideal para sistemas hidropónicos. Se utiliza comúnmente en cocina por su aroma y sabor intensos.",
            imageUrl = getImageResourceByName("albahaca"),
            recommendations = "Prefiere sistemas NFT o DWC. Mantén una alta exposición a la luz y un entorno cálido para optimizar el crecimiento.",
            phMin = 5.5,
            phMax = 6.5,
            tempMin = 18,
            tempMax = 26
        ),
        Plant(
            name = "Cilantro",
            description = "El cilantro es una hierba popular en la hidroponía, especialmente para quienes buscan un sabor fresco y un crecimiento rápido.",
            imageUrl = getImageResourceByName("cilantro"),
            recommendations = "Crece bien en sistemas de raíz flotante o NFT. Asegura buena circulación de aire para evitar enfermedades en las raíces.",
            phMin = 6.5,
            phMax = 6.7,
            tempMin = 17,
            tempMax = 23
        ),
        Plant(
            name = "Cebolla",
            description = "Las cebollas pueden cultivarse hidropónicamente en sistemas de raíz flotante. Son valoradas por su uso en una variedad de recetas culinarias.",
            imageUrl = getImageResourceByName("cebolla"),
            recommendations = "Requiere luz intensa y un sistema de soporte para las raíces. Prefiere temperaturas ligeramente más bajas.",
            phMin = 6.0,
            phMax = 6.5,
            tempMin = 13,
            tempMax = 24
        ),
        Plant(
            name = "Espinaca",
            description = "La espinaca es una excelente opción para la hidroponía, ya que crece rápidamente y produce un alto rendimiento de hojas verdes.",
            imageUrl = getImageResourceByName("espinaca"),
            recommendations = "Prefiere sistemas de raíz flotante. Asegúrate de mantener un rango de temperatura fresco para evitar que la espinaca se espigue.",
            phMin = 6.0,
            phMax = 7.0,
            tempMin = 15,
            tempMax = 21
        ),
        Plant(
            name = "Tomate",
            description = "El tomate es una de las plantas más populares para el cultivo hidropónico. Requiere un entorno controlado y una buena iluminación para crecer.",
            imageUrl = getImageResourceByName("tomate"),
            recommendations = "Crece mejor en sistemas NFT o DWC con buen soporte para las raíces. Requiere luz intensa y un entorno cálido.",
            phMin = 5.5,
            phMax = 6.5,
            tempMin = 18,
            tempMax = 25
        ),
        Plant(
            name = "Kale",
            description = "El kale es un vegetal de hoja verde oscuro, muy popular por su alto valor nutricional.",
            imageUrl = getImageResourceByName("kale"),
            recommendations = "Cultivar en sistemas NFT o de raíz flotante, asegurando buena circulación de nutrientes y aireación para evitar enfermedades.",
            phMin = 6.0,
            phMax = 7.0,
            tempMin = 10,
            tempMax = 22
        ),
        Plant(
            name = "Acelga",
            description = "La acelga es un vegetal de hoja grande, muy utilizado en sistemas hidropónicos por su versatilidad y resistencia.",
            imageUrl = getImageResourceByName("acelga"),
            recommendations = "Funciona bien en sistemas de raíz flotante y NFT. Mantener en un entorno fresco para maximizar el crecimiento.",
            phMin = 6.0,
            phMax = 7.0,
            tempMin = 15,
            tempMax = 24
        ),
        Plant(
            name = "Rúcula",
            description = "La rúcula es una planta de sabor picante, ideal para ensaladas. Crece rápido en hidroponía.",
            imageUrl = getImageResourceByName("rucula"),
            recommendations = "Crece mejor en sistemas de raíz flotante o NFT. Asegúrate de mantener el nivel de nutrientes y pH estables.",
            phMin = 6.0,
            phMax = 7.0,
            tempMin = 10,
            tempMax = 22
        ),
        Plant(
            name = "Perejil",
            description = "El perejil es una planta aromática utilizada ampliamente en la cocina y es adecuada para hidroponía.",
            imageUrl = getImageResourceByName("perejil"),
            recommendations = "Funciona bien en sistemas NFT. Requiere buena exposición a la luz para mantener un crecimiento saludable.",
            phMin = 5.5,
            phMax = 6.0,
            tempMin = 15,
            tempMax = 25
        ),
        Plant(
            name = "Menta",
            description = "La menta es una planta aromática de rápido crecimiento, ideal para infusiones y como hierba aromática.",
            imageUrl = getImageResourceByName("menta"),
            recommendations = "Crece bien en sistemas NFT o de raíz flotante. Prefiere condiciones de humedad moderada y buena circulación de aire.",
            phMin = 5.5,
            phMax = 6.0,
            tempMin = 18,
            tempMax = 24
        ),
        Plant(
            name = "Apio",
            description = "El apio es una planta de tallo crujiente y sabor suave, popular en sistemas hidropónicos.",
            imageUrl = getImageResourceByName("apio"),
            recommendations = "Funciona bien en sistemas NFT o de raíz flotante. Requiere un entorno fresco y estable para evitar estrés en las plantas.",
            phMin = 5.8,
            phMax = 6.2,
            tempMin = 16,
            tempMax = 21
        )


        // Agrega más plantas aquí si es necesario
    )

    // Acceso público a la lista de plantas como una SnapshotStateList, que permite observar los cambios de estado en la UI
    val plants: SnapshotStateList<Plant> get() = _plants

    // Función para obtener una planta específica por nombre
    fun getPlantByName(name: String): Plant? {
        return _plants.find { it.name == name }
    }

    // Función para agregar una nueva planta (opcional)
    fun addPlant(plant: Plant) {
        _plants.add(plant)
    }

    // Función para eliminar una planta (opcional)
    fun removePlant(plant: Plant) {
        _plants.remove(plant)
    }
}
