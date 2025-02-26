package com.example.proyecto_m13;

import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.widget.ArrayAdapter;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Ficha_cliente extends AppCompatActivity {

    private static ArrayList<Cliente> lista_clientes = new ArrayList<>();

    private int cliente_selecionado;

    private Date fecha_nacimiento_Seleccionada;


    //Variables parte izquierda de la app
    private TextView tv_user, title_seleciona, title_acciones;
    private Spinner sp_clientes;
    private AutoCompleteTextView buscar_clientes;
    private Button bt_insert, bt_delete, bt_update, bt_test;
    private Button bt_modificar_aceptar, bt_modificar_salir;
    private ImageButton ib_exit;

    //Variables parte derecha

    private TextView tv_id, title_datos_person, title_dni, title_age, title_tlf, title_tutor, title_mail,title_direc, title_street, title_cp, title_city, title_purebas, title_graduacion, title_fecha_gradu, title_tipo_lente, title_test_TVPS, title_date_test, title_next_date_test;
    private EditText et_dni, et_age, et_tlf, et_tutor, et_name, et_surname, et_mail, et_street, et_cp, et_city;
    private EditText et_graduacion, et_fecha_gradu, et_tipo_lente, et_test_tvps, et_fecha_test_TVPS, et_next_text;
    private TextView tv_graduacion, tv_fecha_gradu, tv_tipo_lente, tv_test_tvps, tv_fecha_test_TVPS, tv_next_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ficha_cliente);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


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

        /*Codigo para insertar un cliente
        Cliente nuevoCliente = new Cliente("usuario123", "contrasena123", "Juan", "Perez", "juan.perez@example.com");
        GestionBBDD.insertarCliente(context, nuevoCliente);
         */

        /*Codigo para modificar un cliente
        Cliente clienteModificado = new Cliente("usuario123", "nuevaContrasena", "Juan", "Perez", "juan.perez@nuevocliente.com");
        GestionBBDD.modificarCliente(context, clienteModificado);
         */

        /*Codigo para eliminar un cliente
        GestionBBDD.eliminarCliente(context, idUsuario);
         */

        inicializar_componentes();
        campos_ficha_visibilidad(false);

        lista_clientes = cargar_lista_empleados();
        String [] nombres = new String[lista_clientes.size()];
        int contador = 0;
        for (Cliente cli : lista_clientes){
            nombres[contador] = cli.getName() + " " +cli.getSurname();
            contador ++;
        }

        //Cargar spinner
        //ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        //sp_clientes.setAdapter(adaptador);

        //AutoCompletText
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombres);
        buscar_clientes.setAdapter(adaptador);
        buscar_clientes.setThreshold(1);

        buscar_clientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String eleccion = (String) adapterView.getItemAtPosition(i);
                for (Cliente cli : lista_clientes){
                    if(eleccion.equalsIgnoreCase(cli.getName() + " " +cli.getSurname())){
                        cliente_selecionado = cli.getId();
                        //Hago visible los campos de la ficha y cargo los datos
                        campos_ficha_visibilidad(true);
                        campos_ficha_editables(false);
                        et_name.setText(cli.getName());
                        et_surname.setText(cli.getSurname());
                        et_dni.setText(cli.getDni());
                        et_city.setText(cli.getCiudad());
                        et_cp.setText(cli.getCp());
                        et_tlf.setText(cli.getTlf());
                        et_mail.setText(cli.getEmail());
                        et_tutor.setText(cli.getTutor());
                        et_street.setText(cli.getStreet());
                        et_age.setText(cli.calcularEdad());


                        et_graduacion.setText(cli.getGraduate() ? "True" : "False");
                        if(cli.getGraduate()){
                            tv_fecha_gradu.setText(cli.getDate_graduacion());
                            tv_tipo_lente.setText(cli.getTipo_lentes());
                        }else{
                            tv_fecha_gradu.setVisibility(View.GONE);
                            title_fecha_gradu.setVisibility(View.GONE);
                            tv_tipo_lente.setVisibility(View.GONE);
                            title_tipo_lente.setVisibility(View.GONE);
                        }

                        et_graduacion.setText(cli.getGraduate() ? "True" : "False");
                        if(cli.getTest_TVPS()){
                            tv_fecha_test_TVPS.setText("pruebaaaaaa");
                            tv_next_text.setText("pruebaaaaa");
                            //tv_fecha_test_TVPS.setText(cli.get_date_test_TVPS);
                            //tv_next_text.setText(cli.get_next_date_TVPS);
                        }else{
                            tv_fecha_test_TVPS.setVisibility(View.GONE);
                            title_date_test.setVisibility(View.GONE);
                            tv_next_text.setVisibility(View.GONE);
                            title_next_date_test.setVisibility(View.GONE);
                        }

                    }

                }
            }
        });

        //Click botones
        bt_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_modificar_aceptar.setVisibility(View.VISIBLE);
                bt_modificar_salir.setVisibility(View.VISIBLE);
                campos_ficha_editables(true);
                title_age.setText("Fecha de nacimiento:");
                //Aquí quiero que el piker pueda ser usado

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

        bt_modificar_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(campos_estan_vacios(et_dni, et_age, et_tlf, et_name, et_surname, et_mail, et_street, et_cp, et_city)){
                    Toast.makeText(Ficha_cliente.this, "Debes rellenar todos los campos editables antes de continuar", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Verificar si la fecha ha sido seleccionada
                if (fecha_nacimiento_Seleccionada == null) {
                    Toast.makeText(Ficha_cliente.this, "Debes seleccionar una fecha de nacimiento", Toast.LENGTH_SHORT).show();
                    return;
                }
                String nombre = et_name.getText().toString();
                String surname = et_surname.getText().toString();
                String dni = et_dni.getText().toString();
                int tlf = Integer.parseInt(et_tlf.getText().toString());
                String email = et_mail.getText().toString();
                String street = et_street.getText().toString();
                int cp = Integer.parseInt(et_cp.getText().toString());
                String city = et_city.getText().toString();
                String tutor = null;
                if(et_tutor.toString().trim().isEmpty()){
                    tutor = et_tutor.getText().toString();
                }
                //Aquí quiero obtener la fecha selecionada

                Cliente cli = new Cliente(cliente_selecionado, nombre, surname, dni, fecha_nacimiento_Seleccionada, tlf, email, tutor, false, null, null, false, street, cp, city);

                if(modificarClienteEnLista(cliente_selecionado, cli)){
                    //Modificar base de datos
                }




            }
                //String age = et_name.getText().toString();
                //String tutor = et_tutor.getText().toString();


        });

        bt_modificar_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title_age.setText("Edad");
            }
        });

        
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
        bt_modificar_aceptar =findViewById(R.id.bt_aceptar);
        bt_modificar_salir = findViewById(R.id.bt_salir);
        bt_modificar_aceptar.setVisibility(View.GONE);
        bt_modificar_salir.setVisibility(View.GONE);
        buscar_clientes = findViewById(R.id.autoct_buscador);
        //sp_clientes = findViewById(R.id.sp_clientes);

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
        et_tlf= findViewById(R.id.et_mail);
        et_tutor= findViewById(R.id.et_tutor);
        et_surname=findViewById(R.id.et_surname);
        et_name= findViewById(R.id.et_name);
        et_mail= findViewById(R.id.et_mail);
        et_street= findViewById(R.id.et_street);
        et_cp= findViewById(R.id.et_cp);
        et_city= findViewById(R.id.et_city);
        tv_graduacion= findViewById(R.id.tv_graduado);
        tv_fecha_gradu= findViewById(R.id.tv_fecha_graduacion);
        tv_tipo_lente= findViewById(R.id.tv_tipo_lente);
        tv_test_tvps= findViewById(R.id.tv_testsiono);
        tv_fecha_test_TVPS= findViewById(R.id.tv_fecha_TVPS);
        tv_next_text= findViewById(R.id.tv_fecha_proxTVPS);

        /*et_graduacion= findViewById(R.id.et_graduado);
        et_fecha_gradu= findViewById(R.id.et_fecha_graduacion);
        et_tipo_lente= findViewById(R.id.et_tipo_lente);
        et_test_tvps= findViewById(R.id.et_testsiono);
        et_fecha_test_TVPS= findViewById(R.id.et_fecha_TVPS);
        et_next_text= findViewById(R.id.et_fecha_proxTVPS);*/

    }

    public void campos_ficha_visibilidad(Boolean mostrar){
        int visibility = mostrar ? View.VISIBLE : View.GONE;

        tv_id.setVisibility(visibility);
        et_dni.setVisibility(visibility);
        et_age.setVisibility(visibility);
        et_tlf.setVisibility(visibility);
        et_tutor.setVisibility(visibility);
        et_name.setVisibility(visibility);
        et_surname.setVisibility(visibility);
        et_mail.setVisibility(visibility);
        et_street.setVisibility(visibility);
        et_cp.setVisibility(visibility);
        et_city.setVisibility(visibility);

        tv_graduacion.setVisibility(visibility);
        tv_fecha_gradu.setVisibility(visibility);
        tv_tipo_lente.setVisibility(visibility);
        tv_test_tvps.setVisibility(visibility);
        tv_fecha_test_TVPS.setVisibility(visibility);
        tv_next_text.setVisibility(visibility);

        /*et_graduacion.setVisibility(visibility);
        et_fecha_gradu.setVisibility(visibility);
        et_tipo_lente.setVisibility(visibility);
        et_test_tvps.setVisibility(visibility);
        et_fecha_test_TVPS.setVisibility(visibility);
        et_next_text.setVisibility(visibility);*/
    }

    public void campos_ficha_editables(boolean editables){
        int tipoinput = editables ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL;

        et_dni.setInputType(tipoinput);
        et_age.setInputType(tipoinput);
        et_tlf.setInputType(tipoinput);
        et_tutor.setInputType(tipoinput);
        et_name.setInputType(tipoinput);
        et_surname.setInputType(tipoinput);
        et_mail.setInputType(tipoinput);
        et_street.setInputType(tipoinput);
        et_cp.setInputType(tipoinput);
        et_city.setInputType(tipoinput);


    }

    public ArrayList<Cliente> cargar_lista_empleados(){
        ArrayList<Cliente> lista = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try{
            lista.add(new Cliente(1, "Eduardo", "Lucas", "520546666K", dateFormat.parse("03/04/1987"), 123456789, "edu@gmail", null,true ,"10/05/2019", "gafas", false,  "Illescas 27", 28047, "Madird" ));
            lista.add(new Cliente(2, "Pacp", "Martin", "520546567L", dateFormat.parse("06/02/2019"), 123456789, "paco@gmail", "Antonio Lucas",false ,null, null, false,  "Valmo 27", 28047, "Barcelona" ));
            lista.add(new Cliente(3, "Sara", "Lopez", "520546123G", dateFormat.parse("10/11/2022"), 123456789, "sara@gmail", null,true ,"10/11/2022", "lentillas", true,  "Oca 27", 28047, "Lugo" ));

            return lista;
        }catch (ParseException e){
            Toast.makeText(Ficha_cliente.this, "Error al cargar los datos por defecto", Toast.LENGTH_SHORT).show();
            return lista;
        }

    }

    public boolean campos_estan_vacios(EditText... campos) {
        for (EditText campo : campos) {
            if (campo.getText().toString().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

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


    public boolean modificarClienteEnLista(int clienteId, Cliente nuevoCliente) {

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



}