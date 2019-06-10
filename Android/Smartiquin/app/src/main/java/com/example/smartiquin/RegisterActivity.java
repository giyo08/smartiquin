package com.example.smartiquin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    // Botones y list view
    private Button btnAgregar;
    private Button btnVaciarBD;
    private ListView lviewMeds;

    // Arrays para almacenar los medicamentos que se ingresen
    private ArrayAdapter<String> adaptador;
    private ArrayList<String> lista;

   /* //Lista que contendra los tres medicamentos
    private List<Medicamento> medicamentosList = new ArrayList<>();*/

   //base de datos
    private MedicamentosDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setea la pantalla en modo portrait, no permitiendo que rote
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ///creo la bd
        db = new MedicamentosDBHelper(getApplicationContext());

        // Asigno las variables con sus elementos en la vista
        lviewMeds = findViewById(R.id.listviewMeds);
        btnAgregar = findViewById(R.id.buttonAdd);
        btnVaciarBD = findViewById(R.id.buttonVaciarBD);

        ///Cargo la lista desde la db
        cargarListMedicamentos();

        // Listener para el botón agregar
        btnAgregar.setOnClickListener(btnAgregarListener);

        btnVaciarBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.eliminarAll();
                cargarListMedicamentos();
            }
        });

        ///Recibo datos de FormActivity
        try{
            String cadenaMedicamento = getIntent().getStringExtra("medicamento");
            String [] cadena = cadenaMedicamento.split("#");

            ///Agreggo el medicamento a la bd
            db.saveMedicamento(new Medicamento(lista.size()+1,cadena[0],cadena[1],cadena[2],cadena[3],cadena[4]));

            ///cargo la lista
            cargarListMedicamentos();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    View.OnClickListener btnAgregarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(lista.size() < 3){
                Intent nuevaVentana = new Intent(RegisterActivity.this, FormActivity.class);
                startActivity(nuevaVentana);
                finish();
            }else
                mostrarMensaje("Ya tiene registrado 3 medicamentos");
        }
    };

    private void mostrarMensaje(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show();
    }

    public void cargarListMedicamentos(){

        lista = db.cargarLista();
        adaptador = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,lista);
        lviewMeds.setAdapter(adaptador);

    }

}

