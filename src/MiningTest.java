import protocol.datatype.Transaction;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MiningTest {
    public static Blockchain blockchain; // store the Block
    public static int diff = 12;

    public static void main(String[]args) throws NoSuchAlgorithmException, IOException {

        LocalClient localClient = new LocalClient(null);
        LocalServer localServer = new LocalServer(localClient);
        localServer.start();
        localClient.initialize();  // localClient needs to initialize AFTER localServer starts!
        // Block information
        String previousHash = "0";
        String data = "Bruh";
        long timestamp = System.currentTimeMillis();

        // TODO: List<Tx> txMemPool;
        /** Tx should contain:
         * String senderAddress
         * String receiverAddress
         * double amount
         * String signature
         */



        // declare the First block
        Block firstBlock = ProofOfWork.findBlock(null, 0, previousHash, timestamp, data, diff);
        blockchain = new Blockchain(firstBlock);

        // find a valid block
        while(true){
            if(blockchain.size()%10==0){
                diff = ProofOfWork.getDifficulty(blockchain);
            }
            // generate new block //TODO: data is merkle root
            String msg = "BlockChain "+blockchain.size();
            Block newBlock = generateNextBlock(msg);

            long start = System.currentTimeMillis(); //get start time
            Block block = ProofOfWork.findBlock(blockchain.get(blockchain.size() - 1), newBlock.getIndex(), newBlock.getPreviousHash(), newBlock.getTimestamp(), newBlock.getData(), newBlock.getDifficulty());
            //store the block
            blockchain.add(block);
            long end = System.currentTimeMillis(); //get end time
//            System.out.println("New block added to blockchain with hash: " + block.getHash());
//            System.out.println("Nonce: " + block.getNonce());
//            System.out.println("running time：" + (end-start) + "ms"); //get running time

            // broadcast the new block
            localClient.broadcastNewBlock(block);

            // get new addresses
            if (blockchain.size() % 20 == 0) {
                localClient.broadcastGetAddr();
            }

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
