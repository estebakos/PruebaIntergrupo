package network;

import model.ProspectResponse;
import network.http.HttpResponse;

public interface WebServiceListener {
	void onInternetFail();
	void onHttpError();
	void onInvalidSession();
	void onUnexpectedError();
	void onValidSession();
	void onProspects(ProspectResponse prospectResponse);
}
