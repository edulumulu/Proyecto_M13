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

    @SuppressLint("StaticFieldLeak")
    public void comprobarCredenciales (Context context, String usuario, String contrasena){
        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... voids){
                try{
                    URL url = new URL("http://192.168.0.105/db_validation.php");
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

    @SuppressLint("StaticFieldLeak")
    public void listarClientes(Context context, final ClienteCallback callback) {
        new AsyncTask<Void, Void, ArrayList<Cliente>>() {
            @Override
            protected ArrayList<Cliente> doInBackground(Void... voids) {
                ArrayList<Cliente> listaClientes = new ArrayList<>();
                try {
                    URL url = new URL("http://192.168.0.105/db_select.php");
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    conexion.setRequestMethod("GET");
                    conexion.setRequestProperty("Accept", "application/json");

                    // Leer la respuesta
                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    // Procesar los datos de la respuesta JSON
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
                                e.printStackTrace();
                            }

                            Cliente cliente = new Cliente(
                                    jsonCliente.getInt("id"),
                                    jsonCliente.getString("name"),
                                    jsonCliente.getString("surname"),
                                    jsonCliente.getString("dni"),
                                    dateBorn,
                                    jsonCliente.getInt("tlf"),
                                    jsonCliente.getString("email"),
                                    jsonCliente.optString("tutor", ""),
                                    jsonCliente.getBoolean("graduate"),
                                    jsonCliente.optString("date_graduacion", ""),
                                    jsonCliente.optString("tipo_lentes", ""),
                                    jsonCliente.getBoolean("Test_TVPS"),
                                    jsonCliente.getString("street"),
                                    jsonCliente.getInt("cp"),
                                    jsonCliente.getString("ciudad")
                            );

                            listaClientes.add(cliente);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return listaClientes;
            }

            @Override
            protected void onPostExecute(ArrayList<Cliente> listaClientes) {
                // Llamamos al callback para devolver la lista de clientes
                if (callback != null) {
                    callback.onClientesListados(listaClientes);
                }else {

                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void insertarCliente(Context context, Cliente cliente) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("http://ipservidor/nombrearchivo_insertar.php");
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    conexion.setRequestMethod("POST");
                    conexion.setRequestProperty("Content-Type", "application/json; utf-8");
                    conexion.setDoOutput(true);

                    // Crear el objeto JSON con los datos del cliente
                    JSONObject jsonInsertar = new JSONObject();
                    jsonInsertar.put("nombre", cliente.getName());
                    jsonInsertar.put("apellido", cliente.getSurname());
                    jsonInsertar.put("email", cliente.getEmail());

                    // Enviar el objeto JSON como cuerpo de la solicitud
                    OutputStream os = conexion.getOutputStream();
                    os.write(jsonInsertar.toString().getBytes("UTF-8"));
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
                            Toast.makeText(context, "Cliente insertado correctamente", Toast.LENGTH_LONG).show();
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
    public void modificarCliente(Context context, Cliente cliente) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("http://ipservidor/nombrearchivo_modificar.php");
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    conexion.setRequestMethod("POST");
                    conexion.setRequestProperty("Content-Type", "application/json; utf-8");
                    conexion.setDoOutput(true);

                    // Crear el objeto JSON con los datos del cliente
                    JSONObject jsonModificar = new JSONObject();
                    //jsonModificar.put("nombre", cliente.getNombre());
                    //jsonModificar.put("apellido", cliente.getApellido());
                    jsonModificar.put("email", cliente.getEmail());

                    // Enviar el objeto JSON como cuerpo de la solicitud
                    OutputStream os = conexion.getOutputStream();
                    os.write(jsonModificar.toString().getBytes("UTF-8"));
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
                            Toast.makeText(context, "Cliente modificado correctamente", Toast.LENGTH_LONG).show();
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
    public void eliminarCliente(Context context, int id_cliente) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("http://192.168.0.105/db_delete.php");
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