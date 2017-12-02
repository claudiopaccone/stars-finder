package it.personal.claudiopaccone.starsfinder.api.models

import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject

data class Stargazer(val username: String, val avatarUrl: String) {

    companion object {
        val deserializer = JsonDeserializer { json, _, _ ->
            val stargazerObject = json as JsonObject
            Stargazer(
                    username = stargazerObject.get("login").asString,
                    avatarUrl = stargazerObject.get("avatar_url").asString)
        }
    }
}