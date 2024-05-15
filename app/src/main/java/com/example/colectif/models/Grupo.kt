package com.example.colectif.models

// Objeto para guardar toda la información de los grupos
data class Grupo(
    var id: String, var administrador: String, var app: String, var contrasenia: String, var email: String, var imagen: Int, var nombre: String,  var plan: String, var precio: String, var fecha: String) {


    constructor(
        administrador: String,
        nombre: String,
        app: String,
        plan: String,
        precio: String,
        email: String,
        contrasenia: String,
        imagen: Int,
        fecha: String
    ) : this("", administrador, app, contrasenia, email, imagen, nombre, plan, precio, fecha)




}
