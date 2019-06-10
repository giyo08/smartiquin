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
                    + MedicamentosBD.MedicamentosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + MedicamentosBD.MedicamentosEntry.ID + " INTEGER NOT NULL,"
                    + MedicamentosBD.MedicamentosEntry.NOMBRE + " TEXT NOT NULL,"
                    + MedicamentosBD.MedicamentosEntry.LABORATORIO + " TEXT NOT NULL,"
                    + MedicamentosBD.MedicamentosEntry.FECHA + " TEXT NOT NULL,"
                    + MedicamentosBD.MedicamentosEntry.CANTMED + " TEXT NOT NULL,"
                    + MedicamentosBD.MedicamentosEntry.CANTLIM + " TEXT NOT NULL,"
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
    public int deleteMedicamento(String lawyerId) {
        return getWritableDatabase().delete(
                MedicamentosBD.MedicamentosEntry.TABLE_NAME,
                MedicamentosBD.MedicamentosEntry.ID + " LIKE ?",
                new String[]{lawyerId});
    }

    //Delete all
    public void eliminarAll(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from "+ MedicamentosBD.MedicamentosEntry.TABLE_NAME);
        database.close();
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

        return lista;

    }

}
