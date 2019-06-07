package com.example.smartiquin;

import android.os.Parcel;
import android.os.Parcelable;

public class Medicamento implements Parcelable {

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

    public int getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public String getLaboratorio() {
        return laboratorio;
    }
    public String getFecha() { return fecha; }
    public int getCantMed() {
        return cantMed;
    }
    public int getCantLim() {
        return cantLim;
    }

    public boolean comprobarLimite(){
        return cantMed <= cantLim;
    }

    public void descontarMedicamento(){
        if(cantMed > 0)
            cantMed--;
    }

    public boolean comprobarFechaVencimiento(){
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
