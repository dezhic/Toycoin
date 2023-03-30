import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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

    private String calculateTransactionId() throws NoSuchAlgorithmException {
        StringBuilder txInContent = new StringBuilder();
        for (TxIn txIn : txIns) {
            txInContent.append(txIn.getPrevTxOutId()).append(txIn.getPrevTxOutIndex());
        }

        StringBuilder txOutContent = new StringBuilder();
        for (TxOut txOut : txOuts) {
            txOutContent.append(txOut.getValue()).append(txOut.getScriptPubKey());
        }

        return ProofOfWork.calculateHash(txInContent.toString() + txOutContent.toString());
    }
}