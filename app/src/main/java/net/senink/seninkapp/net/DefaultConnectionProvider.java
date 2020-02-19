package net.senink.seninkapp.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * default connection provider used by HTTPRequest if none is provided
 * 
 * @author Brian
 * 
 */
public class DefaultConnectionProvider implements IConnectionProvider {

	public HttpURLConnection getConnection(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		return (HttpURLConnection) url.openConnection();
	}

}
