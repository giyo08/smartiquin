package com.example.smartiquin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

    ///Sensores
    private SensorManager sm;
    private Sensor sensorShake;
    private Sensor sensorProx;
    private Sensor sensorLuz;

    ///Variables para Shake
    private long ultShake = 0;
    private Vibrator v;

    ///Bluetooth
    private ConexionBluetooth conexionBluetooth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setea la pantalla en modo portrait, no permitiendo que rote
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

        ///ACA TENGO QUE SETEAR EN QUE ESTADO ESTA EL BOTIQUIN DEPENDE DE LO QUE ME ENVIE EL ARDUINO
        ///AHORA PARA PRUEBAS LO PONGO EN CERRADO
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

        try{
            String aviso = getIntent().getStringExtra("Encender");

            sm.registerListener(proximitySensorListener,sensorProx,SensorManager.SENSOR_DELAY_NORMAL);

            Notificacion n = new Notificacion();

            if(aviso.equals("L"))
                n.generarNuevaNotificacion("ATENCIÓN", "La luz del botiquin se encuentra encendida!!", this);
            else
                n.generarNuevaNotificacion("ATENCIÓN", "La humedad dentro del botiquin es alta!!", this);

        }catch (Exception e){
            e.printStackTrace();
        }
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

                if ((Math.abs(event.values[0]) > 50 || Math.abs(event.values[1]) > 50 || Math.abs(event.values[2]) > 50)) {

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
                sm.unregisterListener(proximitySensorListener);
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

            if( event.values[0] > (sensorLuz.getMaximumRange() / 2))
                conexionBluetooth.enviarMensaje("P");
            else
                conexionBluetooth.enviarMensaje("Z");
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

}