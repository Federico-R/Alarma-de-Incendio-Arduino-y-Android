package com.example.usuario.alarmaincendiofinal;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;
import java.net.UnknownHostException;


public class ServiceShake extends Service implements SensorEventListener {

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
        //Toast.makeText(this, "Servicio Shake Iniciado", Toast.LENGTH_SHORT).show();
        mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //Toast.makeText(this, "Servicio Shake Detenido",Toast.LENGTH_SHORT).show();
        mSensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()== Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }

    private void getAccelerometer(SensorEvent event) {
        float[] valores = event.values;
        // Movimiento
        float x = valores[0];
        float y = valores[1];
        float z = valores[2];
        float referencia = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long horaActual = event.timestamp;
        if (referencia >= SHAKE_THRESHOLD) {
            // Y si pasaron más de 3000 ms entre el último evento
            if (horaActual - UltimaActualizacion < 3000) {
                return;
            }
            UltimaActualizacion = horaActual;
            Toast.makeText(this,"Se detecto un Shake",Toast.LENGTH_SHORT).show();
            String url = getArchivoIP();
            UDP_Client client = new UDP_Client(url);
            client.Message = "Detener";
            client.EnviarInterrupcion();
            //threadLec2.execute();       //borrar, aca podemos pasar parametros extra, si es necesario...
            //AsyncTaskTools.execute(client);
            //UDP_Receive info = new UDP_Receive();
            //info.RecibirInformacion();
            //Toast.makeText(this, "Lectura: " + info.Message.toString(), Toast.LENGTH_SHORT).show();


            //Aca hay que mandar una interrupcion a la placa para que detenga la alarma
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
