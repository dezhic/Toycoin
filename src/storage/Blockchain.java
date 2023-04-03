package storage;

import datatype.*;
import gui.GUI;
import network.LocalClient;
import protocol.message.GetBlocks;
import storage.MemPool;
import storage.Wallet;
import util.ProofOfWork;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

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
                String utxoLocator = txInput.getPrevTxOutId() + ":" + txInput.getPrevTxOutIndex();
                TxOutput toRemove = utxos.get(utxoLocator);
                if (toRemove == null) {
                    System.out.println("Error: utxo not found");
                    continue;
                }
                wallet.removeUtxos(toRemove.getScriptPubKey(), utxoLocator);
                utxos.remove(utxoLocator);
            }
        }
        // add all new utxo
        for (Transaction tx : block.getTransactions()) {
            int idx = 0;
            for (TxOutput txOutput : tx.getTxOutputs()) {
                String utxoLocator = tx.getId() + ":" + idx;
                utxos.put(utxoLocator, txOutput);
                wallet.addUtxos(txOutput.getScriptPubKey(), utxoLocator);
                idx++;
            }
        }
        // convert utxoMap to list of string in format "txid:txOutIndex:pubKey:amount"
        List<String> utxoList = utxos.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue().getScriptPubKey() + ":" + entry.getValue().getValue())
                .collect(Collectors.toList());
        gui.updateUTXOTable(utxoList);
    }

    public void rebuildUtxos() {
        utxos.clear();
        for (int i = 0; i < size(); i++) {
            Block block = getBlock(i);
            for (Transaction tx : block.getTransactions()) {
                int idx = 0;
                for (TxOutput txOutput : tx.getTxOutputs()) {
                    String utxoLocator = tx.getId() + ":" + idx;
                    utxos.put(utxoLocator, txOutput);
                    wallet.addUtxos(txOutput.getScriptPubKey(), utxoLocator);
                    idx++;
                }
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


    private Block generateGenesisBlock(List<Transaction> coinbaseTx) {
        int diff = 16; // default difficulty
        int index= 0;
        long nextTimestamp = System.currentTimeMillis() / 1000;
        String merkleRoot = coinbaseTx.get(0).getId();  // merkle root is simply the hash of the coinbase transaction
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

    private Block generateNextBlock(List<Transaction> txs) {
        // create merkle root
        MerkleTree merkleTree = new MerkleTree(
                txs.stream().map(Transaction::getId).collect(Collectors.toList())
        );
        String merkleRoot = merkleTree.getRoot();
        // get difficulty
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
            Block firstBlock = generateGenesisBlock(List.of(coinbaseTx));
            ProofOfWork.findNonce(firstBlock);
            localClient.addBlock(firstBlock);  // calling localClient for updating GUI
            localClient.broadcastNewBlock(firstBlock);
            nBlocks--;
        }

        // find a valid block
        while(nBlocks > 0) {
            // Gather transactions and create a merkle root
            List<Transaction> txs = new LinkedList<>(); // all transactions
            // 1. coinbase transaction
            Transaction coinbaseTx = new Transaction(
                    new ArrayList<>(),  // no input for coinbase transaction
                    List.of(new TxOutput(50, address))  // 50 coins to the miner's address
            );
            txs.add(coinbaseTx);

            // 2. transactions from the mempool
            List<Transaction> memPoolTxs = MemPool.getInstance().getAllTransactions();
            List<Transaction> validatedMemPoolTxs = filterValidMemPoolTxs(memPoolTxs);
            txs.addAll(validatedMemPoolTxs);

            // generate new block
            Block newBlock = generateNextBlock(txs);

            long start = System.currentTimeMillis(); //get start time
            Block block = ProofOfWork.findNonce(newBlock);
            //store the block
            boolean isAdded = localClient.addBlock(block);  // calling localClient for updating GUI
            if (!isAdded) {
                System.out.println("Failed to add block. The block has become invalid for the current chain.");
                continue;
            }
            long end = System.currentTimeMillis(); //get end time
            System.out.println("New block added to blockchain with hash: " + block.getHash());
            System.out.println("Nonce: " + block.getNonce());
            System.out.println("running time：" + (end-start) + "ms"); //get running time

            // clean up mempool
            for (Transaction tx : memPoolTxs) {
                MemPool.getInstance().remove(tx);
            }

            // broadcast the new block
            localClient.broadcastNewBlock(block);

//            // get new addresses
//            if (this.size() % 20 == 0) {
//                localClient.broadcastGetAddr();
//            }

            nBlocks--;
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
            utxos.clear();
            wallet.clearUtxos();
            return;
        }
        int height = block.getIndex();
        for (int i = height + 1; i < size(); i++) {
            Block b = blockHeightIndex.get(i);
            blockHashIndex.remove(b.getHash());
            blockHeightIndex.remove(i);
            rebuildUtxos();
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

    private synchronized List<Transaction> filterValidMemPoolTxs(List<Transaction> txs) {
        List<Transaction> validTxs = new LinkedList<>();
        for (Transaction tx : txs) {
            if (validateMemPoolTx(tx)) {
                validTxs.add(tx);
            }
        }
        return validTxs;
    }

    // public void storeToFile(String filename) {
    // }

    // public void loadFromFile(String filename) {
    // }

    // function to verify the transaction
    public boolean validateMemPoolTx(Transaction tx) {
        System.out.println("Validating transaction: " + tx.getId());
        // check if the transaction is valid
        for (TxInput txInput : tx.getTxInputs()) {
            // locate the previous transaction output
            String utxoLocator = txInput.getPrevTxOutId() + ":" + txInput.getPrevTxOutIndex();
            if (!wallet.verifyUtxo(utxoLocator, txInput.getSignatureScript())) {
                System.out.println("Signature [" + txInput.getSignatureScript() + "] of prevTxOut [" + utxoLocator + "] is valid");
                return false;
            }
        }
        System.out.println("Transaction " + tx.getId() + " is valid");
        return true;
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

    public Map<String, TxOutput> getUtxos() {
        return utxos;
    }
}
