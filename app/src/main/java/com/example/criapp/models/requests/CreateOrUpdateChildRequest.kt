package com.example.criapp.models.requests

import com.google.gson.Gson

class CreateOrUpdateChildRequest(
    val name : String,
    val birthDate : String,
    val sex : Int,
    val monthsOfBreastFeeding :Int,
    val isPremature : Boolean?
){
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}