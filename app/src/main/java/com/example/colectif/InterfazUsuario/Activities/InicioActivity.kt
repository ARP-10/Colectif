package com.example.colectif.InterfazUsuario.Activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.colectif.InterfazUsuario.Fragments.CrearGrupoFragment
import com.example.colectif.InterfazUsuario.Fragments.InicioFragment
import com.example.colectif.InterfazUsuario.Fragments.ListaGruposFragment
import com.example.colectif.R
import com.example.colectif.databinding.ActivityInicioBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InicioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicioBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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


    }

    // Menu toolbar de arriba de la pantalla
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }
/*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_logout -> {
                // Aquí puedes colocar el código para cerrar sesión
                return true
            }
            R.id.action_notifications -> {
                // Aquí puedes colocar el código para navegar a la pantalla de notificaciones
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
        }

    }*/
}