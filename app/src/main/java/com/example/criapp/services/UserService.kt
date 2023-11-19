package com.example.criapp.services

import com.example.criapp.model.CreateUserModel

class UserService {

    val criappClient = CriappClient();

    fun createUser(data : CreateUserModel){
        criappClient.postWithoutCredentials("/user", data.toString())
    }
}