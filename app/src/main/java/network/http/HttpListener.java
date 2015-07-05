package network.http;

public interface HttpListener {
	void onRequestFinish(HttpResponse httpResponse);
}
