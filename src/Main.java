import gui.GUI;
import network.LocalClient;
import network.LocalServer;
import storage.Blockchain;
import datatype.MerkleTree;
import datatype.Transaction;
import storage.Wallet;
import util.ProofOfWork;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static Blockchain blockchain; // store the Block

    public static void main(String[]args) throws NoSuchAlgorithmException, IOException {
        GUI gui = GUI.getInstance();
        gui.start();

        blockchain = new Blockchain();
        gui.setBlockchain(blockchain);
        blockchain.setGui(gui);

        Wallet wallet = new Wallet();
        gui.setWallet(wallet);
        blockchain.setWallet(wallet);
        wallet.setBlockchain(blockchain);
        wallet.setGui(gui);


        LocalClient localClient = new LocalClient(blockchain, gui);
        blockchain.setLocalClient(localClient);
        gui.setLocalClient(localClient);

        LocalServer localServer = new LocalServer(localClient);
        localServer.start();
        localClient.initialize();  // localClient needs to initialize AFTER localServer starts!

    }
}
