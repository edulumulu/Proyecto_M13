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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GestionBBDD {
    public static final String BASE_URL = "http://192.168.1.143/";

            //"http://192.168.1.143/"
    // madre -->  http://192.168.1.106
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

            protected void onPostExecute (String response){

                if (response != null){
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        String estado = jsonResponse.getString("estado");
                        String mensaje = jsonResponse.getString("mensaje");

                        if ("correcto".equals(estado)){
                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context,Ficha_cliente.class);
                            context.startActivity(intent);
                            intent.putExtra("usuario", usuario);
                            context.startActivity(intent);

                        }else {
                            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e) {
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

    @SuppressLint("StaticFieldLeak")
    public void listarClientes(Context context, final ClienteCallback callback) {
        new AsyncTask<Void, Void, ArrayList<Cliente>>() {
            @Override
            protected ArrayList<Cliente> doInBackground(Void... voids) {
                ArrayList<Cliente> listaClientes = new ArrayList<>();
                try {
                    URL url = new URL(BASE_URL + "db_select.php");
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    conexion.setRequestMethod("GET");
                    conexion.setRequestProperty("Accept", "application/json");

                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.d("ServerResponse", "Respuesta del servidor: " + response.toString());

                    JSONObject jsonObject = new JSONObject(response.toString());
                    String estado = jsonObject.getString("estado");

                    if (estado.equals("correcto")) {
                        JSONArray jsonListado = jsonObject.getJSONArray("datos");
                        for (int i = 0; i < jsonListado.length(); i++) {
                            JSONObject jsonCliente = jsonListado.getJSONObject(i);

                            // Convertir fecha de nacimiento a Date
                            Date dateBorn = null;
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                dateBorn = sdf.parse(jsonCliente.getString("date_born"));
                            } catch (Exception e) {
                                Log.e("Error", "Fecha de nacimiento inválida: " + jsonCliente.optString("date_born"));
                            }

                            // Convertir fecha de graduación a Date, manejando valores nulos o vacíos
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

                            // Manejo de valores booleanos y conversiones seguras
                            boolean graduate = jsonCliente.optString("Test_TVPS", "0").equals("1");
                            boolean testTVPS = jsonCliente.optString("Test_TVPS", "0").equals("1");

                            // Crear objeto Cliente con los datos recibidos
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


    @SuppressLint("StaticFieldLeak")
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
                    jsonModificar.put("resultado_test", JSONObject.NULL); // Se deja como null si no hay un resultado disponible

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
}