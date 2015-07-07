package adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data.ProspectDbHelper;
import model.Prospect;

/**
 * Created by TEBAN on 05/07/2015.
 */
public class ProspectAdapter {

    private static final String tableName = "PROSPECT" ;
    private static final String Cedula = "Cedula";
    private static final String Nombre = "Nombre";
    private static final String Apellido = "Apellido";
    private static final String Zona = "Zona";
    private static final String Direccion = "Direccion";
    private static final String Telefono = "Telefono";
    private static final String Ciudad = "Ciudad";
    private static final String Estado = "Estado";

    private Context context;
    private ProspectDbHelper dbHelper;
    private SQLiteDatabase db;
    private List<Prospect> lProspects;

    private String[] columnas = new String[]{ Cedula, Zona, Nombre, Apellido, Direccion, Telefono, Ciudad, Estado};

    public ProspectAdapter(Context context)
    {
        this.context = context;
    }

    public ProspectAdapter open() throws SQLException
    {
        dbHelper = new ProspectDbHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        dbHelper.close();
    }

    /**
     * Devuelve cursor con todos las columnas de la tabla
     */
    public List<Prospect> getCursor() throws SQLException {
        try {
            lProspects = new ArrayList<>();
            String selectQuery = "SELECT  * FROM " + tableName;
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    Prospect prospect = new Prospect();
                    prospect.setCedula(cursor.getString(cursor.getColumnIndex(Cedula)));
                    prospect.setApellido(cursor.getString(cursor.getColumnIndex(Apellido)));
                    prospect.setCiudad(cursor.getString(cursor.getColumnIndex(Ciudad)));
                    prospect.setDireccion(cursor.getString(cursor.getColumnIndex(Direccion)));
                    prospect.setEstado(cursor.getInt(cursor.getColumnIndex(Estado)));
                    prospect.setNombre(cursor.getString(cursor.getColumnIndex(Nombre)));
                    prospect.setTelefono(cursor.getString(cursor.getColumnIndex(Telefono)));
                    prospect.setZona(cursor.getString(cursor.getColumnIndex(Zona)));
                    lProspects.add(prospect);
                } while (cursor.moveToNext());
            }

            return lProspects;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }


}
