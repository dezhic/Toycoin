import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class MintTest {
    public static void main(String[]args) throws NoSuchAlgorithmException {
        // Block information
        int index = 1;
        String bhash = "55d2cccf51a33f4dc1b4d4c3a4f5f6e5a1a5e6a43f5d5a6a5d5e5f6e5a5a5a1a";
        String bpreviousHash = "0000000000000000000000000000000000000000000000000000000000000000";
        long btimestamp = System.currentTimeMillis();
        String bdata = "Hello, world!";
        int bdifficulty = 1;
        int bnonce = 0;

        Block block = new Block(index, bhash, bpreviousHash, btimestamp, bdata, bdifficulty, bnonce);

        // Proof-of-Work algorithm
        while (true) {
            String blockData = block.getPreviousHash() + block.getData() + block.getTimestamp() + bnonce;
            String hash = ProofOfWork.hash(blockData);

            if (ProofOfWork.hashMatchesDifficulty(hash, 0)) {
                System.out.println("New block added to blockchain with hash: " + hash);
                break;
            }
            bnonce++;
        }
    }
}
