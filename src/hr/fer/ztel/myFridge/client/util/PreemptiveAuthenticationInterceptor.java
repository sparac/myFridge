package hr.fer.ztel.myFridge.client.util;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

/**
 * Class used for intercepting outgoing HTTP requests and setting preemptive authorization.
 * @author suncana
 *
 */
public class PreemptiveAuthenticationInterceptor implements HttpRequestInterceptor {

	@Override
	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {

		AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);

		HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		CredentialsProvider credsProvider = (CredentialsProvider) context
				.getAttribute(ClientContext.CREDS_PROVIDER);

		if (authState.getAuthScheme() == null) {
			AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
			Credentials creds = credsProvider.getCredentials(authScope);

			if (creds != null) {
				authState.setAuthScheme(new BasicScheme());
				authState.setCredentials(creds);
			}
		}
	}

}
