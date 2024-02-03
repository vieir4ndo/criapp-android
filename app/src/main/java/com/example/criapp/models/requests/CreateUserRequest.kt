package com.example.criapp.models.requests

import com.google.gson.Gson
class CreateUserRequest(
    val name: String,
    val username : String,
    val password : String,
    val birthDate : String,
    val numberOfChildren : Int,
    val sex : Int,
    val familyType : Int?,
    val educationalLevel : Int?
)
{
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this)
    }
}