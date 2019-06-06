package com.example.smartiquin;

import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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


        // Seteo el listener para el botón Aceptar
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
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

    private void validarDatos() {
        String nombre = etNombreMed.getText().toString();
        String laboratorio = etLabMed.getText().toString();
        String fecha = etVencMed.getText().toString();


        boolean n = nombreValido(nombre, tilNombreMed);
        boolean l = nombreValido(laboratorio, tilLabMed);
        boolean f = fechaValida(fecha);

        if(n && l && f) {
            Toast.makeText(this,"Agrega registro", Toast.LENGTH_LONG).show();
        }
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

}
