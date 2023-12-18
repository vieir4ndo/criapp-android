package com.example.criapp.model

import com.google.gson.Gson

class CreateOrUpdateChildModel {
    var name = "";
    var birthDate = "";
    var sex = 0;
    var monthsOfBreastFeeding = 0;
    var isPremature : Boolean? = null;

    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}