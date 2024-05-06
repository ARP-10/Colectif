package com.example.colectif.Objetos

import com.example.colectif.R
import com.example.colectif.models.CatalogoGrupos
import com.example.colectif.models.Grupo

object ObjetoGrupos {

    private val gruposNetflix = listOf(
        Grupo(
            nombre = "El desván",
            administrador = "Paco",
            app = "Netflix",
            plan = "Estándar",
            precio = "12.99"/*5.49  17.99*/,
            imagen = R.drawable.netflix,
            email = "netflix@correo.com",
            contrasenia = "123456",
            fecha = "121212"
        ),
        Grupo(
            nombre = "tusa",
            administrador = "Federico",
            app = "Netflix",
            plan = "Estándar",
            precio = "12.99"/*5.49  17.99*/,
            imagen = R.drawable.netflix,
            email = "netflix@correo.com",
            contrasenia = "123456",
            fecha = "121212"
        )

    )

    private val gruposAmazon = listOf(
        Grupo(
            nombre = "Antanarejos",
            administrador = "Vanesa" ,
            app = "Amazon",
            plan = "Plan único",
            precio = "4.99" , /*2.49*/
            imagen = R.drawable.amazon,
            email = "amazon@correo.com",
            contrasenia = "123456",
            fecha = "121212"
        ),

    )
    private val gruposDisney = listOf(
        Grupo(
            nombre = "El pozuelo",
            administrador = "Angel",
            app = "Disney",
            plan = "Estándar con anuncios",
            precio = "5.99" /*8.99  11.99*/,
            imagen = R.drawable.disney,
            email = "disney@correo.com",
            contrasenia = "123456",
            fecha = "121212"
        ),
    )
    private val gruposSpotify = listOf(
        Grupo(
            nombre = "Rock and Roll",
            administrador = "Jorge",
            app = "Spotify",
            plan = "Individual",
            precio = "5.99"/*10.99 14.99  17.99*/,
            imagen = R.drawable.spotify,
            email = "spotify@correo.com",
            contrasenia = "123456",
            fecha = "121212"
        ),
    )


    val catalogoGrupos = listOf(
        CatalogoGrupos(
            catalogo = "Netflix",
            grupos = gruposNetflix
        ),
        CatalogoGrupos(
            catalogo = "Amazon",
            grupos = gruposAmazon
        ),
        CatalogoGrupos(
                catalogo = "Disney",
        grupos = gruposDisney
        ),
        CatalogoGrupos(
            catalogo = "Spotify",
            grupos = gruposSpotify
        )

    )
}