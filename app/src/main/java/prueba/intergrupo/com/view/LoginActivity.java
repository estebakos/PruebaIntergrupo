package prueba.intergrupo.com.view;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import network.WebServiceListener;
import network.WebServiceManager;
import network.http.HttpResponse;


public class LoginActivity extends ActionBarActivity implements WebServiceListener
{

    private EditText etEnterEmail,etEnterPassword;
    private Button btnSignIn;
    private WebServiceManager wsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.app_name);
        }

        etEnterEmail = (EditText) findViewById(R.id.etEnterEmail);
        etEnterPassword = (EditText) findViewById(R.id.etEnterPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wsManager.Authenticate(LoginActivity.this, etEnterEmail.getText().toString(), etEnterPassword.getText().toString());
            }
        });

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInternetFail() {

    }

    @Override
    public void onHttpError(HttpResponse resp) {

    }

    @Override
    public void onInvalidSession() {

    }

    @Override
    public void onUnexpectedError() {

    }

    @Override
    public void onValidSession() {
        Intent intent = new Intent(this, ProspectsActivity.class);
        startActivity(intent);
        finish();
    }
}
