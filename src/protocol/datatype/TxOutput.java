package protocol.datatype;

import java.io.Serializable;

public class TxOutput implements Serializable {
    private long value;
    private String scriptPubKey;

    public TxOutput(long value, String scriptPubKey) {
        this.value = value;
        this.scriptPubKey = scriptPubKey;
    }

    public long getValue() {
        return value;
    }

    public String getScriptPubKey() {
        return scriptPubKey;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void setScriptPubKey(String scriptPubKey) {
        this.scriptPubKey = scriptPubKey;
    }
}