package com.example.proyecto_m13;

import static Utilidades.Utilidades.visibilidad_botones;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import Utilidades.Utilidades;

public class Actividad_Test_TVPS extends AppCompatActivity {

    private int id_empleado;
    private int id_cliente;
    private Button bt_1, bt_2, bt_3, bt_4, bt_5, bt_6, bt_7, bt_8,bt_9, bt_cambio_test;
    ImageView iv_imagen;
    private ArrayList<Diapositiva> diapositivas = new ArrayList<>();
    private ArrayList<Diapositiva> diapositivas_parte_test = new ArrayList<>();
    //private ImageView imageView;
    int currentIndex = 0;
    String[] url_fotos;
    int cont_fallos_general, cont_fallos_1, cont_fallos_2,cont_fallos_3,cont_fallos_4,cont_fallos_5,cont_fallos_6,cont_fallos_7;
    int cont_aciertos_general, cont_aciertos_1, cont_aciertos_2, cont_aciertos_3, cont_aciertos_4, cont_aciertos_5, cont_aciertos_6, cont_aciertos_7;
    int contador_test_Terminados = 0;

    private TextView tv_Cambio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_tvps);



        inicializar_componentes();

        id_cliente = getIntent().getIntExtra ("cliente", -1);

        Diapositiva diapositiva1 = new Diapositiva(1, 1, 1, false, 0,5, 2, "http://192.168.1.143/imagenes/1_01.png" );
        Diapositiva diapositiva2 = new Diapositiva(2, 1, 2, false, 0,5, 4, "http://192.168.1.143/imagenes/1_02.png" );
        Diapositiva diapositiva3 = new Diapositiva(3, 1, 3, false, 0,5, 1, "http://192.168.1.143/imagenes/1_03.png" );
        Diapositiva diapositiva4 = new Diapositiva(4, 2, 1, false, 0,5, 2, "http://192.168.1.143/imagenes/1_07.png" );
        Diapositiva diapositiva5 = new Diapositiva(5, 2, 2, false, 0,5, 4, "http://192.168.1.143/imagenes/1_08.png" );
        Diapositiva diapositiva6 = new Diapositiva(6, 2, 3, false, 0,5, 1, "http://192.168.1.143/imagenes/1_09.png" );

        Diapositiva diapositiva7 = new Diapositiva(7, 3, 1, false, 0,5, 2, "http://192.168.1.143/imagenes/3_01.png" );
        Diapositiva diapositiva8 = new Diapositiva(8, 3, 2, false, 0,5, 4, "http://192.168.1.143/imagenes/3_02.png" );
        Diapositiva diapositiva9 = new Diapositiva(9, 3, 3, false, 0,5, 1, "http://192.168.1.143/imagenes/3_03.png" );
        Diapositiva diapositiva10 = new Diapositiva(10, 4, 1, false, 0,4, 2, "http://192.168.1.143/imagenes/4_11.png" );
        Diapositiva diapositiva11 = new Diapositiva(11, 4, 2, false, 0,4, 4, "http://192.168.1.143/imagenes/4_12.png" );
        Diapositiva diapositiva12= new Diapositiva(12, 4, 3, false, 0,4, 1, "http://192.168.1.143/imagenes/4_13.png" );

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



       // int[] cantidad_test = new int[]{1,2,3,4,5,6,7};
//
//
//        for(int num_test : cantidad_test){

        int[] cantidad_test = new int[]{1,2,3,4,5,6,7};


        pasar_test(1);
        //if(contador_test_Terminados == 1){pasar_test(2);}
        //if(contador_test_Terminados == 2){pasar_test(4);}

        //if(contador_test_Terminados == 3){pasar_test(2);}


    }


    private void pasar_test(int parte_test){
        currentIndex = 0;
        diapositivas_parte_test = diapositivas_test_parte(parte_test);
        url_fotos = obtener_url_fotos_parte_test(parte_test);

        if(!diapositivas_parte_test.get(currentIndex).isTimer()){
            cargar_imagen(url_fotos[currentIndex]);
            if(diapositivas_parte_test.get(currentIndex).getId_estudio() == 1 || diapositivas_parte_test.get(currentIndex).getId_estudio() == 2  || diapositivas_parte_test.get(currentIndex).getId_estudio() == 3  ){
                visibilidad_botones(true, new Button[]{bt_1, bt_2, bt_3,bt_4, bt_5});
            }

            if(diapositivas_parte_test.get(currentIndex).getId_estudio() == 4){
                if(diapositivas_parte_test.get(currentIndex).getN_respuestas() == 5){
                    visibilidad_botones(false, new Button[]{bt_6, bt_7, bt_8,bt_9});
                    visibilidad_botones(true, new Button[]{bt_1, bt_2, bt_3,bt_4, bt_5});
                }else if(diapositivas_parte_test.get(currentIndex).getN_respuestas() == 4){
                    visibilidad_botones(false, new Button[]{bt_1, bt_2, bt_3,bt_4, bt_5});
                    visibilidad_botones(true, new Button[]{bt_6, bt_7, bt_8,bt_9});
                }

            }

            if(diapositivas_parte_test.get(currentIndex).getId_estudio() == 6 || diapositivas_parte_test.get(currentIndex).getId_estudio() == 7 ){
                //visibilidad_botones(true, new Button[]{bt_6, bt_7, bt_8,bt_9});
            }


        }


        View.OnClickListener answerListener = view -> {

            int boton_presionado = Integer.parseInt(((Button) view).getText().toString());
            Log.d("Respuesta", "Usuario eligió: " + boton_presionado);


            if (boton_presionado == diapositivas_parte_test.get(currentIndex).getRespuesta_correcta()) {
                cont_aciertos_general++;
                //Toast.makeText(this, "ACIERTO", Toast.LENGTH_SHORT).show();
                Log.d("Respuesta Correcta", "El usuario eligió la respuesta correcta.");
            } else {
                cont_fallos_general++;
                //Toast.makeText(this, "FALLO", Toast.LENGTH_SHORT).show();
                Log.d("Respuesta Incorrecta", "El usuario eligió una respuesta incorrecta.");
            }
            currentIndex++;





            if (currentIndex < url_fotos.length && cont_fallos_1<4) {
                cargar_imagen(url_fotos[currentIndex]);
            } else {
                Toast.makeText(this, "ACIERTOS --> " +cont_aciertos_general + " /  FALLOS --> "+ cont_fallos_general, Toast.LENGTH_SHORT).show();
                contador_fallos(parte_test);
                contador_aciertos(parte_test);
                Toast.makeText(this, "Fin del test", Toast.LENGTH_SHORT).show();

                visibilidad_botones(false, new Button[]{bt_1, bt_2, bt_3,bt_4,bt_5, bt_6, bt_7, bt_8, bt_9});
                iv_imagen.setVisibility(View.GONE);
                bt_cambio_test.setText("Has terminado el test " + parte_test + "\n Toca la pantalla para continuar");
                //tv_Cambio.setText("Has terminado el test " + parte_test + "\n Toca la pantalla para continuar");
                bt_cambio_test.setVisibility(View.VISIBLE);

                bt_cambio_test.setOnClickListener(v -> {
                    // Si no hemos llegado al final de todos los tests
                    contador_test_Terminados++;
                    Toast.makeText(this, "Test terminados --> "+contador_test_Terminados, Toast.LENGTH_SHORT).show();
                    bt_cambio_test.setVisibility(View.GONE); //hasta aquí funciona
                    if(contador_test_Terminados < 7){
                        bt_cambio_test.setVisibility(View.GONE);
                        pasar_test(contador_test_Terminados+1);

                    }
                    else{
                        tv_Cambio.setText("Finalizaste todos los test \n!!!Enhorabuena!!!");
                        tv_Cambio.setVisibility(View.VISIBLE);

                    }


                });

            }
        };

        bt_1.setOnClickListener(answerListener);
        bt_2.setOnClickListener(answerListener);
        bt_3.setOnClickListener(answerListener);
        bt_4.setOnClickListener(answerListener);
        bt_5.setOnClickListener(answerListener);
        bt_6.setOnClickListener(answerListener);
        bt_7.setOnClickListener(answerListener);
        bt_8.setOnClickListener(answerListener);
        bt_9.setOnClickListener(answerListener);

    }

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

    public void contador_aciertos(int parte_test){
        int cont_aciertos = -1; // Inicializamos la variable

        // Usamos switch para manejar las diferentes partes del test
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


    // AsyncTask para descargar la imagen en segundo plano
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
                bitmap = BitmapFactory.decodeStream(input);  // Convierte el flujo de entrada en un Bitmap
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);  // Establece el Bitmap en el ImageView
            }
        }
    }

    public String[] obtener_url_fotos_parte_test(int parte_test){
        ArrayList<String> urls = new ArrayList<>();
        for (Diapositiva diapo : diapositivas){
            if(diapo.getId_estudio() == parte_test){
                urls.add(diapo.getFoto());
            }
        }
        return urls.toArray(new String[0]);
    }

    private ArrayList<Diapositiva> diapositivas_test_parte (int parte_test){
        ArrayList<Diapositiva> diapositivas_parte_test=new ArrayList<>();
        for (Diapositiva diapo : diapositivas){
            if(diapo.getId_estudio() == parte_test){
                diapositivas_parte_test.add(diapo);
            }

        }
        return diapositivas_parte_test;
    }

    private void cargar_imagen (String url){
        new DownloadImageTask(iv_imagen).execute(url);
    }

    private void mostrar_botones(int num_respeuestas){
        if(num_respeuestas == 4){ visibilidad_botones(true, new Button[]{bt_1, bt_2, bt_3,bt_4, bt_5, bt_6, bt_7, bt_8,bt_9});}

    }

    private void inicializar_componentes(){

        iv_imagen = findViewById(R.id.iv_imagen);
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
        bt_cambio_test = findViewById(R.id.bt_cambio_test);



        visibilidad_botones(false, new Button[]{bt_1, bt_2, bt_3,bt_4, bt_5, bt_6, bt_7,bt_8, bt_9, bt_cambio_test });
        tv_Cambio.setVisibility(View.GONE);
    }
}