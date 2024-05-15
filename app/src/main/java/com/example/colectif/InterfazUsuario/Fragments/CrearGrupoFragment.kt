package com.example.colectif.InterfazUsuario.Fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.colectif.R
import com.example.colectif.databinding.FragmentCrearGrupoBinding
import com.example.colectif.models.Grupo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

/**
 * Fragmento que permite a los usuarios crear un nuevo grupo.
 * Recolecta los datos necesarios, como el nombre del grupo, la aplicación asociada, el plan, el precio, el correo electrónico y la contraseña de registro.
 * Verifica que todos los campos estén completos y luego guarda la información del grupo en la base de datos de Firebase.
 * Utiliza spinners para permitir la selección de la aplicación y el plan correspondientes.
 * Muestra una imagen asociada a la aplicación seleccionada y actualiza el precio y el número total de usuarios en función del plan seleccionado.
 * Al crear un grupo exitosamente, muestra un mensaje de confirmación.
 */
class CrearGrupoFragment: Fragment() {

    private lateinit var binding: FragmentCrearGrupoBinding
    private lateinit var app: String
    private lateinit var nombreGrupo: String
    private lateinit var plan: String
    private lateinit var precio: String
    private lateinit var emailRegistro: String
    private lateinit var contraseniaRegistro: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    // Este método infla el diseño del fragmento y devuelve la vista correspondiente
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCrearGrupoBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Aqui se inicializan los componentes y se desarrolla todas las funcionalidades del fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")

        super.onViewCreated(view, savedInstanceState)

        // Netflix marcado por defecto
        binding.spinnerApps.setSelection(0)

        // Botón de crear grupo, para recoger todos los datos y añadirlo a la base de datos
        binding.btnCrearGrupo.setOnClickListener {
            app = binding.spinnerApps.selectedItem.toString()
            nombreGrupo = binding.editNombre.text.toString()
            plan = binding.spinnerPlan.selectedItem.toString()
            precio = binding.txtPrecioCorrecto.text.toString()
            emailRegistro = binding.editEmailRegistro.text.toString()
            contraseniaRegistro = binding.editContrasenia.text.toString()
            var fechaActual = Calendar.getInstance()
            var año = fechaActual.get(Calendar.YEAR)
            var mes = fechaActual.get(Calendar.MONTH) + 1
            var dia = fechaActual.get(Calendar.DAY_OF_MONTH)
            var hora = fechaActual.get(Calendar.HOUR_OF_DAY)
            var minuto = fechaActual.get(Calendar.MINUTE)
            var segundo = fechaActual.get(Calendar.SECOND)
            var fechaYHora = "$año-$mes-$dia T$hora-$minuto-$segundo"

            // Obtener la imagen correspondiente al grupo seleccionado
            val drawableApp = when (app) {
                "Netflix" -> R.drawable.netflix
                "Spotify" -> R.drawable.spotify
                "Amazon" -> R.drawable.amazon
                "Disney" -> R.drawable.disney
                else -> R.drawable.error
            }

            // Comprueba que todos los campos estén rellenados
            if (nombreGrupo.isEmpty() || emailRegistro.isEmpty() || contraseniaRegistro.isEmpty()) {
                Snackbar.make(binding.root, "Por favor, rellene todos los campos", Snackbar.LENGTH_SHORT).show()
            } else {

                val grupo = Grupo(auth.currentUser!!.uid,nombreGrupo, app, plan, precio, emailRegistro, contraseniaRegistro, drawableApp, fechaYHora)

                val gruposRef = database.getReference("groups")

                // Generar un nuevo ID para el grupo
                val nuevoGrupoRef = gruposRef.push()


                // Se crea una nueva id del grupo recién creado
                nuevoGrupoRef.setValue(grupo)
                    .addOnSuccessListener {
                        // Suma +1 en el número de usuarios que tiene el grupo y también se le añade en su nodo de users
                        sumarId(nuevoGrupoRef.key!!)
                        database.getReference("groups").child(nuevoGrupoRef.key!!).child("id").setValue(nuevoGrupoRef.key!!)
                        database.getReference("groups").child(nuevoGrupoRef.key!!).child("numMax").setValue(binding.txtUsuariosTotal.text.toString())
                        database.getReference("groups").child(nuevoGrupoRef.key!!).child("numUsuarios").setValue(1)
                        database.getReference("groups").child(nuevoGrupoRef.key!!).child("users").child("1").setValue(auth.currentUser!!.uid)
                        context?.let { it1 -> mostrarMensaje(it1, "Grupo creado exitosamente", "¡Has creado un grupo!") }
                    }
                    .addOnFailureListener { e ->
                        Snackbar.make(binding.root, "Error al crear grupo", Snackbar.LENGTH_SHORT).show()
                    }


            }

            // Dejar lo rellenado en blanco
            var texto = ""
            val editable = Editable.Factory.getInstance().newEditable(texto)
            binding.editNombre.text = editable
            binding.editEmailRegistro.text = editable
            binding.editContrasenia.text = editable

        }



        // Configuración spinner de las aplicaciones
        binding.spinnerApps.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                // Comprobamos que app ha elegido
                when (position) {
                    0 -> {
                        binding.imgGrupo.setImageResource(R.drawable.netflix2)

                        // Buscamos el string-array correspondiente
                        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.plan_netflix, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerPlan.adapter = adapter

                        // Manejamos el spinner en base a ese string-array
                        binding.spinnerPlan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                when (position) {
                                    0 -> {
                                        binding.txtPrecioCorrecto.text = "5.49 €/mes"
                                        binding.txtUsuariosTotal.text = "2"
                                    }
                                    1 -> {
                                        binding.txtPrecioCorrecto.text = "12.99 €/mes"
                                        binding.txtUsuariosTotal.text = "2"
                                    }
                                    2 -> {
                                        binding.txtPrecioCorrecto.text = "17.99 €/mes"
                                        binding.txtUsuariosTotal.text = "6"
                                    }
                                }
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                    }
                    1 -> {
                        binding.imgGrupo.setImageResource(R.drawable.spotify2)
                        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.plan_sopitfy, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerPlan.adapter = adapter
                        binding.spinnerPlan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                when (position) {
                                    0 -> {
                                        binding.txtPrecioCorrecto.text = "14.99 €/mes"
                                        binding.txtUsuariosTotal.text = "2"
                                    }
                                    1 -> {
                                        binding.txtPrecioCorrecto.text = "17.99 €/mes"
                                        binding.txtUsuariosTotal.text = "6"
                                    }
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                    }
                    2 -> {
                        binding.imgGrupo.setImageResource(R.drawable.amazon2)
                        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.plan_amazon, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerPlan.adapter = adapter
                        binding.spinnerPlan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                when (position) {
                                    0 -> {
                                        binding.txtPrecioCorrecto.text = "4.99 €/mes"
                                        binding.txtUsuariosTotal.text = "6"
                                    }
                                    1 -> {
                                        binding.txtPrecioCorrecto.text = "6.98 €/mes"
                                        binding.txtUsuariosTotal.text = "6"
                                    }
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                    }
                    3 -> {
                        binding.imgGrupo.setImageResource(R.drawable.disney2)
                        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.plan_disney, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerPlan.adapter = adapter
                        binding.spinnerPlan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                when (position) {
                                    0 -> {
                                        binding.txtPrecioCorrecto.text = "5.99 €/mes"
                                        binding.txtUsuariosTotal.text = "7"
                                    }
                                    1 -> {
                                        binding.txtPrecioCorrecto.text = "8.99 €/mes"
                                        binding.txtUsuariosTotal.text = "7"
                                    }
                                    2 -> {
                                        binding.txtPrecioCorrecto.text = "11.99 €/mes"
                                        binding.txtUsuariosTotal.text = "7"
                                    }
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }

    // Suma +1 en el número de grupos que tiene el usuario y también se le añade en su nodo de groups
    fun sumarId(idGrupo: String){
        var database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var numGrupoActual = snapshot.child(auth.currentUser!!.uid).child("numGrupos").getValue(Int::class.java) ?: 0
                numGrupoActual++
                ref.child(auth.currentUser!!.uid).child("numGrupos").setValue(numGrupoActual)
                database.getReference("users").child(auth.currentUser!!.uid).child("groups").child(numGrupoActual.toString()).setValue(idGrupo)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    // Cuadro que muestra mensaje de aviso
    fun mostrarMensaje(contexto: Context, titulo: String, mensaje: String){
        val builder = AlertDialog.Builder(contexto)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)

        builder.setPositiveButton("Aceptar") {dialog, _ ->

            dialog.dismiss()

        }

        val dialog = builder.create()
        dialog.show()
    }


    override fun onDetach() {
        super.onDetach()
    }

}
