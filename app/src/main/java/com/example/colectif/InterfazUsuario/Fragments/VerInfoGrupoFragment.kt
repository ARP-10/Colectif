package com.example.colectif.InterfazUsuario.Fragments

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.colectif.R
import com.example.colectif.databinding.FragmentVerGrupoBinding
import com.example.colectif.databinding.FragmentVerInfoGrupoBinding
import com.example.colectif.models.Solicitud
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class VerInfoGrupoFragment : Fragment() {

    private lateinit var binding: FragmentVerInfoGrupoBinding
    private lateinit var auth: FirebaseAuth
    private var idGrupo:String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        idGrupo = arguments?.getString("idGrupo", "")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVerInfoGrupoBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Mostrar los datos en el fragment
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("groups")
        val databaseReference2: DatabaseReference = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users")

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(childSnapshot: DataSnapshot) {
                // Obtener los valores de cada hijo
                val administradorId = childSnapshot.child(idGrupo!!).child("administrador").value.toString()
                val app = childSnapshot.child(idGrupo!!).child("app").value.toString()
                val nombre = childSnapshot.child(idGrupo!!).child("nombre").value.toString()
                val plan = childSnapshot.child(idGrupo!!).child("plan").value.toString()
                val precio = childSnapshot.child(idGrupo!!).child("precio").value.toString()
                val usuariosMax = childSnapshot.child(idGrupo!!).child("numMax").value.toString()
                val usuariosActuales = childSnapshot.child(idGrupo!!).child("numUsuarios").value.toString()

                // Obtener el nombre del admin de la bbdd de "users"
                databaseReference2.child(administradorId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val nombreAdmin = snapshot.child("name").value.toString()

                        // Actualizar la interfaz de usuario con los datos recuperados
                        binding.txtAdministrador.text = nombreAdmin
                        binding.txtApp.text = app

                        val drawableApp = when (app) {
                            "Netflix" -> R.drawable.netflix
                            "Spotify" -> R.drawable.spotify
                            "Amazon Prime" -> R.drawable.amazon
                            "Disney +" -> R.drawable.disney
                            else -> R.drawable.error
                        }
                        binding.imgGrupo.setImageResource(drawableApp)
                        binding.txtNombregrupo.text = nombre
                        binding.txtPlan.text = plan
                        binding.txtPrecioInfo.text = precio
                        Log.d("VerInfoGrupoFragment", "Precio: $precio")
                        binding.txtUsuariosPermitidos.text = usuariosMax
                        binding.txtUsuariosActuales.text = usuariosActuales
                        Log.d("VerInfoGrupoFragment", "Valores asignados correctamente")

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("VerInfoGrupoFragment", "Error al leer datos del grupo: ${error.message}")
                }
            })




        binding.btnSolicitudEntrar.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val adminId = auth.currentUser!!.uid
            if (userId != null) {
                // Llamar a la función enviarSolicitud
                Log.d("VerInfoGrupoFragment", "Enviar solicitud. UserID: $userId, AdminID: $adminId, GrupoID: $idGrupo")
                enviarSolicitud(userId, adminId, idGrupo!!, view)
            } else {
                Log.d("VerInfoGrupoFragment", "Usuario no autenticado al intentar enviar solicitud")
                Snackbar.make(view, "Debe iniciar sesión para enviar una solicitud", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDetach() {
        super.onDetach()
    }

    fun enviarSolicitud(idUser: String, idAdmin: String, idGrupo: String, view: View){
        var solicitud = Solicitud(idAdmin,idUser,idGrupo)
        var database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("solicitudes")
        var nuevaId= ""

        val newRef = ref.push()
        newRef.setValue(solicitud)
        nuevaId = newRef.key!!
        ref.child(nuevaId).child("id").setValue(nuevaId)

        var ref2 = database.getReference("users")
        ref2.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var numSolicitudActual = snapshot.child(idAdmin).child("numSolicitudes").getValue(Int::class.java) ?: 0
                numSolicitudActual++
                ref2.child(idAdmin).child("numSolicitudes").setValue(numSolicitudActual)
                database.getReference("users").child(idAdmin).child("solicitudes").child(numSolicitudActual.toString()).setValue(nuevaId)

                //val mensaje = if (idAdmin == idUser) "Grupo creado exitosamente" else "Solicitud enviada exitosamente"
                Snackbar.make(view, "Solicitud enviada exitosamente", Snackbar.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

}