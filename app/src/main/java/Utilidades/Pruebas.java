package Utilidades;


import android.content.Context;
import android.util.Log;

import com.example.proyecto_m13.Estudio;
import com.example.proyecto_m13.Test_realizado;
import com.example.proyecto_m13.GestionBBDD;

import java.util.ArrayList;

public class Pruebas {

    public static void pruebas (Context context) {
        Log.d("Pruebas", "Ejecutando todas las pruebas...");
        insertarTestDePrueba(context);
        logEstudiosEnTerminal(context);
        // Aquí puedes seguir agregando otras pruebas
    }

    public static void insertarTestDePrueba(Context context) {
        Test_realizado test = new Test_realizado(2, 2, 1, "Aprobado");
        GestionBBDD gestionBBDD = new GestionBBDD();

        gestionBBDD.insertarTestRealizado(context, test, new GestionBBDD.insertarTestCallback() {
            @Override
            public void onInsertarTestCallback(String resultado) {
                Log.d("InsertarTest", "Resultado de la inserción: " + resultado);
            }
        });
    }

    public static void logEstudiosEnTerminal(Context context) {
        GestionBBDD gestionBBDD = new GestionBBDD(); // Crear instancia
        gestionBBDD.listarEstudios(context, new GestionBBDD.ListarEstudiosCallback() {
            @Override
            public void onListarEstudiosCallback(ArrayList<Estudio> estudios) {
                if (estudios != null && !estudios.isEmpty()) {
                    Log.d("ESTUDIOS_DEBUG", "════════════ LISTA DE ESTUDIOS ════════════");
                    for (Estudio estudio : estudios) {
                        Log.d("ESTUDIOS_DEBUG",
                                "ID: " + estudio.getIdEstudio() +
                                        " | Nombre: " + estudio.getNombreEstudio());
                    }
                } else {
                    Log.e("ESTUDIOS_DEBUG", "No se obtuvieron estudios");
                }
            }
        });
    }
}

