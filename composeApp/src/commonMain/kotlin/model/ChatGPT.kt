package model

import kotlinx.serialization.Serializable


@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class OpenAIResponse(
    val choices: List<Choice> = emptyList()
)

@Serializable
data class Choice(
    val message: Message
)

@Serializable
data class OpenAIError(
    val message: String,
    val type: String,
    val param: String?,
    val code: String?
)

@Serializable
data class OpenAIErrorResponse(
    val error: OpenAIError
)

data class MessageinBubble(
    val content: String,
    val isUser: Boolean
)

