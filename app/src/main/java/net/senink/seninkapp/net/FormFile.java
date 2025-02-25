package net.senink.seninkapp.net;

/**
 * default implementation of IFormFile
 * 
 * @author bballantine
 * 
 */
public class FormFile implements IFormFile {
	private String filename;
	private String content_type;
	private byte[] bytes;

	public FormFile(String filename, String content_type, byte[] bytes) {
		this.filename = filename;
		this.content_type = content_type;
		this.bytes = bytes;
	}

	public String getFilename() {
		return filename;
	}

	public String getContentType() {
		return content_type;
	}

	public byte[] getBytes() {
		return bytes;
	}
}
