package com.example.criapp.models

import com.google.gson.Gson

class UpdateUserModel {
    var name = "";
    var birthDate = "";
    var sex = 0;
    var familyType = 0;
    var educationalLevel = 0;
    var numberOfChildren = 0;

    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}