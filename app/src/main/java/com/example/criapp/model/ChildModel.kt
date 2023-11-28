package com.example.criapp.model

import com.google.gson.Gson

class ChildModel(
    val id : String,
    val name: String,
    val birthDate: String,
    val sex: Int,
    val monthsOfBreastFeeding : Int,
    val prcId : String,
    val isPremature : Boolean
) {
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}

