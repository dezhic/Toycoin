package gui;
import datatype.Block;
import datatype.Blockchain;

import javax.swing.*;
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

    JButton mineBtn = new JButton("Mine");
    JButton syncBtn = new JButton("Sync");

    public GUI(String name) {
        frame = new JFrame(name);
        blockListModel = new DefaultListModel<>();
        blockList = new JList<>(blockListModel);
    }
    public void run() {
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);

        mineBtn.addActionListener(e -> {
            // mining is time consuming, so we need to run it in a new thread
            new Thread(() -> {
                blockchain.generateToAddress(5, "addr");
            }).start();
        });
        mineBtn.setSize(100, 50);
        mineBtn.setLocation(0, 0);

        syncBtn.addActionListener(e -> {
            blockchain.sync();
        });
        syncBtn.setSize(100, 50);
        syncBtn.setLocation(100, 0);

        frame.getContentPane().add(mineBtn);
        frame.getContentPane().add(syncBtn);

        blockList.setSize(300, 200);
        blockList.setLocation(0, 50);
        frame.getContentPane().add(blockList); // Adds Button to content pane of frame

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

}
