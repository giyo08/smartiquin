package com.example.smartiquin;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
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
    private RadioGroup rgBotones;
    private Button btnAceptar;
    private Button btnCancelar;

    // Variables para los inputs y control de info ingresada
    private int pos = 0;
    private String nombreMed;
    private String labMed;
    private String vencMed;
    private String inicMed;
    private String alarmaMed;
    private String selBoton;
    private int rbuttonId;
    private String cadenaAEnviar;

    private Intent intent;

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

        rgBotones = findViewById(R.id.radioGroupBotones);

        btnAceptar = findViewById(R.id.buttonAceptar);
        btnCancelar = findViewById(R.id.buttonCancelar);

        etNombreMed.addTextChangedListener(camposCompletosTextWatcher);
        etLabMed.addTextChangedListener(camposCompletosTextWatcher);
        etVencMed.addTextChangedListener(camposCompletosTextWatcher);
        etMedInic.addTextChangedListener(camposCompletosTextWatcher);
        etAlarmaMed.addTextChangedListener(camposCompletosTextWatcher);

        //rbuttonId.radioButtonGroup.getCheckedRadioButtonId();
        intent = new Intent(this, RegisterActivity.class);

        // Seteo el listener para los radio buttons
        rgBotones.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radioButtonDia){
                    // selBoton =
                }else if (checkedId == R.id.radioButtonNoche){
                    // hacer algo
                }
            }
        });

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
            nombreMed = etNombreMed.getText().toString();
            labMed = etLabMed.getText().toString();
            vencMed = etVencMed.getText().toString();
            inicMed = etMedInic.getText().toString();
            alarmaMed = etAlarmaMed.getText().toString();

            if(etVencMed.getText().length() == 2 && pos!=3) {
                etVencMed.setText(etVencMed.getText().toString()+"/");
                etVencMed.setSelection(3);
            }

            if(etVencMed.getText().length() == 7) {
                etVencMed.clearFocus();
            }

            btnAceptar.setEnabled(!nombreMed.isEmpty() && !labMed.isEmpty() && !vencMed.isEmpty()
                    && !inicMed.isEmpty() && !alarmaMed.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private boolean validarDatos() {
        //selBoton = rgBotones

        boolean n = nombreValido(nombreMed, tilNombreMed);
        boolean l = nombreValido(labMed, tilLabMed);
        boolean f = fechaValida(vencMed, tilVencMed);
        boolean cp = cantPastillasValidas(inicMed,tilMedInic);
        boolean cl = cantPastillasValidas(alarmaMed,tilAlertaMed);

        if(n && l && f && cp && cl) {
            ///Si esta tod0 OK , armo una cadena para pasar los datos a la activity que tiene la lista de registros
            cadenaAEnviar = nombreMed+"#"+labMed+"#"+vencMed+"#"+inicMed+"#"+alarmaMed;

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

    private boolean fechaValida(String fecha, TextInputLayout campo) {
        String [] partes = fecha.split("/");
        String mes = partes[0];
        String anio = partes[1];

        Pattern patronMes = Pattern.compile("^(0[1-9]|1[0-2])");
        Pattern patronAnio = Pattern.compile("^(2019|20[2-3][0-9])");

        if(!patronMes.matcher(mes).matches()) {
            campo.setError("Mes debe ser entre 1 y 12");
            return false;
        } else {
            campo.setError(null);
        }

        if(!patronAnio.matcher(anio).matches()) {
            campo.setError("Año debe ser entre 2019 y 2039");
            return false;
        } else {
            campo.setError(null);
        }

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

    public void algo(){
        

    }


}
