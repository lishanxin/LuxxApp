package net.senink.seninkapp.net;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * A connection provider to use HTTP Basic Authentication Create this and pass
 * it into the HTTPRequest construction when creating it to use basic
 * authenticatioin:
 * 
 * HettpRequest req = HTTPRequest(new
 * BasicAuthenticationConnectionProvider("foo", "bar"));
 * 
 * @author bballantine
 * 
 */
public class BasicAuthenticationConnectionProvider extends
		DefaultConnectionProvider {

	String credentials;

	public BasicAuthenticationConnectionProvider(String username,
			String password) {
		String rawCreds = username + ":" + password;
		credentials = Base64.encodeBytes(rawCreds.getBytes());
	}

	public HttpURLConnection getConnection(String urlStr) throws IOException {
		HttpURLConnection connection = super.getConnection(urlStr);
		connection.setRequestProperty("Authorization", "Basic " + credentials);
		return connection;
	}

}
