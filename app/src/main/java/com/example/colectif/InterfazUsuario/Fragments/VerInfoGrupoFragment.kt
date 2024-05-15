package com.example.colectif.InterfazUsuario.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.colectif.R
import com.example.colectif.databinding.FragmentVerInfoGrupoBinding
import com.example.colectif.models.Solicitud
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Este fragmento muestra información detallada sobre un grupo, incluyendo el administrador,
 * la aplicación asociada al grupo, el nombre del grupo, el plan, el precio, el número máximo
 * de usuarios permitidos y el número actual de usuarios. También permite a los usuarios enviar
 * solicitudes para unirse al grupo si aún no son miembros.
 */

class VerInfoGrupoFragment : Fragment() {

    private lateinit var binding: FragmentVerInfoGrupoBinding
    private lateinit var auth: FirebaseAuth
    private var idGrupo:String? = null
    private lateinit var idAdmin: String

    //Asocia elementos del activity con el fragment actual, en este caso recoge el id del grupo
    override fun onAttach(context: Context) {
        super.onAttach(context)
        idGrupo = arguments?.getString("idGrupo", "")
    }

    // Este método infla el diseño del fragmento y devuelve la vista correspondiente
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

        // Obtener una referencia a la base de datos de grupos y usuarios
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("groups")
        val databaseReference2: DatabaseReference = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users")

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(childSnapshot: DataSnapshot) {

                // Obtener los valores de cada hijo
                val administradorId = childSnapshot.child(idGrupo!!).child("administrador").value.toString()
                idAdmin = administradorId
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
                        binding.txtUsuariosPermitidos.text = usuariosMax
                        binding.txtUsuariosActuales.text = usuariosActuales

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not necessary")
                        }

                    })
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })





        // Al pulsar el botón se comprueba si hay solicitud pendiente
        binding.btnSolicitudEntrar.setOnClickListener {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                comprobarSolicitudPendiente(userId, idGrupo!!, view, idAdmin)
            } else {
                Snackbar.make(view, "Debe iniciar sesión para enviar una solicitud", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
    }


    // Crea la solicitud en la base de datos y se lo envía al admin del grupo
    fun enviarSolicitud(idGrupo: String, view: View) {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Snackbar.make(view, "Debe iniciar sesión para enviar una solicitud", Snackbar.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        val groupsRef = database.getReference("groups").child(idGrupo)

        // Obtener el ID del administrador
        groupsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adminId = snapshot.child("administrador").value.toString()

                val solicitud = Solicitud(adminId, userId, idGrupo)
                val solicitudesRef = database.getReference("solicitudes")

                val newRef = solicitudesRef.push()
                val nuevaId = newRef.key!!

                // Establecer la clave generada como el ID de la solicitud
                solicitud.id = nuevaId

                // Establecer la solicitud
                newRef.setValue(solicitud)

                // Actualizar el número de solicitudes del administrador
                val usersRef = database.getReference("users").child(adminId)
                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(userSnapshot: DataSnapshot) {
                        var numSolicitudActual = userSnapshot.child("numSolicitudes").getValue(Int::class.java) ?: 0
                        numSolicitudActual++
                        usersRef.child("numSolicitudes").setValue(numSolicitudActual)

                        // Guardar la solicitud en el usuario receptor
                        val solicitudUsuarioRef = usersRef.child("solicitudes").child(numSolicitudActual.toString())
                        solicitudUsuarioRef.setValue(nuevaId)

                        Snackbar.make(view, "Solicitud enviada exitosamente", Snackbar.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Snackbar.make(view, "Error al enviar la solicitud", Snackbar.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(view, "Error al enviar la solicitud", Snackbar.LENGTH_SHORT).show()
            }
        })
    }


    // Comprueba si ya se envío solicitud a ese grupo
    fun comprobarSolicitudPendiente(userId: String, idGrupo: String, view: View, adminId: String) {
        val database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("solicitudes")

        // Buscar solicitudes pendientes para el usuario y el grupo especificado
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var solicitudPendiente = false
                for (solicitudSnapshot in snapshot.children) {

                    // Recoge la información de la base de datos
                    val grupoId = solicitudSnapshot.child("idGrupo").value.toString()
                    val receptorId = solicitudSnapshot.child("idReceptor").value.toString()
                    val mandatarioId = solicitudSnapshot.child("idMandatario").value.toString()

                    // Determina si esta pendiente la solicitud
                    if (grupoId == idGrupo && mandatarioId == userId && receptorId == adminId) {
                        solicitudPendiente = true
                        break
                    }
                }
                if (solicitudPendiente) {

                    // Si ya hay solicitud pendiente
                    Snackbar.make(view, "Ya has enviado una solicitud a este grupo", Snackbar.LENGTH_SHORT).show()
                } else {

                    // Si no hay solicitud pendiente, enviar la solicitud
                    enviarSolicitud(idGrupo, view)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(view, "Error al comprobar las solicitudes pendientes", Snackbar.LENGTH_SHORT).show()
            }
        })
    }
}