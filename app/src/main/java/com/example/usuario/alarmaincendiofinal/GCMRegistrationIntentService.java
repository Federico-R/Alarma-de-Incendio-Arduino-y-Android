package com.example.usuario.alarmaincendiofinal;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class GCMRegistrationIntentService extends IntentService {
    public static final String REGISTRATION_SUCCESS = "RegistracionExito";
    public static final String REGISTRATION_ERROR = "RegistracionError";
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tokenGenerado";

    //public GCMRegistrationIntentService() {super("1035559758682");}
    public GCMRegistrationIntentService() {super("");}

    @Override
    protected void onHandleIntent(Intent intent) {
        registerGCM();
    }

    private void registerGCM() {
        Intent registrationComplete = null;
        String token = null;
        try {
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            File dir = new File(path);
            dir.mkdirs();
            File file = new File(path + "/tokenGen.txt");
            Save(file, token);
            //este token se actualiza dinamicamente, hay que hacerselo saber a la placa
            Log.w("GCMRegIntentService", "token:" + token);
            //notify to UI that registration complete success
            registrationComplete = new Intent(REGISTRATION_SUCCESS);
            registrationComplete.putExtra("token", token);
        } catch (Exception e) {
            Log.w("GCMRegIntentService", "Error de Registracion");
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }
        //Send broadcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    public static void Save(File file, String data)
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        try
        {
            try
            {
                if(data != null){
                    fos.write(data.getBytes());
                    fos.write("\n".getBytes());
                }

            }
            catch (IOException e) {e.printStackTrace();}
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch (IOException e) {e.printStackTrace();}
        }
    }
}
