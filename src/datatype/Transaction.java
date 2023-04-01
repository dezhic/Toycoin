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

    public Transaction(List<TxInput> txInputs, List<TxOutput> txOutputs) {
        this.txInputs = txInputs;
        this.txOutputs = txOutputs;
        this.id = constgetTransactionId();
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

    private String constgetTransactionId() {
        StringBuilder txInputContent = new StringBuilder();
        for (TxInput txInput : txInputs) {
            txInputContent.append(txInput.getPrevTxOutId()).append(txInput.getPrevTxOutIndex());
        }

        StringBuilder txOutputContent = new StringBuilder();
        for (TxOutput txOutput : txOutputs) {
            txOutputContent.append(txOutput.getScriptPubKey()).append(txOutput.getValue());
        }

        String txContent = txInputContent.toString() + txOutputContent.toString();
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(txContent.getBytes());
        return DatatypeConverter.printHexBinary(hash);
    }
}