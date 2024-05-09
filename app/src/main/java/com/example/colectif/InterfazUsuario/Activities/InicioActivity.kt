package com.example.colectif.InterfazUsuario.Activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.colectif.R
import com.example.colectif.databinding.ActivityInicioBinding
import com.example.colectif.interfaces.SolicitudListener
import com.example.colectif.models.Solicitud
import com.example.colectif.services.VerificarSolicitudesService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InicioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
    private var listaSolicitudes: ArrayList<Solicitud> = ArrayList()
    private lateinit var sharedP: SharedPreferences
    private var sharedPref: String = "com.example.colectif"
    private var haySolicitudes: Boolean = false
    private var primeraVez: Boolean = true
    private var menu: Menu? = null


    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val flag = intent?.getBooleanExtra("haySolicitudes", false) ?: false
            cambiarHaySolicitudes(flag)
            onSolicitudesActualizadas()
            if(!flag){
                cambiarHaySolicitudes(flag)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = IntentFilter("com.example.myapplication.ACTUALIZACION_SOLICITUDES")
        registerReceiver(broadcastReceiver, filter)
        startService(Intent(this, VerificarSolicitudesService::class.java))
        sharedP = getSharedPreferences(sharedPref, Context.MODE_PRIVATE)
        primeraVez = sharedP.getBoolean("primeraVez", true)




        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        bottomNavigationView = binding.navMenu
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(
            bottomNavigationView,navController
        )

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemFragmentInicio -> {

                    navController.navigate(R.id.inicioFragment)
                    true
                }

                R.id.itemFragmentGrupos -> {
                    navController.navigate(R.id.listaGruposFragment)
                    true
                }

                R.id.itemFragmentCrearGrupo -> {
                    navController.navigate(R.id.crearGrupoFragment)
                    true
                }

                else -> false
            }

        }
        recogerListaSolicitudes()
        if(primeraVez) {
            mostrarMensaje(this, "Advertencia", "Por favor ten en cuenta que al unirte a grupos en esta aplicación, eres completamente responsable de cualquier interacción, acción o consecuencia que pueda surgir dentro de esos grupos. Los creadores de la aplicación no asumen ninguna responsabilidad por las actividades que ocurran en los grupos.\n" +
                    "\n" +
                    "Te recomendamos encarecidamente que actúes de manera responsable y respetuosa en todos tus intercambios dentro de la aplicación.")
        }

    }

    private fun comprobarVacio() {
        if(listaSolicitudes.isEmpty()){
            haySolicitudes = false
        } else {
            haySolicitudes = true
        }
    }


    // Menu toolbar de arriba de la pantalla
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        onSolicitudesActualizadas()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_logout -> {
                auth.signOut()
                val sharedP: SharedPreferences
                sharedP = this.getSharedPreferences("com.example.colectif",Context.MODE_PRIVATE)
                val editor = sharedP.edit()
                editor.putBoolean("estado",false)
                editor.apply()
                startActivity(Intent(this, LoginActivity::class.java))
                this.finish()
                return true
            }
            R.id.action_notifications -> {
                navController.navigate(R.id.action_global_solicitudesFragment)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }


    }

    fun onSolicitudesActualizadas() {
        val menuItem = menu?.findItem(R.id.action_notifications)
        if(menuItem != null) {
            haySolicitudes = !listaSolicitudes.isEmpty() // Actualizar el estado de las solicitudes
            if (haySolicitudes) {
                menuItem.setIcon(R.drawable.campana_notificacion)
                invalidateOptionsMenu()
            } else {
                menuItem.setIcon(R.drawable.campana)
                invalidateOptionsMenu()
            }
        }
    }


    fun recogerListaSolicitudes(){
        var ref = database.getReference("users")
        var ref2 = database.getReference("solicitudes")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listaSolicitudes.clear()
                for(i in 1 until snapshot.child(auth.currentUser!!.uid).child("numSolicitudes").value.toString().toInt() + 1){
                    val idSolicitud = snapshot.child(auth.currentUser!!.uid).child("solicitudes").child(i.toString()).value.toString()
                    ref2.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // Hacer que no aparezcan los nulos / borrados de la bbdd
                            if (snapshot.child(idSolicitud).child("idGrupo").value.toString() != "null" && snapshot.child(idSolicitud).child("idMandatario").value.toString() != "null" && snapshot.child(idSolicitud).child("idReceptor").value.toString() != "null") {
                                listaSolicitudes.add(
                                    Solicitud(
                                        snapshot.child(idSolicitud).child("id").value.toString(),
                                        snapshot.child(idSolicitud).child("idReceptor").value.toString(),
                                        snapshot.child(idSolicitud).child("idMandatario").value.toString(),
                                        snapshot.child(idSolicitud).child("idGrupo").value.toString()
                                    ))
                                comprobarVacio()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun cambiarHaySolicitudes(flag : Boolean){
        haySolicitudes = flag
    }

    fun mostrarMensaje(contexto: Context, titulo: String, mensaje: String) {
        val sharedPreferences = contexto.getSharedPreferences(sharedPref, Context.MODE_PRIVATE)
        val noMostrarAdvertencia = sharedPreferences.getBoolean("noMostrarAdvertencia", false)

        // Verificar si se debe mostrar el mensaje
        if (!noMostrarAdvertencia) {
            val builder = AlertDialog.Builder(contexto)
            builder.setTitle(titulo)
            builder.setMessage(mensaje)

            builder.setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }

            builder.setNegativeButton("No volver a mostrar") { dialog, _ ->
                dialog.dismiss()
                // Guardar la preferencia del usuario
                val editor = sharedPreferences.edit()
                editor.putBoolean("noMostrarAdvertencia", true)
                editor.apply()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }






}