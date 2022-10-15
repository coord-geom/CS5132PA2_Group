package controller;

import graphics.BTreeDisplay;
import model.BTree_;
import model.graphics.IntegerTreeItemFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Simple Java Swing Application
 */
public class Application {
    public Application() {
        JFrame rootFrame = new JFrame();
        BTree_<Integer> tree = new BTree_<>(3);
        tree.add(3);
        tree.add(6);
        tree.add(7);
        tree.add(2);
        tree.add(60);
        tree.add(24);
        tree.add(66);
        System.out.println(tree);
        BTreeDisplay display = new BTreeDisplay(new IntegerTreeItemFactory(), tree);
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