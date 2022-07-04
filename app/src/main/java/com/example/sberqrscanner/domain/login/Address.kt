package com.example.sberqrscanner.domain.login

data class Address(
    val id: String,
    val name: String
){
    override fun toString(): String {
        return name
    }
}
