import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class MintTest {
    public static void main(String[]args) throws NoSuchAlgorithmException {
        // Block information
        String previousHash = "0000000000000000000000000000000000000000000000000000000000000000";
        String data = "Bruh";
        long timestamp = System.currentTimeMillis();
        Block block = findBlock(0, previousHash, timestamp, data, 1);
        System.out.println("New block added to blockchain with hash: " + block.getHash());
    }
    // find a valid block hash
    public static Block findBlock(int index, String previousHash, long timestamp, String data, int difficulty) throws NoSuchAlgorithmException {
        int nonce = 0;
        String blockData = index + previousHash + timestamp + data + difficulty + nonce;
        while (true) {
            String hash = ProofOfWork.calculateHash(blockData);
            if(ProofOfWork.hashMatchesDifficulty(hash, difficulty)){
                return new Block(index, hash, previousHash, timestamp, data, difficulty, nonce);
            }
            nonce++;
        }
    }
}
