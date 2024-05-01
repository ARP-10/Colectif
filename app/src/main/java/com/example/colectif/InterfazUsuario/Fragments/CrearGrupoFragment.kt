package com.example.colectif.InterfazUsuario.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.colectif.R
import com.example.colectif.databinding.FragmentCrearGrupoBinding
import com.example.colectif.models.Grupo
import com.example.colectif.models.Solicitud
import com.example.colectif.models.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCrearGrupoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")

        super.onViewCreated(view, savedInstanceState)
        // Netflix marcado por defecto
        binding.spinnerApps.setSelection(0)

        binding.btnCrearGrupo.setOnClickListener {
            app = binding.spinnerApps.selectedItem.toString()
            nombreGrupo = binding.editNombre.text.toString()
            plan = binding.spinnerPlan.selectedItem.toString()
            precio = binding.txtPrecioCorrecto.text.toString()
            emailRegistro = binding.editEmailRegistro.text.toString()
            contraseniaRegistro = binding.editContrasenia.text.toString()

            // Obtener la imagen correspondiente al grupo seleccionado
            val drawableApp = when (app) {
                "Netflix" -> R.drawable.netflix
                "Spotify" -> R.drawable.spotify
                "Amazon" -> R.drawable.amazon
                "Disney" -> R.drawable.disney
                else -> R.drawable.error
            }

            // TODO: comprobar que el admin tenga un grupo igual y no dejarle duplicar

            if (nombreGrupo.isEmpty() || emailRegistro.isEmpty() || contraseniaRegistro.isEmpty()) {
                Snackbar.make(binding.root, "Por favor, rellene todos los campos", Snackbar.LENGTH_SHORT).show()
            } else {

                val grupo = Grupo(auth.currentUser!!.uid.toString(),nombreGrupo, app, plan, precio, emailRegistro, contraseniaRegistro, drawableApp)

                val gruposRef = database.getReference("groups")

                // Generar un nuevo ID para el grupo
                val nuevoGrupoRef = gruposRef.push()

                nuevoGrupoRef.setValue(grupo)
                    .addOnSuccessListener {
                        // Sumar +1 en la ID de los grupos que el usuario tiene
                        sumarId(nuevoGrupoRef.key!!)
                        database.getReference("groups").child(nuevoGrupoRef.key!!).child("id").setValue(nuevoGrupoRef.key!!)
                        //Snackbar.make(binding.root,"Grupo creado exitosamente", Snackbar.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("CrearGrupo", "Error al crear grupo: ${e.message}")
                        Snackbar.make(binding.root, "Error al crear grupo", Snackbar.LENGTH_SHORT).show()
                    }


            }

            findNavController().navigate(R.id.action_crearGrupoFragment_to_inicioFragment)
            // TODO: se crea un recycler infinito en inicio fragment, hay que cambiar de pantalla y volver para que salga bien


        }

        // Configuración spinner
        binding.spinnerApps.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                // Comprobamos que app ha elegido
                when (position) {
                    0 -> {
                        binding.imgGrupo.setImageResource(R.drawable.netflix)
                        // Buscamos el string-array correspondiente
                        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.plan_netflix, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerPlan.adapter = adapter
                        // Manejamos el spinner en base a ese string-array
                        binding.spinnerPlan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                when (position) {
                                    0 -> binding.txtPrecioCorrecto.text = "5.49 €/mes"
                                    1 -> binding.txtPrecioCorrecto.text = "12.99 €/mes"
                                    2 -> binding.txtPrecioCorrecto.text = "17.99 €/mes"
                                }
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }
                        }
                    }
                    1 -> {
                        binding.imgGrupo.setImageResource(R.drawable.spotify)
                        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.plan_sopitfy, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerPlan.adapter = adapter
                        binding.spinnerPlan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                when (position) {
                                    0 -> binding.txtPrecioCorrecto.text = "14.99 €/mes"
                                    1 -> binding.txtPrecioCorrecto.text = "17.99 €/mes"
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }
                        }
                    }
                    2 -> {
                        binding.imgGrupo.setImageResource(R.drawable.amazon)
                        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.plan_amazon, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerPlan.adapter = adapter
                        binding.spinnerPlan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                when (position) {
                                    0 -> binding.txtPrecioCorrecto.text = "4.99 €/mes"
                                    1 -> binding.txtPrecioCorrecto.text = "6.98 €/mes"
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }
                        }
                    }
                    3 -> {
                        binding.imgGrupo.setImageResource(R.drawable.disney)
                        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.plan_disney, android.R.layout.simple_spinner_item)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerPlan.adapter = adapter
                        binding.spinnerPlan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                when (position) {
                                    0 -> binding.txtPrecioCorrecto.text = "5.99 €/mes"
                                    1 -> binding.txtPrecioCorrecto.text = "8.99 €/mes"
                                    2 -> binding.txtPrecioCorrecto.text = "11.99 €/mes"
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }

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
                TODO("Not yet implemented")
            }
        })

    }


    override fun onDetach() {
        super.onDetach()
    }
}
