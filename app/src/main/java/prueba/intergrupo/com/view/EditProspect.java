package prueba.intergrupo.com.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.sql.SQLException;

import data.ProspectDbHelper;
import model.Prospect;
import prueba.intergrupo.com.view.R;

public class EditProspect extends AppCompatActivity {

    private TextView tvCedula;
    private EditText etNombre, etApellido, etTelefono;
    private Spinner spStatus;
    private Button btnEditProspect;
    private Bundle extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_prospect);
        tvCedula = (TextView) findViewById(R.id.tvCedula);
        etApellido = (EditText) findViewById(R.id.etApellido);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etTelefono = (EditText) findViewById(R.id.etTelefono);
        spStatus = (Spinner) findViewById(R.id.spStatus);
        btnEditProspect = (Button) findViewById(R.id.btnEdit);

        extras = getIntent().getExtras();
        if(extras != null)
        {
            tvCedula.setText(extras.getString("Cedula"));
            etNombre.setText(extras.getString("Nombre"));
            etApellido.setText(extras.getString("Apellido"));
            etTelefono.setText(extras.getString("Telefono"));
            spStatus.setSelection(extras.getInt("Estado"));
        }

        btnEditProspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidateEdit()) {
                    ProspectDbHelper prospectDbHelper = new ProspectDbHelper(getBaseContext());
                    try {
                        ProspectDbHelper dbHelper = new ProspectDbHelper(getBaseContext());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        boolean bEdit = prospectDbHelper.editProspect(db, tvCedula.getText().toString(), etNombre.getText().toString(), etApellido.getText().toString(), etTelefono.getText().toString(), spStatus.getSelectedItemPosition());
                        if (bEdit) {
                            onBackPressed();
                        }
                        else
                        {
                            new AlertDialog.Builder(getBaseContext())
                                    .setTitle("Error al editar")
                                    .setMessage("Verifica de nuevo los datos que est�s intentando editar")
                                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            dialog.cancel();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    chkSubscription subscription = new chkSubscription(EditProspect.this);
                    subscription.execute("error");
                }
            }
        });
    }

    public boolean isValidateEdit()
    {
        if(etNombre.getText().toString().equals("") || etApellido.getText().toString().equals("") || etTelefono.getText().toString().equals(""))
        {
            return false;
        }
        if(extras != null)
        {
            if(etNombre.getText().toString().equals(extras.getString("Nombre")) &&
               etApellido.getText().toString().equals(extras.getString("Apellido")) &&
               etTelefono.getText().toString().equals(extras.getString("Telefono")) &&
               spStatus.getSelectedItemPosition() == extras.getInt("Estado"))
            {
                return false;
            }
        }
        return true;
    }

    class chkSubscription extends AsyncTask<String, Void, String> {

        private final WeakReference<EditProspect> editActivityWeakRef;

        public chkSubscription(EditProspect editProspect) {
            super();
            this.editActivityWeakRef = new WeakReference<EditProspect>(editProspect);
        }

        protected String doInBackground(String... params) {
            return "error";
        }

        protected void onPostExecute(String result) {
            if (result.contains("error")) //when not subscribed
            {
                if (editActivityWeakRef.get() != null && !editActivityWeakRef.get().isFinishing()) {
                    new AlertDialog.Builder(EditProspect.this)
                            .setTitle("No es posible editar")
                            .setMessage("Es posible que no hayas modificado alguno de los datos o que alguno de ellos se hayan enviado vacíos")
                            .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_prospect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent listIntent = new Intent(this, ProspectsActivity.class);
        startActivity(listIntent);
        finish();
    }
}
