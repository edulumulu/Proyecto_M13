package com.example.proyecto_m13;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    EditText etUsuario, etContrasena;
    Button btIniciar;
    GestionBBDD gestionBBDD = new GestionBBDD();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

            etUsuario = findViewById(R.id.etUsuario);
            etContrasena = findViewById(R.id.etContrasena);
            btIniciar = findViewById(R.id.btIniciar);

            btIniciar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   // try {
                        iniciarSesion();
                    //} catch (ParseException e) {
                    //    throw new RuntimeException(e);
                    //}
                }
            });
    }

    private void iniciarSesion() {
        final String usuario = etUsuario.getText().toString().trim();
        final String contrasena = etContrasena.getText().toString().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "No puedes dejar ningún campo vacío", Toast.LENGTH_LONG).show();
        } else {
            //gestionBBDD.eliminarCliente(this, 2);
            gestionBBDD.comprobarCredenciales(this, usuario, contrasena);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            //lo inserto con id 1 aunque internamente va a tener otro id en la base de datos
          /*gestionBBDD.insertarCliente(this, new Cliente("Carlos", "Herrera",
                   "22222222L", dateFormat.parse("06/02/2019"),
                   123456789, "paco@gmail", "Antonio Lucas","Valmo 27", 28047, "Barcelona"));

           /*gestionBBDD.modificarCliente(this,new Cliente(2, "Bibiana", "Martin",
                    "520546668J", dateFormat.parse("03/04/1987"), 123456789, "edu@gmail",
                    "Paco", true, dateFormat.parse("10/05/2019"),
                 "gafas", false, "Illescas 27", 28047, "Madird"));*/

            // Llamar a listarClientes y manejar los datos con un callback
            /*gestionBBDD.listarClientes(this, new GestionBBDD.ClienteCallback() {
                @Override
                public void onClientesListados(ArrayList<Cliente> listaClientes) {
                    if (listaClientes != null && !listaClientes.isEmpty()) {
                        // Imprimir clientes en Log
                        for (Cliente cliente : listaClientes) {
                            Log.d("Cliente", "ID: " + cliente.getId() + ", Nombre: " + cliente.getName());
                        }

                        // Mostrar mensaje con el número de clientes cargados
                        Toast.makeText(MainActivity.this, "Clientes cargados: " + listaClientes.size(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "No hay clientes disponibles", Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
        }
    }
    }