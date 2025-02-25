package com.example.proyecto_m13;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etUsuario, etContrasena;
    Button btIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_land);

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
            /* si rellenan los datos lanzariamos un método que comprueba las credenciales con la BBDD
            gestionBBDD.comprobarCredenciales();
            En este caso abriremos la siguiente actividad para empezar a hacer pruebas
            Y le paso el usuario por si quieres mostrar el nombre arriba a la izquierda*/
            Intent intent = new Intent(MainActivity.this,Ficha_cliente.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
        }
    }
}
