package controller;

import graphics.BTreeDisplay;

import javax.swing.*;

public class Application {
    public Application() {
        JFrame rootFrame = new JFrame();
        rootFrame.add(new BTreeDisplay(null));

        rootFrame.setSize(800, 500);
        rootFrame.setVisible(true);

        rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}