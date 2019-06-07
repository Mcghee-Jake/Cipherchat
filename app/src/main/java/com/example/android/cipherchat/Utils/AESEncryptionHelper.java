package com.example.android.cipherchat.Utils;

import android.util.Base64;
import android.util.Log;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptionHelper {

    public static String generateKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[16];
        secureRandom.nextBytes(key);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
    }


    public static String encrypt(String data, String secretKeyString) {
        SecretKey secretKey = convertStringToSecretKey(secretKeyString);
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);
        Cipher cipher = createCipher();
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            byte[] cipherText = cipher.doFinal(data.getBytes());
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);
            byteBuffer.putInt(iv.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            return Base64.encodeToString(byteBuffer.array(), Base64.DEFAULT);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String data, String secretKeyString) {
        SecretKey secretKey = convertStringToSecretKey(secretKeyString);
        byte[] encryptedData = Base64.decode(data, Base64.DEFAULT);
        ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);
        int ivLength = byteBuffer.getInt();
        if (ivLength < 12 || ivLength >= 16) throw new IllegalArgumentException("Invalid iv length");
        byte[] iv = new byte[ivLength];
        byteBuffer.get(iv);
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
        Cipher cipher = createCipher();
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            byte[] decryptedData = cipher.doFinal(cipherText);
            return new String(decryptedData);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void testEncryption() {
        String secretKey = generateKey();
        String secretMessage = "This is a very secret message";
        Log.d("ENCRYPTION_TEST", "Plaintext - " + secretMessage);
        String encryptedMessage = encrypt(secretMessage, secretKey);
        Log.d("ENCRYPTION_TEST", "Encrypted text - " + encryptedMessage);
        String decryptedMessage = decrypt(encryptedMessage, secretKey);
        Log.d("ENCRYPTION_TEST", "Decrypted text - " + decryptedMessage);
    }

    private static SecretKey convertStringToSecretKey(String secretKeyString) {
        byte[] encodedKey = Base64.decode(secretKeyString, Base64.DEFAULT);
        return new SecretKeySpec(encodedKey, "AES");
    }

    private static Cipher createCipher() {
        String transformation = "AES/GCM/NoPadding";
        try {
            return Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


}
