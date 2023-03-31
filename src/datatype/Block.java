package datatype;

import com.google.gson.Gson;
import protocol.Payload;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Block extends Payload {
    private Block prevBlock;
    private int index;  // height
    private String hash;
    private String previousHash;
    private long timestamp;
    private String data;
    private int difficulty; //The difficulty defines how many prefixing zeros the block hash must have, for the block to be valid.
    private int nonce; // default: 0

//    private Transaction[] txs;
    private List<Transaction> txs = new ArrayList<>();

    public Block(Block prevBlock, int index, String hash, String previousHash, long timestamp, String data, int difficulty, int nonce) {
        this.prevBlock = prevBlock;
        this.index = index;
        this.hash = hash;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
        this.nonce = nonce;
    }

    // get Transaction
    public List<Transaction> getTransactions() {
        return txs;
    }

    // set or add Transaction
    public void setTransactions(Transaction tx) {
        txs.add(tx);
    }

    public int getIndex() {
        return index;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getData() {
        return data;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getNonce() {
        return nonce;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public Block getPrevBlock() {
        return prevBlock;
    }

    public void setPrevBlock(Block prevBlock) {
        this.prevBlock = prevBlock;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        out.writeUTF(json);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        String json = in.readUTF();
        Gson gson = new Gson();
        Block block = gson.fromJson(json, Block.class);
        this.prevBlock = block.getPrevBlock();
        this.index = block.getIndex();
        this.hash = block.getHash();
        this.previousHash = block.getPreviousHash();
        this.timestamp = block.getTimestamp();
        this.data = block.getData();
        this.difficulty = block.getDifficulty();
        this.nonce = block.getNonce();
        this.txs = block.getTransactions();
    }
}
