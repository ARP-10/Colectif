package com.example.colectif.models

// Objeto creado especificamente para el fragment de VerGrupoAdmin,
// para poder determinar si el usuario ha pagado
data class UsuarioGrupo(var id: String, var nombreUsuario: String, var idGrupo: String, var pagado: Boolean)
