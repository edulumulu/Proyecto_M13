package com.example.proyecto_m13;

import static Utilidades.Utilidades.desactivar_activar_Botones;
import static Utilidades.Utilidades.eliminar_Cliente_PorId;
import static Utilidades.Utilidades.fecha_valida;
import static Utilidades.Utilidades.nombreYDniNoRepetidos;
import static Utilidades.Utilidades.obtener_cliente_por_id;
import static Utilidades.Utilidades.visibilidad_EditTest;
import static Utilidades.Utilidades.visibilidad_Textviews;
import static Utilidades.Utilidades.visibilidad_botones;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import Utilidades.Utilidades;


public class Ficha_cliente extends AppCompatActivity {

    private static ArrayList<Cliente> lista_clientes = new ArrayList<>();
    private Test_realizado datos_test_realizado = new Test_realizado();
    GestionBBDD gestionBBDD = new GestionBBDD();
    private String nombre_empleado;
    private int cliente_selecionado_id;
    private Date fecha_nacimiento_Seleccionada;
    private boolean cliente_selecionado = false;

    private int id_desde_Actividad_test;

    //Variables parte izquierda de la app
    private TextView tv_user, title_seleciona, title_acciones;
    private AutoCompleteTextView buscar_clientes;
    private Button bt_insert, bt_delete, bt_update, bt_test;
    private Button bt_modificar_aceptar, bt_modificar_salir, bt_insertar_aceptar, bt_insertar_salir;
    private Button bt_result;
    private ImageButton ib_exit;
    private ImageView iv_foto;

    //Variables parte derecha
    private TextView tv_id, title_id, title_datos_person, title_dni, title_age, title_tlf, title_tutor, title_mail, title_direc, title_street, title_cp, title_city, title_purebas, title_graduacion, title_fecha_gradu, title_tipo_lente, title_test_TVPS, title_next_date_test;
    private EditText et_dni, et_age, et_tlf, et_tutor, et_name, et_surname, et_mail, et_street, et_cp, et_city;
    private TextView  tv_fecha_gradu, tv_tipo_lente,  tv_next_text; //tv_test_tvps, tv_fecha_test_TVPS,tv_graduacion,


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ficha_cliente);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //relacionar_variables_front_back();
        inicializar_componentes();
        poner_nombre_Empleado();

        if (!buscar_clientes.isSelected()) {
            campos_ficha_visibilidad(false);
        }


        id_desde_Actividad_test = getIntent().getIntExtra("idCliente", -1);

        if (id_desde_Actividad_test > 0){

            cargar_array_list_BBDD();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                cliente_selecionado_id = id_desde_Actividad_test;
                for(Cliente cli : lista_clientes){
                    if(cli.getId() == cliente_selecionado_id){
                        actualizar_nombres_buscador(lista_clientes, buscar_clientes);
                        cargar_cliente_en_ficha(cli);

                    }
                }
            }, 2000); // 5000 milisegundos = 5 segundos

        }else{
            cargar_array_list_BBDD();
        }




        /**
         * Borrar testo del buscador cuando se hace click
         */
        buscar_clientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscar_clientes.setText("");
            }
        });

        /*
        Abre la ficha del cliente cuando se clicak sobre su nombre
         */
        buscar_clientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String eleccion = (String) adapterView.getItemAtPosition(i);
                for (Cliente cli : lista_clientes) {
                    if (eleccion.equalsIgnoreCase(cli.getName() + " " + cli.getSurname())) {
                        cliente_selecionado = true;
                        cliente_selecionado_id = cli.getId();
                        Log.d("Cliente seleccionado", "ID: " + cliente_selecionado_id);

                        //Hago visible los campos de la ficha y cargo los datos
                        cargar_cliente_en_ficha(cli);
                        //Los campos de los datos del cliente no se pueden editar
                        campos_ficha_editables(false);
                        //activo botones restantes
                        desactivar_activar_Botones(true, new Button[]{bt_update, bt_delete, bt_test});
                        //Invisibilizo los botones de manipular modificar y de insertar
                        visibilidad_botones(false, new Button[]{bt_modificar_salir, bt_modificar_aceptar, bt_insertar_aceptar, bt_insertar_salir});
                        break;
                    }

                }
            }
        });


        //BOTONES RELACIONADOS CON LA INSERCIÓN DE CLIENTES
        bt_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                desactivar_activar_Botones(false, new Button[]{bt_insert, bt_delete, bt_test, bt_update});

                bt_result.setVisibility(View.GONE);
                //Vista del formulario editable
                preparar_formulario_insert();
                //Aparicon de botones de interacción
                visibilidad_botones(true, new Button[]{bt_insertar_aceptar, bt_insertar_salir});
                et_tutor.setEnabled(false);

                //Comprobar que cuando se selecciona fecha si es menor de edad aparece el campo tutor
                et_age.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // No es necesario hacer nada aquí
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // No es necesario hacer nada aquí
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String textoFecha = et_age.getText().toString();
                        if (!textoFecha.isEmpty()) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                fecha_nacimiento_Seleccionada = sdf.parse(textoFecha);  // Convertir el texto en un objeto Date

                                if (fecha_nacimiento_Seleccionada != null && fecha_valida(fecha_nacimiento_Seleccionada)) {
                                    if (Utilidades.esMayorDeEdad(fecha_nacimiento_Seleccionada)) {
                                        title_tutor.setVisibility(View.GONE);
                                        et_tutor.setVisibility(View.GONE);
                                        et_tutor.setText(null);
                                        et_tutor.setEnabled(false);  // Deshabilitar campo tutor si es mayor de edad
                                        Log.d("Fecha", "Es mayor de edad, se deshabilita el campo tutor");
                                    } else {
                                        title_tutor.setVisibility(View.VISIBLE);
                                        et_tutor.setVisibility(View.VISIBLE);
                                        et_tutor.setEnabled(true);  // Habilitar campo tutor si es menor de edad
                                        Log.d("Fecha", "Es menor de edad, se habilita el campo tutor");
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                //Toast.makeText(Ficha_cliente.this, "El formato de la fecha es incorrecto", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });


            }
        });

        bt_insertar_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Verificar si el nombre y apellido ya están registrados en la base de datos
                for (Cliente cli : lista_clientes) {
                    if (et_name.getText().toString().equalsIgnoreCase(cli.getName()) &&
                            et_surname.getText().toString().equalsIgnoreCase(cli.getSurname())) {
                        Toast.makeText(Ficha_cliente.this, "El usuario con ese nombre y apellido ya está registrado", Toast.LENGTH_SHORT).show();
                    }
                    if (et_dni.getText().toString().equalsIgnoreCase(cli.getDni())) {
                        Toast.makeText(Ficha_cliente.this, "El usuario con ese DNI ya está registrado", Toast.LENGTH_SHORT).show();
                    }
                }

                //Comprobacion general de los campos rellenos
                if (!comprobación_de_relleno_formulario()) {
                    return;
                }

                //Si es menor de edad comprueba que se rellene el campo tutor
                if (!Utilidades.esMayorDeEdad(fecha_nacimiento_Seleccionada) &&
                        (et_tutor.getText().toString().trim().isEmpty() || et_tutor.getText().toString().equalsIgnoreCase("Si es menor de edad"))) {
                    Toast.makeText(Ficha_cliente.this, "Al ser menor debes incluir un tutor", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Si es mayor de edad comprueba que el tutor esté vacío
                if (Utilidades.esMayorDeEdad(fecha_nacimiento_Seleccionada)) {
                    et_tutor.setHint(null);
                    et_tutor.setText("");
                }

                //recopilación del contenido de los campos
                String nombre = et_name.getText().toString();
                String surname = et_surname.getText().toString();
                String dni = et_dni.getText().toString();
                int tlf = Integer.parseInt(et_tlf.getText().toString());
                String email = et_mail.getText().toString();
                String street = et_street.getText().toString();
                int cp = Integer.parseInt(et_cp.getText().toString());
                String city = et_city.getText().toString();
                String tutor = et_tutor.getText().toString();
                String tipo = "sin datos";
                int id =0;
                for(Cliente cli : lista_clientes){
                    id = cli.getId()+1;
                }

                Cliente cliente_Array = new Cliente(nombre,
                        surname, dni, fecha_nacimiento_Seleccionada, tlf, email,
                        tutor, street, cp, city, tipo);
                Cliente cliente= new Cliente(id ,nombre,
                        surname, dni, fecha_nacimiento_Seleccionada, tlf, email,
                        tutor, street, cp, city, tipo);
                
                if (lista_clientes.add(cliente)){
                    insertar_cliente_aqui_BBDD(cliente_Array);

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        actualizar_nombres_buscador(lista_clientes, buscar_clientes);

                        cargar_cliente_en_ficha(obtener_cliente_por_id(cliente_selecionado_id, lista_clientes));
                        visibilidad_botones(false, new Button[]{bt_insertar_aceptar, bt_insertar_salir});
                        campos_ficha_editables(false);

                        //Visibilizo elementos
                        //visibilidad_Textviews(true, new TextView[]{title_purebas, title_tutor} );
                        iv_foto.setVisibility(View.VISIBLE);
                        et_tutor.setVisibility(View.VISIBLE);

                        visibilidad_Textviews(false, new TextView[]{tv_id, title_id});
                            title_purebas.setVisibility(View.VISIBLE);

                        title_age.setText("Edad:");
                        desactivar_activar_Botones(true, new Button[]{bt_insert, bt_delete, bt_test, bt_update});
                        }, 3000); // 5000 milisegundos = 5 segundos

                }


            }
        });

        bt_insertar_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title_age.setText("Edad");
                campos_ficha_visibilidad(false);
                visibilidad_botones(false, new Button[]{bt_insertar_salir, bt_insertar_aceptar});

                if (cliente_selecionado) {
                    desactivar_activar_Botones(true, new Button[]{bt_delete, bt_test, bt_update, bt_insert});
                } else {
                    desactivar_activar_Botones(false, new Button[]{bt_delete, bt_test, bt_update});
                    bt_insert.setEnabled(true);
                }

            }
        });


        //BOTONES RELACIONADOS CON LA MODIFICACIÓN DE CLIENTES
        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Desactivo botones hasta quwe se resuelva accion
                desactivar_activar_Botones(false, new Button[]{bt_insert, bt_delete, bt_test, bt_update});
                bt_result.setVisibility(View.GONE);
                visibilidad_Textviews(false, new TextView[]{title_graduacion, title_fecha_gradu, title_tipo_lente, tv_fecha_gradu, tv_tipo_lente,title_test_TVPS, title_next_date_test, tv_next_text, title_purebas});

                if (tv_id.getText().toString().isEmpty()) {
                    Toast.makeText(Ficha_cliente.this, "Debes selecionar un cliente previamente", Toast.LENGTH_SHORT).show();
                    return;

                }
                visibilidad_botones(true, new Button[]{bt_modificar_aceptar, bt_modificar_salir});
                iv_foto.setVisibility(View.GONE);
                campos_ficha_editables(true);
                title_age.setText("Fecha de nacimiento:");
                //Aquí quiero que el piker pueda ser usado

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date fechaNacimiento = obtener_cliente_por_id(cliente_selecionado_id, lista_clientes).getDate_born();
                et_age.setText(sdf.format(fechaNacimiento));

                et_age.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Mostrar el DatePicker y obtener la fecha seleccionada
                        mostrarDatePicker(new DatePickerCallback() {
                            @Override
                            public void onDateSelected(Date selectedDate) {
                                // Guardar la fecha seleccionada en la variable
                                fecha_nacimiento_Seleccionada = selectedDate;

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                et_age.setText(sdf.format(fecha_nacimiento_Seleccionada));
                            }
                        });
                    }
                });

                et_age.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // No es necesario hacer nada aquí
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // No es necesario hacer nada aquí
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String textoFecha = et_age.getText().toString();
                        if (!textoFecha.isEmpty()) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                fecha_nacimiento_Seleccionada = sdf.parse(textoFecha);  // Convertir el texto en un objeto Date

                                if (fecha_nacimiento_Seleccionada != null) {

                                    if (Utilidades.esMayorDeEdad(fecha_nacimiento_Seleccionada)) {
                                        title_tutor.setVisibility(View.GONE);
                                        et_tutor.setVisibility(View.GONE);
                                        et_tutor.setText(null);
                                        et_tutor.setEnabled(false);  // Deshabilitar campo tutor si es mayor de edad
                                        Log.d("Fecha", "Es mayor de edad, se deshabilita el campo tutor");
                                    } else {
                                        title_tutor.setVisibility(View.VISIBLE);
                                        et_tutor.setVisibility(View.VISIBLE);
                                        et_tutor.setEnabled(true);  // Habilitar campo tutor si es menor de edad
                                        Log.d("Fecha", "Es menor de edad, se habilita el campo tutor");
                                    }

                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                //Toast.makeText(Ficha_cliente.this, "El formato de la fecha es incorrecto", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        bt_modificar_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Verificar comprobaciones genrales de ingreso de datos
                if (!comprobación_de_relleno_formulario()) {
                    return;
                }

                //Comprobar que los nombres no existen ya en otros clientes
                if (!nombreYDniNoRepetidos(cliente_selecionado_id, et_name.getText().toString(), et_surname.getText().toString(), et_dni.getText().toString(), lista_clientes)) {
                    Toast.makeText(Ficha_cliente.this, "Nombre o DNI ya exstentes en la base de datos", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Si no es mayor de edad aparece campro tuor
                if (!Utilidades.esMayorDeEdad(fecha_nacimiento_Seleccionada) &&
                        (et_tutor.getText().toString().trim().isEmpty() || et_tutor.getText().toString().equalsIgnoreCase(""))) {
                    Toast.makeText(Ficha_cliente.this, "Es menor de edad, debes incluir un tutor", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Si no lo es se modifica el contenido del editTest
                if (Utilidades.esMayorDeEdad(fecha_nacimiento_Seleccionada)) {
                    et_tutor.setText("");
                }


                //Recogemos contenido de los editTest
                String nombre = et_name.getText().toString();
                String surname = et_surname.getText().toString();
                String dni = et_dni.getText().toString();
                int tlf = Integer.parseInt(et_tlf.getText().toString());
                String email = et_mail.getText().toString();
                String street = et_street.getText().toString();
                int cp = Integer.parseInt(et_cp.getText().toString());
                String city = et_city.getText().toString();
                String tutor = et_tutor.getText().toString();


                Cliente cli = new Cliente(cliente_selecionado_id, nombre, surname, dni, fecha_nacimiento_Seleccionada, tlf, email, tutor, cp, city, street);
                //Toast.makeText(Ficha_cliente.this, "El id del cliente es : "+cli.getId(), Toast.LENGTH_LONG).show();

                    modificar_cliente_aqui_BBDD(cli);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            cargar_array_list_BBDD();
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            actualizar_nombres_buscador(lista_clientes, buscar_clientes);
                            cargar_cliente_en_ficha(obtener_cliente_por_id(cliente_selecionado_id, lista_clientes));
                            campos_ficha_editables(false);
                            visibilidad_botones(false, new Button[]{bt_modificar_aceptar, bt_modificar_salir});

                            visibilidad_Textviews(true,  new TextView[]{title_purebas, title_tutor});

                            iv_foto.setVisibility(View.VISIBLE);
                            //et_tutor.setVisibility(View.VISIBLE);
                            title_age.setText("Edad:");

                            et_age.setText(String.valueOf(obtener_cliente_por_id(cliente_selecionado_id, lista_clientes).calcularEdad()));
                            desactivar_activar_Botones(true, new Button[]{bt_insert, bt_delete, bt_test, bt_update});
                        }, 1500); // 5000 milisegundos = 5 segundos
                    }, 1500); // 5000 milisegundos = 5 segundos


            }

        });

        bt_modificar_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargar_cliente_en_ficha(obtener_cliente_por_id(cliente_selecionado_id, lista_clientes));
                title_age.setText("Edad");
                et_age.setText(String.valueOf(obtener_cliente_por_id(cliente_selecionado_id, lista_clientes).calcularEdad()));

                visibilidad_botones(false, new Button[]{bt_modificar_aceptar, bt_modificar_salir});
                iv_foto.setVisibility(View.VISIBLE);
                desactivar_activar_Botones(true, new Button[]{bt_delete, bt_test, bt_update, bt_insert});
            }
        });


        //RESTO DE BOTONES
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_id.getText().toString().isEmpty()) {
                    Toast.makeText(Ficha_cliente.this, "Debes selecionar un cliente previamente", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Dialogo que pide ratificacion en los cambios
                new AlertDialog.Builder(Ficha_cliente.this)
                        .setTitle("Confirmación")
                        .setMessage("¿Estás seguro de que uqiere eliminar el cliente " + obtener_cliente_por_id(cliente_selecionado_id, lista_clientes).getName() + " " + obtener_cliente_por_id(cliente_selecionado_id, lista_clientes).getSurname() + "?")  // Mensaje que se mostrará
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                boolean eliminadoArray = eliminar_Cliente_PorId(cliente_selecionado_id, lista_clientes);
                                if (eliminadoArray) {

                                    elimnar_cliente_aqui_BBDD();

                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        actualizar_nombres_buscador(lista_clientes, buscar_clientes);
                                        campos_ficha_visibilidad(false);
                                        title_age.setText("Edad:");
                                        desactivar_activar_Botones(false, new Button[]{bt_update, bt_delete, bt_test});
                                    }, 2000); // 5000 milisegundos = 5 segundos

                                } else {
                                    Toast.makeText(getApplicationContext(), "Error al eliminar el cliente del listado", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                                .

                        setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Acción a realizar si el usuario presiona "No"
                                dialog.dismiss();  // Cierra el diálogo
                            }
                        })
                                .

                        show();
            }
        });

        bt_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Log.d("DEBUG_TVPS", "Fecha revisión: " + datos_test_realizado.getFecha_proxima_revision());

                if(datos_test_realizado == null){
                    abrir_actividad_test_TVPS();
                }else {
                    if (datos_test_realizado.es_posible_realizar_tvps()) {
                        abrir_actividad_test_TVPS();
                    } else {
                       mostrar_dialogo("Todavía no han pasado 6 meses desde la última prueba realizada.\nDebe espeerar a pasr el test hasta despues de la fecha de revisión");
                    }
                }
            }
        });

        ib_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intento = new Intent(Ficha_cliente.this, MainActivity.class);
                startActivity(intento);
            }
        });


    }

    /**
     * Abre la actividad para pasar el est TVPS
     */
    public void abrir_actividad_test_TVPS(){
        int idEmpleado = getIntent().getIntExtra("idEmpleado", -1);
        int edad =Integer.parseInt(et_age.getText().toString());
        Intent intent = new Intent(Ficha_cliente.this, Actividad_Test_TVPS.class);
        intent.putExtra("idCliente", cliente_selecionado_id);
        intent.putExtra("edadCliente", edad);
        intent.putExtra("idEmpleado", idEmpleado);
        intent.putExtra("usuario", nombre_empleado);
        startActivity(intent);
    }

    /**
     * Muestra un dialogo que hay que aceptar con la frase que se pasa como parámetro
     * @param texto
     */
    public void mostrar_dialogo(String texto){
        AlertDialog.Builder builder = new AlertDialog.Builder(Ficha_cliente.this);
        builder.setTitle("Resultados Finales del Test")
                .setMessage(texto)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Puedes cerrar o hacer algo al aceptar
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }


    //METOOS RELACIONADOS CON LAS CONSULTAS A LA BBDD
    /**
     * Carga el array con los datos de los clientes mediante la consulta a la BBDD
     */
    public void cargar_array_list_BBDD() {
        // Metodo que carga la lista en el arrayList
        gestionBBDD.listarClientes(this, new GestionBBDD.ClienteCallback() {
            @Override
            public void onClientesListados(ArrayList<Cliente> clientes) {
                if (clientes != null && !clientes.isEmpty()) {
                    lista_clientes.clear();
                    lista_clientes.addAll(clientes);
                    // Imprimir clientes en Log
                    for (Cliente cliente : lista_clientes) {
                        Log.d("Cliente", "ID: " + cliente.getId() + ", Nombre: " + cliente.getName());
                    }
                    // Mostrar mensaje con el número de clientes cargados
                    Toast.makeText(Ficha_cliente.this, "Clientes cargados: " + lista_clientes.size(), Toast.LENGTH_LONG).show();
                    actualizar_nombres_buscador(lista_clientes, buscar_clientes);

                } else {
                    Toast.makeText(Ficha_cliente.this, "No hay clientes disponibles", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Elimina el clinete de la base de datos
     */
    public void elimnar_cliente_aqui_BBDD(){
        gestionBBDD.eliminarCliente(Ficha_cliente.this, cliente_selecionado_id, new GestionBBDD.deleteCallback() {
            @Override
            public void onClienteEliminado(String respuesta) {

                try {
                    JSONObject jsonResponse = new JSONObject(respuesta);
                    String estado = jsonResponse.getString("estado"); // Extraer el estado

                    if (estado.equalsIgnoreCase("correcto")) {

                        gestionBBDD.listarClientes(Ficha_cliente.this, new GestionBBDD.ClienteCallback() {
                            @Override
                            public void onClientesListados(ArrayList<Cliente> clientes) {
                                if (clientes != null) {
                                    lista_clientes.clear();
                                    lista_clientes.addAll(clientes);
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e("EliminarCliente", "Error al parsear JSON: " + e.getMessage());
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    /**
     * Inserta el nuevo cliente en la base de datos
     * @param cli
     */
    public void insertar_cliente_aqui_BBDD(Cliente cli){
        gestionBBDD.insertarCliente(Ficha_cliente.this, cli, new GestionBBDD.insertCallback() {
                    @Override
                    public void onClienteInsertado(String respuesta) {
                        if (respuesta != null) {

                        gestionBBDD.listarClientes(Ficha_cliente.this, new GestionBBDD.ClienteCallback() {
                            @Override
                            public void onClientesListados(ArrayList<Cliente> clientes) {
                                if (clientes != null) {
                                    lista_clientes.clear();
                                    lista_clientes.addAll(clientes);
                                    //Comprobaciones
                                    Log.d("DEBUG", "Clientes después de insertar: " + clientes.size());
                                    for (Cliente c : clientes) {Log.d("DEBUG", "Cliente: " + c.getName() + " " + c.getSurname());}
                                    // Mostrar mensaje con el número de clientes cargados
                                    Toast.makeText(Ficha_cliente.this, "Clientes cargados: " + lista_clientes.size(), Toast.LENGTH_LONG).show();
                                    actualizar_nombres_buscador(lista_clientes, buscar_clientes);
                                }
                            }
                        });
                    }

            }
        });
    }

    /**
     * Modifica el cliente de la BBDD
     * @param cli
     */
    public void modificar_cliente_aqui_BBDD(Cliente cli){
        gestionBBDD.modificarCliente(Ficha_cliente.this, cli, new GestionBBDD.updateCallback(){

            @Override
            public void onClienteModificado(String respuesta) {

                try {
                    JSONObject jsonResponse = new JSONObject(respuesta);
                    String estado = jsonResponse.getString("estado"); // Extraer el estado

                    if (estado.equalsIgnoreCase("correcto")) {

                        gestionBBDD.listarClientes(Ficha_cliente.this, new GestionBBDD.ClienteCallback() {
                            @Override
                            public void onClientesListados(ArrayList<Cliente> clientes) {
                                if (clientes != null) {
                                    lista_clientes.clear();
                                    lista_clientes.addAll(clientes);
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e("ModificarcCliente", "Error al parsear JSON: " + e.getMessage());
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });

    }

    /**
     * Carga los datos relacionaos con el test realizado TVPS desde la BBDD
     * @param id
     */
    public void cargar_datos_test_realizado_por_usuario_BBDD(int id){
        gestionBBDD.obtenerTestRealizadoPorId(Ficha_cliente.this, id, new GestionBBDD.TestRealizadoCallback() {
            @Override
            public void onTestRealizadoObtenido(Test_realizado testRealizado) {
                if (testRealizado != null) {
                    datos_test_realizado = testRealizado;

                    tv_next_text.setText(datos_test_realizado.fecha_proxima_buen_formato());
                    bt_result.setVisibility(View.VISIBLE);

                    Toast.makeText(
                            Ficha_cliente.this,
                            "ID Cliente: " + testRealizado.getId_cliente() +
                                    "\nID Test: " + testRealizado.getId_test_realizado() +
                                    "\nFecha próxima revisión: " + testRealizado.getFecha_proxima_revision() +
                                    "\nResultado: " + testRealizado.getResultado(),
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    // Si no hay test, ocultamos campos relacionados
                    visibilidad_Textviews(false, new TextView[]{title_test_TVPS, title_next_date_test, tv_next_text});
                    bt_result.setVisibility(View.GONE);
                    Toast.makeText(Ficha_cliente.this, "Este cliente no tiene test TVPS realizado.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    //MÉTODOS QUE  EJECUTAN ACCIONES CONCRETAS RELACIONADAS CON QUÉ Y COMO SE MUESTRAN LOS COMPONENTES DE LA ACTIVIDAD Y LOS DATOS
    /**
     * Modifica el cliente modificado en el Arraylist (lista de clientes en memoria)
     *
     * @param clienteId
     * @param nuevoCliente
     * @return
     */
    public static boolean modificar_Cliente_EnLista(int clienteId, Cliente nuevoCliente) {

        boolean ok = false;
        for (int i = 0; i < lista_clientes.size(); i++) {
            Cliente cliente = lista_clientes.get(i);

            // Si encontramos al cliente con el ID correspondiente
            if (cliente.getId() == clienteId) {
                // Actualizar el cliente con los nuevos datos
                lista_clientes.set(i, nuevoCliente);  // Reemplazar el cliente en la lista
                ok = true;
                break;  // Salir del bucle cuando encontramos al cliente
            }
        }
        return ok;
    }

    /**
     * Metodo que muestra los datos de un cliente en la ficha
     *
     * @param cli
     */
    public void cargar_cliente_en_ficha(Cliente cli) {

        //Los campos de los datos del cliente no se pueden editar
        campos_ficha_editables(false);
        //activo botones restantes
        desactivar_activar_Botones(true, new Button[]{bt_update, bt_delete, bt_test});
        //Invisibilizo los botones de manipular modificar y de insertar
        visibilidad_botones(false, new Button[]{bt_modificar_salir, bt_modificar_aceptar, bt_insertar_aceptar, bt_insertar_salir});

        if (cli != null) {
            //Hago visible los campos de la ficha y cargo los datos
            campos_ficha_visibilidad(true);
            campos_ficha_editables(false);

            tv_id.setText(String.valueOf(cli.getId()));
            et_name.setText(cli.getName());
            et_surname.setText(cli.getSurname());
            et_dni.setText(cli.getDni());
            et_city.setText(cli.getCiudad());
            et_cp.setText(String.valueOf(cli.getCp()));
            et_tlf.setText(String.valueOf(cli.getTlf()));
            et_mail.setText(cli.getEmail());
            et_tutor.setText(cli.getTutor());
            et_street.setText(cli.getStreet());
            et_age.setText(String.valueOf(cli.calcularEdad()));



            if(cli.getGraduate()){

                //tv_graduacion.setText(cli.getGraduate() ? "True" : "False");

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date fechaGraduacion = cli.getDate_graduacion();

                    tv_fecha_gradu.setText(sdf.format(fechaGraduacion));

                    //tv_fecha_gradu.setText(cli.getDate_graduacion().toString());
                    tv_tipo_lente.setText(cli.getTipo_lentes());

            }else{
                visibilidad_Textviews(false, new TextView[]{title_graduacion, title_fecha_gradu, title_tipo_lente, tv_fecha_gradu, tv_tipo_lente});//, tv_graduacion
            }

            if(cli.getTest_TVPS()){

                cargar_datos_test_realizado_por_usuario_BBDD(cli.getId_test_realizado());
               //bt_result.setVisibility(View.VISIBLE);
                //tv_next_text.setText(datos_test_realizado.fecha_proxima_buen_formato());

            }else{
                datos_test_realizado =new Test_realizado();
                visibilidad_Textviews(false, new TextView[]{title_test_TVPS, title_next_date_test, tv_next_text});
                bt_result.setVisibility(View.GONE);
            }


            if(!cli.getGraduate() && !cli.getTest_TVPS()){
                tv_fecha_gradu.setVisibility(View.VISIBLE);
                tv_fecha_gradu.setText("No tiene pruebas diagnósticas todavía");
                visibilidad_Textviews(false, new TextView[]{title_graduacion, title_test_TVPS});
            }

            bt_result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mostrar_dialogo(datos_test_realizado.getResultado());
                }
            });


        } else {
            Log.d("cliente nullo", "error cliente nulo - funcion cargar ficha cliente: ");
        }
    }

    /**
     * Modifica los nombres de los clientes en el buscador según se haya actualizado la lista de clientes
     *
     * @param lista
     * @param buscador
     */
    public void actualizar_nombres_buscador(ArrayList<Cliente> lista, AutoCompleteTextView buscador) {
        String[] nombres = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            Cliente cli = lista.get(i);
            nombres[i] = cli.getName() + " " + cli.getSurname();
        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombres);
        buscador.setAdapter(adaptador);
        buscador.setThreshold(1);

        // [AÑADIDO] Forzar redibujado y cierre de dropdown
        buscador.dismissDropDown(); // [CORREGIDO] Cierra el dropdown si estaba abierto
        buscador.setText("");       // [CORREGIDO] Limpia el texto para evitar conflicto con sugerencias anteriores
    }

    /**
     * Metodo quehace comprobaciones de la insercion de datos por parte del usuario en el formulario
     *
     * @return
     */
    public boolean comprobación_de_relleno_formulario() {

        // Verificar si hay campos vacíos antes de cualquier validación
        if (Utilidades.campos_estan_vacios(et_dni, et_age, et_tlf, et_name, et_surname, et_mail, et_street, et_cp, et_city)) {
            Toast.makeText(Ficha_cliente.this, "Debes rellenar todos los campos editables antes de continuar", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si el teléfono tiene exactamente 9 dígitos
        String telefono = et_tlf.getText().toString().trim();
        if (!telefono.matches("\\d{9}")) {
            Toast.makeText(Ficha_cliente.this, "El teléfono debe contener exactamente 9 dígitos", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si el CP tiene exactamente 5 dígitos
        String codigoPostal = et_cp.getText().toString().trim();
        if (!codigoPostal.matches("\\d{5}")) {
            Toast.makeText(Ficha_cliente.this, "El código postal debe contener exactamente 5 dígitos", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar correo electrónico
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_mail.getText().toString().trim()).matches()) {
            Toast.makeText(Ficha_cliente.this, "Introduce un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar si la fecha ha sido seleccionada
        if (et_age.getText().toString().trim().isEmpty()) {
            Toast.makeText(Ficha_cliente.this, "Debes seleccionar una fecha de nacimiento", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar formato fecha y modificar la fecha en variable general
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            fecha_nacimiento_Seleccionada = sdf.parse(et_age.getText().toString().trim());
            Log.d("Fecha Nacimiento", "Fecha: " + fecha_nacimiento_Seleccionada.toString());
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(Ficha_cliente.this, "El formato de la fecha es incorrecto", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fecha_valida(fecha_nacimiento_Seleccionada)) {
        } else {
            Toast.makeText(Ficha_cliente.this, "El Cliente debe tener entre 1 y 100 años", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }



    //MÉTODOS QUE INICIALIZAN LOS COMPONENTES Y MODIFICAN LO QUE SEMUESTRA EN LA VISTA DE LA ACTIVIDAD
    /**
     * Método que prepara como se muestra el formulario de inserción
     */
    public void preparar_formulario_insert() {
        //1. Pongo los campos visibles
        campos_ficha_visibilidad(true);

        EditText[] edit = new EditText[]{
                et_dni, et_age, et_tlf, et_tutor, et_name, et_surname, et_mail, et_street, et_cp, et_city
        };
        // 2. Seteo el texto por defecto
        for (EditText ed : edit) {
            ed.setText("");
        }
        // Pongo un testo hint a cada campo
        et_name.setHint("Escribe el nombre");
        et_surname.setHint("Escribe el nombre");
        et_dni.setHint("Escriba aquí");
        et_tlf.setHint("Escriba aquí");
        et_tutor.setHint("Si es menor de edad");
        et_mail.setHint("ejemplo@ejemplo.es");
        et_city.setHint("Escriba aquí");
        et_cp.setHint("5 dígitos");
        et_street.setHint("Escriba aquí");

        // EL campo tutor lo hago invisible y lo deshabilito por el moemento
        et_tutor.setEnabled(false);
        et_tutor.setVisibility(View.GONE);
        title_tutor.setVisibility(View.GONE);


        //Cambio el titulo de edad a fecha y hago un listner para que cuando se toque el campo se habra selector y cuando se selecione guardo la variable
        title_age.setText("Fecha de nacimiento");
        et_age.setHint("Selecciona una fecha");
        et_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar el DatePicker y obtener la fecha seleccionada
                mostrarDatePicker(new DatePickerCallback() {
                    @Override
                    public void onDateSelected(Date selectedDate) {
                        // Guardar la fecha seleccionada en la variable
                        fecha_nacimiento_Seleccionada = selectedDate;
                        // Actualizar el campo EditText con la fecha seleccionada (por ejemplo, et_age)
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        et_age.setText(sdf.format(fecha_nacimiento_Seleccionada));
                    }
                });
            }
        });

        //Permito que los campos oportunos sean editables
        campos_ficha_editables(true);

        visibilidad_Textviews(false, new TextView[]{title_purebas, title_graduacion, title_fecha_gradu, title_tipo_lente, title_test_TVPS,
                 title_next_date_test, title_id, tv_id, tv_fecha_gradu,
                tv_tipo_lente, tv_next_text, tv_id}); // tv_graduacion,
        /*TextView[] textvi = new TextView[]{
                title_purebas, title_graduacion, title_fecha_gradu, title_tipo_lente, title_test_TVPS,
                title_date_test, title_next_date_test, title_id, tv_id, tv_graduacion, tv_fecha_gradu,
                tv_tipo_lente, tv_test_tvps, tv_fecha_test_TVPS, tv_next_text, tv_id
        };
        //Hago invisible los campos que no van a poder ser ingresados
        for (TextView tv : textvi) {
            tv.setVisibility(View.GONE);
        }*/

        iv_foto.setVisibility(View.GONE);

    }

    /**
     * Metodo que inicializa todos los componentes
     */
    public void inicializar_componentes() {
        //Inicializo los componentes parte izquierda

        buscar_clientes = findViewById(R.id.autoct_buscador);
        //sp_clientes = findViewById(R.id.sp_clientes);
        tv_user = findViewById(R.id.tv_user);
        title_seleciona = findViewById(R.id.textview_10);
        title_acciones = findViewById(R.id.textview_11);
        bt_insert = findViewById(R.id.bt_insert);
        bt_update = findViewById(R.id.bt_update);
        bt_delete = findViewById(R.id.bt_delete);
        bt_test = findViewById(R.id.bt_test);
        ib_exit = findViewById(R.id.ib_exit);
        desactivar_activar_Botones(false, new Button[]{bt_update, bt_delete, bt_test});

        ib_exit = findViewById(R.id.ib_exit);
        bt_modificar_aceptar = findViewById(R.id.bt_aceptar);
        bt_modificar_salir = findViewById(R.id.bt_salir);
        bt_insertar_aceptar = findViewById(R.id.bt_aceptar_insert);
        bt_insertar_salir = findViewById(R.id.bt_salir_insertar);
        bt_modificar_aceptar.setVisibility(View.GONE);
        bt_modificar_salir.setVisibility(View.GONE);
        bt_insertar_aceptar.setVisibility(View.GONE);
        bt_insertar_salir.setVisibility(View.GONE);

        bt_result = findViewById(R.id.bt_resultados);
        bt_result.setVisibility(View.GONE);



        //Inicializo componentes de la derecha
        title_datos_person = findViewById(R.id.textView);
        title_dni = findViewById(R.id.textView_2);
        title_age = findViewById(R.id.textView_1);
        title_tlf = findViewById(R.id.textView_3);
        title_tutor = findViewById(R.id.textView_4);
        title_mail = findViewById(R.id.textView_5);
        title_direc = findViewById(R.id.textView_6);
        title_street = findViewById(R.id.textView_7);
        title_cp = findViewById(R.id.textView_8);
        title_city = findViewById(R.id.textView_9);
        title_purebas = findViewById(R.id.textView_12);
        title_graduacion = findViewById(R.id.textView_13);
        title_fecha_gradu = findViewById(R.id.textView_14);
        title_tipo_lente = findViewById(R.id.textView_15);
        title_test_TVPS = findViewById(R.id.textView_16);
        //title_date_test = findViewById(R.id.textView_17);
        title_next_date_test = findViewById(R.id.textView_18);
        title_id = findViewById(R.id.textView_19);

        tv_id = findViewById(R.id.tv_id);
        et_dni = findViewById(R.id.et_dni);
        et_age = findViewById(R.id.et_age);
        et_tlf = findViewById(R.id.et_tel);
        et_tutor = findViewById(R.id.et_tutor);
        et_surname = findViewById(R.id.et_surname);
        et_name = findViewById(R.id.et_name);
        et_mail = findViewById(R.id.et_mail);
        et_street = findViewById(R.id.et_street);
        et_cp = findViewById(R.id.et_cp);
        et_city = findViewById(R.id.et_city);
        //tv_graduacion = findViewById(R.id.tv_graduado);
        tv_fecha_gradu = findViewById(R.id.tv_fecha_graduacion);
        tv_tipo_lente = findViewById(R.id.tv_tipo_lente);
        //tv_test_tvps = findViewById(R.id.tv_testsiono);
       // tv_fecha_test_TVPS = findViewById(R.id.tv_fecha_TVPS);
        tv_next_text = findViewById(R.id.tv_fecha_proxTVPS);
        iv_foto = findViewById(R.id.iv_foto);
        iv_foto.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.foto));

        /*et_graduacion= findViewById(R.id.et_graduado);
        et_fecha_gradu= findViewById(R.id.et_fecha_graduacion);
        et_tipo_lente= findViewById(R.id.et_tipo_lente);
        et_test_tvps= findViewById(R.id.et_testsiono);
        et_fecha_test_TVPS= findViewById(R.id.et_fecha_TVPS);
        et_next_text= findViewById(R.id.et_fecha_proxTVPS);*/

    }

    /**
     * Metodo para modificar que los campos de la ficha sean visibles o no
     *
     * @param mostrar
     */
    public void campos_ficha_visibilidad(Boolean mostrar) {
        int visibility = mostrar ? View.VISIBLE : View.GONE;

        visibilidad_Textviews(mostrar,new EditText[]{ et_dni, et_age, et_tlf, et_tutor, et_name, et_surname, et_mail, et_street, et_cp, et_city});

        //EditText[] editTexts = new EditText[]{ et_dni, et_age, et_tlf, et_tutor, et_name, et_surname, et_mail, et_street, et_cp, et_city};


        visibilidad_Textviews(mostrar, new TextView[]{
                tv_id, tv_fecha_gradu, tv_tipo_lente, tv_next_text, title_datos_person,
                title_dni, title_age, title_tlf, title_tutor, title_mail, title_direc, title_street, title_cp, title_city, title_purebas,
                title_id, title_graduacion, title_fecha_gradu, title_tipo_lente,title_test_TVPS,  title_next_date_test
        });


        /*TextView[] textviews = new TextView[]{
                tv_id, tv_fecha_gradu, tv_tipo_lente, tv_next_text, title_datos_person,
                title_dni, title_age, title_tlf, title_tutor, title_mail, title_direc, title_street, title_cp, title_city, title_purebas,
                 title_id, title_graduacion, title_fecha_gradu, title_tipo_lente,title_test_TVPS,  title_next_date_test
        }; //tv_test_tvps, tv_fecha_test_TVPS,title_date_test,

        for (EditText editText : editTexts) {
            editText.setVisibility(visibility);
        }
        for (TextView textv : textviews) {
            textv.setVisibility(visibility);
        }*/

        iv_foto.setVisibility(visibility);

    }

    /**
     * Método que permite que los datos personales sean editables en la ficha
     *
     * @param editables
     */
    public void campos_ficha_editables(boolean editables) {
        int tipoinput = editables ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL;

        // Lista de EditText en la actividad
        EditText[] editTexts = new EditText[]{
                et_dni, et_age, et_tlf, et_tutor, et_name, et_surname, et_mail, et_street, et_cp, et_city
        };

        // Iterar sobre cada EditText
        for (EditText editText : editTexts) {
            // Establecer tipo de entrada
            editText.setInputType(tipoinput);

            // Hacer editable o no
            editText.setFocusable(editables);
            editText.setTextIsSelectable(editables);
            editText.setEnabled(editables);

            // Cambiar color de texto y fondo
            editText.setTextColor(editables ? Color.BLUE : Color.BLACK); // Cambiar a negro
            editText.setBackgroundColor(editables ? Color.LTGRAY : Color.WHITE); // Fondo blanco o transparente
        }

    }

    /**
     * Método que muestra un desplpigable del campo fecha y guarda la fecha seleccionada en una variable
     *
     * @param callback
     */
    private void mostrarDatePicker(final DatePickerCallback callback) {
        // Obtener la fecha actual
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        // Crear y mostrar el DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    // Crear una nueva fecha con los valores seleccionados
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);  // Configura el año, mes y día

                    // Obtener la fecha seleccionada como un objeto Date
                    Date fechaSeleccionada = selectedDate.getTime();

                    // Llamar al callback para devolver la fecha
                    callback.onDateSelected(fechaSeleccionada);

                    // Mostrar la fecha seleccionada en el EditText
                    et_age.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                anio, mes, dia
        );

        datePickerDialog.show();
    }

    /**
     * Poner el nombre del empleado que está logeado
     */
    public void poner_nombre_Empleado() {

            nombre_empleado = getIntent().getStringExtra("usuario");
            tv_user.setText(nombre_empleado);



    }


}