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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class FormActivity extends AppCompatActivity {

    // Text input layouts, sus respectivos edit texts, radio buttons  y botones
    private TextInputLayout tilNombreMed;
    private TextInputLayout tilLabMed;
    private TextInputLayout tilVencMed;
    private TextInputLayout tilMedInic;
    private TextInputLayout tilAlertaMed;

    private TextView tvPosicion;

    private TextInputEditText etNombreMed;
    private TextInputEditText etLabMed;
    private TextInputEditText etVencMed;
    private TextInputEditText etMedInic;
    private TextInputEditText etAlarmaMed;

    private Button btnAceptar;
    private Button btnCancelar;
    private Spinner spinner;

    // Variables para los inputs y control de info ingresada
    private int pos = 0;
    private int posSwitch;
    private String nombreMed;
    private String labMed;
    private String vencMed;
    private String inicMed;
    private String alarmaMed;
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


        tvPosicion = findViewById(R.id.textViewPosicion);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.switch_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        btnAceptar = findViewById(R.id.buttonAceptar);
        btnCancelar = findViewById(R.id.buttonCancelar);

        etNombreMed.addTextChangedListener(camposCompletosTextWatcher);
        etLabMed.addTextChangedListener(camposCompletosTextWatcher);
        etVencMed.addTextChangedListener(camposCompletosTextWatcher);
        etMedInic.addTextChangedListener(camposCompletosTextWatcher);
        etAlarmaMed.addTextChangedListener(camposCompletosTextWatcher);


        intent = new Intent(this, RegisterActivity.class);

        // Seteo listeners
        btnAceptar.setOnClickListener(btnAceptarListener);
        btnCancelar.setOnClickListener(btnCancelarListener);
        spinner.setOnItemSelectedListener(spinnerListener);

    }///FIN DE ONCREATE


    ///Implementacion de los listeners
    View.OnClickListener btnAceptarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(validarDatos()){
                mostrarMensaje("Medicamento registrado");
                intent.putExtra("medicamento",cadenaAEnviar);
                startActivity(intent);
                finish();
            }else
                mostrarMensaje("Campos invalidos");
        }
    };

    View.OnClickListener btnCancelarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent nuevaVentana = new Intent(FormActivity.this, RegisterActivity.class);
            startActivity(nuevaVentana);
            finish();
        }
    };

    AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            posSwitch = position+1;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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
        boolean ps = switchValido();
        boolean n = nombreValido(nombreMed, tilNombreMed);
        boolean l = nombreValido(labMed, tilLabMed);
        boolean f = fechaValida(vencMed, tilVencMed);
        boolean cp = cantPastillasValidas(inicMed,tilMedInic);
        boolean cl = cantPastillasValidas(alarmaMed,tilAlertaMed);

        if(n && l && f && cp && cl && ps) {
            ///Si esta tod0 OK , armo una cadena para pasar los datos a la activity que tiene la lista de registros
            cadenaAEnviar = posSwitch+"#"+nombreMed+"#"+labMed+"#"+vencMed+"#"+inicMed+"#"+alarmaMed;

            return true;
        }

        return false;
    }

    private boolean switchValido(){

        MedicamentosDBHelper db = new MedicamentosDBHelper(getApplicationContext());

        ArrayList<String> ids;

        ids = db.getIDs();

        db.close();

        if(ids.size() == 0)
            return true;
        else
            for(String s : ids)
                if(Integer.parseInt(s) == posSwitch){
                    tvPosicion.setError("Switch ya utilizado");
                    return false;
                }


        return true;
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

}
