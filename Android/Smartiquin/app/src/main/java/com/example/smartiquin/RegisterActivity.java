package com.example.smartiquin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    // Botones y list view
    private Button btnAgregar;
    private Button btnEliminar;
    private ListView lviewMeds;

    // Arrays para almacenar los medicamentos que se ingresen
    private ArrayAdapter<String> adaptador;
    private ArrayList<String> myString = new ArrayList<>();

    //Lista que contendra los tres medicamentos
    private List<Medicamento> medicamentosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setea la pantalla en modo portrait, no permitiendo que rote
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (savedInstanceState != null){
            medicamentosList = savedInstanceState.getParcelableArrayList("Lista");
            actualizarLista();
            mostrarMensaje("RECUPERO DATOS");
        }

        // Asigno las variables con sus elementos en la vista
        lviewMeds = findViewById(R.id.listviewMeds);
        btnAgregar = findViewById(R.id.buttonAdd);
        btnEliminar = findViewById(R.id.buttonDelete);

        // Datos mockeados hasta desarrollar el intercambio de data entre activities
        /*myString = new ArrayList<String>();

        myString.add("Ribotril");
        myString.add("Xanax");
        myString.add("T4");

        adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myString);

        lviewMeds.setAdapter(adaptador);*/

        // Listener para el botón agregar
        btnAgregar.setOnClickListener(btnAgregarListener);

        ///Recibo datos de FormActivity
        try{
            String cadenaMedicamento = getIntent().getStringExtra("medicamento");
            String [] cadena = cadenaMedicamento.split("#");
            medicamentosList.add(new Medicamento(medicamentosList.size()+1,cadena[0],cadena[1],cadena[2],cadena[3],cadena[4]));
            actualizarLista();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    View.OnClickListener btnAgregarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(medicamentosList.size() < 3){
                Intent nuevaVentana = new Intent(RegisterActivity.this, FormActivity.class);
                startActivity(nuevaVentana);
                finish();
            }else
                mostrarMensaje("Ya tiene registrado 3 medicamentos");
        }
    };

    private void actualizarLista(){

        for(Medicamento m: medicamentosList){
            myString.add(m.getNombre()+'\t'+'\t'+medicamentosList.size());
        }

        adaptador = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,myString);

        lviewMeds.setAdapter(adaptador);
    }

    private void mostrarMensaje(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Lista",(ArrayList<? extends Parcelable>) medicamentosList);

        mostrarMensaje("GUARDO DATOS");

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            medicamentosList = savedInstanceState.getParcelableArrayList("Lista");
            actualizarLista();
            mostrarMensaje("RECUPERO DATOS");
        }


    }
}
