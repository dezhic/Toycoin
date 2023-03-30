import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blockchain {

    private Block lastBlock;

    private Map<String, Block> blockIndex;

    public Blockchain(Block genesisBlock) {
        this.lastBlock = genesisBlock;
        this.blockIndex = new HashMap<>();
        blockIndex.put(genesisBlock.getHash(), genesisBlock);
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

    // public void storeToFile(String filename) {
    // }

    // public void loadFromFile(String filename) {
    // }
}
