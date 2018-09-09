package com.example.hv12.ejercicio_2_audio;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    TextView lblActual,lblFin;
    Button btnIniciar,btnReiniciar;
    AudioAsincrono audioAsincrono;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        lblActual    = findViewById(R.id.lblActual);
        lblFin       = findViewById(R.id.lblFin);
        btnIniciar   = findViewById(R.id.btnIniciar);
        btnReiniciar = findViewById(R.id.btnReiniciar);
        progressBar = findViewById(R.id.progressBar);


        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciar();
            }
        });


        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*boton reiniciar*/
                reiniciar();
            }
        });

    }

    private void iniciar(){

        if(audioAsincrono==null){
            audioAsincrono = new AudioAsincrono(this,lblActual,lblFin,btnIniciar,progressBar);
            audioAsincrono.execute();
            /**si ha terminado de ejecutar el hilo -> se crea otro hilo **/
        }else if(audioAsincrono.getStatus()== AsyncTask.Status.FINISHED){
            audioAsincrono = new AudioAsincrono(this,lblActual,lblFin,btnIniciar,progressBar);
            audioAsincrono.execute();
            /** si esta ejecutado y no esta pausado -> entonces se pausa**/
        }else if(audioAsincrono.getStatus()== AsyncTask.Status.RUNNING && !audioAsincrono.esPausa()  ){
            audioAsincrono.pausarAudio();
            /** si no entro en las condiciones anteriores por defecto esta pausado -> se reanuda*/
        }else{
            audioAsincrono.reanudarAudio();
        }
    }

    private void reiniciar(){
        if(audioAsincrono.getStatus()== AsyncTask.Status.RUNNING && !audioAsincrono.esPausa() || audioAsincrono.getStatus()== AsyncTask.Status.RUNNING && audioAsincrono.esPausa()){
            //cancela el hilo actual
            audioAsincrono.cancel(true);
            //reinicia el audio
            audioAsincrono = new AudioAsincrono(this,lblActual,lblFin,btnIniciar,progressBar);
            audioAsincrono.execute();
        }

    }
}
