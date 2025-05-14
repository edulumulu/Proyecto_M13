package Actividades;

import static Utilidades.Utilidades.visibilidad_Textviews;
import static Utilidades.Utilidades.visibilidad_botones;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import Clases.Diapositiva;
import Clases.Estudio;
import BBDD.GestionBBDD;
import com.example.proyecto_m13.R;
import Clases.Test_realizado;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Actividad_Test_TVPS extends AppCompatActivity {

    public static final String BASE_URL = "http://192.168.56.1/";
    private int id_empleado;
    private int id_cliente;
    private int edad_cliente;
    private String nombre_empleado;

    private GestionBBDD gestionBBDD = new GestionBBDD();

    private Button bt_1, bt_2, bt_3, bt_4, bt_5, bt_6, bt_7, bt_8, bt_9, bt_10, bt_11, bt_12, bt_13, bt_cambio_test;
    private ImageView iv_imagen, iv_imagen_timer;
    private ArrayList<Estudio> lista_estudios = new ArrayList<>();
    private ArrayList<Diapositiva> diapositivas = new ArrayList<>();
    private ArrayList<Diapositiva> diapositivas_parte_test = new ArrayList<>();
    private Test_realizado resultado_test_resalizado;
    private String conclusion_final;

    private TextView tv_Cambio, tv_instrucciones;
    private String[] url_fotos;
    private int cont_fallos_general, cont_fallos_1, cont_fallos_2,cont_fallos_3,cont_fallos_4,cont_fallos_5,cont_fallos_6,cont_fallos_7;
    private int cont_aciertos_general, cont_aciertos_1, cont_aciertos_2, cont_aciertos_3, cont_aciertos_4, cont_aciertos_5, cont_aciertos_6, cont_aciertos_7;

    private int fallos_permitidos = 2;
    private int tiempo_diapositiva = 1000;
    private int indice_actual = 0;
    private int contador_test_Terminados = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_tvps);

        inicializar_componentes();
        //Array de diapositivas test
        //diapositivas_De_prueba();
        datos_cliente_y_empleado();
        cargar_diapositivas_BBDD();
        cargar_estudios_BBDD();

        //mostrar_instrucciones(1);
        bt_cambio_test.setVisibility(View.VISIBLE);
        bt_cambio_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_cambio_test.setVisibility(View.GONE);
                tv_instrucciones.setVisibility(View.GONE);
                pasar_test(1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(Actividad_Test_TVPS.this, "Se ha detenido el test sin guardar los cambios", Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        Toast.makeText(Actividad_Test_TVPS.this, "Se ha detenido el test sin guardar los cambios", Toast.LENGTH_LONG).show();
        super.onStop();
    }
//CONJUNTO DE MÉTODOS QUE HACEN CONSULTAS Y MODIFICACIONES EN LA BBDD
    /**
     * Carga la lista de diapositivas con los datos de la consulta de la BBDD
     */
    private  void cargar_diapositivas_BBDD(){
        gestionBBDD.listarDiapositivasPorTest(Actividad_Test_TVPS.this, 2, new GestionBBDD.DiapositivasCallback() {
            @Override
            public void onDiapositivasListadas(ArrayList<Diapositiva> listaDiapositivas) {
                if (listaDiapositivas != null && !listaDiapositivas.isEmpty()) {
                    diapositivas.clear();
                    diapositivas.addAll(listaDiapositivas);
                    // Imprimir clientes en Log
                    for (Diapositiva diapositiva : diapositivas) {
                        Log.d("Cliente", "ID: " + diapositiva.getId_diapositiva() + ", Nombre: " + diapositiva.getId_estudio());
                    }
                    // Mostrar mensaje con el número de clientes cargados
                    Toast.makeText(Actividad_Test_TVPS.this, "Diapositivas cargadas: " + diapositivas.size(), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(Actividad_Test_TVPS.this, "No hay diapositivs disponibles", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Guarda los datos del test realizado en la BBDD
     * @param test
     */
    private void guardar_test_realizado_BBDD(Test_realizado test){
        gestionBBDD.insertarTestRealizado(Actividad_Test_TVPS.this, test, new GestionBBDD.insertarTestCallback() {
            @Override
            public void onSuccess(int idInsertado) {
                Toast.makeText(Actividad_Test_TVPS.this, "Insertado con ID: " + idInsertado, Toast.LENGTH_SHORT).show();
                actualizar_cliente_BBDD(id_cliente, idInsertado);
            }

            @Override
            public void onError(String mensajeError) {
                Toast.makeText(Actividad_Test_TVPS.this, mensajeError, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Actualiza el cliente en la base de datos
     * @param id_cliente
     * @param id_test
     */
    private void actualizar_cliente_BBDD(int id_cliente, int id_test){
        gestionBBDD.actualizarEstadoTestCliente(Actividad_Test_TVPS.this, id_cliente, true, id_test, new GestionBBDD.UpdateCompletadoCallback() {
            @Override
            public void updateCompletadoCallback(String respuesta) {
            }
        });
    }

    /**
     * Carga la lista de estudios de la BBDD
     */
    private void cargar_estudios_BBDD(){
        gestionBBDD.listarEstudios(Actividad_Test_TVPS.this, new GestionBBDD.ListarEstudiosCallback() {
            @Override
            public void onListarEstudiosCallback(ArrayList<Estudio> estudios) {
                if (estudios != null && !estudios.isEmpty()) {
                    lista_estudios.clear();
                    lista_estudios.addAll(estudios);
                    // Imprimir clientes en Log
                    for (Estudio estudio : lista_estudios) {
                        Log.d("Estudio", "ID: " + estudio.getIdEstudio()  + ", Instrucciones: " + estudio.getDescripcionInstrucciones());
                    }
                    // Mostrar mensaje con el número de clientes cargados
                    Toast.makeText(Actividad_Test_TVPS.this , "Estudios cargados: " + lista_estudios.size(), Toast.LENGTH_LONG).show();
                    mostrar_instrucciones(1);
                    bt_cambio_test.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(Actividad_Test_TVPS.this, "No hay clientes disponibles", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    //METODOES QUE INTERACTUAN CON LOS ELEMENTOS DE LA ACTIVIDAD: BOTONES, IMÁGENES, ACCIONES, TEXTVIEWS, ETC
    /**
     * Método que inicia la parte del test que se solicita como parámetro, siendo este un entero
     * @param parte_test
     */
    private void pasar_test(int parte_test){
        iv_imagen.setVisibility(View.VISIBLE);

        diapositivas_parte_test = diapositivas_test_parte(parte_test);
        Log.d("TEST", "Parte: " + parte_test + " | Diapositivas cargadas: " + diapositivas_parte_test.size());
        url_fotos = obtener_url_fotos_parte_test(parte_test);
        Log.d("TEST", "Parte: " + parte_test + " | URLs cargadas: " + url_fotos.length);

        indice_actual = 0; // RESETEO ASEGURADO

        // Solo muestro la primera diapositiva
        mostrar_diapositiva_y_botones(diapositivas_parte_test, indice_actual, parte_test);

        // Listener para botones cuando la diapositiva no tiene  timer
        View.OnClickListener answerListener = view -> {
            int boton_presionado = Integer.parseInt(((Button) view).getText().toString());
            Log.d("Respuesta", "Usuario eligió: " + boton_presionado);

            //Conteo fallos y  aciertos
            if (boton_presionado == diapositivas_parte_test.get(indice_actual).getRespuesta_correcta()) {
                cont_aciertos_general++;
            } else {
                cont_fallos_general++;
            }

            indice_actual++;     //Aumento el indice

            //Compruebo que si hay mas imagenes en el test y que no se han superado la cantidad de errores permitida para pasar la proxima parte o filaizar el test
            if (indice_actual < url_fotos.length && cont_fallos_general < fallos_permitidos) {
                iv_imagen.setImageBitmap(null);     // seteo el contenedor de imagenes a null
                mostrar_diapositiva_y_botones(diapositivas_parte_test, indice_actual, parte_test);
            } else {
                finalizar_test(parte_test);
                //diapositivas_parte_test.clear();
            }
        };

        // Asigno los listeners a todos los botones
        Button[] botones = new Button[]{bt_1, bt_2, bt_3, bt_4, bt_5, bt_6, bt_7, bt_8, bt_9, bt_10, bt_11, bt_12, bt_13};
        for (Button b : botones) b.setOnClickListener(answerListener);
    }

    /**
     * Metodo encargado de selecionar la cantidad de botones y su situación en pantalla según la diapositiva que tiene que mostrar
     * @param diapositivas
     * @param indice
     * @param parte_test
     */
    private void mostrar_diapositiva_y_botones(ArrayList<Diapositiva> diapositivas, int indice, int parte_test) {
        //Comprueo que el indice pasado no supera la cantidad de diapositivas de la parte del test sobre la que trabaja
        if (indice >= diapositivas.size()) { finalizar_test(parte_test); return; }

        //Hago visible el el contenedor de imagen con respuesta e invisible el de las que tienen timer y hago invisibles todos los botones
        iv_imagen.setVisibility(View.VISIBLE);
        iv_imagen_timer.setVisibility(View.GONE);
        visibilidad_botones(false, new Button[]{bt_1, bt_2, bt_3,bt_4, bt_5, bt_6, bt_7,bt_8, bt_9, bt_10, bt_11,bt_12, bt_13});

        //Creo una variable diapositiva con la información de la diapositiva actual y estraigo su id-estudio y su número de respuestas
        Diapositiva actual = diapositivas.get(indice);
        int estudio = actual.getId_estudio();
        int n_respuestas = actual.getN_respuestas();

        //Selecciono los botones dependiendo de la parte del test que se esté pasando
        if (estudio == 1 || estudio == 3) {
            cargar_imagen(iv_imagen, url_fotos[indice]);
            visibilidad_botones(true, new Button[]{bt_1, bt_2, bt_3, bt_4, bt_5});

        } else if (estudio == 2 && n_respuestas == 4) {
            cargar_imagen(iv_imagen, url_fotos[indice]);
            visibilidad_botones(true, new Button[]{bt_6, bt_7, bt_8, bt_9});
        } else if (estudio == 2 && n_respuestas == 0) {
            // Mostrar imagen con timer y pasados los segundos correspondientes desaparece
            cargar_imagen(iv_imagen_timer, url_fotos[indice]);
            iv_imagen_timer.setVisibility(View.VISIBLE);
            iniciar_temporizador(tiempo_diapositiva, parte_test);

        } else if (estudio == 4) {
            cargar_imagen(iv_imagen, url_fotos[indice]);
            if (n_respuestas == 5) {
                visibilidad_botones(true, new Button[]{bt_1, bt_2, bt_3, bt_4, bt_5});
            } else if (n_respuestas == 4) {
                visibilidad_botones(true, new Button[]{bt_6, bt_7, bt_8, bt_9});
            }

        } else if (estudio == 5 && n_respuestas == 4) {
            cargar_imagen(iv_imagen, url_fotos[indice]);
            visibilidad_botones(true, new Button[]{bt_10, bt_11, bt_12, bt_13});

        } else if (estudio == 5 && n_respuestas == 0) {
            // igual que la parte 2
            cargar_imagen(iv_imagen_timer, url_fotos[indice]);
            iv_imagen_timer.setVisibility(View.VISIBLE);
            iniciar_temporizador(tiempo_diapositiva, parte_test);

        } else if (estudio == 6 || estudio == 7) {
            cargar_imagen(iv_imagen, url_fotos[indice]);
            visibilidad_botones(true, new Button[]{bt_6, bt_7, bt_8, bt_9});
        }
    }

    /**
     * Método que hace desaparecer la imagen actual pasados los segundos correspondientes
     * @param tiempo
     * @param parte_test
     */
    private void iniciar_temporizador(int tiempo, int parte_test){
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            iv_imagen_timer.setVisibility(View.GONE);
            iv_imagen_timer.setImageBitmap(null);
            indice_actual++;

            if (indice_actual < url_fotos.length && cont_fallos_general < 4) {
                mostrar_diapositiva_y_botones(diapositivas_parte_test, indice_actual, parte_test);
            } else {
                finalizar_test(parte_test);
            }
        }, tiempo);
    }

    /**
     * Método que se encarga de guardar los aciertos y fallos de la parte del test correspondiente
     * y que habilita que se pueda pasar la siguiente parte del test tocando la pantalla
     * @param parte_test
     */
    private void finalizar_test(int parte_test){
        indice_actual = 0;

        //Toast de comprobacion se pueden comentar
        Toast.makeText(this, "ACIERTOS --> " +cont_aciertos_general + " /  FALLOS --> "+ cont_fallos_general, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Fin del test", Toast.LENGTH_SHORT).show();

        contador_fallos(parte_test);
        contador_aciertos(parte_test);
        visibilidad_botones(false, new Button[]{bt_1, bt_2, bt_3,bt_4, bt_5, bt_6, bt_7,bt_8, bt_9, bt_10, bt_11,bt_12, bt_13});
        iv_imagen.setVisibility(View.GONE);

        //Si no se han superado las 7 partes del test se pasa a la siguiente parte si no se muestra mensaje de finalizacion
        if (parte_test < 7){
            mostrar_instrucciones (parte_test +1);
            bt_cambio_test.setVisibility(View.VISIBLE);
        }else {
            bt_cambio_test.setVisibility(View.VISIBLE);
            tv_Cambio.setVisibility(View.VISIBLE);
            tv_Cambio.setText("Finalizaste el test\n\nToca la pantalla para finalizar");
        }

        /**
         * Boton que habilita  mostrar el sigiente test o finalizar la actividad guardando los resultados
         */
        bt_cambio_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Si no hemos llegado al final de todos los tests
                contador_test_Terminados++;
                Toast.makeText(Actividad_Test_TVPS.this, "Test terminados --> "+contador_test_Terminados, Toast.LENGTH_SHORT).show();
                bt_cambio_test.setVisibility(View.GONE);
                if(contador_test_Terminados < 7){
                    tv_instrucciones.setVisibility(View.GONE);
                    bt_cambio_test.setVisibility(View.GONE);
                    iv_imagen.setImageBitmap(null);
                    pasar_test(contador_test_Terminados+1);

                }else{
                    tv_Cambio.setVisibility(View.GONE);
                    conclusion_final = contrastar_resultados();
                    resultado_test_resalizado = new Test_realizado(2,id_cliente,id_empleado,conclusion_final);

                   guardar_test_realizado_BBDD(resultado_test_resalizado);
                    //actualizar_cliente_BBDD(id_cliente, id_test_Realizado);

                    AlertDialog.Builder builder = new AlertDialog.Builder(Actividad_Test_TVPS.this);
                    builder.setTitle("Resultados Finales del Test")
                            .setMessage(resultado_test_resalizado.getResultado())
                            .setPositiveButton("Aceptar", (dialog, which) -> {
                                dialog.dismiss();

                                Intent intent = new Intent(Actividad_Test_TVPS.this, Ficha_cliente.class);
                                intent.putExtra("idCliente", id_cliente);
                                intent.putExtra("usuario", nombre_empleado);

                                startActivity(intent);
                                finish();
                            })
                            .setCancelable(false)
                            .show();
                }


            }
        });
    }

    /**
     * Compara los aciertos del cliente segun su edad y devuelve un String con la valoración de cada parte del text y una recomendación final
     * @return
     */
    private String contrastar_resultados() {
        String conclusion;

        String parte1 = "No hay resultados";
        String parte2 = "No hay resultados";
        String parte3 = "No hay resultados";
        String parte4 = "No hay resultados";
        String parte5 = "No hay resultados";
        String parte6 = "No hay resultados";
        String parte7 = "No hay resultados";

        int contador_aciertos_total = cont_aciertos_1 + cont_aciertos_2 +cont_aciertos_3 + cont_aciertos_4 +cont_aciertos_5 + cont_aciertos_6 +cont_aciertos_7;
        String valoracion = null;
        boolean excelencia=false;
        int contador = 0;

        /**
         * Según la edad :
         * - Obtiene una frase que estipula el grado de satisfacción de la parte del test realizado
         * - Contabiliza la cantidad de aciertos y si no supera los minimos establecidos aumenta el contador de eficiencia
         * - Finalmente constrastando esa eficiencia llega a una conclusión general
         * Toda esta información finalmente lo recoge un string que es devuelto
         */
        if (edad_cliente < 6) {
            parte1 = resultado_ponderado_con_la_media(cont_aciertos_1, 2, 4);
            parte2 = resultado_ponderado_con_la_media(cont_aciertos_2, 2, 4);
            parte3 = resultado_ponderado_con_la_media(cont_aciertos_3, 2, 4);
            parte4 = resultado_ponderado_con_la_media(cont_aciertos_4, 2, 4);
            parte5 = resultado_ponderado_con_la_media(cont_aciertos_5, 2, 4);
            parte6 = resultado_ponderado_con_la_media(cont_aciertos_6, 2, 4);
            parte7 = resultado_ponderado_con_la_media(cont_aciertos_7, 2, 4);

           contador = contador_critico_de_fallos(2);

            if(contador_aciertos_total >= 4*7){excelencia = true;}

        } else if (edad_cliente >= 6 && edad_cliente < 10) {

            parte1 = resultado_ponderado_con_la_media(cont_aciertos_1, 3, 5);
            parte2 = resultado_ponderado_con_la_media(cont_aciertos_2, 3, 5);
            parte3 = resultado_ponderado_con_la_media(cont_aciertos_3, 3, 5);
            parte4 = resultado_ponderado_con_la_media(cont_aciertos_4, 3, 5);
            parte5 = resultado_ponderado_con_la_media(cont_aciertos_5, 3, 5);
            parte6 = resultado_ponderado_con_la_media(cont_aciertos_6, 3, 5);
            parte7 = resultado_ponderado_con_la_media(cont_aciertos_7, 3, 5);

            contador = contador_critico_de_fallos(3);

            if(contador_aciertos_total >= 6*7){excelencia = true;}

        } else if (edad_cliente >= 10 && edad_cliente < 13) {

            parte1 = resultado_ponderado_con_la_media(cont_aciertos_1, 4, 7);
            parte2 = resultado_ponderado_con_la_media(cont_aciertos_2, 4, 7);
            parte3 = resultado_ponderado_con_la_media(cont_aciertos_3, 4, 7);
            parte4 = resultado_ponderado_con_la_media(cont_aciertos_4, 4, 7);
            parte5 = resultado_ponderado_con_la_media(cont_aciertos_5, 4, 7);
            parte6 = resultado_ponderado_con_la_media(cont_aciertos_6, 4, 7);
            parte7 = resultado_ponderado_con_la_media(cont_aciertos_7, 4, 7);

            contador = contador_critico_de_fallos(4);

            if(contador_aciertos_total >= 8*7){excelencia = true;}

        } else if (edad_cliente >= 13 ) {

            parte1 = resultado_ponderado_con_la_media(cont_aciertos_1, 5, 9);
            parte2 = resultado_ponderado_con_la_media(cont_aciertos_2, 5, 9);
            parte3 = resultado_ponderado_con_la_media(cont_aciertos_3, 5, 9);
            parte4 = resultado_ponderado_con_la_media(cont_aciertos_4, 5, 9);
            parte5 = resultado_ponderado_con_la_media(cont_aciertos_5, 5, 9);
            parte6 = resultado_ponderado_con_la_media(cont_aciertos_6, 5, 9);
            parte7 = resultado_ponderado_con_la_media(cont_aciertos_7, 5, 9);

            contador = contador_critico_de_fallos(5);

            if(contador_aciertos_total >= 9*7){excelencia = true;}
        }

        valoracion = valoracion_individual(excelencia, contador);

        conclusion = "Gráfica general:\n\n"
                + "Discriminación visual --> " + parte1 +"\n"
                + "Memoria visual --> "+ parte2 +"\n"
                + "Relacción espacial --> " + parte3 +"\n"
                + "Constancia de la forma --> "+ parte4 +"\n"
                + "memoria secuencial --> " + parte5 +"\n"
                + "Figura de fondo --> "+ parte6 +"\n"
                + "Completado visual --> " + parte7 +"\n\n"
                +"VALORACIÓN: "+ valoracion;

        return conclusion;
    }

    /**
     * Aumenta el contador de test que e considera que la respuesta es muy deficiente. Devuelve un int.
     * @param aciertos
     * @return
     */
    private int contador_critico_de_fallos(int aciertos){
        int contador = 0;

        if(cont_aciertos_1<aciertos){ contador++;}
        if(cont_aciertos_2<aciertos){ contador++;}
        if(cont_aciertos_3<aciertos){ contador++;}
        if(cont_aciertos_4<aciertos){ contador++;}
        if(cont_aciertos_5<aciertos){ contador++;}
        if(cont_aciertos_6<aciertos){ contador++;}
        if(cont_aciertos_7<aciertos){ contador++;}

        Log.d("TVPS_Debug", "Número de test por debajo del mínimo (" + aciertos + "): " + contador);
        return contador;
    }

    /**
     * Devuelve un String con la recomendación clínica según las partes del test que tiene desarrolladas de forma deficiente
     * @param contador
     * @return
     */
    private String valoracion_individual(boolean excelencia, int contador){
        String valoracion = "";

        if (!excelencia){
            switch (contador){
                case 1:
                    valoracion = "Tiene un área poco desarrollada, se puede prestar atención en mejorarla pero no es relevante";
                    break;
                case 2:
                    valoracion = "Tiene dos áreas poco desarrolladas, se recomienda prestar atención en mejorarlas";
                    break;
                case 3:
                    valoracion = "Tiene tres áreas poco desarrolladas, se recomienda prestar trabajo en casa específico para mejorarlas";
                    break;
                case 4:
                    valoracion = "Tiene cuatro áreas poco desarrolladas, se recomienda trabajar con un docente en horario estraescolar";
                    break;
                case 5:
                    valoracion = "Tiene cinco áreas poco desarrolladas, se recomienda trabajar con un docente en horario estraescolar";
                    break;
                case 6:
                    valoracion = "Tiene seis áreas poco desarrolladas, Es necesario trabajar con un especialista";
                    break;
                case 7:
                    valoracion = "Tiene siete áreas poco desarrolladas. el retraso madurativo es elevado, se necesita trabajo con un especialista";
                    break;
            }
        }else{
            valoracion = "El cómputo de aciertos es extraordinario, su percepción visual es excelente";
        }
        return valoracion;
    }

    /**
     * Devuelve un String que concreta si esta por encima, por debajo o con la media de su edad
     * @param contador
     * @param minimo
     * @param maximo
     * @return
     */
    private String resultado_ponderado_con_la_media(int contador, int minimo, int maximo){
        Log.d("TVPS_Debug", "Test: " + contador + " aciertos. Mínimo: " + minimo + ", Máximo: " + maximo);

        if(contador < minimo){
            return "Está por debajo de la media";
        }else if(contador >=minimo && contador < maximo){
            return "Dentro de los valores normales";
        }else {
            return "Sobresale de la media";
        }
    }

    /**
     * Guarda los fallos realizados en el test en la variable correspondiente a cada parte
     * @param parte_test
     */
    public void contador_fallos(int parte_test) {
        int valor = cont_fallos_general;
        cont_fallos_general = 0;

        switch (parte_test) {
            case 1: cont_fallos_1 = valor; break;
            case 2: cont_fallos_2 = valor; break;
            case 3: cont_fallos_3 = valor; break;
            case 4: cont_fallos_4 = valor; break;
            case 5: cont_fallos_5 = valor; break;
            case 6: cont_fallos_6 = valor; break;
            case 7: cont_fallos_7 = valor; break;
            default: break;
        }
    }

    /**
     * Guarda los aciertos conseguidos en el test en la variable correspondiente a cada parte
     * @param parte_test
     */
    public void contador_aciertos(int parte_test) {
        int valor = cont_aciertos_general;
        cont_aciertos_general = 0;

        switch (parte_test) {
            case 1: cont_aciertos_1 = valor; break;
            case 2: cont_aciertos_2 = valor; break;
            case 3: cont_aciertos_3 = valor; break;
            case 4: cont_aciertos_4 = valor; break;
            case 5: cont_aciertos_5 = valor; break;
            case 6: cont_aciertos_6 = valor; break;
            case 7: cont_aciertos_7 = valor; break;
            default: break;
        }
    }

    /**
     * Muestra las instrucciones según el test que se va a realizar
     * @param parte_Test
     */
    public void mostrar_instrucciones(int parte_Test){
        tv_instrucciones.setVisibility(View.VISIBLE);
        boolean encontrado = false;

        for (Estudio est : lista_estudios) {
            if (est.getIdEstudio() == parte_Test) {
                tv_instrucciones.setText("Test " + est.getIdEstudio() + "\n" + est.getDescripcionInstrucciones() + "\n\nPara comenzar el test toca la pantalla");
                encontrado = true;
                break; // ✅ sal del bucle cuando lo encuentres
            }
        }

        if (!encontrado) {
            //tv_instrucciones.setText("No se encontró el estudio con ID: " + parte_Test);
            Log.e("mostrar_instrucciones", "No se encontró el estudio con ID: " + parte_Test);
        }
    }

    /**
     * AsyncTask para descargar la imagen en segundo plano
     */
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }

    /**
     * Obtinee un Array de String con las url de las diapositivas de dicha parte del test
     * @param parte_test
     * @return
     */
    public String[] obtener_url_fotos_parte_test(int parte_test){
        ArrayList<String> urls = new ArrayList<>();
        for (Diapositiva diapo : diapositivas){
            if(diapo.getId_estudio() == parte_test){
                urls.add(BASE_URL+ diapo.getFoto());
            }
        }
        return urls.toArray(new String[0]);
    }

    /**
     * Retorna un ArrayList con las diapositivas que pertenencen a una parte del test
     * @param parte_test
     * @return
     */
    private ArrayList<Diapositiva> diapositivas_test_parte (int parte_test){
        ArrayList<Diapositiva> diapositivas_parte_test=new ArrayList<>();
        for (Diapositiva diapo : diapositivas){
            if(diapo.getId_estudio() == parte_test){
                diapositivas_parte_test.add(diapo);
            }
        }
        return diapositivas_parte_test;
    }

    /**
     * Carga la imagen en el ImageView facilitado con la Url pasada por parámetro
     * @param imageView
     * @param url
     */
    private void cargar_imagen( ImageView imageView, String url) {
        new DownloadImageTask(imageView).execute(url);
    }

    /**
     * Inicia los componentes de la actividad y los deja invisibles hasta nueva orden
     */
    private void inicializar_componentes(){
        iv_imagen = findViewById(R.id.iv_imagen);
        iv_imagen_timer = findViewById(R.id.iv_imagen_timer);
        tv_instrucciones = findViewById(R.id.tv_instrucciones);
        tv_Cambio = findViewById(R.id.tv_cambio);

        bt_1 = findViewById(R.id.bt_1);
        bt_2 = findViewById(R.id.bt_2);
        bt_3 = findViewById(R.id.bt_3);
        bt_4 = findViewById(R.id.bt_4);
        bt_5 = findViewById(R.id.bt_5);
        bt_6 = findViewById(R.id.bt_6);
        bt_7 = findViewById(R.id.bt_7);
        bt_8 = findViewById(R.id.bt_8);
        bt_9 = findViewById(R.id.bt_9);
        bt_10 = findViewById(R.id.bt_10);
        bt_11 = findViewById(R.id.bt_11);
        bt_12 = findViewById(R.id.bt_12);
        bt_13 = findViewById(R.id.bt_13);
        bt_cambio_test = findViewById(R.id.bt_cambio_test);

        visibilidad_botones(false, new Button[]{bt_1, bt_2, bt_3,bt_4, bt_5, bt_6, bt_7,bt_8, bt_9, bt_10, bt_11,bt_12, bt_13, bt_cambio_test });
        iv_imagen_timer.setVisibility(View.GONE);
        tv_Cambio.setVisibility(View.GONE);
        visibilidad_Textviews(false, new TextView[]{tv_instrucciones, tv_Cambio});
    }


    /**
     * Recoge las valores de la actividad anterior: idCliente, idEmpleado y edadCliente y los guarda en variables generales
     */
    private void datos_cliente_y_empleado(){
        id_cliente = getIntent().getIntExtra ("idCliente", -1);
        id_empleado = getIntent().getIntExtra("idEmpleado", -1);
        edad_cliente = getIntent().getIntExtra("edadCliente", -1);
        nombre_empleado = getIntent().getStringExtra("usuario");
        Toast.makeText(Actividad_Test_TVPS.this, "Edad cliente --> " +edad_cliente , Toast.LENGTH_SHORT).show();
    }



    //MÉTODOS DE PRUEBA PREVIOS AL FUNCIONAMIENTO DE LAS CONSULTAS EN LA BASE DE DATOS
    public void diapositivas_De_prueba(){

        //"http://192.168.1.143/"    casa
        // madre "http://192.168.1.106/"
        String ip= "http://192.168.1.143/"; //casa
        //String ip= "http://192.168.1.106/"; // Mama
        Diapositiva diapositiva1 = new Diapositiva(1, 1, 1, false, 0,5, 1, ip + "imagenes/1_01.png");
        Diapositiva diapositiva2 = new Diapositiva(2, 1, 2, false, 0,5, 1, ip + "imagenes/1_02.png");
        Diapositiva diapositiva3 = new Diapositiva(3, 1, 3, false, 0,5, 1, ip + "imagenes/1_03.png");
        Diapositiva diapositiva4 = new Diapositiva(4, 1, 4, false, 0,5, 1, ip + "imagenes/1_04.png");
        Diapositiva diapositiva5 = new Diapositiva(5, 1, 5, false, 0,5, 1, ip + "imagenes/1_05.png");
        Diapositiva diapositiva6 = new Diapositiva(6, 1, 6, false, 0,5, 1, ip + "imagenes/1_06.png");
        Diapositiva diapositiva7 = new Diapositiva(7, 1, 7, false, 0,5, 1, ip + "imagenes/1_07.png");
        Diapositiva diapositiva8 = new Diapositiva(8, 1, 8, false, 0,5, 1, ip + "imagenes/1_08.png");
        Diapositiva diapositiva9 = new Diapositiva(9, 1, 9, false, 0,5, 1, ip + "imagenes/1_09.png");
        Diapositiva diapositiva10 = new Diapositiva(10, 1, 10, false, 0,5, 1, ip + "imagenes/1_10.png");

        Diapositiva diapositiva11 = new Diapositiva(11, 2, 1, true, 0,0, 0, ip + "imagenes/2_01.png");
        Diapositiva diapositiva12 = new Diapositiva(12, 2, 2, false, 0,4, 1, ip + "imagenes/2_02.png");
        Diapositiva diapositiva13 = new Diapositiva(13, 2, 3, true, 0,0, 0, ip + "imagenes/2_03.png");
        Diapositiva diapositiva14 = new Diapositiva(14, 2, 4, false, 0,4, 1, ip + "imagenes/2_04.png");
        Diapositiva diapositiva15 = new Diapositiva(15, 2, 5, true, 0,0, 0, ip + "imagenes/2_05.png");
        Diapositiva diapositiva16 = new Diapositiva(16, 2, 6, false, 0,4, 1, ip + "imagenes/2_06.png");
        Diapositiva diapositiva17 = new Diapositiva(17, 2, 7, true, 0,0, 0, ip + "imagenes/2_07.png");
        Diapositiva diapositiva18 = new Diapositiva(18, 2, 8, false, 0,4, 1, ip + "imagenes/2_08.png");
        Diapositiva diapositiva19 = new Diapositiva(19, 2, 9, true, 0,0, 0, ip + "imagenes/2_09.png");
        Diapositiva diapositiva20 = new Diapositiva(20, 2, 10, false, 0,4, 1, ip + "imagenes/2_10.png");
        Diapositiva diapositiva21 = new Diapositiva(21, 2, 11, true, 0,0, 0, ip + "imagenes/2_11.png");
        Diapositiva diapositiva22 = new Diapositiva(22, 2, 12, false, 0,4, 1, ip + "imagenes/2_12.png");
        Diapositiva diapositiva23 = new Diapositiva(23, 2, 13, true, 0,0, 0, ip + "imagenes/2_13.png");
        Diapositiva diapositiva24 = new Diapositiva(24, 2, 14, false, 0,4, 1, ip + "imagenes/2_14.png");
        Diapositiva diapositiva25 = new Diapositiva(25, 2, 15, true, 0,0, 0, ip + "imagenes/2_15.png");
        Diapositiva diapositiva26 = new Diapositiva(26, 2, 16, false, 0,4, 1, ip + "imagenes/2_16.png");
        Diapositiva diapositiva27 = new Diapositiva(27, 2, 17, true, 0,0, 0, ip + "imagenes/2_17.png");
        Diapositiva diapositiva28 = new Diapositiva(28, 2, 18, false, 0,4, 1, ip + "imagenes/2_18.png");
        Diapositiva diapositiva29 = new Diapositiva(29, 2, 19, true, 0,0, 0, ip + "imagenes/2_19.png");
        Diapositiva diapositiva30 = new Diapositiva(30, 2, 20, false, 0,4, 1, ip + "imagenes/2_20.png");

        Diapositiva diapositiva31 = new Diapositiva(31, 3, 1, false, 0,5, 1, ip + "imagenes/3_01.png");
        Diapositiva diapositiva32 = new Diapositiva(32, 3, 2, false, 0,5, 1, ip + "imagenes/3_02.png");
        Diapositiva diapositiva33 = new Diapositiva(33, 3, 3, false, 0,5, 1, ip + "imagenes/3_03.png");
        Diapositiva diapositiva34 = new Diapositiva(34, 3, 4, false, 0,5, 1, ip + "imagenes/3_04.png");
        Diapositiva diapositiva35 = new Diapositiva(35, 3, 5, false, 0,5, 1, ip + "imagenes/3_05.png");
        Diapositiva diapositiva36 = new Diapositiva(36, 3, 6, false, 0,5, 1, ip + "imagenes/3_06.png");
        Diapositiva diapositiva37 = new Diapositiva(37, 3, 7, false, 0,5, 1, ip + "imagenes/3_07.png");
        Diapositiva diapositiva38 = new Diapositiva(38, 3, 8, false, 0,5, 1, ip + "imagenes/3_08.png");
        Diapositiva diapositiva39 = new Diapositiva(39, 3, 9, false, 0,5, 1, ip + "imagenes/3_09.png");
        Diapositiva diapositiva40 = new Diapositiva(40, 3, 10, false, 0,5, 1, ip + "imagenes/3_10.png");

        Diapositiva diapositiva41 = new Diapositiva(41, 4, 1, false, 0,5, 1, ip + "imagenes/4_11.png");
        Diapositiva diapositiva42 = new Diapositiva(42, 4, 2, false, 0,4, 1, ip + "imagenes/4_12.png");
        Diapositiva diapositiva43 = new Diapositiva(43, 4, 3, false, 0,4, 1, ip + "imagenes/4_13.png");
        Diapositiva diapositiva44 = new Diapositiva(44, 4, 4, false, 0,5, 1, ip + "imagenes/4_14.png");
        Diapositiva diapositiva45 = new Diapositiva(45, 4, 5, false, 0,4, 1, ip + "imagenes/4_15.png");
        Diapositiva diapositiva46 = new Diapositiva(46, 4, 6, false, 0,4, 1, ip + "imagenes/4_16.png");
        Diapositiva diapositiva47 = new Diapositiva(47, 4, 7, false, 0,4, 1, ip + "imagenes/4_17.png");
        Diapositiva diapositiva48 = new Diapositiva(48, 4, 8, false, 0,4, 1, ip + "imagenes/4_18.png");
        Diapositiva diapositiva49 = new Diapositiva(49, 4, 9, false, 0,4, 1, ip + "imagenes/4_19.png");
        Diapositiva diapositiva50 = new Diapositiva(50, 4, 10, false, 0,4, 1, ip + "imagenes/4_20.png");

        Diapositiva diapositiva51 = new Diapositiva(51, 5, 1, true, 0,0, 0, ip + "imagenes/5_01.png");
        Diapositiva diapositiva52 = new Diapositiva(52, 5, 2, false, 0,4, 1, ip + "imagenes/5_02.png");
        Diapositiva diapositiva53 = new Diapositiva(53, 5, 3, true, 0,0, 0, ip + "imagenes/5_03.png");
        Diapositiva diapositiva54 = new Diapositiva(54, 5, 4, false, 0,4, 1, ip + "imagenes/5_04.png");
        Diapositiva diapositiva55 = new Diapositiva(55, 5, 5, true, 0,0, 0, ip + "imagenes/5_05.png");
        Diapositiva diapositiva56 = new Diapositiva(56, 5, 6, false, 0,4, 1, ip + "imagenes/5_06.png");
        Diapositiva diapositiva57 = new Diapositiva(57, 5, 7, true, 0,0, 0, ip + "imagenes/5_07.png");
        Diapositiva diapositiva58 = new Diapositiva(58, 5, 8, false, 0,4, 1, ip + "imagenes/5_08.png");
        Diapositiva diapositiva59 = new Diapositiva(59, 5, 9, true, 0,0, 0, ip + "imagenes/5_09.png");
        Diapositiva diapositiva60 = new Diapositiva(60, 5, 10, false, 0,4, 1, ip + "imagenes/5_10.png");
        Diapositiva diapositiva61 = new Diapositiva(61, 5, 11, true, 0,0, 0, ip + "imagenes/5_11.png");
        Diapositiva diapositiva62 = new Diapositiva(62, 5, 12, false, 0,4, 1, ip + "imagenes/5_12.png");
        Diapositiva diapositiva63 = new Diapositiva(63, 5, 13, true, 0,0, 0, ip + "imagenes/5_13.png");
        Diapositiva diapositiva64 = new Diapositiva(64, 5, 14, false, 0,4, 1, ip + "imagenes/5_14.png");
        Diapositiva diapositiva65 = new Diapositiva(65, 5, 15, true, 0,0, 0, ip + "imagenes/5_15.png");
        Diapositiva diapositiva66 = new Diapositiva(66, 5, 16, false, 0,4, 1, ip + "imagenes/5_16.png");
        Diapositiva diapositiva67 = new Diapositiva(67, 5, 17, true, 0,0, 0, ip + "imagenes/5_17.png");
        Diapositiva diapositiva68 = new Diapositiva(68, 5, 18, false, 0,4, 1, ip + "imagenes/5_18.png");
        Diapositiva diapositiva69 = new Diapositiva(69, 5, 19, true, 0, 0, 0, ip + "imagenes/5_19.png");
        Diapositiva diapositiva70 = new Diapositiva(70, 5, 20, false, 0,4, 1, ip + "imagenes/5_20.png");

        Diapositiva diapositiva71 = new Diapositiva(71, 6, 1, false, 0,4, 1, ip + "imagenes/6_01.png");
        Diapositiva diapositiva72 = new Diapositiva(72, 6, 2, false, 0,4, 1, ip + "imagenes/6_02.png");
        Diapositiva diapositiva73 = new Diapositiva(73, 6, 3, false, 0,4, 1, ip + "imagenes/6_03.png");
        Diapositiva diapositiva74 = new Diapositiva(74, 6, 4, false, 0,4, 1, ip + "imagenes/6_04.png");
        Diapositiva diapositiva75 = new Diapositiva(75, 6, 5, false, 0,4, 1, ip + "imagenes/6_05.png");
        Diapositiva diapositiva76 = new Diapositiva(76, 6, 6, false, 0,4, 1, ip + "imagenes/6_06.png");
        Diapositiva diapositiva77 = new Diapositiva(77, 6, 7, false, 0,4, 1, ip + "imagenes/6_07.png");
        Diapositiva diapositiva78 = new Diapositiva(78, 6, 8, false, 0,4, 1, ip + "imagenes/6_08.png");
        Diapositiva diapositiva79 = new Diapositiva(79, 6, 9, false, 0,4, 1, ip + "imagenes/6_09.png");
        Diapositiva diapositiva80 = new Diapositiva(80, 6, 10, false, 0,4, 1, ip + "imagenes/6_10.png");

        Diapositiva diapositiva81 = new Diapositiva(81, 7, 1, false, 0,5, 1, ip + "imagenes/7_01.png");
        Diapositiva diapositiva82 = new Diapositiva(82, 7, 2, false, 0,5, 1, ip + "imagenes/7_02.png");
        Diapositiva diapositiva83 = new Diapositiva(83, 7, 3, false, 0,5, 1, ip + "imagenes/7_03.png");
        Diapositiva diapositiva84 = new Diapositiva(84, 7, 4, false, 0,5, 1, ip + "imagenes/7_04.png");
        Diapositiva diapositiva85 = new Diapositiva(85, 7, 5, false, 0,5, 1, ip + "imagenes/7_05.png");
        Diapositiva diapositiva86 = new Diapositiva(86, 7, 6, false, 0,5, 1, ip + "imagenes/7_06.png");
        Diapositiva diapositiva87 = new Diapositiva(87, 7, 7, false, 0,5, 1, ip + "imagenes/7_07.png");
        Diapositiva diapositiva88 = new Diapositiva(88, 7, 8, false, 0,5, 1, ip + "imagenes/7_08.png");
        Diapositiva diapositiva89 = new Diapositiva(89, 7, 9, false, 0,5, 1, ip + "imagenes/7_09.png");
        Diapositiva diapositiva90 = new Diapositiva(90, 7, 10, false, 0,5, 1, ip + "imagenes/7_10.png");
// Continúa desde aquí con id_estudio 4, 5, 6 y 7



        diapositivas.add(diapositiva1);
        diapositivas.add(diapositiva2);
        diapositivas.add(diapositiva3);
        diapositivas.add(diapositiva4);
        diapositivas.add(diapositiva5);
        diapositivas.add(diapositiva6);
        diapositivas.add(diapositiva7);
        diapositivas.add(diapositiva8);
        diapositivas.add(diapositiva9);
        diapositivas.add(diapositiva10);
        diapositivas.add(diapositiva11);
        diapositivas.add(diapositiva12);
        diapositivas.add(diapositiva13);
        diapositivas.add(diapositiva14);
        diapositivas.add(diapositiva15);
        diapositivas.add(diapositiva16);

        diapositivas.add(diapositiva17);
        diapositivas.add(diapositiva18);
        diapositivas.add(diapositiva19);
        diapositivas.add(diapositiva20);
        diapositivas.add(diapositiva21);
        diapositivas.add(diapositiva22);
        diapositivas.add(diapositiva23);
        diapositivas.add(diapositiva24);
        diapositivas.add(diapositiva25);
        diapositivas.add(diapositiva26);
        diapositivas.add(diapositiva27);
        diapositivas.add(diapositiva28);
        diapositivas.add(diapositiva29);
        diapositivas.add(diapositiva30);

        diapositivas.add(diapositiva31);
        diapositivas.add(diapositiva32);
        diapositivas.add(diapositiva33);
        diapositivas.add(diapositiva34);
        diapositivas.add(diapositiva35);
        diapositivas.add(diapositiva36);
        diapositivas.add(diapositiva37);
        diapositivas.add(diapositiva38);
        diapositivas.add(diapositiva39);
        diapositivas.add(diapositiva40);

        diapositivas.add(diapositiva41);
        diapositivas.add(diapositiva42);
        diapositivas.add(diapositiva43);
        diapositivas.add(diapositiva44);
        diapositivas.add(diapositiva45);
        diapositivas.add(diapositiva46);
        diapositivas.add(diapositiva47);
        diapositivas.add(diapositiva48);
        diapositivas.add(diapositiva49);
        diapositivas.add(diapositiva50);

        diapositivas.add(diapositiva51);
        diapositivas.add(diapositiva52);
        diapositivas.add(diapositiva53);
        diapositivas.add(diapositiva54);
        diapositivas.add(diapositiva55);
        diapositivas.add(diapositiva56);
        diapositivas.add(diapositiva57);
        diapositivas.add(diapositiva58);
        diapositivas.add(diapositiva59);
        diapositivas.add(diapositiva60);

        diapositivas.add(diapositiva61);
        diapositivas.add(diapositiva62);
        diapositivas.add(diapositiva63);
        diapositivas.add(diapositiva64);
        diapositivas.add(diapositiva65);
        diapositivas.add(diapositiva66);
        diapositivas.add(diapositiva67);
        diapositivas.add(diapositiva68);
        diapositivas.add(diapositiva69);
        diapositivas.add(diapositiva70);

        diapositivas.add(diapositiva71);
        diapositivas.add(diapositiva72);
        diapositivas.add(diapositiva73);
        diapositivas.add(diapositiva74);
        diapositivas.add(diapositiva75);
        diapositivas.add(diapositiva76);
        diapositivas.add(diapositiva77);
        diapositivas.add(diapositiva78);
        diapositivas.add(diapositiva79);
        diapositivas.add(diapositiva80);

        diapositivas.add(diapositiva81);
        diapositivas.add(diapositiva82);
        diapositivas.add(diapositiva83);
        diapositivas.add(diapositiva84);
        diapositivas.add(diapositiva85);
        diapositivas.add(diapositiva86);
        diapositivas.add(diapositiva87);
        diapositivas.add(diapositiva88);
        diapositivas.add(diapositiva89);
        diapositivas.add(diapositiva90);



    }

    private void mostrar_Acrietro_yfallos_finales2(String conclusion){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resultados Finales del Test")
                .setMessage(conclusion)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Puedes cerrar o hacer algo al aceptar
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }
    private void mostrar_Acrietro_yfallos_finales(){
//        Toast.makeText(Actividad_Test_TVPS.this, "test 1:\nACIERTOS --> " +cont_aciertos_1 + " /  FALLOS --> "+ cont_fallos_1, Toast.LENGTH_SHORT).show();
//        Toast.makeText(Actividad_Test_TVPS.this, "test 2:\nACIERTOS --> " +cont_aciertos_2 + " /  FALLOS --> "+ cont_fallos_2, Toast.LENGTH_SHORT).show();
//
//        Toast.makeText(Actividad_Test_TVPS.this, "test 3:\nACIERTOS --> " +cont_aciertos_3 + " /  FALLOS --> "+ cont_fallos_3, Toast.LENGTH_SHORT).show();
//        Toast.makeText(Actividad_Test_TVPS.this, "test 4:\nACIERTOS --> " +cont_aciertos_4 + " /  FALLOS --> "+ cont_fallos_4, Toast.LENGTH_SHORT).show();


            String resultados =
                    "Test 1:\nACIERTOS --> " + cont_aciertos_1 + " / FALLOS --> " + cont_fallos_1 + "\n\n" +
                            "Test 2:\nACIERTOS --> " + cont_aciertos_2 + " / FALLOS --> " + cont_fallos_2 + "\n\n" +
                            "Test 3:\nACIERTOS --> " + cont_aciertos_3 + " / FALLOS --> " + cont_fallos_3 + "\n\n" +
                            "Test 4:\nACIERTOS --> " + cont_aciertos_4 + " / FALLOS --> " + cont_fallos_4 + "\n\n" +
                            "Test 5:\nACIERTOS --> " + cont_aciertos_5 + " / FALLOS --> " + cont_fallos_5 + "\n\n" +
                            "Test 6:\nACIERTOS --> " + cont_aciertos_6 + " / FALLOS --> " + cont_fallos_6 + "\n\n" +
                            "Test 7:\nACIERTOS --> " + cont_aciertos_7 + " / FALLOS --> " + cont_fallos_7;



            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Resultados Finales del Test")
                    .setMessage(resultados)
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        // Puedes cerrar o hacer algo al aceptar
                        dialog.dismiss();
                    })
                    .setCancelable(false)
                    .show();
        }


}


