package com.example.smartiquin;

import android.content.ContentValues;

public class Medicamento {

    private int id; //Posicion del Switch
    private String nombre;
    private String laboratorio;
    private String fecha;
    private int cantMed;
    private int cantLim;

    public Medicamento(int id, String nombre, String laboratorio, String fecha, String cantMed, String cantLim){

        this.id = id;
        this.nombre = nombre;
        this.laboratorio = laboratorio;
        this.fecha = fecha;
        this.cantMed = Integer.parseInt(cantMed);
        this.cantLim = Integer.parseInt(cantLim);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(MedicamentosBD.MedicamentosEntry.ID, id);
        values.put(MedicamentosBD.MedicamentosEntry.NOMBRE, nombre);
        values.put(MedicamentosBD.MedicamentosEntry.LABORATORIO, laboratorio);
        values.put(MedicamentosBD.MedicamentosEntry.FECHA, fecha);
        values.put(MedicamentosBD.MedicamentosEntry.CANTMED, cantMed);
        values.put(MedicamentosBD.MedicamentosEntry.CANTLIM, cantLim);
        return values;
    }

    public String descontarMed() {

        if (cantMed > 0) {
            cantMed--;

            if (cantMed == 0)
                return "SIN";

            if (cantMed <= cantLim)
                return "BAJO";

            return "OK";
        }

        return "SIN";
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantMed() {
        return cantMed;
    }
}
