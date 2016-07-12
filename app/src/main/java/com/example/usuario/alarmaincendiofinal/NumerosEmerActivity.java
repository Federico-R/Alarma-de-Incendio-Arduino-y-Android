package com.example.usuario.alarmaincendiofinal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class NumerosEmerActivity extends AppCompatActivity implements View.OnClickListener{
    TextView numReg;
    EditText nuevoNum;
    Button botonRegistrar;
    Button botonLlamarNumReg;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/directorioEmer";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numeros_emer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        numReg = (TextView) findViewById(R.id.textViewNumReg);
        nuevoNum = (EditText) findViewById(R.id.editTextNuevoNum);
        botonRegistrar = (Button) findViewById(R.id.buttonRegistrar);
        botonRegistrar.setOnClickListener(this);
        botonLlamarNumReg = (Button) findViewById(R.id.buttonLlamarReg);
        botonLlamarNumReg.setOnClickListener(this);
        File dir = new File(path);
        dir.mkdirs();
        String nroEmergencia = "";
        nroEmergencia = getNroEmergencia();
        numReg.setText(nroEmergencia.trim());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRegistrar:
                String numero = nuevoNum.getText().toString().trim();
                nuevoNum.setText("");
                if (numero.isEmpty()){
                    Toast.makeText(this, "No Ha Ingresado Ningún Número", Toast.LENGTH_SHORT).show();
                    break;
                }
                try{
                    int num = Integer.parseInt(numero);
                    Log.i("", num + " is a number");
                }catch (NumberFormatException e){
                    Toast.makeText(this, "No Ha Ingresado un Número de Telefono Válido", Toast.LENGTH_SHORT).show();
                    Log.i("", numero + " is not a number");
                    break;
                }
                guardarNroEmergencia(numero);
                Toast.makeText(this, "Número de Emergencias Registrado Correctamente", Toast.LENGTH_SHORT).show();
                numReg.setText(numero);
                break;
            case R.id.buttonLlamarReg:

                String nroEmergencia = getNroEmergencia();
                if(!nroEmergencia.equals("")) {
                    try {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + nroEmergencia));
                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            startActivity(callIntent);
                            return;
                        }
                        startActivity(callIntent);
                    } catch (ActivityNotFoundException activityException) {
                        Log.e("helloandroid dialing", "Call failed");
                    }
                }
                else {
                    Toast.makeText(this, "No hay ningun numero de emergencia registrado", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



    private String getNroEmergencia() {
        String[] archivos = fileList();
        String todo = "";
        if (existe(archivos, "Emergencia.txt"))
            try {
                InputStreamReader archivo = new InputStreamReader(
                        openFileInput("Emergencia.txt"));
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

    public void guardarNroEmergencia(String ip) {
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(
                    "Emergencia.txt", Activity.MODE_PRIVATE));
            archivo.write(ip);
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
        }
        //finish();
    }

}
