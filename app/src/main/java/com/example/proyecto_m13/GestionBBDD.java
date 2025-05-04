package com.example.proyecto_m13;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GestionBBDD {
    public static final String BASE_URL = "http://192.168.1.145/";


    @SuppressLint("StaticFieldLeak")
    public void comprobarCredenciales (Context context, String usuario, String contrasena){
        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... voids){
                try{
                    URL url = new URL(BASE_URL +"db_validation.php");
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    conexion.setRequestMethod("POST");
                    conexion.setRequestProperty("Content-Type","application/json; utf-8");
                    conexion.setDoOutput(true);

                    JSONObject jsonValidar = new JSONObject();
                    jsonValidar.put("usuario", usuario);
                    jsonValidar.put("contrasena", contrasena);

                    OutputStream os = conexion.getOutputStream();
                    os.write(jsonValidar.toString().getBytes("UTF-8"));
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null){
                        response.append(responseLine.trim());
                    }
                    br.close();
                    return response.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("ServerResponse", "Respuesta del servidor: MMMALA");

                    return null;
                }
            }

            protected void onPostExecute(String response) {
                if (response != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String estado = jsonResponse.getString("estado");
                        String mensaje = jsonResponse.getString("mensaje");

                        if ("correcto".equals(estado)) {
                            // Extraer el ID devuelto, que en el PHP se envía en el campo "id"
                            int idEmpleado = jsonResponse.getInt("id");
                            Toast.makeText(context, "Inicio de sesión exitoso. ID: " + idEmpleado, Toast.LENGTH_LONG).show();

                            // Crear el Intent para pasar al siguiente Activit
                            Intent intent = new Intent(context, Ficha_cliente.class);
                            intent.putExtra("usuario", usuario);
                            intent.putExtra("idEmpleado", idEmpleado);
                            context.startActivity(intent);

                        } else {
                            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error al procesar la respuesta", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Error en la conexión", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    public interface ClienteCallback {
        void onClientesListados(ArrayList<Cliente> listaClientes);
    }

    public interface insertCallback {
        void onClienteInsertado(String respuesta);
    }

    public interface updateCallback {
        void onClienteModificado(String respuesta);
    }

    public interface deleteCallback {
        void onClienteEliminado(String respuesta);
    }

    public interface DiapositivasCallback {
        void onDiapositivasListadas(ArrayList<Diapositiva> listaDiapositivas);
    }

    public interface TestRealizadoCallback {
        void onTestRealizadoObtenido(Test_realizado testRealizado);
    }

    public interface insertarTestCallback {
        void onSuccess(int idInsertado);
        void onError(String mensajeError);
    }



    public interface UpdateCompletadoCallback {
        void updateCompletadoCallback(String respuesta);
    }

    public interface ListarEstudiosCallback {
        void onListarEstudiosCallback(ArrayList<Estudio> estudios);
    }


    @SuppressLint("StaticFieldLeak")
    public void listarClientes(Context context, final ClienteCallback callback) {
        new AsyncTask<Void, Void, ArrayList<Cliente>>() {
            @Override
            protected ArrayList<Cliente> doInBackground(Void... voids) {
                ArrayList<Cliente> listaClientes = new ArrayList<>();
                try {
                    URL url = new URL(BASE_URL + "db_select_clientes.php");
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    conexion.setRequestMethod("GET");
                    conexion.setRequestProperty("Accept", "application/json");

                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    JSONObject jsonObject = new JSONObject(response.toString());
                    String estado = jsonObject.getString("estado");

                    if (estado.equals("correcto")) {
                        JSONArray jsonListado = jsonObject.getJSONArray("datos");
                        for (int i = 0; i < jsonListado.length(); i++) {
                            JSONObject jsonCliente = jsonListado.getJSONObject(i);

                            // Convertir fecha de nacimiento
                            Date dateBorn = null;
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                dateBorn = sdf.parse(jsonCliente.getString("date_born"));
                            } catch (Exception e) {
                                Log.e("Error", "Fecha de nacimiento inválida: " + jsonCliente.optString("date_born"));
                            }

                            // Convertir fecha de graduación (puede ser null)
                            Date dateGraduacion = null;
                            try {
                                String dateGraduacionStr = jsonCliente.optString("date_graduacion", "").trim();
                                if (!dateGraduacionStr.isEmpty() && !dateGraduacionStr.equalsIgnoreCase("null")) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    dateGraduacion = sdf.parse(dateGraduacionStr);
                                }
                            } catch (Exception e) {
                                Log.e("Error", "Fecha de graduación inválida: " + jsonCliente.optString("date_graduacion"));
                            }

                            // Conversión de booleanos
                            boolean graduate = jsonCliente.optString("graduate", "0").equals("1");
                            boolean testTVPS = jsonCliente.optString("Test_TVPS", "0").equals("1");

                            // Recoger id_test_realizado y asignarlo al atributo id_test_completado
                            int id_test_completado = jsonCliente.optInt("id_test_realizado", 0);

                            // Crear objeto Cliente
                            Cliente cliente = new Cliente(
                                    jsonCliente.getInt("id"),
                                    jsonCliente.getString("name"),
                                    jsonCliente.getString("surname"),
                                    jsonCliente.getString("dni"),
                                    dateBorn,
                                    jsonCliente.getInt("tlf"),
                                    jsonCliente.getString("email"),
                                    jsonCliente.optString("tutor", ""),
                                    graduate,
                                    dateGraduacion,
                                    jsonCliente.optString("tipo_lentes", ""),
                                    testTVPS,
                                    id_test_completado,
                                    jsonCliente.getString("street"),
                                    jsonCliente.getInt("cp"),
                                    jsonCliente.getString("ciudad")
                            );

                            listaClientes.add(cliente);
                        }
                    }

                } catch (Exception e) {
                    Log.e("Error", "Error al obtener los clientes", e);
                }
                return listaClientes;
            }

            @Override
            protected void onPostExecute(ArrayList<Cliente> listaClientes) {
                if (callback != null) {
                    callback.onClientesListados(listaClientes);
                } else {
                    Log.e("Error", "Callback es null en listarClientes");
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void insertarCliente(Context context, Cliente cliente, final insertCallback callback) {

        new AsyncTask<Void, Void, String>() {
            //String respuesta = null;
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Se establece la URL del servicio donde se enviarán los datos
                    URL url = new URL(BASE_URL+"db_insert.php");
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

                    // Se configura la conexión para enviar una solicitud POST con datos en formato JSON
                    conexion.setRequestMethod("POST");
                    conexion.setRequestProperty("Content-Type", "application/json; utf-8");
                    conexion.setDoOutput(true);

                    // Se define un formateador de fechas para convertir objetos Date a cadenas en formato "yyyy-MM-dd"
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    // Se crea un objeto JSON con los datos del cliente a insertar en la base de datos
                    JSONObject jsonInsertar = new JSONObject();
                    jsonInsertar.put("dni", cliente.getDni());
                    jsonInsertar.put("nombre", cliente.getName());
                    jsonInsertar.put("apellidos", cliente.getSurname());
                    jsonInsertar.put("telefono", cliente.getTlf());
                    jsonInsertar.put("email", cliente.getEmail());
                    jsonInsertar.put("calle", cliente.getStreet());
                    jsonInsertar.put("c_p", cliente.getCp());
                    jsonInsertar.put("ciudad", cliente.getCiudad());
                    jsonInsertar.put("tutor_legal", cliente.getTutor());
                    jsonInsertar.put("tipo_lente", cliente.getTipo_lentes());

                    /* Lo dejo comentado porque en ficha cliente veo que utiliza el constructor sin estos datos
                    jsonInsertar.put("graduado", cliente.getGraduate());
                    jsonInsertar.put("tipo_lente", cliente.getTipo_lentes());
                    jsonInsertar.put("test_completado", cliente.getTest_TVPS());
                    jsonInsertar.put("resultado_test", JSONObject.NULL); // Se deja como null si no hay un resultado disponible
                    */

                    // Se convierten las fechas a formato de texto antes de enviarlas, evitando valores nulos
                    jsonInsertar.put("fecha_nacimiento", cliente.getDate_born() != null ? dateFormat.format(cliente.getDate_born()) : JSONObject.NULL);
                    //jsonInsertar.put("fecha_ultima_graduacion", cliente.getDate_graduacion() != null ? dateFormat.format(cliente.getDate_graduacion()) : JSONObject.NULL);


                    // objeto JSON que se envia
                    OutputStream os = conexion.getOutputStream();
                    os.write(jsonInsertar.toString().getBytes("UTF-8"));
                    os.close();

                    // Se recibe y procesa la respuesta del servidor
                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    br.close();


                    // Se retorna la respuesta obtenida del servidor
                    return response.toString();


                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String response) {
                //boolean ok = false;
                if (response != null) {
                    try {
                        // Se procesa la respuesta del servidor, interpretando los datos en formato JSON
                        JSONObject jsonResponse = new JSONObject(response);
                        String estado = jsonResponse.getString("estado");
                        String mensaje = jsonResponse.getString("mensaje");

                        // Se verifica si la inserción fue exitosa y se muestra un mensaje acorde
                        if ("correcto".equals(estado)) {
                            Toast.makeText(context, "Cliente insertado correctamente", Toast.LENGTH_LONG).show();
                            //ok = true;
                        } else {
                            Toast.makeText(context, "Error: " + mensaje, Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error al procesar la respuesta", Toast.LENGTH_LONG).show();

                    }
                } else {
                    // Se muestra un mensaje si hubo un problema con la conexión al servidor
                    Toast.makeText(context, "Error en la conexión", Toast.LENGTH_LONG).show();
                    // ok = true;
                }
                //return ok;
            }
        }.execute();
    }


    /*@SuppressLint("StaticFieldLeak")
    public void modificarCliente(Context context, Cliente cliente, final updateCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Se establece la URL del servicio que procesará la modificación del cliente
                    URL url = new URL(BASE_URL + "db_update.php");
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

                    // Se configura la conexión para enviar una solicitud POST con datos en formato JSON
                    conexion.setRequestMethod("POST");
                    conexion.setRequestProperty("Content-Type", "application/json; utf-8");
                    conexion.setDoOutput(true);

                    // Se define un formateador de fechas para convertir objetos Date a cadenas en formato "yyyy-MM-dd"
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    // Se crea un objeto JSON con los datos del cliente a modificar
                    JSONObject jsonModificar = new JSONObject();
                    jsonModificar.put("id_cliente", cliente.getId());
                    jsonModificar.put("dni", cliente.getDni());
                    jsonModificar.put("nombre", cliente.getName());
                    jsonModificar.put("apellidos", cliente.getSurname());
                    jsonModificar.put("telefono", cliente.getTlf());
                    jsonModificar.put("email", cliente.getEmail());
                    jsonModificar.put("calle", cliente.getStreet());
                    jsonModificar.put("c_p", cliente.getCp());
                    jsonModificar.put("ciudad", cliente.getCiudad());

                    // Se convierten las fechas a formato de texto antes de enviarlas, evitando valores nulos
                    jsonModificar.put("fecha_nacimiento", cliente.getDate_born() != null ? dateFormat.format(cliente.getDate_born()) : JSONObject.NULL);
                    jsonModificar.put("fecha_ultima_graduacion", cliente.getDate_graduacion() != null ? dateFormat.format(cliente.getDate_graduacion()) : JSONObject.NULL);

                    // Se manejan los valores opcionales, enviando null en caso de que no tengan datos
                    jsonModificar.put("tutor_legal", cliente.getTutor() != null ? cliente.getTutor() : JSONObject.NULL);
                    jsonModificar.put("graduado", cliente.getGraduate() ? 1 : 0);
                    jsonModificar.put("tipo_lente", cliente.getTipo_lentes() != null ? cliente.getTipo_lentes() : JSONObject.NULL);
                    jsonModificar.put("test_completado", cliente.getTest_TVPS() ? 1 : 0);
                    jsonModificar.put("id_test_realizado", JSONObject.NULL); // Se deja como null si no hay un resultado disponible

                    // Se envía el objeto JSON en el cuerpo de la solicitud
                    OutputStream os = conexion.getOutputStream();
                    os.write(jsonModificar.toString().getBytes("UTF-8"));
                    os.close();

                    // Se recibe y procesa la respuesta del servidor
                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    br.close();

                    // Se retorna la respuesta obtenida del servidor
                    return response.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String response) {
                if (response != null) {
                    try {
                        // Se procesa la respuesta del servidor, interpretando los datos en formato JSON
                        JSONObject jsonResponse = new JSONObject(response);
                        String estado = jsonResponse.getString("estado");
                        String mensaje = jsonResponse.getString("mensaje");

                        // Se verifica si la modificación fue exitosa y se muestra un mensaje acorde
                        if ("correcto".equals(estado)) {
                            Toast.makeText(context, "Cliente modificado correctamente", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Error: " + mensaje, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error al procesar la respuesta", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Se muestra un mensaje si hubo un problema con la conexión al servidor
                    Toast.makeText(context, "Error en la conexión", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }*/
    @SuppressLint("StaticFieldLeak")
    public void modificarCliente(Context context, Cliente cliente, final updateCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Se establece la URL del servicio que procesará la modificación del cliente
                    URL url = new URL(BASE_URL + "db_update_2.php");
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

                    // Se configura la conexión para enviar una solicitud POST con datos en formato JSON
                    conexion.setRequestMethod("POST");
                    conexion.setRequestProperty("Content-Type", "application/json; utf-8");
                    conexion.setDoOutput(true);

                    // Se define un formateador de fechas para convertir objetos Date a cadenas en formato "yyyy-MM-dd"
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    // Se crea un objeto JSON con los datos del cliente a modificar
                    JSONObject jsonModificar = new JSONObject();
                    jsonModificar.put("id_cliente", cliente.getId());
                    jsonModificar.put("dni", cliente.getDni());
                    jsonModificar.put("nombre", cliente.getName());
                    jsonModificar.put("apellidos", cliente.getSurname());
                    jsonModificar.put("telefono", cliente.getTlf());
                    jsonModificar.put("email", cliente.getEmail());
                    jsonModificar.put("calle", cliente.getStreet());
                    jsonModificar.put("c_p", cliente.getCp());
                    jsonModificar.put("ciudad", cliente.getCiudad());

                    // Se convierten las fechas a formato de texto antes de enviarlas, evitando valores nulos
                    jsonModificar.put("fecha_nacimiento", cliente.getDate_born() != null ? dateFormat.format(cliente.getDate_born()) : JSONObject.NULL);
                    //jsonModificar.put("fecha_ultima_graduacion", cliente.getDate_graduacion() != null ? dateFormat.format(cliente.getDate_graduacion()) : JSONObject.NULL);

                    // Se manejan los valores opcionales, enviando null en caso de que no tengan datos
                    jsonModificar.put("tutor_legal", cliente.getTutor() != null ? cliente.getTutor() : JSONObject.NULL);
                    //jsonModificar.put("graduado", cliente.getGraduate() ? 1 : 0);
                   // jsonModificar.put("tipo_lente", cliente.getTipo_lentes() != null ? cliente.getTipo_lentes() : JSONObject.NULL);
                   // jsonModificar.put("test_completado", cliente.getTest_TVPS() ? 1 : 0);
                   // jsonModificar.put("id_test_realizado", JSONObject.NULL); // Se deja como null si no hay un resultado disponible

                    // Se envía el objeto JSON en el cuerpo de la solicitud
                    OutputStream os = conexion.getOutputStream();
                    os.write(jsonModificar.toString().getBytes("UTF-8"));
                    os.close();

                    // Se recibe y procesa la respuesta del servidor
                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    br.close();

                    // Se retorna la respuesta obtenida del servidor
                    return response.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String response) {
                if (response != null) {
                    try {
                        // Se procesa la respuesta del servidor, interpretando los datos en formato JSON
                        JSONObject jsonResponse = new JSONObject(response);
                        String estado = jsonResponse.getString("estado");
                        String mensaje = jsonResponse.getString("mensaje");

                        // Se verifica si la modificación fue exitosa y se muestra un mensaje acorde
                        if ("correcto".equals(estado)) {
                            Toast.makeText(context, "Cliente modificado correctamente", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Error: " + mensaje, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error al procesar la respuesta", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Se muestra un mensaje si hubo un problema con la conexión al servidor
                    Toast.makeText(context, "Error en la conexión", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void eliminarCliente(Context context, int id_cliente, final deleteCallback callback) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(BASE_URL+"db_delete.php");
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    conexion.setRequestMethod("POST");
                    conexion.setRequestProperty("Content-Type", "application/json; utf-8");
                    conexion.setDoOutput(true);

                    JSONObject jsonEliminar = new JSONObject();
                    jsonEliminar.put("id_cliente", id_cliente);

                    OutputStream os = conexion.getOutputStream();
                    os.write(jsonEliminar.toString().getBytes("UTF-8"));
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    br.close();
                    return response.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(String response) {
                if (response != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String estado = jsonResponse.getString("estado");
                        String mensaje = jsonResponse.getString("mensaje");

                        if ("correcto".equals(estado)) {
                            Toast.makeText(context, "Cliente eliminado correctamente", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error al procesar la respuesta", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Error en la conexión", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void listarDiapositivasPorTest(Context context, final int idTest, final DiapositivasCallback callback) {
        new AsyncTask<Void, Void, ArrayList<Diapositiva>>() {
            @Override
            protected ArrayList<Diapositiva> doInBackground(Void... voids) {
                ArrayList<Diapositiva> listaDiapositivas = new ArrayList<>();
                try {
                    // Construimos la URL, pasando idTest como parámetro GET
                    URL url = new URL(BASE_URL + "db_diapositivas_by_test.php?id_test=" + idTest);
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    conexion.setRequestMethod("GET");
                    conexion.setRequestProperty("Accept", "application/json");

                    // Leemos la respuesta del servidor
                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        response.append(linea);
                    }
                    br.close();

                    // Procesar el JSON
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String estado = jsonResponse.getString("estado");

                    if (estado.equals("correcto")) {
                        JSONArray datos = jsonResponse.getJSONArray("datos");
                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            // Extrae todos los campos que tengas en la tabla diapositivas
                            int id_diapositiva = obj.getInt("id_diapositiva");
                            int id_estudio = obj.getInt("id_estudio");
                            int n_diapositivas = obj.getInt("n_diapositivas");
                            boolean timer = obj.getBoolean("timer");
                            int tiempo = obj.getInt("tiempo");
                            int n_respuestas = obj.getInt("n_respuestas");
                            int respuesta_correcta = obj.getInt("respuesta_correcta");
                            String foto = obj.getString("foto");

                            // Crea el objeto Diapositiva
                            Diapositiva diapositiva = new Diapositiva(
                                    id_diapositiva,
                                    id_estudio,
                                    n_diapositivas,
                                    timer,
                                    tiempo,
                                    n_respuestas,
                                    respuesta_correcta,
                                    foto
                            );
                            listaDiapositivas.add(diapositiva);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return listaDiapositivas;
            }

            @Override
            protected void onPostExecute(ArrayList<Diapositiva> listaDiapositivas) {
                if (callback != null) {
                    callback.onDiapositivasListadas(listaDiapositivas);
                } else {
                    Log.e("Error", "Callback es null en listarDiapositivas");
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void obtenerTestRealizadoPorId(final Context context, final int idTestRealizado, final TestRealizadoCallback callback) {
        new AsyncTask<Void, Void, Test_realizado>() {
            @Override
            protected Test_realizado doInBackground(Void... voids) {
                try {
                    // 1. Construir la URL con el parámetro id_test_realizado
                    URL url = new URL(BASE_URL + "db_test_realizado.php?id_test_realizado=" + idTestRealizado);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");

                    // 2. Leer la respuesta JSON
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();

                    // 3. Parsear el JSON
                    JSONObject root = new JSONObject(sb.toString());
                    if (!"correcto".equals(root.getString("estado"))) {
                        return null;
                    }
                    JSONObject obj = root.getJSONObject("dato");

                    // 4. Formateador de fechas
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    // 5. Extraer campos
                    int idTR           = obj.getInt("id_test_realizado");
                    int idTest         = obj.getInt("id_test");

                    Date fecha = null;
                    String f1 = obj.optString("fecha", null);
                    if (f1 != null && !f1.equals("null") && !f1.isEmpty()) {
                        fecha = sdf.parse(f1);
                    }

                    Date fechaProx = null;
                    String f2 = obj.optString("fecha_proxima_revision", null);
                    if (f2 != null && !f2.equals("null") && !f2.isEmpty()) {
                        fechaProx = sdf.parse(f2);
                    }

                    int idCliente     = obj.getInt("id_cliente");
                    int idEmpleado    = obj.getInt("id_empleado");
                    String resultado  = obj.getString("resultado");

                    // 6. Instanciar y devolver
                    return new Test_realizado(
                            idTR,
                            idTest,
                            fecha,
                            fechaProx,
                            idCliente,
                            idEmpleado,
                            resultado
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Test_realizado test) {
                if (callback != null) {
                    callback.onTestRealizadoObtenido(test);
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void insertarTestRealizado(Context context, Test_realizado test, final insertarTestCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                /*
                 * Realiza la conexión HTTP en segundo plano para insertar un nuevo registro
                 * de test realizado en la base de datos remota
                 */
                try {
                    Calendar calendar = Calendar.getInstance();
                    Date fechaActual = new Date();
                    calendar.setTime(fechaActual);
                    calendar.add(Calendar.MONTH, 5);
                    Date fechaProximaRevision = calendar.getTime();

                    // Configuración de la conexión HTTP
                    URL url = new URL(BASE_URL+"db_test_realizados_insert.php");
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    conexion.setRequestMethod("POST");
                    conexion.setRequestProperty("Content-Type", "application/json; utf-8");
                    conexion.setDoOutput(true);

                    // Formateo de fechas al estándar ISO para la base de datos
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String fechaStr = dateFormat.format(fechaActual);
                    String fechaProximaStr = dateFormat.format(fechaProximaRevision);


                    // Construcción del objeto JSON con los datos del test
                    JSONObject jsonTest = new JSONObject();
                    jsonTest.put("id_test", test.getId_test());
                    jsonTest.put("fecha", fechaStr != null ? fechaStr : "");
                    jsonTest.put("fecha_proxima_revision", fechaProximaStr != null ? fechaProximaStr : "");
                    jsonTest.put("id_cliente", test.getId_cliente());
                    jsonTest.put("id_empleado", test.getId_empleado());
                    jsonTest.put("resultado", test.getResultado() != null ? test.getResultado() : "");
                    Log.d("INSERT_TEST", "Enviando datos: " + jsonTest.toString());

                    // Envío de los datos a través del stream de salida
                    OutputStream os = conexion.getOutputStream();
                    os.write(jsonTest.toString().getBytes("UTF-8"));
                    os.close();

                    // Lectura de la respuesta del servidor
                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    br.close();
                    return response.toString();

                } catch (Exception e) {
                    Log.e("INSERT_TEST", "Error al crear JSON: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String response) {
                if (response != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String estado = jsonResponse.getString("estado");

                        if ("correcto".equals(estado)) {
                            int idInsertado = jsonResponse.optInt("id_insertado", -1);
                            if (callback != null) callback.onSuccess(idInsertado);
                        } else {
                            String mensaje = jsonResponse.optString("mensaje", "Error desconocido");
                            if (callback != null) callback.onError(mensaje);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (callback != null) callback.onError("Error al procesar la respuesta del servidor");
                    }
                } else {
                    if (callback != null) callback.onError("Error de conexión");
                }
            }

        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void actualizarEstadoTestCliente(Context context, int idCliente, boolean testCompletado, int idTestRealizado, final UpdateCompletadoCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(BASE_URL + "db_update_test_completado.php");
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    conexion.setRequestMethod("POST");
                    conexion.setRequestProperty("Content-Type", "application/json; utf-8");
                    conexion.setDoOutput(true);

                    // Crear JSON con los datos a enviar
                    JSONObject json = new JSONObject();
                    json.put("id_cliente", idCliente);
                    json.put("test_completado", testCompletado ? 1 : 0);
                    json.put("id_test_realizado", idTestRealizado);

                    // Enviar JSON al servidor
                    OutputStream os = conexion.getOutputStream();
                    os.write(json.toString().getBytes("UTF-8"));
                    os.close();

                    // Leer respuesta del servidor
                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }
                    br.close();

                    return response.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String response) {
                if (response != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String estado = jsonResponse.getString("estado");
                        String mensaje = jsonResponse.getString("mensaje");

                        if ("correcto".equals(estado)) {
                            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
                            if (callback != null) callback.updateCompletadoCallback(mensaje);
                        } else {
                            Toast.makeText(context, "Error: " + mensaje, Toast.LENGTH_LONG).show();
                            if (callback != null) callback.updateCompletadoCallback("Error: " + mensaje);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error al procesar la respuesta", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Error en la conexión", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    /**
     * Obtiene un ArrayList de estudios desde el servidor.
     *
     * @param context Contexto de la aplicación
     * @param callback Callback para manejar la respuesta con la lista de estudios
     */
    @SuppressLint("StaticFieldLeak")
    public void listarEstudios(Context context, final ListarEstudiosCallback callback) {
        new AsyncTask<Void, Void, ArrayList<Estudio>>() {
            @Override
            protected ArrayList<Estudio> doInBackground(Void... voids) {
                ArrayList<Estudio> listaEstudios = new ArrayList<>();
                HttpURLConnection conexion = null;
                BufferedReader br = null;

                try {
                    URL url = new URL(BASE_URL + "db_select_estudios.php");
                    conexion = (HttpURLConnection) url.openConnection();
                    conexion.setRequestMethod("GET");
                    conexion.setRequestProperty("Accept", "application/json");

                    br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    JSONObject jsonObject = new JSONObject(response.toString());
                    String estado = jsonObject.getString("estado");

                    if (estado.equals("correcto")) {
                        JSONArray jsonListado = jsonObject.getJSONArray("datos");
                        for (int i = 0; i < jsonListado.length(); i++) {
                            JSONObject jsonEstudio = jsonListado.getJSONObject(i);

                            Estudio estudio = new Estudio(
                                    jsonEstudio.getInt("id_estudio"),
                                    jsonEstudio.getString("nombre_estudio"),
                                    jsonEstudio.getString("descripcion_instrucciones")
                            );
                            listaEstudios.add(estudio);
                        }
                    }

                } catch (Exception e) {
                    Log.e("Error", "Error al obtener los estudios", e);
                } finally {
                    if (conexion != null) {
                        conexion.disconnect();
                    }
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            Log.e("Error", "Error al cerrar BufferedReader", e);
                        }
                    }
                }
                return listaEstudios;
            }

            @Override
            protected void onPostExecute(ArrayList<Estudio> listaEstudios) {
                if (callback != null) {
                    callback.onListarEstudiosCallback(listaEstudios);
                } else {
                    Log.e("Error", "Callback es null en listarEstudios");
                    Toast.makeText(context, "Error interno: callback no definido", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


}