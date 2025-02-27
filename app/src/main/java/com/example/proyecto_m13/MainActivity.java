package com.example.proyecto_m13;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


public class MainActivity extends AppCompatActivity {

    EditText etUsuario, etContrasena;
    Button btIniciar;
    GestionBBDD gestionBBDD = new GestionBBDD();
    RecyclerView recyclerClientes;

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

                    iniciarSesion();
                }
            });
    }

    private void iniciarSesion(){
        final String usuario = etUsuario.getText().toString().trim();
        final String contrasena = etContrasena.getText().toString().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()){
            Toast.makeText(this, "No puedes dejar ningún campo vacío", Toast.LENGTH_LONG).show();
        }else {
            //gestionBBDD.listarClientes(this,this);
            //gestionBBDD.eliminarCliente(this, 2);
            //gestionBBDD.comprobarCredenciales(this, usuario, contrasena);
        }
    }
}
