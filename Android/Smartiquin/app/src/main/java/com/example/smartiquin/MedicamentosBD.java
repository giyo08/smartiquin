package com.example.smartiquin;

import android.provider.BaseColumns;

public class MedicamentosBD {

    public static abstract class MedicamentosEntry implements BaseColumns {
        public static final String TABLE_NAME ="medicamentos";

        public static final String ID = "id";
        public static final String NOMBRE = "nombre";
        public static final String LABORATORIO = "laboratorio";
        public static final String FECHA = "fecha";
        public static final String CANTMED = "cantMed";
        public static final String CANTLIM = "cantLim";
        public static final String OPCIONHORA = "opcionHora";
    }

}
