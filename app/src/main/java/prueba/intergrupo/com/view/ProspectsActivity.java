package prueba.intergrupo.com.view;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;

import adapter.ProspectAdapter;
import adapter.ProspectArrayAdapter;
import data.ProspectDbHelper;
import model.Prospect;
import network.WebServiceListener;
import network.WebServiceManager;
import utilities.SecurePreferences;

public class ProspectsActivity extends ActionBarActivity implements WebServiceListener, SwipeRefreshLayout.OnRefreshListener,
AdapterListener{

    private SecurePreferences preferences;
    private WebServiceManager wsManager;
    private ListView lProspects;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prospects);
        Toolbar toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.app_name);
        }
        lProspects = (ListView)findViewById(R.id.lvProspects);
        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        preferences = new SecurePreferences(this, "login-preferences", "loginkey", true);
        wsManager = new WebServiceManager(this,this);
        getProspects();
    }

    public void getProspects()
    {
        wsManager.GetProspects(this);
        //ProspectDbHelper dbHelper = new ProspectDbHelper(getBaseContext());
        //SQLiteDatabase db = dbHelper.getWritableDatabase();
        //dbHelper.insertProspect(db, "");

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

    }

    @Override
    public void onUnexpectedError() {

    }

    @Override
    public void onValidSession() {

    }

    @Override
    public void onRefresh() {
        getProspects();
    }

    @Override
    public void onEditClick(Prospect prospect) {

    }
}
