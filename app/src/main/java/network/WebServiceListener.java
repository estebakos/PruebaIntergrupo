package network;

import network.http.HttpResponse;

public interface WebServiceListener {

	void onInternetFail();
	void onHttpError();
	void onInvalidSession();
	void onUnexpectedError();
	void onValidSession();
}
