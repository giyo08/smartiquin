package com.example.smartiquin;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ///Botones, switchs, textViews
    private Button btnAbrirCerrar;
    private Button btnMeds;
    private Button btnConectarArduino;
    private Switch switchShake;
    private TextView tvEstBot;

    ///EstadoBotiquin
    private Boolean estadoBotiquin;
    private static final Boolean E_ABIERTO = true;
    private static final Boolean E_CERRADO = false;

    ///String
    private static final String S_ABIERTO = "ABIERTO";
    private static final String S_CERRADO = "CERRADO";
    private static final String S_ABRIR = "Abrir";
    private static final String S_CERRAR = "Cerrar";

    ///Senso
    private SensorManager sm;
    private Sensor sensorShake;
    private Sensor sensorProx;
    private Sensor sensorLuz;

    ///Variables para Shake
    private long ultShake = 0;
    private Vibrator v;

    ///Bluetooth
    private ConexionBluetooth conexionBluetooth;

    private MedicamentosDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setea la pantalla en modo portrait, no permitiendo que rote
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new MedicamentosDBHelper(getApplicationContext());

        conexionBluetooth = new ConexionBluetooth(MainActivity.this);

        ///Asigno a las variables su correspondiente componente
        btnAbrirCerrar = findViewById(R.id.buttonAbrirCerrar);
        btnMeds = findViewById(R.id.buttonMeds);
        tvEstBot = findViewById(R.id.textViewEstBot);
        switchShake = findViewById(R.id.switchShake);
        btnConectarArduino = findViewById(R.id.buttonConectarArduino);
        switchShake.setChecked(false);

        ///Asigno a boton su listener correspondiente
        btnAbrirCerrar.setOnClickListener(btnAbrirCerrarListener);
        btnConectarArduino.setOnClickListener(btnConectarArduinoListener);
        btnMeds.setOnClickListener(btnMedsListener);

        estadoBotiquin = E_CERRADO;

        ///Set como esta el botiquin y habilito/deshabilito botones
        if (estadoBotiquin == E_ABIERTO)
            botiquinAbierto();
        else
            botiquinCerrado();

        ///Vibrador para el sensor shake
        v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        ///Asigno sensores
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorShake = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorProx = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorLuz = sm.getDefaultSensor(Sensor.TYPE_LIGHT);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sm.unregisterListener(luzSensorListener);
        sm.unregisterListener(proximitySensorListener);

        conexionBluetooth.terminarConexion();
    }

    View.OnClickListener btnMedsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent nuevaVentana = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(nuevaVentana);
        }
    };


    View.OnClickListener btnConectarArduinoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            conexionBluetooth.conectar();
        }
    };

    View.OnClickListener btnAbrirCerrarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (estadoBotiquin == E_ABIERTO) {
                ///Envio mensaje para que cierre el botiquin y desactivo el sensor de luz.
                conexionBluetooth.enviarMensaje("C");
                botiquinCerrado();
                sm.unregisterListener(luzSensorListener);

            } else {
                ///Envio mensaje para que abra el botiquin y activo el sensor de luz.
                conexionBluetooth.enviarMensaje("A");
                botiquinAbierto();
                sm.registerListener(luzSensorListener,sensorLuz,SensorManager.SENSOR_DELAY_NORMAL);

            }

        }
    };

    ///METODO HABILITAR EL SHAKE CON SWITCH
    public void habilitarShake(View v) {

        ///SI HABILITO ABRIR/CERRAR AGITANDO
        if (switchShake.isChecked()) {
            btnAbrirCerrar.setEnabled(false);

            ///HABILITO LA LECTURA DEL SENSOR
            sm.registerListener(shakeSensorListener, sensorShake, SensorManager.SENSOR_DELAY_GAME);

            Toast.makeText(getApplicationContext(), "Shake habilitado", Toast.LENGTH_SHORT).show();

        }
        ///NO ESTA SELECCIONADO, habilito los botones que corresponden
        else {

            btnAbrirCerrar.setEnabled(true);

            if (estadoBotiquin == E_ABIERTO)
                botiquinAbierto();
            else
                botiquinCerrado();

            ///DESHABILITO LA LECTURA DEL SENSOR
            sm.unregisterListener(shakeSensorListener);

            Toast.makeText(getApplicationContext(), "Shake deshabilitado", Toast.LENGTH_SHORT).show();
        }
    }

    ///EVENTO ACELEROMETRO (SHAKE)
    SensorEventListener shakeSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            long tiempoAct = System.currentTimeMillis();

            if ((tiempoAct - ultShake) > 1000) {

                if ((Math.abs(event.values[0]) > 30 || Math.abs(event.values[1]) > 30 || Math.abs(event.values[2]) > 30)) {

                    ultShake = tiempoAct;

                    if (estadoBotiquin == E_CERRADO) {
                        conexionBluetooth.enviarMensaje("A");
                        botiquinAbierto();
                        sm.registerListener(luzSensorListener,sensorLuz,SensorManager.SENSOR_DELAY_NORMAL);
                        v.vibrate(100);

                    } else {
                        conexionBluetooth.enviarMensaje("C");
                        botiquinCerrado();
                        sm.unregisterListener(luzSensorListener);
                        v.vibrate(100);

                    }
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    ///EVENTO PARA SENSOR PROXIMIDAD
    SensorEventListener proximitySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.values[0] < sensorProx.getMaximumRange()) {

                conexionBluetooth.enviarMensaje("L");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };


    ///EVENTO PARA SENSOR LUZ
    SensorEventListener luzSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            if(event.values[0]<100){
                conexionBluetooth.enviarMensaje("Z");
            }else if(event.values[0]>210){
                conexionBluetooth.enviarMensaje("P");
            }else{
                conexionBluetooth.enviarMensaje("T");
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void botiquinAbierto() {

        btnAbrirCerrar.setText(S_CERRAR);
        estadoBotiquin = E_ABIERTO;
        tvEstBot.setText(S_ABIERTO);
    }

    private void botiquinCerrado() {

        btnAbrirCerrar.setText(S_ABRIR);
        estadoBotiquin = E_CERRADO;
        tvEstBot.setText(S_CERRADO);

    }

    ///------------------------------BLUETOOTH--------------------------------------//

    public class ConexionBluetooth {

        private BluetoothAdapter bluetoothAdapter;

        private Context context;

        private ConnectedThread connectedThread;

        private ConexionBluetooth (Context context){

            this.context = context;

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            encenderBT();

        }

        private void encenderBT(){

            if(!bluetoothAdapter.isEnabled()){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity)context).startActivityForResult(intent,1);
            }
        }

        private void conectar(){

                    try{
                        connectedThread = new ConnectedThread();
                        connectedThread.start();

                        mostrarMensaje(context,"Conectado al arduino");

                    }catch (Exception e){
                        mostrarMensaje(context,"No se pudo conectar");
                        e.printStackTrace();
                    }
        }

        private void terminarConexion(){

            try {
                connectedThread.bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        private void enviarMensaje(String mensaje){
            connectedThread.write(mensaje);
        }

        private void mostrarMensaje (Context context, String mensaje){
            Toast.makeText(context,mensaje,Toast.LENGTH_SHORT).show();
        }


        ///----------------------------HILO PARA LA CONEXION----------------------------///

        public class ConnectedThread extends Thread{

            private OutputStream mmOutStream;
            private BluetoothAdapter bluetoothAdapter;
            public BluetoothSocket bluetoothSocket;
            private BluetoothDevice bluetoothDevice;
            private OutputStream tmpOut = null;
            private String dirMAC = "00:18:E4:35:5A:64";
            private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

            public ConnectedThread (){

                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                bluetoothDevice = bluetoothAdapter.getRemoteDevice(dirMAC);

                try {
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try
                {
                    //Create I/O streams for connection
                    tmpOut = bluetoothSocket.getOutputStream();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                mmOutStream = tmpOut;

            }

            public void run()
            {
                //Inicio la conexion con el arduino

                try {
                    bluetoothSocket.connect();

                    recibirDatos();

                } catch (Exception e) {
                    try {
                        bluetoothSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }


            public void write(String input) {
                byte[] msgBuffer = input.getBytes();           //converts entered String into bytes

                try {
                    mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            public void recibirDatos(){
                try{
                    ///Abro el hilo secundario para escuchar lo que llega del arduino
                    ThreadAsynctask hilo=new ThreadAsynctask();
                    hilo.execute();

                }catch (Exception e){
                    mostrarMensaje(context,"No se pudo iniciar la escucha de datos");
                }
            }


            ///----------------------------------HILO PARA RECIBIR MENSAJES BLUETOOTH---------------------------///

            public class ThreadAsynctask extends AsyncTask<Void, String, Void> {

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
                            bytes += connectedThread.bluetoothSocket.getInputStream().read(buffer, bytes, buffer.length - bytes);

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

            private void evaluarDato(String dato){

                switch (dato){

                    case "1":{

                        Notificacion n = new Notificacion();

                        String [] medicamento = db.getMedicamento(1);

                        Medicamento m = new Medicamento(Integer.parseInt(medicamento[0]),medicamento[1],medicamento[2],medicamento[3],medicamento[4],medicamento[5]);

                        String resultado = m.descontarMed();

                        switch (resultado){
                            case "BAJO":{
                                n.generarNuevaNotificacion("ATENCIÓN", "Quedan solo "+m.getCantMed()+" "+ m.getNombre(), context);
                                db.deleteMedicamento("1");
                                db.saveMedicamento(m);
                                break;
                            }
                            case "OK":{
                                db.deleteMedicamento("1");
                                db.saveMedicamento(m);
                                break;
                            }
                            case "SIN":{
                                n.generarNuevaNotificacion("ATENCIÓN", "Ya no quedan "+m.getNombre(), context);
                                db.deleteMedicamento("1");
                                break;
                            }
                        }

                        break;

                    }
                    case "2":{

                        Notificacion n = new Notificacion();

                        String [] medicamento = db.getMedicamento(2);

                        Medicamento m = new Medicamento(Integer.parseInt(medicamento[0]),medicamento[1],medicamento[2],medicamento[3],medicamento[4],medicamento[5]);

                        String resultado = m.descontarMed();

                        switch (resultado){
                            case "BAJO":{
                                n.generarNuevaNotificacion("ATENCIÓN", "Quedan solo "+m.getCantMed()+" "+ m.getNombre(), context);
                                db.deleteMedicamento("2");
                                db.saveMedicamento(m);
                                break;
                            }
                            case "OK":{
                                db.deleteMedicamento("2");
                                db.saveMedicamento(m);
                                break;
                            }
                            case "SIN":{
                                n.generarNuevaNotificacion("ATENCIÓN", "Ya no quedan "+m.getNombre(), context);
                                db.deleteMedicamento("2");
                                break;
                            }
                        }

                        break;

                    }
                    case "3":{

                        Notificacion n = new Notificacion();

                        String [] medicamento = db.getMedicamento(3);

                        Medicamento m = new Medicamento(Integer.parseInt(medicamento[0]),medicamento[1],medicamento[2],medicamento[3],medicamento[4],medicamento[5]);

                        String resultado = m.descontarMed();

                        switch (resultado){
                            case "BAJO":{
                                n.generarNuevaNotificacion("ATENCIÓN", "Quedan solo "+m.getCantMed()+" "+ m.getNombre(), context);
                                db.deleteMedicamento("3");
                                db.saveMedicamento(m);
                                break;
                            }
                            case "OK":{
                                db.deleteMedicamento("3");
                                db.saveMedicamento(m);
                                break;
                            }
                            case "SIN":{
                                n.generarNuevaNotificacion("ATENCIÓN", "Ya no quedan "+m.getNombre(), context);
                                db.deleteMedicamento("3");
                                break;
                            }
                        }

                        break;

                    }
                    case "L":{

                        sm.registerListener(proximitySensorListener,sensorProx,SensorManager.SENSOR_DELAY_NORMAL);

                        Notificacion n = new Notificacion();

                        n.generarNuevaNotificacion("ATENCIÓN", "Hay luz dentro del botiquin!!", context);

                        break;
                    }
                    case "H":{

                        sm.registerListener(proximitySensorListener,sensorProx,SensorManager.SENSOR_DELAY_NORMAL);

                        Notificacion n = new Notificacion();

                        n.generarNuevaNotificacion("ATENCIÓN", "La humedad dentro del botiquin es alta!!", context);

                        break;
                    }
                    default:{

                        break;
                    }

                }
            }

        }
    }

}