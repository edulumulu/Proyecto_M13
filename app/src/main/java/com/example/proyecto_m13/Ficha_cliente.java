package com.example.proyecto_m13;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Ficha_cliente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ficha_cliente);

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
    }
}