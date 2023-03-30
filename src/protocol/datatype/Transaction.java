import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.ArrayList;


public class Transaction implements Serializable {
    private String id;
    private List<TxIn> txIns;
    private List<TxOut> txOuts;

    public Transaction(List<TxIn> txIns, List<TxOut> txOuts) throws NoSuchAlgorithmException {
        this.txIns = txIns;
        this.txOuts = txOuts;
        this.id = calculateTransactionId();
    }

    public String getId() {
        return id;
    }

    public List<TxIn> getTxIns() {
        return txIns;
    }

    public List<TxOut> getTxOuts() {
        return txOuts;
    }

    private String constgetTransactionId() throws NoSuchAlgorithmException {
        StringBuilder txInputContent = new StringBuilder();
        for (TxInput txInput : txInputs) {
            txInputContent.append(txInput.getTxOutId()).append(txInput.getTxOutIndex());
        }

        StringBuilder txOutputContent = new StringBuilder();
        for (TxOutput txOutput : txOutputs) {
            txOutputContent.append(txOutput.getAddress()).append(txOutput.getAmount());
        }

        String txContent = txInputContent.toString() + txOutputContent.toString();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(txContent.getBytes());
        return DatatypeConverter.printHexBinary(hash);
    }
}