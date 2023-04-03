package storage;

import datatype.Transaction;
import datatype.TxInput;
import datatype.TxOutput;
import gui.GUI;
import util.Base58;
import util.ECDSAUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
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

    public Transaction createTransaction(String from, String to, long value) throws Exception {
        String publicKey = from;
        String privateKey = keys.get(publicKey);
        if (privateKey == null) {
            throw new Exception("Private key not found for address " + publicKey);
        }
        // get unspent outputs
        List<String> utxoLocators = this.utxoLocators.get(publicKey);
        if (utxoLocators == null) {
            throw new Exception("No unspent outputs found for address " + publicKey.substring(0, 2) + "..." + publicKey.substring(publicKey.length() - 4));
        }
        // generate tx inputs
        List<TxInput> inputs = new LinkedList<>();
        long total = 0;
        // accumulate inputs until total >= value
        for (String utxoLocator : utxoLocators) {
            TxOutput utxo = blockchain.getUtxos().get(utxoLocator);
            if (utxo == null) {
                throw new Exception("Unspent output not found for locator " + utxoLocator);
            }
            String[] locatorParts = utxoLocator.split(":");
            inputs.add(new TxInput(locatorParts[0], Integer.parseInt(locatorParts[1]), signUtxo(utxoLocator, privateKey)));
            total += utxo.getValue();
            if (total >= value) {
                break;
            }
        }
        // If total < value, we don't have enough funds
        if (total < value) {
            throw new Exception("No enough funds");
        }

        // outputs
        List<TxOutput> outputs = new LinkedList<>();
        outputs.add(new TxOutput(value, to));
        if (total > value) {
            // return change
            outputs.add(new TxOutput(total - value, publicKey));
        }
        // create transaction
        return new Transaction(inputs, outputs);
    }

    /**
     * Signs the given utxo with the given private key.
     * The utxo is identified by its locator, which is "txid:index".
     * @param utxoLocator
     * @param privateKey
     * @return
     */
    private String signUtxo(String utxoLocator, String privateKey) {
        // Construct PrivateKey object from privateKey string
        PrivateKey privKey = ECDSAUtils.privateKeyFromBytes(Base58.decode(privateKey));
        // Sign the utxoLocator with the private key
        return ECDSAUtils.signECDSA(privKey, utxoLocator);
    }

    /**
     * Verifies the given signature for the given utxo.
     * This is done by:
     * 1. getting the UTXO from the blockchain by `utxoLocator`
     * 2. getting the public key from the UTXO
     * 3. verifying the signature on `utxoLocator` with the public key
     *
     * @param utxoLocator locator of the UTXO
     * @param signature signature in the spending TxInput
     * @return true if the signature is valid, false otherwise
     */
    public boolean verifyUtxo(String utxoLocator, String signature) {
        // Get the UTXO from the blockchain
        TxOutput utxo = blockchain.getUtxos().get(utxoLocator);
        if (utxo == null) {
            System.out.println("UTXO not found for locator " + utxoLocator);
            return false;
        }
        // Get the public key from the UTXO
        String publicKey = utxo.getScriptPubKey();
        PublicKey pubKey = ECDSAUtils.publicKeyFromBytes(Base58.decode(publicKey));
        // Verify the signature on `utxoLocator` with the public key
        return ECDSAUtils.verifyECDSA(pubKey, signature, utxoLocator);
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

    public void clearUtxos() {
        this.utxoLocators.clear();
    }
}
