package com.example.proyecto_m13;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Ficha_cliente extends AppCompatActivity {

    private static ArrayList<Cliente> lista_clientes = new ArrayList<>();

    //Variables parte izquierda de la app
    private TextView tv_user, title_seleciona, title_acciones;
    private Spinner sp_clientes;
    private Button bt_insert, bt_delete, bt_update, bt_test;
    private ImageButton ib_exit;

    //Variables parte derecha

    private TextView tv_id, title_datos_person, title_dni, title_age, title_tlf, title_tutor, title_mail,title_direc, title_street, title_cp, title_city, title_purebas, title_graduacion, title_fecha_gradu, title_tipo_lente, title_test_TVPS, title_date_test, title_next_date_test;
    private EditText et_dni, et_age, et_tlf_et_mail, et_tutor, et_name, et_mail, et_street, et_cp, et_city, et_graduacion, et_fecha_gradu, et_tipo_lente, et_test_tvps, et_fecha_test_TVPS, et_next_text;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ficha_cliente);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializar_componentes();
        campos_ficha_visibilidad(false);



    }
    public void inicializar_componentes(){
        //Inicializo los componentes parte izquierda

        tv_user = findViewById(R.id.tv_user);
        title_seleciona = findViewById(R.id.textview_10);
        title_acciones = findViewById(R.id.textview_11);
        bt_insert = findViewById(R.id.bt_insert);
        bt_update = findViewById(R.id.bt_update);
        bt_delete = findViewById(R.id.bt_delete);
        bt_test = findViewById(R.id.bt_test);
        sp_clientes = findViewById(R.id.sp_clientes);

        //Inicializo componentes de la derecha
        title_datos_person = findViewById(R.id.textView);
        title_dni= findViewById(R.id.textView_2);
        title_age= findViewById(R.id.textView_1);
        title_tlf= findViewById(R.id.textView_3);
        title_tutor= findViewById(R.id.textView_4);
        title_mail= findViewById(R.id.textView_5);
        title_direc= findViewById(R.id.textView_6);
        title_street= findViewById(R.id.textView_7);
        title_cp= findViewById(R.id.textView_8);
        title_city= findViewById(R.id.textView_9);
        title_purebas= findViewById(R.id.textView_12);
        title_graduacion= findViewById(R.id.textView_13);
        title_fecha_gradu= findViewById(R.id.textView_14);
        title_tipo_lente= findViewById(R.id.textView_15);
        title_test_TVPS= findViewById(R.id.textView_16);
        title_date_test= findViewById(R.id.textView_17);
        title_next_date_test= findViewById(R.id.textView_18);

        tv_id= findViewById(R.id.tv_id);
        et_dni = findViewById(R.id.et_dni);
        et_age= findViewById(R.id.et_age);
        et_tlf_et_mail= findViewById(R.id.et_mail);
        et_tutor= findViewById(R.id.et_tutor);
        et_name= findViewById(R.id.et_name);
        et_mail= findViewById(R.id.et_mail);
        et_street= findViewById(R.id.et_street);
        et_cp= findViewById(R.id.et_cp);
        et_city= findViewById(R.id.et_city);
        et_graduacion= findViewById(R.id.et_graduado);
        et_fecha_gradu= findViewById(R.id.et_fecha_graduacion);
        et_tipo_lente= findViewById(R.id.et_tipo_lente);
        et_test_tvps= findViewById(R.id.et_testsiono);
        et_fecha_test_TVPS= findViewById(R.id.et_fecha_TVPS);
        et_next_text= findViewById(R.id.et_fecha_proxTVPS);

    }

    public void campos_ficha_visibilidad(Boolean mostrar){
        int visibility = mostrar ? View.VISIBLE : View.GONE;

        tv_id.setVisibility(visibility);
        et_dni.setVisibility(visibility);
        et_age.setVisibility(visibility);
        et_tlf_et_mail.setVisibility(visibility);
        et_tutor.setVisibility(visibility);
        et_name.setVisibility(visibility);
        et_mail.setVisibility(visibility);
        et_street.setVisibility(visibility);
        et_cp.setVisibility(visibility);
        et_city.setVisibility(visibility);
        et_graduacion.setVisibility(visibility);
        et_fecha_gradu.setVisibility(visibility);
        et_tipo_lente.setVisibility(visibility);
        et_test_tvps.setVisibility(visibility);
        et_fecha_test_TVPS.setVisibility(visibility);
        et_next_text.setVisibility(visibility);
    }
}