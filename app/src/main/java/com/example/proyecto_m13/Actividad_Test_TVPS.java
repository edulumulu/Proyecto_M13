package com.example.proyecto_m13;

import static Utilidades.Utilidades.visibilidad_Textviews;
import static Utilidades.Utilidades.visibilidad_botones;

import android.app.AlertDialog;
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Actividad_Test_TVPS extends AppCompatActivity {

    private int id_empleado;
    private int id_cliente;
    private int edad_cliente;

    private Button bt_1, bt_2, bt_3, bt_4, bt_5, bt_6, bt_7, bt_8, bt_9, bt_10, bt_11, bt_12, bt_13, bt_cambio_test;
    private ImageView iv_imagen, iv_imagen_timer;
    private ArrayList<Diapositiva> diapositivas = new ArrayList<>();
    private ArrayList<Diapositiva> diapositivas2 = new ArrayList<>();
    private ArrayList<Diapositiva> diapositivas_parte_test = new ArrayList<>();
    private Test_realizado resultado_test_resalizado;
    private String conclusion_final;

    private TextView tv_Cambio, tv_instrucciones;
    private String[] url_fotos;
    private int cont_fallos_general, cont_fallos_1, cont_fallos_2,cont_fallos_3,cont_fallos_4,cont_fallos_5,cont_fallos_6,cont_fallos_7;
    private int cont_aciertos_general, cont_aciertos_1, cont_aciertos_2, cont_aciertos_3, cont_aciertos_4, cont_aciertos_5, cont_aciertos_6, cont_aciertos_7;

    private int fallos_permitidos = 2;
    private int indice_actual = 0;
    private int contador_test_Terminados = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_tvps);

        inicializar_componentes();
        //Array de diapositivas test
        diapositivas_De_prueba();

        mostrar_instrucciones(1);
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


    /**
     * Método que inicia la parte del test que se solicita como parámetro, siendo este un entero
     * @param parte_test
     */
    private void pasar_test(int parte_test){
        iv_imagen.setVisibility(View.VISIBLE);
        diapositivas_parte_test = diapositivas_test_parte(parte_test);
        url_fotos = obtener_url_fotos_parte_test(parte_test);

        // CORRECCIÓN: Guardamos las diapositivas actuales para el temporizador
       // diapositivas2 = diapositivas_parte_test;

        // Solo mostramos la primera diapositiva
        mostrar_diapositiva_y_botones(diapositivas_parte_test, indice_actual, parte_test);

        // Listener para botones cuando NO hay timer
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
            iniciar_temporizador(3000, parte_test);

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
            iniciar_temporizador(3000, parte_test);

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
        Toast.makeText(this, "ACIERTOS --> " +cont_aciertos_general + " /  FALLOS --> "+ cont_fallos_general, Toast.LENGTH_SHORT).show();
        contador_fallos(parte_test);
        contador_aciertos(parte_test);
        Toast.makeText(this, "Fin del test", Toast.LENGTH_SHORT).show();

        visibilidad_botones(false, new Button[]{bt_1, bt_2, bt_3,bt_4, bt_5, bt_6, bt_7,bt_8, bt_9, bt_10, bt_11,bt_12, bt_13});
        iv_imagen.setVisibility(View.GONE);

        mostrar_instrucciones (parte_test +1);
        //tv_Cambio.setText("Has terminado el test " + parte_test + "\n Toca la pantalla para continuar");
        bt_cambio_test.setVisibility(View.VISIBLE);

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

                }
                else{
                    tv_Cambio.setText("Finalizaste todos los test \n!!!Enhorabuena!!!");
                    //Guardar los datos en la BBDD


                    conclusion_final = contrastar_resultados();
                    //resultado_test_resalizado = new Test_realizado();

                    datoscliente_y_empleado();
                    //mostrar_Acrietro_yfallos_finales();
                    mostrar_Acrietro_yfallos_finales2(conclusion_final);

                    //Toast.makeText(Actividad_Test_TVPS.this, "Empleado --> "+ id_empleado+" / Cliente --> "+id_cliente + " ,tiene "+edad_cliente+" años", Toast.LENGTH_LONG).show();
                    tv_Cambio.setVisibility(View.VISIBLE);

                }
            }
        });
    }

    private String contrastar_resultados() {
        String conclusion;

        String parte1 = "No hay resultados";
        String parte2 = "No hay resultados";
        String parte3 = "No hay resultados";
        String parte4 = "No hay resultados";
        String parte5 = "No hay resultados";
        String parte6 = "No hay resultados";
        String parte7 = "No hay resultados";
        String valoracion = null;
        int contador = 0;

        if (edad_cliente < 6) {
            parte1 = resultado_ponderado(cont_aciertos_1, 2, 4);
            parte2 = resultado_ponderado(cont_aciertos_2, 2, 4);
            parte3 = resultado_ponderado(cont_aciertos_3, 2, 4);
            parte4 = resultado_ponderado(cont_aciertos_4, 2, 4);
            parte5 = resultado_ponderado(cont_aciertos_5, 2, 4);
            parte6 = resultado_ponderado(cont_aciertos_6, 2, 4);
            parte7 = resultado_ponderado(cont_aciertos_7, 2, 4);

           contador = partes_no_superadas(2);


        } else if (edad_cliente >= 6 && edad_cliente < 10) {

            parte1 = resultado_ponderado(cont_aciertos_1, 3, 5);
            parte2 = resultado_ponderado(cont_aciertos_2, 3, 5);
            parte3 = resultado_ponderado(cont_aciertos_3, 3, 5);
            parte4 = resultado_ponderado(cont_aciertos_3, 3, 5);
            parte5 = resultado_ponderado(cont_aciertos_3, 3, 5);
            parte6 = resultado_ponderado(cont_aciertos_3, 3, 5);
            parte7 = resultado_ponderado(cont_aciertos_3, 3, 5);

            contador = partes_no_superadas(3);

        } else if (edad_cliente >= 10 && edad_cliente < 13) {

            parte1 = resultado_ponderado(cont_aciertos_1, 4, 7);
            parte2 = resultado_ponderado(cont_aciertos_2, 4, 7);
            parte3 = resultado_ponderado(cont_aciertos_3, 4, 7);
            parte4 = resultado_ponderado(cont_aciertos_3, 4, 7);
            parte5 = resultado_ponderado(cont_aciertos_3, 4, 7);
            parte6 = resultado_ponderado(cont_aciertos_3, 4, 7);
            parte7 = resultado_ponderado(cont_aciertos_3, 4, 7);

            contador = partes_no_superadas(3);

        } else if (edad_cliente >= 13 ) {

            parte1 = resultado_ponderado(cont_aciertos_1, 5, 9);
            parte2 = resultado_ponderado(cont_aciertos_2, 5, 9);
            parte3 = resultado_ponderado(cont_aciertos_3, 5, 9);
            parte4 = resultado_ponderado(cont_aciertos_3, 5, 9);
            parte5 = resultado_ponderado(cont_aciertos_3, 5, 9);
            parte6 = resultado_ponderado(cont_aciertos_3, 5, 9);
            parte7 = resultado_ponderado(cont_aciertos_3, 5, 9);

            contador = partes_no_superadas(3);

        }

        valoracion = valoracion(contador);

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

    private int partes_no_superadas(int aciertos){
        int contador = 0;

        if(cont_aciertos_1<aciertos){ contador++;}
        if(cont_aciertos_2<aciertos){ contador++;}
        if(cont_aciertos_3<aciertos){ contador++;}
        if(cont_aciertos_4<aciertos){ contador++;}
        if(cont_aciertos_5<aciertos){ contador++;}
        if(cont_aciertos_6<aciertos){ contador++;}
        if(cont_aciertos_7<aciertos){ contador++;}

        return contador;
    }

    private String valoracion (int contador){

        String valoracion = "";

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
        return valoracion;
    }

    private String resultado_ponderado (int contador, int minimo, int maximo){
        if(contador <minimo){
            return "Está por debajo de la media";
        }else if(cont_fallos_1 <=minimo &&cont_fallos_1 <=maximo){
            return "Dentro de los valores normales";
        }else {
            return "Sobresale de la media";
        }
    }

    private void datoscliente_y_empleado(){
        id_cliente = getIntent().getIntExtra ("idCliente", -1);
        id_empleado = getIntent().getIntExtra("idEmpleado", -1);
        edad_cliente = getIntent().getIntExtra("edadCliente", -1);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resultados Finales del Test")
                .setMessage("Empleado --> "+ id_empleado+" / Cliente --> "+id_cliente + " ,tiene "+edad_cliente+" años")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Puedes cerrar o hacer algo al aceptar
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();

    }

    /**
     * Guarda los fallos realizados en el test en la variable correspondiente a cada parte
     * @param parte_test
     */
    public void contador_fallos(int parte_test){
        // Usamos switch para manejar las diferentes partes del test
        switch(parte_test) {
            case 1:
                cont_fallos_1 = cont_fallos_general; // Asignamos los fallos a la parte 1
                cont_fallos_general = 0; // Reiniciamos el contador general de fallos
                break;

            case 2:
                cont_fallos_2 = cont_fallos_general; // Asignamos los fallos a la parte 2
                cont_fallos_general = 0; // Reiniciamos el contador general de fallos
                break;

            case 3:
                cont_fallos_3 = cont_fallos_general; // Asignamos los fallos a la parte 3
                cont_fallos_general = 0; // Reiniciamos el contador general de fallos
                break;

            case 4:
                cont_fallos_4 = cont_fallos_general; // Asignamos los fallos a la parte 4
                cont_fallos_general = 0; // Reiniciamos el contador general de fallos
                break;

            case 5:
                cont_fallos_5 = cont_fallos_general; // Asignamos los fallos a la parte 5
                cont_fallos_general = 0; // Reiniciamos el contador general de fallos
                break;

            case 6:
                cont_fallos_6 = cont_fallos_general; // Asignamos los fallos a la parte 6
                cont_fallos_general = 0; // Reiniciamos el contador general de fallos
                break;

            case 7:
                cont_fallos_7 = cont_fallos_general; // Asignamos los fallos a la parte 7
                cont_fallos_general = 0; // Reiniciamos el contador general de fallos
                break;

            default:
                // Si la parte del test no es válida (fuera del rango 1-7), no se hace nada
                break;
        }
    }

    /**
     * Guarda los aciertos conseguidos en el test en la variable correspondiente a cada parte
     * @param parte_test
     */
    public void contador_aciertos(int parte_test){

        switch(parte_test) {
            case 1:
                cont_aciertos_1 = cont_aciertos_general; // Asignamos los aciertos a la parte 1
                cont_aciertos_general = 0; // Reiniciamos el contador general de aciertos
                break;

            case 2:
                cont_aciertos_2 = cont_aciertos_general; // Asignamos los aciertos a la parte 2
                cont_aciertos_general = 0; // Reiniciamos el contador general de aciertos
                break;

            case 3:
                cont_aciertos_3 = cont_aciertos_general; // Asignamos los aciertos a la parte 3
                cont_aciertos_general = 0; // Reiniciamos el contador general de aciertos
                break;

            case 4:
                cont_aciertos_4 = cont_aciertos_general; // Asignamos los aciertos a la parte 4
                cont_aciertos_general = 0; // Reiniciamos el contador general de aciertos
                break;

            case 5:
                cont_aciertos_5 = cont_aciertos_general; // Asignamos los aciertos a la parte 5
                cont_aciertos_general = 0; // Reiniciamos el contador general de aciertos
                break;

            case 6:
                cont_aciertos_6 = cont_aciertos_general; // Asignamos los aciertos a la parte 6
                cont_aciertos_general = 0; // Reiniciamos el contador general de aciertos
                break;

            case 7:
                cont_aciertos_7 = cont_aciertos_general; // Asignamos los aciertos a la parte 7
                cont_aciertos_general = 0; // Reiniciamos el contador general de aciertos
                break;

            default:
                // Si la parte del test no es válida (fuera del rango 1-7), no se hace nada
                break;
        }
    }

    /**
     * Muestra las instrucciones según el test que se va a realizar
     * @param parte_Test
     */
    public void mostrar_instrucciones(int parte_Test){
        tv_instrucciones.setVisibility(View.VISIBLE);
        switch (parte_Test){
            case 1:
                tv_instrucciones.setText("Test "+parte_Test+"\nDiscriminacion Visual\n\nDebes comprar un burro y ponerle una cola\n\nPara comenzar el test toca la pantalla");
                break;
            case 2:
                tv_instrucciones.setText("Has terminado el test 1"+"\n\nTest "+parte_Test+"Discriminacion VisualTest Memoria visual\nMemoriza la figurara\nPasados 10 segundos podras selecionar la figura memorizada\n\nPara comenzar el test toca la pantalla");
                break;
            case 3:
                tv_instrucciones.setText("Debes comprar un burro y ponerle una cola");
                break;
            case 4:
                tv_instrucciones.setText("Debes comprar un burro y ponerle una cola");
                break;
            case 5:
                tv_instrucciones.setText("Debes comprar un burro y ponerle una cola");
                break;
            case 6:
                tv_instrucciones.setText("Debes comprar un burro y ponerle una cola");
                break;
            case 7:
                break;

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
                urls.add(diapo.getFoto());
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




    //Metodos que he usado para ir probando
    public void diapositivas_De_prueba(){

        //"http://192.168.1.143/"    casa
        // madre "http://192.168.1.106/"
        //String ip= "http://192.168.1.143/"; //casa
        String ip= "http://192.168.1.106/"; // Mama
        Diapositiva diapositiva1 = new Diapositiva(1, 1, 1, false, 0,5, 1, ip + "imagenes/1_01.png");
        Diapositiva diapositiva2 = new Diapositiva(2, 1, 2, false, 0,5, 1, ip + "imagenes/1_02.png");
        Diapositiva diapositiva3 = new Diapositiva(3, 1, 3, false, 0,5, 1, ip + "imagenes/1_03.png");
        Diapositiva diapositiva4 = new Diapositiva(4, 2, 1, true, 0,0, 0, ip + "imagenes/2_01.png");
        Diapositiva diapositiva5 = new Diapositiva(5, 2, 2, false, 0,4, 1, ip + "imagenes/2_02.png");
        Diapositiva diapositiva6 = new Diapositiva(6, 2, 3, true, 0,0, 0, ip + "imagenes/2_39.png");
        Diapositiva diapositiva7 = new Diapositiva(7, 2, 4, false, 0,4, 1, ip + "imagenes/2_40.png");

        Diapositiva diapositiva11 = new Diapositiva(11, 3, 1, false, 0,5, 1, ip + "imagenes/3_01.png");
        Diapositiva diapositiva12 = new Diapositiva(12, 3, 2, false, 0,5, 1, ip + "imagenes/3_02.png");
        Diapositiva diapositiva13 = new Diapositiva(13, 3, 3, false, 0,5, 1, ip + "imagenes/3_03.png");
        Diapositiva diapositiva14 = new Diapositiva(14, 4, 1, false, 0,5, 1, ip + "imagenes/4_11.png");
        Diapositiva diapositiva15 = new Diapositiva(15, 4, 2, false, 0,4, 1, ip + "imagenes/4_12.png");
        Diapositiva diapositiva16 = new Diapositiva(16, 4, 3, false, 0,4, 1, ip + "imagenes/4_13.png");

        Diapositiva diapositiva17 = new Diapositiva(17, 5, 1, true, 0,0, 0, ip + "imagenes/5_01.png");
        Diapositiva diapositiva18 = new Diapositiva(18, 5, 2, false, 0,4, 1, ip + "imagenes/5_02.png");
        Diapositiva diapositiva19 = new Diapositiva(19, 5, 3, true, 0,0, 0, ip + "imagenes/5_39.png");
        Diapositiva diapositiva20 = new Diapositiva(20, 5, 4, false, 0,4, 1, ip + "imagenes/5_40.png");

        diapositivas.add(diapositiva1);
        diapositivas.add(diapositiva2);
        diapositivas.add(diapositiva3);
        diapositivas.add(diapositiva4);
        diapositivas.add(diapositiva5);
        diapositivas.add(diapositiva6);
        diapositivas.add(diapositiva7);
//        diapositivas.add(diapositiva8);
//        diapositivas.add(diapositiva9);
//        diapositivas.add(diapositiva10);
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


