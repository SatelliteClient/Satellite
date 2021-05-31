package com.github.satellite.network;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSA {
	
	public PublicKey publicKey;
	public PrivateKey privateKey;
	
	public RSA() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
	    KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
	    kg.initialize(1024);
	    KeyPair keyPair = kg.generateKeyPair();
	    KeyFactory factoty = KeyFactory.getInstance("RSA");
	    RSAPublicKeySpec publicKeySpec = factoty.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
	    RSAPrivateKeySpec privateKeySpec = factoty.getKeySpec(keyPair.getPrivate(), RSAPrivateKeySpec.class);
	    publicKey = factoty.generatePublic(publicKeySpec);
	    privateKey = factoty.generatePrivate(privateKeySpec);
	}
	
	public byte[] toByte(String str) throws UnsupportedEncodingException {
		return str.getBytes("UTF-8");
	}
	
	public String toString(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}
    
	public byte[] encrypt(byte[] plain) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
	    Cipher encrypterWithPublicKey = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	    encrypterWithPublicKey.init(Cipher.ENCRYPT_MODE, publicKey);
	    return encrypterWithPublicKey.doFinal(plain);
	}
	
	public byte[] decrypt(byte[] encrypted) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
	    Cipher decrypterWithPriavteKey = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	    decrypterWithPriavteKey.init(Cipher.DECRYPT_MODE, privateKey);
	    return decrypterWithPriavteKey.doFinal(encrypted);
	}

    public static String decodeHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
