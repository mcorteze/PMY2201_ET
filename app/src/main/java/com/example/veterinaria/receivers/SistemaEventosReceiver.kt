package com.example.veterinaria.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.veterinaria.R
import com.example.veterinaria.services.SincronizacionService

// Receptor de eventos del sistema para manejar alertas y estados
class SistemaEventosReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "SistemaEventosReceiver"
        private const val CHANNEL_ID = "VeterinariaEventos"
        private const val NOTIFICATION_ID = 2001

        // Definicion de acciones propias
        const val ACTION_RECORDATORIO_CITA = "com.example.veterinaria.RECORDATORIO_CITA"
        const val ACTION_SINCRONIZAR_DATOS = "com.example.veterinaria.SINCRONIZAR_DATOS"
    }

    // Metodo principal que recibe los eventos
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val accion = intent.action
        Log.d(TAG, "Broadcast recibido: $accion")

        when (accion) {
            // Se ejecuto al encender el dispositivo
            Intent.ACTION_BOOT_COMPLETED -> {
                manejarInicioDispositivo(context)
            }

            // Hubo un cambio en la conexion de red
            ConnectivityManager.CONNECTIVITY_ACTION,
            "android.net.conn.CONNECTIVITY_CHANGE" -> {
                manejarCambioConectividad(context)
            }

            // La bateria esta baja
            Intent.ACTION_BATTERY_LOW -> {
                manejarBateriaBaja(context)
            }

            // La bateria se recupero
            Intent.ACTION_BATTERY_OKAY -> {
                manejarBateriaOK(context)
            }

            // Cargador conectado
            Intent.ACTION_POWER_CONNECTED -> {
                manejarConexionEnergia(context)
            }

            // Cargador desconectado
            Intent.ACTION_POWER_DISCONNECTED -> {
                manejarDesconexionEnergia(context)
            }

            // Cambio estado modo avion
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                manejarCambioModoAvion(context, intent)
            }

            // Evento de recordatorio de cita
            ACTION_RECORDATORIO_CITA -> {
                manejarRecordatorioCita(context, intent)
            }

            ACTION_SINCRONIZAR_DATOS -> {
                manejarSincronizacionDatos(context)
            }

            else -> {
                Log.d(TAG, "Acción no manejada: $accion")
            }
        }
    }

    // Configura servicios al arrancar el telefono
    private fun manejarInicioDispositivo(context: Context) {
        Log.d(TAG, "Dispositivo iniciado - Configurando servicios")

        // Iniciamos servicio de sincronizacion
        val intent = Intent(context, SincronizacionService::class.java).apply {
            action = SincronizacionService.ACTION_INICIAR_SYNC
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        mostrarNotificacion(
            context,
            "Veterinaria iniciada",
            "Los servicios de la aplicación están activos"
        )
    }

    // Reacciona a cambios de internet
    private fun manejarCambioConectividad(context: Context) {
        val conectado = verificarConectividad(context)

        if (conectado) {
            Log.d(TAG, "Conexión a Internet disponible")

            // Si hay internet sincronizamos datos
            val intent = Intent(context, SincronizacionService::class.java).apply {
                action = SincronizacionService.ACTION_INICIAR_SYNC
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }

            mostrarNotificacion(
                context,
                "Conexión restaurada",
                "Sincronizando datos de la veterinaria"
            )
        } else {
            Log.d(TAG, "Sin conexión a Internet")
            mostrarNotificacion(
                context,
                "Sin conexión",
                "Trabajando en modo offline"
            )
        }
    }

    // Ahorro de energia si la bateria baja
    private fun manejarBateriaBaja(context: Context) {
        Log.d(TAG, "Batería baja detectada - Pausando servicios no críticos")

        // Detenemos sincronizacion para ahorrar
        val intent = Intent(context, SincronizacionService::class.java).apply {
            action = SincronizacionService.ACTION_DETENER_SYNC
        }
        context.startService(intent)

        mostrarNotificacion(
            context,
            "Batería baja",
            "Servicios pausados para ahorrar energía"
        )
    }

    // Restauramos servicios si la bateria esta bien
    private fun manejarBateriaOK(context: Context) {
        Log.d(TAG, "Batería recuperada - Reanudando servicios")

        // Reiniciamos servicios
        val intent = Intent(context, SincronizacionService::class.java).apply {
            action = SincronizacionService.ACTION_INICIAR_SYNC
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    // Acciones al conectar el cargador
    private fun manejarConexionEnergia(context: Context) {
        Log.d(TAG, "Dispositivo conectado a la corriente")

        mostrarNotificacion(
            context,
            "Dispositivo en carga",
            "Optimizando datos de la veterinaria"
        )
    }

    // Acciones al desconectar cargador
    private fun manejarDesconexionEnergia(context: Context) {
        Log.d(TAG, "Dispositivo desconectado de la corriente")
    }

    // Notificar modo avion
    private fun manejarCambioModoAvion(context: Context, intent: Intent) {
        val modoAvionActivado = intent.getBooleanExtra("state", false)

        if (modoAvionActivado) {
            Log.d(TAG, "Modo avión activado")
            mostrarNotificacion(
                context,
                "Modo avión activado",
                "Trabajando en modo offline"
            )
        } else {
            Log.d(TAG, "Modo avión desactivado")
        }
    }

    // Mostrar recordatorio de cita agendada
    private fun manejarRecordatorioCita(context: Context, intent: Intent) {
        val nombreMascota = intent.getStringExtra("NOMBRE_MASCOTA") ?: "Mascota"
        val horaCita = intent.getStringExtra("HORA_CITA") ?: "Pronto"

        Log.d(TAG, "Recordatorio de cita para: $nombreMascota")

        mostrarNotificacion(
            context,
            "Recordatorio de Cita",
            "Cita de $nombreMascota a las $horaCita"
        )
    }

    // Forzar sincronizacion manual
    private fun manejarSincronizacionDatos(context: Context) {
        Log.d(TAG, "Iniciando sincronización manual de datos")

        val intent = Intent(context, SincronizacionService::class.java).apply {
            action = SincronizacionService.ACTION_INICIAR_SYNC
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    // Revisa si hay red disponible
    private fun verificarConectividad(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            networkInfo?.isConnected == true
        }
    }

    // Crea y muestra una notificacion
    private fun mostrarNotificacion(context: Context, titulo: String, mensaje: String) {
        crearCanalNotificacion(context)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificacion = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notificacion)
    }

    // Crea el canal necesario para notificaciones en Android moderno
    private fun crearCanalNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Eventos del Sistema"
            val descripcion = "Notificaciones de eventos del sistema"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT

            val canal = NotificationChannel(CHANNEL_ID, nombre, importancia).apply {
                description = descripcion
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }
}
