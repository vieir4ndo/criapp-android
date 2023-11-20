package com.example.criapp.model

import com.google.gson.Gson

class CreateUserModel {
    var name = "";
    var birthDate = "";
    var sex = 0;
    var email = "";
    var password = "";
    var familyType = 0;
    var educationalLevel = 0;
    var numberOfChildren = 0;

    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}