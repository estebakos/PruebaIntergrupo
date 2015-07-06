package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TEBAN on 05/07/2015.
 */
public class ProspectDbHelper extends SQLiteOpenHelper {
        private static int version = 1;
        private static String name = "Prospect" ;
        private static SQLiteDatabase.CursorFactory factory = null;
        private static final String Cedula = "Cedula";
        private static final String Nombre = "Nombre";
        private static final String Apellido = "Apellido";
        private static final String Zona = "Zona";
        private static final String Direccion = "Direccion";
        private static final String Telefono = "Telefono";
        private static final String Ciudad = "Ciudad";
        private static final String Estado = "Estado";

    public ProspectDbHelper(Context context)
        {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("CREATE TABLE PROSPECT(" + Cedula + " TEXT PRIMARY KEY, " + Zona +
                    " TEXT, " + Nombre + "  TEXT, " + Apellido + " TEXT, "
                    + Direccion + " TEXT, " + Telefono + " TEXT, " + Ciudad + " TEXT, " + Estado + " INTEGER)");
        }

        public void insertProspect(SQLiteDatabase db, String sqlSentence)
        {
            db.execSQL("INSERT INTO PROSPECT(Cedula, Zona, Nombre, Apellido, Direccion, Telefono, Ciudad, Estado)" +
                    " VALUES('1152446595','19001', 'Esteban', 'Marin', 'Cra 72 # 30 - 56', '2652534', 'Medellin', 0 )");
            //db.execSQL(sqlSentence);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {

        }
}