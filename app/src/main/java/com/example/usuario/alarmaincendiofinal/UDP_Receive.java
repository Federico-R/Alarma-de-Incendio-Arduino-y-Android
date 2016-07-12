package com.example.usuario.alarmaincendiofinal;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.nio.charset.StandardCharsets;

/**
 * Created by leandro on 16/06/2016.
 */
public class UDP_Receive {

    private AsyncTask<Void, Void, Void> async_cient;
    byte[] Message = new byte[1024];

    @SuppressLint("NewApi")
    public void RecibirInformacion() {
        async_cient = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatagramPacket dp = new DatagramPacket(Message, Message.length);
                DatagramSocket ds = null;

                try {
                    ds = new DatagramSocket(8080);
                    //Inet4Address ip = (Inet4Address) Inet4Address.getByName("192.168.10.248");
                    //System.out.println("Direccion ip: " + ds.getInetAddress());
                    ds.receive(dp);
                    String ltext = new String(Message, 0, dp.getLength());
                    System.out.println("Paquete recibido: " + ltext);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null) {
                        ds.close();
                    }
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }

        };

        if (Build.VERSION.SDK_INT >= 11)
            async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else async_cient.execute();
    }
}