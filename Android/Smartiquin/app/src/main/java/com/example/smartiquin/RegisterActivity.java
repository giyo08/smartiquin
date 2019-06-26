package com.example.smartiquin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    // Botones y list view
    private Button btnAgregar;
    private Button btnEliminar;
    private ListView lviewMeds;

    // Arrays para almacenar los medicamentos que se ingresen
    private ArrayList<String> lista;

   //base de datos
    private MedicamentosDBHelper db;

    private String idSeleccionado;

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
        btnEliminar = findViewById(R.id.buttonEliminar);

        btnEliminar.setEnabled(false);
        btnEliminar.setVisibility(View.INVISIBLE);

        ///Cargo la lista desde la db
        cargarListMedicamentos();

        // Listener para el botón agregar
        btnAgregar.setOnClickListener(btnAgregarListener);
        btnEliminar.setOnClickListener(btnEliminarListener);

        //Listener para la seleccion de items de la lista
        lviewMeds.setOnItemClickListener(lviewMedsItemListener);

        ///Recibo datos de FormActivity
        try{
            String cadenaMedicamento = getIntent().getStringExtra("medicamento");
            String [] cadena = cadenaMedicamento.split("#");

            ///Agreggo el medicamento a la bd
            db.saveMedicamento(new Medicamento(Integer.parseInt(cadena[0]),cadena[1],cadena[2],cadena[3],cadena[4],cadena[5],cadena[6]));

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

    AdapterView.OnItemClickListener lviewMedsItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(lista.get(position) !=  "<Vacio>"){

                String[] datos = db.getMedicamento(position+1);

                String mensaje = "Nro switch: "+datos[0]+'\n'
                        +"Nombre: "+datos[1]+'\n'
                        +"Laboratorio: "+datos[2]+'\n'
                        +"Fecha de Vencimiento: "+datos[3]+'\n'
                        +"Cantidad de Medicamentos: "+datos[4]+'\n'
                        +"Hora Alarma: "+datos[6];

                btnEliminar.setText("Eliminar "+datos[1]);
                idSeleccionado = position+1+"";

                btnEliminar.setEnabled(true);
                btnEliminar.setVisibility(View.VISIBLE);

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Información");
                builder.setMessage(mensaje);
                builder.create().show();
            }
        }
    };

    ///Listeners para botones
    View.OnClickListener btnAgregarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(comprobarCantRegistros() < 3){
                Intent nuevaVentana = new Intent(RegisterActivity.this, FormActivity.class);
                startActivity(nuevaVentana);
                finish();
            }else
                mostrarMensaje("Ya tiene registrado 3 medicamentos");
        }
    };

    View.OnClickListener btnEliminarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            db.deleteMedicamento(idSeleccionado);
            mostrarMensaje("Eliminado correctamente");
            cargarListMedicamentos();

            idSeleccionado=null;
            btnEliminar.setEnabled(false);
            btnEliminar.setVisibility(View.INVISIBLE);
        }
    };

    private void mostrarMensaje(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show();
    }

    public void cargarListMedicamentos(){

        lista = db.cargarLista();
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,lista);
        lviewMeds.setAdapter(adaptador);

    }

    ///Ve cuantos medicamentos hay registrados
    public int comprobarCantRegistros(){

        int cantidad=0;

        for(String s : lista)
            if(s != "<Vacio>" )
                cantidad++;

        return cantidad;
    }

}

