package com.example.criapp.models.requests

import com.google.gson.GsonBuilder

class UpdateUserRequest (
    val name : String,
    val birthDate : String,
    val sex : Int,
    val familyType : Int?,
    val educationalLevel : Int?,
    val numberOfChildren : Int
){
    override fun toString(): String {
        val gson = GsonBuilder().serializeNulls().create()

        return gson.toJson(this);
    }
}