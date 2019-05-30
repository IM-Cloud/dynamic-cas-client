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
     * 从字符串中加载公钥
     * @param publicKeyStr 公钥数据字符串
     * @return RSA公钥
     * @throws Exception 加载公钥时产生的异常
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
     * 公钥解密
     * @param publicKey 公钥
     * @param cipherData 密文
     * @return 明文
     * @throws Exception 解密过程中的异常信息
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
