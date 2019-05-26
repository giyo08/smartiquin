package com.example.smartiquin;

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




}
