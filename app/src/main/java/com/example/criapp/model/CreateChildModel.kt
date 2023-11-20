package com.example.criapp.model

import com.google.gson.Gson

class CreateChildModel {
    var name = "";
    var birthDate = "";
    var sex = 0;
    var monthsOfBreastFeeding = 0;
    var prcId : String? = "";
    var isPremature : Boolean? = null;

    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}