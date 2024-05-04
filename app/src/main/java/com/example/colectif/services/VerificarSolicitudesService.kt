package com.example.colectif.services
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.example.colectif.models.Solicitud
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class VerificarSolicitudesService : Service() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val handler = Handler()
    private val INTERVALO_DE_VERIFICACION = 6000L // Intervalo de verificación en milisegundos (1 minuto)


    private val verificarSolicitudesTask = object : Runnable {
        override fun run() {
            var ref = database.getReference("users")
            var ref2 = database.getReference("solicitudes")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var listaSolicitudes: ArrayList<Solicitud> = ArrayList<Solicitud>()
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
                                        )
                                    )


                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                    }

                    val intent = Intent("com.example.myapplication.ACTUALIZACION_SOLICITUDES")
                    intent.putExtra("haySolicitudes", listaSolicitudes.isNotEmpty())
                    sendBroadcast(intent)
                    if(listaSolicitudes.isEmpty()){
                        Log.v("pruebecita2", "es nulo confirmamos")
                    }
                    Log.v("pruebecita", listaSolicitudes.isNotEmpty().toString())
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            handler.postDelayed(this, INTERVALO_DE_VERIFICACION)
        }
    }

    override fun onCreate() {
        super.onCreate()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Iniciar la verificación de las solicitudes al iniciar el servicio
        handler.post(verificarSolicitudesTask)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener la verificación de las solicitudes al destruir el servicio
        handler.removeCallbacks(verificarSolicitudesTask)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}