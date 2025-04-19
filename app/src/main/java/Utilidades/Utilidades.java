package Utilidades;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_m13.Cliente;
import com.example.proyecto_m13.Ficha_cliente;
import com.example.proyecto_m13.GestionBBDD;
import com.example.proyecto_m13.Test_realizado;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Utilidades {

    /**
     * Método que devuelve un ArrayList de empleados prefijado
     *
     * @return
     */
    public static ArrayList<Cliente> cargar_lista_empleados() {
        ArrayList<Cliente> lista = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            lista.add(new Cliente(1, "Eduardo", "Lucas", "520546666K", dateFormat.parse("03/04/1987"), 123456789, "edu@gmail", "Paco", true, dateFormat.parse("10/05/2019"), "gafas", false, "Illescas 27", 28047, "Madird"));
            lista.add(new Cliente(2, "Carlos", "Herrera", "520546567L", dateFormat.parse("06/02/2019"), 123456789, "paco@gmail", "Antonio Lucas", false, null, null, false, "Valmo 27", 28047, "Barcelona"));
            lista.add(new Cliente(3, "Bibiana", "Martinez", "520546123G", dateFormat.parse("10/11/2022"), 123456789, "sara@gmail", null, true, dateFormat.parse("10/11/2022"), "lentillas", true, "Oca 27", 28047, "Lugo"));
            lista.add(new Cliente(4, "Eduardo", "Jose", "520546566K", dateFormat.parse("03/04/1987"), 123456789, "edu@gmail", "Paco", true, dateFormat.parse("10/05/2019"), "gafas", false, "Illescas 27", 28047, "Madird"));

            return lista;
        } catch (ParseException e) {

            return lista;
        }

    }

    /**
     * Método que comprba si los editText están vacios
     *
     * @param campos
     * @return
     */
    public static boolean campos_estan_vacios(EditText... campos) {
        for (EditText campo : campos) {
            if (campo.getText().toString().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Método para saber si han pasado 18 años de la fecha introducida
     *
     * @param fechaNacimiento
     * @return
     */
    public static boolean esMayorDeEdad(Date fechaNacimiento) {
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
    }

    /**
     * Método para saber si la fecha elegida corres ponde a una persona de entre 1 y 100 años
     *
     * @param fechaNacimiento
     * @return
     */
    public static boolean fecha_valida(Date fechaNacimiento) {
        Calendar cal = Calendar.getInstance();
        int añoActual = cal.get(Calendar.YEAR);
        int añoNacimiento = fechaNacimiento.getYear() + 1900; // Ajustar año de Date

        int edad = añoActual - añoNacimiento;

        // Verificar si aún no ha cumplido años en el año actual
        if (cal.get(Calendar.MONTH) < fechaNacimiento.getMonth() ||
                (cal.get(Calendar.MONTH) == fechaNacimiento.getMonth() && cal.get(Calendar.DAY_OF_MONTH) < fechaNacimiento.getDate())) {
            edad--;
        }

        return edad >= 1 && edad <= 100;
    }


    /**
     * Metodo que retorna un cliente del ArrayList a partir de su id
     *
     * @param id
     * @return
     */
    public static Cliente obtener_cliente_por_id(int id, ArrayList<Cliente> listaclientes) {
        for (Cliente cliente : listaclientes) {
            if (cliente.getId() == id) {
                return cliente; // Retorna el cliente si encuentra coincidencia
            }
        }
        return null;
    }

    /**
     * Metodo para comprobar que el nombre y apellidos o el dni no coincida con otro cliente del arraylist
     * @param id
     * @param nombre
     * @param apellido
     * @param dni
     * @param listaClientes
     * @return
     */
    public static boolean nombreYDniNoRepetidos(int id, String nombre, String apellido, String dni, ArrayList<Cliente> listaClientes) {
        for (Cliente cli : listaClientes) {
            if (cli.getId() != id) { // Permitir solo si el ID es diferente
                if (cli.getName().equalsIgnoreCase(nombre) && cli.getSurname().equalsIgnoreCase(apellido)) {
                    return false; // Ya existe alguien con ese nombre y apellido
                }
                if (cli.getDni().equalsIgnoreCase(dni)) {
                    return false; // Ya existe alguien con ese DNI
                }
            }
        }
        return true; // Si no encontró coincidencias, devuelve true
    }


    /**
     * Método que elimina un cliente del array list por id
     *
     * @param clienteId
     * @return
     */
    public static boolean eliminar_Cliente_PorId(int clienteId, ArrayList<Cliente> listaclientes) {

        boolean ok = false;
        for (int i = 0; i < listaclientes.size(); i++) {
            Cliente cliente = listaclientes.get(i);

            // Si encontramos al cliente con el ID correspondiente
            if (cliente.getId() == clienteId) {
                // Actualizar el cliente con los nuevos datos
                listaclientes.remove(i);
                ok = true;
                break;  // Salir del bucle cuando encontramos al cliente
            }
        }
        return ok;

        /*boolean ok = false;
        for(Cliente cli : lista_clientes){
            if(cli.getId() == clienteId){
                lista_clientes.remove(cli);
                ok = true;
            }
        }
        return ok;*/
    }


    public static void visibilidad_botones(boolean mostrar, Button[] botones) {
        int visibility = mostrar ? View.VISIBLE : View.GONE;

        for (Button bt : botones) {
            bt.setVisibility(visibility);
        }
    }

    public static void visibilidad_Textviews(boolean mostrar, TextView[] botones) {
        int visibility = mostrar ? View.VISIBLE : View.GONE;

        for (TextView tv : botones) {
            tv.setVisibility(visibility);
        }
    }

    public static void desactivar_activar_Botones(boolean habilitados, Button[] botones) {
        for (Button bt : botones) {
            bt.setEnabled(habilitados); // Habilita o deshabilita cada botón
        }
    }

    /**
     * Inserta un test realizado en la base de datos remota usando GestionBBDD
     *
     * @param context El contexto desde el que se llama (por ejemplo, una actividad)
     * @param idTest ID del test
     * @param idCliente ID del cliente
     * @param idEmpleado ID del empleado que realizó el test
     * @param resultado Resultado del test ("Positivo", "Negativo", etc.)
     */

   //Utilidades.llamarInsertarTestRealizado(this, 1, 2, 2, "Positivo");
    public static void llamarInsertarTestRealizado(final Context context, int idTest, int idCliente, int idEmpleado, String resultado) {
        // Crea el  objeto Test_realizado con los datos proporcionados
        Test_realizado test = new Test_realizado(idTest, idCliente, idEmpleado, resultado);



        Toast.makeText(context, "Enviando datos del test...", Toast.LENGTH_SHORT).show();
        GestionBBDD gestionBBDD = new GestionBBDD();

        // Llamar al método asíncrono para insertar el test
        gestionBBDD.insertarTestRealizado(context, test, new GestionBBDD.InsertTestCallback() {
            @Override
            public void onTestInsertado(String respuesta) {
                try {
                    JSONObject jsonResponse = new JSONObject(respuesta);
                    if (jsonResponse.getString("estado").equalsIgnoreCase("correcto")) {
                        int idInsertado = jsonResponse.getInt("id_insertado");
                        Toast.makeText(context, "Test insertado con ID: " + idInsertado, Toast.LENGTH_LONG).show();
                    } else {
                        String mensajeError = jsonResponse.optString("mensaje", "Error desconocido");
                        Toast.makeText(context, "Error al insertar test: " + mensajeError, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "Error al procesar la respuesta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(context, "Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
