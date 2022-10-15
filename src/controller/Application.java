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
        tree.add(3, 7);
        tree.add(4, 2);
        tree.add(5, 60);
        tree.add(6, 24);
        tree.add(7, 66);
        BTreeDisplay display = new BTreeDisplay(tree);
        rootFrame.add(display);

        TextField textField = new TextField();
        Button buttonAdd = new Button("Add Item");
        rootFrame.add(textField);
        rootFrame.add(buttonAdd);

        rootFrame.setSize(800, 500);
        BoxLayout layout = new BoxLayout(rootFrame.getContentPane(), BoxLayout.PAGE_AXIS);
        rootFrame.setLayout(layout);
        rootFrame.setVisible(true);

        rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}