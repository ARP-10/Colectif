package com.example.colectif.models

data class User(val name : String,val firstSurName : String, val secondSurName : String
                , val mail : String, val userName : String, val imagen : String, val groups: ArrayList<String> = ArrayList(), var numGrupos: Int, var numSolicitudes: Int)

