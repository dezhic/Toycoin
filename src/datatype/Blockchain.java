package datatype;

import gui.GUI;
import network.LocalClient;
import protocol.message.GetBlocks;
import storage.Wallet;
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

    private Map<String, TxOutput> utxos;  // unspent transaction output map <txid:txOutIndex, TxOutput>

    private GUI gui;

    private Wallet wallet;

    public Blockchain() {
        this.blockHeightIndex = new HashMap<>();
        this.blockHashIndex = new HashMap<>();
        this.utxos = new HashMap<>();
    }

    public void setLocalClient(LocalClient localClient) {
        this.localClient = localClient;
    }

    private void updateUtxo(Block block) {
        // update utxo list
        // remove all utxo that are spent in this block
        for (Transaction tx : block.getTransactions()) {
            for (TxInput txInput : tx.getTxInputs()) {
                utxos.remove(txInput.getPrevTxOutId() + ":" + txInput.getPrevTxOutIndex());
            }
        }
        // add all new utxo
        for (Transaction tx : block.getTransactions()) {
            int idx = 0;
            for (TxOutput txOutput : tx.getTxOutputs()) {
                utxos.put(tx.getId() + ":" + idx, txOutput);
                idx++;
            }
        }
        // convert utxoMap to list of string in format "txid:txOutIndex:pubKey:amount"
        List<String> utxoList = utxos.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue().getScriptPubKey() + ":" + entry.getValue().getValue())
                .collect(Collectors.toList());
        gui.updateUTXOTable(utxoList);
    }

    public synchronized boolean add(Block block) {
        // add genesis block
        if (this.lastBlock == null) {
            this.lastBlock = block;
            blockHashIndex.put(block.getHash(), block);
            blockHeightIndex.put(block.getIndex(), block);
            updateUtxo(block);
            return true;
        }
        // check if block is valid
        if (block.getPreviousHash().equals(lastBlock.getHash())) {
            // add block to blockchain
            block.setPrevBlock(lastBlock);
            lastBlock = block;
            blockHashIndex.put(block.getHash(), block);
            blockHeightIndex.put(block.getIndex(), block);
            updateUtxo(block);
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


    private Block generateGenesisBlock(List<Transaction> coinbaseTx, String merkleRoot) {
        int diff = 16; // default difficulty
        int index= 0;
        long nextTimestamp = System.currentTimeMillis() / 1000;
        String blockData = index +
                "0" +   // previous hash == 0
                nextTimestamp +
                merkleRoot +
                diff +
                0;      // initial nonce == 0, to be found by proof of work algorithm
        String hash = ProofOfWork.calculateHash(blockData);
        // new block here, with initial nonce 0. The exact nonce will be found by the proof of work algorithm
        Block genesisBlock = new Block(null, index, hash, "0", nextTimestamp, merkleRoot, diff, 0, coinbaseTx);
        return genesisBlock;
    }

    private Block generateNextBlock(List<Transaction> txs, String merkleRoot) {
        int diff = ProofOfWork.getDifficulty(this);
        Block previousBlock= this.lastBlock;
        int nextIndex= previousBlock.getIndex() + 1;
        long nextTimestamp = System.currentTimeMillis() / 1000;
        String blockData = nextIndex + previousBlock.getHash() + nextTimestamp + merkleRoot + diff + 0;
        String nextHash = ProofOfWork.calculateHash(blockData);
        // new block here, with initial nonce 0. The exact nonce will be found by the proof of work algorithm
        Block newBlock = new Block(previousBlock, nextIndex, nextHash, previousBlock.getHash(), nextTimestamp, blockData, diff, 0, txs);
        return newBlock;
    }
    public void generateToAddress(int nBlocks, String address) {
        // if the blockchain is empty, generate a genesis block
        if (this.size() == 0) {
            Transaction coinbaseTx = new Transaction(
                    new ArrayList<>(),  // no input for coinbase transaction
                    List.of(new TxOutput(50, address))  // 50 coins to the miner's address
            );
            String merkleRoot = "TODO";  // TODO: get merkle root from txs
            Block firstBlock = generateGenesisBlock(List.of(coinbaseTx), merkleRoot);
            ProofOfWork.findNonce(firstBlock);
            localClient.addBlock(firstBlock);  // calling localClient for updating GUI
            localClient.broadcastNewBlock(firstBlock);
            nBlocks--;
        }

        // find a valid block
        while(nBlocks > 0) {
            nBlocks--;

            // Gather transactions and create a merkle root
            // 1. coinbase transaction
            Transaction coinbaseTx = new Transaction(
                    new ArrayList<>(),  // no input for coinbase transaction
                    List.of(new TxOutput(50, address))  // 50 coins to the miner's address
            );

            // 2. transactions from the mempool
            // TODO
             List<Transaction> memPoolTx = null;

             List<Transaction> txs = new LinkedList<>(); // all transactions
             txs.add(coinbaseTx);
//             txs.addAll(memPoolTx);

            // 3. create merkle root
            String merkleRoot = "TODO";  // TODO: get merkle root from txs


            // generate new block //TODO: data is merkle root
            Block newBlock = generateNextBlock(txs, merkleRoot);

            long start = System.currentTimeMillis(); //get start time
            Block block = ProofOfWork.findNonce(newBlock);
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
        for (int i = height + 1; i < size(); i++) {
            blockHashIndex.remove(blockHeightIndex.get(i).getHash());
            blockHeightIndex.remove(i);
        }
        lastBlock = block;
        System.out.println("Pruned the chain to height " + height);
    }

    public synchronized List<Block> getBlockList() {
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

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
