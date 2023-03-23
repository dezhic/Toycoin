import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class MintTest {
    public static void main(String[]args) throws NoSuchAlgorithmException {
        // Block information
        String previousHash = "0";
        String data = "Bruhqweqweqweqweqwe";
        long timestamp = System.currentTimeMillis();

        // find a valid block hash
        long start = System.currentTimeMillis(); //get start time
        Block block = ProofOfWork.findBlock(0, previousHash, timestamp, data, 8);
        long end = System.currentTimeMillis(); //get end time
        System.out.println("New block added to blockchain with hash: " + block.getHash());
        System.out.println("Nonce: " + block.getNonce());
        System.out.println("running time：" + (end-start) + "ms"); //get running time
    }

}
