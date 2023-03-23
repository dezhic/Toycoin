import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProofOfWork {
//    private static final int TARGET = 4; // Target number of leading zeros in hash
    private static final String ALGORITHM = "SHA-256"; // Hashing algorithm

    // Helper method to calculate hash value of input string using SHA-256 algorithm
    public static String hash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : hashBytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

    // Check whether the hash is correct in terms of difficulty
    public static Boolean hashMatchesDifficulty(String hash, int difficulty) {
        // convert it in binary
        String hashInBinary = hexToBinary(hash);
        // store the number "0" of difficulty
        String requiredPrefix = "";
        for (int i = 0; i < difficulty; i++) {
            requiredPrefix += "0";
        }
        return hashInBinary.startsWith(requiredPrefix);
    }

    // convert string hash to binary string
    public static String hexToBinary(String hash){
        byte[] hashBytes = hexStringToByteArray(hash); // convert hex string to byte array
        String binaryString = String.format("%256s", new BigInteger(1, hashBytes).toString(2)).replace(' ', '0'); // convert byte array to binary string with leading zeros
        return binaryString;
    }
    // convert string hash to byte array
    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return data;
    }

}
