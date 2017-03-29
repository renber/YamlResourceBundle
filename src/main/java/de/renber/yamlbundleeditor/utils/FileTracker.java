package de.renber.yamlbundleeditor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.renber.quiterables.QuIterables;

/**
 * Class which detects if a file has been changed outside our application
 * @author berre
 *
 */
public class FileTracker {

	/**
	 * Calculate an Id which describes the current version of the given file (e.g. a hash)
	 */
	public static FileRevisionId getRevisionId(File file) {
		try {			
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			try (InputStream fStream = new FileInputStream(file)) 
				{				  
					return new MD5FileId(getDigest(fStream, md, 2048));
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (IOException e) {					
					throw new RuntimeException(e.getMessage(), e);
				}
		} catch (NoSuchAlgorithmException e) {
			// should never happen
			throw new RuntimeException(e.getMessage(), e);
		}				
	}
	
	private static byte[] getDigest(InputStream is, MessageDigest md, int byteArraySize)
			throws NoSuchAlgorithmException, IOException {

		md.reset();
		byte[] bytes = new byte[byteArraySize];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			md.update(bytes, 0, numBytes);
		}
		byte[] digest = md.digest();
		return digest;
	}
}

/**
 * File revision id based on an MD5 hash
 *
 */
class MD5FileId implements FileRevisionId {
	
	protected byte[] md5value;
	
	public MD5FileId(byte[] md5value) {
		this.md5value = md5value;
	}	
	
	public boolean hasChanged(FileRevisionId other) {
		if (!(other instanceof MD5FileId)) {
			return true;
		}
		
		return !QuIterables.query(md5value).sequenceEquals(QuIterables.query(((MD5FileId)other).md5value));
	}
	
}

