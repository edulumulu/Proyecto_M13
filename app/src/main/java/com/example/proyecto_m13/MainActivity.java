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

                    try {
                        iniciarSesion();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
    }

    private void iniciarSesion() throws ParseException {
        final String usuario = etUsuario.getText().toString().trim();
        final String contrasena = etContrasena.getText().toString().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()){
            Toast.makeText(this, "No puedes dejar ningún campo vacío", Toast.LENGTH_LONG).show();
        }else {
            //gestionBBDD.listarClientes(this,this);
            //gestionBBDD.eliminarCliente(this, 2);
            //gestionBBDD.comprobarCredenciales(this, usuario, contrasena);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            //lo inserto con id 1 aunque internamente va a tener otro id en la base de datos
          /* gestionBBDD.insertarCliente(this, new Cliente(1,"Carlos", "Herrera",
                   "520546567L", dateFormat.parse("06/02/2019"),
                   123456789, "paco@gmail", "Antonio Lucas", false, null,
                   null, false, "Valmo 27", 28047, "Barcelona")); y

           /*gestionBBDD.modificarCliente(this,new Cliente(2, "Bibiana", "Martin",
                    "520546668J", dateFormat.parse("03/04/1987"), 123456789, "edu@gmail",
                    "Paco", true, dateFormat.parse("10/05/2019"),
                 "gafas", false, "Illescas 27", 28047, "Madird"));*/
        }
    }
}
