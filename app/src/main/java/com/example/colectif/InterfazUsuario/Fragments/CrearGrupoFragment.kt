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
    private lateinit var nombreGrupo: String
    private lateinit var plan: String
    private var precio: Double = 0.0
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val gruposRef = database.getReference("groups")

    // Permite asociar elementos de la Activity (InicioActivity) con el fragment
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCrearGrupoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Netflix marcado por defecto
        binding.spinnerApps.setSelection(0)

        // TODO: Comprobar si todos los campos están rellenos
        binding.btnCrearGrupo.setOnClickListener {
            val nombreGrupo = binding.txtNombre.text.toString()
            val app = binding.spinnerApps.selectedItem.toString()
            val plan = binding.spinnerPlan.selectedItem.toString()
            val precio = binding.txtPrecio.text.toString().toDoubleOrNull()
            Log.d("Valores", "Nombre del Grupo: $nombreGrupo")
            Log.d("Valores", "Aplicación: $app")
            Log.d("Valores", "Plan: $plan")
            Log.d("Texto Precio", binding.txtPrecio.text.toString())

            if (nombreGrupo.isNotEmpty() && app.isNotEmpty() && plan.isNotEmpty() && precio != null) {
                val grupo = Grupo(nombreGrupo, auth.currentUser?.uid.toString(), app, plan, precio, imagen = R.drawable.foto_perfil)
                agregarGrupo(grupo)
            } else {
                Snackbar.make(view, "Rellene todos los campos", Snackbar.LENGTH_SHORT).show()
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

    // TODO: COMPROBAR SI FUNCIONA
    private fun agregarGrupo(grupo: Grupo) {
        val grupoRef = gruposRef.push() // Genera una clave única para el grupo
        grupoRef.setValue(grupo).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Snackbar.make(requireView(), "Grupo creado exitosamente", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(requireView(), "Error al crear el grupo", Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    // Permite desasociar elementos de la Activity (InicioActivity) con el fragment
    override fun onDetach() {
        super.onDetach()
    }
}
