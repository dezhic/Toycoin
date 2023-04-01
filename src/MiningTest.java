import gui.GUI;
import network.LocalClient;
import network.LocalServer;
import datatype.Blockchain;
import datatype.MerkleTree;
import datatype.Transaction;
import util.ProofOfWork;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MiningTest {
    public static Blockchain blockchain; // store the Block

    public static void main(String[]args) throws NoSuchAlgorithmException, IOException {
        GUI GUI = new GUI(System.getenv("PORT"));
        GUI.start();

        blockchain = new Blockchain();
        GUI.setBlockchain(blockchain);
        blockchain.setGui(GUI);

        LocalClient localClient = new LocalClient(blockchain, GUI);
        blockchain.setLocalClient(localClient);
        GUI.setLocalClient(localClient);
        LocalServer localServer = new LocalServer(localClient);
        localServer.start();
        localClient.initialize();  // localClient needs to initialize AFTER localServer starts!

        // TODO: Given a List of Txs List<Tx>, select max. 30 of them and build a MercleTree (So, we need a merkle tree data structure to contain Txs).
        // ** Each block have its own Merkle tree
        // List to store the transaction obj
        List<Transaction> txMemPool = new ArrayList<>();
        // TODO: some data should be add into txMemPool...
        /** txMemPool.add(); */
        // Also, we need to have the hash val convert by each transaction
        List<String> txHashPool = new ArrayList<>();
        // function: calculate the hash with SHA-256
        for (Transaction tx : txMemPool) {
            String txHash = ProofOfWork.calculateHash(tx.toString());
            txHashPool.add(txHash); // put them into txPoolHash
        }
        // Then, put txHashPool into merkle tree function and generate
        MerkleTree merkleTrees = new MerkleTree(txHashPool);
//        merkleTrees.generateMerkleTreeRoot();
        // get root => data
//        System.out.println("root : " + merkleTrees.getRoot());


//        if (System.getenv("PORT").equals("3888")) {
//            blockchain.generateToAddress(5, "dummy_address");
//        } else if (System.getenv("PORT").equals("3889")) {
//            blockchain.sync();
//        } else {
//            blockchain.generateToAddress(3, "dummy_address");
//            blockchain.sync();
//        }
    }
}
