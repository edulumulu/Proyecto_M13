package Utilidades;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.proyecto_m13.Cliente;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utilidades {

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    //MÉTODOS  RELACIONADOS CON EL USO DE LISTAS DE LAS DISTINTAS CLASES DE LA APP
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

    public static String formatear_Fecha_string (Date fecha){
        String texto= "";
        if(fecha != null){
            texto =  sdf.format(fecha);
        }
        return texto;
    }

    public static Date formatear_texto_a_fecha (String fecha_texto) throws ParseException {
        Date fecha =  null;
        if(fecha_texto != null){
            fecha =  sdf.parse(fecha_texto);
        }
        return fecha;
    }


    //COMPROBACIONES
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


    //MÉTODOS RELACIONADO CON LA UTILIZACION DE LOS COMPONETTES VISUALES
    /**
     * Permite hacer visible o invisible una cantidad indeterminada de botones
     * @param mostrar
     * @param botones
     */
    public static void visibilidad_botones(boolean mostrar, Button[] botones) {
        int visibility = mostrar ? View.VISIBLE : View.GONE;

        for (Button bt : botones) {
            bt.setVisibility(visibility);
        }
    }

    /**
     * Permite hacer visible o invisible una cantidad indeterminada de Textviews
     * @param mostrar
     * @param textViews
     */
    public static void visibilidad_Textviews(boolean mostrar, TextView[] textViews) {
        int visibility = mostrar ? View.VISIBLE : View.GONE;

        for (TextView tv : textViews) {
            tv.setVisibility(visibility);
        }
    }

    /**
     * Permite hacer visible o invisible una cantidad indeterminada de EditTests
     * @param mostrar
     * @param editest
     */
    public static void visibilidad_EditTest(boolean mostrar, EditText[] editest) {
        int visibility = mostrar ? View.VISIBLE : View.GONE;

        for (TextView tv : editest) {
            tv.setVisibility(visibility);
        }
    }

    /**
     * Permite activar o desactivar una cantidad indeterminada de botones
     * @param habilitados
     * @param botones
     */
    public static void desactivar_activar_Botones(boolean habilitados, Button[] botones) {
        for (Button bt : botones) {
            bt.setEnabled(habilitados); // Habilita o deshabilita cada botón
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

}
