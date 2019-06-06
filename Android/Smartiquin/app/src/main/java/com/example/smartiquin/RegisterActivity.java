package com.example.smartiquin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    // Botones y list view
    private Button btnAgregar;
    private Button btnEliminar;
    private ListView lviewMeds;

    // Arrays para almacenar los medicamentos que se ingresen
    private ArrayAdapter<String> adaptador;
    private ArrayList<String> myString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setea la pantalla en modo portrait, no permitiendo que rote
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Asigno las variables con sus elementos en la vista
        lviewMeds = findViewById(R.id.listviewMeds);
        btnAgregar = findViewById(R.id.buttonAdd);
        btnEliminar = findViewById(R.id.buttonDelete);

        // Datos mockeados hasta desarrollar el intercambio de data entre activities
        myString = new ArrayList<String>();

        myString.add("Ribotril");
        myString.add("Xanax");
        myString.add("T4");

        adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myString);

        lviewMeds.setAdapter(adaptador);

        // Listener para el botón agregar
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nuevaVentana = new Intent(RegisterActivity.this, FormActivity.class);
                startActivity(nuevaVentana);
            }
        });
    }
}
