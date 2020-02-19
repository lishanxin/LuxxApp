package net.senink.seninkapp.net;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * implement to provide an HttpURLConnection w/ some properties pre-set see
 * BasicAuthenticationConnectionProvider for an example
 * 
 * This is passed into HttpRequest on creation to provide it w/ the connection
 * 
 * @author bballantine
 * 
 */
public interface IConnectionProvider {
	public HttpURLConnection getConnection(String urlStr) throws IOException;
}
