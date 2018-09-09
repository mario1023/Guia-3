package com.example.hv12.ejercicio_2_audio;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 7/9/18.
 */

public class AudioAsincrono extends AsyncTask<Void,String,String> {

    Context context;
    TextView lblActual,lblFin;
    Button btnIniciar;
    ProgressBar progressBar;
    MediaPlayer reproductorMusica;


    boolean pausa=false;

    private String VIGILANTE = "vigilante";

    public AudioAsincrono(Context context, TextView lblActual, TextView lblFin,Button btnIniciar,ProgressBar progressBar) {
        this.context = context;
        this.lblActual = lblActual;
        this.lblFin = lblFin;
        this.btnIniciar = btnIniciar;
        this.progressBar = progressBar;

    }

    @Override
    protected String doInBackground(Void... voids) {
        reproductorMusica.start();

          while (reproductorMusica.isPlaying()){
              if(isCancelled())reproductorMusica.stop();
              esperarUnSegundo();
              publishProgress(tiempo(reproductorMusica.getCurrentPosition()));
              if(pausa==true){
                  publishProgress(tiempo(reproductorMusica.getCurrentPosition()).substring(0,2)+
                          String.valueOf(Integer.parseInt(tiempo(reproductorMusica.getCurrentPosition()).substring(tiempo(reproductorMusica.getCurrentPosition()).lastIndexOf(":")+1,
                                  tiempo(reproductorMusica.getCurrentPosition()).length()))*-1));

                  synchronized (VIGILANTE){
                      try {
                          /**realiza pausa  en el hilo**/
                          reproductorMusica.pause();
                          VIGILANTE.wait();
                      }
                      catch (InterruptedException e) {
                          e.printStackTrace();
                      }/**sale del sincronized por lo que ya no hay pausa*/
                      pausa = false;
                      reproductorMusica.start();
                  }
              }
          }


        return null;
    }

/*************************FUNCIONES************************/
    public boolean esPausa(){
        return pausa;
    }

    public void pausarAudio(){
        pausa = true;
    }

    /** notifica a VIGILANTE en todas sus llamadas con syncronized**/
    public void reanudarAudio(){
        synchronized (VIGILANTE){
            VIGILANTE.notify();
        }
    }

    private void esperarUnSegundo() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignore) {}
    }

    private String tiempo(long t){
        long fin_min = TimeUnit.MILLISECONDS.toMinutes(t);
        long fin_sec = TimeUnit.MILLISECONDS.toSeconds(t) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(t));
        return fin_min+":"+fin_sec;
    }

    /******************************************************/
    @Override
    protected void onCancelled() {
      //  Toast.makeText(context, "Tarea cancelada!", Toast.LENGTH_SHORT).show();
        super.onCancelled();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if(Integer.parseInt(values[0].substring(values[0].lastIndexOf(":")+1,values[0].length()))<0 ){
           btnIniciar.setText("REANUDAR");
        }else{
            progressBar.setProgress(Integer.parseInt(values[0].substring(values[0].lastIndexOf(":")+1,values[0].length())));
            lblActual.setText(values[0]);
            btnIniciar.setText("PAUSAR");
            super.onProgressUpdate(values);
        }

    }

    @Override
    protected void onPreExecute() {
        reproductorMusica = MediaPlayer.create(context,R.raw.nokia_tune);
        long fin = reproductorMusica.getDuration();
        lblFin.setText(tiempo(fin));
        progressBar.setMax(Integer.parseInt(tiempo(fin).substring(tiempo(fin).lastIndexOf(":")+1,tiempo(fin).length())));
        progressBar.setProgress(0);
        btnIniciar.setText("PAUSAR");
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        btnIniciar.setText("INICIAR");
        super.onPostExecute(s);
    }



}
