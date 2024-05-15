package com.example.colectif.InterfazUsuario.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.colectif.Adapter.AdapterUsuarios
import com.example.colectif.R
import com.example.colectif.databinding.FragmentVerGrupoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Fragmento que muestra los detalles de un grupo para los miembros del grupo.
 * Muestra información como el administrador del grupo, la aplicación del grupo, la contraseña,
 * el correo electrónico, el plan, el precio, y la lista de usuarios que pertenecen al grupo.
 * Permite a los usuarios ver la información del grupo y salir del grupo si lo desean.
 * Utiliza Firebase Realtime Database para recuperar y mostrar los datos del grupo y sus usuarios.
 */

class VerGrupoFragment : Fragment() {

    private lateinit var binding: FragmentVerGrupoBinding
    private var idGrupo:String? = null
    private lateinit var adaptadorUsuarios : AdapterUsuarios
    private lateinit var listaUsuarios: ArrayList<String>
    private lateinit var database: FirebaseDatabase
    private lateinit var textPassword: TextView
    private lateinit var txtShowPassword: TextView

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
        binding = FragmentVerGrupoBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Aqui se inicializan los componentes y se desarrolla todas las funcionalidades del fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        listaUsuarios = ArrayList()

        // Configuración del RecyclerView
        adaptadorUsuarios = context?.let { AdapterUsuarios(it, listaUsuarios) }!!
        binding.recyclerVerUsuarios.adapter = adaptadorUsuarios
        binding.recyclerVerUsuarios.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // Añadir los usuarios al recycler
        recogerUsuarios()

        var ref = database.getReference("groups")
        var ref2 = database.getReference("users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(childSnapshot: DataSnapshot) {

                // Obtener los valores de cada hijo
                val administradorId = childSnapshot.child(idGrupo!!).child("administrador").value.toString()
                val app = childSnapshot.child(idGrupo!!).child("app").value.toString()
                val contrasenia = childSnapshot.child(idGrupo!!).child("contrasenia").value.toString()
                val email = childSnapshot.child(idGrupo!!).child("email").value.toString()
                val nombre = childSnapshot.child(idGrupo!!).child("nombre").value.toString()
                val plan = childSnapshot.child(idGrupo!!).child("plan").value.toString()

                val precioString = childSnapshot.child(idGrupo!!).child("precio").value.toString()
                val precio = precioString.substringBefore(" ")
                    .replace(",", ".")
                    .toDoubleOrNull() ?: 0.0

                var numUsuariosActual = childSnapshot.child(idGrupo!!).child("numUsuarios").value.toString().toDouble()


                // Inicializar elementos para mostrar/ocultar contraseña
                textPassword = view.findViewById(R.id.txt_password)
                txtShowPassword = view.findViewById(R.id.txt_show_password)

                // Estado inicial de la visibilidad de la contraseña
                var passwordVisible = false

                // Llamar a la función para mostrar/ocultar la contraseña al inicio
                togglePasswordVisibility(passwordVisible, contrasenia)

                // Establecer el onClickListener para mostrar/ocultar la contraseña
                txtShowPassword.setOnClickListener {
                    passwordVisible = !passwordVisible
                    togglePasswordVisibility(passwordVisible, contrasenia)
                }

                // Obtener el nombre del admin de la base de datos de "users"
                ref2.child(administradorId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val nombreAdmin = snapshot.child("name").value.toString()

                            // Actualizar la interfaz de usuario con los datos recuperados
                            binding.txtAdministrador.text = nombreAdmin

                            //binding.txtPassword.text = contrasenia
                            binding.txtCorreo.text = email

                            val drawableApp = when (app) {
                                "Netflix" -> R.drawable.netflix2
                                "Spotify" -> R.drawable.spotify2
                                "Amazon Prime" -> R.drawable.amazon2
                                "Disney +" -> R.drawable.disney2
                                else -> R.drawable.error
                            }
                            binding.imgGrupo.setImageResource(drawableApp)
                            binding.txtNombregrupo.text = nombre
                            binding.txtPlan.text = plan
                            numUsuariosActual = precio / numUsuariosActual

                            // Redondear el resultado de la división a dos decimales
                            val gastosRedondeados = String.format("%.2f", numUsuariosActual)

                            binding.txtPrecio.text = "$precio €"
                            binding.txtGastos.text = "$gastosRedondeados €"

                        }

                        override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        // Botón salir grupo
        binding.btnSalirGrupo.setOnClickListener {
            context?.let { it1 -> mostrarMensaje(it1, "Abandonar grupo", "¿Deseas salir del grupo?") }
        }
    }

    // Recuperar a los usuarios que pertenecen al grupo
    fun recogerUsuarios() {
        val ref = database.getReference("groups")
        val ref2 = database.getReference("users")
        val ref3 = database.getReference("groups").child(idGrupo!!).child("users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    // Recoger información del administrador
                    val adminId = snapshot.child(idGrupo!!).child("administrador").value.toString()

                    ref2.child(adminId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(adminSnapshot: DataSnapshot) {
                            if (adminSnapshot.exists()) {
                                val nombreAdmin = adminSnapshot.child("userName").value.toString()
                                adaptadorUsuarios.addUsuario(nombreAdmin)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })

                    // Recoge el nombre de los usuarios que pertenezcan al grupo, incluyendo al administrador
                    ref3.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                for(snapshot in snapshot.children){
                                    var idUsuario = snapshot.key

                                    ref2.child(idUsuario!!).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(userSnapshot: DataSnapshot) {
                                            if (userSnapshot.exists()) {
                                                val nombreUser = userSnapshot.child("userName").value.toString()

                                                // Se añade el nombre de los usuarios
                                                adaptadorUsuarios.addUsuario(nombreUser)
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            // Manejar errores de base de datos
                                        }
                                    })
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Cuadro que muestra mensaje de aviso
    fun mostrarMensaje(contexto: Context, titulo: String, mensaje: String){
        val builder = AlertDialog.Builder(contexto)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)


        // Botón aceptar al mensaje
        builder.setPositiveButton("Sí, estoy de acuerdo") { dialog, _ ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId != null) {
                val database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
                val referenciaUsuario = database.getReference("users").child(userId)
                val ref3 = database.getReference("groups").child(idGrupo!!)
                val ref4 = database.getReference("groups").child(idGrupo!!).child("users")

                referenciaUsuario.child("groups").addListenerForSingleValueEvent(object : ValueEventListener {

                    // Elimina el grupo del nodo groups del usuario
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (grupoSnapshot in snapshot.children) {
                            val grupoId = grupoSnapshot.value.toString()
                            if (grupoId == idGrupo) {
                                grupoSnapshot.ref.removeValue()
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

                // Reduce -1 el número de usuarios del grupo
                ref3.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var numUsuariosActual = snapshot.child("numUsuarios").getValue(Int::class.java) ?: 0
                        numUsuariosActual--
                        ref3.child("numUsuarios").setValue(numUsuariosActual)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

                // Elimina al usuario del nodo users del grupo
                ref4.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (userSnapchot in snapshot.children) {
                            val usuarioId = userSnapchot.value.toString()
                            if (usuarioId == userId) {

                                userSnapchot.ref.removeValue()
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            // Ir a pantalla buscar grupos
            findNavController().navigate(R.id.action_verGrupoFragment_to_listaGruposFragment)
            dialog.dismiss()
        }


        // Botón Rechazar
        builder.setNegativeButton("No, quiero seguir en el grupo") {dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    // Función para mostrar u ocultar la contraseña
    private fun togglePasswordVisibility(showPassword: Boolean, password: String) {
        if (showPassword) {
            // Mostrar la contraseña
            textPassword.text = password
            txtShowPassword.text = "Ocultar"
        } else {
            // Ocultar la contraseña
            textPassword.text = "********"
            txtShowPassword.text = "Mostrar"
        }
    }







}