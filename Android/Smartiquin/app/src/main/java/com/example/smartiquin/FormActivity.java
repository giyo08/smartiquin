package com.example.smartiquin;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Parcelable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class FormActivity extends AppCompatActivity {

    // Text input layouts, sus respectivos edit texts, radio buttons  y botones
    private TextInputLayout tilNombreMed;
    private TextInputLayout tilLabMed;
    private TextInputLayout tilVencMed;
    private TextInputLayout tilMedInic;
    private TextInputLayout tilAlertaMed;

    private TextInputEditText etNombreMed;
    private TextInputEditText etLabMed;
    private TextInputEditText etVencMed;
    private TextInputEditText etMedInic;
    private TextInputEditText etAlarmaMed;

    private RadioButton rbtnDia;
    private RadioButton rbtnNoche;

    private Button btnAceptar;
    private Button btnCancelar;

    private String cadenaAEnviar;

    private Intent intent;

    public int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setea la pantalla en modo portrait, no permitiendo que rote
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // Asigno las variables con sus elementos en la vista
        tilNombreMed = findViewById(R.id.textInputNombre);
        tilLabMed = findViewById(R.id.textInputLaboratorio);
        tilVencMed = findViewById(R.id.textInputVencimiento);
        tilMedInic = findViewById(R.id.textInputCantMed);
        tilAlertaMed = findViewById(R.id.textInputAlertaMed);

        etNombreMed = findViewById(R.id.editTextNombre);
        etLabMed = findViewById(R.id.editTextLab);
        etVencMed = findViewById(R.id.editTextVenc);
        etMedInic = findViewById(R.id.editTextCantMed);
        etAlarmaMed = findViewById(R.id.editTextAlertaMed);

        rbtnDia = findViewById(R.id.radioButtonDia);
        rbtnNoche = findViewById(R.id.radioButtonNoche);

        btnAceptar = findViewById(R.id.buttonAceptar);
        btnCancelar = findViewById(R.id.buttonCancelar);

        etNombreMed.addTextChangedListener(camposCompletosTextWatcher);
        etLabMed.addTextChangedListener(camposCompletosTextWatcher);
        etVencMed.addTextChangedListener(camposCompletosTextWatcher);

        intent = new Intent(this, RegisterActivity.class);

        // Seteo el listener para el botón Aceptar
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarDatos()){
                    mostrarMensaje("Medicamento registrado");
                    intent.putExtra("medicamento",cadenaAEnviar);
                    startActivity(intent);
                    finish();
                }else
                    mostrarMensaje("Campos invalidos");
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nuevaVentana = new Intent(FormActivity.this, RegisterActivity.class);
                startActivity(nuevaVentana);
                finish();
            }
        });
    }

    private TextWatcher camposCompletosTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            pos = etVencMed.getText().length();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String nombreMed = etNombreMed.getText().toString();
            String labMed = etLabMed.getText().toString();
            String vencMed = etVencMed.getText().toString();

            if(etVencMed.getText().length() == 2 && pos!=3) {
                etVencMed.setText(etVencMed.getText().toString()+"/");
                etVencMed.setSelection(3);
            }

            if(etVencMed.getText().length() == 7) {
                etVencMed.clearFocus();
            }

            btnAceptar.setEnabled(!nombreMed.isEmpty() && !labMed.isEmpty() && !vencMed.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private boolean validarDatos() {
        String nombre = etNombreMed.getText().toString();
        String laboratorio = etLabMed.getText().toString();
        String fecha = etVencMed.getText().toString();
        String cantMed = etMedInic.getText().toString();
        String cantLim = etAlarmaMed.getText().toString();


        boolean n = nombreValido(nombre, tilNombreMed);
        boolean l = nombreValido(laboratorio, tilLabMed);
        boolean f = fechaValida(fecha);
        boolean cp = cantPastillasValidas(cantMed,tilMedInic);
        boolean cl = cantPastillasValidas(cantLim,tilAlertaMed);

        if(n && l && f && cp && cl) {
            ///Si esta tod0 OK , armo una cadena para pasar los datos a la activity que tiene la lista de registros
            cadenaAEnviar = nombre+"#"+laboratorio+"#"+fecha+"#"+cantMed+"#"+cantLim;

            return true;
        }

        return false;
    }

    private boolean nombreValido(String nombre, TextInputLayout campo) {
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        if (!patron.matcher(nombre).matches() || nombre.length() > 30) {
            campo.setError("Maximo 30 caracteres alfanumericos");
            return false;
        } else {
            campo.setError(null);
        }

        return true;
    }

    private boolean fechaValida(String fecha) {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("MM/yyyy");
            formatoFecha.setLenient(false);
            formatoFecha.parse(fecha);
        } catch (ParseException e) {
            tilVencMed.setError("Formato fecha mm/yyyy");
            return false;
        }
        tilVencMed.setError(null);
        return true;
    }

    private boolean cantPastillasValidas(String cantPastillas, TextInputLayout campo){

        if(Integer.parseInt(cantPastillas) <= 0){
            campo.setError("Ingrese una cantidad valida");
            return false;
        }else{
            campo.setError(null);
        }

        return true;
    }

    private void mostrarMensaje(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show();
    }



}
