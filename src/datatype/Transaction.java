package datatype;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;


public class Transaction implements Serializable {
    private String id;
    private List<TxInput> txInputs;
    private List<TxOutput> txOutputs;
    private long timestamp;

    public Transaction(List<TxInput> txInputs, List<TxOutput> txOutputs) {
        this.txInputs = txInputs;
        this.txOutputs = txOutputs;
        this.timestamp = System.currentTimeMillis();
        this.id = getTransactionId();
    }

    public String getId() {
        return id;
    }

    public List<TxInput> getTxIns() {
        return txInputs;
    }

    public List<TxOutput> getTxOuts() {
        return txOutputs;
    }

    /**
     * Reference: <a href="https://reference.cash/protocol/blockchain/hash">https://reference.cash/protocol/blockchain/hash</a>
     * Transaction Hashing (Double SHA-256)
     * Transactions are also hashed using a double application of SHA-256.
     * This is referred to as the transaction hash and is used to uniquely identify the transaction.
     * (NOTE: Historical transaction hashes are not universally unique, there are two sets of two identical coinbase transactions (and thus identical hashes).
     * Since BIP-34, the block height is now required to be in the coinbase transaction, which drastically reduces the possibility of duplicate transaction hashes in the future.)
     * @return transaction id (Transaction Hash)
     */
    private String getTransactionId() {
        StringBuilder txInputContent = new StringBuilder();
        for (TxInput txInput : txInputs) {
            txInputContent.append(txInput.getPrevTxOutId()).append(txInput.getPrevTxOutIndex());
        }

        StringBuilder txOutputContent = new StringBuilder();
        for (TxOutput txOutput : txOutputs) {
            txOutputContent.append(txOutput.getScriptPubKey()).append(txOutput.getValue());
        }

        String txContent = txInputContent.toString() + txOutputContent.toString() + timestamp;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(txContent.getBytes());
        return DatatypeConverter.printHexBinary(hash);
    }

    public List<TxInput> getTxInputs() {
        return txInputs;
    }

    public List<TxOutput> getTxOutputs() {
        return txOutputs;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}