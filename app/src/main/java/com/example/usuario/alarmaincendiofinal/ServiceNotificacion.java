package com.example.usuario.alarmaincendiofinal;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class ServiceNotificacion extends Service {

    ThreadLecturasBGUdp threadLec;
    private static final float SHAKE_THRESHOLD = (float)10.0;
    SensorManager mSensorManager;
    Sensor sensor;
    private long UltimaActualizacion;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        //Toast.makeText(this, "Servicio Notificacion Iniciado", Toast.LENGTH_SHORT).show();
        threadLec = new ThreadLecturasBGUdp();
        //threadLec.execute();
        AsyncTaskTools.execute(threadLec);

        return START_STICKY;
    }


    public void crearNotificacion(String mensaje){
        Intent intent = new Intent(this, LlamadaActivity.class);
        int requestCode = 0;//Your request code
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent,0);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Alarma Incendio")
                .setTicker("Alarma Incendio")
                .setContentText(mensaje)  //cambiar por mensaje que envie la placa, o setear uno por defecto
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000})
                .setVibrate(new long[]{1000, 1000})
                .addAction(R.drawable.llamadaicono,"Llam. Num. Emergencia",pendingIntent)
                .setSound(sound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
        threadLec = new ThreadLecturasBGUdp();
        threadLec.execute();
    }

    @Override
    public void onDestroy(){
        //Toast.makeText(this, "Servicio Notificacion Detenido", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private class ThreadLecturasBGUdp extends AsyncTask<Long, String, Long> {
        byte[] Message = new byte[1024];
        //private String ipPlaca;

       /* public ThreadLecturasUdp (String ipObtenida){
            ipPlaca = ipObtenida;
        }*/

        @Override
        protected Long doInBackground(Long... params) {       //reemplazar con nuestra lectura UDP
            DatagramPacket dp = new DatagramPacket(Message, Message.length);
            DatagramSocket ds = null;
            int VALOR_INCENDIO = 50;
            String ltext = "";

            try {
                ds = new DatagramSocket(8081); //puerto que escucha los valores de la placa
                //Inet4Address ip = (Inet4Address) Inet4Address.getByName("192.168.10.248");
                //System.out.println("Direccion ip: " + ds.getInetAddress());
                ds.receive(dp);
                ltext = new String(Message, 0, dp.getLength());
                System.out.println("Paquete recibido: " + ltext);
                while(Integer.parseInt(ltext) > VALOR_INCENDIO){
                    if(isCancelled()){
                        break;
                    }
                    ds.receive(dp);
                    ltext = new String(Message, 0, dp.getLength());
                    //System.out.println("Paquete recibido: " + ltext);
                }
               // UDP_Client client = new UDP_Client(getArchivoIP().trim());
                //client.Message = "Recibido";
                //client.EnviarInterrupcion();
                crearNotificacion("INCENDIO");
                SystemClock.sleep(15000);

            } catch (SocketException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                if (ds != null) {
                    ds.close();
                }
            }

            return null;    //aca podemos retornar un long... medio al pedo...
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private String getArchivoIP() {
        String[] archivos = fileList();
        String todo = "";
        if (existe(archivos, "ip.txt"))
            try {
                InputStreamReader archivo = new InputStreamReader(
                        openFileInput("ip.txt"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                todo = "";
                while (linea != null) {
                    todo = todo + linea + "\n";
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
            }
        return todo;
    }

    private boolean existe(String[] archivos, String archbusca) {
        for (int f = 0; f < archivos.length; f++)
            if (archbusca.equals(archivos[f]))
                return true;
        return false;
    }


}
