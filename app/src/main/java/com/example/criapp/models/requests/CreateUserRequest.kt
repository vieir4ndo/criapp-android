package com.example.criapp.models.requests

import com.google.gson.Gson
class CreateUserRequest(
    var name: String = "",
    var username : String = "",
    var password : String = "",
    var birthDate : String = "",
    var numberOfChildren : Int = 0,
    var sex : Int = 0,
    var familyType : Int = 0,
    var educationalLevel : Int = 0,
)
{
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this)
    }
}