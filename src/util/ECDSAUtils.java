package util;//import org.apache.commons.codec.binary.Hex;
import javax.xml.bind.DatatypeConverter;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class ECDSAUtils {

    //generate KeyPair
    public static KeyPair getKeyPair() {
        //Creating KeyPair generator object
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("EC");
            //Initializing the KeyPairGenerator
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            keyPairGen.initialize(256, random);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return keyPairGen.generateKeyPair();
    }


    //generate signature
    public static String signECDSA(PrivateKey privateKey, String message) {
        String result = "";
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());

            byte[] sign = signature.sign();

            //System.out.println("ECDSA signature: " + Hex.encodeHexString(sign));
            //return Hex.encodeHexString(sign);
            //System.out.println("ECDSA signature: " + DatatypeConverter.printHexBinary(sign));
            return DatatypeConverter.printHexBinary(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //verify signature
    public static boolean verifyECDSA(PublicKey publicKey, String signed, String message) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initVerify(publicKey);
            signature.update(message.getBytes());

            //byte[] hex = Hex.decodeHex(signed);
            byte[] hex = DatatypeConverter.parseHexBinary(signed);
            boolean bool = signature.verify(hex);

            System.out.println("verify：" + bool);
            return bool;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static PrivateKey privateKeyFromBytes(byte[] bytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PublicKey publicKeyFromBytes(byte[] bytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
