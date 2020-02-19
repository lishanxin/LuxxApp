package net.senink.seninkapp.net;

/**
 * interface to encapsulate a file to be POSTed to a restful web service use in
 * conjunction with HttpRequest.postMultipart();
 * 
 * 
 * @author bballantine
 * 
 */
public interface IFormFile {

	public String getFilename();

	public String getContentType();

	public byte[] getBytes();
}
