package protocol.datatype;

import java.io.Serializable;

public class TxIn implements Serializable {
    private String prevTxOutId;
    private int prevTxOutIndex;
    private String signatureScript;

}
