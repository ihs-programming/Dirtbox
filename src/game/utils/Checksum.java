package game.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Checksum {
	/*
	 * This class allows for file checking to prevent the use of modified clients
	 */

	private static File[] getFilesinDirectory(File folder) {
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
	}

	private static String md5Checksum(File file)
			throws NoSuchAlgorithmException, IOException {
		MessageDigest md5Digest = MessageDigest.getInstance("MD5");
		return getFileChecksum(md5Digest, file);
	}

	private static String sha1Checksum(File file)
			throws NoSuchAlgorithmException, IOException {
		MessageDigest shaDigest = MessageDigest.getInstance("SHA-1");
		return getFileChecksum(shaDigest, file);
	}

	private static String getFileChecksum(MessageDigest digest, File file)
			throws IOException {
		// Get file input stream for reading the file content
		FileInputStream fis = new FileInputStream(file);

		// Create byte array to read data in chunks
		byte[] byteArray = new byte[1024];
		int bytesCount = 0;

		// Read file data and update in message digest
		while ((bytesCount = fis.read(byteArray)) != -1) {
			digest.update(byteArray, 0, bytesCount);
		}
		;

		// close the stream; We don't need it now.
		fis.close();

		// Get the hash's bytes
		byte[] bytes = digest.digest();

		// This bytes[] has bytes in decimal format;
		// Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}

		// return complete hash
		return sb.toString();
	}
}
