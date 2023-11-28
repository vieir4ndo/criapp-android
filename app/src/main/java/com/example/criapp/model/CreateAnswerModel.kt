package com.example.criapp.model

import com.google.gson.Gson

class CreateAnswerModel(
    val value : Int,
    val questionId : String,
    val childId : String
)
{
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}