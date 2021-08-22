package edu.ncl.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAUtil {

    public static String getSignature(String privateKey, String text){
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey p = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initSign(p);
            signature.update(text.getBytes());
            byte[] result = signature.sign();
            return Hex.encodeHexString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Map<String, Object> generateKeyPairs() throws NoSuchAlgorithmException {
        Map<String, Object> keyPairMap = new HashMap<>();
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = generator.genKeyPair();
        PublicKey aPublic = keyPair.getPublic();
        PrivateKey aPrivate = keyPair.getPrivate();
        keyPairMap.put("publicKey", Base64.encodeBase64String(aPublic.getEncoded()));
        keyPairMap.put("privateKey", Base64.encodeBase64String(aPrivate.getEncoded()));
        return keyPairMap;
    }
}
