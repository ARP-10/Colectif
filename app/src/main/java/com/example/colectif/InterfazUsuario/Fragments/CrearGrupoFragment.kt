package com.example.colectif.InterfazUsuario.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.colectif.R
import com.example.colectif.databinding.FragmentCrearGrupoBinding
import com.example.colectif.models.Grupo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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

        // Comprobar si todos los campos están rellenos
        binding.btnCrearGrupo.setOnClickListener {
            app = binding.spinnerApps.selectedItem.toString()
            nombreGrupo = binding.editNombre.text.toString()
            plan = binding.spinnerPlan.selectedItem.toString()
            precio = binding.txtPrecioCorrecto.text.toString()
            emailRegistro = binding.editEmailRegistro.text.toString()
            contraseniaRegistro = binding.editContrasenia.text.toString()

            if (nombreGrupo.isEmpty() || emailRegistro.isEmpty() || contraseniaRegistro.isEmpty()) {
                Snackbar.make(binding.root, "Por favor, rellene todos los campos", Snackbar.LENGTH_SHORT).show()
            } else {
                // Crear un objeto Grupo con los datos obtenidos
                val grupo =
                    Grupo(nombreGrupo, app, plan, precio, emailRegistro, contraseniaRegistro)

                // Obtener una referencia al nodo "groups" en la base de datos
                val gruposRef = database.getReference("groups")

                // Generar un nuevo ID para el grupo
                val nuevoGrupoRef = gruposRef.push()

                // Guardar el grupo en la base de datos utilizando el ID generado
                nuevoGrupoRef.setValue(grupo)
                    .addOnSuccessListener {
                        Snackbar.make(
                            binding.root,
                            "Grupo creado exitosamente",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("CrearGrupo", "Error al crear grupo: ${e.message}")
                        Snackbar.make(binding.root, "Error al crear grupo", Snackbar.LENGTH_SHORT)
                            .show()
                    }
            }
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
                                    0 -> binding.txtPrecioCorrecto.text = "10.99 €/mes"
                                    1 -> binding.txtPrecioCorrecto.text = "5.99 €/mes"
                                    2 -> binding.txtPrecioCorrecto.text = "14.99 €/mes"
                                    3 -> binding.txtPrecioCorrecto.text = "17.99 €/mes"
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
                                    1 -> binding.txtPrecioCorrecto.text = "2.49 €/mes"
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




    // Permite desasociar elementos de la Activity (InicioActivity) con el fragment
    override fun onDetach() {
        super.onDetach()
    }
}
