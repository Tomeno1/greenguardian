package model

import kotlinx.serialization.Serializable

@Serializable
enum class Status {
    GOOD, PRECAUTION, WARNING
}