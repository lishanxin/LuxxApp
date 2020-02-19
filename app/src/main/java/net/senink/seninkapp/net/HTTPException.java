package net.senink.seninkapp.net;

import java.io.IOException;

/**
 * A special kind of IOException that contains an HTTP Error Code For a list of
 * codes see HTTPResponse
 * 
 * @author bballantine
 * 
 */
public class HTTPException extends IOException {
	private static final long serialVersionUID = -4526324236776240815L;
	private int _httpErrorCode;

	public HTTPException(int errorCode, String error) {
		super(error);
		_httpErrorCode = errorCode;
	}

	public int getErrorCode() {
		return _httpErrorCode;
	}
}
