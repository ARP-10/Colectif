package com.example.colectif.models

// Objeto para guardar toda la información de las solicitudes
data class Solicitud(var id: String,var idReceptor:String, var idMandatario:String, var idGrupo:String) {
    constructor(
        idReceptor: String,
        idMandatario: String,
        idGrupo: String
    ) : this("", idReceptor, idMandatario, idGrupo)
}