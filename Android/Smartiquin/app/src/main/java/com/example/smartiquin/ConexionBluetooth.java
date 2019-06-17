package com.example.smartiquin;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ConexionBluetooth {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;

    private Context context;

    private Integer idThread=0;

    public ConnectedThread connectedThread;

    public ConexionBluetooth (Context context){

        this.context = context;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        encenderBT();

    }

    public void encenderBT(){

        if(!bluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)context).startActivityForResult(intent,1);
        }
    }

    public void conectar(){

        try {
            if(bluetoothAdapter.isEnabled()){

                String dirMAC = "00:18:E4:35:5A:64";

                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(dirMAC);

                try{
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                }catch (Exception e){
                    mostrarMensaje(context,"Error al crear el socket");
                }

                try{
                    bluetoothSocket.connect();
                }catch (Exception e){
                    bluetoothSocket.close();
                    mostrarMensaje(context,"Error al conectar");
                }

                try {
                    Method method = bluetoothDevice.getClass().getMethod("createBond", (Class[]) null);
                    method.invoke(bluetoothDevice, (Object[]) null);
                } catch (Exception e) {
                    mostrarMensaje(context,"Error al sincronizar");
                    e.printStackTrace();
                }

                try{
                    connectedThread = new ConnectedThread(bluetoothSocket);
                    connectedThread.start();

                    mostrarMensaje(context,"Conectado al arduino");

                }catch (Exception e){
                    mostrarMensaje(context,"No se pudo conectar");
                    e.printStackTrace();
                }

                try{

                    ///Abro el hilo secundario para escuchar lo que llega del arduino
                    ThreadAsynctask hilo=new ThreadAsynctask();
                    hilo.execute();


                }catch (Exception e){
                    mostrarMensaje(context,"No se pudo iniciar la escucha de datos");
                }

            }
        }catch (Exception e){
            mostrarMensaje(context,"No se pudo conectar");
        }
    }

    public void enviarMensaje(String mensaje){
        connectedThread.write(mensaje);
    }

    public void terminarConexion(){

        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void mostrarMensaje (Context context, String mensaje){
        Toast.makeText(context,mensaje,Toast.LENGTH_SHORT).show();
    }

    private class ThreadAsynctask extends AsyncTask<Void, String, Void>{

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                byte[] buffer = new byte[1024];
                int bytes = 0;

                // Recibe los valores de arduino tod0 el tiempo, hasta que termine la aplicación
                while(true) {

                    // Leo el inputstram del Bluetooth
                    bytes += bluetoothSocket.getInputStream().read(buffer, bytes, buffer.length - bytes);

                    // Convierto a string los datos recibidos
                    String strReceived = new String(buffer, 0, bytes);

                    // Publico el progreso
                    publishProgress(strReceived);

                    // Reinicio el buffer
                    buffer = new byte[1024];
                    bytes = 0;

                }
            }
            catch (IOException e)
            {
                mostrarMensaje(context,"No se pudo recibir datos");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mostrarMensaje(context, "Finalizando escucha de datos");
        }

        @Override
        protected void onProgressUpdate(String... values) {

            evaluarDato(values[0]);

        }
    }

    public void evaluarDato(String dato){

        if(dato.equals("A")){
            mostrarMensaje(context,"Es una A");
        }else{
            mostrarMensaje(context,dato);
        }



    }


}
