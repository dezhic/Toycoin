package datatype;

import java.io.Serializable;

public class Header implements Serializable {
    private int index;  // height
    private String hash;
    private String previousHash;
    private long timestamp;
    private String data;
    private int difficulty; //The difficulty defines how many prefixing zeros the block hash must have, for the block to be valid.

    private int nonce; // default: 0

    public Header(int index, String hash, String previousHash, long timestamp, String data, int difficulty, int nonce) {
        this.index = index;
        this.hash = hash;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
        this.nonce = nonce;
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
}
