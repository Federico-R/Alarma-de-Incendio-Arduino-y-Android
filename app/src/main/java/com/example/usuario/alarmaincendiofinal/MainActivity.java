package com.example.usuario.alarmaincendiofinal;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    Button boton;
    Button botonQR;
    ImageButton botonLlamada;
    EditText editText;
    private BroadcastReceiver myRegistracionBroadcastReceiver;
    private String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        boton = (Button) findViewById(R.id.button);
        boton.setOnClickListener(this);
        botonQR = (Button) findViewById(R.id.button2);
        botonQR.setOnClickListener(this);
        editText.setText(getArchivoIP().trim());
        //startService(new Intent(getBaseContext(), ServiceShake.class));  //iniciamos el servicio para el gesto de shake
        myRegistracionBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().endsWith(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    String token = intent.getStringExtra("token");
                    //Toast.makeText(getApplicationContext(), "GCM token:" + token, Toast.LENGTH_LONG).show();
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    //Toast.makeText(getApplicationContext(), "GCM Error Registracion!", Toast.LENGTH_LONG).show();
                }
            }
        };
        int codResultado = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != codResultado) {
            if (GooglePlayServicesUtil.isUserRecoverableError(codResultado)) {
                Toast.makeText(getApplicationContext(), "El servicio de Google Play no esta instalado/ activado en este dispositivo", Toast.LENGTH_SHORT).show();
                GooglePlayServicesUtil.showErrorNotification(codResultado, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), "Este dispositivo no soporta Google Play Services", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent intent2 = new Intent(this, GCMRegistrationIntentService.class);
            startService(intent2);
        }
        addListenerOnbutton();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void addListenerOnbutton() {
        botonLlamada = (ImageButton) findViewById(R.id.imageButton);
        botonLlamada.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intentL = new Intent(MainActivity.this, NumerosEmerActivity.class);
                startActivity(intentL);
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(myRegistracionBroadcastReceiver, new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(myRegistracionBroadcastReceiver, new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
        startService(new Intent(getBaseContext(), ServiceShake.class));  //iniciamos el servicio para el gesto de shake
        startService(new Intent(getBaseContext(), ServiceNotificacion.class));

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myRegistracionBroadcastReceiver);
        //stopService(new Intent(getBaseContext(), ServiceShake.class));   //detenemos el servicio shake
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getBaseContext(), ServiceShake.class));//detenemos el servicio shake
        stopService(new Intent(getBaseContext(), ServiceNotificacion.class));//detenemos el servicio notificacion
        //Toast.makeText(this, "on destroy",Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //Recibimos lo que haya escaneado la camara
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        try {
            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();    //Obtenemos el contenido del escaneo
                System.out.println("LEI DEL QR:" + scanContent);
                editText.setText(scanContent.trim());
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No se ha logrado escanear un QRCode", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "No se ha logrado escanear un QRCode", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                String urlIp = editText.getText().toString();
                if (urlIp.matches("")) {
                    Toast.makeText(this, "No se ha ingresado ninguna IP", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (esIPValida(urlIp)) {
                    guardarIP(urlIp);
                    getArchivoIP();
                    Intent intent = new Intent(MainActivity.this, LecturasUdpActivity.class);
                    intent.putExtra("URL", urlIp);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No se ha ingresado una IP con formato valido", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
            case R.id.button2:
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();             //iniciamos el escaneo de codigo
                break;
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

    public void guardarIP(String ip) {
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(
                    "ip.txt", Activity.MODE_PRIVATE));
            archivo.write(ip);
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
        }
    }

    private boolean esIPValida(String url) {
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

}
