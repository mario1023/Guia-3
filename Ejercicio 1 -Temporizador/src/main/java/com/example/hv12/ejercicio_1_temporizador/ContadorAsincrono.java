package com.example.hv12.ejercicio_1_temporizador;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 7/9/18.
 */

public class ContadorAsincrono extends AsyncTask<Integer,Integer,String> {


    boolean pausa = false;

    Context context;
    TextView lblContador,porcentaje;
    Button btnIniciar;
    EditText cantidad;
    ProgressBar progressBar;
    private String VIGILANTE = "VIGILANTE";


    public ContadorAsincrono(Context context, TextView lblContador,Button btnIniciar,EditText cantidad,TextView porcentaje,ProgressBar progressBar) {
        this.context = context;
        this.lblContador = lblContador;
        this.btnIniciar = btnIniciar;
        this.cantidad = cantidad;
        this.porcentaje = porcentaje;
        this.progressBar = progressBar;
    }

    @Override
    protected String doInBackground(Integer... integers) {

        int i = integers[0];

        while (i>=0){
            publishProgress(i);
            i--;
            esperarUnSegundo();
            /** si esta pausado**/
            if(pausa==true){
                publishProgress(i*-1);
                synchronized (VIGILANTE){
                    try {
                        /**realiza pausa  en el hilo**/
                        VIGILANTE.wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }/**sale del sincronized por lo que ya no hay pausa*/
                    pausa = false;
                }
            }

        }

        return "Finalizado";
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if(values[0]<0)
        {
            btnIniciar.setText("REANUDAR");
        }else{
            progressBar.setProgress(values[0]);
            porcentaje.setText(values[0]+"%");
            lblContador.setText(values[0]+" Seg");
            btnIniciar.setText("PAUSAR");
        }

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        btnIniciar.setText("PAUSAR");
        cantidad.setEnabled(false);
        porcentaje.setVisibility(View.VISIBLE);
        porcentaje.setText("0%");
        progressBar.setProgress(0);
        if(cantidad.getText().toString().equals("")){
            progressBar.setMax(60);
        }else{
            progressBar.setMax(Integer.parseInt(cantidad.getText().toString()));
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        cantidad.setEnabled(true);
        btnIniciar.setText("INICIAR");
        porcentaje.setText("Finalizado");
        lblContador.setText("1min");
    }

    private void esperarUnSegundo() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignore) {}
    }

    public boolean esPausa(){
        return pausa;
    }

    public void pausarContador(){
        pausa = true;
    }

    /** notifica a VIGILANTE en todas sus llamadas con syncronized**/
    public void reanudarContador(){
        synchronized (VIGILANTE){
            VIGILANTE.notify();
        }
    }
}
