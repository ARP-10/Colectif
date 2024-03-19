package com.example.colectif.InterfazUsuario.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.colectif.R
import com.example.colectif.databinding.FragmentCrearGrupoBinding
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
            //nombreGrupo = binding.editNombre.text.toString()
            //plan = binding.editPlan.text.toString()
            //precio = binding.editPrecio.text.toString().toDouble()
            if (nombreGrupo != "" || plan != "" || precio == 0.0) {
                agregarGrupo(nombreGrupo, plan, precio)
            } else {
                Snackbar.make(binding.root, "Rellene todos los campos", Snackbar.LENGTH_SHORT).show()
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
    private fun agregarGrupo(nombre: String, plan: String, precio: Double) {
        val ref = database.getReference("groups")
        val key = ref.push().key ?: ""
        val grupoMap = hashMapOf(
            "nombre" to nombre,
            "plan" to plan,
            "precio" to precio
        )
        // Guardar los datos del grupo en la base de datos bajo la clave única generada
        ref.child(key).setValue(grupoMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Snackbar.make(binding.root, "Grupo creado exitosamente", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, "Error al crear el grupo", Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    // Permite desasociar elementos de la Activity (InicioActivity) con el fragment
    override fun onDetach() {
        super.onDetach()
    }
}
