package com.example.colectif.services
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.example.colectif.models.Solicitud
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/**
 * Servicio que verifica las solicitudes de los usuarios en intervalos regulares y envía una difusión
 * cuando se detectan nuevas solicitudes.
 */
class VerificarSolicitudesService : Service() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val handler = Handler()
    private val INTERVALO_DE_VERIFICACION = 6000L // Intervalo de verificación en milisegundos


    // Tarea para verificar las solicitudes
    private val verificarSolicitudesTask = object : Runnable {
        override fun run() {
            var ref = database.getReference("users")
            var ref2 = database.getReference("solicitudes")

            // Escuchar cambios en los datos de los usuarios
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var listaSolicitudes: ArrayList<Solicitud> = ArrayList()

                    // Iterar sobre las solicitudes del usuario actual
                    for(i in 1 until snapshot.child(auth.currentUser!!.uid).child("numSolicitudes").value.toString().toInt() + 1){
                        val idSolicitud = snapshot.child(auth.currentUser!!.uid).child("solicitudes").child(i.toString()).value.toString()
                        ref2.addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                // Hacer que no aparezcan los nulos / borrados de la bbdd
                                if (snapshot.child(idSolicitud).child("idGrupo").value.toString() != "null" && snapshot.child(idSolicitud).child("idMandatario").value.toString() != "null" && snapshot.child(idSolicitud).child("idReceptor").value.toString() != "null") {

                                    // Agregar la solicitud a la lista
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

                    // Enviar una difusión con la información de las solicitudes
                    val intent = Intent("com.example.myapplication.ACTUALIZACION_SOLICITUDES")
                    intent.putExtra("haySolicitudes", listaSolicitudes.isNotEmpty())
                    sendBroadcast(intent)
                    if(listaSolicitudes.isEmpty()){
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

            // Programar la próxima ejecución de la tarea
            handler.postDelayed(this, INTERVALO_DE_VERIFICACION)
        }
    }

    // Método de inicialización del servicio
    override fun onCreate() {
        super.onCreate()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://colectif-project-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    // Método llamado cuando el servicio se inicia
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Iniciar la verificación de las solicitudes al iniciar el servicio
        handler.post(verificarSolicitudesTask)

        // El servicio seguirá funcionando aunque la aplicación se cierre
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener la verificación de las solicitudes al destruir el servicio
        handler.removeCallbacks(verificarSolicitudesTask)
    }

    // Método llamado cuando se vincula el servicio con un componente
    override fun onBind(intent: Intent?): IBinder? {

        // No se permite la vinculación con este servicio
        return null
    }
}