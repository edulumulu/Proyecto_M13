package com.example.proyecto_m13;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    EditText etUsuario, etContrasena;
    Button btIniciar;
    GestionBBDD gestionBBDD = new GestionBBDD();
    ImageView ivState;
    boolean contrasenaVisible = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

            etUsuario = findViewById(R.id.etUsuario);
            etContrasena = findViewById(R.id.etContrasena);
            btIniciar = findViewById(R.id.btIniciar);
            ivState = findViewById(R.id.ivState);

            ivState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (contrasenaVisible) {
                        // Ocultar contraseña
                        etContrasena.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        ivState.setImageResource(R.drawable.invisible);
                    } else {
                        // Mostrar contraseña
                        etContrasena.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        ivState.setImageResource(R.drawable.visible);
                    }
                    // Mueve el cursor al final del texto
                    etContrasena.setSelection(etContrasena.getText().length());
                    contrasenaVisible = !contrasenaVisible;
                }
            });

            btIniciar.setOnClickListener(new View.OnClickListener() {
               /* @Override
                public void onClick(View v) {
                        iniciarSesion();
                }*/

                @Override
                public void onClick(View v) {

                    Test_realizado test = new Test_realizado(
                            1,        // id_test
                            2,      // id_cliente
                            1,      // id_empleado
                            "Positivo" // resultado
                    );
                    Toast.makeText(MainActivity.this, "Botón pulsado", Toast.LENGTH_SHORT).show();


                    gestionBBDD.insertarTestRealizado(MainActivity.this, test, new GestionBBDD.InsertTestCallback() {
                        @Override
                        public void onTestInsertado(String respuesta) {
                            try {
                                JSONObject jsonResponse = new JSONObject(respuesta);
                                if (jsonResponse.getString("estado").equals("correcto")) {
                                    int idInsertado = jsonResponse.getInt("id_insertado");
                                    Toast.makeText(MainActivity.this, "Insertado con ID: " + idInsertado, Toast.LENGTH_LONG).show();
                                } else {
                                    String errorMessage = jsonResponse.getString("mensaje");
                                    Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(MainActivity.this, "Error al procesar la respuesta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
    }

    private void iniciarSesion() {
        final String usuario = etUsuario.getText().toString().trim();
        final String contrasena = etContrasena.getText().toString().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "No puedes dejar ningún campo vacío", Toast.LENGTH_LONG).show();
        } else {
            gestionBBDD.comprobarCredenciales(this, usuario, contrasena);
        }
    }
}