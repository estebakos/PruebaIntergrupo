package network;

import network.http.HttpResponse;

public interface WebServiceListener {

	void onInternetFail();
	void onHttpError(HttpResponse resp);
	void onInvalidSession();
	void onUnexpectedError();
	void onValidSession();
}
