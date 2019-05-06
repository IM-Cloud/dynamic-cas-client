package com.dengshaolin.decrypt;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSADecrypt implements Decrypt {
    public static final String RSA = "RSA";
    public static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * ���ַ����м��ع�Կ
     * @param publicKeyStr ��Կ�����ַ���
     * @return RSA��Կ
     * @throws Exception ���ع�Կʱ�������쳣
     */
    public RSAPublicKey loadPublicKeyFromStr(String publicKeyStr)
            throws Exception {
        try {
            byte[] buffer = base64Decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("No such algorithm");
        } catch (InvalidKeySpecException e) {
            throw new Exception("invalid key spec");
        } catch (NullPointerException e) {
            throw new Exception("public key is null");
        }
    }

    /**
     * ��Կ����
     * @param publicKey ��Կ
     * @param cipherData ����
     * @return ����
     * @throws Exception ���ܹ����е��쳣��Ϣ
     */
    public byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData)
            throws Exception {
        if (publicKey == null) {
            throw new Exception("public key is null");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("No such algorithm");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("invalid key");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("invalid key block size");
        } catch (BadPaddingException e) {
            throw new Exception("the key is bad");
        }
    }

    @Override
    public String decrypt(String pubKeyStr, String encodedData) throws Exception
    {
        RSAPublicKey pubKey = loadPublicKeyFromStr(pubKeyStr);
        return new String(decrypt(pubKey, base64Decode(encodedData)), UTF8);
    }

    private static byte[] base64Decode(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }
}
