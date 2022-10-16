package controller;

import graphics.BTreeDisplay;
import model.BTree;
import model.graphics.EntryTreeItemFactory;
import model.graphics.IntegerTreeItemFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileFilter;
import java.util.Optional;

/**
 * Simple Java Swing Application
 */
public class Application extends JFrame {

    private BTreeDisplay display;
    private TextField textField;
    private Label label;

    public Application() {
        super("B Tree Application");
        JPanel rootPanel = new JPanel(new BorderLayout());

        Font font = new Font("Courier New", Font.BOLD, 14);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setSize(0, 200);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        TextField textField = new TextField();
        Button buttonAdd = new Button("Add Item");
        Button buttonDel = new Button("Remove Item");
        Label label = new Label("Add/Remove nodes using the text field");
        Label emptySpaceLabel = new Label("   ");
        Label infoLabel = new Label(
                "Add/Remove nodes using the text field and buttons, click and drag to look around");

        textField.setMinimumSize(new Dimension(500, 50));
        textField.setFont(font);
        textField.setText("Input Here");
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                label.setForeground(Color.BLACK);
                label.setText("Add/Remove nodes using the text field");
            }

            @Override
            public void focusLost(FocusEvent e) {
                label.setForeground(Color.BLACK);
                label.setText("Add/Remove nodes using the text field");
            }
        });
        buttonAdd.setMaximumSize(new Dimension(100,30));
        buttonAdd.setFont(font);
        buttonAdd.addActionListener(e -> {
            if (textField.getText().equals("")) {
                label.setForeground(Color.RED);
                label.setText("Please Input Some Text!");
            }
            else if (!display.addItem(textField.getText())) {
                label.setForeground(Color.RED);
                label.setText("Invalid String Representation!");
            }
            else {
                display.update();
                label.setForeground(Color.BLUE);
                label.setText("Node " + textField.getText() + " Added");
            }
        });

        buttonDel.setMaximumSize(new Dimension(130,30));
        buttonDel.setFont(font);
        buttonDel.addActionListener(e -> {
            if (textField.getText().equals("")) {
                label.setForeground(Color.RED);
                label.setText("Please Input Some Text!");
            }
            else if (!display.deleteItem(textField.getText())) {
                label.setForeground(Color.RED);
                label.setText("Invalid String Representation!");
            }
            else {
                display.update();
                label.setForeground(Color.BLUE);
                label.setText("Node " + textField.getText() + " Removed");
            }
        });

        label.setMaximumSize(new Dimension(500,30));
        label.setFont(font);

        emptySpaceLabel.setMaximumSize(new Dimension(10,30));
        emptySpaceLabel.setFont(font);

        infoLabel.setMaximumSize(new Dimension(500,30));
        infoLabel.setFont(font);

        // Check for txt/csv files to read
        File file = new File(".");
        File[] readableFiles = file.listFiles(pathname -> {
            if (!pathname.getName().contains("."))
                return false;
            String[] d = pathname.getName().split("\\.");
            String extension = d[d.length - 1];
            return (extension.equals("csv") || extension.equals("txt"));
        });

        // Initialise ComboBox
        assert readableFiles != null;
        String[] optionsToChoose = new String[2 + readableFiles.length];
        optionsToChoose[0] = "Custom";
        optionsToChoose[1] = "Comparative Political Data Set";
        for (int i = 0; i < readableFiles.length; i++) {
            optionsToChoose[i + 2] = readableFiles[i].getName();
        }
        JComboBox<String> jComboBox = new JComboBox<>(optionsToChoose);
        jComboBox.setMaximumSize(new Dimension(300,30));
        jComboBox.addActionListener(e -> {
            // remove previous display
            rootPanel.remove(display);
            if (jComboBox.getSelectedIndex() == 0){
                // default empty canvas for user
                display = new BTreeDisplay(new IntegerTreeItemFactory());
                display.center();
            } else if (jComboBox.getSelectedIndex() == 1) {
                // initialise the CPDS dataset
                EntryTreeItemFactory entryTreeItemFactory = new EntryTreeItemFactory();
                display = new BTreeDisplay(entryTreeItemFactory,
                        entryTreeItemFactory.createFromFileTree(3, null));
                display.setVertical(true);
                display.setItemVertical(true);
                display.center();
            } else {
                IntegerTreeItemFactory factory = new IntegerTreeItemFactory();
                display = new BTreeDisplay(factory,
                        factory.createTreeFromFile(optionsToChoose[jComboBox.getSelectedIndex()]));
                display.center();
            }
            // add new display and revalidate the panel to see changes to gui
            rootPanel.add(display, BorderLayout.CENTER);
            rootPanel.revalidate();
            rootPanel.repaint();
        });

        panel.add(textField);
        panel.add(buttonAdd);
        panel.add(buttonDel);
        panel.add(emptySpaceLabel);
        panel.add(label);
        panel.add(jComboBox);

        display = new BTreeDisplay(new IntegerTreeItemFactory());
        display.center();
        rootPanel.add(display, BorderLayout.CENTER);
        rootPanel.add(panel, BorderLayout.PAGE_START);
        rootPanel.add(infoLabel, BorderLayout.PAGE_END);

        add(rootPanel, BorderLayout.CENTER);
        setSize(1000, 600);
        setVisible(true);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initialiseData() {

    }
}