package com.example.colectif.models

import androidx.annotation.DrawableRes

data class Grupo (var nombre: String, var administrador: String, var categoria: String, var plan: String, var precio: Double, @DrawableRes var imagen: Int) {
    //  , var id: Int,
}