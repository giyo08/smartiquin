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

///NOTA: AUNQUE PARECE QUE ESTA TOD0 DESACOMODADO EN LA VISTA , EN MI CELULAR ESTA PERFECTAMENTE ALINEADO XD
///NO SE PORQUE :)

public class MainActivity extends AppCompatActivity {

    ///Botones, switchs, textViews
    private Button btnAbrirCerrar;
    private Button btnMeds;
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
    private static final float LIMITE_SHAKE = 75;
    private long ultShake = 0;
    private Vibrator v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setea la pantalla en modo portrait, no permitiendo que rote
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///Asigno a las variables su correspondiente cosa
        btnAbrirCerrar = findViewById(R.id.buttonAbrirCerrar);
        btnMeds = findViewById(R.id.buttonMeds);
        tvEstBot = findViewById(R.id.textViewEstBot);
        switchShake = findViewById(R.id.switchShake);

        //Seteo el switch en falso
        switchShake.setChecked(false);

        ///Asigno a boton su listener correspondiente
        btnAbrirCerrar.setOnClickListener(btnAbrirCerrarListener);

        ///ACA TENGO QUE SETEAR EN QUE ESTADO ESTA EL BOTIQUIN DEPENDE DE LO QUE ME ENVIE EL ARDUINO
        ///AHORA PARA PRUEBAS LO PONGO EN CERRADO
        estadoBotiquin = E_CERRADO;

        ///Set como esta el botiquin y habilito/deshabilito botones
        if(estadoBotiquin == E_ABIERTO)
            botiquinAbierto();
        else
            botiquinCerrado();


        v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        ///Asigno sensores
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorShake = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorProx = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorLuz = sm.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Listener para el botón de registro
        btnMeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nuevaVentana = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(nuevaVentana);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        ///ACA VOY A ESTAR (CON UN THREAD) ESPERANDO QUE EL ARDUINO ME MANDE QUE EL BUZZER SE ENCENDIO
        ///CUANDO LO HAGA VOY A ACTIVAR EL SENSOR DE PROXIMIDAD
        //sm.registerListener(proximitySensorListener,sensorProx,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        ///SI CIERRO LA APLICACION Y EL SENSOR DE PROXIMIDAD ESTA ACTIVO LO VOY A CERRAR
        ///ESTO HAY QUE PROBAR QUE PASA SI LO DEJO ACTIVO Y SUENA EL BUZZER Y LA APP ESTA MINIMIZADA
        //sm.unregisterListener(proximitySensorListener);
    }

    View.OnClickListener btnAbrirCerrarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(estadoBotiquin == E_ABIERTO){
                botiquinCerrado();
                ///ENVIO DATO CERRAR AL ARDUINO
            }else{
                botiquinAbierto();
                ///ENVIO DATO ABRIR AL ARDUINO

                ///Cuando abro el botiquin activo el sensor de luz para saber si prendo el led?
                //sm.registerListener(luzSensorListener,sensorLuz,SensorManager.SENSOR_DELAY_NORMAL);

            }

        }
    };

    ///METODO HABILITAR EL SHAKE CON SWITCH
    public void habilitarShake(View v){

        ///SI HABILITO ABRIR/CERRAR AGITANDO
        if(switchShake.isChecked()){
           btnAbrirCerrar.setEnabled(false);

           ///HABILITO LA LECTURA DEL SENSOR
            sm.registerListener(shakeSensorListener,sensorShake,SensorManager.SENSOR_DELAY_GAME);

            Toast.makeText(getApplicationContext(),"Shake habilitado",Toast.LENGTH_SHORT).show();

        }
        ///NO ESTA SELECCIONADO, habilito los botones que corresponden
        else{

            btnAbrirCerrar.setEnabled(true);

            if(estadoBotiquin == E_ABIERTO)
                botiquinAbierto();
            else
                botiquinCerrado();

            ///DESHABILITO LA LECTURA DEL SENSOR
            sm.unregisterListener(shakeSensorListener);

            Toast.makeText(getApplicationContext(),"Shake deshabilitado",Toast.LENGTH_SHORT).show();
        }
    }

    ///EVENTO ACELEROMETRO (SHAKE)
    SensorEventListener shakeSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            long tiempoAct = System.currentTimeMillis();

            if((tiempoAct - ultShake) > 300){

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double a = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;

                /*float gX = x/SensorManager.GRAVITY_EARTH;
                float gY = x/SensorManager.GRAVITY_EARTH;
                float gZ = x/SensorManager.GRAVITY_EARTH;

                float a = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);*/

                if(a > LIMITE_SHAKE){

                    ultShake = tiempoAct;

                    if(estadoBotiquin == E_CERRADO){
                        botiquinAbierto();
                        v.vibrate(100);
                    }
                    else{
                        botiquinCerrado();
                        v.vibrate(100);
                    }
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    ///EVENTO PROXIMIDAD
    SensorEventListener proximitySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.values[0] < sensorProx.getMaximumRange()){
                ///ACA MANDO PARA APAGAR EL BUZZER

                ///Y DESACTIVO EL SENSOR DE PROXIMIDAD
                ///HAY QUE PROBAR SI LO PUEDO LLAMAR DESDE ACA A ESTO XD
                sm.unregisterListener(proximitySensorListener);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    ///EVENTO LUZ
    SensorEventListener luzSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            ///Poca luz?
            if(event.values[0] < 10){
                ///Encender led?

            }else{
                ///Apagar led?
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {



        }
    };

    private void botiquinAbierto(){

        btnAbrirCerrar.setText(S_CERRAR);
        estadoBotiquin = E_ABIERTO;
        tvEstBot.setText(S_ABIERTO);
    }

    private void botiquinCerrado(){

        btnAbrirCerrar.setText(S_ABRIR);
        estadoBotiquin = E_CERRADO;
        tvEstBot.setText(S_CERRADO);
    }

}
