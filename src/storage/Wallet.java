package storage;

import datatype.Blockchain;
import datatype.Transaction;
import datatype.TxOutput;
import gui.GUI;
import util.Base58;
import util.ECDSAUtils;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A collection of public and private keys.
 * Responsible for generating new keys, and signing transactions.
 */
public class Wallet {

    private Map<String, String> keys;  // public key -> private key
    private Map<String, List<String>> utxoLocators;  // public key -> locator of unspent outputs (txid:index)

    private Blockchain blockchain;

    private GUI gui;

    public Wallet() {
        keys = new HashMap<>();
        utxoLocators = new HashMap<>();
    }

    /**
     * Generates ECDSA key pair and stores them in this wallet.
     */
    public void generateKey() {
        KeyPair keyPair = ECDSAUtils.getKeyPair();
        byte[] pubKeyBytes = keyPair.getPublic().getEncoded();
        byte[] privKeyBytes = keyPair.getPrivate().getEncoded();
        String publicKey = Base58.encode(pubKeyBytes);
        String privateKey = Base58.encode(privKeyBytes);
        keys.put(publicKey, privateKey);
        gui.updateKeyTable(getKeysWithBalance());
    }

    /**
     * return a list of public and private keys with the sum of their unspent output values
     * each item formatted as "publicKey:privateKey:balance"
     */
    private List<String> getKeysWithBalance() {
        // Get a list of public keys and private keys from Map keys
        return keys.entrySet().stream()
                .map(entry -> {
                    String publicKey = entry.getKey();
                    String privateKey = entry.getValue();
                    // Get the sum of unspent outputs for this public key
                    if (!utxoLocators.containsKey(publicKey)) {
                        return publicKey + ":" + privateKey + ":0";
                    } else {
                        long balance = utxoLocators.get(publicKey).stream()
                                .map(locator -> blockchain.getUtxos().get(locator))
                                .mapToLong(TxOutput::getValue)
                                .sum();
                        return publicKey + ":" + privateKey + ":" + balance;
                    }
                })
                .collect(Collectors.toList());
    }

    public void addUtxos(String publicKey, String utxoLocator) {
        // add or concatenate to existing list
        if (this.utxoLocators.containsKey(publicKey)) {
            this.utxoLocators.get(publicKey).add(utxoLocator);
        } else {
            List<String> utxoLocators = new LinkedList<>();
            utxoLocators.add(utxoLocator);
            this.utxoLocators.put(publicKey, utxoLocators);
        }
        gui.updateKeyTable(getKeysWithBalance());
    }

    public void removeUtxos(String publicKey, String utxoLocator) {
        // remove from existing list
        if (this.utxoLocators.containsKey(publicKey)) {
            this.utxoLocators.get(publicKey).remove(utxoLocator);
        }
    }

    public Transaction createTransaction(String from, String to, long value) {
        // TODO
        return null;
    }

    public void setBlockchain(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }
}
