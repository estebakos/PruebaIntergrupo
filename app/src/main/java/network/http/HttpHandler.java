package network.http;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class HttpHandler extends AsyncTask<Void, Integer, HttpResponse> {

	private ArrayList<NameValuePair> _queryStringParams;
	private ArrayList<NameValuePair> _contentParams;
	private ArrayList<NameValuePair> _headers;

	private final String _url;
	private RequestMethod _verb;
	private String _content;
	private String _requestContentType = null;

	private final HttpListener _httpListener;
	private final HttpResponse _httpResponse;

	private int _connectionTimeOut = 10000;
	private int _socketTimeOut = 10000;

	public void setConnectionTimeOut(int connectionTimeOut) {
		_connectionTimeOut = connectionTimeOut;
	}

	public void setSocketTimeOut(int socketTimeOut) {
		_socketTimeOut = socketTimeOut;
	}

	public enum RequestMethod {
		GET, POST
	}

	public HttpHandler(String url, String route, HttpListener listener) {
		this(url + "/" + route, listener);
		_httpResponse.Route = route;
	}

	public void setContentType(String contentType)
	{
		_requestContentType = contentType;
	}

	public String getUrl() {
		return _url;
	}

	public HttpHandler(String url, HttpListener listener) {
		_url = url;
		_queryStringParams = new ArrayList<NameValuePair>();
		_contentParams = new ArrayList<NameValuePair>();
		_headers = new ArrayList<NameValuePair>();
		_content = null;
		_httpListener = listener;
		_httpResponse = new HttpResponse();
	}

	public void addQueryStringParam(String name, String value) {
		_queryStringParams.add(new BasicNameValuePair(name, value));
	}

	public void addContentParam(String name, String value) {
		_contentParams.add(new BasicNameValuePair(name, value));
	}

	public void setContent(String content) {
		_content = content;
	}

	public void addHeader(String name, String value) {
		_headers.add(new BasicNameValuePair(name, value));
	}

	public void ExecuteGet() {
		_verb = RequestMethod.GET;
		Execute(_verb);
	}

	public void ExecutePost() {
		_verb = RequestMethod.POST;
		Execute(_verb);
	}

	@SuppressLint("NewApi")
	public void Execute(RequestMethod method) {
		_verb = method;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					(Void[]) null);
		} else {
			this.execute((Void[]) null);
		}
	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected HttpResponse doInBackground(Void... params) {
		try {
			String combinedParams = "";
			if (!_queryStringParams.isEmpty()) {
				combinedParams += "?";
				for (NameValuePair p : _queryStringParams) {
					String paramString = p.getName() + "="
							+ URLEncoder.encode(p.getValue(), "UTF-8");
					if (combinedParams.length() > 1) {
						combinedParams += "&" + paramString;
					} else {
						combinedParams += paramString;
					}
				}
			}

			String fullUrl = _url + combinedParams;

			switch (_verb) {
			case GET: {

				HttpGet request = new HttpGet(fullUrl);
				if(_requestContentType != null && !_requestContentType.equals(""))
				{
					request.setHeader("Content-Type" , _requestContentType);
				}

				return executeRequest(request, _url);
			}
			case POST: {
				HttpPost request = new HttpPost(_url);
				if(_requestContentType != null && !_requestContentType.equals(""))
				{
					request.setHeader("Content-Type" , _requestContentType);
				}
				if (!_contentParams.isEmpty())
				{

					request.setEntity(new UrlEncodedFormEntity(_contentParams,
							HTTP.UTF_8));
				}

				if (_content != null)
				{
					request.setEntity(new StringEntity(_content, HTTP.UTF_8));
				}

				return executeRequest(request, _url);
			}
			}
		} catch (UnsupportedEncodingException e) {
			_httpResponse.ErrorCodeMessage = "No se lograron codificar los parámetros o la URL";
			_httpResponse.Canceled = true;
		}

		return _httpResponse;
	}

	private HttpResponse executeRequest(HttpUriRequest request, String url) {


		for (NameValuePair h : _headers) {
			request.addHeader(h.getName(), h.getValue());
		}

		HttpClient client = null;

		try {
			HttpParams httpParams = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(httpParams,
					_connectionTimeOut);

			HttpConnectionParams.setSoTimeout(httpParams, _socketTimeOut);

			client = new DefaultHttpClient(httpParams);

			org.apache.http.HttpResponse httpResponse;

			httpResponse = client.execute(request);

			_httpResponse.ResponseCode = httpResponse.getStatusLine()
					.getStatusCode();

			_httpResponse.ResponseMessage = httpResponse.getStatusLine()
					.getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				String response = EntityUtils.toString(entity);
				entity.consumeContent();

				_httpResponse.Canceled = false;
				_httpResponse.Response = response;
			}

		} catch (ClientProtocolException e) {
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			_httpResponse.ErrorCodeMessage = "Servidor no disponible. Intente nuevamente";
			_httpResponse.Canceled = true;
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			_httpResponse.ErrorCodeMessage = "Servidor no disponible. Intente nuevamente";
			_httpResponse.Canceled = true;
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		} catch (HttpHostConnectException e) {
			e.printStackTrace();
			_httpResponse.ErrorCodeMessage = "La URL no es correcta "
					+ e.toString();
			_httpResponse.Canceled = true;
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		} catch (Exception e) {
			e.printStackTrace();
			_httpResponse.ErrorCodeMessage = "Se ha producido un error desconocido. "
					+ e.toString();
			_httpResponse.Canceled = true;
		}

		return _httpResponse;
	}

	@Override
	protected void onCancelled() {
		this.cancel(true);
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		this.cancel(true);

        if(_httpListener!=null) {
            _httpListener.onRequestFinish(result);
        }
	}

	@Override
	protected void onProgressUpdate(Integer... values) {

	}
}
