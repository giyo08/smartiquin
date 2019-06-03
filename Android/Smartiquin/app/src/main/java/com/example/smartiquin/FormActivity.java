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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.regex.Pattern;

public class FormActivity extends AppCompatActivity {
    private TextInputLayout tilNombreMed;
    private TextInputLayout tilLabMed;
    private TextInputLayout tilVencMed;
    private TextInputEditText etNombreMed;
    private TextInputEditText etLabMed;
    private TextInputEditText etVencMed;
    private Button btnAceptar;
    private Button btnCancelar;

    public int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setea la pantalla en modo portrait, no permitiendo que rote
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        tilNombreMed = findViewById(R.id.textInputNombre);
        tilLabMed = findViewById(R.id.textInputLaboratorio);
        tilVencMed = findViewById(R.id.textInputVencimiento);
        etNombreMed = findViewById(R.id.editTextNombre);
        etLabMed = findViewById(R.id.editTextLab);
        etVencMed = findViewById(R.id.editTextVenc);

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


        boolean n = nombreValido(nombre);
        boolean l = laboratorioValido(laboratorio);
        boolean f = fechaValida(fecha);

        if(n && l && f) {
            Toast.makeText(this,"Agrega registro", Toast.LENGTH_LONG).show();
        }
    }

    private boolean nombreValido(String nombre) {
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        if (!patron.matcher(nombre).matches() || nombre.length() > 30) {
            etNombreMed.setError("No debe ser mayor a 30 caracteres alfanumericos");
            return false;
        } else {
            tilNombreMed.setError(null);
        }

        return true;
    }

    private boolean laboratorioValido(String nombre) {
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        if (!patron.matcher(nombre).matches() || nombre.length() > 30) {
            tilLabMed.setError("No debe ser mayor a 30 caracteres alfanumericos");
            return false;
        } else {
            tilLabMed.setError(null);
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
