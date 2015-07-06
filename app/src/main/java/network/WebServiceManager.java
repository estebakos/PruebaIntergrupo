package network;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import model.AuthenticateResponse;
import model.DynamicAttributes;
import model.Zone;
import network.http.HttpHandler;
import network.http.HttpListener;
import network.http.HttpResponse;
import prueba.intergrupo.com.view.R;
import utilities.SessionVars;
import utilities.json.Json;
import utilities.json.OutputType;

/**
 * Created by TEBAN on 05/07/2015.
 */
public class WebServiceManager
{
    private final Context _context;
    private WebServiceListener WsListener;

    public WebServiceManager(Context context) {
        _context = context;
    }

    public WebServiceManager(Context context, WebServiceListener listener) {
        WsListener = listener;
        _context = context;
    }


    private boolean isNetworkAvailable(Context context)
    {
        if(context!=null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        else
        {
            return false;
        }
    }


    public String getRestUrl() {
        try {
            return _context.getResources().getString(R.string.urlbase);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public void Authenticate(Context context, String Email, String Password) {
        try {
            if (!isNetworkAvailable(context)) {
                WsListener.onInternetFail();
            } else {
                if (getRestUrl() != "") {
                    HttpHandler httpGet = new HttpHandler(getRestUrl(),
                            "application/login", new HttpListener() {

                        @Override
                        public void onRequestFinish(
                                HttpResponse httpResponse) {
                            Json json = new Json();
                            json.setOutputType(OutputType.minimal);
                            json.setElementType(
                                    AuthenticateResponse.class, "zone",
                                    Zone.class);
                            AuthenticateResponse resp;
                            json.setElementType(
                                    Zone.class, "dynamicAttributes",
                                    DynamicAttributes.class);
                            AuthenticateResponse authResp;
                            try {
                                authResp = json.fromJson(
                                        AuthenticateResponse.class,
                                        httpResponse.Response);
                                if(authResp.getSuccess())
                                {
                                    if(authResp.getAuthToken()!=null && !authResp.getAuthToken().equals(""))
                                    {
                                        SessionVars.AuthToken = authResp.getAuthToken();
                                        WsListener.onValidSession();
                                    }
                                    else
                                    {
                                        WsListener.onInvalidSession();
                                    }
                                }
                                else
                                {
                                    WsListener.onInvalidSession();
                                }
                            }
                            catch (Exception ex)
                            {
                                WsListener.onHttpError();
                            }
                        }
                    });
                    httpGet.addQueryStringParam("email", Email);
                    httpGet.addQueryStringParam("password", Password);
                    httpGet.ExecuteGet();
                } else {
                    WsListener.onUnexpectedError();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void GetProspects(Context context) {
        try {
            if (!isNetworkAvailable(context)) {
                WsListener.onInternetFail();
            } else {
                if (getRestUrl() != "") {
                    HttpHandler httpGet = new HttpHandler(getRestUrl(),
                            "sch/prospects", new HttpListener() {

                        @Override
                        public void onRequestFinish(
                                HttpResponse httpResponse) {
                            Json json = new Json();
                            json.setOutputType(OutputType.minimal);
                            json.setElementType(
                                    AuthenticateResponse.class, "zone",
                                    Zone.class);
                            AuthenticateResponse resp;
                            json.setElementType(
                                    Zone.class, "dynamicAttributes",
                                    DynamicAttributes.class);
                            AuthenticateResponse authResp;
                            try {
                                authResp = json.fromJson(
                                        AuthenticateResponse.class,
                                        httpResponse.Response);
                                if(authResp.getSuccess())
                                {
                                    if(authResp.getAuthToken()!=null && !authResp.getAuthToken().equals(""))
                                    {
                                        SessionVars.AuthToken = authResp.getAuthToken();
                                        WsListener.onValidSession();
                                    }
                                    else
                                    {
                                        WsListener.onInvalidSession();
                                    }
                                }
                                else
                                {
                                    WsListener.onInvalidSession();
                                }
                            }
                            catch (Exception ex)
                            {
                                WsListener.onHttpError();
                            }
                        }
                    });
                    httpGet.addHeader("token", SessionVars.AuthToken);
                    httpGet.ExecuteGet();
                } else {
                    WsListener.onUnexpectedError();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setListener(WebServiceListener servicesListener) {
        WsListener = servicesListener;
    }
}
