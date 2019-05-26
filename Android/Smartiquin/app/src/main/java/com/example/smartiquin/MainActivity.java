package com.example.smartiquin;

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

///NOTA: AUNQUE PARECE QUE ESTA TODO DESACOMODADO EN LA VISTA , EN MI CELULAR ESTA PERFECTAMENTE ALINEADO XD
///NO SE PORQUE :)

public class MainActivity extends AppCompatActivity {

    ///Botones, switchs, textViews
    private Button btnAbrir;
    private Button btnCerrar;
    private Switch switchShake;
    private TextView tvEstBot;

    ///EstadoBotiquin
    private Boolean estadoBotiquin;
    private static final Boolean E_ABIERTO = true;
    private static final Boolean E_CERRADO = false;

    ///String
    private static final String S_ABIERTO = "ABIERTO";
    private static final String S_CERRADO = "CERRADO";

    ///Sensores
    private SensorManager sm;
    private Sensor sensorShake;

    ///Variables para Shake
    private static final float LIMITE_SHAKE = 75;
    private long ultShake = 0;
    private Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///Asigno a las variables su correspondiente cosa
        btnAbrir = findViewById(R.id.buttonAbrir);
        btnCerrar = findViewById(R.id.buttonCerrar);
        tvEstBot = findViewById(R.id.textViewEstBot);
        switchShake = findViewById(R.id.switchShake);

        //Seteo el switch en falso
        switchShake.setChecked(false);

        ///ACA TENGO QUE SETEAR EN QUE ESTADO ESTA EL BOTIQUIN DEPENDE DE LO QUE ME ENVIE EL ARDUINO
        ///AHORA PARA PRUEBAS LO PONGO EN CERRADO
        estadoBotiquin = E_CERRADO;

        ///Seteo como esta el botiquin y habilito/deshabilito botones
        if(estadoBotiquin == E_ABIERTO){
            tvEstBot.setText(S_ABIERTO);
            btnAbrir.setEnabled(false);
        }
        else{
            tvEstBot.setText(S_CERRADO);
            btnCerrar.setEnabled(false);
        }

        v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        ///Asigno sensores
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorShake = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void abrirBotiquin(View v){

        ///Valido por las dudas
        if(estadoBotiquin == E_ABIERTO)
            return;

        tvEstBot.setText(S_ABIERTO);

        ///ACA DEBERIA MANDAR AL ARDUINO ALGO PARA INDICAR QUE SE ABRA

        ///Habilito/deshabilito botones
        btnCerrar.setEnabled(true);
        btnAbrir.setEnabled(false);
    }

    public void cerrarBotiquin(View v){

        //Valido por las dudas
        if(estadoBotiquin = E_CERRADO)
            return;

        tvEstBot.setText(S_CERRADO);

        //ACA DEBERIA MANDAR AL ARDUINO ALGO PARA INDICAR QUE SE CIERRE

        ///Habilito/deshabilito botones
        btnCerrar.setEnabled(false);
        btnAbrir.setEnabled(true);
    }

    public void habilitarShake(View v){

        ///SI HABILITO ABRIR/CERRAR AGITANDO
        if(switchShake.isChecked()){
            btnAbrir.setEnabled(false);
            btnAbrir.setEnabled(false);

            ///HABILITO LA LECTURA DEL SENSOR
            sm.registerListener(shakeSensorListener,sensorShake,SensorManager.SENSOR_DELAY_GAME);
        }
        ///NO ESTA SELECCIONADO, habilito los botones que corresponden
        else{
            if(estadoBotiquin == E_ABIERTO){
                btnAbrir.setEnabled(false);
                btnCerrar.setEnabled(true);
            }
            else{
                btnCerrar.setEnabled(false);
                btnAbrir.setEnabled(true);
            }

            ///DESHABILITO LA LECTURA DEL SENSOR
            sm.unregisterListener(shakeSensorListener);
        }
    }

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
                        tvEstBot.setText(S_ABIERTO);
                        estadoBotiquin = E_ABIERTO;
                        v.vibrate(100);
                    }
                    else{
                        tvEstBot.setText(S_CERRADO);
                        estadoBotiquin = E_CERRADO;
                        v.vibrate(100);
                    }
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}
