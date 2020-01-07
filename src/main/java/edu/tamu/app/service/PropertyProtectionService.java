package edu.tamu.app.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PropertyProtectionService {
    @Value("${app.security.secret:verysecretsecret}")
    private String secret;

    @Value("${app.security.propertySalt:13ksloe*}")
    private String propertySalt;

    private final static String ENCRYPTION_ALGORITHM = "AES";
    private final static String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA512";
    private final static String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private final static int iterationCount = 40000;
    private final static int keyLength = 128;

    public String encryptPropertyValue(String propertyValue) throws UnsupportedEncodingException, GeneralSecurityException {
        return encrypt(propertyValue, createSecretKey(secret.toCharArray(), propertySalt.getBytes(), iterationCount, keyLength));
    }

    public List<String> encryptPropertyValues(List<String> propertyValues) throws NoSuchAlgorithmException, InvalidKeySpecException, GeneralSecurityException, IOException {
        List<String> protectedValues = new ArrayList<String>();
        propertyValues.forEach(v -> {
            try {
                protectedValues.add(encryptPropertyValue(v));
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        });
        return protectedValues;
    }


    public String decryptPropertyValue(String propertyValue) throws NoSuchAlgorithmException, InvalidKeySpecException, GeneralSecurityException, IOException {
        return decrypt(propertyValue, createSecretKey(secret.toCharArray(), propertySalt.getBytes(), iterationCount, keyLength));
    }

    public List<String> decryptPropertyValues(List<String> propertyValues) throws NoSuchAlgorithmException, InvalidKeySpecException, GeneralSecurityException, IOException {
        List<String> protectedValues = new ArrayList<String>();
        propertyValues.forEach(v -> {
            try {
                protectedValues.add(decryptPropertyValue(v));
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        });
        return protectedValues;
    }

    private static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), ENCRYPTION_ALGORITHM);
    }

    private static String encrypt(String property, SecretKeySpec key) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
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
        Cipher pbeCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        return Base64.getDecoder().decode(property);
    }
}