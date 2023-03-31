package datatype;

import java.io.Serializable;

public class TxInput implements Serializable {
    private String prevTxOutId;
    private int prevTxOutIndex;
    private String signatureScript;

    public TxInput(String prevTxOutId, int prevTxOutIndex, String signatureScript) {
        this.prevTxOutId = prevTxOutId;
        this.prevTxOutIndex = prevTxOutIndex;
        this.signatureScript = signatureScript;
    }

    public String getPrevTxOutId() {
        return prevTxOutId;
    }

    public int getPrevTxOutIndex() {
        return prevTxOutIndex;
    }

    public String getSignatureScript() {
        return signatureScript;
    }

    public void setPrevTxOutId(String prevTxOutId) {
        this.prevTxOutId = prevTxOutId;
    }

    public void setPrevTxOutIndex(int prevTxOutIndex) {
        this.prevTxOutIndex = prevTxOutIndex;
    }

    public void setSignatureScript(String signatureScript) {
        this.signatureScript = signatureScript;
    }
}