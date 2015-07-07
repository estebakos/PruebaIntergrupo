package prueba.intergrupo.com.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.afollestad.materialdialogs.MaterialDialog;

import java.lang.ref.WeakReference;

import data.ProspectDbHelper;
import model.ProspectResponse;
import network.WebServiceListener;
import network.WebServiceManager;
import network.http.HttpResponse;
import utilities.SecurePreferences;


public class LoginActivity extends AppCompatActivity implements WebServiceListener, View.OnClickListener
{

    private EditText etEnterEmail,etEnterPassword;
    private Button btnSignIn;
    private TextView tvEmailRequired, tvPasswordRequired;
    private WebServiceManager wsManager;
    private String user, password;
    private SecurePreferences preferences, prefsLastUser;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.app_name);
        }
        preferences = new SecurePreferences(this, "login-preferences", "loginkey", true);
        prefsLastUser = new SecurePreferences(this, "db-preferences", "dbKey", true);
        etEnterEmail = (EditText) findViewById(R.id.etEnterEmail);
        etEnterPassword = (EditText) findViewById(R.id.etEnterPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(this);

        tvEmailRequired = (TextView)findViewById(R.id.tvEmailRequired);
        tvPasswordRequired = (TextView)findViewById(R.id.tvPasswordRequired);

        wsManager = new WebServiceManager(this, this);
        wsManager.setListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
    public void onInternetFail() {
        mDialog.cancel();
        new AlertDialog.Builder(this)
                .setTitle("Sin Conexion")
                .setMessage("No se detecta ninguna conexión. Revisa el estado de tu red y vuelve a intentarlo")
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onHttpError() {
        mDialog.cancel();
        new AlertDialog.Builder(this)
                .setTitle("Error en el servidor")
                .setMessage("Sentimos los problemas ocasionados. Vuelve a intentarlo en unos segundos")
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onInvalidSession() {
        mDialog.cancel();
        new AlertDialog.Builder(this)
                .setTitle("Sesión Inválida")
                .setMessage("El Email o Password ingresados no coinciden con ningún usuario")
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onUnexpectedError() {
        mDialog.cancel();
        new AlertDialog.Builder(this)
                .setTitle("Error en el servidor")
                .setMessage("Sentimos los problemas ocasionados. Vuelve a intentarlo en unos segundos")
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onValidSession()
    {
        mDialog.cancel();

        if(prefsLastUser.getString("user") != null && !prefsLastUser.getString("user").equals(user)) {
            ProspectDbHelper dbHelper = new ProspectDbHelper(getBaseContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.CreateProspectTable(db);
            dbHelper.insertTestProspects(db);
        }
        prefsLastUser.put("user", user);
        preferences.put("email", user);
        preferences.put("password", password);

        Intent intent = new Intent(this, ProspectsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onProspects(ProspectResponse prospectResponse) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnSignIn:
                user = etEnterEmail.getText().toString();
                password = etEnterPassword.getText().toString();

                if(user.equals("") || password.equals(""))
                {
                    if(user.equals("")) {
                        tvEmailRequired.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        tvEmailRequired.setVisibility(View.INVISIBLE);
                    }
                    if(password.equals(""))
                    {
                        tvPasswordRequired.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        tvPasswordRequired.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    tvEmailRequired.setVisibility(View.INVISIBLE);
                    tvPasswordRequired.setVisibility(View.INVISIBLE);
                    chkSubscription dialog = new chkSubscription(this);
                    dialog.execute("loading");
                    wsManager.Authenticate(this,user,password);
                }
        }
    }

    class chkSubscription extends AsyncTask<String, Void, String> {

        private final WeakReference<LoginActivity> loginActivityWeakRef;

        public chkSubscription(LoginActivity loginActivity) {
            super();
            this.loginActivityWeakRef = new WeakReference<LoginActivity>(loginActivity);
        }

        protected String doInBackground(String... params) {
            return "loading";
        }

        protected void onPostExecute(String result) {
            if (result.contains("loading")) //when not subscribed
            {
                if (loginActivityWeakRef.get() != null && !loginActivityWeakRef.get().isFinishing()) {
                    mDialog  = new ProgressDialog(LoginActivity.this);
                    mDialog.setCancelable(true);
                    mDialog.setMessage("Por favor espera un momento");
                    mDialog.setInverseBackgroundForced(true);
                    mDialog.show();
                }
            }
        }
    }
}
