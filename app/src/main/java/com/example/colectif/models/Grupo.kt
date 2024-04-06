package com.example.colectif.models

data class Grupo(
    var id: String, var administrador: String, var app: String, var contrasenia: String, var email: String, var imagen: Int, var nombre: String,  var plan: String, var precio: String) {
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
    ) : this("", administrador, app, contrasenia, email, imagen, nombre, plan, precio)


}
