package com.example.proyecto_m13;

import static androidx.core.widget.TextViewKt.addTextChangedListener;

import static Utilidades.Utilidades.eliminar_Cliente_PorId;
import static Utilidades.Utilidades.modificar_Cliente_EnLista;
import static Utilidades.Utilidades.obtener_cliente_por_id;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import Utilidades.Utilidades;


public class Ficha_cliente extends AppCompatActivity {

    private static ArrayList<Cliente> lista_clientes = new ArrayList<>();
    private int cliente_selecionado_id;
    private Date fecha_nacimiento_Seleccionada;


    //Variables parte izquierda de la app
    private TextView tv_user, title_seleciona, title_acciones;
    //private Spinner sp_clientes;
    private AutoCompleteTextView buscar_clientes;
    private Button bt_insert, bt_delete, bt_update, bt_test;
    private Button bt_modificar_aceptar, bt_modificar_salir, bt_insertar_aceptar, bt_insertar_salir;
    private ImageButton ib_exit;
    private ImageView iv_foto;

    //Variables parte derecha

    private TextView tv_id, title_id, title_datos_person, title_dni, title_age, title_tlf, title_tutor, title_mail, title_direc, title_street, title_cp, title_city, title_purebas, title_graduacion, title_fecha_gradu, title_tipo_lente, title_test_TVPS, title_date_test, title_next_date_test;
    private EditText et_dni, et_age, et_tlf, et_tutor, et_name, et_surname, et_mail, et_street, et_cp, et_city;
    //private EditText et_graduacion, et_fecha_gradu, et_tipo_lente, et_test_tvps, et_fecha_test_TVPS, et_next_text;
    private TextView tv_graduacion, tv_fecha_gradu, tv_tipo_lente, tv_test_tvps, tv_fecha_test_TVPS, tv_next_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ficha_cliente);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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
        inicializar_componentes();


        // Metodo que carga la lista en el arrayList
         /*Código que obtiene un arraylist de clientes, y obtiene los datos de cada cliente
        y los mete en un arrayList de strings para mostrarlo en el adaptador
        GestionBBDD gestionBBDD = new GestionBBDD();
        gestionBBDD.listarClientes(this, new GestionBBDD.ClienteCallback() {
            @Override
            public void onClientesListados(ArrayList<Cliente> listaClientes) {
                if (listaClientes != null && !listaClientes.isEmpty()) {
                    // Aquí puedes actualizar la UI con los clientes
                    ArrayList<String> listaClientesAdaptador = new ArrayList<>();
                    for (Cliente cliente : listaClientes) {
                        listaClientesAdaptador.add("Usuario: " + cliente.getUsuario() + "\nContraseña: " + cliente.getContrasena());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Ficha_cliente.this, android.R.layout.simple_list_item_1, listaClientesAdaptador);
                    ListView viewCliente = findViewById(R.id.viewCliente);
                    viewCliente.setAdapter(adapter);
                } else {
                    Toast.makeText(Ficha_cliente.this, "No se encontraron clientes", Toast.LENGTH_LONG).show();
                }
            }
        });*/
        lista_clientes = Utilidades.cargar_lista_empleados();

        actualizar_nombres_buscador(lista_clientes, buscar_clientes);


        if (!buscar_clientes.isSelected()) {
            campos_ficha_visibilidad(false);
        }

        //Cargar spinner
        //ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        //sp_clientes.setAdapter(adaptador);

        buscar_clientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscar_clientes.setText(""); // Borra el texto cuando se hace clic
            }
        });
        buscar_clientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String eleccion = (String) adapterView.getItemAtPosition(i);
                for (Cliente cli : lista_clientes) {
                    if (eleccion.equalsIgnoreCase(cli.getName() + " " + cli.getSurname())) {
                        cliente_selecionado_id = cli.getId();
                        Log.d("Cliente seleccionado", "ID: " + cliente_selecionado_id);
                        //Hago visible los campos de la ficha y cargo los datos
                        cargar_cliente_en_ficha(cli);

                        campos_ficha_editables(false);
                        bt_modificar_salir.setVisibility(View.GONE);
                        bt_modificar_aceptar.setVisibility(View.GONE);
                        bt_insertar_aceptar.setVisibility(View.GONE);
                        bt_insertar_salir.setVisibility(View.GONE);
                        break;
                    }

                }
            }
        });


        //Click botones
        bt_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                preparar_formulario_insert();
                bt_insertar_aceptar.setVisibility(View.VISIBLE);
                bt_insertar_salir.setVisibility(View.VISIBLE);
                et_tutor.setEnabled(false);

                //ESTOY CON ESTO
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

                                if (fecha_nacimiento_Seleccionada != null) {
                                    if (Utilidades.esMayorDeEdad(fecha_nacimiento_Seleccionada)) {
                                        title_tutor.setVisibility(View.GONE);
                                        et_tutor.setVisibility(View.GONE);
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


                //recoger datos del cliente y hacer el insert

                //3. boton aceptar


            }
        });

        bt_insertar_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!comprobación_de_relleno_formulario()) {
                    return;
                }

                if(!Utilidades.esMayorDeEdad(fecha_nacimiento_Seleccionada) &&
                        (et_tutor.getText().toString().trim().isEmpty()|| et_tutor.getText().toString().equalsIgnoreCase("Si es menor de edad"))){
                    Toast.makeText(Ficha_cliente.this, "Al ser menor debes incluir un tutor", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Utilidades.esMayorDeEdad(fecha_nacimiento_Seleccionada)){
                   et_tutor.setHint(null);
                   et_tutor.setText("");
                }
                String nombre = et_name.getText().toString();
                String surname = et_surname.getText().toString();
                String dni = et_dni.getText().toString();
                int tlf = Integer.parseInt(et_tlf.getText().toString());
                String email = et_mail.getText().toString();
                String street = et_street.getText().toString();
                int cp = Integer.parseInt(et_cp.getText().toString());
                String city = et_city.getText().toString();

                String tutor = et_tutor.getText().toString();

                //Cliente que usaré para hacer el insert
                Cliente cli = new Cliente(nombre, surname, dni, fecha_nacimiento_Seleccionada, tlf, email, tutor, street, cp, city);

                        /*//Para cuando mi compañero tenga el metodo hecho
                        //1 hago el insert en BBDD
                        //Vuelvo a cargar el arrayList tras el insert
                        //Dejo abierta la ficha del cliente insertado

                 *//*Codigo para insertar un cliente
        Cliente nuevoCliente = new Cliente("usuario123", "contrasena123", "Juan", "Perez", "juan.perez@example.com");
        GestionBBDD.insertarCliente(context, nuevoCliente);
         *//*
                        //Si es correcto cargo nuevamente con el select los clientes en el array*/

                //por ahora para testear...
                //lista_clientes = cargar_lista_empleados();
                lista_clientes.add(new Cliente(5, nombre,
                        surname, dni, fecha_nacimiento_Seleccionada, tlf, email,
                        tutor, false, null, null,
                        false, street, cp, city));


                if (lista_clientes.size() == 5) {
                    //Actualizo datos buscador y modifico el id selecionado al nuevo cliente para que aparezca su ficha
                    actualizar_nombres_buscador(lista_clientes, buscar_clientes);
                    cliente_selecionado_id = 5;

                    bt_insertar_salir.setVisibility(View.GONE);
                    bt_insertar_aceptar.setVisibility(View.GONE);
                    cargar_cliente_en_ficha(obtener_cliente_por_id(5, lista_clientes));
                    campos_ficha_editables(false);

                    TextView[] textvi = new TextView[]{title_id, tv_id, title_purebas, title_test_TVPS, title_graduacion, title_tutor};

                    for(TextView tv : textvi){tv.setVisibility(View.VISIBLE);}

                    iv_foto.setVisibility(View.VISIBLE);
                    et_tutor.setVisibility(View.VISIBLE);
                    title_age.setText("Edad:");

                }

            }
        });

        bt_insertar_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title_age.setText("Edad");
                campos_ficha_visibilidad(false);
                bt_insertar_salir.setVisibility(View.GONE);
                bt_insertar_aceptar.setVisibility(View.GONE);

            }
        });



        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_id.getText().toString().isEmpty()){
                    Toast.makeText(Ficha_cliente.this, "Debes selecionar un cliente previamente", Toast.LENGTH_SHORT).show();
                    return;

                }
                bt_modificar_aceptar.setVisibility(View.VISIBLE);
                bt_modificar_salir.setVisibility(View.VISIBLE);
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

                if (!comprobación_de_relleno_formulario()) {
                    return;
                }
                if(!Utilidades.esMayorDeEdad(fecha_nacimiento_Seleccionada) &&
                        (et_tutor.getText().toString().trim().isEmpty()|| et_tutor.getText().toString().equalsIgnoreCase(""))){
                    Toast.makeText(Ficha_cliente.this, "Es menor de edad, debes incluir un tutor", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Utilidades.esMayorDeEdad(fecha_nacimiento_Seleccionada)){
                    et_tutor.setText("");
                }

                Date fecha_gradu;
                String texto_fecha_gra = tv_fecha_gradu.getText().toString();
                if (!texto_fecha_gra.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        fecha_gradu = sdf.parse(texto_fecha_gra);

                        if (fecha_gradu != null) {
                            // Usar la fecha según sea necesario
                            Log.d("Fecha Nacimiento", "Fecha: " + fecha_gradu.toString());
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(Ficha_cliente.this, "El formato de la fecha de lentillas", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Si el campo está vacío, manejarlo
                    Toast.makeText(Ficha_cliente.this, "Por favor ingresa una fecha", Toast.LENGTH_SHORT).show();
                }
                String nombre = et_name.getText().toString();
                String surname = et_surname.getText().toString();
                String dni = et_dni.getText().toString();
                int tlf = Integer.parseInt(et_tlf.getText().toString());
                String email = et_mail.getText().toString();
                String street = et_street.getText().toString();
                int cp = Integer.parseInt(et_cp.getText().toString());
                String city = et_city.getText().toString();
                String tutor = et_tutor.getText().toString();

                boolean graduado = obtener_cliente_por_id(cliente_selecionado_id, lista_clientes).getGraduate();
                Date fecha_gradu1 = obtener_cliente_por_id(cliente_selecionado_id, lista_clientes).getDate_graduacion();
                String tipo = obtener_cliente_por_id(cliente_selecionado_id, lista_clientes).getTipo_lentes();
                boolean test_tvps= obtener_cliente_por_id(cliente_selecionado_id, lista_clientes).getTest_TVPS();


                // Esto hay que cambiarlo

                Cliente cli = new Cliente(cliente_selecionado_id, nombre, surname, dni, fecha_nacimiento_Seleccionada, tlf, email, tutor, graduado, fecha_gradu1, tipo, test_tvps, street, cp, city);

                if (modificar_Cliente_EnLista(cliente_selecionado_id, cli, lista_clientes)) {

                    //Modificar base de datos
                    /*Codigo para modificar un cliente
                    Cliente clienteModificado = new Cliente("usuario123", "nuevaContrasena", "Juan", "Perez", "juan.perez@nuevocliente.com");
                    GestionBBDD.modificarCliente(context, clienteModificado);
                     */

                    actualizar_nombres_buscador(lista_clientes, buscar_clientes);
                    cargar_cliente_en_ficha(obtener_cliente_por_id(cliente_selecionado_id, lista_clientes));
                    campos_ficha_editables(false);
                    bt_modificar_salir.setVisibility(View.GONE);
                    bt_modificar_aceptar.setVisibility(View.GONE);

                    //TextView[] textvi = new TextView[]{title_id, tv_id, title_purebas, title_test_TVPS, title_graduacion, title_tutor};

                    //for(TextView tv : textvi){tv.setVisibility(View.VISIBLE);}

                    iv_foto.setVisibility(View.VISIBLE);
                    et_tutor.setVisibility(View.VISIBLE);
                    title_age.setText("Edad:");

                    et_age.setText(String.valueOf(obtener_cliente_por_id(cliente_selecionado_id, lista_clientes).calcularEdad()));

                }

            }

        });

        bt_modificar_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargar_cliente_en_ficha(obtener_cliente_por_id(cliente_selecionado_id, lista_clientes));
                title_age.setText("Edad");
                et_age.setText(String.valueOf(obtener_cliente_por_id(cliente_selecionado_id, lista_clientes).calcularEdad()));
                bt_modificar_salir.setVisibility(View.GONE);
                bt_modificar_aceptar.setVisibility(View.GONE);
                iv_foto.setVisibility(View.VISIBLE);

            }
        });

        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_id.getText().toString().isEmpty()){
                    Toast.makeText(Ficha_cliente.this, "Debes selecionar un cliente previamente", Toast.LENGTH_SHORT).show();
                    return;

                }

                new AlertDialog.Builder(Ficha_cliente.this)
                        .setTitle("Confirmación")  // Título del diálogo
                        .setMessage("¿Estás seguro de que uqiere eliminar el cliente " + obtener_cliente_por_id(cliente_selecionado_id, lista_clientes).getName() + " " + obtener_cliente_por_id(cliente_selecionado_id, lista_clientes).getSurname() + "?")  // Mensaje que se mostrará
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Acción a realizar si el usuario presiona "Sí"
                                if (eliminar_Cliente_PorId(cliente_selecionado_id, lista_clientes)) {

                                    //Método de eliminacion BBDD
                                     /*Codigo para eliminar un cliente
                                        GestionBBDD.eliminarCliente(context, idUsuario);
                                    */

                                    Toast.makeText(getApplicationContext(), "Cliente borrado", Toast.LENGTH_SHORT).show();
                                    actualizar_nombres_buscador(lista_clientes, buscar_clientes);
                                    campos_ficha_visibilidad(false);
                                    title_age.setText("Edad:");
                                }


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Acción a realizar si el usuario presiona "No"
                                dialog.dismiss();  // Cierra el diálogo
                            }
                        })
                        .show();
            }
        });


    }




    /**
     * Metodo que muestra los datos de un cliente en la ficha
     *
     * @param cli
     */
    public void cargar_cliente_en_ficha(Cliente cli) {

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


            tv_graduacion.setText(cli.getGraduate() ? "True" : "False");

            if (cli.getGraduate()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date fechaGraduacion = cli.getDate_graduacion();

                tv_fecha_gradu.setText(sdf.format(fechaGraduacion));

                //tv_fecha_gradu.setText(cli.getDate_graduacion().toString());
                tv_tipo_lente.setText(cli.getTipo_lentes());
            } else {
                tv_fecha_gradu.setVisibility(View.GONE);
                title_fecha_gradu.setVisibility(View.GONE);
                tv_tipo_lente.setVisibility(View.GONE);
                title_tipo_lente.setVisibility(View.GONE);
            }


            tv_test_tvps.setText(cli.getGraduate() ? "True" : "False");
            if (cli.getTest_TVPS()) {
                tv_fecha_test_TVPS.setText("pruebaaaaaa");
                tv_next_text.setText("pruebaaaaa");
                //tv_fecha_test_TVPS.setText(cli.get_date_test_TVPS);
                //tv_next_text.setText(cli.get_next_date_TVPS);
            } else {
                tv_fecha_test_TVPS.setVisibility(View.GONE);
                title_date_test.setVisibility(View.GONE);
                tv_next_text.setVisibility(View.GONE);
                title_next_date_test.setVisibility(View.GONE);
            }
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
            //nombres[i] = cli.getName();
            nombres[i] = cli.getName() + " " + cli.getSurname();

            //AutoCompletText
            ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombres);
            buscador.setAdapter(adaptador);
            buscador.setThreshold(1);

        }
    }

    /**
     * Metodo quehace comprobaciones de la insercion de datos por parte del usuario en el formulario
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

        return true;
    }

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

        TextView[]  textvi = new TextView[]{
                title_purebas, title_graduacion,title_fecha_gradu, title_tipo_lente, title_test_TVPS,
                title_date_test, title_next_date_test, title_id, tv_id, tv_graduacion, tv_fecha_gradu,
                tv_tipo_lente, tv_test_tvps, tv_fecha_test_TVPS, tv_next_text,tv_id
        };
        //Hago invisible los campos que no van a poder ser ingresados
        for(TextView tv : textvi){
            tv.setVisibility(View.GONE);
        }

        iv_foto.setVisibility(View.GONE);

    }

    /**
     * Metodo que inicializa todos los componentes
     */
    public void inicializar_componentes() {
        //Inicializo los componentes parte izquierda

        bt_modificar_aceptar = findViewById(R.id.bt_aceptar);
        bt_modificar_salir = findViewById(R.id.bt_salir);
        bt_insertar_aceptar = findViewById(R.id.bt_aceptar_insert);
        bt_insertar_salir = findViewById(R.id.bt_salir_insertar);
        bt_modificar_aceptar.setVisibility(View.GONE);
        bt_modificar_salir.setVisibility(View.GONE);
        bt_insertar_aceptar.setVisibility(View.GONE);
        bt_insertar_salir.setVisibility(View.GONE);


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
        title_date_test = findViewById(R.id.textView_17);
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
        tv_graduacion = findViewById(R.id.tv_graduado);
        tv_fecha_gradu = findViewById(R.id.tv_fecha_graduacion);
        tv_tipo_lente = findViewById(R.id.tv_tipo_lente);
        tv_test_tvps = findViewById(R.id.tv_testsiono);
        tv_fecha_test_TVPS = findViewById(R.id.tv_fecha_TVPS);
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

        EditText[] editTexts = new EditText[]{
                et_dni, et_age, et_tlf, et_tutor, et_name, et_surname, et_mail, et_street, et_cp, et_city
        };

        TextView[] textviews = new TextView[]{
                tv_id, tv_graduacion, tv_fecha_gradu, tv_tipo_lente, tv_test_tvps, tv_fecha_test_TVPS, tv_next_text, title_datos_person,
                title_dni, title_age, title_tlf, title_tutor, title_mail, title_direc, title_street, title_cp, title_city, title_purebas,
                title_graduacion, title_fecha_gradu, title_tipo_lente, title_test_TVPS, title_date_test, title_next_date_test, title_id
        };

        for (EditText editText : editTexts) {
            editText.setVisibility(visibility);
        }
        for (TextView textv : textviews) {
            textv.setVisibility(visibility);
        }

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



    //Metodos traspasados a el pacage utilidades
    /**
     * Modifica el cliente modificado en el Arraylista (lista de clientes en memoria)
     *
     * @param clienteId
     * @param nuevoCliente
     * @return
     */
    /*public boolean modificar_Cliente_EnLista(int clienteId, Cliente nuevoCliente) {

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
    }*/

    /**
     * Método que elimina un cliente del array list por id
     *
     * @param clienteId
     * @return
     */
    /*public boolean eliminar_Cliente_PorId(int clienteId) {

        boolean ok = false;
        for (int i = 0; i < lista_clientes.size(); i++) {
            Cliente cliente = lista_clientes.get(i);

            // Si encontramos al cliente con el ID correspondiente
            if (cliente.getId() == clienteId) {
                // Actualizar el cliente con los nuevos datos
                lista_clientes.remove(i);
                ok = true;
                break;  // Salir del bucle cuando encontramos al cliente
            }
        }
        return ok;

        */

    /**
     * Metodo que retorna un cliente del ArrayList a partir de su id
     *
     * @param id
     * @return
     */
    /*public Cliente obtener_cliente_por_id(int id) {
        for (Cliente cliente : lista_clientes) {
            if (cliente.getId() == id) {
                return cliente; // Retorna el cliente si encuentra coincidencia
            }
        }
        return null;
    }*/

    /**
     * Método para saber si han pasado 18 años de la fecha introducida
     * @param fechaNacimiento
     * @return
     */
    /*public static boolean esMayorDeEdad(Date fechaNacimiento) {
        Calendar cal = Calendar.getInstance();
        int edadActual = cal.get(Calendar.YEAR) - (fechaNacimiento.getYear() + 1900); // Sumamos 1900 ya que el año de Date es desde 1900

        if (cal.get(Calendar.MONTH) < fechaNacimiento.getMonth()) {
            edadActual--; // Si la fecha actual aún no ha llegado al cumpleaños
        } else if (cal.get(Calendar.MONTH) == fechaNacimiento.getMonth()) {
            if (cal.get(Calendar.DAY_OF_MONTH) < fechaNacimiento.getDate()) {
                edadActual--; // Si el cumpleaños no ha pasado en el mes actual
            }
        }

        return edadActual >= 18;  // Si tiene 18 años o más, es mayor de edad
    }*/

    /**
     * Método que comprba si los editText están vacios
     *
     * @param campos
     * @return
     */
    /*public boolean campos_estan_vacios(EditText... campos) {
        for (EditText campo : campos) {
            if (campo.getText().toString().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }*/

    /**
     * Método que devuelve un ArrayList de empleados prefijado
     *
     * @return
     */
    /*public ArrayList<Cliente> cargar_lista_empleados() {
        ArrayList<Cliente> lista = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            lista.add(new Cliente(1, "Eduardo", "Lucas", "520546666K", dateFormat.parse("03/04/1987"), 123456789, "edu@gmail", "Paco", true, dateFormat.parse("10/05/2019"), "gafas", false, "Illescas 27", 28047, "Madird"));
            lista.add(new Cliente(2, "Carlos", "Herrera", "520546567L", dateFormat.parse("06/02/2019"), 123456789, "paco@gmail", "Antonio Lucas", false, null, null, false, "Valmo 27", 28047, "Barcelona"));
            lista.add(new Cliente(3, "Bibiana", "Martinez", "520546123G", dateFormat.parse("10/11/2022"), 123456789, "sara@gmail", null, true, dateFormat.parse("10/11/2022"), "lentillas", true, "Oca 27", 28047, "Lugo"));
            lista.add(new Cliente(4, "Eduardo", "Jose", "520546666K", dateFormat.parse("03/04/1987"), 123456789, "edu@gmail", "Paco", true, dateFormat.parse("10/05/2019"), "gafas", false, "Illescas 27", 28047, "Madird"));

            return lista;
        } catch (ParseException e) {
            Toast.makeText(Ficha_cliente.this, "Error al cargar los datos por defecto", Toast.LENGTH_SHORT).show();
            return lista;
        }

    }*/

    /*private void mostrarDatePicker() {
        // Obtener la fecha actual
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        // Crear y mostrar el DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    // Formatear la fecha y mostrarla en el EditText
                    String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                    et_age.setText(fechaSeleccionada);
                },
                anio, mes, dia
        );

        datePickerDialog.show();
    }*/
}