package prueba.intergrupo.com.view;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;

import adapter.ProspectAdapter;
import adapter.ProspectArrayAdapter;
import data.ProspectDbHelper;
import model.Prospect;
import model.ProspectResponse;
import network.WebServiceListener;
import network.WebServiceManager;
import utilities.SecurePreferences;

public class ProspectsActivity extends AppCompatActivity implements WebServiceListener, SwipeRefreshLayout.OnRefreshListener,
AdapterListener{

    private SecurePreferences preferences;
    private WebServiceManager wsManager;
    private ListView lProspects;
    private SwipeRefreshLayout swipeLayout;
    private ProspectDbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prospects);
        Toolbar toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_48dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.app_name);
        }
        lProspects = (ListView)findViewById(R.id.lvProspects);
        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        preferences = new SecurePreferences(this, "login-preferences", "loginkey", true);
        wsManager = new WebServiceManager(this,this);
        dbHelper = new ProspectDbHelper(getBaseContext());
        db = dbHelper.getWritableDatabase();
        getProspects();
    }

    public void getProspects()
    {
        addProspectsToAdapter();
        wsManager.GetProspects(this);
    }

    public void addProspectsToAdapter()
    {
        ProspectAdapter adapter = new ProspectAdapter(this);
        try
        {
            adapter.open();
            List<Prospect> lProspect = adapter.getCursor();
            if(lProspect.size() > 0)
            {
                ProspectArrayAdapter prospectAdapter = new ProspectArrayAdapter(this, lProspect, this);
                lProspects.setAdapter(prospectAdapter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            adapter.close();
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prospects, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            preferences.clear();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInternetFail() {

    }

    @Override
    public void onHttpError() {

    }

    @Override
    public void onInvalidSession() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onUnexpectedError() {

    }

    @Override
    public void onValidSession() {

    }

    @Override
    public void onProspects(ProspectResponse prospectResponse) {
        try {
            if (prospectResponse != null && prospectResponse.getProspects() != null && prospectResponse.getProspects().size() > 0) {
                for (Prospect prospect : prospectResponse.getProspects()) {
                    dbHelper.insertProspect(db, prospect.getCedula(), prospect.getNombre(),
                            prospect.getApellido(), prospect.getTelefono(), prospect.getEstado());
                }
                addProspectsToAdapter();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        getProspects();
    }

    @Override
    public void onEditClick(Prospect prospect) {
        Intent editProspectIntent = new Intent(this, EditProspect.class);
        editProspectIntent.putExtra("Cedula",prospect.getCedula());
        editProspectIntent.putExtra("Nombre",prospect.getNombre());
        editProspectIntent.putExtra("Apellido",prospect.getApellido());
        editProspectIntent.putExtra("Telefono",prospect.getTelefono());
        editProspectIntent.putExtra("Estado",prospect.getEstado());
        startActivity(editProspectIntent);
        finish();
    }
}
