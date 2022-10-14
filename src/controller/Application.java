package controller;

import graphics.BTreeDisplay;
import model.BTree;

import javax.swing.*;
import java.awt.*;

/**
 * Simple Java Swing Application
 */
public class Application {
    public Application() {
        JFrame rootFrame = new JFrame();
        BTree<Integer> tree = new BTree<>(5);
        tree.add(1, 3);
        tree.add(2, 6);
        BTreeDisplay display = new BTreeDisplay(tree);
        rootFrame.add(display);

        rootFrame.setSize(800, 500);
        rootFrame.setLayout(new BoxLayout(rootFrame.getContentPane(), BoxLayout.Y_AXIS));
        rootFrame.setVisible(true);

        rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}