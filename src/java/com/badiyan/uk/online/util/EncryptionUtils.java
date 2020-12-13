/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.util;

import com.badiyan.uk.beans.CUBean;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author marlo
 */
public class EncryptionUtils {
	
	private static String password = "9m3KU7hw!";
	private static byte[] salt = new String("my cat's breath smells like catfood").getBytes();

	// Decreasing this speeds down startup time and can be useful during testing, but it also makes it easier for brute force attackers
	private static int iterationCount = 40000;
	// Other values give me java.security.InvalidKeyException: Illegal key size or default parameters
	private static int keyLength = 128;
	private static SecretKeySpec key = null;

	
	public static String encryptString(String _str_to_encrypt) {
		
		try {
			if (key == null) {
				key = EncryptionUtils.createSecretKey(password.toCharArray(), salt, iterationCount, keyLength);
			}

			String encryptedStr = encrypt(_str_to_encrypt, key);
			return encryptedStr;
		} catch (Exception x) {
			x.printStackTrace();
		}
		return "";
	}
	
	public static String decryptString(String _str_to_decrypt) {
		
		try {
			if (key == null) {
				key = EncryptionUtils.createSecretKey(password.toCharArray(), salt, iterationCount, keyLength);
			}

			return decrypt(_str_to_decrypt, key);
		} catch (Exception x) {
			x.printStackTrace();
		}
		return "";
	}
	
    private static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    private static String encrypt(String property, SecretKeySpec key) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(property.getBytes("UTF-8"));
        byte[] iv = ivParameterSpec.getIV();
        return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }

    private static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static String decrypt(String string, SecretKeySpec key) throws GeneralSecurityException, IOException {
        String iv = string.split(":")[0];
        String property = string.split(":")[1];
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        return Base64.getDecoder().decode(property);
    }
	
}
