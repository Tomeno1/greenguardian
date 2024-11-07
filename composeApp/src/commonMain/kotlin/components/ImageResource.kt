package components
import techminds.greenguardian.R




// --- getImageResourceByName ---
// Función que devuelve el recurso de imagen correspondiente al nombre de una planta
fun getImageResourceByName(imageName: String): Int {
    return when (imageName) {
        "lechuga" -> R.drawable.lechuga
        "cilantro" -> R.drawable.cilantro
        "cebolla" -> R.drawable.cebolla
        "tomate" -> R.drawable.tomate
        "espinaca" -> R.drawable.espinaca
        "albahaca" -> R.drawable.albahaca
        "kale" -> R.drawable.kale
        "acelga" -> R.drawable.acelga
        "rucula" -> R.drawable.rucula
        "perejil" -> R.drawable.perejil
        "menta" -> R.drawable.menta
        "apio" -> R.drawable.apio
        else -> R.drawable.lechuga // Imagen por defecto si no coincide con ningún caso
    }
}

// --- getImageResourceSensorByName ---
// Función que devuelve el recurso de imagen correspondiente al tipo de sensor
fun getImageRsourceSensorByName(imageName: String): Int {
    return when (imageName) {
        "temperatura" -> R.drawable.temperature
        "humedad" -> R.drawable.water_drop
        "flow" -> R.drawable.water_tap
        "ph" -> R.drawable.flask
        "tds" -> R.drawable.test_tube
        "ldr" -> R.drawable.sun
        else -> R.drawable.temperature
    }
}

// --- getImagenBottomNavigationBar ---
// Función que devuelve el recurso de imagen para los íconos de la barra de navegación inferior
fun getImagenBottomNavigationBar(imageName: String): Int {
    return when (imageName) {
        "home" -> R.drawable.home_sharp
        "ponds" -> R.drawable.leaf_sharp
        "asistente" -> R.drawable.icons8_chatbot_64
        "profile" -> R.drawable.person_sharp
        else -> R.drawable.person_sharp
    }
}
