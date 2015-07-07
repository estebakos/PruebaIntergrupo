package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;

import model.Prospect;

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
        private static final String Editado = "Editado";
        private SQLiteDatabase db;

    public ProspectDbHelper(Context context)
        {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            this.db = db;

        }

        public void CreateProspectTable(SQLiteDatabase db)
        {
            try {
                db.execSQL("DROP TABLE IF EXISTS PROSPECT");
                db.execSQL("CREATE TABLE PROSPECT(" + Cedula + " TEXT PRIMARY KEY, " + Zona +
                        " TEXT, " + Nombre + "  TEXT, " + Apellido + " TEXT, "
                        + Direccion + " TEXT, " + Telefono + " TEXT, " + Ciudad + " TEXT, " + Estado + " INTEGER," +
                        " " + Editado + " INTEGER )");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        public void insertTestProspects(SQLiteDatabase db)
        {
            try {
                db.execSQL("INSERT INTO PROSPECT(Cedula, Nombre, Apellido, Telefono , Estado, Editado)" +
                        " VALUES('1152446595' , 'Esteban', 'Marin', '2652534', 0, 0 )");
                db.execSQL("INSERT INTO PROSPECT(Cedula, Nombre, Apellido, Telefono , Estado, Editado)" +
                        " VALUES('1152446594' , 'Diego', 'Perez', '2652538', 0, 0 )");
                db.execSQL("INSERT INTO PROSPECT(Cedula, Nombre, Apellido, Telefono , Estado, Editado)" +
                        " VALUES('1152446593' , 'Carlos', 'Rueda', '2652532', 0, 0 )");
            }
            catch (Exception ex)
            {
            ex.printStackTrace();
            }
        }

        public boolean insertProspect(SQLiteDatabase db, String Cedula, String Nombre, String Apellido, String Telefono, int Estado)
        {
            try {
                ContentValues cv = new ContentValues();
                cv.put(this.Nombre, Nombre); //These Fields should be your String values of actual column names
                cv.put(this.Apellido, Apellido);
                cv.put(this.Telefono, Telefono);
                cv.put(this.Estado, Estado);
                cv.put(Editado, 1);

                String selectQuery = "SELECT  * FROM " + name + " WHERE CEDULA =" +Cedula;
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    if(cursor.getInt(cursor.getColumnIndex(Editado))== 1)
                    {
                        return false;
                    }
                    else
                    {
                        db.update(name, cv, "Cedula " + "=" + Cedula, null);
                    }
                }
                else {
                    cv.put(this.Cedula, Cedula);
                    long b = db.insert(name, null,cv);
                }
                return true;
            }
            catch (Exception ex)
            {
            ex.printStackTrace();
            }
            return false;
        }

    public boolean editProspect(SQLiteDatabase db, String Cedula, String Nombre, String Apellido, String Telefono, int Estado) throws SQLException {
        try {
            String selectQuery = "SELECT  * FROM " + name + " WHERE CEDULA =" +Cedula;
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                   ContentValues cv = new ContentValues();
                    cv.put(this.Nombre, Nombre); //These Fields should be your String values of actual column names
                    cv.put(this.Apellido, Apellido);
                    cv.put(this.Telefono, Telefono);
                    cv.put(this.Estado, Estado);
                    cv.put(Editado, 1);
                    int b = db.update(name, cv, "Cedula " + "=" + Cedula, null);
                    if (b==1)
                    {
                        return true;
                    }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {

        }
}