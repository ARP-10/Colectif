package com.example.colectif.InterfazUsuario.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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

        // Comprobar si todos los campos están rellenos
        binding.btnCrearGrupo.setOnClickListener {
            nombreGrupo = binding.editNombre.text.toString()
            plan = binding.editPlan.text.toString()
            precio = binding.editPrecio.text.toString().toDouble()
            if (nombreGrupo != "" || plan != "" || precio == 0.0) {
                agregarGrupo(nombreGrupo, plan, precio)
            } else {
                Snackbar.make(binding.root, "Rellene todos los campos", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Configuración spinner
        binding.spinnerApps.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                when (position) {
                    0 -> binding.imgGrupo.setImageResource(R.drawable.netflix)
                    1 -> binding.imgGrupo.setImageResource(R.drawable.spotify)
                    2 -> binding.imgGrupo.setImageResource(R.drawable.amazon)
                    3 -> binding.imgGrupo.setImageResource(R.drawable.disney)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


    }

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