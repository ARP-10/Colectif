package com.example.colectif.models

data class Grupo(
    var id: String, var administrador: String, var nombre: String, var app: String, var plan: String, var precio: String, var email: String,
    var contrasenia: String, var imagen: Int) {
// administrador tiene que ser de tipo User

    constructor(
        administrador: String,
        nombre: String,
        app: String,
        plan: String,
        precio: String,
        email: String,
        contrasenia: String,
        imagen: Int
    ) : this("", administrador, nombre, app, plan, precio, email, contrasenia, imagen)
}
