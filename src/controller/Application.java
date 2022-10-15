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
        for (int i = 0; i < 100; i++) {
            tree.add(i);
        }
        System.out.println(tree.getHeight());
        System.out.println("In Application:");
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