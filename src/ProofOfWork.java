import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProofOfWork {
    private static final String ALGORITHM = "SHA-256"; // Hashing algorithm
    private static final int BLOCK_GENERATION_INTERVAL = 250; // defines how often a block should be found (in ms)
    private static final int DIFFICULTY_ADJUSTMENT_INTERVAL = 1; //defines how often the difficulty should be adjusted with the increasing or decreasing network hashrate.

    // find the next block
    public static Block findBlock(Block prevBlock, int index, String previousHash, long timestamp, String data, int difficulty) {
        int nonce = 0;

        while (true) {
            String blockData = index + previousHash + timestamp + data + difficulty + nonce;
            String hash = calculateHash(blockData);
            if(hashMatchesDifficulty(hash, difficulty)){
                return new Block(prevBlock, index, hash, previousHash, timestamp, data, difficulty, nonce);
            }
            nonce++;
        }
    }

    // calculate the SHA-256
    public static String calculateHash(String input) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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
        StringBuilder requiredPrefix = new StringBuilder();
        for (int i = 0; i < difficulty; i++) {
            requiredPrefix.append("0");
        }
        return hashInBinary.startsWith(requiredPrefix.toString());
    }

    // convert string hash to binary string
    public static String hexToBinary(String hash){
        byte[] hashBytes = hexStringToByteArray(hash); // convert hex string to byte array
        String binaryString = String.format("%256s", new BigInteger(1, hashBytes).toString(2)).replace(' ', '0'); // convert byte array to binary string with leading zeros
        return binaryString;
    }

    // convert string hash to byte array
    private static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return data;
    }

    // determine to change the difficulty and get the latest difficulty
    public static int getDifficulty(Blockchain aBlockChain){
        Block latestBlock = aBlockChain.getLastBlock();
        if(latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0 && latestBlock.getIndex() != 0){
            return getAdjustedDifficulty(latestBlock, aBlockChain);
        }else{
            return latestBlock.getDifficulty();
        }
    }

    // get the Adjusted difficulty value
    private static int getAdjustedDifficulty(Block latestBlock, Blockchain aBlockChain){
        Block prevAdjustmentBlock = aBlockChain.getBlock(aBlockChain.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);
        long timeExpected = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
        long timeTaken = latestBlock.getTimestamp() - prevAdjustmentBlock.getTimestamp();
        if (timeTaken < timeExpected / 2) {
            System.out.println("Difficulty increased by 1");
            return prevAdjustmentBlock.getDifficulty() + 1;
        } else if (timeTaken > timeExpected * 2) {
            System.out.println("Difficulty decreased by 1");
            return prevAdjustmentBlock.getDifficulty() - 1;
        } else {
            return prevAdjustmentBlock.getDifficulty();
        }
    }
}
