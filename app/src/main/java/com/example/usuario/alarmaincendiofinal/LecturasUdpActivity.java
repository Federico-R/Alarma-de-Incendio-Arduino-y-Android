package com.example.usuario.alarmaincendiofinal;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class LecturasUdpActivity extends AppCompatActivity implements View.OnClickListener{
    ThreadLecturasUdp threadLec2;
    TextView texto;
    TextView infoLectura;
    String datoIp;      //obtener de archivo, previa lectura del QR
    Button botonSilenciar;
    int bandera=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        texto = (TextView) findViewById(R.id.textViewIp);

        botonSilenciar = (Button) findViewById(R.id.botonSilenciar);
        infoLectura = (TextView) findViewById(R.id.textViewLectura);
        botonSilenciar.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
            datoIp = extras.getString("URL").trim();        //obtener de archivo, previa lectura del QR
            texto.setText(datoIp);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onResume() {     //el asyntask esta bindeado a la activity...
        super.onResume();
        //en datoIP tiene que estar la IP desde el archivo, quizas tengamos que obtenerla de nuevo aca...
        //ThreadLecturasUdp threadLec = new ThreadLecturasUdp(datoIp);
        threadLec2 = new ThreadLecturasUdp();
        //threadLec2.execute();       //borrar, aca podemos pasar parametros extra, si es necesario...
        AsyncTaskTools.execute(threadLec2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(threadLec2.getStatus() == AsyncTask.Status.RUNNING){
            threadLec2.cancel(true);
            //Toast.makeText(getApplicationContext(), "Estoy " + threadLec.getStatus().toString(), Toast.LENGTH_LONG).show();
            if(threadLec2.isCancelled()==true){
               // Toast.makeText(getApplicationContext(), "Me cancelé", Toast.LENGTH_LONG).show();
            }
            else{
               // Toast.makeText(getApplicationContext(), "No Me cancelé", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.botonSilenciar:
                infoLectura.setText("");
                bandera=1;
                UDP_Client client = new UDP_Client(texto.getText().toString());
                client.Message = "Detener";
                client.EnviarInterrupcion();
                threadLec2 = new ThreadLecturasUdp();
                AsyncTaskTools.execute(threadLec2);
                break;
        }
    }

    private class ThreadLecturasUdp extends AsyncTask<Long, String, Long> {
        byte[] Message = new byte[1024];
        //private String ipPlaca;

       /* public ThreadLecturasUdp (String ipObtenida){
            ipPlaca = ipObtenida;
        }*/

        @Override
        protected Long doInBackground(Long... params) {       //reemplazar con nuestra lectura UDP
            DatagramPacket dp2 = new DatagramPacket(Message, Message.length);
            DatagramSocket ds2 = null;
            int VALOR_INCENDIO = 50;
            String ltext = "";
            //System.out.println("THREAD VALOREEEEEEEEEEEEEEEEES");
            //Toast.makeText(getApplicationContext(), "Thread Lectura", Toast.LENGTH_LONG).show();

            try {
                ds2 = new DatagramSocket(8033);
                //Inet4Address ip = (Inet4Address) Inet4Address.getByName("192.168.10.248");
                //System.out.println("Direccion ip: " + ds.getInetAddress());
                ds2.receive(dp2);
                ltext = new String(Message, 0, dp2.getLength());
                System.out.println("Paquete recibido: " + ltext);
                while(Integer.parseInt(ltext) > VALOR_INCENDIO){
                    if(isCancelled()){
                        break;
                    }
                    ds2.receive(dp2);
                    ltext = new String(Message, 0, dp2.getLength());
                    System.out.println("Paquete recibido: " + ltext);
                    publishProgress(ltext);   //metodo para publcar las lecturas en el hilo principal de la activity
                }
                bandera=0;

            } catch (SocketException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            finally {
                if (ds2 != null) {
                    ds2.close();
                }
            }

            return null;    //aca podemos retornar un long... medio al pedo...
        }

        @Override
        protected void onProgressUpdate(String... values) {     //esto se ejecuta en el thread de la activity...
            super.onProgressUpdate(values);
            infoLectura.setText(values[0]);     //seteamos el textView de la activity con las lecturas de la placa
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            if(bandera == 0){
            infoLectura.setText("INCENDIO!!!");
            botonSilenciar.setVisibility(View.VISIBLE); }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
