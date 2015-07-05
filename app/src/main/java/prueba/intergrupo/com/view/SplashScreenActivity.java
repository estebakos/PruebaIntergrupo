package prueba.intergrupo.com.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

import network.WebServiceListener;
import network.WebServiceManager;
import network.http.HttpResponse;
import prueba.intergrupo.com.view.R;
import utilities.SecurePreferences;

public class SplashScreenActivity extends Activity implements WebServiceListener{

    private static final long SPLASH_SCREEN_DELAY = 3000;
    private SecurePreferences preferences;
    private String ActivityToOpen;
    private Timer timer;
    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        preferences = new SecurePreferences(this, "login-preferences", "loginkey", true);
        ActivityToOpen = "Login";
        task = new TimerTask() {
            @Override
            public void run() {

                if(ActivityToOpen.equals("Login"))
                {
                    Intent mainIntent = new Intent().setClass(
                            SplashScreenActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                }
                else
                {
                    Intent mainIntent = new Intent().setClass(
                            SplashScreenActivity.this, ProspectsActivity.class);
                    startActivity(mainIntent);
                }

                finish();
            }
        };

        // Simulate a long loading process on application startup.
        timer = new Timer();
        if(preferences.getString("email") == null || preferences.getString("email").equals(""))
        {
            timer.schedule(task, SPLASH_SCREEN_DELAY);
        }
        else
        {
            WebServiceManager wsManager = new WebServiceManager(this,this);
            wsManager.Authenticate(this, preferences.getString("email"), preferences.getString("password"));
        }

    }


    @Override
    public void onInternetFail() {
        ActivityToOpen = "Prospects";
        timer.schedule(task, 1000);
    }

    @Override
    public void onHttpError(HttpResponse resp) {
        ActivityToOpen = "Prospects";
        timer.schedule(task, 1000);
    }

    @Override
    public void onInvalidSession() {
        timer.schedule(task, 1000);
    }

    @Override
    public void onUnexpectedError() {
        ActivityToOpen = "Prospects";
        timer.schedule(task, 1000);
    }

    @Override
    public void onValidSession()    {
        ActivityToOpen = "Prospects";
        timer.schedule(task, 1000);
    }
}
