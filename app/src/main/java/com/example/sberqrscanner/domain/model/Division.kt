package com.example.sberqrscanner.domain.model

data class Division(
    val id: String,
    val name: String,
    val checked: Boolean,
    val number: Int,
    val floor: Int,
    val wing: String,
    val phone: String,
    val fio: String
)