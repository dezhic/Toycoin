import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MiningTest {
    public static ArrayList<Block> blockchain = new ArrayList<Block>(); // store the Block
    public static int diff = 12;

    public static void main(String[]args) throws NoSuchAlgorithmException, IOException {

        LocalClient localClient = new LocalClient(null);
        LocalServer localServer = new LocalServer(localClient);
        localServer.start();
        // Block information
        String previousHash = "0";
        String data = "Bruh";
        long timestamp = System.currentTimeMillis();

        // declare the First block
        Block firstBlock = ProofOfWork.findBlock(null, 0, previousHash, timestamp, data, diff);
        blockchain.add(firstBlock);

        // find a valid block
        while(true){
            if(blockchain.size()%10==0){
                diff = ProofOfWork.getDifficulty(blockchain);
            }
            // generate new block
            String msg = "BlockChain "+blockchain.size();
            Block newBlock = generateNextBlock(msg);

            long start = System.currentTimeMillis(); //get start time
            Block block = ProofOfWork.findBlock(blockchain.get(blockchain.size() - 1), newBlock.getIndex(), newBlock.getPreviousHash(), newBlock.getTimestamp(), newBlock.getData(), newBlock.getDifficulty());
            //store the block
            blockchain.add(block);
            long end = System.currentTimeMillis(); //get end time
            System.out.println("New block added to blockchain with hash: " + block.getHash());
            System.out.println("Nonce: " + block.getNonce());
            System.out.println("running time：" + (end-start) + "ms"); //get running time

            // broadcast the new block
            localClient.broadcastNewBlock(block);

        }
    }

    public static Block generateNextBlock(String blockdata) throws NoSuchAlgorithmException {
        Block previousBlock= blockchain.get(blockchain.size() - 1);
        int nextIndex= previousBlock.getIndex() + 1;
        long nextTimestamp = System.currentTimeMillis() / 1000;
        String blockData = nextIndex + previousBlock.getHash() + nextTimestamp + blockdata + diff + 0;
        String nextHash = ProofOfWork.calculateHash(blockData);
        // new block here
        Block newBlock = new Block(previousBlock, nextIndex, nextHash, previousBlock.getHash(), nextTimestamp, blockData, diff, 0);
        return newBlock;
    }

}
