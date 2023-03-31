import protocol.datatype.Transaction;
import protocol.datatype.TxInput;
import protocol.datatype.TxOutput;

import java.util.HashMap;
import java.util.Map;
import static protocol.datatype.ECDSAUtils.*;

public class Blockchain {

    LocalClient localClient;
    private Block lastBlock;

    private Map<String, Block> blockHashIndex;

    private Map<Integer, Block> blockHeightIndex;

    public Blockchain(Block genesisBlock, LocalClient localClient) {
        this.lastBlock = genesisBlock;
        this.blockHashIndex = new HashMap<>();
        blockHashIndex.put(genesisBlock.getHash(), genesisBlock);
        blockHeightIndex.put(0, genesisBlock);
        this.localClient = localClient;
    }

    public void rebase(Block newBranchTop, int baseHeight) {

    }

    public void add(Block block) {
        // check if block is valid
        if (block.getPreviousHash().equals(lastBlock.getHash())) {
            // add block to blockchain
            lastBlock = block;
            blockHashIndex.put(block.getHash(), block);
            blockHeightIndex.put(block.getIndex(), block);
        } else {
            throw new IllegalArgumentException("Block is not valid");
        }
    }

    public int size() {
        return 0;
    }

    /**
     * Get block by height (0-based)
     * @param height
     * @return
     */
    public Block getBlock(int height) {
        return blockHeightIndex.get(height);
    }

    public boolean hasBlock(String hash) {
        return blockHashIndex.containsKey(hash);
    }

    /**
     * Get block by its hash
     * @param hash hash of the block
     * @return the target block, or null
     */
    public Block getBlock(String hash) {
        return blockHashIndex.get(hash);
    }


    public Block generateNextBlock(String blockdata, int diff) {
        Block previousBlock= this.getBlock(this.size() - 1);
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
            Block block = ProofOfWork.findBlock(this.getBlock(this.size() - 1), newBlock.getIndex(), newBlock.getPreviousHash(), newBlock.getTimestamp(), newBlock.getData(), newBlock.getDifficulty());
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

    //verify the transaction (plz put it anywhere...)
    public boolean validateTransaction(Transaction tx, Map<String, Block> blockIndex) {
        // loop through each TxIn and verify it against a previous TxOut
        for (TxInput txIn : tx.getTxIns()) {
            // get the previous transaction containing the TxOut
            Block prevBlock = blockIndex.get(txIn.getPrevTxOutId());
            if (prevBlock == null) {
                // previous transaction not found, invalid transaction
                return false;
            }

            // loop through each transaction in the previous block and find the TxOut
            for (Transaction prevTx : prevBlock.getTransactions()) {
                if (prevTx.getId().equals(txIn.getPrevTxOutIndex())) {
                    // TxOut found, verify the signature
                    TxOutput txOut = prevTx.getTxOuts().get(txIn.getPrevTxOutIndex());
                    String message = tx.getId() + txOut.getValue() + txOut.getScriptPubKey();
                    // TODO: verify code here
//                    if (!verifyECDSA(txOut.getScriptPubKey(), txIn.getSignatureScript(), message)) {
//                        // invalid signature, invalid transaction
//                        return false;
//                    }
                }
            }
        }
        // all TxIns have been validated, transaction is valid
        return true;
    }
}
