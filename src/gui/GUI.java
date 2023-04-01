package gui;
import datatype.Block;
import datatype.Blockchain;
import network.LocalClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GUI extends Thread {
    JFrame frame;
    JList<String> blockList;
    DefaultListModel<String> blockListModel;

    Blockchain blockchain;
    public void setBlockchain(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    LocalClient localClient;
    public void setLocalClient(LocalClient localClient) {
        this.localClient = localClient;
    }


    JButton mineBtn = new JButton("Mine");
    JButton syncBtn = new JButton("Sync");

    JTextArea blockInfoTextArea = new JTextArea();

    JTable peerTable;
    DefaultTableModel peerTableModel;

    JButton getAddrBtn = new JButton("Find Peers (getaddr)");

    JTable utxoTable;
    DefaultTableModel utxoTableModel;

    public GUI(String name) {
        frame = new JFrame(name);
        blockListModel = new DefaultListModel<>();
        blockList = new JList<>(blockListModel);
        peerTableModel = new DefaultTableModel(new String[] {"Peer IP", "Server Port", "Client Port"}, 0);
        peerTable = new JTable(peerTableModel);
        utxoTableModel = new DefaultTableModel(new String[] {"UTXO Tx.", "TxOut Idx", "PubKey", "Amt"}, 0);
        utxoTable = new JTable(utxoTableModel);
    }

    public void run() {
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,850);

        mineBtn.addActionListener(e -> {
            // mining is time consuming, so we need to run it in a new thread
            new Thread(() -> {
                blockchain.generateToAddress(5, "addr");
            }).start();
        });
        mineBtn.setSize(100, 30);
        mineBtn.setLocation(480, 560);

        syncBtn.addActionListener(e -> {
            blockchain.sync();
        });
        syncBtn.setSize(95, 30);
        syncBtn.setLocation(175, 560);

        frame.getContentPane().add(mineBtn);
        frame.getContentPane().add(syncBtn);

        blockList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = blockList.getSelectedIndex();
                if (index >= 0) {
                    blockInfoTextArea.setText(blockchain.getBlock(index).toString());
                }
            }
        });
        // scrollable blockList
        JScrollPane blockListPane = new JScrollPane(blockList);
        blockListPane.setSize(250, 200);
        blockListPane.setLocation(20, 20);
        frame.getContentPane().add(blockListPane);

//        blockInfoTextArea.setSize(300, 410);
//        blockInfoTextArea.setLocation(280, 20);
        blockInfoTextArea.setEditable(false);
        JScrollPane blockInfoPane = new JScrollPane(blockInfoTextArea);
        blockInfoPane.setSize(300, 410);
        blockInfoPane.setLocation(280, 20);
        frame.getContentPane().add(blockInfoPane);

        getAddrBtn.addActionListener(e -> {
            try {
                localClient.broadcastGetAddr();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        getAddrBtn.setSize(145, 30);
        getAddrBtn.setLocation(20, 560);
        frame.getContentPane().add(getAddrBtn);

        peerTable.setDefaultEditor(Object.class, null);  // disable editing
        peerTable.setCellSelectionEnabled(true);
        JScrollPane peerTablePane = new JScrollPane(peerTable);
        peerTablePane.setSize(250, 100);
        peerTablePane.setLocation(20, 450);
        frame.getContentPane().add(peerTablePane);

        // UTXO table
        utxoTable.setDefaultEditor(Object.class, null);  // disable editing
        utxoTable.setCellSelectionEnabled(true);
        JScrollPane utxoTablePane = new JScrollPane(utxoTable);
        utxoTablePane.setSize(250, 200);
        utxoTablePane.setLocation(20, 230);
        frame.getContentPane().add(utxoTablePane);

        frame.setVisible(true);
    }

    public void updateBlockList(List<Block> blockList) {
        blockListModel.clear();
        blockListModel.addAll(blockList.stream().map(Block::getHash).collect(Collectors.toList()));
//        frame.setVisible(true);
        if (this.blockListModel.size() > 0) {
            this.blockList.ensureIndexIsVisible(this.blockListModel.size() - 1);
        }
    }

    /**
     * Update the peer table
     * @param peerList  a list of peers, each peer is a string in the format of "ip:serverPort:clientPort"
     */
    public void updatePeerTable(List<String> peerList) {
        peerTableModel.setRowCount(0);
        for (String peer : peerList) {
            peerTableModel.addRow(peer.split(":"));
        }
    }

    public void updateUTXOTable(List<String> utxoList) {
        utxoTableModel.setRowCount(0);
        for (String utxo : utxoList) {
            utxoTableModel.addRow(utxo.split(":"));
        }
    }

}
