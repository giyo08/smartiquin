package com.example.smartiquin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MedicamentosDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Medicamentos.db";

    public MedicamentosDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + MedicamentosBD.MedicamentosEntry.TABLE_NAME + " ("
                    + MedicamentosBD.MedicamentosEntry.ID + " INTEGER NOT NULL,"
                    + MedicamentosBD.MedicamentosEntry.NOMBRE + " TEXT NOT NULL,"
                    + MedicamentosBD.MedicamentosEntry.LABORATORIO + " TEXT NOT NULL,"
                    + MedicamentosBD.MedicamentosEntry.FECHA + " TEXT NOT NULL,"
                    + MedicamentosBD.MedicamentosEntry.CANTMED + " INTEGER NOT NULL,"
                    + MedicamentosBD.MedicamentosEntry.CANTLIM + " INTEGER NOT NULL,"
                    + "UNIQUE (" + MedicamentosBD.MedicamentosEntry.ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            ///No creo que usemos esto
    }

    public long saveMedicamento(Medicamento medicamento) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                MedicamentosBD.MedicamentosEntry.TABLE_NAME,
                null,
                medicamento.toContentValues());

    }

    //Delete por id
    public int deleteMedicamento(String medicamentoId) {
        return getWritableDatabase().delete(
                MedicamentosBD.MedicamentosEntry.TABLE_NAME,
                MedicamentosBD.MedicamentosEntry.ID + " LIKE ?",
                new String[]{medicamentoId});
    }

    public void limpiarBD(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from "+ MedicamentosBD.MedicamentosEntry.TABLE_NAME);
        database.close();
    }


    public String[] getMedicamento(int id){

        String[] datos = new String[5];

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + MedicamentosBD.MedicamentosEntry.TABLE_NAME +" WHERE "+ MedicamentosBD.MedicamentosEntry.ID +"= "+id;
        Cursor registros = sqLiteDatabase.rawQuery(query,null);

        if(registros.moveToFirst()){
            for(int i = 1 ; i<6;i++){
                datos[i-1]= registros.getString(i);
            }
        }else{

            datos[0]= "No se encontro a ";
        }
        sqLiteDatabase.close();
        return datos;

    }


    public ArrayList<String> cargarLista(){

        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + MedicamentosBD.MedicamentosEntry.TABLE_NAME;
        Cursor registros = sqLiteDatabase.rawQuery(query,null);

        if(registros.moveToFirst()){
            do{
                lista.add(registros.getString(2));
            }while(registros.moveToNext());
        }

        registros.close();

        return lista;
    }
}
