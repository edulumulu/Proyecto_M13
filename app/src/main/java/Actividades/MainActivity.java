package Actividades;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import BBDD.GestionBBDD;
import com.example.proyecto_m13.R;


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

     //Pruebas.pruebas(this);

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

            @Override
            public void onClick(View v) {
                iniciarSesion();

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