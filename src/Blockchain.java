import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blockchain {

    LocalClient localClient;
    private Block lastBlock;

    private Map<String, Block> blockIndex;

    public Blockchain(Block genesisBlock, LocalClient localClient) {
        this.lastBlock = genesisBlock;
        this.blockIndex = new HashMap<>();
        blockIndex.put(genesisBlock.getHash(), genesisBlock);
        this.localClient = localClient;
    }

    public void rebase(Block newBranchTop, int baseHeight) {

    }

    public void add(Block block) {
        // check if block is valid
        if (block.getPreviousHash().equals(lastBlock.getHash())) {
            // add block to blockchain
            lastBlock = block;
            blockIndex.put(block.getHash(), block);
        } else {
            throw new IllegalArgumentException("Block is not valid");
        }
    }

    public int size() {
        return 0;
    }

    public Block get(int height) {
        return null;
    }

    public boolean hasBlock(String hash) {
        return blockIndex.containsKey(hash);
    }

    public Block getBlock(String hash) {
        return blockIndex.get(hash);
    }

    public Block generateNextBlock(String blockdata, int diff) {
        Block previousBlock= this.get(this.size() - 1);
        int nextIndex= previousBlock.getIndex() + 1;
        long nextTimestamp = System.currentTimeMillis() / 1000;
        String blockData = nextIndex + previousBlock.getHash() + nextTimestamp + blockdata + diff + 0;
        String nextHash = ProofOfWork.calculateHash(blockData);
        // new block here
        Block newBlock = new Block(previousBlock, nextIndex, nextHash, previousBlock.getHash(), nextTimestamp, blockData, previousBlock.getDifficulty(), 0);
        return newBlock;
    }
    public void generateToAddress(int nBlocks, String address) {
        // find a valid block
        while(nBlocks > 0) {
            nBlocks--;
            int diff = 16;  // default difficulty
            if(this.size()%10==0){
                diff = ProofOfWork.getDifficulty(this);
            }
            // generate new block //TODO: data is merkle root
            String msg = "BlockChain "+this.size();
            Block newBlock = generateNextBlock(msg, diff);

            long start = System.currentTimeMillis(); //get start time
            Block block = ProofOfWork.findBlock(this.get(this.size() - 1), newBlock.getIndex(), newBlock.getPreviousHash(), newBlock.getTimestamp(), newBlock.getData(), newBlock.getDifficulty());
            //store the block
            this.add(block);
            long end = System.currentTimeMillis(); //get end time
//            System.out.println("New block added to blockchain with hash: " + block.getHash());
//            System.out.println("Nonce: " + block.getNonce());
//            System.out.println("running time：" + (end-start) + "ms"); //get running time

            // broadcast the new block
            localClient.broadcastNewBlock(block);

//            // get new addresses
//            if (this.size() % 20 == 0) {
//                localClient.broadcastGetAddr();
//            }

        }
    }

    public Block getLastBlock() {
        return lastBlock;
    }

    // public void storeToFile(String filename) {
    // }

    // public void loadFromFile(String filename) {
    // }
}
