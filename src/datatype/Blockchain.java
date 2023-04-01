package datatype;

import network.LocalClient;
import protocol.message.GetBlocks;
import util.ProofOfWork;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

import static util.ECDSAUtils.verifyECDSA;

public class Blockchain {

    LocalClient localClient;
    private Block lastBlock;

    private Map<String, Block> blockHashIndex;

    private Map<Integer, Block> blockHeightIndex;

    public Blockchain() {
        this.blockHeightIndex = new HashMap<>();
        this.blockHashIndex = new HashMap<>();
    }

    public void setLocalClient(LocalClient localClient) {
        this.localClient = localClient;
    }

    public void rebase(Block newBranchTop, int baseHeight) {

    }

    public synchronized boolean add(Block block) {
        // add genesis block
        if (this.lastBlock == null) {
            this.lastBlock = block;
            blockHashIndex.put(block.getHash(), block);
            blockHeightIndex.put(block.getIndex(), block);
            return true;
        }
        // check if block is valid
        if (block.getPreviousHash().equals(lastBlock.getHash())) {
            // add block to blockchain
            block.setPrevBlock(lastBlock);
            lastBlock = block;
            blockHashIndex.put(block.getHash(), block);
            blockHeightIndex.put(block.getIndex(), block);
            return true;
        } else {
            System.out.println("Previous hash mismatch. Block adding failed");
            return false;
        }
    }

    public synchronized int size() {
        return blockHeightIndex.size();
    }

    /**
     * Get block by height (0-based)
     * @param height
     * @return
     */
    public synchronized Block getBlock(int height) {
        return blockHeightIndex.get(height);
    }

    public synchronized boolean hasBlock(String hash) {
        return blockHashIndex.containsKey(hash);
    }

    /**
     * Get block by its hash
     * @param hash hash of the block
     * @return the target block, or null
     */
    public synchronized Block getBlock(String hash) {
        return blockHashIndex.get(hash);
    }


    private Block generateNextBlock(String blockdata, int diff) {
        Block previousBlock= this.getBlock(this.size() - 1);
        int nextIndex= previousBlock.getIndex() + 1;
        long nextTimestamp = System.currentTimeMillis() / 1000;
        String blockData = nextIndex + previousBlock.getHash() + nextTimestamp + blockdata + diff + 0;
        String nextHash = ProofOfWork.calculateHash(blockData);
        // new block here, with initial nonce 0. The exact nonce will be found by the proof of work algorithm
        Block newBlock = new Block(previousBlock, nextIndex, nextHash, previousBlock.getHash(), nextTimestamp, blockData, diff, 0);
        return newBlock;
    }
    public synchronized void generateToAddress(int nBlocks, String address) {
        // if the blockchain is empty, generate a genesis block
        if (this.size() == 0) {
            Block firstBlock = ProofOfWork.findBlock(null, 0, "0", System.currentTimeMillis(),
                    "DUMMY_Coinbase_Tx_MercleRoot", 16);
            localClient.addBlock(firstBlock);  // calling localClient for updating GUI
            nBlocks--;
        }
        // find a valid block
        while(nBlocks > 0) {
            nBlocks--;
            int diff = ProofOfWork.getDifficulty(this);
            // generate new block //TODO: data is merkle root
            String msg = "BlockChain "+this.size();
            Block newBlock = generateNextBlock(msg, diff);

            long start = System.currentTimeMillis(); //get start time
            Block block = ProofOfWork.findBlock(this.getBlock(this.size() - 1), newBlock.getIndex(), newBlock.getPreviousHash(), newBlock.getTimestamp(), newBlock.getData(), newBlock.getDifficulty());
            //store the block
            localClient.addBlock(block);  // calling localClient for updating GUI
            long end = System.currentTimeMillis(); //get end time
            System.out.println("New block added to blockchain with hash: " + block.getHash());
            System.out.println("Nonce: " + block.getNonce());
            System.out.println("running time：" + (end-start) + "ms"); //get running time

            // broadcast the new block
            localClient.broadcastNewBlock(block);

//            // get new addresses
//            if (this.size() % 20 == 0) {
//                localClient.broadcastGetAddr();
//            }

        }
    }

    public synchronized Block getLastBlock() {
        return lastBlock;
    }

    public void sync() {
        List<String> locator = constructLocator();
        GetBlocks getBlocks = new GetBlocks(locator, null);
        localClient.broadcastGetBlocks(getBlocks);
    }


    /**
     * <p>To create the block locator hashes, keep pushing hashes until you go back to the genesis block.
     * After pushing 10 hashes back, the step backwards doubles every loop</p>
     *
     * <p>If there is no genesis block, return an empty list</p>
     *
     * @see <a href="https://en.bitcoin.it/wiki/Protocol_documentation#getblocks">getblocks</a>
     * @return the list of block locator hashes
     */
    private synchronized List<String> constructLocator() {
        List<String> locator = new LinkedList<>();
        if (blockHeightIndex.get(0) == null) {  // no genesis block
            return locator;
        }
        Block current = lastBlock;
        int step = 1;
        while (current != null) {
            locator.add(current.getHash());
            if (locator.size() > 10) {
                step *= 2;
            }
            for (int i = 0; i < step && current != null; i++) {
                current = getBlock(current.getPreviousHash());
            }
        }
        // add the genesis block
        if (blockHeightIndex.get(0) != null && !locator.contains(blockHeightIndex.get(0).getHash())) {
            locator.add(blockHeightIndex.get(0).getHash());
        }
        return locator;
    }

    /**
     * Prune the blockchain to the given block hash.
     * The block with the given hash will be the last block afterwards.
     * @param hash the hash of the block to prune to
     */
    public synchronized void prune(String hash) {
        Block block = getBlock(hash);
        if (block == null) {
            System.out.println("Pruning the entire chain");
            blockHashIndex.clear();
            blockHeightIndex.clear();
            lastBlock = null;
            return;
        }
        int height = block.getIndex();
        for (int i = height; i < size(); i++) {
            blockHashIndex.remove(blockHeightIndex.get(i).getHash());
            blockHeightIndex.remove(i);
        }
        lastBlock = block;
        System.out.println("Pruned the chain to height " + height);
    }

    public List<Block> getBlockList() {
        return blockHeightIndex.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }


    // public void storeToFile(String filename) {
    // }

    // public void loadFromFile(String filename) {
    // }

    // function to verify the transaction
    public boolean validateTransaction(Transaction tx) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Iterate over the blockchain to find the transaction inputs
        double totalInput = 0;
        for (Map.Entry<String, Block> entry : blockHashIndex.entrySet()) {
            Block block = entry.getValue();
            for (Transaction transaction : block.getTransactions()) {
                for (TxInput input : tx.getTxIns()) {
                    if (transaction.getId().equals(input.getPrevTxOutId())) {
                        // Found a matching transaction output, verify the signature
                        TxOutput output = transaction.getTxOuts().get(input.getPrevTxOutIndex());
                        // convert the public key string to object
                        PublicKey publicKey = convertKeyString(output.getScriptPubKey());
                        //data is the prevTxOutId. Just assume...
                        if (!verifyECDSA(publicKey, input.getSignatureScript(), input.getPrevTxOutId())) {
                            return false; // Invalid signature
                        }
                        totalInput += output.getValue();
                    }
                }
            }
        }
        // Verify that the total input value equals the total output value
        double totalOutput = 0;
        for (TxOutput output : tx.getTxOuts()) {
            totalOutput += output.getValue();
        }
        if (totalInput != totalOutput) {
            return false; // Total input value does not equal total output value
        }

        return true;
    }

    // function to convert the string key to Public key object
    public PublicKey convertKeyString(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Convert the public key string to a byte array
        byte[] publicKeyBytes = java.util.Base64.getDecoder().decode(key);
        // Create a X509EncodedKeySpec from the byte array
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        // Get a KeyFactory instance for the EC algorithm
        KeyFactory keyFactory;
        keyFactory = KeyFactory.getInstance("EC");
        // Generate the PublicKey object from the key specification
        PublicKey publicKey;
        publicKey = keyFactory.generatePublic(spec);
        return publicKey;
    }
}
