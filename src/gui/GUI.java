package gui;
import datatype.Block;
import datatype.Blockchain;
import network.LocalClient;
import storage.Wallet;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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

    Wallet wallet;
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
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

    JTable keyTable;
    DefaultTableModel keyTableModel;

    TextField genToAddrField = new TextField("");

    JSpinner genNBlockSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 9999, 1));

    JButton genKeyBtn = new JButton("GenKey");

    JButton copyBtn = new JButton("Copy");

    TextField fromAddrField = new TextField("");
    TextField toAddrField = new TextField("");
    JSpinner amtSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    JButton sendBtn = new JButton("Send");

    private class LongTextCellRenderer extends DefaultTableCellRenderer {
        @Override
        public void setValue(Object value) {
            String text = value.toString();
            if (text.length() > 7) {
                setText(text.substring(0, 2) + "..." + text.substring(text.length() - 4));
            } else {
                setText(text);
            }
        }
    }

    LongTextCellRenderer longTextCellRenderer = new LongTextCellRenderer();

    public GUI(String name) {
        frame = new JFrame(name);
        blockListModel = new DefaultListModel<>();
        blockList = new JList<>(blockListModel);
        peerTableModel = new DefaultTableModel(new String[] {"Peer IP", "Server Port", "Client Port"}, 0);
        peerTable = new JTable(peerTableModel);
        utxoTableModel = new DefaultTableModel(new String[] {"UTXO Tx.", "TxOut Idx", "PubKey", "Amt"}, 0);
        utxoTable = new JTable(utxoTableModel);
        utxoTable.setDefaultRenderer(Object.class, longTextCellRenderer);
        keyTableModel = new DefaultTableModel(new String[] {"PubKey", "PrivKey", "Balance"}, 0);
        keyTable = new JTable(keyTableModel);
        keyTable.setDefaultRenderer(Object.class, longTextCellRenderer);
    }

    public void run() {
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,850);

        mineBtn.addActionListener(e -> {
            // mining is time consuming, so we need to run it in a new thread
            new Thread(() -> {
                blockchain.generateToAddress((int) genNBlockSpinner.getValue(), genToAddrField.getText().trim());
            }).start();
        });
        mineBtn.setSize(100, 30);
        mineBtn.setLocation(480, 560);

        genToAddrField.setSize(105, 30);
        genToAddrField.setLocation(375, 560);
        frame.getContentPane().add(genToAddrField);

        genNBlockSpinner.setSize(70, 30);
        genNBlockSpinner.setLocation(300, 560);
        frame.getContentPane().add(genNBlockSpinner);

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

        // Key table
        keyTable.setDefaultEditor(Object.class, null);  // disable editing
        keyTable.setCellSelectionEnabled(true);
        JScrollPane keyTablePane = new JScrollPane(keyTable);
        keyTablePane.setSize(250, 140);
        keyTablePane.setLocation(20, 610);
        frame.getContentPane().add(keyTablePane);

        // GenKey button
        genKeyBtn.addActionListener(e -> {
            wallet.generateKey();
        });
        genKeyBtn.setSize(100, 30);
        genKeyBtn.setLocation(20, 750);
        frame.getContentPane().add(genKeyBtn);

        // Copy button
        // copy selected value in the key table cell to the clipboard
        copyBtn.addActionListener(e -> {
            int row = keyTable.getSelectedRow();
            int col = keyTable.getSelectedColumn();
            if (row >= 0 && col >= 0) {
                String value = keyTable.getValueAt(row, col).toString();
                StringSelection selection = new StringSelection(value);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        });
        copyBtn.setSize(100, 30);
        copyBtn.setLocation(130, 750);
        frame.getContentPane().add(copyBtn);

        // Transfer Section
        // FROM
        JLabel fromLabel = new JLabel("FROM:");
        fromLabel.setSize(50, 30);
        fromLabel.setLocation(290, 620);
        fromLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fromLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        frame.getContentPane().add(fromLabel);
        // from address field
        fromAddrField.setSize(220, 30);
        fromAddrField.setLocation(350, 620);
        frame.getContentPane().add(fromAddrField);
        // TO
        JLabel toLabel = new JLabel("TO:");
        toLabel.setSize(50, 30);
        toLabel.setLocation(290, 660);
        toLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        toLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        frame.getContentPane().add(toLabel);
        // to address field
        toAddrField.setSize(220, 30);
        toAddrField.setLocation(350, 660);
        frame.getContentPane().add(toAddrField);
        // AMOUNT
        JLabel amountLabel = new JLabel("AMOUNT:");
        amountLabel.setSize(50, 30);
        amountLabel.setLocation(290, 700);
        amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        frame.getContentPane().add(amountLabel);
        // amount field
        amtSpinner.setSize(220, 30);
        amtSpinner.setLocation(350, 700);
        frame.getContentPane().add(amtSpinner);
        // SEND
        sendBtn.addActionListener(e -> {
            String fromAddr = fromAddrField.getText();
            String toAddr = toAddrField.getText();
            int amount = (int) amtSpinner.getValue();
            if (fromAddr.isEmpty() || toAddr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter from/to address");
                return;
            }
            if (amount <= 0) {
                JOptionPane.showMessageDialog(frame, "Please enter a positive amount");
                return;
            }
            try {
                localClient.broadcastTx(fromAddr, toAddr, amount);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });
        sendBtn.setSize(100, 30);
        sendBtn.setLocation(400, 735);
        frame.getContentPane().add(sendBtn);

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

    public void updateKeyTable(List<String> keysWithBalance) {
        keyTableModel.setRowCount(0);
        for (String kwb : keysWithBalance) {
            keyTableModel.addRow(kwb.split(":"));
        }
    }

}
