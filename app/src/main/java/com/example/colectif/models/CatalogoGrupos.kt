package com.example.colectif.models

//Objeto utilizado para agrupar los grupos en varios catálogos
// y también para la creación del doble recyclerview de ListaGruposFragment
data class CatalogoGrupos(val catalogo: String, var grupos: List<Grupo>) {
}