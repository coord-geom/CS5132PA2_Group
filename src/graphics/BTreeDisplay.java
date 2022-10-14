package graphics;

import model.BNode;
import model.BTree;

import java.awt.*;

public class BTreeDisplay extends Canvas {

    private BTree<?> tree;

    public BTreeDisplay(BTree<?> tree) {
        super();
        this.tree = tree;
    }

    public void setTree(BTree<?> tree) {
        this.tree = tree;
    }
    public BTree<?> getTree() {
        return tree;
    }

    @Override
    public void paint(Graphics graphics) {
        setBackground(Color.WHITE);
        drawNode(graphics, 0, 0, null);
    }

    public void drawNode(Graphics graphics, int posX, int posY, BNode<?> node) {
        graphics.fillRect(posX, posY, 10, 10);
    }
}
