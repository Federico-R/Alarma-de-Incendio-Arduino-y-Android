package com.example.usuario.alarmaincendiofinal;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

public class UDP_Client {
    private AsyncTask<Void, Void, Void> async_cient;
    public String Message;
    String url = "";

    public UDP_Client(String url) {
        this.url = url;
    }
    @SuppressLint("NewApi")
    public void EnviarInterrupcion() {
        async_cient = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatagramSocket ds = null;
                System.out.println("soy enviarInterrupcion");
                try {
                    ds = new DatagramSocket(8080);
                    System.out.println("soy la url:" + url);
                    Inet4Address ip = (Inet4Address) Inet4Address.getByName(url.trim());
                    DatagramPacket dp;
                    dp = new DatagramPacket(Message.getBytes(), Message.length(), ip, 8081);    //envia msj a la ip de la placa con ese puerto
                    ds.setBroadcast(true);
                    ds.send(dp);
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